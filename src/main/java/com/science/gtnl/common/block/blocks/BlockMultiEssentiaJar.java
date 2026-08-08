package com.science.gtnl.common.block.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemEssence;

public class BlockMultiEssentiaJar extends BlockJar {

    private static final int PHIAL_AMOUNT = 8;

    public BlockMultiEssentiaJar() {
        super();
        setBlockName("MultiEssentiaJar");
        setHardness(0.3F);
        setResistance(1.0F);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockMultiEssentiaJar.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityMultiEssentiaJar.class, "MultiEssentiaJarTileEntity");
        GTNLItemList.MultiEssentiaJar.set(new ItemStack(this));
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List blocks) {
        blocks.add(new ItemStack(item));
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityMultiEssentiaJar();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityMultiEssentiaJar();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, net.minecraft.entity.EntityLivingBase entity,
        ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityMultiEssentiaJar jar) {
            jar.readFromItemStack(stack);

            // 标签默认贴在南面，放置时按玩家朝向旋转，使标签面朝玩家
            if (entity != null) {
                int playerFacing = MathHelper
                    .floor_double((entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
                // playerFacing: 0=南 1=西 2=北 3=东 -> 对应标签朝向的 facing 值
                jar.setFacing(new int[] { 2, 5, 3, 4 }[playerFacing]);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMultiEssentiaJar jar)) return drops;

        ItemStack drop = new ItemStack(this);
        if (jar.getTotalAmount() > 0 || jar.hasFilterLabel()) {
            jar.writeToItemStack(drop);
        }

        drops.add(drop);
        return drops;
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, x, y, z, metadata, 0);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMultiEssentiaJar jar)) return false;

        ItemStack heldStack = player.getHeldItem();

        if (heldStack == null) {
            if (world.isRemote) return true;

            /*
             * 对准标签正面潜行右击：
             * 移除标签，并且不掉落标签物品。
             */
            if (jar.hasFilterLabel() && player.isSneaking() && side == jar.facing) {
                if (jar.removeFilterLabel()) {
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "thaumcraft:jar", 0.4F, 1.0F);
                }
                return true;
            }

            /*
             * 有标签且有源质：
             * 禁止空手右击打开选择 GUI。
             */
            if (jar.hasFilterLabel() && jar.getTotalAmount() > 0) {
                return true;
            }

            /*
             * 空罐，不论是否已经有标签：
             * 打开过滤标签 GUI。
             */
            if (jar.getStoredTypeCount() <= 0) {
                if (player instanceof EntityPlayerMP playerMP) {
                    GuiFactories.tileEntity()
                        .open(playerMP, x, y, z);
                }
                return true;
            }

            /*
             * 无标签且有源质：
             * 潜行右击切换下一个当前源质。
             */
            if (player.isSneaking()) {
                Aspect previousAspect = jar.getActiveAspect();
                Aspect activeAspect = jar.cycleActiveAspect();

                if (activeAspect != null && activeAspect != previousAspect) {
                    playEssentiaSlosh(world, x, y, z);
                }

                sendActiveAspectStatus(player, jar, activeAspect);
                return true;
            }

            /*
             * 无标签且有源质：
             * 普通右击打开当前源质选择 GUI。
             */
            if (player instanceof EntityPlayerMP playerMP) {
                GuiFactories.tileEntity()
                    .open(playerMP, x, y, z);
            }

            return true;
        }

        // 安瓿交互
        if (heldStack.getItem() != ConfigItems.itemEssence) return false;

        ItemEssence phialItem = (ItemEssence) ConfigItems.itemEssence;

        if (heldStack.getItemDamage() == 0) {
            return fillPhialFromJar(world, x, y, z, player, jar, phialItem);
        }

        return emptyPhialIntoJar(world, x, y, z, player, jar, phialItem, heldStack);
    }

    public static void sendActiveAspectStatus(EntityPlayer player, TileEntityMultiEssentiaJar jar,
        Aspect activeAspect) {

        if (activeAspect == null) {
            player.addChatMessage(
                new ChatComponentTranslation("Info_MultiEssentiaJar_Empty", TileEntityMultiEssentiaJar.MAX_CAPACITY));
            return;
        }

        player.addChatMessage(
            new ChatComponentTranslation(
                "Info_MultiEssentiaJar_Status",
                ItemBlockMultiEssentiaJar
                    .createServerAspectDisplay(player, activeAspect, jar.containerContains(activeAspect)),
                jar.getTotalAmount(),
                TileEntityMultiEssentiaJar.MAX_CAPACITY,
                jar.getStoredTypeCount()));
    }

    private boolean fillPhialFromJar(World world, int x, int y, int z, EntityPlayer player,
        TileEntityMultiEssentiaJar jar, ItemEssence phialItem) {
        if (world.isRemote) {
            player.swingItem();
            return true;
        }

        Aspect extractedAspect = jar.selectAspectWithAmount(PHIAL_AMOUNT);
        if (extractedAspect == null) return true;
        if (!jar.takeFromContainer(extractedAspect, PHIAL_AMOUNT)) return true;

        ItemStack filledPhial = new ItemStack(ConfigItems.itemEssence, 1, 1);
        phialItem.setAspects(filledPhial, new AspectList().add(extractedAspect, PHIAL_AMOUNT));
        exchangeHeldItem(world, x, y, z, player, filledPhial);
        playTransferEffects(world, player);
        return true;
    }

    private boolean emptyPhialIntoJar(World world, int x, int y, int z, EntityPlayer player,
        TileEntityMultiEssentiaJar jar, ItemEssence phialItem, ItemStack heldStack) {
        AspectList phialAspects = phialItem.getAspects(heldStack);
        if (phialAspects == null || phialAspects.size() != 1) return false;

        Aspect insertedAspect = phialAspects.getAspects()[0];
        if (insertedAspect == null || phialAspects.getAmount(insertedAspect) < PHIAL_AMOUNT
            || TileEntityMultiEssentiaJar.MAX_CAPACITY - jar.getTotalAmount() < PHIAL_AMOUNT) {
            return true;
        }
        if (world.isRemote) {
            player.swingItem();
            return true;
        }

        if (jar.addToContainer(insertedAspect, PHIAL_AMOUNT) != 0) return true;

        exchangeHeldItem(world, x, y, z, player, new ItemStack(ConfigItems.itemEssence, 1, 0));
        playTransferEffects(world, player);
        return true;
    }

    private static void exchangeHeldItem(World world, int x, int y, int z, EntityPlayer player, ItemStack replacement) {
        if (player.capabilities.isCreativeMode) return;

        ItemStack heldStack = player.getHeldItem();
        heldStack.stackSize--;
        if (heldStack.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }

        if (!player.inventory.addItemStackToInventory(replacement)) {
            world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, replacement));
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    private static void playTransferEffects(World world, EntityPlayer player) {
        player.swingItem();
        world.playSoundAtEntity(player, "game.neutral.swim", 0.25F, 1.0F);
    }

    public static void playEssentiaSlosh(World world, int x, int y, int z) {
        if (world == null || world.isRemote) return;

        float pitch = 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;

        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.neutral.swim", 0.5F, pitch);
    }

    public static void playEssentiaSlosh(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;

        World world = player.worldObj;
        float pitch = 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;

        world.playSoundAtEntity(player, "game.neutral.swim", 0.5F, pitch);
    }
}
