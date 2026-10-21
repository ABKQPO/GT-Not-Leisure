
package com.science.gtnl.common.recipe.thaumcraft;

import net.minecraft.item.ItemStack;

import thaumcraft.api.aspects.AspectList;

public class CrucibleRecipeData {

    public int sourceHash;
    public String researchKey;
    public ItemStack[] catalysts;
    public String catalystOreName;
    public ItemStack output;
    public AspectList aspects;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CrucibleRecipeData data = new CrucibleRecipeData();

        public Builder sourceHash(int sourceHash) {
            data.sourceHash = sourceHash;
            return this;
        }

        public Builder researchKey(String researchKey) {
            data.researchKey = researchKey;
            return this;
        }

        public Builder catalysts(ItemStack... catalysts) {
            data.catalysts = catalysts;
            return this;
        }

        public Builder catalystOreName(String catalystOreName) {
            data.catalystOreName = catalystOreName;
            return this;
        }

        public Builder output(ItemStack output) {
            data.output = output;
            return this;
        }

        public Builder aspects(AspectList aspects) {
            data.aspects = aspects;
            return this;
        }

        public CrucibleRecipeData build() {
            return data;
        }
    }
}
