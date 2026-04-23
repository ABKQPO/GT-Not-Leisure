package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.ClientProxy;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockEnderElevator;
import com.science.gtnl.common.block.blocks.tile.TileEntityEnderElevator;
import com.science.gtnl.utils.event.LivingSneakEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnderElevator extends BlockContainer {

    public IIcon overlayIcon;

    public int type;

    public static final int[] COLOR_TABLE = new int[] { 0xFFFFFF, 0xD87F33, 0xB24CA5, 0x6699D8, 0xE5E533, 0x7FCC19,
        0xF27FA5, 0x4C4C4C, 0x999999, 0x4C7F99, 0x7F3FB2, 0x334CB2, 0x664C33, 0x667F33, 0x993333, 0x191919 };

    public BlockEnderElevator(int type) {
        super(Material.iron);
        this.type = type;
        this.setHardness(3.0f);
        String suffix = switch (type) {
            case 1 -> "Slab";
            case 2 -> "Carpet";
            default -> "Block";
        };
        this.setBlockName("EnderElevator" + suffix);
        this.setBlockTextureName(RESOURCE_ROOT_ID + ":EnderElevator");
        this.setLightLevel(1.0f);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockEnderElevator.class, getUnlocalizedName());
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        if (type == 1) setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        else if (type == 2) setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        else setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        overlayIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":EnderElevatorOverlay");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityEnderElevator();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return ClientProxy.enderElevatorRenderID;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntityEnderElevator te = (TileEntityEnderElevator) world.getTileEntity(x, y, z);
        if (te == null) return false;

        ItemStack held = player.getHeldItem();

        if (player.isSneaking() && held == null) {
            te.clearDisguise();
            world.markBlockForUpdate(x, y, z);
            return true;
        }

        if (held != null) {
            if (held.getItem() instanceof ItemDye) {
                if (!world.isRemote) {
                    world.setBlockMetadataWithNotify(x, y, z, 15 - held.getItemDamage(), 3);
                }
                return true;
            }

            Block heldBlock = Block.getBlockFromItem(held.getItem());
            if (heldBlock != null) {
                if (heldBlock instanceof BlockEnderElevator) {
                    return false;
                }

                if (heldBlock.getRenderType() == -1) {
                    return false;
                }

                if (!world.isRemote) {
                    te.setDisguise(heldBlock, held.getItemDamage());
                    world.markBlockForUpdate(x, y, z);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 0 && meta < COLOR_TABLE.length) {
            return COLOR_TABLE[meta];
        }
        return 0xFFFFFF;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderColor(int meta) {
        if (meta >= 0 && meta < COLOR_TABLE.length) {
            return COLOR_TABLE[meta];
        }
        return 0xFFFFFF;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingJumpEvent event) {
        EntityLivingBase entity = event.entityLiving;
        handleElevatorDetection(entity, true);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingSneakEvent event) {
        if (!event.isSneaking()) return;
        EntityLivingBase entity = event.entityLiving;
        handleElevatorDetection(entity, false);
    }

    public static void handleElevatorDetection(EntityLivingBase entity, boolean up) {
        World world = entity.worldObj;
        int x = (int) Math.floor(entity.posX);
        int z = (int) Math.floor(entity.posZ);

        int y1 = (int) entity.posY;
        int y2 = (int) entity.posY - 1;

        if (tryTeleport(world, x, y1, z, entity, up)) return;
        tryTeleport(world, x, y2, z, entity, up);
    }

    private static boolean tryTeleport(World world, int x, int y, int z, EntityLivingBase entity, boolean up) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockEnderElevator enderElevator) {
            TileEntityEnderElevator te = (TileEntityEnderElevator) world.getTileEntity(x, y, z);
            if (te != null && te.isReady()) {
                enderElevator.teleportAction(world, x, y, z, te, entity, up);
                return true;
            }
        }
        return false;
    }

    public void teleportAction(World world, int x, int y, int z, TileEntityEnderElevator te, Entity entity,
        boolean upHint) {
        if (world.isRemote) return;

        int power = world.getStrongestIndirectPower(x, y, z);
        boolean finalDirection = (power > 0) ? (power < 8) : upHint;
        int color = world.getBlockMetadata(x, y, z);
        BlockPos target = te.findTarget(finalDirection, color, y);

        if (target != null) {
            Block targetBlock = world.getBlock(target.x, target.y, target.z);
            double landingOffset = 1.0;

            if (targetBlock instanceof BlockEnderElevator targetElevator) {
                if (targetElevator.type == 1) {
                    landingOffset = 0.6;
                } else if (targetElevator.type == 2) {
                    landingOffset = 0.3;
                }
            }

            double destX = target.x + 0.5;
            double destY = target.y + landingOffset;
            double destZ = target.z + 0.5;

            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).setPositionAndUpdate(destX, destY, destZ);
                } else {
                    entity.setPosition(destX, destY, destZ);
                }

                te.startCooldown();
                TileEntity targetTile = world.getTileEntity(target.x, target.y, target.z);
                if (targetTile instanceof TileEntityEnderElevator targetTe) {
                    targetTe.startCooldown();
                }

                world.playSoundEffect(destX, destY, destZ, "mob.endermen.portal", 1.0F, 1.0F);
            }
        }
    }
}
