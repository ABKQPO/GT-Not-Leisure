package com.science.gtnl.client.celestweave;

import java.util.List;

import net.minecraft.entity.Entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class CelestweaveClientState {

    static final Int2ObjectMap<CelestweaveClientState> STATES = new Int2ObjectOpenHashMap<>();
    static final long NEVER = Long.MIN_VALUE / 4;

    final List<CelestweaveArc> arcs = new ObjectArrayList<>();
    CelestweaveLook look;
    int lookTick = Integer.MIN_VALUE;
    CelestweavePose pose;
    long poseFrame = -1;
    long lastNanos;
    float rotor, inner, crystal, swirl;
    long hitAt = NEVER, dashAt = NEVER;
    float hitPower = 1F, hitDirectionX, hitDirectionY;
    boolean hitDirectional;
    boolean flying;
    long wingT0 = NEVER;
    float[] wingSnap;

    public static CelestweaveClientState of(Entity entity) {
        return of(entity.getEntityId());
    }

    public static CelestweaveClientState of(int entityId) {
        CelestweaveClientState state = STATES.get(entityId);
        if (state == null) STATES.put(entityId, state = new CelestweaveClientState());
        return state;
    }

    public static CelestweaveClientState peek(int entityId) {
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
        for (CelestweaveArc arc : arcs) if (arc.kind == CelestweaveArc.Kind.IDLE) count++;
        return count;
    }

    boolean hasAntenna() {
        for (CelestweaveArc arc : arcs) if (arc.kind == CelestweaveArc.Kind.ANTENNA) return true;
        return false;
    }
}
