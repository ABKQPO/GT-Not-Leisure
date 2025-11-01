package com.science.gtnl.mixins.late.TecTech;

import static gregtech.api.recipe.RecipeMaps.*;
import static gregtech.api.util.GTUtility.*;
import static net.minecraft.util.StatCollector.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.llamalad7.mixinextras.sugar.Local;

import gregtech.api.gui.widgets.PhantomItemButton;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

@Mixin(value = MTEResearchStation.class, remap = false)
public abstract class MixinMTEResearchStation extends TTMultiblockBase {

    @Unique
    public ItemStack[] lockedItems = new ItemStack[1];

    @Unique
    public IItemHandlerModifiable lockedInventoryHandler = new ItemStackHandler(lockedItems);

    public MixinMTEResearchStation(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (lockedItems[0] != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setInteger("Slot", 0);
            lockedItems[0].writeToNBT(itemTag);
            aNBT.setTag("lockedItem", itemTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("lockedItem", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound itemTag = aNBT.getCompoundTag("lockedItem");
            lockedItems[0] = ItemStack.loadItemStackFromNBT(itemTag);
        }
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        builder.widget(
            SlotGroup.ofItemHandler(lockedInventoryHandler, 1)
                .startFromSlot(0)
                .endAtSlot(0)
                .background(PhantomItemButton.FILTER_BACKGROUND)
                .phantom(true)
                .slotCreator(index -> new BaseSlot(lockedInventoryHandler, index, true))
                .build()
                .setSize(18, 18)
                .setPos(173, 96));
    }

    @Redirect(
        method = "checkProcessing_EM",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lgregtech/api/util/GTUtility;areStacksEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Z)Z"))
    private boolean redirectCheckProcessingAreStacksEqual0(ItemStack stack1, ItemStack stack2, boolean matchNBT,
        @Local(name = "assRecipe") GTRecipe.RecipeAssemblyLine assRecipe) {
        if (lockedItems[0] != null && !GTUtility.areStacksEqual(assRecipe.mOutput, lockedItems[0], matchNBT)) {
            return false;
        }

        return GTUtility.areStacksEqual(stack1, stack2, matchNBT);
    }

    @Redirect(
        method = "checkProcessing_EM",
        at = @At(
            ordinal = 2,
            value = "INVOKE",
            target = "Lgregtech/api/util/GTUtility;areStacksEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Z)Z"))
    private boolean redirectCheckProcessingAreStacksEqual2(ItemStack stack1, ItemStack stack2, boolean matchNBT,
        @Local(name = "assRecipe") GTRecipe.RecipeAssemblyLine assRecipe) {
        if (lockedItems[0] != null && !GTUtility.areStacksEqual(assRecipe.mOutput, lockedItems[0], matchNBT)) {
            return false;
        }

        return GTUtility.areStacksEqual(stack1, stack2, matchNBT);
    }
}
