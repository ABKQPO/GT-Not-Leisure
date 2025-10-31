package com.science.gtnl.mixins.late.Gregtech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IWirelessMode;

import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.common.tileentities.machines.multi.purification.LinkedPurificationUnit;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBaryonicPerfection;
import lombok.Getter;
import lombok.Setter;

@Mixin(value = MTEPurificationPlant.class, remap = false)
public abstract class MixinMTEPurificationPlant extends MTEExtendedPowerMultiBlockBase<MixinMTEPurificationPlant>
    implements IWirelessMode {

    @Getter
    @Setter
    @Unique
    public boolean wirelessMode;

    @Shadow
    @Final
    private List<LinkedPurificationUnit> mLinkedUnits;

    public MixinMTEPurificationPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public boolean checkExoticAndNormalEnergyHatches() {
        boolean t8water = false;

        for (LinkedPurificationUnit unit : mLinkedUnits) {
            if (unit.metaTileEntity() instanceof MTEPurificationUnitBaryonicPerfection) {
                if (mExoticEnergyHatches.isEmpty() && mEnergyHatches.isEmpty()) {
                    wirelessMode = true;
                }
                t8water = true;
                break;
            }
        }

        for (LinkedPurificationUnit unit : mLinkedUnits) {
            ((IWirelessMode) unit.metaTileEntity()).setWirelessMode(wirelessMode);
        }

        if (t8water) return true;
        return super.checkExoticAndNormalEnergyHatches();
    }

    @Override
    public void clearHatches() {
        wirelessMode = false;
        super.clearHatches();
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        wirelessMode = aNBT.getBoolean("wirelessMode");
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        aNBT.setBoolean("wirelessMode", wirelessMode);
    }

    @Override
    public String[] getInfoData() {
        List<String> ret = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (wirelessMode)
            ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
        return ret.toArray(new String[0]);
    }
}
