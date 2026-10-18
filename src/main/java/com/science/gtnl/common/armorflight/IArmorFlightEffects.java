package com.science.gtnl.common.armorflight;

import net.minecraft.entity.Entity;

import com.science.gtnl.common.entity.EntityArmorFlightPiece;

public interface IArmorFlightEffects {

    /**
     * Plays the sounds and particles of one flying piece for the current client tick.
     *
     * @param piece the piece, ticking on the client
     */
    void tick(EntityArmorFlightPiece piece);

    /**
     * Yaw the pieces flying to a wearer line up with on this client. The viewer's own body in first person is
     * replaced by the camera yaw, so the suit assembles in line with the view instead of the lagging body.
     *
     * @param wearer      the entity the pieces fly to
     * @param partialTick render partial tick
     * @return yaw in degrees
     */
    float wearerYaw(Entity wearer, float partialTick);
}
