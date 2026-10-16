package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import com.science.gtnl.common.railgun.RailgunVec;

public final class RailgunVisuals {

    private RailgunVisuals() {}

    public static RailgunVec muzzle(int shooterId, RailgunVec fallback, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity entity = mc.theWorld == null ? null : mc.theWorld.getEntityByID(shooterId);
        if (entity == null) return fallback;
        return entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0 ? firstPersonMuzzle(partialTicks)
            : thirdPersonMuzzle(entity, partialTicks);
    }

    public static RailgunVec firstPersonMuzzle(float partialTicks) {
        var player = Minecraft.getMinecraft().thePlayer;
        RailgunVec look = RailgunVec.look(player);
        RailgunVec right = look.cross(new RailgunVec(0, 1, 0))
            .normalize();
        RailgunVec up = right.cross(look)
            .normalize();
        return interpolated(player, partialTicks).add(look.scale(0.9))
            .add(right.scale(0.32))
            .add(up.scale(-0.22));
    }

    public static RailgunVec thirdPersonMuzzle(Entity entity, float partialTicks) {
        RailgunVec look = RailgunVec.look(entity);
        RailgunVec right = look.cross(new RailgunVec(0, 1, 0))
            .normalize();
        return interpolated(entity, partialTicks).add(0, entity.getEyeHeight() - entity.yOffset - 0.2, 0)
            .add(right.scale(0.35))
            .add(look.scale(1.2));
    }

    public static RailgunVec interpolated(Entity entity, float partialTicks) {
        return new RailgunVec(
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks);
    }
}
