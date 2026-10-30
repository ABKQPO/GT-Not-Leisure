package com.gtnllib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import com.gtnllib.client.fx.world.FxEffects;
import com.gtnllib.client.fx.world.FxMoteRenderer;
import com.gtnllib.client.hud.HudLayout;
import com.gtnllib.client.hud.HudOverlay;
import com.gtnllib.client.shader.ShaderEffect;
import com.gtnllib.client.text.EffectTextRenderer;
import com.gtnllib.client.text.effect.BuiltinTextEffects;
import com.gtnllib.client.text.preview.TextEffectPreviewCommand;
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.gui.theme.PaletteThemes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GTNLLibClient {

    public static void preInit() {
        FxEffects.register();
        GTNLLibConfig.register();
        connectConfig();
        HudOverlay.register();
        MinecraftForge.EVENT_BUS.register(new ShaderEffect.Reloader());
        MinecraftForge.EVENT_BUS.register(new PaletteThemes.Reloader());
        BuiltinTextEffects.register();
        ((IReloadableResourceManager) Minecraft.getMinecraft()
            .getResourceManager()).registerReloadListener(EffectTextRenderer.INSTANCE);
        ClientCommandHandler.instance.registerCommand(new TextEffectPreviewCommand());
    }

    public static void connectConfig() {
        PaletteThemes.chooseWith(() -> GTNLLibConfig.theme);
        PaletteThemes.onRegister(() -> GTNLLibConfig.listThemes(PaletteThemes.ids()));
        HudLayout.settings(() -> GTNLLibConfig.enableHud, () -> GTNLLibConfig.hudScale);
        FxMoteRenderer.limitWith(() -> GTNLLibConfig.maxWorldMotes);
        EffectTextRenderer.enabledWith(() -> GTNLLibConfig.enableTextEffects);
    }
}
