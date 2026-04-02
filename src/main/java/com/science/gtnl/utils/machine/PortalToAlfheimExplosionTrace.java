package com.science.gtnl.utils.machine;

import static com.science.gtnl.utils.machine.PortalToAlfheimExplosion.EXPLOSION;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.brandon3055.brandonscore.common.handlers.IProcess;

public class PortalToAlfheimExplosionTrace implements IProcess {

    private final World world;
    private final int xCoord;
    private final int yCoord;
    private final int zCoord;
    private final float power;
    private boolean isDead = false;

    public PortalToAlfheimExplosionTrace(World world, int x, int y, int z, float power) {
        this.world = world;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.power = power;
    }

    @Override
    public void updateProcess() {

        float energy = power * 20;
        for (int y = yCoord; y > 0 && energy > 0; y--) {
            Block block = world.getBlock(xCoord, y, zCoord);

            List<Entity> entities = world.getEntitiesWithinAABB(
                Entity.class,
                AxisAlignedBB.getBoundingBox(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));
            for (Entity entity : entities) {
                entity.attackEntityFrom(EXPLOSION, power * 100);
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).setHealth(0.0F);
                    ((EntityPlayer) entity).onDeath(EXPLOSION);
                }
            }

            energy -= block instanceof BlockLiquid ? 5 : block.getExplosionResistance(null);

            boolean blockRemoved = false;
            if (energy >= 0 && block != Blocks.air) {
                world.setBlockToAir(xCoord, y, zCoord);
                blockRemoved = true;
            }
            energy -= 0.5F + 0.1F * (yCoord - y);

            if (world.rand.nextInt(20) == 0 && blockRemoved) {
                if (world.rand.nextInt(3) > 0) {
                    world.setBlock(xCoord, y, zCoord, Blocks.fire);
                } else {
                    world.setBlock(xCoord, y, zCoord, Blocks.flowing_lava);
                }
            }
        }

        energy = power * 20;
        for (int y = yCoord + 1; y < 255 && energy > 0; y++) {
            Block block = world.getBlock(xCoord, y, zCoord);

            List<Entity> entities = world.getEntitiesWithinAABB(
                Entity.class,
                AxisAlignedBB.getBoundingBox(xCoord, y, zCoord, xCoord + 1, y + 1, zCoord + 1));
            for (Entity entity : entities) {
                entity.attackEntityFrom(EXPLOSION, power * 100);
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).setHealth(0.0F);
                    ((EntityPlayer) entity).onDeath(EXPLOSION);
                }
            }

            energy -= block instanceof BlockLiquid ? 10 : block.getExplosionResistance(null);
            if (energy >= 0) {
                world.setBlockToAir(xCoord, y, zCoord);
            }
            energy -= 0.5F + (0.1F * (y - yCoord));
        }

        isDead = true;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
}
