package com.science.gtnl.mixins.late.gregtech;

import java.math.BigInteger;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnllib.utils.MathUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.mixinHelper.ICostingEUHolder;
import com.science.gtnl.api.mixinHelper.IWirelessMode;
import com.science.gtnl.common.machine.purification.PurificationUnitHooks;

import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.structure.error.StructureError;
import gregtech.common.tileentities.machines.multi.purification.LinkedPurificationUnit;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEPurificationPlant.class, remap = false)
public abstract class MixinMTEPurificationPlant extends MTEExtendedPowerMultiBlockBase<MixinMTEPurificationPlant>
    implements IWirelessMode, ICostingEUHolder {

    @Getter
    @Setter
    @Unique
    public boolean gtnl$wirelessMode;

    @Shadow
    @Final
    private List<LinkedPurificationUnit> linkedUnits;

    @Unique
    public BigInteger gtnl$costingEU = BigInteger.ZERO;

    @Unique
    public String gtnl$costingEUText = MathUtils.ZERO_STRING;

    public MixinMTEPurificationPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public BigInteger getGtnl$costingEU() {
        return gtnl$costingEU;
    }

    @Override
    public void checkHasAnyEnergy(List<StructureError> errors) {
        super.checkExoticAndNormalEnergyHatches(errors);
    }

    @Override
    public void checkExoticAndNormalEnergyHatches(List<StructureError> errors) {
        if (!PurificationUnitHooks
            .linkWirelessMode(linkedUnits, this, mExoticEnergyHatches.isEmpty() && mEnergyHatches.isEmpty())) {
            super.checkExoticAndNormalEnergyHatches(errors);
        }
    }

    @Override
    public void clearHatches() {
        gtnl$wirelessMode = false;
        super.clearHatches();
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        gtnl$wirelessMode = aNBT.getBoolean("wirelessMode");
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        aNBT.setBoolean("wirelessMode", gtnl$wirelessMode);
    }

    @Inject(method = "startCycle", at = @At("HEAD"))
    private void gtnl$resetCostingEU(CallbackInfo ci) {
        gtnl$costingEU = BigInteger.ZERO;
        gtnl$costingEUText = MathUtils.ZERO_STRING;
    }

    @Inject(method = "startCycle", at = @At("TAIL"))
    private void gtnl$setCostingEU(CallbackInfo ci) {
        gtnl$costingEUText = NumberFormatUtil.formatNumber(gtnl$costingEU);
    }

    @Inject(method = "registerLinkedUnit", at = @At("HEAD"))
    private void gtnl$registerLinkedUnit(CallbackInfo ci) {
        linkedUnits.removeIf(
            link -> link.metaTileEntity() == null || link.metaTileEntity()
                .getBaseMetaTileEntity() == null);
    }

    @Inject(method = "unregisterLinkedUnit", at = @At("HEAD"))
    private void gtnl$unregisterLinkedUnit(CallbackInfo ci) {
        linkedUnits.removeIf(
            link -> link.metaTileEntity() == null || link.metaTileEntity()
                .getBaseMetaTileEntity() == null);
    }

    @Inject(
        method = "startCycle",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/tileentities/machines/multi/purification/MTEPurificationUnitBase;startCycle(II)V"))
    private void gtnl$collectUnitCostingEU(CallbackInfo ci,
        @Local(name = "metaTileEntity") MTEPurificationUnitBase<?> metaTileEntity) {
        if (!gtnl$wirelessMode) return;
        BigInteger costingEU = ((ICostingEUHolder) metaTileEntity).getGtnl$costingEU();
        if (costingEU.signum() > 0) {
            gtnl$costingEU = gtnl$costingEU.add(costingEU);
        }
    }

    @Override
    public String[] getInfoData() {
        return PurificationUnitHooks.plantInfoData(super.getInfoData(), gtnl$wirelessMode, gtnl$costingEUText);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        PurificationUnitHooks.addWirelessWailaBody(currentTip, accessor.getNBTData());
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (getBaseMetaTileEntity() != null) {
            PurificationUnitHooks.writeWirelessWailaData(tag, gtnl$wirelessMode, gtnl$costingEUText);
        }
    }
}
