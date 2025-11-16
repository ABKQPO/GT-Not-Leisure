package com.science.gtnl.mixins.late.NEICustomDiagram;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.github.dcysteine.neicustomdiagram.main.NeiCustomDiagram;

@Mixin(value = NeiCustomDiagram.class, remap = false)
public interface AccessorNeiCustomDiagram {

    @Accessor("hasGenerated")
    boolean getHasGenerated();

    @Accessor("hasGenerated")
    void setHasGenerated(boolean inventory);
}
