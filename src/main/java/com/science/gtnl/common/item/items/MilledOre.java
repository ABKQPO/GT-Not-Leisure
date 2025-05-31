package com.science.gtnl.common.item.items;

import net.minecraftforge.fluids.Fluid;

import com.science.gtnl.Utils.enums.GTNLItemList;

import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gtPlusPlus.core.item.base.ore.BaseItemMilledOre;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class MilledOre {

    public static Fluid NaquadahEnrichedFlotationFroth;

    public MilledOre() {
        items();
        fluids();
    }

    public void items() {
        GTNLItemList.MilledNaquadahEnriched
            .set(BaseItemMilledOre.generate(Materials.NaquadahEnriched, (int) TierEU.RECIPE_ZPM));
    }

    public void fluids() {

        short[] aNaquadahEnrichedFrothRGB = Materials.NaquadahEnriched.mRGBa;
        NaquadahEnrichedFlotationFroth = FluidUtils.generateFluidNoPrefix(
            "froth.naquadahenrichedflotation",
            "Nquadah Enriched Froth",
            32 + 175,
            new short[] { aNaquadahEnrichedFrothRGB[0], aNaquadahEnrichedFrothRGB[1], aNaquadahEnrichedFrothRGB[2],
                100 },
            true);
    }
}
