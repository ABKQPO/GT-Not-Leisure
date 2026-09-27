/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Decorative placeable rotating pig doll ("Pigmee Fumo").
 *
 * <p>
 * The world geometry is drawn exclusively by the bound TileEntitySpecialRenderer; the block itself reports
 * {@code -1} from {@link #getRenderType()} so the chunk renderer never emits a duplicate full cube.
 * Four horizontal facings are persisted in block metadata, and the spin toggle lives on
 * {@link TileEntityPigmeeFumo}.
 */
public class BlockPigmeeFumo extends BlockContainer {

    /** Local bounding boxes per metadata, in 1/16 units: minX, minY, minZ, maxX, maxY, maxZ. */
    private static final double[][] BOUNDS = {
        // NORTH (metadata 2)
        { 3.9, 0, 4.0, 12.2, 13.6, 14.7 },
        // SOUTH (metadata 3)
        { 3.8, 0, 1.3, 12.1, 13.6, 12.0 },
        // WEST (metadata 4)
        { 4.0, 0, 3.8, 14.7, 13.6, 12.2 },
        // EAST (metadata 5)
        { 1.3, 0, 3.8, 12.0, 13.6, 12.2 } };

    public BlockPigmeeFumo() {
        super(Material.cloth);
        setHardness(0.5F);
        setStepSound(Block.soundTypeCloth);
        setLightOpacity(2);
        setBlockName("gtnl.pigmee_fumo");
        setBlockTextureName(RESOURCE_ROOT_ID + ":blocks/pigmee_fumo");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockPigmeeFumo.class, "pigmee_fumo");
        GameRegistry.registerTileEntity(TileEntityPigmeeFumo.class, "pigmee_fumo_tile_entity");
        GTNLItemList.PigmeeFumo.set(new ItemStack(this, 1));
    }

    /** @return fixed language prefix {@code gtnl.block.pigmee_fumo}; no {@code .name} suffix. */
    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.pigmee_fumo";
    }

    /** @return false; the model must not hide complete neighbour faces. */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /** @return false; the block does not use the vanilla full-cube appearance. */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /** @return -1; world geometry is emitted only by the bound TileEntitySpecialRenderer. */
    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPigmeeFumo();
    }

    /**
     * Normalises the persisted metadata into a horizontal facing.
     *
     * @param metadata persisted facing value
     * @return 2/3/4/5 as NORTH/SOUTH/WEST/EAST, any other value as {@link ForgeDirection#NORTH}; never throws
     */
    public static ForgeDirection facingFromMetadata(int metadata) {
        return switch (metadata) {
            case 2 -> ForgeDirection.NORTH;
            case 3 -> ForgeDirection.SOUTH;
            case 4 -> ForgeDirection.WEST;
            case 5 -> ForgeDirection.EAST;
            default -> ForgeDirection.NORTH;
        };
    }

    /**
     * Reads the per-facing local bounding box.
     *
     * @param metadata persisted facing value
     * @return fresh 6-element array in 1/16 units; callers never share a mutable instance
     */
    private static double[] boundsFor(int metadata) {
        int index = switch (metadata) {
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };
        return BOUNDS[index].clone();
    }

    /**
     * Faces the doll away from the placer.
     *
     * @param world  placement world
     * @param x      block X
     * @param y      block Y
     * @param z      block Z
     * @param placer placer, may be null
     * @param stack  placed stack; no item NBT is copied
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        int metadata = 2;
        if (placer != null) {
            int quad = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            metadata = switch (quad) {
                case 0 -> 2;
                case 1 -> 5;
                case 2 -> 3;
                default -> 4;
            };
        }
        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
    }

    /**
     * Hands the right-click to the tile's server-authoritative toggle.
     *
     * @param world  interaction world
     * @param x      block X
     * @param y      block Y
     * @param z      block Z
     * @param player interacting player
     * @param side   clicked face
     * @param hitX   local X
     * @param hitY   local Y
     * @param hitZ   local Z
     * @return true when a target tile exists; exactly one server-side state flip happens
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPigmeeFumo fumo)) {
            return false;
        }
        if (!world.isRemote) {
            fumo.toggleSpinningServer();
        }
        return true;
    }

    /**
     * Refreshes the queried block's bounds so successive queries cannot inherit stale singleton bounds.
     *
     * @param world read-only world
     * @param x     block X
     * @param y     block Y
     * @param z     block Z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        double[] b = boundsFor(world.getBlockMetadata(x, y, z));
        setBlockBounds(
            (float) b[0] / 16.0F,
            (float) b[1] / 16.0F,
            (float) b[2] / 16.0F,
            (float) b[3] / 16.0F,
            (float) b[4] / 16.0F,
            (float) b[5] / 16.0F);
    }

    /**
     * @param world owning world
     * @param x     block X
     * @param y     block Y
     * @param z     block Z
     * @return the metadata bounding box in world space; never null and independent of the spin angle
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        double[] b = boundsFor(world.getBlockMetadata(x, y, z));
        return AxisAlignedBB.getBoundingBox(
            x + b[0] / 16.0D,
            y + b[1] / 16.0D,
            z + b[2] / 16.0D,
            x + b[3] / 16.0D,
            y + b[4] / 16.0D,
            z + b[5] / 16.0D);
    }

    /**
     * @param world owning world
     * @param x     block X
     * @param y     block Y
     * @param z     block Z
     * @return the selection outline, identical in extent to the collision box
     */
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
