package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.reavaritia.client.render.CustomEntityRenderer;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TimeStopPocketWatch extends Item {

    public static boolean TIME_STOPPED = false;
    public static final String WATCH_ACTIVE_TAG = "WatchActive";

    private boolean playSound = false;

    public TimeStopPocketWatch() {
        this.setUnlocalizedName("TimeStopPocketWatch");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "time_stop_pocket_watch");
        this.setMaxStackSize(1);
        GameRegistry.registerItem(this, "time_stop_pocket_watch");
        GTNLItemList.TimeStopPocketWatch.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.item.time_stop_pocket_watch";
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        if (nbt.getBoolean(WATCH_ACTIVE_TAG)) {
            if (world.isRemote) {
                EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
                if (renderer instanceof CustomEntityRenderer customEntityRenderer) {
                    customEntityRenderer.resetShader();
                }
            } else {
                nbt.setBoolean(WATCH_ACTIVE_TAG, false);
                playSound = false;
            }
            setTimeStopped(false);
        } else {
            if (player.worldObj.isRemote) {
                EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
                if (renderer instanceof CustomEntityRenderer customEntityRenderer) {
                    customEntityRenderer.activateDesaturateShader(16);
                }
            } else {
                if (!playSound) {
                    player.worldObj.playSoundAtEntity(player, RESOURCE_ROOT_ID + ":" + "time.stop", 1.0F, 1.0F);
                    playSound = true;
                }
                nbt.setBoolean(WATCH_ACTIVE_TAG, true);
            }
            setTimeStopped(true);
        }

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean(WATCH_ACTIVE_TAG);
    }

    public static boolean isTimeStopped() {
        return TIME_STOPPED;
    }

    public static void setTimeStopped(boolean timeStopped) {
        TIME_STOPPED = timeStopped;
    }
}
