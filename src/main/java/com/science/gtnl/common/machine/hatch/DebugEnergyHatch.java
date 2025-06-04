package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.GTValues.VN;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.api.NumberFormatMUI;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.gui.modularui.GTUIInfos;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.util.GTUtility;
import tectech.thing.metaTileEntity.Textures;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaser;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaserMirror;
import tectech.util.CommonValues;
import tectech.util.TTUtility;

public class DebugEnergyHatch extends MTEHatchEnergy implements IAddUIWidgets, IAddGregtechLogo {

    public long EUT = 0, AMP = 0;
    public boolean producing = true;
    private static final NumberFormatMUI numberFormat = new NumberFormatMUI();

    public DebugEnergyHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        TTUtility.setTier(aTier, this);
    }

    @Override
    public String[] getDescription() {

        ArrayList<String> desc = new ArrayList<>();

        desc.add(StatCollector.translateToLocal("Tooltip_DebugEnergyHatch_00"));
        desc.add(StatCollector.translateToLocal("Tooltip_DebugEnergyHatch_01"));
        desc.add(StatCollector.translateToLocal("Tooltip_DebugEnergyHatch_02"));

        return desc.toArray(new String[] {});
    }

    public DebugEnergyHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        TTUtility.setTier(aTier, this);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        GTUIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
        return true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DebugEnergyHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        return new ITexture[] { super.getTexture(aBaseMetaTileEntity, side, facing, colorIndex, aActive, aRedstone)[0],
            side != facing
                ? (aActive ? Textures.OVERLAYS_ENERGY_OUT_POWER_TT[mTier] : Textures.OVERLAYS_ENERGY_IN_POWER_TT[mTier])
                : Textures.OVERLAYS_ENERGY_IN_WIRELESS_LASER[mTier] };
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("eEUT", EUT);
        aNBT.setLong("eAMP", AMP);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        EUT = aNBT.getLong("eEUT");
        AMP = aNBT.getLong("eAMP");
        producing = AMP * EUT >= 0;
        getBaseMetaTileEntity().setActive(producing);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            aBaseMetaTileEntity.setActive(producing);
            if (aBaseMetaTileEntity.isActive()) {
                byte Tick = (byte) (aTick % 20);
                if (CommonValues.TRANSFER_AT == Tick) {
                    setEUVar(maxEUStore());
                    moveAround(aBaseMetaTileEntity);
                } else {
                    setEUVar(maxEUStore());
                }
            } else {
                byte Tick = (byte) (aTick % 20);
                if (CommonValues.TRANSFER_AT == Tick) {
                    setEUVar(0);
                } else {
                    setEUVar(0);
                }
            }
        }
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
    public long maxAmperesIn() {
        return Math.abs(AMP);
    }

    @Override
    public long maxAmperesOut() {
        return Math.abs(AMP);
    }

    @Override
    public long maxEUInput() {
        return Math.abs(EUT);
    }

    @Override
    public long maxEUOutput() {
        return Math.abs(EUT);
    }

    @Override
    public long maxEUStore() {
        return Math.abs(EUT * AMP);
    }

    @Override
    public long getMinimumStoredEU() {
        return Math.abs(EUT * AMP);
    }

    @Override
    public int getProgresstime() {
        return (int) getBaseMetaTileEntity().getUniversalEnergyStored();
    }

    @Override
    public int maxProgresstime() {
        return (int) getBaseMetaTileEntity().getUniversalEnergyCapacity();
    }

    public long getEUT() {
        return EUT;
    }

    public void setEUT(long EUT) {
        this.EUT = EUT;
    }

    public long getAMP() {
        return AMP;
    }

    public void setAMP(long AMP) {
        this.AMP = AMP;
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

                // If we hit a mirror, use the mirror's view instead
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
                            AMP * 20L * maxEUOutput(),
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

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(111, 55));
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setSize(90, 72)
                .setPos(43, 4))
            .widget(
                new TextWidget().setStringSupplier(() -> "TIER: " + VN[GTUtility.getTier(Math.abs(EUT))])
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setPos(46, 22))
            .widget(
                new TextWidget().setStringSupplier(() -> "SUM: " + numberFormat.format(AMP * EUT))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setPos(46, 46));

        addLabelledIntegerTextField(builder, "EUT: ", this::getEUT, this::setEUT, 8);
        addLabelledIntegerTextField(builder, "AMP: ", this::getAMP, this::setAMP, 34);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> EUT -= val, 512, 64, 7, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> EUT /= val, 512, 64, 7, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> AMP -= val, 512, 64, 7, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> AMP /= val, 512, 64, 7, 58);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> EUT -= val, 16, 1, 25, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> EUT /= val, 16, 2, 25, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> AMP -= val, 16, 1, 25, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> AMP /= val, 16, 2, 25, 58);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> EUT += val, 16, 1, 133, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> EUT *= val, 16, 2, 133, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> AMP += val, 16, 1, 133, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> AMP *= val, 16, 2, 133, 58);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> EUT += val, 512, 64, 151, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> EUT *= val, 512, 64, 151, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> AMP += val, 512, 64, 151, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> AMP *= val, 512, 64, 151, 58);
    }

    private void addLabelledIntegerTextField(ModularWindow.Builder builder, String label, LongSupplier getter,
        LongConsumer setter, int yPos) {
        builder.widget(
            new TextWidget(label).setDefaultColor(COLOR_TEXT_WHITE.get())
                .setPos(46, yPos))
            .widget(
                new NumericWidget().setGetter(getter::getAsLong)
                    .setSetter(val -> setter.accept((int) val))
                    .setTextColor(COLOR_TEXT_WHITE.get())
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
                    .setPos(46 + 24, yPos - 1)
                    .setSize(56, 10));
    }

    private void addChangeNumberButton(ModularWindow.Builder builder, IDrawable overlay, Consumer<Integer> setter,
        int changeNumberShift, int changeNumber, int xPos, int yPos) {
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            setter.accept(clickData.shift ? changeNumberShift : changeNumber);
            producing = AMP * EUT >= 0;
        })
            .setBackground(GTUITextures.BUTTON_STANDARD, overlay)
            .setSize(18, 18)
            .setPos(xPos, yPos));
    }
}
