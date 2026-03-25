package com.science.gtnl.api;

public interface IBeamFormerRenderer {

    default void init(IBeamFormer beamFormer) {}

    boolean shouldRenderDynamic(IBeamFormer partBeamFormer);

    default void renderDynamic(IBeamFormer partBeamFormer, double x, double y, double z, float partialTicks) {}
}
