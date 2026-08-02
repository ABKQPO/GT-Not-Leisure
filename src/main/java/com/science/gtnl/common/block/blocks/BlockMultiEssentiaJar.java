package com.science.gtnl.common.block.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
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
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMultiEssentiaJar jar)) return drops;

        ItemStack drop = new ItemStack(this);
        if (jar.getTotalAmount() > 0) {
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

            if (player.isSneaking()) {
                // 空手潜行右键：切换到下一个源质
                Aspect activeAspect = jar.cycleActiveAspect();
                sendActiveAspectStatus(player, jar, activeAspect);
                return true;
            }

            // 空手普通右键：打开选择 GUI
            if (jar.getStoredTypeCount() <= 0) {
                player.addChatMessage(
                    new ChatComponentTranslation(
                        "Info_MultiEssentiaJar_Empty",
                        TileEntityMultiEssentiaJar.MAX_CAPACITY));
                return true;
            }

            GuiFactories.tileEntity()
                .open(player, x, y, z);
            return true;
        }

        if (heldStack.getItem() != ConfigItems.itemEssence) return false;
        ItemEssence phialItem = (ItemEssence) ConfigItems.itemEssence;

        if (heldStack.getItemDamage() == 0) {
            return fillPhialFromJar(world, x, y, z, player, jar, phialItem);
        }
        return emptyPhialIntoJar(world, x, y, z, player, jar, phialItem, heldStack);
    }

    public static void sendActiveAspectStatus(
        EntityPlayer player,
        TileEntityMultiEssentiaJar jar,
        Aspect activeAspect) {

        if (activeAspect == null) {
            player.addChatMessage(
                new ChatComponentTranslation(
                    "Info_MultiEssentiaJar_Empty",
                    TileEntityMultiEssentiaJar.MAX_CAPACITY));
            return;
        }

        player.addChatMessage(
            new ChatComponentTranslation(
                "Info_MultiEssentiaJar_Status",
                ItemBlockMultiEssentiaJar.createServerAspectDisplay(
                    player,
                    activeAspect,
                    jar.containerContains(activeAspect)),
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

    private static void exchangeHeldItem(World world, int x, int y, int z, EntityPlayer player,
                                         ItemStack replacement) {
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
}
