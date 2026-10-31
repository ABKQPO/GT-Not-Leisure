package com.science.gtnl.loader;

import com.science.gtnl.common.machine.voidminer.VMTweakHelper;

public class VoidMinerLoader implements IContentLoader {

    @Override
    public String name() {
        return "voidminer";
    }

    @Override
    public void loadComplete() {
        VMTweakHelper.initializeDimensionMappings();
    }
}
