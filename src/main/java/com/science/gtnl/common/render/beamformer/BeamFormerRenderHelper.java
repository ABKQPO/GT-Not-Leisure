package com.science.gtnl.common.render.beamformer;

import java.util.EnumSet;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBeamFormerRenderer;
import com.science.gtnl.utils.RenderUtils;

import appeng.api.util.AEColor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BeamFormerRenderHelper {

    public static final double MIN_SCALE = 0.15d;
    private static final StaticBloomMetadata[] DIRECTIONS = createDirections();
    private static final float[][] COLORS = createColors();

    public static final EnumSet<ForgeDirection> FACINGS_ALONG_Z = EnumSet
        .of(ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST);

    public static final EnumSet<ForgeDirection> FACINGS_ALONG_X = EnumSet
        .of(ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.NORTH);

    public static final EnumSet<ForgeDirection> FACINGS_ALONG_Y = EnumSet
        .of(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST);

    public static final IBeamFormerRenderer RENDERER = NativeBeamFormerRenderer.create();

    public static boolean shouldRenderDynamic(IBeamFormer partBeamFormer) {
        return RENDERER.shouldRenderDynamic(partBeamFormer);
    }

    public static void renderDynamic(IBeamFormer partBeamFormer, double x, double y, double z, float partialTicks) {
        RENDERER.renderDynamic(partBeamFormer, x, y, z, partialTicks);
    }

    public static float[] getColor(IBeamFormer partBeamFormer) {
        return COLORS[partBeamFormer.getBeamColor()
            .ordinal()];
    }

    public static void drawCube(Tessellator tessellator, double x, double y, double z, double length,
        StaticBloomMetadata result, float[] rgb) {

        EnumSet<ForgeDirection> facings;
        double scaleX;
        double scaleY;
        double scaleZ;

        if (result.dx != 0) {
            facings = FACINGS_ALONG_X;

            scaleX = Math.max(MIN_SCALE, Math.abs(result.dx * length + result.dx * 0.25d));
            scaleY = MIN_SCALE;
            scaleZ = MIN_SCALE;

        } else if (result.dy != 0) {
            facings = FACINGS_ALONG_Y;

            scaleX = MIN_SCALE;
            scaleY = Math.max(MIN_SCALE, Math.abs(result.dy * length + result.dy * 0.25d));
            scaleZ = MIN_SCALE;

        } else if (result.dz != 0) {
            facings = FACINGS_ALONG_Z;

            scaleX = MIN_SCALE;
            scaleY = MIN_SCALE;
            scaleZ = Math.max(MIN_SCALE, Math.abs(result.dz * length + result.dz * 0.25d));

        } else {
            return;
        }

        RenderUtils.drawCube(tessellator, x, y, z, scaleX, scaleY, scaleZ, rgb, facings);
    }

    public static StaticBloomMetadata getBloomMetadata(IBeamFormer partBeamFormer) {
        return DIRECTIONS[partBeamFormer.getDirection()
            .ordinal()];
    }

    public static AxisAlignedBB getRenderBoundingBox(IBeamFormer former) {
        var pos = former.getPos();
        ForgeDirection direction = former.getDirection();
        int length = former.shouldRenderBeam() ? former.getBeamLength() : 0;
        return AxisAlignedBB.getBoundingBox(
            pos.x + Math.min(0, direction.offsetX * length),
            pos.y + Math.min(0, direction.offsetY * length),
            pos.z + Math.min(0, direction.offsetZ * length),
            pos.x + 1 + Math.max(0, direction.offsetX * length),
            pos.y + 1 + Math.max(0, direction.offsetY * length),
            pos.z + 1 + Math.max(0, direction.offsetZ * length));
    }

    private static StaticBloomMetadata[] createDirections() {
        StaticBloomMetadata[] result = new StaticBloomMetadata[7];
        for (ForgeDirection facing : ForgeDirection.values()) {
            int dx = facing.offsetX;
            int dy = facing.offsetY;
            int dz = facing.offsetZ;
            float pitch = (float) Math.toDegrees(Math.atan2(Math.sqrt(dx * dx + dz * dz), dy));
            float yaw = (float) (90 - Math.toDegrees(Math.atan2(dz, dx)));
            result[facing.ordinal()] = new StaticBloomMetadata(dx, dy, dz, pitch, yaw);
        }
        return result;
    }

    private static float[][] createColors() {
        AEColor[] colors = AEColor.values();
        float[][] result = new float[colors.length][3];
        for (AEColor color : colors) {
            int rgb = color.mediumVariant;
            result[color.ordinal()][0] = (rgb >> 16 & 255) / 255F;
            result[color.ordinal()][1] = (rgb >> 8 & 255) / 255F;
            result[color.ordinal()][2] = (rgb & 255) / 255F;
        }
        return result;
    }

    public static void init(IBeamFormer partBeamFormer) {
        RENDERER.init(partBeamFormer);
    }

    @Desugar
    public record StaticBloomMetadata(int dx, int dy, int dz, float pitch, float yaw) {}
}
