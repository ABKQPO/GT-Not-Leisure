package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaInputHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaInputHatch;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.util.GTUtility;
import thaumcraft.api.aspects.AspectList;

public class BlockMultiEssentiaInputHatch extends BlockContainer {

    public BlockMultiEssentiaInputHatch() {
        super(Material.iron);
        setHardness(9.0F);
        setResistance(5.0F);
        setBlockName("MultiEssentiaInputHatch");
        setBlockTextureName(RESOURCE_ROOT_ID + ":EssentiaHatch");
        setHarvestLevel("wrench", 2);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureMachine);
        GameRegistry.registerBlock(this, ItemBlockMultiEssentiaInputHatch.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityMultiEssentiaInputHatch.class, "MultiEssentiaInputHatchTileEntity");
        GregTechAPI.registerMachineBlock(this, -1);
        GTNLItemList.MultiEssentiaInputHatch.set(new ItemStack(this, 1));
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (GregTechAPI.isMachineBlock(this, world.getBlockMetadata(x, y, z))) {
            GregTechAPI.causeMachineUpdate(world, x, y, z);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (GregTechAPI.isMachineBlock(this, world.getBlockMetadata(x, y, z))) {
            GregTechAPI.causeMachineUpdate(world, x, y, z);
        }
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote || !player.isSneaking() || player.getHeldItem() != null) return false;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMultiEssentiaInputHatch hatch)) return false;

        int removed = hatch.getTotalAmount();
        hatch.setAspects(new AspectList());
        GTUtility.sendChatTrans(player, "Info_MultiEssentiaInputHatch_Cleared", removed);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMultiEssentiaInputHatch();
    }
}
