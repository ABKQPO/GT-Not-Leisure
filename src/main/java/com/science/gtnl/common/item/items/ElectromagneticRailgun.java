package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.equipment.EquipmentElectricItem;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.gui.EquipmentContainer;
import com.science.gtnl.common.gui.RailgunGui;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunEnergy;
import com.science.gtnl.common.railgun.RailgunFireService;
import com.science.gtnl.common.railgun.RailgunServerEvents;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.modularui2.GTModularScreen;

public class ElectromagneticRailgun extends Item implements EquipmentElectricItem, IGuiHolder<PlayerInventoryGuiData> {

    public static final String NBT_EXTREME_HIGH_VOLTAGE = "ExtremeHighVoltage";
    public static final int USE_DURATION = 72000;

    public ElectromagneticRailgun() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("gtnl.electromagnetic_railgun");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "electromagnetic_railgun");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "electromagnetic_railgun");
        GTNLItemList.ElectromagneticRailgun.set(new ItemStack(this, 1));
        RailgunServerEvents.register();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> toolTip, boolean advancedToolTips) {
        toolTip.add(
            StatCollector.translateToLocalFormatted(
                "item.gtnl.electromagnetic_railgun.tooltip.0",
                EquipmentEnergy.format(EquipmentEnergy.read(stack)),
                EquipmentEnergy.format(RailgunEnergy.capacity(stack))));
        toolTip.add(
            StatCollector.translateToLocalFormatted(
                "item.gtnl.electromagnetic_railgun.tooltip.1",
                RailgunData.modules(stack)
                    .total()));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.electromagnetic_railgun.tooltip.2"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.electromagnetic_railgun.tooltip.3"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        items.add(new ItemStack(this));
        items.add(charged());
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        if (!world.isRemote && held
            && entity instanceof EntityPlayerMP player
            && !player.isUsingItem()
            && world.getTotalWorldTime() % 20 == 0) RailgunEnergy.refillFromWireless(stack, player);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isUsingItem() || player.getItemInUse() != stack) player.setItemInUse(stack, USE_DURATION);
        if (!world.isRemote && player instanceof EntityPlayerMP serverPlayer)
            RailgunFireService.beginCharge(serverPlayer, stack);
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (!player.worldObj.isRemote && player instanceof EntityPlayerMP serverPlayer)
            RailgunFireService.tickCharge(serverPlayer, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
        if (!world.isRemote && player instanceof EntityPlayerMP serverPlayer)
            RailgunFireService.release(serverPlayer, stack);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return true;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        return 0;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        return false;
    }

    @Override
    public int energyTier(ItemStack stack) {
        return RailgunData.modules(stack)
            .energyTier();
    }

    @Override
    public double capacity(ItemStack stack) {
        return RailgunEnergy.capacity(stack);
    }

    public static boolean is(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ElectromagneticRailgun;
    }

    public static boolean isHeld(EntityPlayer player) {
        return player != null && is(player.getHeldItem());
    }

    public static boolean isExtremeHighVoltage(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getBoolean(NBT_EXTREME_HIGH_VOLTAGE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel panel) {
        return new GTModularScreen(panel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager sync, UISettings settings) {
        ItemStack bound = data.getUsedItemStack();
        RailgunGui gui = new RailgunGui(data, sync);
        settings.customContainer(() -> new EquipmentContainer(data.getSlotIndex(), gui, RailgunEnergy::clamp));
        settings.canInteractWith(player -> !player.isDead && data.getUsedItemStack() == bound);
        return gui.build();
    }
}
