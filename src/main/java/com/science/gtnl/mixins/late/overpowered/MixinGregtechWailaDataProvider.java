package com.science.gtnl.mixins.late.overpowered;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.recipes.ChanceBonusManager;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.StringUtils;
import gregtech.crossmod.waila.GregtechTEWailaDataProvider;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = GregtechTEWailaDataProvider.class, remap = false)
public class MixinGregtechWailaDataProvider {

    @Inject(
        method = "getNBTData(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;III)Lnet/minecraft/nbt/NBTTagCompound;",
        at = @At("RETURN"),
        cancellable = true)
    private void injectGetNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (ModList.Overpowered.isModLoaded() || !MainConfig.machine.enableRecipeOutputChance) return;
        if (tile instanceof BaseMetaTileEntity baseMetaTileEntity) {
            IMetaTileEntity meta = baseMetaTileEntity.getMetaTileEntity();
            if (meta instanceof MTEMultiBlockBase mte) {
                GTRecipe recipe = ChanceBonusManager.customProvider.getRecipeForMachine(mte);
                if (recipe != null) {
                    int tier = GTUtility.getTier(mte.getMaxInputVoltage());
                    int baseTier = GTUtility.getTier(recipe.mEUt);
                    double bonus = tier <= baseTier ? 0.0 : (tier - baseTier) * MainConfig.machine.recipeOutputChance;
                    NBTTagCompound chanceTag = new NBTTagCompound();
                    chanceTag.setInteger("tier", tier);
                    chanceTag.setInteger("baseTier", baseTier);
                    chanceTag.setDouble("bonus", bonus);
                    tag.setTag("outputChanceData", chanceTag);
                    cir.setReturnValue(tag);
                }
            }
        }
    }

    @Inject(method = "getWailaBody", at = @At("RETURN"), cancellable = true)
    private void injectGetWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config, CallbackInfoReturnable<List<String>> cir) {
        if (ModList.Overpowered.isModLoaded() || !MainConfig.machine.enableRecipeOutputChance) return;
        NBTTagCompound tag = accessor.getNBTData();

        if (tag.hasKey("outputChanceData")) {
            NBTTagCompound chanceTag = tag.getCompoundTag("outputChanceData");

            int tier = chanceTag.getInteger("tier");
            int baseTier = chanceTag.getInteger("baseTier");
            double bonus = chanceTag.getDouble("bonus");

            String debugMessage = String.format(
                StatCollector.translateToLocal("Info_VoltageChanceBonus_00"),
                bonus,
                StringUtils.voltageTooltipFormatted(tier),
                StringUtils.voltageTooltipFormatted(baseTier));

            currenttip.add(debugMessage);
            cir.setReturnValue(currenttip);
        }
    }

}
