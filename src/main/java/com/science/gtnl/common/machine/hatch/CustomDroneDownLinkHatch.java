package com.science.gtnl.common.machine.hatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;
import com.science.gtnl.api.IConfigurationMaintenance;
import com.science.gtnl.common.gui.modularui.GTNLCustomDroneDownLinkHatchGui;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.tileentities.machines.multi.drone.MTEHatchDroneDownLink;
import lombok.Getter;
import lombok.Setter;

public class CustomDroneDownLinkHatch extends MTEHatchDroneDownLink
    implements IConfigurationMaintenance, IAddGregtechLogo, IDataCopyable {

    public static final String COPIED_DATA_IDENTIFIER = "customDroneDownLinkHatch";

    public int mMinConfigTime;
    public int mMaxConfigTime;
    public int mConfigTime = 100;
    @Getter
    @Setter
    public int mCleanroomTier;
    public String[] mDescription;

    public CustomDroneDownLinkHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = 100;
        mMaxConfigTime = 100;
    }

    public CustomDroneDownLinkHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aTier,
        String[] aDescription) {
        super(aID, aName, aNameRegional, aTier);
        mDescription = aDescription;
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = 100;
        mMaxConfigTime = 100;
    }

    public CustomDroneDownLinkHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aMinConfigTime,
        int aMaxConfigTime, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomDroneDownLinkHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aMinConfigTime,
        int aMaxConfigTime, int aTier, String[] aDescription) {
        super(aID, aName, aNameRegional, aTier);
        mDescription = aDescription;
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomDroneDownLinkHatch(int aID, String aName, String aNameRegional, int aMinConfigTime, int aMaxConfigTime,
        int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mCleanroomTier = 0;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomDroneDownLinkHatch(int aID, String aName, String aNameRegional, int aMinConfigTime, int aMaxConfigTime,
        int aTier, String[] aDescription) {
        super(aID, aName, aNameRegional, aTier);
        mDescription = aDescription;
        mCleanroomTier = 0;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomDroneDownLinkHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures,
        int aMinConfigTime, int aMaxConfigTime) {
        super(aName, aTier, aDescription, aTextures);
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
        mDescription = aDescription;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new CustomDroneDownLinkHatch(
            this.mName,
            this.mTier,
            this.mDescriptionArray,
            this.mTextures,
            this.mMinConfigTime,
            this.mMaxConfigTime);
    }

    @Override
    public String[] getDescription() {
        return mDescription;
    }

    @Override
    public int getConfigTime() {
        return this.mConfigTime;
    }

    @Override
    public boolean isConfiguration() {
        return mMinConfigTime != 100 || mMaxConfigTime != 100;
    }

    @Override
    public int getMinConfigTime() {
        return this.mMinConfigTime;
    }

    @Override
    public int getMaxConfigTime() {
        return this.mMaxConfigTime;
    }

    @Override
    public int getGUIHeight() {
        return isConfiguration() ? 85 : 40;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new GTNLCustomDroneDownLinkHatchGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    @Deprecated
    public void addGregTechLogo(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after CustomDroneDownLinkHatch mui2 rollout is complete.
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(125, 62));
    }

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after CustomDroneDownLinkHatch mui2 rollout is complete.
        super.addUIWidgets(builder, buildContext);
        if (isConfiguration()) {
            builder.widget(
                TextWidget.localised("Info_ConfigurationMaintenanceHatch_00")
                    .setTextAlignment(Alignment.Center)
                    .setPos(0, 38)
                    .setSize(150, 14))
                .widget(
                    new TextFieldWidget().setSetterInt(val -> mConfigTime = val)
                        .setGetterInt(() -> mConfigTime)
                        .setNumbers(mMinConfigTime, mMaxConfigTime)
                        .setOnScrollNumbers(1, 2, 5)
                        .setTextAlignment(Alignment.Center)
                        .setTextColor(Color.WHITE.normal)
                        .setSize(70, 18)
                        .setPos(40, 56)
                        .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mConfigTime", mConfigTime);
        aNBT.setInteger("mMinConfigTime", mMinConfigTime);
        aNBT.setInteger("mMaxConfigTime", mMaxConfigTime);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mMinConfigTime = aNBT.getInteger("mMinConfigTime");
        mMaxConfigTime = aNBT.getInteger("mMaxConfigTime");
        setConfigTimeFromGui(aNBT.getInteger("mConfigTime"));
    }

    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return COPIED_DATA_IDENTIFIER;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("mConfigTime", mConfigTime);
        return tag;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        if (!aNBT.hasKey("mConfigTime")) return false;
        setConfigTimeFromGui(aNBT.getInteger("mConfigTime"));
        return true;
    }

    public void setConfigTimeFromGui(int configTime) {
        mConfigTime = Math.min(mMaxConfigTime, Math.max(mMinConfigTime, configTime));
    }
}
