package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.GTValues.V;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;

public class TapDynamoHatch extends MTEHatchDynamo {

    public int clickCount = 0;
    public long lastClickTime = 0;

    public TapDynamoHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            new String[] { StatCollector.translateToLocal("Tooltip_TapDynamoHatch_00"),
                StatCollector.translateToLocal("Tooltip_TapDynamoHatch_01") });
    }

    public TapDynamoHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new TapDynamoHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("clickCount", clickCount);
        aNBT.setLong("lastClickTime", lastClickTime);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        clickCount = aNBT.getInteger("clickCount");
        lastClickTime = aNBT.getLong("lastClickTime");
    }

    @Override
    public long maxAmperesOut() {
        return 16;
    }

    @Override
    public long maxEUStore() {
        return 512L + V[mTier + 1] * 16L;
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        World world = aBaseMetaTileEntity.getWorld();
        int x = aBaseMetaTileEntity.getXCoord();
        int y = aBaseMetaTileEntity.getYCoord();
        int z = aBaseMetaTileEntity.getZCoord();
        world.playSoundEffect(
            x,
            y,
            z,
            "random.explode",
            4.0F,
            (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        world.spawnParticle("hugeexplosion", x, y, z, 1.0D, 0.0D, 0.0D);
        world.spawnParticle("largeexplode", x, y, z, 1.0D, 0.0D, 0.0D);

        if (world.getTotalWorldTime() > lastClickTime + 20) {
            lastClickTime = world.getTotalWorldTime();
            clickCount = 0;
        } else if (clickCount > 5) {
            world.createExplosion(null, x, y, z, mTier * 20, true);
            world.setBlock(x, y, z, Blocks.air);
            world.removeTileEntity(x, y, z);
            return;
        }

        long storeEU = getEUVar();
        long addedEU = V[mTier] * 16;
        setEUVar(Math.min(storeEU + addedEU, maxEUStore()));

        clickCount++;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, ForgeDirection side,
        float aX, float aY, float aZ) {
        this.onLeftclick(aBaseMetaTileEntity, aPlayer);
        return false;
    }
}
