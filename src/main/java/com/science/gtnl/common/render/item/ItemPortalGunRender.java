package com.science.gtnl.common.render.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.gtnllib.client.fx.world.FxArcRenderer;
import com.gtnllib.client.fx.world.FxArcRenderer.Color;
import com.gtnllib.client.fx.world.FxArcRenderer.Style;
import com.gtnllib.client.fx.world.FxRenderHelper;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.item.items.PortalGun;
import com.science.gtnl.common.render.item.PortalGunCoreArcs.Arc;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class ItemPortalGunRender extends ItemGunRender {

    public static final float FLUID_BOTTOM = 0.203125F;
    public static final int SCREEN_FRAMES = 8, SCREEN_FRAME_TICKS = 2;
    private static final long MILLIS_PER_TICK = 50;
    public static final long CORE_PULSE_MILLIS = 1500;
    private static final Color CORE_GREEN = new Color(0.47F, 1F, 0.31F), CORE_LIGHT = new Color(0.72F, 1F, 0.6F);
    private static final Style ARC_STYLE = new Style(CORE_GREEN, CORE_GREEN, CORE_LIGHT, CORE_GREEN, CORE_LIGHT, 0.045);

    public record Core(float x, float y, float z, float half, float roomX, float roomY, float roomZ,
        float glowRadius) {}

    public record Edition(String folder, float centerX, float centerY, float centerZ, float firstPersonLift,
        float firstPersonPush, Core core, Vec3d muzzle) {

        public static final Edition TECH = new Edition(
            "portal_gun",
            0F,
            -0.047F,
            0.361F,
            0,
            0,
            null,
            new Vec3d(0, 0.0625, -0.131));
        public static final Edition C137 = new Edition(
            "portal_gun_c137",
            0F,
            -0.077F,
            0.326F,
            0.15F,
            0.45F,
            new Core(0F, 0.099F, 0.188F, 0.0406F, 0.1016F, 0.1422F, 0.1016F, 0.15F),
            new Vec3d(0, -0.145, -0.17));
    }

    private final Edition edition;
    private final IModelCustom model;
    private final ResourceLocation base, glow, fx, screen;

    public ItemPortalGunRender(Edition edition) {
        String root = RESOURCE_ROOT_ID + ":models/" + edition.folder() + "/";
        this.edition = edition;
        this.model = AdvancedModelLoader.loadModel(new ResourceLocation(root + edition.folder() + ".obj"));
        this.base = new ResourceLocation(root + "base.png");
        this.glow = new ResourceLocation(root + "glow.png");
        this.fx = new ResourceLocation(root + "fx.png");
        this.screen = new ResourceLocation(root + "screen.png");
    }

    @Override
    protected void draw(ItemRenderType type, ItemStack item, Object[] data) {
        anchorMuzzle(type, data, edition.muzzle());
        TextureManager textures = Minecraft.getMinecraft().renderEngine;
        textures.bindTexture(base);
        model.renderPart("solid");
        float level = fluidLevel(item);
        fullBright(() -> renderLit(textures, level));
    }

    public static float fluidLevel(ItemStack stack) {
        if (!(stack.getItem() instanceof PortalGun gun)) return 0;
        FluidStack shimmer = gun.getFluid(stack);
        return shimmer == null ? 0 : MathUtils.clampUnit(shimmer.amount / (float) Math.max(1, gun.getCapacity(stack)));
    }

    public static int screenFrame(long millis) {
        return (int) (millis / MILLIS_PER_TICK / SCREEN_FRAME_TICKS % SCREEN_FRAMES);
    }

    public static float corePulse(long millis) {
        return 0.8F + 0.2F * MathHelper.sin(2 * PI * (millis % CORE_PULSE_MILLIS) / CORE_PULSE_MILLIS);
    }

    private void renderLit(TextureManager textures, float level) {
        long millis = Minecraft.getSystemTime();
        textures.bindTexture(glow);
        model.renderPart("glow");
        if (level > 0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, FLUID_BOTTOM, 0);
            GL11.glScalef(1, level, 1);
            GL11.glTranslatef(0, -FLUID_BOTTOM, 0);
            model.renderPart("fluid");
            GL11.glPopMatrix();
        }

        textures.bindTexture(screen);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, screenFrame(millis) / (float) SCREEN_FRAMES, 0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        model.renderPart("screen");
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        textures.bindTexture(base);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        model.renderPart("glass");
        GL11.glDepthMask(true);
        if (edition.core() != null) {
            // Why: after the glass tint so the dark glass cannot dim it, after its glow so no face is lit alone.
            GL11.glDisable(GL11.GL_BLEND);
            glowCore(edition.core(), millis);
            textures.bindTexture(glow);
            spinCore(edition.core(), millis);
            arcCore(edition.core(), millis);
            GL11.glEnable(GL11.GL_BLEND);
        }
        // Why: the glass writes depth so the far side of its outline does not show through it.
        GL11.glColorMask(false, false, false, false);
        model.renderPart("glass");
        GL11.glColorMask(true, true, true, true);
        textures.bindTexture(fx);
        GL11.glDepthMask(false);
        // Why: added light changes colour with whatever is behind it, so the outline and its ring are painted solid.
        model.renderPart("ring");
        model.renderPart("halo");
        GL11.glDepthMask(true);
    }

    private void spinCore(Core core, long millis) {
        double[] angles = PortalGunCoreArcs.angles(millis), shake = PortalGunCoreArcs.shake(millis);
        GL11.glPushMatrix();
        GL11.glTranslated(core.x() + shake[0], core.y() + shake[1], core.z() + shake[2]);
        GL11.glRotated(angles[0], 1, 0, 0);
        GL11.glRotated(angles[1], 0, 1, 0);
        GL11.glRotated(angles[2], 0, 0, 1);
        GL11.glTranslatef(-core.x(), -core.y(), -core.z());
        model.renderPart("core");
        GL11.glPopMatrix();
    }

    private static void glowCore(Core core, long millis) {
        float pulse = corePulse(millis);
        double[] shake = PortalGunCoreArcs.shake(millis);
        Vec3d center = new Vec3d(core.x() + shake[0], core.y() + shake[1], core.z() + shake[2]);
        FxRenderHelper.renderLocalGlow(Sprite.RADIAL, () -> {
            Tessellator.instance.setColorRGBA_F(0.47F, 1F, 0.31F, 0.9F * pulse);
            FxRenderHelper.localGlow(center, core.glowRadius());
            Tessellator.instance.setColorRGBA_F(0.8F, 1F, 0.65F, pulse);
            FxRenderHelper.localGlow(center, core.glowRadius() * 0.45);
        });
    }

    private static void arcCore(Core core, long millis) {
        List<Arc> arcs = PortalGunCoreArcs.arcs(
            millis,
            core.half(),
            new double[] { core.roomX(), core.roomY(), core.roomZ() },
            PortalGunCoreArcs.angles(millis),
            PortalGunCoreArcs.shake(millis));
        FxRenderHelper.renderLocal(() -> {
            for (Arc arc : arcs) {
                List<Vec3d> points = new ObjectArrayList<>(arc.points().length);
                for (double[] point : arc.points())
                    points.add(new Vec3d(core.x() + point[0], core.y() + point[1], core.z() + point[2]));
                FxArcRenderer.drawPath(points, ARC_STYLE, arc.brightness());
            }
        });
    }

    @Override
    protected void firstPerson() {
        swingIntoView();
        GL11.glTranslatef(0.46F - 0.56F, -0.40F + 0.52F, -1.20F + 0.72F);
        tiltInHand();
        GL11.glScalef(HAND_SCALE, HAND_SCALE, HAND_SCALE);
        MainConfig.Client settings = MainConfig.client;
        GL11.glTranslated(0, edition.firstPersonLift(), edition.firstPersonPush() - settings.portalGunHeldForward);
        // Why: tipping the top toward the eye and turning the gun shows the vial's whole outline, bottom edge included.
        GL11.glTranslatef(edition.centerX(), edition.centerY(), edition.centerZ());
        GL11.glRotated(settings.portalGunHeldPitch, 1, 0, 0);
        GL11.glRotated(settings.portalGunHeldYaw, 0, 1, 0);
        GL11.glTranslatef(-edition.centerX(), -edition.centerY(), -edition.centerZ());
    }

    @Override
    protected void ground() {
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glTranslatef(0.0F, 0.15F, 0.0F);
        GL11.glScalef(0.7F, 0.7F, 0.7F);
        GL11.glTranslatef(-edition.centerX(), -edition.centerY(), -edition.centerZ());
    }

    @Override
    protected void inventory() {
        GL11.glTranslatef(8.0F, 8.0F, 0.0F);
        GL11.glScalef(16.0F, -16.0F, 16.0F);
        GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(0.85F, 0.85F, 0.85F);
        GL11.glTranslatef(-edition.centerX(), -edition.centerY(), -edition.centerZ());
    }
}
