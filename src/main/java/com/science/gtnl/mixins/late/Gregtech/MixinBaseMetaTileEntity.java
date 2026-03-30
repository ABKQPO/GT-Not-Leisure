package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.util.AxisAlignedBB;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.api.ITileEntityTickAcceleration;
import com.science.gtnl.asm.GTNLEarlyCoreMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.CommonBaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.render.IMTERenderer;

@Mixin(value = BaseMetaTileEntity.class, remap = false)
public abstract class MixinBaseMetaTileEntity extends CommonBaseMetaTileEntity implements ITileEntityTickAcceleration {

    @Shadow
    protected MetaTileEntity mMetaTileEntity;

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        if (GTNLEarlyCoreMod.enableAprilFool || mMetaTileEntity instanceof IMTERenderer) {
            return AxisAlignedBB.getBoundingBox(
                this.xCoord - 1024,
                this.yCoord - 1024,
                this.zCoord - 1024,
                this.xCoord + 1024,
                this.yCoord + 1024,
                this.zCoord + 1024);
        }
        return super.getRenderBoundingBox();
    }
}
