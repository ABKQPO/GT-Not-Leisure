package com.science.gtnl.client.armorflight;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

import com.science.gtnl.common.armorflight.ArmorFlight;
import com.science.gtnl.common.armorflight.FlightVec;
import com.science.gtnl.common.entity.EntityArmorFlightPiece;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArmorFlightSound extends MovingSound {

    public enum Kind {
        ONESHOT,
        JET,
        TURBINE
    }

    private final EntityArmorFlightPiece piece;
    private final Kind kind;
    private final float gain;
    private FlightVec last;

    public ArmorFlightSound(EntityArmorFlightPiece piece, ResourceLocation sound, Kind kind, float volume,
        float pitch) {
        super(sound);
        this.piece = piece;
        this.kind = kind;
        this.gain = volume;
        this.repeat = kind != Kind.ONESHOT;
        this.field_147665_h = 0;
        this.volume = kind == Kind.ONESHOT ? volume : 0.01F;
        this.field_147663_c = pitch;
        last = piece.flightPosition(0);
        moveTo(last);
    }

    private void moveTo(FlightVec at) {
        xPosF = (float) at.x();
        yPosF = (float) at.y();
        zPosF = (float) at.z();
    }

    @Override
    public void update() {
        if (kind == Kind.ONESHOT) {
            if (!piece.isDead) moveTo(piece.flightPosition(0));
            return;
        }
        if (piece.isDead) {
            donePlaying = true;
            return;
        }
        float age = piece.age(0);
        int flight = piece.duration();
        boolean ret = piece.isReturn();
        float end = ret ? flight : piece.snapEnd();
        if (age >= end) {
            donePlaying = true;
            return;
        }
        FlightVec at = piece.flightPosition(0);
        float speed = (float) at.distanceTo(last);
        last = at;
        moveTo(at);
        boolean turbine = kind == Kind.TURBINE;
        float t = ret ? (age - ArmorFlight.PURGE_TICKS) / Math.max(1, flight - ArmorFlight.PURGE_TICKS) : age / flight;
        float level;
        if (ret) level = ArmorFlight.clamp(t * 6, 0, 1) * (1 - ArmorFlight.clamp((t - 0.8F) / 0.2F, 0, 1));
        else if (age < 3) level = 1.15F;
        else if (t < ArmorFlight.BRAKE_AT) level = 0.85F;
        else if (t < 1) level = turbine ? 0.7F : 1.1F;
        else level = turbine ? 0.35F : 0.45F;
        volume = gain * level + 0.01F;
        float s = ArmorFlight.clamp(speed, 0, 1.6F);
        field_147663_c = turbine ? 0.75F + s * 0.45F : 0.85F + s * 0.15F;
    }
}
