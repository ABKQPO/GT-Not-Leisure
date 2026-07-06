package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;

import com.science.gtnl.api.mixinHelper.IMultiblockRecipeMap;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.Utils;

import appeng.api.networking.crafting.ICraftingMedium;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputSlave;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEHatchCraftingInputSlave.class, remap = false)
public abstract class MixinMTEHatchCraftingInputSlave extends MTEHatchInputBus
    implements ICraftingMedium, IMultiblockRecipeMap {

    public MixinMTEHatchCraftingInputSlave(int id, String name, String nameRegional, int tier) {
        super(id, name, nameRegional, tier);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        currenttip.add((tag.getBoolean("linked") ? "Linked" : "Not linked"));

        if (tag.hasKey("masterX")) {
            currenttip.add(
                "Bound to " + tag
                    .getInteger("masterX") + ", " + tag.getInteger("masterY") + ", " + tag.getInteger("masterZ"));
        }

        if (tag.hasKey("masterName")) {
            currenttip.add(
                EnumChatFormatting.GOLD
                    + (MainConfig.machine.enableHatchInterfaceTerminalEnhance
                        ? Utils.getExtraInterfaceName(tag.getString("masterName"))
                        : tag.getString("masterName"))
                    + EnumChatFormatting.RESET);
        }

        super.getWailaBody(itemStack, currenttip, accessor, config);
    }
}
