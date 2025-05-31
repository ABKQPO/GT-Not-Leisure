package com.science.gtnl.common.machine.hatch;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUIInfos;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.render.TextureFactory;

public class CustomMaintenanceHatch extends MTEHatchMaintenance
    implements ICleanRoomMaintenance, IConfigurationMaintenance, IAddGregtechLogo {

    private static Textures.BlockIcons.CustomIcon face;
    protected int mMinConfigTime;
    protected int mMaxConfigTime;
    protected int mConfigTime = 100;
    protected int mCleanroomTier;
    protected String[] mDescription;

    public CustomMaintenanceHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = 100;
        mMaxConfigTime = 100;
    }

    public CustomMaintenanceHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aTier,
        String[] aDescription) {
        super(aID, aName, aNameRegional, aTier);
        mDescription = aDescription;
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = 100;
        mMaxConfigTime = 100;
    }

    public CustomMaintenanceHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aMinConfigTime,
        int aMaxConfigTime, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomMaintenanceHatch(int aID, String aName, String aNameRegional, int aCleanroomTier, int aMinConfigTime,
        int aMaxConfigTime, int aTier, String[] aDescription) {
        super(aID, aName, aNameRegional, aTier);
        mDescription = aDescription;
        mCleanroomTier = aCleanroomTier;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomMaintenanceHatch(int aID, String aName, String aNameRegional, int aMinConfigTime, int aMaxConfigTime,
        int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mCleanroomTier = 0;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomMaintenanceHatch(int aID, String aName, String aNameRegional, int aMinConfigTime, int aMaxConfigTime,
        int aTier, String[] aDescription) {
        super(aID, aName, aNameRegional, aTier);
        mDescription = aDescription;
        mCleanroomTier = 0;
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
    }

    public CustomMaintenanceHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures,
        int aMinConfigTime, int aMaxConfigTime) {
        super(aName, aTier, aDescription, aTextures, false);
        mMinConfigTime = aMinConfigTime;
        mMaxConfigTime = aMaxConfigTime;
        mDescription = aDescription;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new CustomMaintenanceHatch(
            this.mName,
            this.mTier,
            this.mDescriptionArray,
            this.mTextures,
            this.mMinConfigTime,
            this.mMaxConfigTime);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public String[] getDescription() {
        return mDescription;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        super.registerIcons(aBlockIconRegister);
        face = new Textures.BlockIcons.CustomIcon("iconsets/OVERLAY_FULLAUTOMAINTENANCE");
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(face) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(face) };
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        this.mWrench = this.mScrewdriver = this.mSoftMallet = this.mHardHammer = this.mCrowbar = this.mSolderingTool = true;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public int getConfigTime() {
        return this.mConfigTime;
    }

    @Override
    public int getCleanroomTier() {
        return 0;
    }

    @Override
    public boolean isConfiguration() {
        return getMinConfigTime() != 100 || getMaxConfigTime() != 100;
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
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mConfigTime", mConfigTime);
        aNBT.setInteger("mMinConfigTime", mMinConfigTime);
        aNBT.setInteger("mMaxConfigTime", mMaxConfigTime);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mConfigTime = aNBT.getInteger("mConfigTime");
        mMinConfigTime = aNBT.getInteger("mMinConfigTime");
        mMaxConfigTime = aNBT.getInteger("mMaxConfigTime");
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, ForgeDirection side,
        float aX, float aY, float aZ) {
        return onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (isConfiguration()) GTUIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
        return true;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        if (isConfiguration()) {
            builder.widget(
                TextWidget.localised("Info_ConfigurationMaintenanceHatch_00")
                    .setPos(49, 18)
                    .setSize(81, 14))
                .widget(
                    new TextFieldWidget().setSetterInt(val -> mConfigTime = val)
                        .setGetterInt(() -> mConfigTime)
                        .setNumbers(getMinConfigTime(), getMaxConfigTime())
                        .setOnScrollNumbers(1, 2, 5)
                        .setTextAlignment(Alignment.Center)
                        .setTextColor(Color.WHITE.normal)
                        .setSize(70, 18)
                        .setPos(54, 36)
                        .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));
        }
    }
}
