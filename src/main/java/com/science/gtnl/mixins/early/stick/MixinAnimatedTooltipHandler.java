package com.science.gtnl.mixins.early.stick;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler;
import com.science.gtnl.common.item.items.stick;

@Mixin(value = AnimatedTooltipHandler.class, remap = false)
public abstract class MixinAnimatedTooltipHandler {

    @ModifyArg(
        method = "renderTooltip",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
        index = 0)
    private static Object redirectTooltipMapGet(Object original) {
        if (original instanceof ItemStack stack && stack.getItem() instanceof Stick stick && !stick.isShiftDown()) {
            ItemStack fake = Stick.getDisguisedStack(stack);
            if (fake != null) {
                return fake;
            }
        }
        return original;
    }
}
