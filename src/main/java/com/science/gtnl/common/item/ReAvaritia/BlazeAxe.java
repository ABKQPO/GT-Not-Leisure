package com.science.gtnl.common.item.ReAvaritia;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
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

public class BlazeAxe extends ItemAxe {

    private static final Random random = new Random();
    public static final ToolMaterial BLAZE = EnumHelper.addToolMaterial("BLAZE", 32, 7777, 9999F, 33.0F, 22);

    public BlazeAxe() {
        super(BLAZE);
        this.setUnlocalizedName("BlazeAxe");
        setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(CreativeTabsLoader.ReAvaritia);
        this.setTextureName("sciencenotleisure:BlazeAxe");
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
        toolTip.add(TextLocalization.Tooltip_BlazeAxe_00);
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
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
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
        if (!(heldItem.getItem() instanceof BlazeAxe)) return;

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
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        int duration = this.getMaxItemUseDuration(stack) - timeLeft;

        if (duration > 20) {
            playerIn.addPotionEffect(new PotionEffect(Potion.resistance.id, 40, 1));
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));

        if (random.nextInt(100) < 30) {
            performAreaAttack(attacker, target, 10);
        }

        stack.damageItem(1, attacker);
        return true;
    }

    private void performAreaAttack(EntityLivingBase attacker, EntityLivingBase target, double radius) {
        World world = attacker.worldObj;
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
            target.posX - radius,
            target.posY - radius,
            target.posZ - radius,
            target.posX + radius,
            target.posY + radius,
            target.posZ + radius);

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase entity : entities) {
            if (entity != attacker && entity != target) {
                entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 25.0F);
            }
        }
    }
}
