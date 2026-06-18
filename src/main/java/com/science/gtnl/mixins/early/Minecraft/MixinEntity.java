package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.items.TimeStopPocketWatch;

import Forge.NullPointerException;
import gregtech.api.objects.XSTR;

@Mixin(value = Entity.class, remap = true)
public class MixinEntity {

    @Inject(method = "moveEntity", at = @At("HEAD"), cancellable = true)
    public void mixin$moveEntity(double x, double y, double z, CallbackInfo ci) {
        if (TimeStopPocketWatch.isTimeStopped() && !(((Entity) ((Object) this)) instanceof EntityPlayer)) {
            ci.cancel();
        }
    }

    @Inject(method = "addEntityCrashInfo", at = @At("HEAD"), cancellable = true)
    public void addEntityCrashInfo(CrashReportCategory category, CallbackInfo ci) {
        Entity entity = (Entity) ((Object) this);

        if (entity instanceof NullPointerException) {

            category.addCrashSection("Entity Type", gtnl$randomError() + " (" + gtnl$randomError() + ")");
            category.addCrashSection("Entity ID", gtnl$randomError());
            category.addCrashSection("Entity Name", gtnl$randomError());
            category.addCrashSection(
                "Entity's Exact location",
                gtnl$randomError() + ", " + gtnl$randomError() + ", " + gtnl$randomError());
            category.addCrashSection(
                "Entity's Block location",
                "World: (" + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + "), "
                    + "Chunk: (at "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + " in "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + "; contains blocks "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + " to "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + "), "
                    + "Region: ("
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + "; contains chunks "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + " to "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ", blocks "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + " to "
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ","
                    + gtnl$randomError()
                    + ")");
            category.addCrashSection(
                "Entity's Momentum",
                gtnl$randomError() + ", " + gtnl$randomError() + ", " + gtnl$randomError());

            ci.cancel();
        }
    }

    @Unique
    private String[] gtnl$errors = { "ArrayIndexOutOfBoundsException", "ArrayStoreException", "BootstrapMethodError",
        "IllegalThreadStateException", "IllegalArgumentException", "ArithmeticException", "ClassCastException",
        "IllegalStateException", "UnsupportedOperationException", "NumberFormatException", "IndexOutOfBoundsException",
        "NullPointerException", "NoSuchMethodException", "ClassNotFoundException", "ConcurrentModificationException",
        "StackOverflowError", "OutOfMemoryError", "RuntimeException", "SecurityException",
        "StringIndexOutOfBoundsException", "ThreadDeath", "TypeNotPresentException", "UnknownError",
        "VirtualMachineError", "VerifyError", "AbstractMethodError" };

    @Unique
    private String gtnl$randomError() {
        return "java.lang." + gtnl$errors[new XSTR().nextInt(gtnl$errors.length)];
    }
}
