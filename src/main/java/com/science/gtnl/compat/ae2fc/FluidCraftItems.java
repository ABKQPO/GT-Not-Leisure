package com.science.gtnl.compat.ae2fc;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidDrop;
import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.inventory.InventoryHandler;
import com.glodblock.github.inventory.item.IWirelessTerminal;
import com.glodblock.github.util.BlockPos;
import com.glodblock.github.util.Util;

import appeng.api.networking.security.IActionHost;
import appeng.core.sync.GuiBridge;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

public class FluidCraftItems {

    public static boolean isFluidItem(ItemStack stack) {
        return stack != null && Mods.AE2FluidCraft.isModLoaded() && fluidItem(stack);
    }

    public static FluidStack fluidIn(ItemStack stack) {
        return isFluidItem(stack) ? fluid(stack) : null;
    }

    public static boolean openWirelessCraftAmount(IActionHost host, EntityPlayerMP player) {
        return Mods.AE2FluidCraft.isModLoaded() && openCraftAmount(host, player);
    }

    @Optional.Method(modid = "ae2fc")
    private static boolean fluidItem(ItemStack stack) {
        return stack.getItem() instanceof ItemFluidPacket || stack.getItem() instanceof ItemFluidDrop;
    }

    @Optional.Method(modid = "ae2fc")
    private static FluidStack fluid(ItemStack stack) {
        return stack.getItem() instanceof ItemFluidPacket ? ItemFluidPacket.getFluidStack(stack)
            : ItemFluidDrop.getFluidStack(stack);
    }

    @Optional.Method(modid = "ae2fc")
    private static boolean openCraftAmount(IActionHost host, EntityPlayerMP player) {
        if (!(host instanceof IWirelessTerminal terminal)) return false;
        InventoryHandler.openGui(
            player,
            player.worldObj,
            new BlockPos(terminal.getInventorySlot(), Util.GuiHelper.encodeType(0, Util.GuiHelper.GuiType.ITEM), 0),
            ForgeDirection.UNKNOWN,
            GuiBridge.GUI_CRAFTING_AMOUNT);
        return true;
    }
}
