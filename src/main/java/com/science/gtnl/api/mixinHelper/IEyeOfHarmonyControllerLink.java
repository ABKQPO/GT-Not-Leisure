package com.science.gtnl.api.mixinHelper;

import java.util.Map;

import net.minecraftforge.fluids.Fluid;

import com.gtnewhorizons.modularui.api.widget.Widget;
import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;

public interface IEyeOfHarmonyControllerLink {

    void setController(EyeOfHarmonyInjector controller);

    EyeOfHarmonyInjector getController();

    int getControllerX();

    int getControllerY();

    int getControllerZ();

    void setControllerSet(boolean value);

    boolean isControllerSet();

    void unlinkController();

    Widget makeSyncerWidgets();

    Map<Fluid, Long> getValidFluidMap();

    long gtnl$getHydrogenStored();

    long gtnl$getHeliumStored();

    long gtnl$getStellarPlasmaStored();
}
