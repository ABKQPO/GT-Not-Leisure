package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.sugar.Local;

import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTUtil;

@Mixin(value = GTUtil.class, remap = false)
public abstract class MixinGTUtil {

    @Shadow
    private static NBTTagList saveConfigurationToDataStick(EntityPlayer player, List<? extends MTEHatch> hatches) {
        return null;
    }

    @Shadow
    private static boolean checkCanLoadConfigurationFromDataStick(NBTTagList list, EntityPlayer player,
        List<? extends MTEHatch> hatches) {
        return false;
    }

    @Shadow
    private static boolean loadConfigurationFromDataStick(NBTTagList list, EntityPlayer player,
        List<? extends MTEHatch> hatches) {
        return false;
    }

    @Redirect(
        method = "saveMultiblockInputConfiguration",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;tagCount()I", ordinal = 3))
    private static int redirectTagCount(NBTTagList originalList, MTEMultiBlockBase controller, EntityPlayer player,
        @Local NBTTagCompound newTag) {
        int originalCount = originalList.tagCount();

        NBTTagList maintenanceList = saveConfigurationToDataStick(player, controller.mMaintenanceHatches);

        int maintenanceCount = 0;
        if (maintenanceList != null && maintenanceList.tagCount() > 0) {
            newTag.setTag("mMaintenanceHatches", maintenanceList);
            maintenanceCount = maintenanceList.tagCount();
        }

        return originalCount + maintenanceCount;
    }

    @Inject(
        method = "loadMultiblockInputConfiguration",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/NBTTagCompound;getTagList(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagList;",
            ordinal = 6),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onLoadMaintenanceHatches(MTEMultiBlockBase controller, EntityPlayer player,
        CallbackInfoReturnable<Boolean> cir, ItemStack dataOrb, NBTTagCompound tag) {
        if (tag.hasKey("mMaintenanceHatches")) {
            NBTTagList maintenanceList = tag.getTagList("mMaintenanceHatches", Constants.NBT.TAG_COMPOUND);
            if (checkCanLoadConfigurationFromDataStick(maintenanceList, player, controller.mMaintenanceHatches)) {
                loadConfigurationFromDataStick(maintenanceList, player, controller.mMaintenanceHatches);
            }
        }
    }
}
