package com.science.gtnl.mixins.late.twistSpaceTechnology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.Nxer.TwistSpaceTechnology.common.modularizedMachine.MM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2;
import com.Nxer.TwistSpaceTechnology.common.modularizedMachine.ModularizedMachineLogic.MultiExecutionCoreMachineSupportAllModuleBase;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReason;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2.class, remap = false)
public abstract class MixinMM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2 extends
    MultiExecutionCoreMachineSupportAllModuleBase<MixinMM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2> {

    @Shadow
    private long runningTime;

    @Shadow
    protected double fuelCostMultiplier;

    @Unique
    public boolean gtnl$lockRuntime;

    public MixinMM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Inject(method = "resetDiscount", at = @At("HEAD"), cancellable = true)
    public void resetDiscount(int tickDecrease, CallbackInfo ci) {
        if (gtnl$lockRuntime) {
            if (this.runningTime > 1728000) this.runningTime = 1728000L;
            ci.cancel();
        }
    }

    @Override
    public void setItemNBT(NBTTagCompound nbt) {
        nbt.setBoolean("lockRuntime", gtnl$lockRuntime);
        super.setItemNBT(nbt);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        gtnl$lockRuntime = aNBT.getBoolean("lockRuntime");
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        aNBT.setBoolean("lockRuntime", gtnl$lockRuntime);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!gtnl$lockRuntime) {
            ItemStack heldItem = aPlayer.getHeldItem();
            if (GTUtility.areStacksEqual(heldItem, GTNLItemList.TransdimensionalMnemonicMatrix.get(1), true)) {
                aPlayer.setCurrentItemOrArmor(0, ItemUtils.depleteStack(heldItem, 1));
                gtnl$lockRuntime = true;
                return true;
            }
        }
        return super.onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    @Inject(method = "getInfoData", at = @At("RETURN"), cancellable = true)
    public void getInfoData(CallbackInfoReturnable<String[]> cir) {
        if (!gtnl$lockRuntime) return;
        String[] original = cir.getReturnValue();
        List<String> list = new ArrayList<>(Arrays.asList(original));
        list.add(StatCollector.translateToLocal("Info_PlasmaForge_00"));
        cir.setReturnValue(list.toArray(new String[0]));
    }

    @Override
    public void stopMachine(@NotNull ShutDownReason reason) {
        if (!gtnl$lockRuntime) {
            this.runningTime = 0L;
            this.fuelCostMultiplier = 1.0F;
        }
        super.stopMachine(reason);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (!tag.getBoolean("lockRuntime")) return;
        currentTip.add(StatCollector.translateToLocal("Info_PlasmaForge_00"));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setBoolean("lockRuntime", gtnl$lockRuntime);
    }
}
