package com.science.gtnl.mixins.late.BetterQuesting;

import java.io.File;

import net.minecraft.command.ICommandSender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hfstudio.bqapi.runtime.BQReinjector;

import betterquesting.commands.admin.QuestCommandDefaults;
import cpw.mods.fml.common.FMLCommonHandler;

@Mixin(value = QuestCommandDefaults.class, remap = false)
public abstract class MixinQuestCommandDefaults {

    @Inject(method = "load(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;Ljava/io/File;Z)V", at = @At("TAIL"))
    private static void hfstudio$bqapi$afterLoad(ICommandSender sender, String databaseName, File dataDir,
        boolean loadWorldSettings, CallbackInfo ci) {
        BQReinjector.reinject(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance());
    }

    @Inject(
        method = "loadLegacy(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;Ljava/io/File;Z)V",
        at = @At("TAIL"))
    private static void hfstudio$bqapi$afterLoadLegacy(ICommandSender sender, String databaseName, File legacyFile,
        boolean loadWorldSettings, CallbackInfo ci) {
        BQReinjector.reinject(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance());
    }
}
