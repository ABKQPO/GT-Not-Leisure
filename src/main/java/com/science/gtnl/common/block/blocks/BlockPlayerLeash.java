package com.science.gtnl.common.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockPlayerLeash;
import com.science.gtnl.common.entity.EntityPlayerLeashKnot;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockPlayerLeash extends Block {

    public BlockPlayerLeash() {
        super(Material.wood);
        this.setHardness(3f);
        this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
        this.setLightOpacity(0);
        this.setBlockName("gtnl.player_leash");
        this.setBlockTextureName("planks_oak");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockPlayerLeash.class, "player_leash");
        GTNLItemList.PlayerLeash.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.player_leash";
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote && placer instanceof EntityPlayerMP player) {
            EntityPlayerLeashKnot leashKnot = new EntityPlayerLeashKnot(world, x + 0.5, y + 0.5, z + 0.5, player);
            world.spawnEntityInWorld(leashKnot);
        }
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
