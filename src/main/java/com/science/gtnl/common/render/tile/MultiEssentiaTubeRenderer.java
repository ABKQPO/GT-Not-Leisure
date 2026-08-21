package com.science.gtnl.common.render.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.ClientProxy;
import com.science.gtnl.common.machine.basicMachine.SmallEssentiaSmeltery;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import thaumcraft.client.renderers.block.BlockTubeRenderer;

public class MultiEssentiaTubeRenderer extends BlockTubeRenderer {

    @Override
    public int getRenderId() {
        return ClientProxy.MULTI_ESSENTIA_TUBE_RENDER_ID;
    }

    @Override
    public TileEntity getConnectableTile(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        TileEntity connectableTile = super.getConnectableTile(world, x, y, z, face);
        if (connectableTile != null) return connectableTile;

        TileEntity adjacentTile = world.getTileEntity(x + face.offsetX, y + face.offsetY, z + face.offsetZ);
        if (adjacentTile instanceof IGregTechTileEntity gregTechTile
            && gregTechTile.getMetaTileEntity() instanceof SmallEssentiaSmeltery
            && gregTechTile.getFrontFacing() == face.getOpposite()) {
            return adjacentTile;
        }
        return null;
    }
}
