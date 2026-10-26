package com.science.gtnl.common.machine.hatch;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.modularui.api.NumberFormatMUI;
import com.science.gtnl.common.gui.modularui.hatch.DebugEnergyHatchGui;
import com.science.gtnl.mixins.early.gregtech.AccessorMTETieredMachineBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaser;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaserMirror;
import tectech.util.CommonValues;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class DebugEnergyHatch extends MTEHatchEnergy {

    private static final NumberFormatMUI numberFormat = new NumberFormatMUI();

    @Getter
    public long mEUT = 0, mAMP = 0;
    public boolean producing = true;

    public DebugEnergyHatch(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 14);
    }

    public DebugEnergyHatch(String aName, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, 14, aDescription, aTextures);
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.debug_energy.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    public void setMEUT(long eut) {
        mEUT = Math.max(0L, eut);
        updateProducingState();
    }

    public void setMAMP(long amp) {
        mAMP = Math.max(0L, amp);
        updateProducingState();
    }

    public static String formatPowerSum(long eut, long amp) {
        return numberFormat.format(amp * eut);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DebugEnergyHatch(mName, mDescriptionArray, mTextures);
    }

    public void setMEUTFromGui(long eut) {
        setMEUT(eut);
    }

    public void setMAMPFromGui(long amp) {
        setMAMP(amp);
    }

    @Override
    public long getInputTier() {
        return GTUtility.getTier(Math.abs(mEUT));
    }

    @Override
    public long getOutputTier() {
        return GTUtility.getTier(Math.abs(mEUT));
    }

    @Override
    public long maxAmperesIn() {
        return Math.abs(mAMP);
    }

    @Override
    public long maxAmperesOut() {
        return Math.abs(mAMP);
    }

    @Override
    public long maxEUInput() {
        return Math.abs(mEUT);
    }

    @Override
    public long maxEUOutput() {
        return Math.abs(mEUT);
    }

    @Override
    public long maxEUStore() {
        return Math.abs(mEUT * mAMP);
    }

    @Override
    public long getMinimumStoredEU() {
        return Math.abs(mEUT * mAMP);
    }

    @Override
    public int getProgresstime() {
        return (int) getBaseMetaTileEntity().getUniversalEnergyStored();
    }

    @Override
    public int maxProgresstime() {
        return (int) getBaseMetaTileEntity().getUniversalEnergyCapacity();
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        openGui(aPlayer);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            aBaseMetaTileEntity.setActive(producing);
            if (aBaseMetaTileEntity.isActive()) {
                byte tick = (byte) (aTick % 20);
                if (CommonValues.TRANSFER_AT == tick) {
                    setEUVar(maxEUStore());
                    moveAround(aBaseMetaTileEntity);
                } else {
                    setEUVar(maxEUStore());
                }
                ((AccessorMTETieredMachineBlock) this).setMachineTier(GTUtility.getTier(Math.abs(mEUT)));
            } else {
                setEUVar(0);
            }
        }
    }

    @Override
    public String[] getDescription() {
        ObjectArrayList<String> desc = new ObjectArrayList<>();
        desc.add(StatCollector.translateToLocal("gtnl.hatch.debug_energy.tooltip.0"));
        desc.add(StatCollector.translateToLocal("gtnl.hatch.debug_energy.tooltip.1"));
        desc.add(StatCollector.translateToLocal("gtnl.hatch.debug_energy.tooltip.2"));
        return desc.toArray(new String[] {});
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        return new ITexture[] { super.getTexture(aBaseMetaTileEntity, side, facing, colorIndex, aActive, aRedstone)[0],
            side != facing
                ? (aActive ? Textures.BlockIcons.OVERLAYS_ENERGY_OUT_MULTI_64A[mTier]
                    : Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_64A[mTier])
                : Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS_64A[mTier] };
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        return !producing && side != getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean isOutputFacing(ForgeDirection side) {
        return producing && side != getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new DebugEnergyHatchGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("mEUT", mEUT);
        aNBT.setLong("mAMP", mAMP);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mEUT = Math.max(0L, aNBT.getLong("mEUT"));
        mAMP = Math.max(0L, aNBT.getLong("mAMP"));
        updateProducingState();
        getBaseMetaTileEntity().setActive(producing);
    }

    private void updateProducingState() {
        producing = mAMP >= 0 && mEUT >= 0;
    }

    private void moveAround(IGregTechTileEntity aBaseMetaTileEntity) {
        for (final ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
            if (face == aBaseMetaTileEntity.getFrontFacing()) continue;
            ForgeDirection opposite = face.getOpposite();
            for (short dist = 1; dist < 1000; dist++) {
                IGregTechTileEntity tGTTileEntity = aBaseMetaTileEntity
                    .getIGregTechTileEntityAtSideAndDistance(face, dist);
                if (tGTTileEntity == null) {
                    break;
                }
                IMetaTileEntity aMetaTileEntity = tGTTileEntity.getMetaTileEntity();
                if (aMetaTileEntity == null) {
                    break;
                }

                if (aMetaTileEntity instanceof MTEPipeLaserMirror tMirror) {
                    tGTTileEntity = tMirror.bendAround(opposite);
                    if (tGTTileEntity == null) {
                        break;
                    } else {
                        aMetaTileEntity = tGTTileEntity.getMetaTileEntity();
                        opposite = tMirror.getChainedFrontFacing();
                    }
                }

                if (aMetaTileEntity instanceof MTEHatchEnergyTunnel && opposite == tGTTileEntity.getFrontFacing()) {
                    if (maxEUOutput() > ((MTEHatchEnergyTunnel) aMetaTileEntity).maxEUInput()) {
                        aMetaTileEntity.doExplosion(maxEUOutput());
                    } else {
                        long diff = Math.min(
                            mAMP * 20L * maxEUOutput(),
                            Math.min(
                                ((MTEHatchEnergyTunnel) aMetaTileEntity).maxEUStore()
                                    - aMetaTileEntity.getBaseMetaTileEntity()
                                        .getStoredEU(),
                                aBaseMetaTileEntity.getStoredEU()));
                        ((MTEHatchEnergyTunnel) aMetaTileEntity).setEUVar(
                            aMetaTileEntity.getBaseMetaTileEntity()
                                .getStoredEU() + diff);
                    }
                } else if (aMetaTileEntity instanceof MTEPipeLaser) {
                    if (((MTEPipeLaser) aMetaTileEntity).connectionCount < 2) {
                        break;
                    } else {
                        ((MTEPipeLaser) aMetaTileEntity).markUsed();
                    }
                } else {
                    break;
                }
            }
        }
    }
}
