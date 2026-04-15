package com.science.gtnl.common.machine.hatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.science.gtnl.api.IFluidsLockable;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.machine.FluidsLockWidget;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchVoid;
import gregtech.api.util.GTUtility;

public class SuperVoidHatch extends MTEHatchVoid implements IFluidsLockable, IAddGregtechLogo {

    public String[] lockedFluidNames = new String[100];

    public SuperVoidHatch(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SuperVoidHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperVoidHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean canStoreFluid(@NotNull FluidStack fluidStack) {
        if (isFluidsLocked()) {
            if (lockedFluidNames == null || lockedFluidNames.length == 0) {
                return false;
            }
            String fluidName = fluidStack.getFluid()
                .getName();
            for (String locked : lockedFluidNames) {
                if (fluidName.equals(locked)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("mMode", mMode);

        if (isFluidsLocked() && lockedFluidNames != null && lockedFluidNames.length > 0) {
            NBTTagList list = new NBTTagList();
            for (String name : lockedFluidNames) {
                if (name == null || name.isEmpty()) name = "";
                list.appendTag(new NBTTagString(name));
            }
            aNBT.setTag("lockedFluidNames", list);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mMode = aNBT.getByte("mMode");

        if (isFluidsLocked() && aNBT.hasKey("lockedFluidNames", 9)) {
            NBTTagList list = aNBT.getTagList("lockedFluidNames", 8);
            lockedFluidNames = new String[list.tagCount()];
            for (int i = 0; i < list.tagCount(); i++) {
                String s = list.getStringTagAt(i);
                lockedFluidNames[i] = GTUtility.isStringInvalid(s) ? null : s;
            }
        } else {
            lockedFluidNames = new String[100];
        }
    }

    @Override
    public String getLockedFluidName() {
        return lockedFluidNames[0];
    }

    @Override
    public String[] getLockedFluidNames() {
        return lockedFluidNames;
    }

    @Override
    public String getLockedFluidNames(int index) {
        return lockedFluidNames[index];
    }

    @Override
    public void setLockedFluidName(String lockedFluidName) {
        this.lockedFluidNames[0] = lockedFluidName;
        markDirty();
    }

    @Override
    public void setLockedFluidNames(String[] lockedFluidNames) {
        if (lockedFluidNames == null) return;
        this.lockedFluidNames = lockedFluidNames;
        markDirty();
    }

    @Override
    public void setLockedFluidNames(int index, String lockedFluidName) {
        if (lockedFluidName == null) return;
        this.lockedFluidNames[index] = lockedFluidName;
        markDirty();
    }

    @Override
    public void lockFluids(boolean lock, int index) {
        if (lock) {
            if (!isFluidsLocked()) {
                this.mMode = 9;
                markDirty();
            }
        } else {
            if (lockedFluidNames == null || Arrays.stream(lockedFluidNames)
                .allMatch(Objects::isNull)) {
                this.mMode = 0;
            }
            if (this.lockedFluidNames != null) {
                this.lockedFluidNames[index] = null;
            }
            markDirty();
        }
    }

    @Override
    public boolean isFluidLocked() {
        return mMode == 8 || mMode == 9;
    }

    @Override
    public boolean isFluidsLocked() {
        return mMode == 8 || mMode == 9;
    }

    @Override
    public boolean acceptsFluidsLock(String[] name) {
        return true;
    }

    @Override
    public boolean acceptsFluidsLock(String name, int index) {
        return true;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        Scrollable scrollable = new Scrollable().setVerticalScroll();
        SlotGroup slotGroup = new SlotGroup();

        int x = 0, y = 0;
        for (int i = 0; i < 100; i++) {
            FluidsLockWidget widget = new FluidsLockWidget(this, i);
            widget.setPos(new Pos2d(x * 18, y * 18))
                .setBackground(getGUITextureSet().getFluidSlot());
            slotGroup.addChild(widget);

            if (++x == 10) {
                x = 0;
                y++;
            }
        }

        scrollable.widget(slotGroup);

        builder.widget(
            scrollable.setSize(18 * 10 + 4, 72)
                .setPos(20, 9));

        addGregTechLogo(builder);
    }

    @Override
    public int getGUIWidth() {
        return 220;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(195, 83));
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ArrayList<>();

        info.add(
            EnumChatFormatting.BLUE + StatCollector.translateToLocal("GT5U.infodata.hatch.output")
                + EnumChatFormatting.RESET);

        info.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.hatch.output.fluid",
                EnumChatFormatting.GOLD
                    + (mFluid == null ? StatCollector.translateToLocal("GT5U.infodata.hatch.output.fluid.none")
                        : mFluid.getLocalizedName())
                    + EnumChatFormatting.RESET));

        info.add(
            EnumChatFormatting.GREEN + GTUtility.formatNumbers(mFluid == null ? 0 : mFluid.amount)
                + " L"
                + EnumChatFormatting.RESET
                + " "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(getCapacity())
                + " L"
                + EnumChatFormatting.RESET);

        if (!isFluidsLocked() || lockedFluidNames == null || lockedFluidNames.length == 0) {
            info.add(StatCollector.translateToLocal("GT5U.infodata.hatch.output.fluid.locked_to.none"));
        } else {
            info.add(
                StatCollector.translateToLocalFormatted(
                    "GT5U.infodata.hatch.output.fluid.locked_to",
                    getLockedFluidsLocalized()));
        }

        return info.toArray(new String[0]);
    }

    public String getLockedFluidsLocalized() {
        StringBuilder sb = new StringBuilder();
        for (String name : lockedFluidNames) {
            if (name != null) {
                FluidStack fs = FluidRegistry.getFluidStack(name, 1);
                if (fs != null) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(StatCollector.translateToLocal(fs.getUnlocalizedName()));
                }
            }
        }
        return sb.toString();
    }
}
