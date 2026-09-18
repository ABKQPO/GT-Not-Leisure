package com.science.gtnl.common.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;

public class GTNLItemRecord extends ItemRecord {

    public GTNLItemRecord(String recordName) {
        super(recordName);
        setTextureName(RESOURCE_ROOT_ID + ":record." + recordName);
        setUnlocalizedName("record");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(RESOURCE_ROOT_ID + ":" + recordName);
    }
}
