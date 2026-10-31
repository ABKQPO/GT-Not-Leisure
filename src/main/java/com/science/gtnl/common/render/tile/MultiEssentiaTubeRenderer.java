package com.science.gtnl.common.render.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.machine.basicMachine.SmallEssentiaSmeltery;
import com.science.gtnl.loader.RenderLoader;

import thaumcraft.client.renderers.block.BlockTubeRenderer;

public class MultiEssentiaTubeRenderer extends BlockTubeRenderer {

    @Override
    public int getRenderId() {
        return RenderLoader.MULTI_ESSENTIA_TUBE_RENDER_ID;
    }

    @Override
    public TileEntity getConnectableTile(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        TileEntity connectableTile = super.getConnectableTile(world, x, y, z, face);
        if (connectableTile != null) return connectableTile;

        TileEntity adjacentTile = world.getTileEntity(x + face.offsetX, y + face.offsetY, z + face.offsetZ);
        return SmallEssentiaSmeltery.isOutputFacing(adjacentTile, face.getOpposite()) ? adjacentTile : null;
    }
}
