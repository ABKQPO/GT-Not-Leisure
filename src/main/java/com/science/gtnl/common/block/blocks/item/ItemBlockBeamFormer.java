package com.science.gtnl.common.block.blocks.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.mixins.late.AppliedEnergistics.AccessorAEBaseItemBlock;

import appeng.block.AEBaseItemBlock;
import appeng.block.AEBaseTileBlock;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.AEBaseTile;

public class ItemBlockBeamFormer extends AEBaseItemBlock {

    public ItemBlockBeamFormer(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(final ItemStack stack, final EntityPlayer player, final World w, final int x,
        final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ,
        final int metadata) {
        var blockType = ((AccessorAEBaseItemBlock) this).getBlockType();
        ForgeDirection up;
        ForgeDirection forward;

        forward = ForgeDirection.getOrientation(side);
        if (forward == ForgeDirection.UP || forward == ForgeDirection.DOWN) {
            up = ForgeDirection.SOUTH;
        } else {
            up = ForgeDirection.UP;
        }

        if (!blockType.isValidOrientation(w, x, y, z, forward, up)) {
            return false;
        }

        if (!w.setBlock(x, y, z, field_150939_a, metadata, 3)) {
            return false;
        }

        if (w.getBlock(x, y, z) == field_150939_a) {
            field_150939_a.onBlockPlacedBy(w, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(w, x, y, z, metadata);

            final AEBaseTile tile = ((AEBaseTileBlock) blockType).getTileEntity(w, x, y, z);
            if (tile == null) return true;

            if (tile.getForward() == null || tile.getForward() == ForgeDirection.UNKNOWN
                || tile.getUp() == null
                || tile.getUp() == ForgeDirection.UNKNOWN) {
                tile.setOrientation(forward, up);
            }

            if (tile instanceof IGridProxyable proxyable) {
                proxyable.getProxy()
                    .setOwner(player);
            }

            tile.onPlacement(stack, player, side);
        }

        return true;
    }
}
