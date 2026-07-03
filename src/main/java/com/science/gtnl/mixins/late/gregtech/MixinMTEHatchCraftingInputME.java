package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IMultiblockRecipeMap;
import com.science.gtnl.config.MainConfig;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.util.ReadableNumberConverter;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEHatchCraftingInputME.class, remap = false)
public abstract class MixinMTEHatchCraftingInputME extends MTEHatchInputBus
    implements ICraftingMedium, IMultiblockRecipeMap {

    @Shadow
    @Final
    private static int SLOT_CIRCUIT;

    @Shadow
    @Final
    private static int SLOT_MANUAL_START;

    public MixinMTEHatchCraftingInputME(int id, String name, String nameRegional, int tier) {
        super(id, name, nameRegional, tier);
    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void gtnl$overrideGetName(CallbackInfoReturnable<String> cir) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        var self = (MTEHatchCraftingInputME) (Object) this;

        if (self.hasCustomName()) {
            cir.setReturnValue(self.getCustomName());
            return;
        }

        StringBuilder name = new StringBuilder();

        if (mInventory[SLOT_CIRCUIT] != null) {
            name.append("gt_circuit_")
                .append(mInventory[SLOT_CIRCUIT].getItemDamage())
                .append('_');
        }

        if (gtnl$getRecipeMapName() != null) {
            name.append("extra_start_")
                .append(gtnl$getRecipeMapName())
                .append("_extra_end_");
        }

        if (mInventory[SLOT_MANUAL_START] != null) {
            ItemStack stack = mInventory[SLOT_MANUAL_START];
            Item item = stack.getItem();
            String registryName = GameRegistry.findUniqueIdentifierFor(item)
                .toString();

            name.append("extra_item_start_")
                .append(registryName)
                .append("@")
                .append(stack.getItemDamage());

            if (stack.hasDisplayName()) {
                name.append("{")
                    .append(stack.getDisplayName())
                    .append("}");
            }

            name.append("extra_item_end_");
        }

        if (getCrafterIcon() != null) {
            name.append(getCrafterIcon().getUnlocalizedName());
        } else {
            name.append("gt.blockmachines.")
                .append(mName)
                .append(".name");
        }

        cir.setReturnValue(name.toString());
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("inventory")) {
            NBTTagList inventory = tag.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inventory.tagCount(); ++i) {
                NBTTagCompound item = inventory.getCompoundTagAt(i);
                String name = item.getString("name");
                long amount = item.getLong("amount");
                currenttip.add(
                    name + ": "
                        + EnumChatFormatting.GOLD
                        + ReadableNumberConverter.INSTANCE.toWideReadableForm(amount)
                        + EnumChatFormatting.RESET);
            }
        }
        super.getWailaBody(itemStack, currenttip, accessor, config);
    }
}
