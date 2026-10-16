package com.science.gtnl.common.railgun;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.RailgunRecoilFxPacket;

public final class RailgunRecoilService {

    public static final String GRACE = "gtnl.railgunRecoilGrace";

    private RailgunRecoilService() {}

    public static void apply(EntityPlayerMP player, RailgunChargeTier tier) {
        double speed = tier.recoilSpeed * (player.isSneaking() ? RailgunDefaults.RECOIL_CROUCH_MUL : 1)
            * (player.onGround ? 1 : RailgunDefaults.RECOIL_AIRBORNE_MUL);
        float pitch = tier.recoilPitch * (player.isSneaking() ? (float) RailgunDefaults.RECOIL_CROUCH_MUL : 1);
        RailgunVec back = RailgunVec.look(player)
            .scale(-1);
        RailgunVec behind = RailgunVec.pos(player)
            .add(back);
        int x = MathHelper.floor_double(behind.x()), y = MathHelper.floor_double(behind.y()),
            z = MathHelper.floor_double(behind.z());
        if (player.worldObj.getBlock(x, y, z)
            .getCollisionBoundingBoxFromPool(player.worldObj, x, y, z) != null) speed = 0;
        player.motionX += back.x() * speed;
        player.motionY += back.y() * speed;
        player.motionZ += back.z() * speed;
        player.velocityChanged = true;
        player.fallDistance = 0;
        player.getEntityData()
            .setLong(GRACE, player.worldObj.getTotalWorldTime() + 60);
        ScienceNotLeisure.network.sendTo(new RailgunRecoilFxPacket(pitch, tier.ordinal()), player);
    }
}
