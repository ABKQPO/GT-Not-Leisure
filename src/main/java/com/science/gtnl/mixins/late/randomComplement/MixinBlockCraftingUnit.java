package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.utils.recipes.CraftingUnitHandler;

import appeng.block.AEBaseTileBlock;
import appeng.block.crafting.BlockCraftingUnit;

@SuppressWarnings("ALL")
@Mixin(value = BlockCraftingUnit.class, remap = false)
public class MixinBlockCraftingUnit extends AEBaseTileBlock {

    public MixinBlockCraftingUnit(Material mat) {
        super(mat);
    }

    @Inject(method = "onBlockActivated", at = @At("HEAD"), remap = true, cancellable = true)
    public void onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int side, float hitX, float hitY,
        float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (CraftingUnitHandler.replaceUnit((BlockCraftingUnit) (Object) this, w, x, y, z, p)) cir.setReturnValue(true);
    }
}
