package com.science.gtnl.client.railgun;

import java.util.function.DoubleSupplier;

import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunDefaults;

public class RailgunChargeState {

    private boolean live;
    private long startedTick, lastLiveTick, cappedTick = Long.MIN_VALUE, finishedTick = Long.MIN_VALUE;
    private double rate = 1;
    private int cap = Integer.MAX_VALUE;

    public void update(long now, boolean using, boolean waiting, DoubleSupplier rateOnStart) {
        if (finishedTick != Long.MIN_VALUE) {
            if (using && now - finishedTick <= RailgunDefaults.CHARGE_RESUME_GRACE_TICKS) return;
            finishedTick = Long.MIN_VALUE;
        }
        if (using) {
            if (!live || now - lastLiveTick > RailgunDefaults.CHARGE_RESUME_GRACE_TICKS) {
                startedTick = now;
                rate = Math.max(1, rateOnStart.getAsDouble());
                if (!recentlyCapped(now)) cap = Integer.MAX_VALUE;
            }
            live = true;
            lastLiveTick = now;
            return;
        }
        if (live && (!waiting || now - lastLiveTick > RailgunDefaults.CHARGE_RESUME_GRACE_TICKS)) live = false;
    }

    public boolean live() {
        return live;
    }

    public boolean idle(long now) {
        return !live && finishedTick == Long.MIN_VALUE && !recentlyCapped(now);
    }

    public int units(long now) {
        return live ? Math.min(cap, RailgunData.units(now - startedTick, rate)) : 0;
    }

    public double age(long now, float partialTicks) {
        return live ? Math.max(0, now - startedTick + partialTicks) : 0;
    }

    public float units(long now, float partialTicks) {
        return live ? Math.min(cap, (float) (Math.max(0F, now - startedTick + partialTicks) * rate)) : 0F;
    }

    public void cap(int units, long now) {
        cap = Math.max(0, units);
        cappedTick = now;
    }

    private boolean recentlyCapped(long now) {
        return cappedTick != Long.MIN_VALUE && now - cappedTick <= RailgunDefaults.CHARGE_RESUME_GRACE_TICKS;
    }

    public void end() {
        live = false;
    }

    public void endUntilReleased(long now) {
        live = false;
        finishedTick = now;
    }
}
