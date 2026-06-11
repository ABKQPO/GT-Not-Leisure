package com.science.gtnl.mixins.late.AppliedEnergistics;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.glodblock.github.inventory.IDualHost;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.mixinHelper.IDualityInterface;
import com.science.gtnl.config.MainConfig;

import appeng.api.config.YesNo;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.MultiCraftingTracker;
import appeng.helpers.UnlockCraftingEvent;
import appeng.me.helpers.AENetworkProxy;
import appeng.parts.automation.UpgradeInventory;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.ConfigManager;
import appeng.util.ScheduledReason;
import appeng.util.inv.WrapperInvSlot;
import lombok.Getter;
import lombok.Setter;

@Mixin(value = DualityInterface.class, remap = false)
@Getter
@Setter
public abstract class MixinDualityInterface implements IDualityInterface {

    @Shadow
    @Final
    @Mutable
    private int[] sides;
    @Shadow
    @Final
    @Mutable
    private IAEItemStack[] requireWork;
    @Shadow
    @Final
    @Mutable
    private MultiCraftingTracker craftingTracker;
    @Shadow
    @Final
    @Mutable
    protected AENetworkProxy gridProxy;
    @Shadow
    @Final
    @Mutable
    private IInterfaceHost iHost;
    @Shadow
    @Final
    @Mutable
    private BaseActionSource mySource;
    @Shadow
    @Final
    @Mutable
    private BaseActionSource interfaceRequestSource;
    @Shadow
    @Final
    @Mutable
    private ConfigManager cm;
    @Shadow
    @Final
    @Mutable
    private AppEngInternalAEInventory config;
    @Shadow
    @Final
    @Mutable
    private AppEngInternalInventory patterns;
    @Shadow
    @Final
    @Mutable
    private UpgradeInventory upgrades;

    @Shadow
    private AppEngInternalInventory storage;
    @Shadow
    private WrapperInvSlot slotInv;
    @Shadow
    private boolean hasConfig;
    @Shadow
    private int priority;
    @Shadow
    public List<ICraftingPatternDetails> craftingList;
    @Shadow
    public boolean sharedInventory;
    @Shadow
    private List<IAEStack<?>> waitingToSend;
    @Shadow
    private IMEInventory<IAEItemStack> destination;
    @Shadow
    private boolean isWorking;
    @Shadow
    private YesNo redstoneState;
    @Shadow
    private UnlockCraftingEvent unlockEvent;
    @Shadow
    private List<IAEStack<?>> unlockStacks;
    @Shadow
    private int lastInputHash;
    @Shadow
    private ScheduledReason scheduledReason;
    @Shadow
    @Final
    @Mutable
    private boolean isFluidInterface;

    @Unique
    private boolean[] gtnl$hasFuzzyConfig = new boolean[9];

    @Override
    public boolean[] getHasFuzzyConfig() {
        return gtnl$hasFuzzyConfig;
    }

    @Override
    public void setHasFuzzyConfig(boolean[] hasFuzzyConfig) {
        gtnl$hasFuzzyConfig = hasFuzzyConfig;
    }

    @Invoker("updatePlan")
    public abstract void gtnl$updatePlan(int slot);

    // Fuck you
    @Deprecated
    @Inject(method = "<init>", at = @At("RETURN"))
    private void gtnl$markDualHostAsFluidInterface(AENetworkProxy networkProxy, IInterfaceHost ih, CallbackInfo ci) {
        if (ih instanceof IDualHost) {
            isFluidInterface = true;
        }
    }

    @Inject(
        method = "getRawTermName",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getUnlocalizedName()Ljava/lang/String;",
            remap = true),
        cancellable = true)
    private void gtnl$injectBeforeItemReturn(CallbackInfoReturnable<String> cir,
        @Local(name = "item") ItemStack itemStack) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        if (!itemStack.hasDisplayName()) return;
        String name = itemStack.getDisplayName();
        if (!name.startsWith("gt_circuit_") && !name.contains("extra_start_")) return;
        cir.setReturnValue(name + itemStack.getUnlocalizedName());
    }

    @Unique
    private int configSlots = 9;
    @Unique
    private int storageSlots = 9;
    @Unique
    private int patternSlots = 9 * 4;
    @Unique
    private int upgradeSlots = 4;

    @ModifyConstant(method = "readConfig", constant = @Constant(intValue = 9))
    private int modifyConfigSlots(int original) {
        return configSlots;
    }

    @ModifyConstant(method = "updateStorage", constant = @Constant(intValue = 9))
    private int modifyUpdateStorage(int original) {
        return storageSlots;
    }

}
