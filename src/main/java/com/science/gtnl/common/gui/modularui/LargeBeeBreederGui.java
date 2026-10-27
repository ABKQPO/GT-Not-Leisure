// SPDX-License-Identifier: MIT
package com.science.gtnl.common.gui.modularui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerGhostIngredientSlot;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.PageButton;
import com.cleanroommc.modularui.widgets.PagedWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.machine.multiblock.LargeBeeBreeder;
import com.science.gtnl.utils.machine.bee.BeeBreedingHelper;
import com.science.gtnl.utils.machine.bee.BeeBreedingPlanner;

import cpw.mods.fml.common.network.ByteBufUtils;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;

public final class LargeBeeBreederGui extends GTNLMultiBlockBaseGui<LargeBeeBreeder> {

    private static final int PAGE_WIDTH = 348;
    private static final int PAGE_HEIGHT = 180;
    private static final int SPECIES_ROWS = 6;
    private static final int ROUTE_ROWS = 3;

    private final PagedWidget.Controller pages = new PagedWidget.Controller();
    private String targetDraft;
    private int cachedRevision = Integer.MIN_VALUE;
    private List<String> serverArchive = Collections.emptyList();
    private List<String> serverReachable = Collections.emptyList();
    private List<NBTTagCompound> serverSteps = Collections.emptyList();
    private List<String> archive = Collections.emptyList();
    private List<String> reachable = Collections.emptyList();
    private List<NBTTagCompound> steps = Collections.emptyList();
    private int routePage;
    private final SpeciesListView archiveView = new SpeciesListView(() -> archive);
    private final SpeciesListView reachableView = new SpeciesListView(() -> reachable);
    private StringSyncValue draft;
    private StringSyncValue target;
    private BooleanSyncValue valid;
    private IntSyncValue pending;
    private IntSyncValue total;
    private IntSyncValue completed;
    private IntSyncValue bonus;
    private LongSyncValue energy;
    private StringSyncValue missing;

    public LargeBeeBreederGui(LargeBeeBreeder multiblock) {
        super(multiblock);
        targetDraft = multiblock.getTargetBeeSpecies();
    }

    @Override
    protected void registerSyncValues(PanelSyncManager sync) {
        super.registerSyncValues(sync);
        target = new StringSyncValue(multiblock::getTargetBeeSpecies);
        draft = new StringSyncValue(
            () -> targetDraft,
            value -> targetDraft = value.length() > 256 ? value.substring(0, 256) : value).allowC2S();
        valid = new BooleanSyncValue(multiblock::isTargetInputValid);
        pending = new IntSyncValue(multiblock::getPendingPrincessOutputs);
        total = new IntSyncValue(multiblock::getChainTotalSteps);
        completed = new IntSyncValue(multiblock::getChainCompletedSteps);
        bonus = new IntSyncValue(multiblock::getMutationChanceBonus);
        energy = new LongSyncValue(multiblock::getEuPerBreedingAttempt);
        missing = new StringSyncValue(() -> multiblock.isAllTasksBlocked() ? multiblock.getMissingDroneSpecies() : "");
        sync.syncValue("beeTarget", target);
        sync.syncValue("beeDraft", draft);
        sync.syncValue("beeValid", valid);
        sync.syncValue("beePending", pending);
        sync.syncValue("beeTotal", total);
        sync.syncValue("beeCompleted", completed);
        sync.syncValue("beeBonus", bonus);
        sync.syncValue("beeEnergy", energy);
        sync.syncValue("beeMissing", missing);
        sync.syncValue("beeApply", new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (mouse.mouseButton != 0) return;
            if (mouse.isClient()) {
                var species = BeeBreedingHelper.getSpeciesByUID(targetDraft.trim());
                if (species == null) species = BeeBreedingHelper.getSpeciesByName(targetDraft.trim());
                if (species != null) draft.setStringValue(species.getUID());
            } else {
                multiblock.setTargetBeeSpecies(targetDraft);
                if (multiblock.isTargetInputValid()) targetDraft = multiblock.getTargetBeeSpecies();
            }
        }));
        GenericListSyncHandler<String> archiveSync = new GenericListSyncHandler<>(
            this::archiveSnapshot,
            value -> archive = value,
            ByteBufUtils::readUTF8String,
            ByteBufUtils::writeUTF8String,
            String::equals,
            value -> value);
        GenericListSyncHandler<NBTTagCompound> routeSync = new GenericListSyncHandler<>(this::routeSnapshot, value -> {
            steps = value;
            routePage = Math.min(routePage, routePageCount() - 1);
        },
            ByteBufUtils::readTag,
            ByteBufUtils::writeTag,
            NBTTagCompound::equals,
            value -> (NBTTagCompound) value.copy());
        sync.syncValue("beeArchive", archiveSync);
        sync.syncValue(
            "beeReachable",
            new GenericListSyncHandler<>(
                this::reachableSnapshot,
                value -> reachable = value,
                ByteBufUtils::readUTF8String,
                ByteBufUtils::writeUTF8String,
                String::equals,
                value -> value));
        sync.syncValue("beeRoute", routeSync);
    }

    private List<String> archiveSnapshot() {
        refreshSnapshots();
        return serverArchive;
    }

    private List<NBTTagCompound> routeSnapshot() {
        refreshSnapshots();
        return serverSteps;
    }

    private List<String> reachableSnapshot() {
        refreshSnapshots();
        return serverReachable;
    }

    private void refreshSnapshots() {
        if (cachedRevision == multiblock.getBreedingRevision()) return;
        cachedRevision = multiblock.getBreedingRevision();
        List<String> nextArchive = new ArrayList<>(
            multiblock.getDronePool()
                .getAvailableSpecies());
        Collections.sort(nextArchive);
        if (!serverArchive.equals(nextArchive)) {
            serverArchive = nextArchive;
            serverReachable = BeeBreedingPlanner.findReachableSpecies(new HashSet<>(serverArchive));
        }
        serverSteps = new ArrayList<>();
        for (BeeBreedingHelper.BreedingStep step : multiblock.getBreedingSteps()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("parent1", step.parent1);
            tag.setString("parent2", step.parent2);
            tag.setString("result", step.result);
            tag.setDouble("chance", Math.min(100.0D, step.chance + multiblock.getMutationChanceBonus()));
            byte status = multiblock.getDronePool()
                .hasDrone(step.result) ? (byte) 2
                    : multiblock.getDronePool()
                        .hasDrone(step.parent1)
                        && multiblock.getDronePool()
                            .hasDrone(step.parent2) ? (byte) 1 : (byte) 0;
            tag.setByte("status", status);
            serverSteps.add(tag);
        }
    }

    @Override
    protected int getBasePanelWidth() {
        return 360;
    }

    @Override
    protected int getBasePanelHeight() {
        return 300;
    }

    @Override
    public Flow createMainColumn(ModularPanel panel, PanelSyncManager sync) {
        Flow tabs = Flow.row()
            .size(PAGE_WIDTH, 20)
            .marginBottom(4);
        // #tr gtnl.bee_breeder.run
        // # Operation
        // # zh_CN 运行
        tabs.child(tab(0, "gtnl.bee_breeder.run"));
        // #tr gtnl.bee_breeder.archive
        // # Species Archive
        // # zh_CN 物种档案
        tabs.child(tab(1, "gtnl.bee_breeder.archive"));
        // #tr gtnl.bee_breeder.reachable
        // # Breedable Bees
        // # zh_CN 可杂交到蜂
        tabs.child(tab(2, "gtnl.bee_breeder.reachable"));
        // #tr gtnl.bee_breeder.route
        // # Breeding Route
        // # zh_CN 杂交路线
        tabs.child(tab(3, "gtnl.bee_breeder.route"));
        return Flow.column()
            .padding(6)
            .child(tabs)
            .child(
                new PagedWidget<>().controller(pages)
                    .size(PAGE_WIDTH, PAGE_HEIGHT)
                    .addPage(operationPage(panel, sync))
                    .addPage(archivePage())
                    .addPage(reachablePage())
                    .addPage(routePage()))
            .child(
                Flow.column()
                    .size(PAGE_WIDTH, 76)
                    .marginTop(6)
                    .child(createInventoryRow(panel, sync)));
    }

    private PageButton tab(int index, String key) {
        return new PageButton(index, pages).size(84, 20)
            .marginRight(index == 3 ? 0 : 4)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .overlay(IKey.lang(key));
    }

    private ParentWidget<?> operationPage(ModularPanel panel, PanelSyncManager sync) {
        ParentWidget<?> page = new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT);
        // #tr gtnl.bee_breeder.target_hint
        // # Enter a species name / UID, or drop a bee from NEI
        // # zh_CN 输入蜂种名称 / UID，或从 NEI 拖入蜜蜂
        page.child(text(() -> tr("gtnl.bee_breeder.target_hint"), 0, 0, PAGE_WIDTH));
        page.child(
            new BeeSpeciesDropTextField().value(draft)
                .setMaxLength(256)
                .autoUpdateOnChange(true)
                .pos(0, 17)
                .size(286, 18));
        // #tr gtnl.bee_breeder.apply
        // # Apply
        // # zh_CN 应用
        page.child(
            new ButtonWidget<>().pos(292, 17)
                .size(56, 18)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .hoverBackground(GTGuiTextures.BUTTON_STANDARD_PRESSED)
                .overlay(IKey.lang("gtnl.bee_breeder.apply"))
                .syncHandler("beeApply"));
        // #tr gtnl.bee_breeder.current_target
        // # Target: %s
        // # zh_CN 当前目标：%s
        page.child(
            text(
                () -> tr("gtnl.bee_breeder.current_target", speciesOrEmpty(target.getStringValue())),
                0,
                40,
                PAGE_WIDTH));
        // #tr gtnl.bee_breeder.invalid_target
        // # Unknown species. The current target has not changed.
        // # zh_CN 未找到该蜂种，当前目标保持不变。
        // #tr gtnl.bee_breeder.draft_hint
        // # Press Apply to confirm. Apply an empty field to clear the target.
        // # zh_CN 点击应用确认；留空后应用可清除目标。
        page.child(
            text(
                () -> valid.getBoolValue() ? tr("gtnl.bee_breeder.draft_hint")
                    : EnumChatFormatting.RED + tr("gtnl.bee_breeder.invalid_target"),
                0,
                83,
                PAGE_WIDTH));
        // #tr gtnl.bee_breeder.overview
        // # Waiting: %s | Archived: %s | Parallel limit: %s
        // # zh_CN 待转换：%s | 已解锁：%s | 并行上限：%s
        page.child(
            text(
                () -> tr(
                    "gtnl.bee_breeder.overview",
                    pending.getIntValue(),
                    archive.size(),
                    multiblock.getMaxParallelBreedingSteps()),
                0,
                55,
                PAGE_WIDTH));
        // #tr gtnl.bee_breeder.energy
        // # Per attempt: %s EU/t | Chance bonus: +%s%%
        // # zh_CN 每次尝试：%s EU/t | 成功率加成：+%s%%
        page.child(
            text(() -> tr("gtnl.bee_breeder.energy", energy.getLongValue(), bonus.getIntValue()), 0, 69, PAGE_WIDTH));
        page.child(createTerminalRow(panel, sync).pos(0, 100));
        return page;
    }

    @Override
    protected int getTerminalRowWidth() {
        return PAGE_WIDTH;
    }

    @Override
    protected int getTerminalRowHeight() {
        return 80;
    }

    @Override
    protected int getTerminalWidgetWidth() {
        return PAGE_WIDTH;
    }

    @Override
    protected int getTerminalWidgetHeight() {
        return 80;
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager sync, ModularPanel panel) {
        return super.createTerminalTextWidget(sync, panel).child(terminalText(this::missingText));
    }

    private ParentWidget<?> archivePage() {
        // #tr gtnl.bee_breeder.search
        // # Search name or UID; select a species to fill the target
        // # zh_CN 搜索名称或 UID；点击蜂种可填入目标
        // #tr gtnl.bee_breeder.no_matches
        // # No matching archived species
        // # zh_CN 没有符合条件的已解锁蜂种
        return speciesPage(archiveView, "gtnl.bee_breeder.search", "gtnl.bee_breeder.no_matches");
    }

    private ParentWidget<?> reachablePage() {
        // #tr gtnl.bee_breeder.reachable_hint
        // # Search name / UID: reachable but unarchived (all breeding steps)
        // # zh_CN 搜索名称 / UID：根据档案可经多步杂交获得、尚未存档的蜂种
        // #tr gtnl.bee_breeder.no_reachable_matches
        // # No matching unarchived species are reachable from the current archive
        // # zh_CN 当前档案没有符合条件的可杂交未存档蜂种
        return speciesPage(reachableView, "gtnl.bee_breeder.reachable_hint", "gtnl.bee_breeder.no_reachable_matches");
    }

    private ParentWidget<?> speciesPage(SpeciesListView view, String hintKey, String emptyKey) {
        ParentWidget<?> page = new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT)
            .background(new Rectangle().color(0xFFC6C6C6));
        page.child(
            text(() -> tr(hintKey), 0, 0, PAGE_WIDTH).color(0xFF202020)
                .shadow(false));
        page.child(
            new TextFieldWidget().value(view.search)
                .setMaxLength(128)
                .autoUpdateOnChange(true)
                .pos(0, 16)
                .size(PAGE_WIDTH, 18));
        for (int row = 0; row < SPECIES_ROWS; row++) {
            final int index = row;
            page.child(
                new ButtonWidget<>().pos(0, 38 + row * 17)
                    .size(PAGE_WIDTH, 16)
                    .background(GTGuiTextures.BUTTON_STANDARD)
                    .hoverBackground(GTGuiTextures.BUTTON_STANDARD_PRESSED)
                    .setEnabledIf(
                        widget -> index == 0 || !view.uid(index)
                            .isEmpty())
                    .overlay(
                        IKey.dynamic(() -> speciesLabel(view, index, emptyKey))
                            .color(0xFF202020)
                            .shadow(false))
                    .tooltipBuilder(t -> {
                        String uid = view.uid(index);
                        if (!uid.isEmpty()) t.addLine(uid);
                    })
                    .onMousePressed(mouse -> {
                        if (mouse == 0) {
                            String uid = view.uid(index);
                            if (!uid.isEmpty()) {
                                draft.setStringValue(uid);
                                pages.setPage(0);
                            }
                        }
                        return true;
                    }));
        }
        page.child(
            new ButtonWidget<>().pos(0, 143)
                .size(30, 16)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .hoverBackground(GTGuiTextures.BUTTON_STANDARD_PRESSED)
                .overlay(
                    IKey.str("<")
                        .color(0xFF202020)
                        .shadow(false))
                .onMousePressed(mouse -> {
                    if (mouse == 0) view.page = Math.max(0, view.page - 1);
                    return true;
                }));
        page.child(
            new ButtonWidget<>().pos(318, 143)
                .size(30, 16)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .hoverBackground(GTGuiTextures.BUTTON_STANDARD_PRESSED)
                .overlay(
                    IKey.str(">")
                        .color(0xFF202020)
                        .shadow(false))
                .onMousePressed(mouse -> {
                    if (mouse == 0) view.page = Math.min(view.pageCount() - 1, view.page + 1);
                    return true;
                }));
        // #tr gtnl.bee_breeder.archive_count
        // # %s species | Page %s/%s
        // # zh_CN %s 个蜂种 | 第 %s/%s 页
        page.child(
            text(
                () -> tr(
                    "gtnl.bee_breeder.archive_count",
                    view.filtered()
                        .size(),
                    view.page + 1,
                    view.pageCount()),
                40,
                146,
                268).color(0xFF202020)
                    .shadow(false));
        return page;
    }

    private String speciesLabel(SpeciesListView view, int row, String emptyKey) {
        String uid = view.uid(row);
        if (uid.isEmpty()) return row == 0 ? tr(emptyKey) : "";
        return BeeBreedingHelper.getSpeciesDisplayName(uid);
    }

    private ParentWidget<?> routePage() {
        ParentWidget<?> page = new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT)
            .background(new Rectangle().color(0xFFC6C6C6));
        // #tr gtnl.bee_breeder.route_progress
        // # Route: %s/%s | Target: %s
        // # zh_CN 路线：%s/%s | 目标：%s
        page.child(
            text(
                () -> tr(
                    "gtnl.bee_breeder.route_progress",
                    completed.getIntValue(),
                    total.getIntValue(),
                    speciesOrEmpty(target.getStringValue())),
                0,
                0,
                PAGE_WIDTH));
        // #tr gtnl.bee_breeder.no_steps
        // # No pending route. Select a target; archived targets need no further breeding.
        // # zh_CN 暂无待杂交步骤。请选择目标；已解锁目标无需再次杂交。
        page.child(
            IKey.lang("gtnl.bee_breeder.no_steps")
                .asWidget()
                .pos(0, 17)
                .size(PAGE_WIDTH, 40)
                .setEnabledIf(widget -> steps.isEmpty()));
        for (int row = 0; row < ROUTE_ROWS; row++) {
            final int index = row;
            page.child(new TextWidget<>(IKey.dynamic(() -> {
                NBTTagCompound step = routeStep(index);
                return step == null ? "" : routeLabel(step);
            })).pos(0, 17 + row * 42)
                .size(PAGE_WIDTH, 38)
                .alignment(Alignment.CenterLeft)
                .setEnabledIf(widget -> routeStep(index) != null)
                .tooltipAutoUpdate(true)
                .tooltipBuilder(t -> {
                    NBTTagCompound step = routeStep(index);
                    if (step != null) t.addLine(step.getString("parent1") + " + " + step.getString("parent2"))
                        .addLine(step.getString("result"));
                }));
        }
        page.child(
            new ButtonWidget<>().pos(0, 143)
                .size(30, 16)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .hoverBackground(GTGuiTextures.BUTTON_STANDARD_PRESSED)
                .overlay(IKey.str("<"))
                .onMousePressed(mouse -> {
                    if (mouse == 0) routePage = Math.max(0, routePage - 1);
                    return true;
                }));
        page.child(
            new ButtonWidget<>().pos(318, 143)
                .size(30, 16)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .hoverBackground(GTGuiTextures.BUTTON_STANDARD_PRESSED)
                .overlay(IKey.str(">"))
                .onMousePressed(mouse -> {
                    if (mouse == 0) routePage = Math.min(routePageCount() - 1, routePage + 1);
                    return true;
                }));
        // #tr gtnl.bee_breeder.route_page
        // # Page %s/%s
        // # zh_CN 第 %s/%s 页
        page.child(text(() -> tr("gtnl.bee_breeder.route_page", routePage + 1, routePageCount()), 40, 146, 268));
        page.child(text(this::missingText, 0, 166, PAGE_WIDTH));
        return page;
    }

    private int routePageCount() {
        return Math.max(1, (steps.size() + ROUTE_ROWS - 1) / ROUTE_ROWS);
    }

    private NBTTagCompound routeStep(int row) {
        int index = routePage * ROUTE_ROWS + row;
        return index < steps.size() ? steps.get(index) : null;
    }

    private String routeLabel(NBTTagCompound step) {
        byte status = step.getByte("status");
        // #tr gtnl.bee_breeder.unlocked
        // # Unlocked
        // # zh_CN 已解锁
        // #tr gtnl.bee_breeder.ready
        // # Ready
        // # zh_CN 可杂交
        // #tr gtnl.bee_breeder.waiting
        // # Waiting for parents
        // # zh_CN 等待亲本
        String state = status == 2 ? tr("gtnl.bee_breeder.unlocked")
            : status == 1 ? tr("gtnl.bee_breeder.ready") : tr("gtnl.bee_breeder.waiting");
        EnumChatFormatting color = status == 2 ? EnumChatFormatting.DARK_GREEN
            : status == 1 ? EnumChatFormatting.DARK_AQUA : EnumChatFormatting.DARK_RED;
        return color + state
            + EnumChatFormatting.RESET
            + "  "
            + BeeBreedingHelper.getSpeciesDisplayName(step.getString("result"))
            + "  "
            + String.format(Locale.ROOT, "%.1f%%", step.getDouble("chance"))
            + "\n"
            + BeeBreedingHelper.getSpeciesDisplayName(step.getString("parent1"))
            + " + "
            + BeeBreedingHelper.getSpeciesDisplayName(step.getString("parent2"));
    }

    private String missingText() {
        // #tr gtnl.bee_breeder.missing
        // # Missing parent: %s
        // # zh_CN 缺少亲本：%s
        String uid = missing.getStringValue();
        return uid.isEmpty() ? ""
            : EnumChatFormatting.RED + tr("gtnl.bee_breeder.missing", BeeBreedingHelper.getSpeciesDisplayName(uid));
    }

    private static String speciesOrEmpty(String uid) {
        // #tr gtnl.bee_breeder.unset
        // # Not selected
        // # zh_CN 未选择
        return uid.isEmpty() ? tr("gtnl.bee_breeder.unset") : BeeBreedingHelper.getSpeciesDisplayName(uid);
    }

    private static TextWidget<?> text(Supplier<String> value, int x, int y, int width) {
        return new TextWidget<>(IKey.dynamic(value)).pos(x, y)
            .size(width, 12)
            .alignment(Alignment.CenterLeft);
    }

    private static TextWidget<?> terminalText(Supplier<String> value) {
        return new TextWidget<>(IKey.dynamic(value)).fullWidth()
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE);
    }

    private static String tr(String key, Object... args) {
        return StatCollector.translateToLocalFormatted(key, args);
    }

    private static final class SpeciesListView {

        private final Supplier<List<String>> source;
        private final StringValue search = new StringValue("");
        private List<String> lastSource;
        private List<String> filtered = Collections.emptyList();
        private String lastSearch;
        private int page;

        private SpeciesListView(Supplier<List<String>> source) {
            this.source = source;
        }

        private List<String> filtered() {
            List<String> values = source.get();
            String query = search.getStringValue()
                .trim()
                .toLowerCase(Locale.ROOT);
            if (lastSource != values || !query.equals(lastSearch)) {
                lastSource = values;
                lastSearch = query;
                page = 0;
                filtered = new ArrayList<>();
                for (String uid : values) {
                    if (uid.toLowerCase(Locale.ROOT)
                        .contains(query)
                        || BeeBreedingHelper.getSpeciesDisplayName(uid)
                            .toLowerCase(Locale.ROOT)
                            .contains(query)) {
                        filtered.add(uid);
                    }
                }
                filtered.sort(
                    Comparator.comparing(BeeBreedingHelper::getSpeciesDisplayName)
                        .thenComparing(uid -> uid));
            }
            return filtered;
        }

        private int pageCount() {
            return Math.max(1, (filtered().size() + SPECIES_ROWS - 1) / SPECIES_ROWS);
        }

        private String uid(int row) {
            List<String> values = filtered();
            int index = page * SPECIES_ROWS + row;
            return index < values.size() ? values.get(index) : "";
        }
    }

    private static final class BeeSpeciesDropTextField extends TextFieldWidget
        implements RecipeViewerGhostIngredientSlot<ItemStack> {

        @Override
        public boolean handleDragAndDrop(@NotNull ItemStack stack, int button) {
            if (button != 0 || !BeeBreedingHelper.isBee(stack)) return false;
            String uid = BeeBreedingHelper.getBeeUID(stack);
            if (uid == null || uid.isEmpty()) return false;
            setText(uid);
            onTextChanged();
            return true;
        }
    }
}
