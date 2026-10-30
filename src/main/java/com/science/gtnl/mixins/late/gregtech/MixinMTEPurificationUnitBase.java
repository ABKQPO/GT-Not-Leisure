package com.science.gtnl.mixins.late.gregtech;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.common.machine.purification.IPurificationUnitHost;
import com.science.gtnl.common.machine.purification.PurificationUnitHooks;
import com.science.gtnl.common.machine.purification.PurificationUnitState;
import com.science.gtnl.utils.machine.MachineUtils;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEPurificationUnitBase.class, remap = false)
public abstract class MixinMTEPurificationUnitBase extends MTEExtendedPowerMultiBlockBase<MixinMTEPurificationUnitBase>
    implements IPurificationUnitHost {

    @Shadow
    protected ArrayList<FluidStack> storedFluids;

    @Shadow
    protected GTRecipe currentRecipe;

    @Shadow
    protected int effectiveParallel;

    @Shadow
    private MTEPurificationPlant controller;

    @Shadow
    protected float currentRecipeChance;

    @Unique
    private final PurificationUnitState gtnl$state = new PurificationUnitState();

    public MixinMTEPurificationUnitBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public PurificationUnitState gtnl$state() {
        return gtnl$state;
    }

    @Override
    public ArrayList<FluidStack> gtnl$storedFluids() {
        return storedFluids;
    }

    @Override
    public void gtnl$setStoredFluids(ArrayList<FluidStack> fluids) {
        storedFluids = fluids;
    }

    @Override
    public GTRecipe gtnl$currentRecipe() {
        return currentRecipe;
    }

    @Override
    public void gtnl$setCurrentRecipe(GTRecipe recipe) {
        currentRecipe = recipe;
    }

    @Override
    public void gtnl$setCurrentRecipeChance(float chance) {
        currentRecipeChance = chance;
    }

    @Override
    public int gtnl$effectiveParallel() {
        return effectiveParallel;
    }

    @Override
    public MTEPurificationPlant gtnl$controller() {
        return controller;
    }

    @Override
    public boolean gtnl$supportsCraftingMEBuffer() {
        return supportsCraftingMEBuffer();
    }

    @Override
    public boolean isGtnl$wirelessMode() {
        return gtnl$state.isWirelessMode();
    }

    @Override
    public void setGtnl$wirelessMode(boolean wirelessMode) {
        gtnl$state.setWirelessMode(wirelessMode);
    }

    @Override
    public BigInteger getGtnl$costingEU() {
        return gtnl$state.getCostingEU();
    }

    @Override
    public long gtnl$getMaxParallelLong() {
        return gtnl$state.getMaxParallel();
    }

    @Override
    public void gtnl$setMaxParallelLong(long maxParallel) {
        gtnl$state.setMaxParallel(maxParallel);
    }

    @Override
    public long gtnl$getEffectiveParallelLong() {
        return gtnl$state.getEffectiveParallel();
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        gtnl$state.bindOwner(aBaseMetaTileEntity.getOwnerUuid());
    }

    @Override
    public boolean supportsCraftingMEBuffer() {
        return gtnl$state.isWirelessMode();
    }

    @Inject(method = "checkProcessing", at = @At("HEAD"), cancellable = true)
    public void checkProcessing(CallbackInfoReturnable<CheckRecipeResult> cir) {
        CheckRecipeResult result = PurificationUnitHooks
            .checkWirelessProcessing((MTEPurificationUnitBase<?>) (Object) this);
        if (result != null) cir.setReturnValue(result);
    }

    @Inject(method = "doPurificationRecipeCheck", at = @At("HEAD"))
    public void doPurificationRecipeCheck(CallbackInfoReturnable<Boolean> cir) {
        gtnl$state.resetParallel();
    }

    @Inject(method = "startCycle", at = @At("HEAD"), cancellable = true)
    public void startCycle(int cycleTime, int progressTime, CallbackInfo ci) {
        if (PurificationUnitHooks
            .startWirelessCycle((MTEPurificationUnitBase<?>) (Object) this, cycleTime, progressTime)) ci.cancel();
    }

    @Inject(method = "endCycle", at = @At("TAIL"))
    private void onEndCycle(CallbackInfo ci) {
        gtnl$state.resetParallel();
    }

    @Redirect(
        method = "calculateBoostedSuccessChance",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/tileentities/machines/multi/purification/MTEPurificationUnitBase;isWaterBoosted(Lgregtech/api/util/GTRecipe;)Z"))
    private boolean redirectWaterBoost(MTEPurificationUnitBase<?> instance, GTRecipe recipe) {
        if (gtnl$state.isWirelessMode()) return PurificationUnitHooks.isWaterBoostedList(instance, recipe);
        return instance.isWaterBoosted(recipe);
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        return MachineUtils.storedFluidsForColor(this, color, this::setHatchRecipeMap, supportsCraftingMEBuffer());
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        return MachineUtils.storedInputsForColor(this, color, supportsCraftingMEBuffer());
    }

    @Inject(method = "getActualPowerUsage", at = @At("TAIL"), cancellable = true)
    public void getActualPowerUsage(CallbackInfoReturnable<Long> cir) {
        if (gtnl$state.isWirelessMode()) cir.setReturnValue(0L);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        gtnl$state.readFromNBT(aNBT);
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        gtnl$state.writeToNBT(aNBT);
    }

    @Inject(method = "getInfoData", at = @At("TAIL"))
    public void getInfoData(CallbackInfoReturnable<String[]> cir, @Local(name = "ret") ArrayList<String> ret) {
        PurificationUnitHooks.addParallelInfo((MTEPurificationUnitBase<?>) (Object) this, ret);
    }

    @Inject(method = "getExtraWailaBody", at = @At("HEAD"))
    public void getExtraWailaBody(ItemStack itemStack, List<String> list, NBTTagCompound tag,
        IWailaDataAccessor accessor, IWailaConfigHandler config, CallbackInfo ci) {
        PurificationUnitHooks.addWirelessWailaBody(list, tag);
    }

    @Inject(method = "getExtraWailaNBT", at = @At("HEAD"))
    public void getExtraWailaNBT(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z, CallbackInfo ci) {
        if (getBaseMetaTileEntity() != null) {
            PurificationUnitHooks
                .writeWirelessWailaData(tag, gtnl$state.isWirelessMode(), gtnl$state.getCostingEUText());
        }
    }
}
