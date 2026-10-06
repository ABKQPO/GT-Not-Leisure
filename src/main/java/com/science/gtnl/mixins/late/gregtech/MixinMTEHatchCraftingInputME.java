package com.science.gtnl.mixins.late.gregtech;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import appeng.api.interfaces.IInterfaceNameProvider;
import appeng.api.util.IInterfaceViewable;
import appeng.util.ReadableNumberConverter;
import gregtech.api.metatileentity.CommonBaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME.PatternSlot;
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
        MTEHatchCraftingInputME hatch = (MTEHatchCraftingInputME) (Object) this;
        tag.removeTag("nameLines");

        ItemStack display = hatch.getDisplayRep();
        IChatComponent suffix = !hatch.hasCustomName()
            && hatch.getBaseMetaTileEntity() instanceof IInterfaceNameProvider provider
                ? provider.getInterfaceNameSuffix()
                : null;
        NBTTagList manualItems = new NBTTagList();
        if (!hatch.hasCustomName()) {
            for (int slot = MTEHatchCraftingInputME.SLOT_MANUAL_START; slot < hatch.getSizeInventory(); slot++) {
                ItemStack stack = hatch.getStackInSlot(slot);
                if (stack != null) manualItems.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
        }
        InterfaceNameLocalization.writeName(tag, "gtnl$crafting", hatch.getRawName(), suffix, display);
        tag.removeTag("gtnl$craftingManualItems");
        if (manualItems.tagCount() > 0) tag.setTag("gtnl$craftingManualItems", manualItems);

        Map<String, NBTTagCompound> entries = new HashMap<>();
        Map<String, Long> amounts = new HashMap<>();
        Iterator<PatternSlot<MTEHatchCraftingInputME>> inventories = hatch.inventories();
        while (inventories.hasNext()) {
            PatternSlot<MTEHatchCraftingInputME> slot = inventories.next();
            for (ItemStack item : slot.getItemInputs()) {
                if (item == null || item.stackSize <= 0) continue;
                ItemStack displayItem = item.copy();
                displayItem.stackSize = 1;
                NBTTagCompound entry = displayItem.writeToNBT(new NBTTagCompound());
                String key = "item:" + entry;
                entries.putIfAbsent(key, entry);
                amounts.merge(key, (long) item.stackSize, Long::sum);
            }
            for (FluidStack fluid : slot.getFluidInputs()) {
                if (fluid == null || fluid.amount <= 0) continue;
                FluidStack displayFluid = fluid.copy();
                displayFluid.amount = 1;
                NBTTagCompound entry = displayFluid.writeToNBT(new NBTTagCompound());
                String key = "fluid:" + entry;
                entries.putIfAbsent(key, entry);
                amounts.merge(key, (long) fluid.amount, Long::sum);
            }
        }

        NBTTagList inventory = new NBTTagList();
        for (Map.Entry<String, Long> amount : amounts.entrySet()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setByte(
                "type",
                (byte) (amount.getKey()
                    .startsWith("item:") ? 0 : 1));
            entry.setTag("entry", entries.get(amount.getKey()));
            entry.setLong("amount", amount.getValue());
            inventory.appendTag(entry);
        }
        tag.removeTag("inventory");
        tag.setTag("gtnl$craftingInventory", inventory);
    }

    @Inject(method = "getWailaBody", at = @At("HEAD"))
    private void gtnl$addLocalizedWailaName(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config, CallbackInfo callbackInfo) {
        NBTTagCompound tag = accessor.getNBTData();
        String name = InterfaceNameLocalization.localizeName(tag, "gtnl$crafting");
        if (!name.isEmpty()) currenttip.add(EnumChatFormatting.AQUA + name + EnumChatFormatting.RESET);

        NBTTagList manualItems = tag.getTagList("gtnl$craftingManualItems", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < manualItems.tagCount(); i++) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(manualItems.getCompoundTagAt(i));
            if (stack != null) {
                currenttip.add(
                    EnumChatFormatting.AQUA + "  "
                        + CommonBaseMetaTileEntity.getShortItemDisplayName(stack)
                        + EnumChatFormatting.RESET);
            }
        }
    }

    @Inject(method = "getWailaBody", at = @At("TAIL"))
    private void gtnl$addLocalizedWailaInventory(ItemStack itemStack, List<String> currenttip,
        IWailaDataAccessor accessor, IWailaConfigHandler config, CallbackInfo callbackInfo) {
        NBTTagList inventory = accessor.getNBTData()
            .getTagList("gtnl$craftingInventory", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < inventory.tagCount(); i++) {
            NBTTagCompound entry = inventory.getCompoundTagAt(i);
            NBTTagCompound stackTag = entry.getCompoundTag("entry");
            String name;
            if (entry.getByte("type") == 0) {
                ItemStack stack = ItemStack.loadItemStackFromNBT(stackTag);
                name = stack == null ? "" : stack.getDisplayName();
            } else {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(stackTag);
                name = fluid == null ? "" : fluid.getLocalizedName();
            }
            if (!name.isEmpty()) {
                currenttip.add(
                    name + ": "
                        + EnumChatFormatting.GOLD
                        + ReadableNumberConverter.INSTANCE.toWideReadableForm(entry.getLong("amount"))
                        + EnumChatFormatting.RESET);
            }
        }
    }
}
