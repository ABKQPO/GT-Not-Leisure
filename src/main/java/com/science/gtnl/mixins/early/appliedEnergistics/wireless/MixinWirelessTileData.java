package com.science.gtnl.mixins.early.appliedEnergistics.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.wireless.IWirelessTileData;
import com.science.gtnl.common.wireless.WirelessFrequencies;
import com.science.gtnl.common.wireless.WirelessLinkPersistence;

@Mixin(TileEntity.class)
public class MixinWirelessTileData implements IWirelessTileData {

    @Unique
    private NBTTagCompound gtnl$wirelessData;

    @Override
    public NBTTagCompound gtnl$wirelessData() {
        if (gtnl$wirelessData == null) gtnl$wirelessData = new NBTTagCompound();
        return gtnl$wirelessData;
    }

    @Override
    public boolean gtnl$hasWirelessData() {
        return gtnl$wirelessData != null && gtnl$wirelessData.hasKey(KEY, 10);
    }

    @Override
    public boolean gtnl$hasFrequencyData() {
        return gtnl$wirelessData != null && gtnl$wirelessData.hasKey(FREQUENCY_KEY, 10);
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void gtnl$readWireless(NBTTagCompound data, CallbackInfo ci) {
        gtnl$wirelessData = IWirelessTileData.load(data);
    }

    @Inject(method = "validate", at = @At("RETURN"))
    private void gtnl$placedWireless(CallbackInfo ci) {
        if (gtnl$hasFrequencyData()) WirelessFrequencies.register((TileEntity) (Object) this);
        if (gtnl$hasWirelessData()) WirelessLinkPersistence.placed((TileEntity) (Object) this);
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void gtnl$writeWireless(NBTTagCompound data, CallbackInfo ci) {
        IWirelessTileData.save(gtnl$wirelessData, data);
    }
}
