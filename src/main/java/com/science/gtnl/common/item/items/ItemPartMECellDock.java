package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartMECellDock;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;

public class ItemPartMECellDock extends Item implements IPartItem {

    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    private static final int MODE_PLAIN = 0;
    private static final int MODE_INVERT = 1;
    private static final int MODE_MIRROR = 2;

    @SideOnly(Side.CLIENT)
    public static IIcon frontTexture;
    @SideOnly(Side.CLIENT)
    public static IIcon sideTexture;

    @SideOnly(Side.CLIENT)
    public static FaceUV bodyDown;
    @SideOnly(Side.CLIENT)
    public static FaceUV bodyUp;
    @SideOnly(Side.CLIENT)
    public static FaceUV bodyNorth;
    @SideOnly(Side.CLIENT)
    public static FaceUV bodySouth;
    @SideOnly(Side.CLIENT)
    public static FaceUV bodyWest;
    @SideOnly(Side.CLIENT)
    public static FaceUV bodyEast;
    @SideOnly(Side.CLIENT)
    public static FaceUV baseDown;
    @SideOnly(Side.CLIENT)
    public static FaceUV baseUp;
    @SideOnly(Side.CLIENT)
    public static FaceUV baseNorth;
    @SideOnly(Side.CLIENT)
    public static FaceUV baseSouth;
    @SideOnly(Side.CLIENT)
    public static FaceUV baseWest;
    @SideOnly(Side.CLIENT)
    public static FaceUV baseEast;
    @SideOnly(Side.CLIENT)
    public static FaceUV slotUp;
    @SideOnly(Side.CLIENT)
    public static FaceUV slotNorth;
    @SideOnly(Side.CLIENT)
    public static FaceUV slotSouth;
    @SideOnly(Side.CLIENT)
    public static FaceUV slotWest;
    @SideOnly(Side.CLIENT)
    public static FaceUV slotEast;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsVerticalEast;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsVerticalWest;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsHorizontalUp;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsHorizontalDown;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsCenterEast;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsCenterWest;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsCenterUp;
    @SideOnly(Side.CLIENT)
    public static FaceUV internalsCenterDown;

    public ItemPartMECellDock() {
        setMaxStackSize(64);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setUnlocalizedName("MECellDock");
        setTextureName(RESOURCE_ROOT_ID + ":part/cell_dock");
        GameRegistry.registerItem(this, "me_cell_dock");
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
        GTNLItemList.MECellDock.set(new ItemStack(this));
    }

    @Override
    @Nullable
    public PartMECellDock createPartFromItemStack(ItemStack stack) {
        return new PartMECellDock(stack);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float xOffset, float yOffset, float zOffset) {
        return AEApi.instance()
            .partHelper()
            .placeBus(player.getHeldItem(), x, y, z, side, player, world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        frontTexture = register.registerIcon(RESOURCE_ROOT_ID + ":part/cell_dock");
        sideTexture = register.registerIcon(RESOURCE_ROOT_ID + ":part/cell_dock_side");

        bodyDown = new FaceUV(frontTexture, AXIS_X, 3, 3, 13, 13, AXIS_Z, 12, 16, 16, 12);
        bodyUp = new FaceUV(frontTexture, AXIS_X, 3, 3, 13, 13, AXIS_Z, 12, 0, 16, 4);
        bodyNorth = new FaceUV(sideTexture, AXIS_X, 13, 4, 3, 14, AXIS_Y, 13, 6, 3, 16);
        bodySouth = new FaceUV(frontTexture, AXIS_X, 3, 3, 13, 13, AXIS_Y, 13, 3, 3, 13);
        bodyWest = new FaceUV(frontTexture, AXIS_Z, 12, 0, 16, 4, AXIS_Y, 13, 3, 3, 13);
        bodyEast = new FaceUV(frontTexture, AXIS_Z, 16, 12, 12, 16, AXIS_Y, 13, 3, 3, 13);

        baseDown = new FaceUV(sideTexture, AXIS_X, 11, 10, 5, 16, AXIS_Z, 10.99f, 4.5f, 12, 4.5f);
        baseUp = new FaceUV(sideTexture, AXIS_X, 11, 10, 5, 16, AXIS_Z, 10.99f, 1.5f, 12, 1.5f);
        baseNorth = new FaceUV(sideTexture, AXIS_X, 11, 10, 5, 16, AXIS_Y, 11, 0, 5, 6);
        baseSouth = new FaceUV(sideTexture, AXIS_X, 5, 10, 11, 16, AXIS_Y, 11, 0, 5, 6);
        baseWest = new FaceUV(sideTexture, AXIS_Z, 10.99f, 14.5f, 12, 14.5f, AXIS_Y, 11, 0, 5, 6);
        baseEast = new FaceUV(sideTexture, AXIS_Z, 12, 11.5f, 10.99f, 11.5f, AXIS_Y, 11, 0, 5, 6);

        slotUp = new FaceUV(frontTexture, AXIS_X, 4.99f, 5, 11.01f, 11, AXIS_Z, 12.99f, 10, 15.01f, 12);
        slotNorth = new FaceUV(frontTexture, AXIS_X, 11.01f, 5, 4.99f, 11, AXIS_Y, 13.01f, 10.05f, 10.01f, 13);
        slotSouth = new FaceUV(frontTexture, AXIS_X, 4.99f, 5, 11.01f, 11, AXIS_Y, 13.01f, 10.05f, 10.01f, 13);
        slotWest = new FaceUV(frontTexture, AXIS_Z, 12.99f, 7, 15.01f, 9, AXIS_Y, 13.01f, 10.05f, 10.01f, 13);
        slotEast = new FaceUV(frontTexture, AXIS_Z, 15.01f, 7, 12.99f, 9, AXIS_Y, 13.01f, 10.05f, 10.01f, 13);

        internalsVerticalEast = new FaceUV(sideTexture, AXIS_Z, 12, 15, 11, 16, AXIS_Y, 11, 10, 5, 16);
        internalsVerticalWest = new FaceUV(sideTexture, AXIS_Z, 11, 15, 12, 16, AXIS_Y, 11, 10, 5, 16);
        internalsHorizontalUp = new FaceUV(sideTexture, AXIS_Z, 11, 15, 12, 16, AXIS_X, 11, 10, 5, 16);
        internalsHorizontalDown = new FaceUV(sideTexture, AXIS_Z, 12, 15, 11, 16, AXIS_X, 11, 10, 5, 16);
        internalsCenterEast = new FaceUV(sideTexture, AXIS_Z, 12, 14, 11, 15, AXIS_Y, 9.99f, 1, 6.01f, 5);
        internalsCenterWest = new FaceUV(sideTexture, AXIS_Z, 11, 14, 12, 15, AXIS_Y, 9.99f, 1, 6.01f, 5);
        internalsCenterUp = new FaceUV(sideTexture, AXIS_Z, 12, 14, 11, 15, AXIS_X, 9.99f, 5, 6.01f, 1);
        internalsCenterDown = new FaceUV(sideTexture, AXIS_Z, 11, 14, 12, 15, AXIS_X, 9.99f, 5, 6.01f, 1);

        itemIcon = frontTexture;
    }

    @SideOnly(Side.CLIENT)
    private static boolean isPositive(ForgeDirection dir) {
        return dir == ForgeDirection.EAST || dir == ForgeDirection.UP || dir == ForgeDirection.SOUTH;
    }

    @SideOnly(Side.CLIENT)
    private static int worldAxis(ForgeDirection dir) {
        return switch (dir) {
            case WEST, EAST -> AXIS_X;
            case DOWN, UP -> AXIS_Y;
            default -> AXIS_Z;
        };
    }

    @SideOnly(Side.CLIENT)
    private static int worldFaceAxisU(ForgeDirection face) {
        return switch (face) {
            case WEST, EAST -> AXIS_Z;
            default -> AXIS_X;
        };
    }

    @SideOnly(Side.CLIENT)
    private static int worldFaceModeU(ForgeDirection face) {
        return switch (face) {
            case NORTH, EAST -> MODE_MIRROR;
            default -> MODE_PLAIN;
        };
    }

    @SideOnly(Side.CLIENT)
    private static int worldFaceModeV(ForgeDirection face) {
        return switch (face) {
            case DOWN, UP -> MODE_PLAIN;
            default -> MODE_INVERT;
        };
    }

    @SideOnly(Side.CLIENT)
    private static int worldFaceSwapRotation(ForgeDirection face) {
        return switch (face) {
            case DOWN, NORTH, EAST -> 2;
            default -> 1;
        };
    }

    @SideOnly(Side.CLIENT)
    private static float param(ForgeDirection axis, int mode, float self, float other) {
        float coord = mode == MODE_MIRROR ? other : self;
        float world = isPositive(axis) ? coord : 16 - coord;
        return mode == MODE_INVERT ? 16 - world : world;
    }

    @SideOnly(Side.CLIENT)
    public static final class FaceUV {

        private final IIcon source;
        private final int axisU;
        private final float coordU0;
        private final float texelU0;
        private final float coordU1;
        private final float texelU1;
        private final int axisV;
        private final float coordV0;
        private final float texelV0;
        private final float coordV1;
        private final float texelV1;

        public FaceUV(IIcon source, int axisU, float coordU0, float texelU0, float coordU1, float texelU1, int axisV,
            float coordV0, float texelV0, float coordV1, float texelV1) {
            this.source = source;
            this.axisU = axisU;
            this.coordU0 = coordU0;
            this.texelU0 = texelU0;
            this.coordU1 = coordU1;
            this.texelU1 = texelU1;
            this.axisV = axisV;
            this.coordV0 = coordV0;
            this.texelV0 = texelV0;
            this.coordV1 = coordV1;
            this.texelV1 = texelV1;
        }

        public MappedIcon bake(ForgeDirection[] axes, ForgeDirection worldFace) {
            ForgeDirection dirU = axes[axisU];
            ForgeDirection dirV = axes[axisV];
            boolean swapped = worldAxis(dirU) != worldFaceAxisU(worldFace);
            int modeU;
            int modeV;
            if (swapped) {
                boolean vertical = worldFace == ForgeDirection.UP || worldFace == ForgeDirection.DOWN;
                modeU = vertical ? MODE_PLAIN : MODE_MIRROR;
                modeV = MODE_INVERT;
            } else {
                modeU = worldFaceModeU(worldFace);
                modeV = worldFaceModeV(worldFace);
            }
            return new MappedIcon(
                source,
                swapped ? worldFaceSwapRotation(worldFace) : 0,
                param(dirU, modeU, coordU0, coordU1),
                param(dirU, modeU, coordU1, coordU0),
                texelU0,
                texelU1,
                param(dirV, modeV, coordV0, coordV1),
                param(dirV, modeV, coordV1, coordV0),
                texelV0,
                texelV1);
        }
    }

    @SideOnly(Side.CLIENT)
    public static final class MappedIcon implements IIcon {

        private final IIcon source;
        @Getter
        private final int uvRotation;
        private final float aFrom;
        private final float aTo;
        private final float uFrom;
        private final float uTo;
        private final float bFrom;
        private final float bTo;
        private final float vFrom;
        private final float vTo;

        public MappedIcon(IIcon source, int uvRotation, float aFrom, float aTo, float uFrom, float uTo, float bFrom,
            float bTo, float vFrom, float vTo) {
            this.source = source;
            this.uvRotation = uvRotation;
            this.aFrom = aFrom;
            this.aTo = aTo;
            this.uFrom = uFrom;
            this.uTo = uTo;
            this.bFrom = bFrom;
            this.bTo = bTo;
            this.vFrom = vFrom;
            this.vTo = vTo;
        }

        private static double remap(double value, float from, float to, float target0, float target1) {
            double span = to - from;
            double mapped = span == 0 ? target0 : target0 + (target1 - target0) * (value - from) / span;
            return mapped < 0 ? 0 : Math.min(mapped, 16);
        }

        @Override
        public int getIconWidth() {
            return source.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return source.getIconHeight();
        }

        @Override
        public float getMinU() {
            return source.getInterpolatedU(Math.min(uFrom, uTo));
        }

        @Override
        public float getMaxU() {
            return source.getInterpolatedU(Math.max(uFrom, uTo));
        }

        @Override
        public float getInterpolatedU(double value) {
            return source.getInterpolatedU(remap(value, aFrom, aTo, uFrom, uTo));
        }

        @Override
        public float getMinV() {
            return source.getInterpolatedV(Math.min(vFrom, vTo));
        }

        @Override
        public float getMaxV() {
            return source.getInterpolatedV(Math.max(vFrom, vTo));
        }

        @Override
        public float getInterpolatedV(double value) {
            return source.getInterpolatedV(remap(value, bFrom, bTo, vFrom, vTo));
        }

        @Override
        public String getIconName() {
            return source.getIconName();
        }
    }
}
