package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.EQuantumComputerCPUStatus;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiCraftingCPUTable;
import appeng.container.implementations.CraftingCPUStatus;

@Mixin(value = GuiCraftingCPUTable.class, remap = false)
public abstract class MixinGuiCraftingCPUTable {

    @Unique
    private static final ResourceLocation GTNL$QUANTUM_COMPUTER_OVERLAY = new ResourceLocation(
        ScienceNotLeisure.MODID,
        "textures/gui/ecalculator_gui_2.png");

    private static final int CPU_CRAFTING_ICON_SIZE = 16;

    @WrapOperation(
        method = "drawFG",
        at = @At(value = "INVOKE", target = "Lappeng/client/gui/AEBaseGui;drawTexturedModalRect(IIIIII)V", ordinal = 0),
        require = 1)
    private void gtnl$drawQuantumComputerBackground(final AEBaseGui gui, final int x, final int y, final int textureX,
        final int textureY, final int width, final int height, final Operation<Void> original,
        @Local(name = "cpu") final CraftingCPUStatus cpu) {
        original.call(gui, x, y, textureX, textureY, width, height);

        final byte cpuType = cpu instanceof EQuantumComputerCPUStatus status ? status.ec$getCPUType()
            : EQuantumComputerCPUStatus.NORMAL_CPU;
        if (cpuType == EQuantumComputerCPUStatus.NORMAL_CPU) {
            return;
        }

        final TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        textureManager.bindTexture(GTNL$QUANTUM_COMPUTER_OVERLAY);
        gui.drawTexturedModalRect(x, y, 1, cpuType == EQuantumComputerCPUStatus.VIRTUAL_CPU ? 1 : 26, 67, 22);
    }

    @WrapOperation(
        method = "drawFG",
        at = @At(value = "INVOKE", target = "Lappeng/client/gui/AEBaseGui;drawTexturedModalRect(IIIIII)V", ordinal = 1),
        require = 1)
    private void gtnl$drawQuantumComputerBusyIcon(final AEBaseGui gui, final int x, final int y, final int textureX,
        final int textureY, final int width, final int height, final Operation<Void> original,
        @Local(name = "cpu") final CraftingCPUStatus cpu) {
        final byte cpuType = cpu instanceof EQuantumComputerCPUStatus status ? status.ec$getCPUType()
            : EQuantumComputerCPUStatus.NORMAL_CPU;
        if (cpu.isBusy() && cpuType != EQuantumComputerCPUStatus.NORMAL_CPU) {
            gtnl$drawQuantumComputerIcon(gui, x, y, cpuType);
            return;
        }

        original.call(gui, x, y, textureX, textureY, width, height);
    }

    @WrapOperation(
        method = "drawFG",
        at = @At(value = "INVOKE", target = "Lappeng/client/gui/AEBaseGui;drawItem(IILnet/minecraft/item/ItemStack;)V"),
        require = 1)
    private void gtnl$drawIdleQuantumComputerIcon(final AEBaseGui gui, final int x, final int y, final ItemStack stack,
        final Operation<Void> original, @Local(name = "cpu") final CraftingCPUStatus cpu) {
        final byte cpuType = cpu instanceof EQuantumComputerCPUStatus status ? status.ec$getCPUType()
            : EQuantumComputerCPUStatus.NORMAL_CPU;
        if (!cpu.isBusy() && cpuType != EQuantumComputerCPUStatus.NORMAL_CPU && x == 0 && y == 0) {
            gtnl$drawQuantumComputerIcon(gui, x, y, cpuType);
            gui.bindTexture("guis/states.png");
            return;
        }
        if (!cpu.isBusy() && cpuType != EQuantumComputerCPUStatus.NORMAL_CPU && x == 64 && y == 0) {
            gtnl$drawQuantumComputerParallelIcon(gui, x, y);
            gui.bindTexture("guis/states.png");
            return;
        }

        original.call(gui, x, y, stack);
    }

    @Unique
    private static void gtnl$drawQuantumComputerIcon(final AEBaseGui gui, final int x, final int y,
        final byte cpuType) {
        final TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        textureManager.bindTexture(GTNL$QUANTUM_COMPUTER_OVERLAY);
        gui.drawTexturedModalRect(
            x,
            y,
            cpuType == EQuantumComputerCPUStatus.VIRTUAL_CPU ? 0 : 34,
            124,
            CPU_CRAFTING_ICON_SIZE,
            CPU_CRAFTING_ICON_SIZE);
    }

    @Unique
    private static void gtnl$drawQuantumComputerParallelIcon(final AEBaseGui gui, final int x, final int y) {
        final TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        textureManager.bindTexture(GTNL$QUANTUM_COMPUTER_OVERLAY);
        gui.drawTexturedModalRect(x, y, 17, 124, CPU_CRAFTING_ICON_SIZE, CPU_CRAFTING_ICON_SIZE);
    }

    @WrapOperation(
        method = "drawFG",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I",
            ordinal = 0),
        require = 1)
    private int gtnl$offsetQuantumComputerName(final FontRenderer font, final String text, final int x, final int y,
        final int color, final Operation<Integer> original, @Local(name = "cpu") final CraftingCPUStatus cpu) {
        if (cpu instanceof EQuantumComputerCPUStatus status
            && status.ec$getCPUType() != EQuantumComputerCPUStatus.NORMAL_CPU) {
            return original.call(font, text, x + 8, y, color);
        }
        return original.call(font, text, x, y, color);
    }
}
