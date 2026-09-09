package com.science.gtnl.common.block.blocks;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.BlockFluidBase;
import com.science.gtnl.common.recipe.gtnl.ShimmerRecipes;
import com.science.gtnl.loader.EffectLoader;

import cpw.mods.fml.common.registry.GameRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class BlockShimmerFluid extends BlockFluidBase {

    public Object2IntMap<UUID> itemTimeMap = new Object2IntOpenHashMap<>();

    public BlockShimmerFluid(Fluid fluid) {
        super(fluid, Material.water);
        this.setBlockName("gtnl.block.shimmer");
        this.setQuantaPerBlock(7);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, "shimmer_fluid_block");
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (world.isRemote) return;

        if (entity instanceof EntityLivingBase living) {
            PotionEffect effect = living.getActivePotionEffect(EffectLoader.shimmering);
            if (effect == null) {
                living.addPotionEffect(new PotionEffect(EffectLoader.shimmering.getId(), 6000, 0));
            }
        }

        if (entity instanceof EntityItem itemEntity) {
            handleItemEntity(world, itemEntity);
        }

        if (entity instanceof IProjectile) {
            if (entity.motionY < 0) {
                entity.motionY *= -0.7;
            } else {
                entity.motionY += 0.5;
            }

            entity.isAirBorne = true;
            entity.velocityChanged = true;
        }
    }

    private void handleItemEntity(World world, EntityItem itemEntity) {
        UUID uuid = itemEntity.getUniqueID();
        int time = itemTimeMap.getOrDefault(uuid, 0) + 1;
        itemTimeMap.put(uuid, time);

        if (itemEntity.getEntityData()
            .getBoolean("ShimmerConverted")) {
            if (isInFluid(world, itemEntity) && !isAboveFluid(world, itemEntity)) {
                itemEntity.motionY = 0.05;
                itemEntity.velocityChanged = true;
            }
            return;
        }

        if (time < 20) return;

        ItemStack original = itemEntity.getEntityItem();
        List<ItemStack> results = ShimmerRecipes.getConversionResult(original);

        if (results != null && !results.isEmpty()) {
            spawnShimmerParticles(world, itemEntity.posX, itemEntity.posY, itemEntity.posZ);
            itemEntity.setDead();

            for (ItemStack output : results) {
                while (output.stackSize > 0) {
                    int count = Math.min(64, output.stackSize);
                    ItemStack part = output.splitStack(count);

                    EntityItem newItem = new EntityItem(world, itemEntity.posX, itemEntity.posY, itemEntity.posZ, part);
                    newItem.delayBeforeCanPickup = 10;
                    newItem.lifespan = 6000;
                    newItem.getEntityData()
                        .setBoolean("ShimmerConverted", true);
                    world.spawnEntityInWorld(newItem);
                }
            }

            itemTimeMap.remove(uuid);
        }
    }

    private void spawnShimmerParticles(World world, double x, double y, double z) {
        if (world.isRemote) return;

        for (int i = 0; i < 40; i++) {
            double offsetX = (world.rand.nextDouble() - 0.5) * 0.5;
            double offsetY = world.rand.nextDouble() * 0.5 + 1;
            double offsetZ = (world.rand.nextDouble() - 0.5) * 0.5;

            double motionX = (world.rand.nextDouble() - 0.5) * 0.1;
            double motionY = world.rand.nextDouble() * 0.1;
            double motionZ = (world.rand.nextDouble() - 0.5) * 0.1;

            world.spawnParticle("fireworksSpark", x + offsetX, y + offsetY, z + offsetZ, motionX, motionY, motionZ);
        }
    }

    private boolean isInFluid(World world, Entity entity) {
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY);
        int z = MathHelper.floor_double(entity.posZ);
        return world.getBlock(x, y, z) == this;
    }

    private boolean isAboveFluid(World world, Entity entity) {
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY + 0.5);
        int z = MathHelper.floor_double(entity.posZ);
        return world.getBlock(x, y, z) != this;
    }
}
