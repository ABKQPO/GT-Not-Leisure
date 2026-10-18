package com.science.gtnl.client.armorflight;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArmorFlightFX extends EntityFX {

    public enum Kind {
        THRUST,
        CORE,
        TRAIL
    }

    private final Kind kind;
    private final float friction;
    private final float lift;
    private final float baseScale;

    public ArmorFlightFX(World world, Kind kind, double x, double y, double z, double vx, double vy, double vz) {
        super(world, x, y, z);
        this.kind = kind;
        motionX = vx;
        motionY = vy;
        motionZ = vz;
        noClip = true;
        switch (kind) {
            case THRUST -> {
                friction = 0.84F;
                lift = 0;
                particleMaxAge = 7 + rand.nextInt(5);
                baseScale = 1.2F + rand.nextFloat() * 0.7F;
            }
            case CORE -> {
                friction = 0.6F;
                lift = 0;
                particleMaxAge = 3 + rand.nextInt(2);
                baseScale = 1.0F + rand.nextFloat() * 0.5F;
            }
            default -> {
                friction = 0.92F;
                lift = 0.002F;
                particleMaxAge = 22 + rand.nextInt(12);
                baseScale = 1.4F + rand.nextFloat() * 0.6F;
                float g = 0.88F + rand.nextFloat() * 0.08F;
                setRBGColorF(g, g, Math.min(1, g + 0.04F));
                particleAlpha = 0.45F;
            }
        }
        particleScale = baseScale;
        updateLook();
    }

    private void updateLook() {
        float k = particleAge / (float) particleMaxAge;
        switch (kind) {
            case THRUST -> {
                setParticleTextureIndex(Math.min(7, 3 + (int) (k * 5)));
                setRBGColorF(1 - 0.6F * k, 1 - 0.3F * k, 1);
                particleAlpha = 1 - k * k;
                particleScale *= 1.02F - 0.08F * k;
            }
            case CORE -> {
                setParticleTextureIndex(6 + rand.nextInt(2));
                setRBGColorF(1, 1, 0.92F + rand.nextFloat() * 0.08F);
                particleScale = baseScale * (0.82F + rand.nextFloat() * 0.2F);
            }
            default -> {
                setParticleTextureIndex(Math.max(0, 7 - (int) (k * 8)));
                particleAlpha = 0.45F * (1 - k);
                particleScale *= 1.035F;
            }
        }
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setDead();
            return;
        }
        motionY += lift;
        setPosition(posX + motionX, posY + motionY, posZ + motionZ);
        motionX *= friction;
        motionY *= friction;
        motionZ *= friction;
        updateLook();
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return kind == Kind.TRAIL ? super.getBrightnessForRender(partialTick) : 0xF000F0;
    }

    @Override
    public float getBrightness(float partialTick) {
        return kind == Kind.TRAIL ? super.getBrightness(partialTick) : 1.0F;
    }
}
