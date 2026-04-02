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
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockEssentiaHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.util.GTUtility;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class BlockEssentiaHatch extends BlockContainer {

    public BlockEssentiaHatch() {
        super(Material.iron);
        this.setHardness(9.0F);
        this.setResistance(5.0F);
        this.setBlockName("EssentiaHatch");
        this.setBlockTextureName(RESOURCE_ROOT_ID + ":" + "EssentiaHatch");
        this.setHarvestLevel("wrench", 2);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureMachine);
        GameRegistry.registerBlock(this, ItemBlockEssentiaHatch.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityEssentiaHatch.class, "EssentiaHatchTileEntity");
        GregTechAPI.registerMachineBlock(this, -1);
        GTNLItemList.EssentiaHatch.set(new ItemStack(this, 1));
    }

    @Override
    public void onBlockAdded(World aWorld, int aX, int aY, int aZ) {
        if (GregTechAPI.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTechAPI.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }

    @Override
    public void breakBlock(World aWorld, int aX, int aY, int aZ, Block aBlock, int aMetaData) {
        if (GregTechAPI.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTechAPI.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
        aWorld.removeTileEntity(aX, aY, aZ);
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
        float par8, float par9) {
        if (world.isRemote) return false;
        var tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityEssentiaHatch essentiaHatch)) return false;
        var tItemStack = player.getHeldItem();
        if (tItemStack == null) {
            GTUtility.sendChatToPlayer(player, StatCollector.translateToLocal("Info_EssentiaHatch_01"));
            return false;
        }
        var tItem = tItemStack.getItem();
        if (!(tItem instanceof IEssentiaContainerItem essItem)) return false;
        var heldAspects = essItem.getAspects(player.getHeldItem());
        if (heldAspects == null || heldAspects.size() <= 0) {
            return false;
        }

        var tLocked = heldAspects.getAspects()[0];
        essentiaHatch.setLockedAspect(tLocked);

        GTUtility.sendChatToPlayer(
            player,
            StatCollector.translateToLocalFormatted("Info_EssentiaHatch_00", tLocked.getLocalizedDescription()));
        world.markBlockForUpdate(x, y, z);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityEssentiaHatch();
    }
}
