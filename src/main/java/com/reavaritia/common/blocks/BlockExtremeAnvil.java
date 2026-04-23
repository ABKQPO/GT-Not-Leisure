package com.reavaritia.common.blocks;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;
import com.reavaritia.ClientProxy;
import com.reavaritia.ReAvaritia;
import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.common.blocks.item.ItemBlockExtrumeAnvil;
import com.reavaritia.common.blocks.tile.TileEntityExtremeAnvil;
import com.reavaritia.common.entity.EntityExtremeAnvil;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockExtremeAnvil extends BlockContainer {

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

    public void isFalling(World world, int x, int y, int z, int meta) {
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
        int i1 = world.getBlockMetadata(x, y, z) & 4;

        switch (direction) {
            case 0:
                world.setBlockMetadataWithNotify(x, y, z, 2 | i1, 2);
                break;
            case 1:
                world.setBlockMetadataWithNotify(x, y, z, 1 | i1, 2);
                break;
            case 2:
                world.setBlockMetadataWithNotify(x, y, z, 3 | i1, 2);
                break;
            case 3:
                world.setBlockMetadataWithNotify(x, y, z, i1, 2);
                break;
        }
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
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityExtremeAnvil();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        checkAndFall(world, x, y, z);
    }

    public boolean hasOreTag(ItemStack stack) {
        if (ItemUtil.isStackInvalid(stack)) return false;

        int[] oreIDs = OreDictionary.getOreIDs(stack);
        for (int id : oreIDs) {
            if (OreDictionary.getOreName(id)
                .equalsIgnoreCase(MainConfig.re_avaritia.unbreakOre)) {
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

    public void checkAndFall(World world, int x, int y, int z) {
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
            if (!hasOreTag(foundationStack)) {
                world.func_147480_a(x, y - 1, z, true);
            }
        }
    }

    public boolean shouldFall(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        return below.isAir(world, x, y - 1, z) || below.getMaterial()
            .isLiquid() || below == Blocks.fire;
    }

    public void startFalling(World world, int x, int y, int z) {
        if (canFallInto(world, x, y - 1, z) && y >= 0) {
            int meta = world.getBlockMetadata(x, y, z);

            EntityExtremeAnvil entity = new EntityExtremeAnvil(world, x + 0.5D, y + 0.5D, z + 0.5D, meta);
            world.spawnEntityInWorld(entity);
        }
    }

    public boolean canFallInto(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block.isAir(world, x, y, z) || block == Blocks.fire
            || block.getMaterial()
                .isLiquid();
    }
}
