package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartMECellDock;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartMECellDock extends Item implements IPartItem {

    @SideOnly(Side.CLIENT)
    public static IIcon frontTexture;
    @SideOnly(Side.CLIENT)
    public static IIcon sideTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon bodyDown;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyUp;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyNorth;
    @SideOnly(Side.CLIENT)
    public static IIcon bodySouth;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyWest;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyEast;

    @SideOnly(Side.CLIENT)
    public static IIcon baseDown;
    @SideOnly(Side.CLIENT)
    public static IIcon baseUp;
    @SideOnly(Side.CLIENT)
    public static IIcon baseNorth;
    @SideOnly(Side.CLIENT)
    public static IIcon baseSouth;
    @SideOnly(Side.CLIENT)
    public static IIcon baseWest;
    @SideOnly(Side.CLIENT)
    public static IIcon baseEast;

    @SideOnly(Side.CLIENT)
    public static IIcon slotUp;
    @SideOnly(Side.CLIENT)
    public static IIcon slotNorth;
    @SideOnly(Side.CLIENT)
    public static IIcon slotSouth;
    @SideOnly(Side.CLIENT)
    public static IIcon slotWest;
    @SideOnly(Side.CLIENT)
    public static IIcon slotEast;

    @SideOnly(Side.CLIENT)
    public static IIcon internalsVertical;
    @SideOnly(Side.CLIENT)
    public static IIcon internalsHorizontal;
    @SideOnly(Side.CLIENT)
    public static IIcon internalsCenterSide;
    @SideOnly(Side.CLIENT)
    public static IIcon internalsCenterFace;

    public ItemPartMECellDock() {
        setMaxStackSize(64);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setUnlocalizedName("MECellDock");
        setTextureName(RESOURCE_ROOT_ID + ":part/cell_dock");
        GameRegistry.registerItem(this, getUnlocalizedName());
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
    public void registerIcons(IIconRegister register) {
        frontTexture = register.registerIcon(RESOURCE_ROOT_ID + ":part/cell_dock");
        sideTexture = register.registerIcon(RESOURCE_ROOT_ID + ":part/cell_dock_side");

        bodyDown = new MappedIcon(frontTexture, 3, 13, 3, 13, 12, 16, 12, 16);
        bodyUp = new MappedIcon(frontTexture, 3, 13, 3, 13, 12, 16, 0, 4);
        bodyNorth = new MappedIcon(sideTexture, 3, 13, 4, 14, 3, 13, 6, 16);
        bodySouth = new MappedIcon(frontTexture, 3, 13, 3, 13, 3, 13, 3, 13);
        bodyWest = new MappedIcon(frontTexture, 12, 16, 0, 4, 3, 13, 3, 13);
        bodyEast = new MappedIcon(frontTexture, 12, 16, 12, 16, 3, 13, 3, 13);

        baseDown = new MappedIcon(sideTexture, 5, 11, 16, 10, 10.99f, 12, 6, 5);
        baseUp = new MappedIcon(sideTexture, 5, 11, 16, 10, 10.99f, 12, 1, 0);
        baseNorth = new MappedIcon(sideTexture, 5, 11, 10, 16, 5, 11, 0, 6);
        baseSouth = new MappedIcon(sideTexture, 5, 11, 10, 16, 5, 11, 0, 6);
        baseWest = new MappedIcon(sideTexture, 10.99f, 12, 15, 16, 5, 11, 0, 6);
        baseEast = new MappedIcon(sideTexture, 10.99f, 12, 10, 11, 5, 11, 0, 6);

        slotUp = new MappedIcon(frontTexture, 4.99f, 11.01f, 5, 11, 12.99f, 15.01f, 10, 12);
        slotNorth = new MappedIcon(frontTexture, 4.99f, 11.01f, 5, 11, 2.99f, 5.99f, 10.05f, 13);
        slotSouth = new MappedIcon(frontTexture, 4.99f, 11.01f, 5, 11, 2.99f, 5.99f, 10.05f, 13);
        slotWest = new MappedIcon(frontTexture, 12.99f, 15.01f, 7, 9, 2.99f, 5.99f, 10.05f, 13);
        slotEast = new MappedIcon(frontTexture, 12.99f, 15.01f, 7, 9, 2.99f, 5.99f, 10.05f, 13);

        internalsVertical = new MappedIcon(sideTexture, 11, 12, 15, 16, 5, 11, 10, 16);
        internalsHorizontal = new MappedIcon(sideTexture, 0, 16, 15.5f, 15.5f, 0, 16, 10.5f, 10.5f);
        internalsCenterSide = new MappedIcon(sideTexture, 11, 12, 14, 15, 6.01f, 9.99f, 1, 5);
        internalsCenterFace = new MappedIcon(sideTexture, 6.01f, 9.99f, 14, 15, 11, 12, 1, 5);

        itemIcon = frontTexture;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public static final class MappedIcon implements IIcon {

        private final IIcon source;
        private final float aFrom;
        private final float aTo;
        private final float uFrom;
        private final float uTo;
        private final float bFrom;
        private final float bTo;
        private final float vFrom;
        private final float vTo;

        public MappedIcon(IIcon source, float aFrom, float aTo, float uFrom, float uTo, float bFrom, float bTo,
            float vFrom, float vTo) {
            this.source = source;
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
