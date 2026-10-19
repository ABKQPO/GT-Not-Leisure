package com.science.gtnl.common.celestweave;

import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.BLIND;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.IGNITE;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.INERTIA;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.JUMP;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.LEVEL;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.LEVITATE;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.OMNI;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.PROJECTILE;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.SPEED;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.STEP;
import static com.science.gtnl.common.celestweave.CelestweaveBehaviors.VSPEED;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.config.MainConfig;

public final class CelestweaveSettings {

    public static final int ENABLE = 0, SHOW = 1, CFG = 2, COSMETIC = 3;
    public static final String GLOW_COLOR = "GlowColor", IDLE_ARCS = "IdleArcs", ARC_FREQ = "ArcFreq",
        ARC_SPREAD = "ArcSpread", ARC_BRIGHT = "ArcBright", ARC_EVENTS = "ArcEvents";

    public record Option(CelestweaveModule module, String key, boolean bool, int min, int max, int fallback) {

        public int max() {
            var config = MainConfig.item.celestweave;
            if (module == CelestweaveModule.FLIGHT && !bool) return config.flightMaxLevel;
            if (module == CelestweaveModule.WALK_ASSIST && !bool) return config.walkMaxLevel;
            return max;
        }

        public int get(ItemStack stack) {
            return bool ? CelestweaveData.cfgBool(stack, module, key, fallback != 0) ? 1 : 0
                : CelestweaveData.cfgInt(stack, module, key, fallback, min, max());
        }
    }

    public static final List<Option> OPTIONS = Arrays.asList(
        new Option(CelestweaveModule.FLIGHT, SPEED, false, 1, 16, 1),
        new Option(CelestweaveModule.FLIGHT, VSPEED, false, 1, 16, 1),
        new Option(CelestweaveModule.FLIGHT, LEVITATE, true, 0, 1, 0),
        new Option(CelestweaveModule.FLIGHT, INERTIA, true, 0, 1, 0),
        new Option(CelestweaveModule.WALK_ASSIST, SPEED, false, 1, 16, 2),
        new Option(CelestweaveModule.WALK_ASSIST, JUMP, false, 1, 16, 1),
        new Option(CelestweaveModule.WALK_ASSIST, STEP, true, 0, 1, 1),
        new Option(CelestweaveModule.WALK_ASSIST, OMNI, true, 0, 1, 1),
        new Option(CelestweaveModule.REACH, LEVEL, false, 0, 2, 0),
        new Option(CelestweaveModule.REFLECT, PROJECTILE, true, 0, 1, 1),
        new Option(CelestweaveModule.REFLECT, IGNITE, true, 0, 1, 1),
        new Option(CelestweaveModule.REFLECT, BLIND, true, 0, 1, 1));

    private CelestweaveSettings() {}

    public static Option option(CelestweaveModule module, String key) {
        for (Option option : OPTIONS) if (option.module() == module && option.key()
            .equals(key)) return option;
        return null;
    }

    public static boolean apply(ItemStack stack, CelestweaveModule module, int op, String key, float value) {
        if (stack == null || !Float.isFinite(value)) return false;
        switch (op) {
            case ENABLE -> {
                if (module == null || !module.toggleable()
                    || !CelestweaveData.installed(stack)
                        .contains(module))
                    return false;
                CelestweaveData.setFlag(stack, CelestweaveData.DISABLED, module, value < 0.5F);
                return true;
            }
            case SHOW -> {
                if (module == null || !module.hideable()
                    || !CelestweaveData.installed(stack)
                        .contains(module))
                    return false;
                CelestweaveData.setFlag(stack, CelestweaveData.HIDDEN, module, value < 0.5F);
                return true;
            }
            case CFG -> {
                Option option = option(module, key);
                if (option == null || !CelestweaveData.installed(stack)
                    .contains(module)) return false;
                NBTTagCompound cfg = CelestweaveData.cfgForWrite(stack, module);
                if (option.bool()) cfg.setBoolean(key, value >= 0.5F);
                else cfg.setInteger(key, Math.max(option.min(), Math.min(option.max(), Math.round(value))));
                return true;
            }
            case COSMETIC -> {
                CelestweaveCosmetic current = CelestweaveData.cosmetic(stack);
                CelestweaveCosmetic next = cosmetic(current, key, value);
                if (next == null) return false;
                CelestweaveData.writeCosmetic(stack, next);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private static CelestweaveCosmetic cosmetic(CelestweaveCosmetic c, String key, float value) {
        NBTTagCompound tag = c.write();
        switch (key) {
            case GLOW_COLOR -> tag.setInteger(GLOW_COLOR, (int) value & 0xFFFFFF);
            case IDLE_ARCS -> tag.setInteger(IDLE_ARCS, Math.round(value));
            case ARC_FREQ, ARC_SPREAD, ARC_BRIGHT -> tag.setFloat(key, value);
            case ARC_EVENTS -> tag.setInteger(ARC_EVENTS, (int) value & CelestweaveCosmetic.EVENT_ALL);
            default -> {
                return null;
            }
        }
        return CelestweaveCosmetic.read(tag);
    }
}
