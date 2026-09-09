package com.science.gtnl.common.machine.hatch;

import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.modularui.TypeFilteredInputBusMEGui;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.machine.ItemFilteredList;

import appeng.api.networking.GridFlags;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.enums.GTValues;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTRecipeBuilder;

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
    @Deprecated
    public ModularWindow createStackSizeConfigurationWindow(EntityPlayer player) {
        // TODO: Remove this mui1 fallback after TypeFilteredInputBusME mui2 parity is verified.
        final int WIDTH = 78;
        final int HEIGHT = 237;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();

        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)));

        builder.widget(
            TextWidget.localised("GT5U.machines.stocking_bus.min_stack_size")
                .setPos(3, 2)
                .setSize(74, 14))
            .widget(
                new NumericWidget().setSetter(val -> minAutoPullStackSize = (int) val)
                    .setGetter(() -> minAutoPullStackSize)
                    .setBounds(1, Integer.MAX_VALUE)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(70, 18)
                    .setPos(3, 18)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));

        builder.widget(
            TextWidget.localised("GT5U.machines.stocking_bus.refresh_time")
                .setPos(3, 42)
                .setSize(74, 14))
            .widget(
                new NumericWidget().setSetter(val -> autoPullRefreshTime = (int) val)
                    .setGetter(() -> autoPullRefreshTime)
                    .setBounds(1, Integer.MAX_VALUE)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(70, 18)
                    .setPos(3, 58)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));

        builder.widget(
            TextWidget.localised("gtnl.hatch.type_filtered_input_bus_me.info.mod_id")
                .setPos(3, 120)
                .setSize(60, 14))
            .widget(
                new TextFieldWidget().setSetter(this::setModid)
                    .setGetter(() -> modid != null ? modid : "")
                    .setTextAlignment(Alignment.Center)
                    .setScrollBar()
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .setPos(3, 136)
                    .setSize(70, 14)
                    .attachSyncer(new FakeSyncWidget.StringSyncer(this::getModid, this::setModid), builder));

        builder.widget(
            TextWidget.localised("gtnl.hatch.type_filtered_input_bus_me.info.item_id")
                .setPos(3, 154)
                .setSize(60, 14))
            .widget(
                new TextFieldWidget().setSetter(this::setNameFilter)
                    .setGetter(() -> name != null ? name : "")
                    .setTextAlignment(Alignment.Center)
                    .setScrollBar()
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .setPos(3, 170)
                    .setSize(70, 16)
                    .attachSyncer(new FakeSyncWidget.StringSyncer(this::getNameFilter, this::setNameFilter), builder));

        builder.widget(
            TextWidget.localised("gtnl.hatch.type_filtered_input_bus_me.info.item_meta")
                .setPos(3, 188)
                .setSize(60, 14))
            .widget(
                new NumericWidget().setSetter(val -> setMetaFilter((int) val))
                    .setGetter(() -> meta)
                    .setTextAlignment(Alignment.Center)
                    .setScrollBar()
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .setPos(3, 204)
                    .setSize(70, 16)
                    .attachSyncer(new FakeSyncWidget.IntegerSyncer(this::getMetaFilter, this::setMetaFilter), builder));

        return builder.build();
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
