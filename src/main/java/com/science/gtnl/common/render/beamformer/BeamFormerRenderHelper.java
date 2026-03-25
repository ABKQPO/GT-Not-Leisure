package com.science.gtnl.common.render.beamformer;

import java.util.EnumSet;
import java.util.WeakHashMap;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBeamFormerRenderer;
import com.science.gtnl.utils.RenderUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BeamFormerRenderHelper {

    public static final double MIN_SCALE = 0.15d;
    public static final WeakHashMap<IBeamFormer, StaticBloomMetadata> META_CACHE = new WeakHashMap<>();

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
        int color = partBeamFormer.getColor().mediumVariant;
        float scale = 255f;

        return new float[] { ((color >> 16) & 0xff) / scale, ((color >> 8) & 0xff) / scale, (color & 0xff) / scale };
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
        StaticBloomMetadata metadata = META_CACHE.get(partBeamFormer);
        if (metadata != null) {
            return metadata;
        }

        ForgeDirection facing = partBeamFormer.getDirection();

        int dx = facing.offsetX;
        int dy = facing.offsetY;
        int dz = facing.offsetZ;

        float pitch = (float) Math.atan2(Math.sqrt(dx * dx + dz * dz), dy) * (180F / (float) Math.PI);
        float yaw = (float) (180 - Math.atan2(dz, dx) * (180F / (float) Math.PI) - 90.0F);

        StaticBloomMetadata newMetadata = new StaticBloomMetadata(dx, dy, dz, pitch, yaw);
        META_CACHE.put(partBeamFormer, newMetadata);

        return newMetadata;
    }

    public static void init(IBeamFormer partBeamFormer) {
        RENDERER.init(partBeamFormer);
    }

    @Desugar
    public record StaticBloomMetadata(int dx, int dy, int dz, float pitch, float yaw) {}
}
