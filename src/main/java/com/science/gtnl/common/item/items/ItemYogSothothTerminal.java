package com.science.gtnl.common.item.items;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.YogSothothTerminalGui;
import com.science.gtnl.common.yogsothoth.YogTerminalService;
import com.science.gtnl.common.yogsothoth.YogTerminals;
import com.science.gtnl.common.yogsothoth.YogToken;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.features.INetworkEncodable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.AppEng;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.IBaubleExpanded;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Optional.Interface(iface = "cofh.api.energy.IEnergyContainerItem", modid = "CoFHAPI|energy")
public class ItemYogSothothTerminal extends Item implements IBauble, IBaubleExpanded, ISpecialElectricItem,
    IAEItemPowerStorage, IEnergyContainerItem, IGuiHolder<PlayerInventoryGuiData>, INetworkEncodable {

    public static ItemYogSothothTerminal INSTANCE;
    private static final Set<Class<?>> FAILING_TICKS = new ObjectOpenHashSet<>();

    public ItemYogSothothTerminal() {
        INSTANCE = this;
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(ScienceNotLeisure.MODID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new YogSothothTerminalGui(data, syncManager).build();
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) YogTerminalService.touch(stack, player);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        if (world.isRemote || !(entity instanceof EntityPlayer)) return;
        for (ItemStack terminal : YogTerminals.stored(stack)) {
            tick(
                terminal,
                () -> terminal.getItem()
                    .onUpdate(terminal, world, entity, slot, false));
        }
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase wearer) {
        if (wearer.worldObj.isRemote) return;
        for (ItemStack terminal : YogTerminals.stored(stack)) {
            if (terminal.getItem() instanceof IBauble bauble) tick(terminal, () -> bauble.onWornTick(terminal, wearer));
            else tick(
                terminal,
                () -> terminal.getItem()
                    .onUpdate(terminal, wearer.worldObj, wearer, 0, false));
        }
    }

    private static void tick(ItemStack terminal, Runnable tick) {
        try {
            tick.run();
        } catch (RuntimeException exception) {
            if (FAILING_TICKS.add(
                terminal.getItem()
                    .getClass()))
                ScienceNotLeisure.LOG.error(
                    "Stored terminal {} failed to tick inside a Yog-Sothoth terminal",
                    terminal.getItem()
                        .getClass()
                        .getName(),
                    exception);
        }
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return new String[] { AppEng.BAUBLESLOT, BaubleExpandedSlots.universalType };
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase wearer) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase wearer) {
        return true;
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack stack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack stack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        double charge = 0;
        for (ItemStack terminal : YogTerminals.stored(stack))
            if (terminal.getItem() instanceof IElectricItem electric) charge += electric.getMaxCharge(terminal);
        return charge;
    }

    @Override
    public int getTier(ItemStack stack) {
        int tier = 1;
        for (ItemStack terminal : YogTerminals.stored(stack))
            if (terminal.getItem() instanceof IElectricItem electric) tier = Math.max(tier, electric.getTier(terminal));
        return tier;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        double limit = 0;
        for (ItemStack terminal : YogTerminals.stored(stack)) if (terminal.getItem() instanceof IElectricItem electric)
            limit = Math.max(limit, electric.getTransferLimit(terminal));
        return limit;
    }

    @Override
    public IElectricItemManager getManager(ItemStack stack) {
        return Manager.INSTANCE;
    }

    private static class Manager implements IElectricItemManager {

        static final Manager INSTANCE = new Manager();

        @Override
        public double charge(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
            double charged = 0;
            for (ItemStack terminal : YogTerminals.stored(stack)) {
                if (charged >= amount) break;
                if (terminal.getItem() instanceof IElectricItem) charged += ElectricItem.manager
                    .charge(terminal, amount - charged, tier, ignoreTransferLimit, simulate);
            }
            return charged;
        }

        @Override
        public double discharge(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit,
            boolean externally, boolean simulate) {
            return 0;
        }

        @Override
        public double getCharge(ItemStack stack) {
            double charge = 0;
            for (ItemStack terminal : YogTerminals.stored(stack))
                if (terminal.getItem() instanceof IElectricItem) charge += ElectricItem.manager.getCharge(terminal);
            return charge;
        }

        @Override
        public boolean canUse(ItemStack stack, double amount) {
            return false;
        }

        @Override
        public boolean use(ItemStack stack, double amount, EntityLivingBase entity) {
            return false;
        }

        @Override
        public void chargeFromArmor(ItemStack stack, EntityLivingBase entity) {}

        @Override
        public String getToolTip(ItemStack stack) {
            return null;
        }
    }

    @Override
    public double injectAEPower(ItemStack stack, double amount) {
        double left = amount;
        for (ItemStack terminal : YogTerminals.stored(stack)) {
            if (left <= 0) break;
            if (terminal.getItem() instanceof IAEItemPowerStorage storage) left = storage.injectAEPower(terminal, left);
        }
        return left;
    }

    @Override
    public double extractAEPower(ItemStack stack, double amount) {
        return 0;
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        double power = 0;
        for (ItemStack terminal : YogTerminals.stored(stack))
            if (terminal.getItem() instanceof IAEItemPowerStorage storage) power += storage.getAEMaxPower(terminal);
        return power;
    }

    @Override
    public double getAECurrentPower(ItemStack stack) {
        double power = 0;
        for (ItemStack terminal : YogTerminals.stored(stack))
            if (terminal.getItem() instanceof IAEItemPowerStorage storage) power += storage.getAECurrentPower(terminal);
        return power;
    }

    @Override
    public AccessRestriction getPowerFlow(ItemStack stack) {
        return AccessRestriction.WRITE;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        int received = 0;
        for (ItemStack terminal : YogTerminals.stored(stack)) {
            if (received >= maxReceive) break;
            if (terminal.getItem() instanceof IEnergyContainerItem container)
                received += container.receiveEnergy(terminal, maxReceive - received, simulate);
        }
        return received;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int getEnergyStored(ItemStack stack) {
        long energy = 0;
        for (ItemStack terminal : YogTerminals.stored(stack))
            if (terminal.getItem() instanceof IEnergyContainerItem container)
                energy += container.getEnergyStored(terminal);
        return (int) Math.min(Integer.MAX_VALUE, energy);
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int getMaxEnergyStored(ItemStack stack) {
        long energy = 0;
        for (ItemStack terminal : YogTerminals.stored(stack))
            if (terminal.getItem() instanceof IEnergyContainerItem container)
                energy += container.getMaxEnergyStored(terminal);
        return (int) Math.min(Integer.MAX_VALUE, energy);
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
    public String getEncryptionKey(ItemStack stack) {
        return "";
    }

    // Wireless access points and the security terminal link whatever is encodable; pass it on to every stored
    // terminal that can be linked.
    @Override
    public void setEncryptionKey(ItemStack stack, String encryptionKey, String name) {
        for (ItemStack terminal : YogTerminals.stored(stack)) {
            INetworkEncodable encodable = AEApi.instance()
                .registries()
                .wireless()
                .getWirelessTerminalHandler(terminal);
            if (encodable == null && terminal.getItem() instanceof INetworkEncodable item) encodable = item;
            if (encodable != null) encodable.setEncryptionKey(terminal, encryptionKey, name);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getAEMaxPower(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        double max = getAEMaxPower(stack);
        return max <= 0 ? 0 : 1 - getAECurrentPower(stack) / max;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        int stored = YogTerminals.stored(stack)
            .size();
        lines.add(StatCollector.translateToLocalFormatted("gtnl.yog_sothoth_terminal.tooltip.stored", stored));
        int pending = YogToken.pendingCount(stack);
        if (pending > 0) lines.add(
            EnumChatFormatting.GRAY
                + StatCollector.translateToLocalFormatted("gtnl.yog_sothoth_terminal.tooltip.pending", pending));
        lines.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("gtnl.yog_sothoth_terminal.tooltip.1"));
        lines.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("gtnl.yog_sothoth_terminal.tooltip.2"));
        lines.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("gtnl.yog_sothoth_terminal.tooltip.3"));
    }
}
