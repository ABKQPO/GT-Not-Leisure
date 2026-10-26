package com.science.gtnl.common.machine.hatch;

import java.util.function.Predicate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.modularui.hatch.TypeFilteredInputBusMEGui;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.machine.ItemFilteredList;

import appeng.api.networking.GridFlags;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTRecipeBuilder;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class TypeFilteredInputBusME extends OredictInputBusME {

    @Nullable
    public String modid;
    @Nullable
    public String name = "*";
    public int meta = GTRecipeBuilder.WILDCARD;

    public TypeFilteredInputBusME(int aID, String aName, String aNameRegional, boolean isSuper) {
        super(aID, aName, aNameRegional, isSuper);
    }

    public TypeFilteredInputBusME(String aName, boolean autoPullAvailable, int aTier, String[] aDescription,
        ITexture[][][] aTextures, boolean isSuper) {
        super(aName, autoPullAvailable, aTier, aDescription, aTextures, isSuper);
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return isSuper ? "gtnl.hatch.type_filtered_input_bus_me.advanced.name"
            : "gtnl.hatch.type_filtered_input_bus_me.name";
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new TypeFilteredInputBusME(mName, autoPullAvailable, mTier, mDescriptionArray, mTextures, isSuper);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.0"),
            StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.1") + " "
                + GTValues.TIER_COLORS[isSuper ? 8 : 6],
            StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.2"),
            StatCollector
                .translateToLocalFormatted("gtnl.hatch.type_filtered_input_bus_me.tooltip.3", isSuper ? 100 : 16),
            StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.4"),
            StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.5"),
            StatCollector
                .translateToLocalFormatted("gtnl.hatch.type_filtered_input_bus_me.tooltip.6", isSuper ? 100 : 16),
            StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.7"),
            StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.8"),
            StatCollector.translateToLocal("gtnl.hatch.type_filtered_input_bus_me.tooltip.9") };
    }

    @Override
    public String[] getInfoData() {
        String busStatusKey = getProxy() != null && getProxy().isActive()
            ? "gtnl.hatch.type_filtered_input_bus_me.info.online"
            : IGregTechDeviceInformation
                .encode("gtnl.hatch.type_filtered_input_bus_me.info.offline", getAEDiagnostics());
        return new String[] { busStatusKey, hasFilter()
            ? IGregTechDeviceInformation.encode(
                "gtnl.hatch.type_filtered_input_bus_me.info.filtered.format",
                EnumChatFormatting.GREEN + (modid != null ? modid : "*")
                    + ":"
                    + (name != null ? name : "*")
                    + "@"
                    + (meta != GTRecipeBuilder.WILDCARD ? meta : "*")
                    + EnumChatFormatting.RESET)
            : IGregTechDeviceInformation.encode(
                "gtnl.hatch.type_filtered_input_bus_me.info.filtered.format",
                IGregTechDeviceInformation.translatable("gtnl.hatch.type_filtered_input_bus_me.info.filtered.unset")) };
    }

    @Override
    public boolean hasFilter() {
        return (modid != null && !modid.isEmpty()) || (name != null && !name.isEmpty())
            || (meta != GTRecipeBuilder.WILDCARD);
    }

    @Override
    public Predicate<IAEItemStack> createFilter() {
        if (!hasFilter()) return null;
        return ItemFilteredList.makeFilter(buildFilterString());
    }

    @Nullable
    public String getModid() {
        return modid;
    }

    @Nullable
    public String getNameFilter() {
        return name;
    }

    public int getMetaFilter() {
        return meta;
    }

    public String getModidForGui() {
        return modid == null ? "" : modid;
    }

    public String getNameFilterForGui() {
        return name == null ? "" : name;
    }

    public void setModid(@Nullable String modid) {
        this.modid = normalizeFilterValue(modid);
        refreshItemList();
        updateAllInformationSlots();
    }

    public void setNameFilter(@Nullable String name) {
        this.name = normalizeFilterValue(name);
        refreshItemList();
        updateAllInformationSlots();
    }

    public void setMetaFilter(int meta) {
        this.meta = meta;
        refreshItemList();
        updateAllInformationSlots();
    }

    public String buildFilterString() {
        StringBuilder sb = new StringBuilder();
        if (modid != null && !modid.isEmpty()) sb.append(modid);
        if (name != null && !name.isEmpty()) {
            if (!sb.isEmpty()) sb.append(":");
            sb.append(name);
        }
        if (meta != GTRecipeBuilder.WILDCARD) {
            sb.append("@")
                .append(meta);
        }
        return sb.toString();
    }

    @NotNull
    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable gridProxyable) {
                gridProxy = new AENetworkProxy(
                    gridProxyable,
                    "proxy",
                    isSuper ? GTNLItemList.SuperTypeFilteredInputBusME.get(1)
                        : GTNLItemList.TypeFilteredInputBusME.get(1),
                    true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                updateValidGridProxySides();

                var bmte = getBaseMetaTileEntity();
                if (bmte.getWorld() != null) {
                    gridProxy.setOwner(
                        bmte.getWorld()
                            .getPlayerEntityByName(bmte.getOwnerName()));
                }
            }
        }
        return gridProxy;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new TypeFilteredInputBusMEGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    public void saveFilter(NBTTagCompound aNBT) {
        if (modid != null) aNBT.setString("modid", modid);
        if (name != null) aNBT.setString("name", name);
        aNBT.setInteger("meta", meta);
    }

    @Override
    public void loadFilter(NBTTagCompound aNBT) {
        if (aNBT.hasKey("modid")) modid = aNBT.getString("modid");
        if (aNBT.hasKey("name")) name = aNBT.getString("name");
        if (aNBT.hasKey("meta")) meta = aNBT.getInteger("meta");
    }

    private String normalizeFilterValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
