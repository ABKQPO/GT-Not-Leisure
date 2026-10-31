package com.science.gtnl.common.machine.hatch;

import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class ExplosionDynamoHatch extends MTEHatchDynamo {

    public ExplosionDynamoHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public ExplosionDynamoHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.explosion_dynamo.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("gtnl.hatch.explosion_dynamo.tooltip.0") };
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ExplosionDynamoHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public long maxEUStore() {
        return Long.MAX_VALUE;
    }

    @Override
    public long maxAmperesOut() {
        return 64;
    }

    // Scans the 21x21x21 cube; the first absorbing hatch in each column stops that column only.
    public static boolean absorbAround(World world, double cx, double cy, double cz, float size) {
        int ex = (int) cx, ey = (int) cy, ez = (int) cz;
        boolean absorbed = false;
        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {
                    if (absorbExplosion(world, x, y, z, cx, cy, cz, size * 20480)) {
                        absorbed = true;
                        break;
                    }
                }
            }
        }
        return absorbed;
    }

    // Stops at the first hatch in the 21x21x21 cube that takes the explosion.
    public static boolean absorbFirst(World world, double cx, double cy, double cz, double energyAtCentre) {
        int ex = (int) cx, ey = (int) cy, ez = (int) cz;
        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {
                    if (absorbExplosion(world, x, y, z, cx, cy, cz, energyAtCentre)) return true;
                }
            }
        }
        return false;
    }

    // Returns whether the hatch took the explosion.
    public static boolean absorbExplosion(World world, int x, int y, int z, double cx, double cy, double cz,
        double energyAtCentre) {
        if (!world.blockExists(x, y, z)) return false;
        if (!(world.getTileEntity(x, y, z) instanceof BaseMetaTileEntity te)
            || !(te.getMetaTileEntity() instanceof ExplosionDynamoHatch machine)) return false;

        double dx = te.getXCoord() + 0.5 - cx;
        double dy = te.getYCoord() + 0.5 - cy;
        double dz = te.getZCoord() + 0.5 - cz;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > 10.0) return false;

        double efficiency = Math.max(0.0, 1.0 - 0.05 * distance);
        long addedEU = (long) (energyAtCentre * efficiency);
        long stored = machine.getEUVar();
        if (stored + addedEU > machine.maxEUStore()) return false;
        machine.setEUVar(stored + addedEU);
        return true;
    }
}
