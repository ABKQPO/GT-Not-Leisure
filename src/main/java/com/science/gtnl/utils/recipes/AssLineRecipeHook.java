package com.science.gtnl.utils.recipes;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.science.gtnl.config.MainConfig;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public class AssLineRecipeHook {

    public static final Map<String, List<RemovalRecipe>> RECIPE_TO_REMOVE = new HashMap<>();

    // 必须在此处移除装配线配方, 不能调用对象获取name, 因为本方法执行在gt pre load 阶段开始
    public static void loadAndInit() {

        if (MainConfig.recipe.enableDeleteRecipe) {
            // 移除 生物处理器单元
            Builder.newBuilder()
                .addOutputItem("gregtech:gt.metaitem.03", 1, 32077)
                .addResearchItem("gregtech:gt.metaitem.03", 1, 32072)
                .addResearchKey(90 * SECONDS)
                .addProcessKey(30 * SECONDS, 983040)
                .build();

            // 移除 神经元处理器
            Builder.newBuilder()
                .addOutputItem("gregtech:gt.metaitem.03", 1, 32072)
                .addResearchItem("gregtech:gt.metaitem.03", 1, 32073)
                .addResearchKey(60 * SECONDS)
                .addProcessKey(30 * SECONDS, 122880)
                .build();

            // 移除 湿件处理器主机
            Builder.newBuilder()
                .addOutputItem("gregtech:gt.metaitem.03", 1, 32095)
                .addResearchItem("gregtech:gt.metaitem.03", 1, 32094)
                .addResearchKey(24000, 64, 50000, 4)
                .addProcessKey(100 * SECONDS, 300000)
                .build();
        }
    }

    @SuppressWarnings("SameParameterValue")
    public static class Builder {

        private String researchKey;

        private String processKey;

        private String outputItemKey;

        private String researchItemKey;

        @Contract(value = " -> new", pure = true)
        private static @NotNull Builder newBuilder() {
            return new Builder();
        }

        private Builder addOutputItem(String outputItemName, int amount, int meta) {
            this.outputItemKey = outputItemName + "@" + amount + "@" + meta;
            return this;
        }

        private Builder addResearchItem(String researchItemName, int amount, int meta) {
            this.researchItemKey = researchItemName + "@" + amount + "@" + meta;
            return this;
        }

        private Builder addResearchKey(int researchTime) {
            this.researchKey = "@" + researchTime;
            return this;
        }

        private Builder addResearchKey(int totalComputationRequired, int computationRequiredPerSec, int researchEUt,
            int researchAmperage) {
            this.researchKey = "@" + totalComputationRequired
                + "@"
                + computationRequiredPerSec
                + "@"
                + researchEUt
                + "@"
                + researchAmperage;
            return this;
        }

        private Builder addProcessKey(int duration, int aEUt) {
            this.processKey = "@" + duration + "@" + aEUt;
            return this;
        }

        private void build() {
            if (researchItemKey == null || researchKey == null || outputItemKey == null || processKey == null)
                throw new NullPointerException("Check....");
            RECIPE_TO_REMOVE.computeIfAbsent(researchItemKey + researchKey, k -> new ArrayList<>())
                .add(new RemovalRecipe(outputItemKey + processKey));
        }
    }

    public static class RemovalRecipe {

        public final String removalKey;

        public boolean hasNotBeenRemoved = true;

        public RemovalRecipe(String removalKey) {
            this.removalKey = removalKey;
        }

    }

}
