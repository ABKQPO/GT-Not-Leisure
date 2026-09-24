package com.science.gtnl.common.world;

import java.util.List;
import java.util.Map;

import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.ShimmerOreMaterial;

import galacticgreg.api.enums.DimensionDef;
import gregtech.api.interfaces.IOreMaterial;
import gregtech.common.OreMixBuilder;
import gtneioreplugin.util.GT5OreLayerHelper;

public final class GTNLVeinCatalog {

    public static final String SHIMMER_VEIN_NAME = "ore.mix.gtnl.shimmer";
    private static final int SHIMMER_VEIN_BLOCK_SIZE = 16;
    private static final int SHIMMER_VEIN_MIN_Y = 8;
    private static final int SHIMMER_VEIN_MAX_Y = 70;

    private GTNLVeinCatalog() {}

    public static List<OreMixBuilder> additionalVeins() {
        IOreMaterial shimmer = findShimmerMaterial();
        if (shimmer == null) {
            return List.of();
        }
        return List.of(createShimmerVein(shimmer));
    }

    private static OreMixBuilder createShimmerVein(IOreMaterial shimmer) {
        return new OreMixBuilder().name(SHIMMER_VEIN_NAME)
            .size(SHIMMER_VEIN_BLOCK_SIZE)
            .heightRange(SHIMMER_VEIN_MIN_Y, SHIMMER_VEIN_MAX_Y)
            .enableInDim(DimensionDef.Overworld)
            .primary(shimmer)
            .secondary(shimmer)
            .inBetween(shimmer)
            .sporadic(shimmer);
    }

    public static void fillOreLayerWrappers(Map<String, Object> target) {
        for (OreMixBuilder vein : additionalVeins()) {
            target.put(vein.oreMixName, new GT5OreLayerHelper.OreLayerWrapper(vein));
        }
    }

    public static IOreMaterial findShimmerMaterial() {
        if (GTNLMaterials.Shimmer != null) {
            return GTNLMaterials.Shimmer;
        }
        return IOreMaterial.findMaterial(ShimmerOreMaterial.INTERNAL_NAME);
    }
}
