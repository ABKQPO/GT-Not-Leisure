package com.science.gtnl.mixins.late.tecTech;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.mixinHelper.IResearchStationMarker;

import gregtech.api.util.GTUtility;
import gregtech.api.util.item.PhantomSingleSlotItemStackHandler;
import tectech.recipe.TecTechRecipeMaps;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

@Mixin(value = MTEResearchStation.class, remap = false)
public abstract class MixinMTEResearchStation extends TTMultiblockBase implements IResearchStationMarker {

    @Unique
    public ItemStack[] gtnl$lockedItems = new ItemStack[1];

    @Unique
    public IItemHandlerModifiable gtnl$lockedInventoryHandler = new PhantomSingleSlotItemStackHandler(
        () -> gtnl$lockedItems[0],
        this::gtnl$setLockedItem);

    public MixinMTEResearchStation(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (gtnl$lockedItems[0] != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setInteger("Slot", 0);
            gtnl$lockedItems[0].writeToNBT(itemTag);
            aNBT.setTag("lockedItem", itemTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("lockedItem", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound itemTag = aNBT.getCompoundTag("lockedItem");
            gtnl$setLockedItem(ItemStack.loadItemStackFromNBT(itemTag));
        }
    }

    @Override
    public IItemHandlerModifiable gtnl$getResearchMarkerInventoryHandler() {
        return gtnl$lockedInventoryHandler;
    }

    @Unique
    private void gtnl$setLockedItem(ItemStack item) {
        gtnl$lockedItems[0] = item == null ? null : GTUtility.copyAmount(1, item);
    }

    @Redirect(
        method = "findResearchStationRecipe",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lgregtech/api/util/GTUtility;areStacksEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Z)Z"))
    private boolean gtnl$filterLockedResearchRecipe(ItemStack stack1, ItemStack stack2, boolean matchNBT,
        @Local(name = "assRecipe") TecTechRecipeMaps.TTResearchStationALRecipe assRecipe) {
        if (gtnl$lockedItems[0] != null
            && !GTUtility.areStacksEqual(assRecipe.mOutput, gtnl$lockedItems[0], matchNBT)) {
            return false;
        }

        return GTUtility.areStacksEqual(stack1, stack2, matchNBT);
    }
}
