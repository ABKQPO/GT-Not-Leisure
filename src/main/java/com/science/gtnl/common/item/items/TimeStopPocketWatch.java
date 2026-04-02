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
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import lombok.Setter;

public class TimeStopPocketWatch extends Item {

    @Getter
    @Setter
    public static boolean timeStopped = false;

    private boolean playSound = false;

    public TimeStopPocketWatch() {
        this.setUnlocalizedName("TimeStopPocketWatch");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "TimeStopPocketWatch");
        this.setMaxStackSize(1);
        GameRegistry.registerItem(this, getUnlocalizedName());
        GTNLItemList.TimeStopPocketWatch.set(new ItemStack(this, 1));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        if (nbt.getBoolean("WatchActive")) {
            if (world.isRemote) {
                EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
                if (renderer instanceof CustomEntityRenderer customEntityRenderer) {
                    customEntityRenderer.resetShader();
                }
            } else {
                nbt.setBoolean("WatchActive", false);
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
                    player.worldObj
                        .playSoundAtEntity(player, ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "time.stop", 1.0F, 1.0F);
                    playSound = true;
                }
                nbt.setBoolean("WatchActive", true);
            }
            setTimeStopped(true);
        }

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean("WatchActive");
    }
}
