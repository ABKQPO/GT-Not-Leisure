package com.science.gtnl.common.block.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.BlockFluidBase;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockHoneyFluid extends BlockFluidBase {

    public BlockHoneyFluid(Fluid fluid) {
        super(fluid, Material.water);
        this.setBlockName("gtnl.honey");
        this.setQuantaPerBlock(4);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, "honey_fluid_block");
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        entity.motionX *= 0.2;
        entity.motionZ *= 0.2;
        entity.motionY *= 0.2;

        if (entity instanceof EntityLivingBase living) {
            living.addPotionEffect(new PotionEffect(Potion.regeneration.id, 100, 0));
        }
    }
}
