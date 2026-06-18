package com.science.gtnl.mixins.late.NoNHU;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.enderio.core.common.TileEntityEnder;
import com.science.gtnl.api.ITileEntityTickAcceleration;

@Mixin(value = TileEntityEnder.class)
public abstract class MixinTileEntityEnder extends TileEntity implements ITileEntityTickAcceleration {

    @Shadow(remap = false)
    private long lastUpdate;

    @Unique
    private int GTNotLeisure$tickAcceleratedRate = 1;

    @Override
    public int getTickAcceleratedRate() {
        return this.GTNotLeisure$tickAcceleratedRate;
    }

    @Override
    public boolean tickAcceleration(int tickAcceleratedRate) {
        this.GTNotLeisure$tickAcceleratedRate = tickAcceleratedRate;
        for (int i = 0; i < tickAcceleratedRate; i++) {
            this.lastUpdate = -1L; // make sure updateEntity() be called
            this.updateEntity();
        }
        return true;
    }
}
