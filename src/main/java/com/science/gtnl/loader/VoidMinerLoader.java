package com.science.gtnl.loader;

import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.utils.machine.VMTweakHelper;

import cpw.mods.fml.common.FMLCommonHandler;

public class VoidMinerLoader implements IContentLoader {

    public static final VMTweakHelper VM_TWEAK_HELPER = new VMTweakHelper();

    @Override
    public String name() {
        return "voidminer";
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(VM_TWEAK_HELPER);
        FMLCommonHandler.instance()
            .bus()
            .register(VM_TWEAK_HELPER);
    }

    @Override
    public void loadComplete() {
        VMTweakHelper.initializeDimensionMappings();
    }
}
