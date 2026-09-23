package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.DirectionBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.DirectionBlockProperty.AbstractDirectionBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockSearedLadder;
import com.science.gtnl.common.block.blocks.tile.TileEntitySearedLadder;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.iface.IMasterLogic;
import mantle.blocks.iface.IServantLogic;

public class BlockSearedLadder extends Block {

    private static final DirectionBlockProperty FACING = new AbstractDirectionBlockProperty("facing") {

        @Override
        public int getMeta(ForgeDirection value, int existing) {
            return isValidDirection(value) ? value.ordinal() : ForgeDirection.NORTH.ordinal();
        }

        @Override
        public ForgeDirection getValue(int meta) {
            return getFacing(meta);
        }

        @Override
        public boolean isValidDirection(ForgeDirection value) {
            return value != null && value.ordinal() >= 2 && value.ordinal() <= 5;
        }

        @Override
        public void setValue(World world, int x, int y, int z, ForgeDirection value) {
            int metadata = getMeta(value, 0);
            if (world.getBlockMetadata(x, y, z) == metadata) return;
            notifyMaster(world, x, y, z);
            world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
        }
    };

    private static final BooleanBlockProperty BOTTOM = new BooleanBlockProperty() {

        @Override
        public String getName() {
            return "bottom";
        }

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return trait == BlockPropertyTrait.SupportsWorld;
        }

        @Override
        public Boolean getValue(IBlockAccess world, int x, int y, int z) {
            return hasBottom(world, x, y, z);
        }
    };

    private static final AxisAlignedBB[][] COLLISION_SHAPES = createShapes(false);
    private static final AxisAlignedBB[][] SELECTION_SHAPES = createShapes(true);

    public IIcon ladderIcon;

    public BlockSearedLadder() {
        super(Material.rock);
        setHardness(3F);
        setResistance(20F);
        setStepSound(soundTypeMetal);
        this.setBlockName("gtnl.seared_ladder");
        this.setBlockTextureName(RESOURCE_ROOT_ID + ":seared_ladder/fancy_bricks");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockSearedLadder.class, "seared_ladder");
        BlockPropertyRegistry.registerBlockItemProperty(this, FACING, ForgeDirection.NORTH);
        BlockPropertyRegistry.registerBlockItemProperty(this, BOTTOM, false);
        GameRegistry.registerTileEntity(TileEntitySearedLadder.class, "seared_ladder_tile_entity");
        GTNLItemList.SearedLadder.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.seared_ladder";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == getFacing(meta).ordinal() ? ladderIcon : blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        this.ladderIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":seared_ladder/ladder");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntitySearedLadder();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        // The broad-phase box encloses the compound collision shape below.
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> boxes,
        Entity entity) {
        for (AxisAlignedBB box : COLLISION_SHAPES[getShapeIndex(world, x, y, z)]) {
            if (mask.maxX > x + box.minX && mask.minX < x + box.maxX
                && mask.maxY > y + box.minY
                && mask.minY < y + box.maxY
                && mask.maxZ > z + box.minZ
                && mask.minZ < z + box.maxZ) {
                boxes.add(box.getOffsetBoundingBox(x, y, z));
            }
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        Vec3 localStart = start.addVector(-x, -y, -z);
        Vec3 localEnd = end.addVector(-x, -y, -z);
        MovingObjectPosition closest = null;
        double distance = Double.POSITIVE_INFINITY;
        for (AxisAlignedBB box : SELECTION_SHAPES[getShapeIndex(world, x, y, z)]) {
            MovingObjectPosition hit = box.calculateIntercept(localStart, localEnd);
            if (hit == null) continue;
            double hitDistance = localStart.squareDistanceTo(hit.hitVec);
            if (hitDistance < distance) {
                closest = hit;
                distance = hitDistance;
            }
        }
        return closest == null ? null
            : new MovingObjectPosition(x, y, z, closest.sideHit, closest.hitVec.addVector(x, y, z));
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.updateLadderBounds(world.getBlockMetadata(x, y, z));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    public void updateLadderBounds(int metadata) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemIn) {
        int direction = 0;
        if (player != null) {
            direction = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        }
        int metadata = switch (direction) {
            case 0 -> 2;
            case 1 -> 5;
            case 2 -> 3;
            case 3 -> 4;
            default -> 2;
        };

        FACING.setValue(world, x, y, z, getFacing(metadata));
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.isRemote) return;
        FACING.setValue(world, x, y, z, getFacing(world.getBlockMetadata(x, y, z)));
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            notifyMaster(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        world.markBlockRangeForRenderUpdate(x, y, z, x, y + 1, z);
        notifyMaster(world, x, y, z);
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
        notifyMaster(world, x, y, z);
        super.onBlockPreDestroy(world, x, y, z, metadata);
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        if (axis != ForgeDirection.UP && axis != ForgeDirection.DOWN) return false;
        FACING.setValue(world, x, y, z, getFacing(world.getBlockMetadata(x, y, z)).getRotation(axis));
        return true;
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
        return new ForgeDirection[] { ForgeDirection.UP, ForgeDirection.DOWN };
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return switch (side) {
            case DOWN -> hasBottom(world, x, y, z);
            case NORTH, SOUTH, WEST, EAST -> side != getFacing(world.getBlockMetadata(x, y, z));
            default -> false;
        };
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }

    private static void notifyMaster(World world, int x, int y, int z) {
        if (world.isRemote) return;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IServantLogic servant) {
            servant.notifyMasterOfChange();
        } else if (tile instanceof IMasterLogic master) {
            master.notifyChange(null, x, y, z);
        }
    }

    private static ForgeDirection getFacing(int metadata) {
        return metadata >= 2 && metadata <= 5 ? ForgeDirection.getOrientation(metadata) : ForgeDirection.NORTH;
    }

    private static boolean hasBottom(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z) != world.getBlock(x, y, z)
            || getFacing(world.getBlockMetadata(x, y - 1, z)) != getFacing(world.getBlockMetadata(x, y, z));
    }

    private static int getShapeIndex(IBlockAccess world, int x, int y, int z) {
        return (getFacing(world.getBlockMetadata(x, y, z)).ordinal() - 2) * 2 + (hasBottom(world, x, y, z) ? 1 : 0);
    }

    private static AxisAlignedBB[][] createShapes(boolean selection) {
        // The recess is deeper for collision so the player's center can enter the ladder block.
        int count = selection ? 7 : 3;
        int depth = selection ? 3 : 5;
        AxisAlignedBB[] north = new AxisAlignedBB[count + 1];
        north[0] = box(0, 0, depth, 16, 16, 16);
        north[1] = box(0, 0, 0, 2, 16, depth);
        north[2] = box(14, 0, 0, 16, 16, depth);
        if (selection) {
            for (int rung = 0; rung < 4; rung++) {
                north[3 + rung] = box(2, 2 + rung * 4, 2, 14, 4 + rung * 4, 3);
            }
        }
        north[count] = box(2, 0, 0, 14, 2, depth);

        AxisAlignedBB[][] shapes = new AxisAlignedBB[8][];
        for (int metadata = 2; metadata <= 5; metadata++) {
            for (int bottom = 0; bottom < 2; bottom++) {
                AxisAlignedBB[] boxes = new AxisAlignedBB[count + bottom];
                for (int i = 0; i < boxes.length; i++) {
                    boxes[i] = rotateBox(north[i], getFacing(metadata));
                }
                shapes[(metadata - 2) * 2 + bottom] = boxes;
            }
        }
        return shapes;
    }

    private static AxisAlignedBB box(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return AxisAlignedBB
            .getBoundingBox(minX / 16.0, minY / 16.0, minZ / 16.0, maxX / 16.0, maxY / 16.0, maxZ / 16.0);
    }

    private static AxisAlignedBB rotateBox(AxisAlignedBB box, ForgeDirection facing) {
        return switch (facing) {
            case SOUTH -> AxisAlignedBB
                .getBoundingBox(1 - box.maxX, box.minY, 1 - box.maxZ, 1 - box.minX, box.maxY, 1 - box.minZ);
            case WEST -> AxisAlignedBB
                .getBoundingBox(box.minZ, box.minY, 1 - box.maxX, box.maxZ, box.maxY, 1 - box.minX);
            case EAST -> AxisAlignedBB
                .getBoundingBox(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
            default -> box;
        };
    }
}
