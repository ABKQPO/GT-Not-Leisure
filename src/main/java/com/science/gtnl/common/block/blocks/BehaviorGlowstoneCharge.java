package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.science.gtnl.common.block.blocks.tile.TileEntityDimensionRespawnAnchor;

public class BehaviorGlowstoneCharge extends BehaviorDefaultDispenseItem {

    @Override
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {

        World world = source.getWorld();

        EnumFacing facing = BlockDispenser.func_149937_b(source.getBlockMetadata());

        int x = source.getXInt() + facing.getFrontOffsetX();
        int y = source.getYInt() + facing.getFrontOffsetY();
        int z = source.getZInt() + facing.getFrontOffsetZ();

        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileEntityDimensionRespawnAnchor anchor) {

            if (anchor.getEnergyLevel() < 4) {

                anchor.addEnergy(1);

                world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    RESOURCE_ROOT_ID + ":respawn_anchor.charge" + (1 + world.rand.nextInt(3)),
                    1.0F,
                    0.8F + world.rand.nextFloat() * 0.4F);

                stack.stackSize--;

                world.markBlockForUpdate(x, y, z);

                return stack;
            }
        }

        return super.dispenseStack(source, stack);
    }
}
