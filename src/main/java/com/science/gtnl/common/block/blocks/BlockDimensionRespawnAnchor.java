package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.api.mixinHelper.IAnchorRespawn;
import com.science.gtnl.common.block.blocks.item.ItemBlockDimensionRespawnAnchor;
import com.science.gtnl.common.block.blocks.tile.TileEntityDimensionRespawnAnchor;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDimensionRespawnAnchor extends BlockContainer {

    public IIcon[] icons = new IIcon[8];

    public BlockDimensionRespawnAnchor() {
        super(Material.rock);
        this.setBlockName("DimensionRespawnAnchor");
        this.setHardness(50.0F);
        this.setResistance(2000.0F);
        this.setStepSound(soundTypeStone);
        this.setHarvestLevel("pickaxe", 3);
        this.setLightOpacity(0);
        this.setTickRandomly(true);
        GameRegistry.registerBlock(this, ItemBlockDimensionRespawnAnchor.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityDimensionRespawnAnchor.class, "DimensionRespawnAnchorTileEntity");
        GTNLItemList.DimensionRespawnAnchor.set(new ItemStack(this, 1));
        BlockDispenser.dispenseBehaviorRegistry
            .putObject(Item.getItemFromBlock(Blocks.glowstone), new BehaviorGlowstoneCharge());
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons[0] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorTopOff");
        icons[1] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorTop");
        icons[2] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorSide");
        icons[3] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorSide1");
        icons[4] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorSide2");
        icons[5] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorSide3");
        icons[6] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorSide4");
        icons[7] = register.registerIcon(RESOURCE_ROOT_ID + ":" + "DimensionRespawnAnchorBottom");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        int energy = 0;

        if (te instanceof TileEntityDimensionRespawnAnchor anchor) {
            energy = anchor.getEnergyLevel();
        }

        if (side == 0) {
            return icons[7];
        }

        if (side == 1) {
            return energy == 0 ? icons[0] : icons[1];
        }

        return switch (energy) {
            case 1 -> icons[3];
            case 2 -> icons[4];
            case 3 -> icons[5];
            case 4 -> icons[6];
            default -> icons[2];
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0) {
            return icons[7];
        }

        if (side == 1) {
            return icons[0];
        }
        return icons[2];
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDimensionRespawnAnchor();
    }

    @Override
    public int getMobilityFlag() {
        return 2;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntityDimensionRespawnAnchor te = (TileEntityDimensionRespawnAnchor) world.getTileEntity(x, y, z);
        if (te == null) return 0;
        return Math.max(0, te.getEnergyLevel() * 4 - 1);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntityDimensionRespawnAnchor te = (TileEntityDimensionRespawnAnchor) world.getTileEntity(x, y, z);
        if (te == null) return 0;

        return switch (te.getEnergyLevel()) {
            case 1 -> 3;
            case 2 -> 7;
            case 3 -> 11;
            case 4 -> 15;
            default -> 0;
        };
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntityDimensionRespawnAnchor te = (TileEntityDimensionRespawnAnchor) world.getTileEntity(x, y, z);
        if (te == null) return false;

        ItemStack held = player.getHeldItem();

        if (held != null && held.getItem() == Item.getItemFromBlock(Blocks.glowstone)) {

            if (te.getEnergyLevel() < 4) {
                te.addEnergy(1);
                world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    RESOURCE_ROOT_ID + ":respawn_anchor.charge" + (1 + world.rand.nextInt(3)),
                    1.0F,
                    0.8F + world.rand.nextFloat() * 0.4F);

                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                }

                world.markBlockForUpdate(x, y, z);
                return true;
            }
            return true;
        }

        if (te.getEnergyLevel() > 0) {
            IAnchorRespawn anchor = (IAnchorRespawn) player;
            anchor.setAnchorRespawn(world.provider.dimensionId, x, y + 1, z);

            player.addChatMessage(new ChatComponentTranslation("Info_DimensionRespawnAnchor_SetSpawn"));

            world.playSoundEffect(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                RESOURCE_ROOT_ID + ":respawn_anchor.set_spawn" + (1 + world.rand.nextInt(3)),
                1.0F,
                0.8F + world.rand.nextFloat() * 0.4F);

            return true;
        }

        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityDragon) {
            return false;
        }
        return super.canEntityDestroy(world, x, y, z, entity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileEntityDimensionRespawnAnchor te = (TileEntityDimensionRespawnAnchor) world.getTileEntity(x, y, z);
        if (te == null || te.getEnergyLevel() == 0) return;

        world.spawnParticle("portal", x + 0.5, y + 1.0, z + 0.5, 0, 0.1, 0);

        if (rand.nextInt(80) == 0) {

            world.playSound(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                RESOURCE_ROOT_ID + ":respawn_anchor.ambient" + (1 + world.rand.nextInt(3)),
                1.0F,
                0.8F + rand.nextFloat() * 0.4F,
                false);
        }
    }

    public static ChunkCoordinates verifyAnchorRespawnCoordinates(World world, ChunkCoordinates anchorPos,
        boolean force) {
        IChunkProvider chunkProvider = world.getChunkProvider();

        chunkProvider.loadChunk((anchorPos.posX - 3) >> 4, (anchorPos.posZ - 3) >> 4);
        chunkProvider.loadChunk((anchorPos.posX + 3) >> 4, (anchorPos.posZ - 3) >> 4);
        chunkProvider.loadChunk((anchorPos.posX - 3) >> 4, (anchorPos.posZ + 3) >> 4);
        chunkProvider.loadChunk((anchorPos.posX + 3) >> 4, (anchorPos.posZ + 3) >> 4);

        return findSafeAnchorRespawn(world, anchorPos.posX, anchorPos.posY - 1, anchorPos.posZ, 10);
    }

    public static ChunkCoordinates findSafeAnchorRespawn(World world, int anchorX, int anchorY, int anchorZ,
        int safeIndex) {
        for (int yOffset = 0; yOffset <= 10; yOffset++) {
            for (int y : new int[] { anchorY + yOffset, anchorY - yOffset }) {
                for (int xOffset = 0; xOffset <= 10; xOffset++) {
                    for (int zOffset = 0; zOffset <= 10; zOffset++) {
                        for (int xSign : new int[] { 1, -1 }) {
                            for (int zSign : new int[] { 1, -1 }) {
                                int x = anchorX + xOffset * xSign;
                                int z = anchorZ + zOffset * zSign;
                                if (World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && !world.getBlock(x, y, z)
                                    .getMaterial()
                                    .isOpaque()
                                    && !world.getBlock(x, y + 1, z)
                                        .getMaterial()
                                        .isOpaque()) {

                                    if (safeIndex <= 0) {
                                        return new ChunkCoordinates(x, y, z);
                                    }
                                    safeIndex--;
                                }
                            }
                        }
                    }
                }
            }
        }

        return new ChunkCoordinates(anchorX, anchorY + 1, anchorZ);
    }

}
