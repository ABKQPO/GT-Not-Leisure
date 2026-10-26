package com.science.gtnl.mixins.late.matterManipulator;

import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.recursive_pineapple.matter_manipulator.common.building.BlockAnalyzer.IBlockApplyContext;
import com.recursive_pineapple.matter_manipulator.common.building.PendingBlock;
import com.recursive_pineapple.matter_manipulator.common.items.manipulator.Transform;
import com.science.gtnl.common.wireless.IWirelessCopiedLink;
import com.science.gtnl.common.wireless.WirelessCopiedLinks;
import com.science.gtnl.common.wireless.WirelessCopiedLinks.CopiedLink;

@Mixin(value = PendingBlock.class, remap = false)
public abstract class MixinPendingBlockWireless implements IWirelessCopiedLink {

    @Unique
    private transient Map<ForgeDirection, CopiedLink> gtnl$copiedLinks;

    @Override
    public Map<ForgeDirection, CopiedLink> gtnl$copiedLinks() {
        return gtnl$copiedLinks;
    }

    @Override
    public void gtnl$setCopiedLinks(Map<ForgeDirection, CopiedLink> links) {
        gtnl$copiedLinks = links;
    }

    @Inject(method = "analyze", at = @At("RETURN"))
    private void gtnl$captureLinks(TileEntity tile, int flags, CallbackInfoReturnable<PendingBlock> cir) {
        gtnl$copiedLinks = WirelessCopiedLinks.capture(tile);
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void gtnl$resetLinks(CallbackInfoReturnable<PendingBlock> cir) {
        gtnl$copiedLinks = null;
    }

    @Inject(
        method = "clone()Lcom/recursive_pineapple/matter_manipulator/common/building/PendingBlock;",
        at = @At("RETURN"))
    private void gtnl$cloneLinks(CallbackInfoReturnable<PendingBlock> cir) {
        ((IWirelessCopiedLink) cir.getReturnValue()).gtnl$setCopiedLinks(gtnl$copiedLinks);
    }

    @Inject(
        method = "transform(Lcom/recursive_pineapple/matter_manipulator/common/items/manipulator/Transform;)V",
        at = @At("RETURN"))
    private void gtnl$rotateLinks(Transform transform, CallbackInfo ci) {
        gtnl$copiedLinks = WirelessCopiedLinks.rotate(gtnl$copiedLinks, transform::apply);
    }

    @Inject(method = "apply", at = @At("RETURN"))
    private void gtnl$restoreLinks(IBlockApplyContext context, World world, CallbackInfoReturnable<Boolean> cir) {
        WirelessCopiedLinks.pasted(context.getRealPlayer(), context.getTileEntity(), gtnl$copiedLinks);
    }
}
