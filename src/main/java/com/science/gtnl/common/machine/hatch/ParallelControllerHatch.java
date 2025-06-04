package com.science.gtnl.common.machine.hatch;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUIInfos;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;

public class ParallelControllerHatch extends MTEHatch implements IAddGregtechLogo {

    public static final String TEXTURE_OVERLAY_PARALLEL_CONTROLLER = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_PARALLEL_CONTROLLER";
    public static Textures.BlockIcons.CustomIcon OVERLAY_PARALLEL_CONTROLLER = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_PARALLEL_CONTROLLER);
    protected int maxParallel;
    protected int parallel = 1;

    public ParallelControllerHatch(int aID, String aName, String aNameRegional, int aTier, ITexture... aTextures) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            0,
            new String[] { StatCollector.translateToLocal("Tooltip_ParallelControllerHatch_00"), "", "", "" },
            aTextures);
        this.maxParallel = setMaxParallel(mTier);
        this.parallel = maxParallel;
        int speedBoost = setDurationModifier(mTier);
        int euDiscount = setEUtDiscount(mTier);
        mDescriptionArray[1] = String
            .format(StatCollector.translateToLocal("Tooltip_ParallelControllerHatch_01"), maxParallel);
        mDescriptionArray[2] = String
            .format(StatCollector.translateToLocal("Tooltip_ParallelControllerHatch_02"), speedBoost);
        mDescriptionArray[3] = String
            .format(StatCollector.translateToLocal("Tooltip_ParallelControllerHatch_03"), euDiscount);
    }

    public ParallelControllerHatch(String aName, int aTier, int aInvSlotCount, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aTier, aInvSlotCount, aDescription, aTextures);
        this.maxParallel = setMaxParallel(mTier);
        this.parallel = maxParallel;
        int speedBoost = setDurationModifier(mTier);
        int euDiscount = setEUtDiscount(mTier);
        mDescriptionArray[1] = String
            .format(StatCollector.translateToLocal("Tooltip_ParallelControllerHatch_01"), maxParallel);
        mDescriptionArray[2] = String
            .format(StatCollector.translateToLocal("Tooltip_ParallelControllerHatch_02"), speedBoost);
        mDescriptionArray[3] = String
            .format(StatCollector.translateToLocal("Tooltip_ParallelControllerHatch_03"), euDiscount);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ParallelControllerHatch(this.mName, this.mTier, 0, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 80));
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    @Override
    public boolean isLiquidInput(ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return false;
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_PARALLEL_CONTROLLER) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_PARALLEL_CONTROLLER) };
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("parallel", parallel);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        parallel = aNBT.getInteger("parallel");
    }

    public int setMaxParallel(int mTier) {
        if (mTier <= 1) {
            return 1;
        } else {
            return (int) Math.pow(4, mTier - 2);
        }
    }

    public int setDurationModifier(int mTier) {
        if (mTier <= 1) {
            return 1;
        } else {
            return 2 * (mTier - 1);
        }
    }

    public int setEUtDiscount(int mTier) {
        return 2 * mTier;
    }

    public int getMaxParallel() {
        return maxParallel;
    }

    public int getParallel() {
        return parallel;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) {
            return true;
        }

        GTUIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
        return true;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(
            TextWidget.localised("Info_ParallelControllerHatch_00")
                .setPos(49, 18)
                .setSize(81, 14))
            .widget(
                new TextFieldWidget().setSetterInt(val -> parallel = val)
                    .setGetterInt(() -> parallel)
                    .setNumbers(1, maxParallel)
                    .setOnScrollNumbers(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(70, 18)
                    .setPos(54, 36)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));
        addGregTechLogo(builder);
    }

}
