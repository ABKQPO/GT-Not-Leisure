package com.science.gtnl.mixins.late.Gregtech;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.mixinHelper.ICostingEUHolder;
import com.science.gtnl.api.mixinHelper.IWirelessMode;
import com.science.gtnl.utils.Utils;

import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.multi.purification.LinkedPurificationUnit;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBaryonicPerfection;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEPurificationPlant.class, remap = false)
public abstract class MixinMTEPurificationPlant extends MTEExtendedPowerMultiBlockBase<MixinMTEPurificationPlant>
    implements IWirelessMode {

    @Getter
    @Setter
    @Unique
    public boolean gtnl$wirelessMode;

    @Shadow
    @Final
    private List<LinkedPurificationUnit> mLinkedUnits;

    @Unique
    public BigInteger gtnl$costingEU = BigInteger.ZERO;

    @Unique
    public String gtnl$costingEUText = Utils.ZERO_STRING;

    public MixinMTEPurificationPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public boolean checkExoticAndNormalEnergyHatches() {
        boolean t8water = false;

        for (LinkedPurificationUnit unit : mLinkedUnits) {
            if (unit.metaTileEntity() instanceof MTEPurificationUnitBaryonicPerfection) {
                if (mExoticEnergyHatches.isEmpty() && mEnergyHatches.isEmpty()) {
                    gtnl$wirelessMode = true;
                }
                t8water = true;
                break;
            }
        }

        for (LinkedPurificationUnit unit : mLinkedUnits) {
            ((IWirelessMode) unit.metaTileEntity()).setGtnl$wirelessMode(gtnl$wirelessMode);
        }

        if (t8water) return true;
        return super.checkExoticAndNormalEnergyHatches();
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
        gtnl$costingEUText = Utils.ZERO_STRING;
    }

    @Inject(method = "startCycle", at = @At("TAIL"))
    private void gtnl$setCostingEU(CallbackInfo ci) {
        gtnl$costingEUText = GTUtility.formatNumbers(gtnl$costingEU);
    }

    @Inject(method = "registerLinkedUnit", at = @At("HEAD"))
    private void gtnl$registerLinkedUnit(CallbackInfo ci) {
        mLinkedUnits.removeIf(
            link -> link.metaTileEntity() == null || link.metaTileEntity()
                .getBaseMetaTileEntity() == null);
    }

    @Inject(method = "unregisterLinkedUnit", at = @At("HEAD"))
    private void gtnl$unregisterLinkedUnit(CallbackInfo ci) {
        mLinkedUnits.removeIf(
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

    @Inject(
        method = "addUIWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gtnewhorizons/modularui/api/screen/ModularWindow$Builder;widget(Lcom/gtnewhorizons/modularui/api/widget/Widget;)Lcom/gtnewhorizons/modularui/api/widget/IWidgetBuilder;",
            ordinal = 4,
            shift = At.Shift.BEFORE))
    private void gtnl$beforePowerSwitchWidget(ModularWindow.Builder builder, UIBuildContext buildContext,
        CallbackInfo ci) {
        builder.widget(createStructureUpdateButton(builder));
    }

    @Override
    public String[] getInfoData() {
        List<String> ret = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (gtnl$wirelessMode) {
            ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            ret.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + gtnl$costingEUText
                    + EnumChatFormatting.RESET
                    + " EU");
        }
        return ret.toArray(new String[0]);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currentTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (getBaseMetaTileEntity() != null) {
            tag.setBoolean("wirelessMode", gtnl$wirelessMode);
            if (gtnl$wirelessMode) tag.setString("costingEUText", gtnl$costingEUText);
        }
    }
}
