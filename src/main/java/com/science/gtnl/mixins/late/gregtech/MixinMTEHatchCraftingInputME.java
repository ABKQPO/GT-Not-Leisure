package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.me.CraftingInputMEWaila;

import appeng.api.util.IInterfaceViewable;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEHatchCraftingInputME.class, remap = false)
public abstract class MixinMTEHatchCraftingInputME extends MTEHatchInputBus implements IInterfaceViewable {

    public MixinMTEHatchCraftingInputME(int id, String name, String nameRegional, int tier) {
        super(id, name, nameRegional, tier);
    }

    public boolean handlesOwnInterfaceName() {
        return true;
    }

    @Override
    public ItemStack getDisplayRep() {
        MTEHatchCraftingInputME hatch = (MTEHatchCraftingInputME) (Object) this;
        ItemStack crafterIcon = hatch.getCrafterIcon();
        ItemStack display = crafterIcon != null ? crafterIcon : hatch.getSelfRep();
        if (display == null || !hatch.hasCustomName()) return display;
        ItemStack namedDisplay = display.copy();
        namedDisplay.setStackDisplayName(hatch.getCustomName());
        return namedDisplay;
    }

    @Inject(method = "getRawName", at = @At("RETURN"), cancellable = true)
    private void gtnl$useUntranslatedMachineName(CallbackInfoReturnable<String> callbackInfo) {
        MTEHatchCraftingInputME hatch = (MTEHatchCraftingInputME) (Object) this;
        if (!hatch.hasCustomName() && hatch.getCrafterIcon() == null) {
            callbackInfo.setReturnValue(hatch.getLocalNameKey());
        }
    }

    @Inject(method = "getWailaNBTData", at = @At("TAIL"))
    private void gtnl$writeLocalizableWailaData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world,
        int x, int y, int z, CallbackInfo callbackInfo) {
        CraftingInputMEWaila.writeData((MTEHatchCraftingInputME) (Object) this, tag);
    }

    @Inject(method = "getWailaBody", at = @At("HEAD"))
    private void gtnl$addLocalizedWailaName(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config, CallbackInfo callbackInfo) {
        CraftingInputMEWaila.addName(accessor.getNBTData(), currenttip);
    }

    @Inject(method = "getWailaBody", at = @At("TAIL"))
    private void gtnl$addLocalizedWailaInventory(ItemStack itemStack, List<String> currenttip,
        IWailaDataAccessor accessor, IWailaConfigHandler config, CallbackInfo callbackInfo) {
        CraftingInputMEWaila.addInventory(accessor.getNBTData(), currenttip);
    }
}
