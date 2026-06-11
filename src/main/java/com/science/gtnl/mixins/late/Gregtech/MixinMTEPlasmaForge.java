package com.science.gtnl.mixins.late.Gregtech;

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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.multi.MTEPlasmaForge;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEPlasmaForge.class, remap = false)
public abstract class MixinMTEPlasmaForge extends MTEExtendedPowerMultiBlockBase<MixinMTEPlasmaForge> {

    @Shadow
    @Final
    private static double efficiency_decay_rate;

    @Unique
    public boolean gtnl$lockRuntime;

    public MixinMTEPlasmaForge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public void setItemNBT(NBTTagCompound nbt) {
        if (gtnl$lockRuntime) nbt.setBoolean("lockRuntime", true);
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

    @ModifyConstant(method = "onPostTick", constant = @Constant(longValue = 100, ordinal = 0))
    private long gtnl$modifyLockRuntime(long original) {
        return gtnl$lockRuntime ? 0 : (long) efficiency_decay_rate;
    }

    @Override
    public String[] getInfoData() {
        String[] original = super.getInfoData();
        if (!gtnl$lockRuntime) return original;
        List<String> list = new ArrayList<>(Arrays.asList(original));
        list.add(StatCollector.translateToLocal("Info_PlasmaForge_00"));
        return list.toArray(new String[0]);
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
