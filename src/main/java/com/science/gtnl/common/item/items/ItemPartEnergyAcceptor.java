package com.science.gtnl.common.item.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartEnergyAcceptor;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartEnergyAcceptor extends Item implements IPartItem {

    public ItemPartEnergyAcceptor() {
        setMaxStackSize(64);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setUnlocalizedName("PartEnergyAcceptor");
        GameRegistry.registerItem(this, "part_energy_acceptor");
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
        GTNLItemList.PartEnergyAcceptor.set(new ItemStack(this));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.item.part_energy_acceptor";
    }

    @Override
    public PartEnergyAcceptor createPartFromItemStack(ItemStack stack) {
        return new PartEnergyAcceptor(stack);
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
    public IIcon getIconIndex(ItemStack stack) {
        Block energyAcceptor = AEApi.instance()
            .definitions()
            .blocks()
            .energyAcceptor()
            .maybeBlock()
            .orNull();
        return energyAcceptor.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }
}
