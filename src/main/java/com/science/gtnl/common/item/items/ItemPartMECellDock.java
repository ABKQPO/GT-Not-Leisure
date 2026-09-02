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
    public static IIcon bodyIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyDownIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyUpIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyNorthIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon bodySouthIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyWestIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon bodyEastIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon baseDownIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon baseUpIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon baseNorthIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon baseSouthIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon baseWestIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon baseEastIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalVerticalIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalVerticalEastIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalHorizontalIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalHorizontalDownIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalCenterSideIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalCenterEastIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalCenterFaceIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon internalCenterDownIcon;

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
        bodyIcon = register.registerIcon(RESOURCE_ROOT_ID + ":part/cell_dock");
        sideIcon = register.registerIcon(RESOURCE_ROOT_ID + ":part/cell_dock_side");
        bodyDownIcon = new UVIcon(bodyIcon, 13, 16, 3, 12);
        bodyUpIcon = new UVIcon(bodyIcon, 13, 4, 3, 0);
        bodyNorthIcon = new UVIcon(sideIcon, 4, 6, 14, 16);
        bodySouthIcon = new UVIcon(bodyIcon, 3, 3, 13, 13);
        bodyWestIcon = new UVIcon(bodyIcon, 12, 3, 16, 13);
        bodyEastIcon = new UVIcon(bodyIcon, 0, 3, 4, 13);
        baseDownIcon = new UVIcon(sideIcon, 10, 5, 16, 6);
        baseUpIcon = new UVIcon(sideIcon, 10, 0, 16, 1);
        baseNorthIcon = new UVIcon(sideIcon, 10, 0, 16, 6);
        baseSouthIcon = new UVIcon(sideIcon, 10, 0, 16, 6);
        baseWestIcon = new UVIcon(sideIcon, 10, 0, 11, 6);
        baseEastIcon = new UVIcon(sideIcon, 15, 0, 16, 6);
        internalVerticalIcon = new UVIcon(sideIcon, 15, 10, 16, 16);
        internalVerticalEastIcon = internalVerticalIcon;
        internalHorizontalIcon = new UVIcon(sideIcon, 15, 10, 16, 16);
        internalHorizontalDownIcon = internalHorizontalIcon;
        internalCenterSideIcon = new UVIcon(sideIcon, 14, 1, 15, 5);
        internalCenterEastIcon = internalCenterSideIcon;
        internalCenterFaceIcon = internalCenterSideIcon;
        internalCenterDownIcon = internalCenterSideIcon;
        itemIcon = bodyIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    private static class UVIcon implements IIcon {

        private final IIcon source;
        private final float minU;
        private final float minV;
        private final float maxU;
        private final float maxV;

        private UVIcon(IIcon source, float minU, float minV, float maxU, float maxV) {
            this.source = source;
            this.minU = minU;
            this.minV = minV;
            this.maxU = maxU;
            this.maxV = maxV;
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
            return getInterpolatedU(0);
        }

        @Override
        public float getMaxU() {
            return getInterpolatedU(16);
        }

        @Override
        public float getInterpolatedU(double value) {
            float coordinate = (float) (minU + (maxU - minU) * value / 16);
            return source.getInterpolatedU(Math.max(0, Math.min(16, coordinate)));
        }

        @Override
        public float getMinV() {
            return getInterpolatedV(0);
        }

        @Override
        public float getMaxV() {
            return getInterpolatedV(16);
        }

        @Override
        public float getInterpolatedV(double value) {
            float coordinate = (float) (minV + (maxV - minV) * value / 16);
            return source.getInterpolatedV(Math.max(0, Math.min(16, coordinate)));
        }

        @Override
        public String getIconName() {
            return source.getIconName();
        }

    }
}
