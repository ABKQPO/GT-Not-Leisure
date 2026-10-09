package com.science.gtnl.wirelesstest;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

public final class WirelessTestTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinExtrasBootstrap.init();
        MixinEnvironment.getDefaultEnvironment()
            .setSide(MixinEnvironment.Side.SERVER);
        Mixins.addConfiguration("mixins.gtnl.wireless-test.json");
        String compatibility = System.getProperty("gtnl.wireless.compatMixin");
        if (compatibility != null) Mixins.addConfiguration(compatibility);
    }

    @Override
    public String getLaunchTarget() {
        // The Mixin tweaker discovers dependency manifests during injection, after our injection callback.
        Mixins.getConfigs()
            .removeIf(
                config -> !config.getName()
                    .equals("mixins.gtnl.wireless-test.json")
                    && !config.getName()
                        .equals(System.getProperty("gtnl.wireless.compatMixin")));
        return "com.science.gtnl.wirelessverify.WirelessMixinTarget";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
