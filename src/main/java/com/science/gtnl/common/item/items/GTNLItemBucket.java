package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;

import org.apache.commons.lang3.StringUtils;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.BucketHandler;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTNLItemBucket extends ItemBucket {

    public static GTNLItemBucket create(Fluid fluid) {
        GTNLItemBucket bucket = new GTNLItemBucket(
            fluid.getBlock() != null ? fluid.getBlock() : Blocks.air,
            fluid.getName());
        bucket.init();

        FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.bucket));
        BucketHandler.instance.registerFluid(fluid.getBlock(), bucket);

        return bucket;
    }

    private String fluidName;

    public GTNLItemBucket(Block block, String fluidName) {
        super(block);
        this.fluidName = fluidName;
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        setContainerItem(Items.bucket);
        String str = StringUtils.capitalize(fluidName) + "Bucket";
        setUnlocalizedName(str);
        setTextureName(RESOURCE_ROOT_ID + ":" + str);
    }

    public void init() {
        GameRegistry.registerItem(this, StringUtils.capitalize(fluidName) + "Bucket");
    }
}
