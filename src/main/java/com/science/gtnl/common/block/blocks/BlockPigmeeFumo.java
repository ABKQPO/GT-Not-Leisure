// Pigmee Fumo port from AE2 Lightning Tech Reborn.
// Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
// License: LGPL-3.0. Model author: TedXenon.
// Original model credit: "Made with Blockbench, made by TedXenon".
// Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.api.IBlockModelProvider;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.OrientationBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.render.model.PigmeeFumoModel;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Decorative placeable rotating pig doll ("Pigmee Fumo").
// World geometry is drawn by the bound TileEntitySpecialRenderer, which owns the per-frame spin and so cannot be
// replaced by a static ISBRH model. The block reports -1 from getRenderType() so the chunk renderer
// never emits a duplicate full cube. The four horizontal facings are persisted in block metadata and mirrored into
// GTNHLib's block property registry, so the model can be resolved by orientation. The spin toggle lives on
// TileEntityPigmeeFumo.
public class BlockPigmeeFumo extends BlockContainer implements IBlockModelProvider {

    // Local bounding boxes per metadata, in 1/16 units: minX, minY, minZ, maxX, maxY, maxZ.
    // Taken from the model's own extents (8 x 10 x 14). Upstream's VoxelShape constants describe an
    // 8.3 x 13.6 x 10.7 box, which swaps the model's depth and height, so the outline stood far taller than the doll
    // and never reached its snout.
    // Only two entries are distinct: the model spans X 4..12 and Z 1..15, both symmetric about the
    // block centre, so a 180 degree turn in Y leaves the box unchanged and the perpendicular facings swap the X and Z
    // extents.
    private static final double[][] BOUNDS = {
        // metadata 2 and 3, snout along Z
        { 4.0, 0, 1.0, 12.0, 10.0, 15.0 },
        { 4.0, 0, 1.0, 12.0, 10.0, 15.0 },
        // metadata 4 and 5, snout along X
        { 1.0, 0, 4.0, 15.0, 10.0, 12.0 },
        { 1.0, 0, 4.0, 15.0, 10.0, 12.0 } };

    // Sprite handed to the particle engine. Null until registerBlockIcons runs.
    @SideOnly(Side.CLIENT)
    private IIcon particleIcon;

    // Mirrors the persisted metadata facing into GTNHLib's block state, so ModelRegistry and
    // ModelISBRH can select the matching baked orientation without a blockstate JSON file.
    private static final OrientationBlockProperty FACING_PROPERTY = new OrientationBlockProperty() {

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return trait == BlockPropertyTrait.SupportsWorld || trait == BlockPropertyTrait.SupportsStacks;
        }

        @Override
        public Orientation getValue(IBlockAccess world, int x, int y, int z) {
            return orientationOf(world.getBlockMetadata(x, y, z));
        }

        @Override
        public Orientation getValue(ItemStack stack) {
            return PigmeeFumoModel.DEFAULT_ORIENTATION;
        }
    };

    public BlockPigmeeFumo() {
        super(Material.cloth);
        setHardness(0.5F);
        setStepSound(Block.soundTypeCloth);
        setLightOpacity(2);
        setBlockName("gtnl.pigmee_fumo");
        setBlockTextureName(RESOURCE_ROOT_ID + ":blocks/pigmee_fumo");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockPigmeeFumo.class, "pigmee_fumo");
        BlockPropertyRegistry.registerBlockItemProperty(this, FACING_PROPERTY);
        GameRegistry.registerTileEntity(TileEntityPigmeeFumo.class, "pigmee_fumo_tile_entity");
        GTNLItemList.PigmeeFumo.set(new ItemStack(this, 1));
    }

    // Returns fixed language prefix gtnl.block.pigmee_fumo; no .name suffix.
    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.pigmee_fumo";
    }

    // Returns false; the model must not hide complete neighbour faces.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    // Returns false; the block does not use the vanilla full-cube appearance.
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    // Returns -1; world geometry is emitted only by the bound TileEntitySpecialRenderer.
    @Override
    public int getRenderType() {
        return -1;
    }

    // Registers the sprite used for break and hit particles.
    // Without it the particle engine samples the doll's own texture, which is a 32x32 atlas of its parts and more than
    // half empty, so particles landed on transparent texels and barely appeared.
    // register: the atlas register for the current pass
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);
        particleIcon = register.registerIcon(RESOURCE_ROOT_ID + ":blocks/pigmee_fumo_particle");
    }

    // side: ignored; the block has no per-face sprite
    // meta: ignored
    // Returns the particle sprite. Nothing else reads this: the block is drawn by its tile renderer and its item form
    // by a custom renderer, so getIcon only feeds the particle engine.
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return particleIcon != null ? particleIcon : blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPigmeeFumo();
    }

    // Serves the baked model to GTNHLib.
    // Declaring IBlockModelProvider is what makes this reachable: ModelRegistry.getBakedModel prefers
    // the provider and only falls back to a blockstates/<name>.json when the block does not implement it.
    // context: GTNHLib's quad context for the block being drawn
    // Returns the baked model for the block's persisted facing, or null while unloaded
    @SideOnly(Side.CLIENT)
    @Override
    public BakedModel getModel(BakedModelQuadContext context) {
        Orientation orientation = context.getBlockState().getPropertyValue(FACING_PROPERTY);
        return PigmeeFumoModel.INSTANCE.get(orientation);
    }

    // Maps persisted metadata onto a GTNHLib orientation.
    // metadata: persisted facing value
    // Returns NORTH_UP for metadata 2, SOUTH_UP for 3, WEST_UP for 4, EAST_UP for 5
    // and NORTH_UP for anything else; never null
    public static Orientation orientationOf(int metadata) {
        return switch (metadata) {
            case 3 -> Orientation.SOUTH_UP;
            case 4 -> Orientation.WEST_UP;
            case 5 -> Orientation.EAST_UP;
            default -> Orientation.NORTH_UP;
        };
    }

    // Reads the per-facing local bounding box.
    // metadata: persisted facing value
    // Returns fresh 6-element array in 1/16 units; callers never share a mutable instance
    private static double[] boundsFor(int metadata) {
        int index = switch (metadata) {
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };
        return BOUNDS[index].clone();
    }

    // Faces the doll away from the placer.
    // world: placement world
    // x: block X
    // y: block Y
    // z: block Z
    // placer: placer, may be null
    // stack: placed stack; no item NBT is copied
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

    // Hands the right-click to the tile's server-authoritative toggle.
    // world: interaction world
    // x: block X
    // y: block Y
    // z: block Z
    // player: interacting player
    // side: clicked face
    // hitX: local X
    // hitY: local Y
    // hitZ: local Z
    // Returns true when a target tile exists; exactly one server-side state flip happens
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

    // Refreshes the queried block's bounds so successive queries cannot inherit stale singleton bounds.
    // world: read-only world
    // x: block X
    // y: block Y
    // z: block Z
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

    // world: owning world
    // x: block X
    // y: block Y
    // z: block Z
    // Returns the metadata bounding box in world space; never null and independent of the spin angle
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

    // world: owning world
    // x: block X
    // y: block Y
    // z: block Z
    // Returns the selection outline, identical in extent to the collision box
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
