package com.science.gtnl.common.machine.basicMachine;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;

public class Replicator extends MTEBasicMachine {

    public Replicator(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            1,
            new String[] {},
            1,
            1,
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_SIDE_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_SIDE_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_SIDE_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_FRONT_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_FRONT_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_TOP_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_TOP_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_TOP_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_BOTTOM_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_GLOW)
                    .glow()
                    .build()));
    }

    public Replicator(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures, 1, 1);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new Replicator(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public int getCapacity() {
        return 256000;
    }

    @Override
    public long maxEUStore() {
        return 0;
    }

    @Override
    public boolean hasEnoughEnergyToCheckRecipe() {
        return true;
    }

    @Override
    public boolean drainEnergyForProcess(long aEUt) {
        return true;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return getFillableStack() != null;
    }

    @Override
    public int checkRecipe() {
        ItemStack input = getInputAt(0);
        if (input == null) return 0;
        ItemStack output = input.copy();
        output.stackSize = 1;
        if (mFluid.getFluid()
            .equals(Materials.UUMatter.mFluid) && mFluid.amount >= 100
            && canOutput(output)) {
            this.mFluid.amount -= 100;
            this.mOutputItems[0] = output;
            this.mMaxProgresstime = 1;
            this.mEUt = 0;
            return 2;
        }
        return 0;
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 1) {
            GTUtility.doSoundAtClient(SoundResource.IC2_MACHINES_MAGNETIZER_LOOP, 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public void startProcess() {
        sendLoopStart((byte) 1);
    }
}
