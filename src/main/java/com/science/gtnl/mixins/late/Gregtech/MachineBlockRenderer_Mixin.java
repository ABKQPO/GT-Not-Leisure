package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.machine.multiblock.MeteorMiner;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.render.GTRendererBlock;

@SuppressWarnings("UnusedMixin")
@Mixin(value = GTRendererBlock.class, remap = false)
public abstract class MachineBlockRenderer_Mixin {

    @Inject(method = "renderWorldBlock", at = @At("HEAD"), cancellable = true)
    public void onRenderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelID,
        RenderBlocks renderer, CallbackInfoReturnable<Boolean> cir) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IGregTechTileEntity iGregTechTileEntity) {
            if (iGregTechTileEntity.getMetaTileEntity() instanceof MeteorMiner) {
                renderer.enableAO = false;
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
