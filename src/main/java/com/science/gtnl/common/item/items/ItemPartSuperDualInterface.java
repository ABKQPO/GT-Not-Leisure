package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartSuperDualInterface;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartSuperDualInterface extends Item implements IPartItem {

    public ItemPartSuperDualInterface() {
        setMaxStackSize(64);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setUnlocalizedName("PartSuperDualInterface");
        setTextureName(RESOURCE_ROOT_ID + ":SuperDualInterface");
        GameRegistry.registerItem(this, "PartSuperDualInterface");
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
        GTNLItemList.PartSuperDualInterface.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.item.part_super_dual_interface";
    }

    @Nullable
    @Override
    public PartSuperDualInterface createPartFromItemStack(ItemStack is) {
        return new PartSuperDualInterface(is);
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
