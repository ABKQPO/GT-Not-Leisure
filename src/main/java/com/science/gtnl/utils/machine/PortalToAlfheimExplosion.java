package com.science.gtnl.utils.machine;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.common.utils.Utils;
import com.brandon3055.draconicevolution.common.utils.handlers.IProcess;
import com.brandon3055.draconicevolution.common.utils.handlers.ProcessHandler;

public class PortalToAlfheimExplosion implements IProcess {

    public static final DamageSource EXPLOSION = new DamageSource("damage.gtnl.portalExplode").setExplosion()
        .setDamageBypassesArmor()
        .setDamageIsAbsolute()
        .setFireDamage()
        .setProjectile()
        .setDamageAllowedInCreativeMode()
        .setMagicDamage();

    public final World world;
    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    public final float power;
    public boolean isDead;
    public double expansion = 0;

    public PortalToAlfheimExplosion(World world, int x, int y, int z, float power) {
        this.world = world;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.power = power;
        isDead = world.isRemote;
    }

    @Override
    public void updateProcess() {

        int size = (int) expansion;
        for (int x = xCoord - size; x < xCoord + size; x++) {
            for (int z = zCoord - size; z < zCoord + size; z++) {
                double distance = Utils.getDistanceAtoB(x, z, xCoord, zCoord);
                if (distance < expansion && distance >= size - 1) {
                    float tracePower = power - (float) (expansion / 10D);
                    tracePower *= (float) (1F + (world.rand.nextFloat() - 0.5F) * 0.2);
                    ProcessHandler.addProcess(new PortalToAlfheimExplosionTrace(world, x, yCoord, z, tracePower));
                }
            }
        }

        isDead = expansion >= power * 2;
        expansion += 1;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
}
