package com.science.gtnl.mixins.late.matterManipulator;

import java.util.Deque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.recursive_pineapple.matter_manipulator.common.building.PendingBlock;
import com.recursive_pineapple.matter_manipulator.common.building.PendingBuild;
import com.science.gtnl.common.wireless.WirelessConstructionSites;
import com.science.gtnl.common.wireless.WirelessConstructionSites.Site;

@Mixin(value = PendingBuild.class, remap = false)
public abstract class MixinPendingBuildWireless {

    @Shadow
    @Final
    private Deque<PendingBlock> pendingBlocks;

    @Unique
    private Site gtnl$site;

    @Unique
    private Object gtnl$task;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void gtnl$measureSite(CallbackInfo ci) {
        Site site = null;
        for (PendingBlock block : pendingBlocks) {
            site = site == null ? Site.of(block.worldId, block.x, block.y, block.z)
                : site.include(block.x, block.y, block.z);
        }
        gtnl$site = site;
        gtnl$task = new Object();
        WirelessConstructionSites.update(gtnl$task, true, site);
    }

    @Inject(method = "tryPlaceBlocks", at = @At("RETURN"))
    private void gtnl$building(ItemStack stack, EntityPlayer player, CallbackInfo ci) {
        WirelessConstructionSites.update(gtnl$task, !pendingBlocks.isEmpty(), gtnl$site);
    }
}
