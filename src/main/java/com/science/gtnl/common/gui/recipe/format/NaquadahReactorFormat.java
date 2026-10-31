package com.science.gtnl.common.gui.recipe.format;

import java.util.List;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class NaquadahReactorFormat implements INEISpecialInfoFormatter {

    @Override
    @NotNull
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> msgs = new ObjectArrayList<>();
        msgs.add(
            StatCollector.translateToLocal("gtnl.nei.naquadah_reactor.special_value") + NumberFormatUtil.formatNumber(
                recipeInfo.recipe.getMetadataOrDefault(NaquadahReactorMetadata.INSTANCE, NaquadahReactorMetadata.NONE)
                    .rightLong())
                + " EU/t");
        return msgs;
    }
}
