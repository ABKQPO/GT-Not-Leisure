package com.science.gtnl.mixins.early.minecraft;

import java.util.Set;

import net.minecraft.client.resources.SimpleReloadableResourceManager;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Sets;

@Mixin(value = SimpleReloadableResourceManager.class, remap = true)
public class MixinSimpleReloadableResourceManager {

    @Shadow
    @Final
    @Mutable
    private Set<String> setResourceDomains;

    @Redirect(
        method = "<init>",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/resources/SimpleReloadableResourceManager;setResourceDomains:Ljava/util/Set;",
            opcode = Opcodes.PUTFIELD))
    private void redirectSetResourceDomains(SimpleReloadableResourceManager instance, Set<String> value) {
        this.setResourceDomains = Sets.newLinkedHashSet();
    }
}
