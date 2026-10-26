package com.science.gtnl.loader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;

import com.science.gtnl.client.text.EffectTextRenderer;
import com.science.gtnl.client.text.effect.BuiltinTextEffects;
import com.science.gtnl.client.text.preview.TextEffectPreviewCommand;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TextEffectLoader implements IContentLoader {

    @Override
    public String name() {
        return "texteffect";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        BuiltinTextEffects.register();
        ((IReloadableResourceManager) Minecraft.getMinecraft()
            .getResourceManager()).registerReloadListener(EffectTextRenderer.INSTANCE);
        ClientCommandHandler.instance.registerCommand(new TextEffectPreviewCommand());
    }
}
