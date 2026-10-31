package com.science.gtnl.mixins.late.twistSpaceTechnology;

import java.util.ArrayList;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;

import com.Nxer.TwistSpaceTechnology.common.machine.multiMachineClasses.GTCM_MultiMachineBase;
import com.Nxer.TwistSpaceTechnology.system.OreProcess.machines.TST_OreProcessingFactory;
import com.science.gtnl.utils.machine.MachineUtils;

@Mixin(value = TST_OreProcessingFactory.class, remap = false)
public abstract class MixinTST_OreProcessingFactory extends GTCM_MultiMachineBase<TST_OreProcessingFactory> {

    public MixinTST_OreProcessingFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        return MachineUtils.storedFluidsForColor(this, color, this::setHatchRecipeMap, supportsCraftingMEBuffer());
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        return MachineUtils.storedInputsForColor(this, color, supportsCraftingMEBuffer());
    }
}
