package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;
import static com.reavaritia.common.BlockLoader.BlockSoulFarmland;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlazeHoe extends ItemHoe implements SubtitleDisplay {

    public BlazeHoe() {
        super(ToolHelper.BLAZE);
        this.setUnlocalizedName("BlazeHoe");
        setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "BlazeHoe");
        this.setMaxDamage(7777);
        ReAvaItemList.BlazeHoe.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_BlazeHoe_00"));
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
            String messageKey = isSmeltingModeActive(stack) ? StatCollector.translateToLocal("Tooltip_Blaze_Smelt_On")
                : StatCollector.translateToLocal("Tooltip_Blaze_Smelt_Off");
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

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle(String messageKey) {
        IChatComponent component = new ChatComponentTranslation(messageKey);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
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
