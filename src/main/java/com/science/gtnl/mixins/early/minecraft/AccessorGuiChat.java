package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiChat.class, remap = true)
public interface AccessorGuiChat {

    @Accessor("inputField")
    GuiTextField getInputField();

    @Accessor("inputField")
    void setInputField(GuiTextField textField);
}
