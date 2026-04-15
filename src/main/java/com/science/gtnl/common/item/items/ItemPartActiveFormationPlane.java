package com.science.gtnl.common.item.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartActiveFormationPlane;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;

public class ItemPartActiveFormationPlane extends Item implements IPartItem {

    public ItemPartActiveFormationPlane() {
        this.setMaxStackSize(64);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        this.setUnlocalizedName("PartActiveFormationPlane");
        this.setTextureName(Mods.AppliedEnergistics2.ID + ":" + "ItemPart.FormationPlane");
        GameRegistry.registerItem(this, getUnlocalizedName());
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
        GTNLItemList.PartActiveFormationPlane.set(new ItemStack(this, 1));
    }

    @Nullable
    @Override
    public PartActiveFormationPlane createPartFromItemStack(ItemStack is) {
        return new PartActiveFormationPlane(is);
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
