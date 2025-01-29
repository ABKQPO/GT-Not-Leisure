package com.science.gtnl.common.item.ReAvaritia;

import static com.science.gtnl.common.block.Casings.BasicBlocks.BlockSoulFarmland;

import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.world.BlockEvent;

import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.client.CreativeTabsLoader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlazeHoe extends ItemHoe {

    public static final ToolMaterial BLAZE = EnumHelper.addToolMaterial("BLAZE", 3, 7777, 9999F, 11.0F, 10);

    public BlazeHoe() {
        super(BLAZE);
        this.setUnlocalizedName("BlazeHoe");
        setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(CreativeTabsLoader.ReAvaritia);
        this.setTextureName("sciencenotleisure:BlazeHoe");
        this.setMaxDamage(7777);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(TextLocalization.Tooltip_BlazeHoe_00);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setBoolean("SmeltingMode", false);
        updateEnchantments(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            toggleSmeltingMode(stack);
            String messageKey = isSmeltingModeActive(stack) ? TextLocalization.Tooltip_Blaze_Smelt_On
                : TextLocalization.Tooltip_Blaze_Smelt_Off;
            if (world.isRemote) {
                showSubtitle(messageKey);
            }
        }
        return stack;
    }

    private void toggleSmeltingMode(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        boolean currentMode = nbt.getBoolean("SmeltingMode");
        nbt.setBoolean("SmeltingMode", !currentMode);
        updateEnchantments(stack);
    }

    private void updateEnchantments(ItemStack stack) {
        Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(Enchantment.fireAspect.effectId, 10);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    private boolean isSmeltingModeActive(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean("SmeltingMode");
    }

    @SubscribeEvent
    public void onBlockHarvest(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null) return;

        ItemStack heldItem = event.harvester.getCurrentEquippedItem();
        if (!(heldItem.getItem() instanceof BlazePickaxe)) return;

        boolean smeltingActive = isSmeltingModeActive(heldItem);
        if (!smeltingActive) return;

        Block block = event.block;
        int meta = event.blockMetadata;
        ItemStack blockStack = new ItemStack(block, 1, meta);
        ItemStack smeltResult = FurnaceRecipes.smelting()
            .getSmeltingResult(blockStack);
        if (smeltResult == null) return;

        int totalCount = 0;
        for (ItemStack drop : event.drops) {
            totalCount += drop.stackSize;
        }

        event.drops.clear();
        ItemStack result = smeltResult.copy();
        result.stackSize = totalCount;
        event.drops.add(result);
    }

    @SideOnly(Side.CLIENT)
    private void showSubtitle(String messageKey) {
        String localized = StatCollector.translateToLocal(messageKey);
        ChatComponentText text = new ChatComponentText(EnumChatFormatting.WHITE + localized);
        Minecraft.getMinecraft().ingameGUI.func_110326_a(text.getFormattedText(), true);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block instanceof BlockSoulSand) {
            world.setBlock(x, y, z, BlockSoulFarmland);

            stack.damageItem(1, player);

            world.playSoundEffect(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                BlockSoulFarmland.stepSound.getStepResourcePath(),
                (BlockSoulFarmland.stepSound.getVolume() + 1.0F) / 2.0F,
                BlockSoulFarmland.stepSound.getPitch() * 0.8F);

            return true;
        }

        return false;
    }
}
