package com.science.gtnl.mixins.early.gregtech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.science.gtnl.api.ITileEntityTickAcceleration;
import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.helpers.ICustomNameObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.INonConsumedItemDisplay;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.CommonBaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.common.render.IMTERenderer;
import gregtech.crossmod.ae2.ChatComponentNonConsumedItemsSuffix;

@Mixin(value = BaseMetaTileEntity.class, remap = false)
public abstract class MixinBaseMetaTileEntity extends CommonBaseMetaTileEntity implements ITileEntityTickAcceleration {

    @Shadow
    protected MetaTileEntity mMetaTileEntity;

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        if (GTNLEarlyCoreMod.enableAprilFool || mMetaTileEntity instanceof IMTERenderer) {
            return AxisAlignedBB.getBoundingBox(
                this.xCoord - 1024,
                this.yCoord - 1024,
                this.zCoord - 1024,
                this.xCoord + 1024,
                this.yCoord + 1024,
                this.zCoord + 1024);
        }
        return super.getRenderBoundingBox();
    }

    @Override
    public IChatComponent getInterfaceNameSuffix() {
        IChatComponent nameSuffix = super.getInterfaceNameSuffix();

        if (mMetaTileEntity instanceof ICustomNameObject customNameObject && customNameObject.hasCustomName()) {
            return nameSuffix;
        }

        RecipeMap<?> recipeMap = gtnl$getHatchRecipeMap();
        if (recipeMap != null) {
            IChatComponent recipeMapSuffix = new ChatComponentText(" - ")
                .appendSibling(new ChatComponentTranslation(recipeMap.unlocalizedName));
            nameSuffix = nameSuffix == null ? recipeMapSuffix : nameSuffix.appendSibling(recipeMapSuffix);
        }

        List<ItemStack> extraItems = gtnl$getExtraItems(recipeMap);
        if (extraItems.isEmpty()) return nameSuffix;

        IChatComponent extraItemSuffix = new ChatComponentNonConsumedItemsSuffix(extraItems);
        return nameSuffix == null ? extraItemSuffix : nameSuffix.appendSibling(extraItemSuffix);
    }

    @Unique
    private RecipeMap<?> gtnl$getHatchRecipeMap() {
        if (mMetaTileEntity instanceof MTEHatchInput inputHatch) return inputHatch.mRecipeMap;
        if (mMetaTileEntity instanceof MTEHatchInputBus inputBus) return inputBus.mRecipeMap;
        return null;
    }

    @Unique
    private List<ItemStack> gtnl$getExtraItems(RecipeMap<?> recipeMap) {
        if (!(mMetaTileEntity instanceof MTEHatchInput) && !(mMetaTileEntity instanceof MTEHatchInputBus)) {
            return Collections.emptyList();
        }

        List<ItemStack> extraItems = new ArrayList<>();
        for (int slot = 0; slot < mMetaTileEntity.getSizeInventory(); slot++) {
            ItemStack stack = mMetaTileEntity.getStackInSlot(slot);
            if (ItemUtils.isExtraItem(stack) && !INonConsumedItemDisplay.isDisplayableItem(recipeMap, stack)) {
                extraItems.add(stack.copy());
            }
        }
        return extraItems;
    }
}
