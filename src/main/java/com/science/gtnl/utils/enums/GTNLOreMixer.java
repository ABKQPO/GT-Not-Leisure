package com.science.gtnl.utils.enums;

import galacticgreg.WorldgenOreLayerSpace;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.common.OreMixBuilder;
import gregtech.common.WorldgenGTOreLayer;

public enum GTNLOreMixer {

    SulfuricAcid(new OreMixBuilder().name("ore.mix.sulfuricacid")
        .heightRange(0, 255)
        .weight(160)
        .density(999)
        .size(32)
        .enableInDim(OreMixBuilder.OW, OreMixBuilder.NETHER, OreMixBuilder.THE_END, OreMixBuilder.TWILIGHT_FOREST)
        .enableInDim(DimensionDef.values())
        .primary(Materials.SulfuricAcid)
        .secondary(Materials.NitricAcid)
        .inBetween(Materials.HydrochloricAcid)
        .sporadic(Materials.HydrofluoricAcid)),

    Oil(new OreMixBuilder().name("ore.mix.oil")
        .heightRange(0, 255)
        .weight(160)
        .density(999)
        .size(32)
        .enableInDim(OreMixBuilder.OW, OreMixBuilder.NETHER, OreMixBuilder.THE_END, OreMixBuilder.TWILIGHT_FOREST)
        .enableInDim(DimensionDef.values())
        .primary(Materials.OilLight)
        .secondary(Materials.OilHeavy)
        .inBetween(Materials.OilMedium)
        .sporadic(Materials.OilExtraHeavy)),

    Oxygen(new OreMixBuilder().name("ore.mix.oxygen")
        .heightRange(0, 255)
        .weight(160)
        .density(999)
        .size(32)
        .enableInDim(OreMixBuilder.OW, OreMixBuilder.NETHER, OreMixBuilder.THE_END, OreMixBuilder.TWILIGHT_FOREST)
        .enableInDim(DimensionDef.values())
        .primary(Materials.Oxygen)
        .secondary(Materials.Hydrogen)
        .inBetween(Materials.Nitrogen)
        .sporadic(Materials.Chlorine)),

    Universium(new OreMixBuilder().name("ore.mix.universium")
        .heightRange(80, 210)
        .weight(160)
        .density(16)
        .size(32)
        .enableInDim(OreMixBuilder.OW)
        .primary(MaterialsUEVplus.SpaceTime)
        .secondary(MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter)
        .inBetween(MaterialsUEVplus.Universium)
        .sporadic(MaterialsUEVplus.MagMatter)),

    ;

    public final OreMixBuilder oreMixBuilder;

    GTNLOreMixer(OreMixBuilder oreMixBuilder) {
        this.oreMixBuilder = oreMixBuilder;
    }

    public WorldgenGTOreLayer addGTOreLayer() {
        return new WorldgenGTOreLayer(this.oreMixBuilder);
    }

    public WorldgenOreLayerSpace addGaGregOreLayer() {
        return new WorldgenOreLayerSpace(this.oreMixBuilder);
    }
}
