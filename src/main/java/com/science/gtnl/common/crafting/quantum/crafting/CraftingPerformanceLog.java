package com.science.gtnl.common.crafting.quantum.crafting;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.core.AELog;

final class CraftingPerformanceLog {

    private final boolean chinese = "zh".equalsIgnoreCase(
        Locale.getDefault()
            .getLanguage());
    private AEKey output;
    private long amount;
    private long stages;

    void start(AEKey output, long amount) {
        this.output = output;
        this.amount = amount;
        this.stages = 0;
        log(chinese ? "开始 输出=" + output + " 数量=" + amount : "start output=" + output + " amount=" + amount);
    }

    void stage(String name, long nanos) {
        this.stages++;
        var micros = TimeUnit.NANOSECONDS.toMicros(nanos);
        log(chinese ? "阶段=" + name + " 耗时=" + micros + " 微秒" : "stage=" + name + " time=" + micros + " us");
    }

    void count(String name, long amount) {
        log(chinese ? "计数=" + name + " 值=" + amount : "count=" + name + " Value=" + amount);
    }

    void finish(long nanos, CraftingCalculation calculation) {
        var micros = TimeUnit.NANOSECONDS.toMicros(nanos);
        if (chinese) {
            log(
                "汇总 输出=" + output
                    + " 数量="
                    + amount
                    + " 耗时="
                    + micros
                    + " 微秒 合成树深度="
                    + calculation.getTreeDepth()
                    + " 节点数="
                    + calculation.getTreeNodeCount()
                    + " 样板节点数="
                    + calculation.getPatternNodeCount()
                    + " 最大请求深度="
                    + calculation.getMaxRequestDepth()
                    + " 阶段数="
                    + stages);
        } else {
            log(
                "summary output=" + output
                    + " amount="
                    + amount
                    + " time="
                    + micros
                    + " us treeDepth="
                    + calculation.getTreeDepth()
                    + " treeNodes="
                    + calculation.getTreeNodeCount()
                    + " patternNodes="
                    + calculation.getPatternNodeCount()
                    + " maxRequestDepth="
                    + calculation.getMaxRequestDepth()
                    + " stages="
                    + stages);
        }
    }

    private static void log(String message) {
        AELog.craftingPerformance("%s", message);
    }
}
