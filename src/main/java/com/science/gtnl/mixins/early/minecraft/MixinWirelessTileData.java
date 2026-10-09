package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.wireless.WirelessTileData;

/** Preserves saved data while the prototype is disabled; this hook alone never creates a wireless connection. */
@Mixin(TileEntity.class)
public class MixinWirelessTileData implements WirelessTileData {

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

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void gtnl$readWireless(NBTTagCompound data, CallbackInfo ci) {
        gtnl$wirelessData = null;
        if (data.hasKey(KEY, 10)) gtnl$wirelessData().setTag(
            KEY,
            data.getCompoundTag(KEY)
                .copy());
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void gtnl$writeWireless(NBTTagCompound data, CallbackInfo ci) {
        if (gtnl$wirelessData != null && gtnl$wirelessData.hasKey(KEY, 10)) {
            data.setTag(
                KEY,
                gtnl$wirelessData.getCompoundTag(KEY)
                    .copy());
        } else {
            data.removeTag(KEY);
        }
    }
}
