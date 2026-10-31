package com.science.gtnl.common.world;

import galacticgreg.api.enums.DimensionDef;
import gregtech.api.enums.Materials;
import gregtech.common.OreMixBuilder;
import gregtech.common.WorldgenGTOreLayer;

// See gregtech.api.enums.OreMixes
public enum GTNLOreMixer {

    SulfuricAcid(new OreMixBuilder().name("ore.mix.sulfuricacid")
        .heightRange(0, 255)
        .weight(160)
        .density(999)
        .size(32)
        .enableInDim(DimensionDef.Overworld, DimensionDef.Nether, DimensionDef.TheEnd, DimensionDef.TwilightForest)
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
        .enableInDim(DimensionDef.Overworld, DimensionDef.Nether, DimensionDef.TheEnd, DimensionDef.TwilightForest)
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
        .enableInDim(DimensionDef.Overworld, DimensionDef.Nether, DimensionDef.TheEnd, DimensionDef.TwilightForest)
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
        .enableInDim(DimensionDef.Overworld)
        .primary(Materials.SpaceTime)
        .secondary(Materials.MHDCSM)
        .inBetween(Materials.Universium)
        .sporadic(Materials.MagMatter)),

    ;

    public final OreMixBuilder oreMixBuilder;

    GTNLOreMixer(OreMixBuilder oreMixBuilder) {
        this.oreMixBuilder = oreMixBuilder;
    }

    public WorldgenGTOreLayer addGTOreLayer() {
        return new WorldgenGTOreLayer(this.oreMixBuilder);
    }
}
