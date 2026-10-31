package com.science.gtnl.common.item.items;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@EventBusSubscriber(phase = Phase.PRE)
public class BucketHandler {

    public static final Map<Block, Item> BUCKETS = new Object2ObjectOpenHashMap<>();

    public static void registerFluid(Block fluidBlock, Item fullBucket) {
        BUCKETS.put(fluidBlock, fullBucket);
    }

    @SubscribeEvent
    public static void onBucketFill(FillBucketEvent event) {
        // no instanceof check, someone may subclass the vanilla bucket
        if (event.current != null && event.current.getItem() == Items.bucket && event.current.stackSize > 0) {
            ItemStack res = getFilledBucket(event.world, event.target);
            if (res != null) {
                event.result = res;
                event.setResult(Result.ALLOW);
            }
        }
    }

    private static ItemStack getFilledBucket(World world, MovingObjectPosition pos) {

        Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
        Item bucket = BUCKETS.get(block);
        if (bucket != null && world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) {
            world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
            return new ItemStack(bucket);
        } else {
            return null;
        }
    }

    // @SubscribeEvent
    // public void addFuelTooltip(ItemTooltipEvent evt) {
    // ItemStack stack = evt.itemStack;
    // if(stack == null) {
    // return;
    // }
    //
    // if(Config.addFuelTooltipsToAllFluidContainers || stack.getItem() instanceof ItemBucketEio) {
    // FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
    // if(fluidStack == null) {
    // return;
    // }
    // addTooltipForFluid(evt.toolTip, fluidStack.getFluid());
    // }
    // }
    //
    // public void addTooltipForFluid(List list, Fluid fluid) {
    // if(fluid != null) {
    // Fuel fuel = IronEngineFuel.getFuelForFluid(fluid);
    // if(fuel != null) {
    // list.add(EnderIO.lang.localize("fuel.tooltip.heading"));
    // list.add(EnumChatFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerPerTick(fuel.powerPerCycle));
    // list.add(EnumChatFormatting.ITALIC + " " + fuel.totalBurningTime + " " +
    // EnderIO.lang.localize("fuel.tooltip.burnTime"));
    // }
    // }
    // }

}
