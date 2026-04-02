package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.render.PlayerDollRenderManager.fetchUUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockPlayerDoll;
import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPlayerDoll extends BlockContainer {

    public BlockPlayerDoll() {
        super(Material.iron);
        this.setResistance(99999999f);
        this.setHardness(5f);
        this.setBlockName("PlayerDoll");
        this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 1.0F, 0.9F);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisurePlayerDoll);
        this.setLightLevel(1f);
        this.setStepSound(soundTypeGlass);
        GameRegistry.registerBlock(this, ItemBlockPlayerDoll.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityPlayerDoll.class, "PlayerDollTileEntity");
        GTNLItemList.PlayerDoll.set(new ItemStack(this, 1));
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
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPlayerDoll();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "air");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityPlayerDoll tileEntityPlayerDoll) {
            if (itemstack.hasTagCompound()) {
                NBTTagCompound nbt = itemstack.getTagCompound();

                if (nbt.hasKey("SkinHttp", 8)) {
                    String skinHttp = nbt.getString("SkinHttp");
                    tileEntityPlayerDoll.setSkinHttp(skinHttp);
                }

                if (nbt.hasKey("CapeHttp", 8)) {
                    String capeHttp = nbt.getString("CapeHttp");
                    tileEntityPlayerDoll.setCapeHttp(capeHttp);
                }

                if (nbt.hasKey("RenderCapeMode", 1)) {
                    byte renderCapeMode = nbt.getByte("RenderCapeMode");
                    tileEntityPlayerDoll.setRenderCapeMode(renderCapeMode);
                }

                if (nbt.hasKey("SkullOwner", 8)) {
                    String playerName = nbt.getString("SkullOwner");
                    tileEntityPlayerDoll.setOwner(playerName);
                    String uuid = fetchUUID(playerName);
                    if (!StringUtils.isNullOrEmpty(uuid)) {
                        nbt.setString("OwnerUUID", uuid);
                        tileEntityPlayerDoll.setOwnerUUID(uuid);
                    }
                } else {
                    String playerName = player.getCommandSenderName();
                    tileEntityPlayerDoll.setOwner(playerName);
                    String uuid = fetchUUID(playerName);
                    if (!StringUtils.isNullOrEmpty(uuid)) {
                        nbt.setString("OwnerUUID", uuid);
                        tileEntityPlayerDoll.setOwnerUUID(uuid);
                    }
                }

                if (nbt.hasKey("OwnerUUID", 8)) {
                    String uuid = nbt.getString("OwnerUUID");
                    tileEntityPlayerDoll.setOwnerUUID(uuid);
                } else {
                    String playerName = player.getCommandSenderName();
                    tileEntityPlayerDoll.setOwner(playerName);
                    String uuid = fetchUUID(playerName);
                    if (!StringUtils.isNullOrEmpty(uuid)) {
                        nbt.setString("OwnerUUID", uuid);
                        tileEntityPlayerDoll.setOwnerUUID(uuid);
                    }
                }
            } else if (player != null) {
                String playerName = player.getCommandSenderName();
                tileEntityPlayerDoll.setOwner(playerName);
                String uuid = fetchUUID(playerName);
                if (!StringUtils.isNullOrEmpty(uuid)) {
                    tileEntityPlayerDoll.setOwnerUUID(uuid);
                }
            }
        }

        int direction = 0;
        if (player != null) {
            direction = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        }
        int metadata = switch (direction) {
            case 0 -> 2; // 南
            case 1 -> 5; // 西
            case 2 -> 3; // 北
            case 3 -> 4;
            default -> 0; // 东
        };

        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityPlayerDoll tileEntityPlayerDoll) {
            ItemStack drop = new ItemStack(this);
            NBTTagCompound nbt = new NBTTagCompound();

            if (tileEntityPlayerDoll.hasOwner()) {
                nbt.setString("SkullOwner", tileEntityPlayerDoll.getSkullOwner());

            }

            if (tileEntityPlayerDoll.hasOwnerUUID()) {
                nbt.setString("OwnerUUID", tileEntityPlayerDoll.getOwnerUUID());
            }

            if (tileEntityPlayerDoll.hasSkinHttp()) {
                nbt.setString("SkinHttp", tileEntityPlayerDoll.getSkinHttp());
            }

            if (tileEntityPlayerDoll.hasCapeHttp()) {
                nbt.setString("CapeHttp", tileEntityPlayerDoll.getCapeHttp());
            }
            nbt.setByte("RenderCapeMode", tileEntityPlayerDoll.getRenderCapeMode());

            if (!nbt.hasNoTags()) {
                drop.setTagCompound(nbt);
            }

            EntityItem itemEntity = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop);
            world.spawnEntityInWorld(itemEntity);
        } else {
            ItemStack drop = new ItemStack(this);
            EntityItem itemEntity = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop);
            world.spawnEntityInWorld(itemEntity);
        }

        super.breakBlock(world, x, y, z, block, meta);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {}

    @Override
    public void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemStack) {}

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }
}
