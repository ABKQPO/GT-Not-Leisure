package com.science.gtnl.mixins.late.Overpowered;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.recipes.ChanceBonusManager;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.items.MetaBaseItem;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.StringUtils;
import gregtech.common.items.behaviors.BehaviourScanner;

@Mixin(value = BehaviourScanner.class, remap = false)
public class MixinBehaviourScanner {

    @Inject(
        method = "onItemUseFirst(Lgregtech/api/items/MetaBaseItem;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIILnet/minecraftforge/common/util/ForgeDirection;FFF)Z",
        at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;size()I", shift = At.Shift.BEFORE))
    private void GTNotLeisure$addScanInfo(MetaBaseItem aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld,
        int aX, int aY, int aZ, ForgeDirection side, float hitX, float hitY, float hitZ,
        CallbackInfoReturnable<Boolean> cir, @Local ArrayList<String> tList) {
        if (ModList.Overpowered.isModLoaded() || !MainConfig.enableRecipeOutputChance) return;
        TileEntity tile = aWorld.getTileEntity(aX, aY, aZ);
        if (!(tile instanceof BaseMetaTileEntity baseMetaTileEntity)) {
            return;
        }

        IMetaTileEntity meta = baseMetaTileEntity.getMetaTileEntity();
        if (!(meta instanceof MTEMultiBlockBase mte)) {
            return;
        }

        GTRecipe recipe = ChanceBonusManager.customProvider.getRecipeForMachine(mte);
        if (recipe == null) {
            return;
        }

        int tier = GTUtility.getTier(mte.getMaxInputVoltage());
        int baseTier = GTUtility.getTier(recipe.mEUt);
        double bonus = tier <= baseTier ? 0.0 : (tier - baseTier) * MainConfig.recipeOutputChance;

        String debugMessage = String.format(
            StatCollector.translateToLocal("Info_VoltageChanceBonus_00"),
            bonus,
            StringUtils.voltageTooltipFormatted(tier),
            StringUtils.voltageTooltipFormatted(baseTier));

        tList.add(debugMessage);
    }
}
