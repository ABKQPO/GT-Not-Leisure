package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.GTValues.VN;

import java.util.ArrayList;
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
import com.science.gtnl.mixins.early.Gregtech.AccessorMTETieredMachineBlock;
import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import lombok.Getter;
import lombok.Setter;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaser;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaserMirror;
import tectech.util.CommonValues;

public class DebugEnergyHatch extends MTEHatchEnergy implements IAddUIWidgets, IAddGregtechLogo {

    @Setter
    @Getter
    public long mEUT = 0, mAMP = 0;
    public boolean producing = true;
    private static final NumberFormatMUI numberFormat = new NumberFormatMUI();

    public DebugEnergyHatch(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 14);
    }

    public DebugEnergyHatch(String aName, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, 14, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DebugEnergyHatch(mName, mDescriptionArray, mTextures);
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
    public String[] getDescription() {

        ArrayList<String> desc = new ArrayList<>();

        desc.add(StatCollector.translateToLocal("Tooltip_DebugEnergyHatch_00"));
        desc.add(StatCollector.translateToLocal("Tooltip_DebugEnergyHatch_01"));
        desc.add(StatCollector.translateToLocal("Tooltip_DebugEnergyHatch_02"));

        return desc.toArray(new String[] {});
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
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        return new ITexture[] { super.getTexture(aBaseMetaTileEntity, side, facing, colorIndex, aActive, aRedstone)[0],
            side != facing
                ? (aActive ? Textures.BlockIcons.OVERLAYS_ENERGY_OUT_MULTI_64A[mTier]
                    : Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_64A[mTier])
                : Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS_64A[mTier] };
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
        mEUT = aNBT.getLong("mEUT");
        mAMP = aNBT.getLong("mAMP");
        producing = mAMP * mEUT >= 0;
        getBaseMetaTileEntity().setActive(producing);
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
    public boolean isInputFacing(ForgeDirection side) {
        return !producing && side != getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean isOutputFacing(ForgeDirection side) {
        return producing && side != getBaseMetaTileEntity().getFrontFacing();
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
                new TextWidget().setStringSupplier(() -> "TIER: " + VN[GTUtility.getTier(Math.abs(mEUT))])
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setPos(46, 22))
            .widget(
                new TextWidget().setStringSupplier(() -> "SUM: " + numberFormat.format(mAMP * mEUT))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setPos(46, 46));

        addLabelledIntegerTextField(builder, "EUT: ", this::getMEUT, this::setMEUT, 8);
        addLabelledIntegerTextField(builder, "AMP: ", this::getMAMP, this::setMAMP, 34);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> mEUT -= val, 512, 64, 7, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> mEUT /= val, 512, 64, 7, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> mAMP -= val, 512, 64, 7, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_LARGE, val -> mAMP /= val, 512, 64, 7, 58);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> mEUT -= val, 16, 1, 25, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> mEUT /= val, 16, 2, 25, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> mAMP -= val, 16, 1, 25, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_MINUS_SMALL, val -> mAMP /= val, 16, 2, 25, 58);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> mEUT += val, 16, 1, 133, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> mEUT *= val, 16, 2, 133, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> mAMP += val, 16, 1, 133, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_SMALL, val -> mAMP *= val, 16, 2, 133, 58);

        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> mEUT += val, 512, 64, 151, 4);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> mEUT *= val, 512, 64, 151, 22);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> mAMP += val, 512, 64, 151, 40);
        addChangeNumberButton(builder, GTUITextures.OVERLAY_BUTTON_PLUS_LARGE, val -> mAMP *= val, 512, 64, 151, 58);
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

    private void addChangeNumberButton(ModularWindow.Builder builder, IDrawable overlay, IntConsumer setter,
        int changeNumberShift, int changeNumber, int xPos, int yPos) {
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            setter.accept(clickData.shift ? changeNumberShift : changeNumber);
            producing = mAMP * mEUT >= 0;
        })
            .setBackground(GTUITextures.BUTTON_STANDARD, overlay)
            .setSize(18, 18)
            .setPos(xPos, yPos));
    }
}
