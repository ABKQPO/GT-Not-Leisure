package com.science.gtnl.common.item.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.part.PartSuperInterface;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartSuperInterface extends Item implements IPartItem {

    public ItemPartSuperInterface() {
        this.setMaxStackSize(64);
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.part_super_interface";
    }

    @Nullable
    @Override
    public PartSuperInterface createPartFromItemStack(ItemStack is) {
        return new PartSuperInterface(is);
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
}
