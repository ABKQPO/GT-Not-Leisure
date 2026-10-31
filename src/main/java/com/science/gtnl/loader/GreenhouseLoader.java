package com.science.gtnl.loader;

import com.science.gtnl.common.greenhouse.GreenHouseBucket;

public class GreenhouseLoader implements IContentLoader {

    @Override
    public String name() {
        return "greenhouse";
    }

    @Override
    public void postInit() {
        GreenHouseBucket.LoadGreenHouseBuckets();
    }
}
