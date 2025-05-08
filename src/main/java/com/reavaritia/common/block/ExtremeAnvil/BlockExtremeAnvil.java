package com.reavaritia.common.block.ExtremeAnvil;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.reavaritia.ClientProxy;
import com.reavaritia.ReAvaCreativeTabs;
import com.reavaritia.ReAvaItemList;
import com.reavaritia.ReAvaritia;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockExtremeAnvil extends BlockFalling {

    public BlockExtremeAnvil() {
        super(Material.anvil);
        setBlockName("ExtremeAnvil");
        setBlockTextureName(RESOURCE_ROOT_ID + ":" + "ExtremeAnvil");
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        setStepSound(Block.soundTypeAnvil);
        setHardness(10.0F);
        setResistance(2000.0F);
        GameRegistry.registerBlock(this, ItemBlockExtrumeAnvil.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityExtremeAnvil.class, "ExtremeAnvilTileEntity");
        ReAvaItemList.ExtremeAnvil.set(new ItemStack(this, 1));
    }

    private IIcon Icon;

    @Override
    public void func_149828_a(World world, int x, int y, int z, int meta) {
        world.setBlock(x, y, z, this, meta, 3);
        world.playSoundEffect(
            x + 0.5D,
            y + 0.5D,
            z + 0.5D,
            "random.anvil_land",
            0.5F,
            world.rand.nextFloat() * 0.1F + 0.9F);

    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int direction = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, direction, 2);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return ClientProxy.extremeAnvilRenderType;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 3;
        float minX = 0.125F, maxX = 0.875F;
        float minZ = 0.25F, maxZ = 0.75F;

        if (meta == 0 || meta == 2) {
            this.setBlockBounds(minX, 0.0F, minZ, maxX, 1.0F, maxZ);
        } else {
            this.setBlockBounds(minZ, 0.0F, minX, maxZ, 1.0F, maxX);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(ReAvaritia.instance, 1, world, x, y, z);
        }
        return true;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Icon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.Icon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "ExtremeAnvil");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityExtremeAnvil();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            this.harvesters.remove();
            super.breakBlock(world, x, y, z, block, meta);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        checkAndFall(world, x, y, z);
    }

    private boolean hasOreTag(ItemStack stack, String tag) {
        if (stack == null || stack.getItem() == null) return false;

        int[] oreIDs = OreDictionary.getOreIDs(stack);
        for (int id : oreIDs) {
            if (OreDictionary.getOreName(id)
                .equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (!world.isRemote) {
            this.checkAndFall(world, x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!world.isRemote) {
            this.checkAndFall(world, x, y, z);
        }
    }

    private void checkAndFall(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != this) return;

        Block below = world.getBlock(x, y - 1, z);

        if (below instanceof BlockExtremeAnvil) return;

        Block foundationBlock = world.getBlock(x, y - 1, z);

        if (foundationBlock.isAir(world, x, y - 1, z)) {
            if (shouldFall(world, x, y, z)) {
                startFalling(world, x, y, z);
            }
        } else {
            ItemStack foundationStack = new ItemStack(foundationBlock, 1, world.getBlockMetadata(x, y - 1, z));
            if (!hasOreTag(foundationStack, "neutronUnbreak")) {
                world.func_147480_a(x, y - 1, z, true);
            }
        }
    }

    private boolean shouldFall(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        return below.isAir(world, x, y - 1, z) || below.getMaterial()
            .isLiquid() || below == Blocks.fire;
    }

    private void startFalling(World world, int x, int y, int z) {
        if (canFallInto(world, x, y - 1, z) && y >= 0) {
            int meta = world.getBlockMetadata(x, y, z);

            EntityExtremeAnvil entity = new EntityExtremeAnvil(world, x + 0.5D, y + 0.5D, z + 0.5D, this, meta);
            world.spawnEntityInWorld(entity);
        }
    }

    private boolean canFallInto(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block.isAir(world, x, y, z) || block == Blocks.fire
            || block.getMaterial()
                .isLiquid();
    }
}
