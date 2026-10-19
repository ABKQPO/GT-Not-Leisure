package com.science.gtnl.client.celestweave;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@SideOnly(Side.CLIENT)
public final class CwClientState {

    static final Int2ObjectMap<CwClientState> STATES = new Int2ObjectOpenHashMap<>();
    static final long NEVER = Long.MIN_VALUE / 4;

    final List<CwArc> arcs = new ArrayList<>();
    CwLook look;
    int lookTick = Integer.MIN_VALUE;
    CwPose pose;
    long poseFrame = -1;
    long lastNanos;
    float rotor, inner, crystal, swirl;
    long hitAt = NEVER, dashAt = NEVER;
    float hitPower = 1F, hitDirectionX, hitDirectionY;
    boolean hitDirectional;
    boolean flying;
    long wingT0 = NEVER;
    float[] wingSnap;

    private CwClientState() {}

    public static CwClientState of(Entity entity) {
        return of(entity.getEntityId());
    }

    public static CwClientState of(int entityId) {
        CwClientState state = STATES.get(entityId);
        if (state == null) STATES.put(entityId, state = new CwClientState());
        return state;
    }

    public static CwClientState peek(int entityId) {
        return STATES.get(entityId);
    }

    public static void clear() {
        STATES.clear();
    }

    boolean recentlyHit(long now) {
        return now - hitAt < 500;
    }

    float hitGlow(long now) {
        long age = now - hitAt;
        if (age < 0 || age > 900) return 0F;
        float attack = Math.min(1F, age / 40F);
        return attack * (float) Math.exp(-Math.max(0, age - 40) / 170.0) * hitPower;
    }

    int countIdle() {
        int count = 0;
        for (CwArc arc : arcs) if (arc.kind == CwArc.Kind.IDLE) count++;
        return count;
    }

    boolean hasAntenna() {
        for (CwArc arc : arcs) if (arc.kind == CwArc.Kind.ANTENNA) return true;
        return false;
    }
}
