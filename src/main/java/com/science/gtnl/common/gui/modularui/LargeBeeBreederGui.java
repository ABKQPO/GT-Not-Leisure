package com.science.gtnl.common.gui.modularui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerGhostIngredientSlot;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.PageButton;
import com.cleanroommc.modularui.widgets.PagedWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.machine.multiblock.LargeBeeBreeder;
import com.science.gtnl.utils.machine.bee.BeeBreedingHelper;
import com.science.gtnl.utils.machine.bee.BeeBreedingPlanner;

import cpw.mods.fml.common.network.ByteBufUtils;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

public final class LargeBeeBreederGui extends GTNLMultiBlockBaseGui<LargeBeeBreeder> {

    private static final int PAGE_WIDTH = 232;
    private static final int PAGE_HEIGHT = 114;
    private static final int SPECIES_COLUMNS = 10;
    private static final int SPECIES_ROWS = 3;
    private static final int SPECIES_PER_PAGE = SPECIES_COLUMNS * SPECIES_ROWS;
    private static final int ROUTE_HEIGHT = PAGE_HEIGHT - 18;
    private static final int ROUTE_WIDTH = PAGE_WIDTH - 8;
    private static final UITexture ARROW_EMPTY = GTGuiTextures.PROGRESSBAR_ARROW_STANDARD.getSubArea(0, 0, 1, 0.5F);
    private static final UITexture ARROW_FULL = GTGuiTextures.PROGRESSBAR_ARROW_STANDARD.getSubArea(0, 0.5F, 1, 1);
    private static final UITexture ARROW_COMPLETE = ARROW_FULL.withColorOverride(0xFF55DD55);

    private final PagedWidget.Controller pages = new PagedWidget.Controller();
    private String targetDraft;
    private int cachedRevision = Integer.MIN_VALUE;
    private List<String> serverArchive = Collections.emptyList();
    private List<String> serverReachable = Collections.emptyList();
    private List<NBTTagCompound> serverSteps = Collections.emptyList();
    private String serverRouteTarget = "";
    private final Map<String, BeeBreedingHelper.BreedingStep> serverRoute = new LinkedHashMap<>();
    private List<String> archive = Collections.emptyList();
    private Set<String> archivedSpecies = Collections.emptySet();
    private List<String> reachable = Collections.emptyList();
    private List<NBTTagCompound> steps = Collections.emptyList();
    private Set<String> activeSpecies = Collections.emptySet();
    private List<NBTTagCompound> activeSteps = Collections.emptyList();
    private final Map<String, ItemStack> beeIcons = new HashMap<>();
    private final SpeciesListView archiveView = new SpeciesListView(() -> archive);
    private final SpeciesListView reachableView = new SpeciesListView(() -> reachable);
    private StringSyncValue draft;
    private StringSyncValue target;
    private BooleanSyncValue valid;
    private IntSyncValue pending;
    private IntSyncValue parallel;
    private IntSyncValue bonus;
    private LongSyncValue energy;
    private StringSyncValue missing;
    private IntSyncValue progressTime;
    private IntSyncValue maxProgressTime;
    private BooleanSyncValue machineActive;
    private BreedingRouteWidget routeWidget;

    public LargeBeeBreederGui(LargeBeeBreeder multiblock) {
        super(multiblock);
        targetDraft = multiblock.getTargetBeeSpecies();
    }

    @Override
    protected void registerSyncValues(PanelSyncManager sync) {
        super.registerSyncValues(sync);
        progressTime = sync.findSyncHandler("progressTime", IntSyncValue.class);
        maxProgressTime = sync.findSyncHandler("maxProgressTime", IntSyncValue.class);
        machineActive = sync.findSyncHandler("machineActive", BooleanSyncValue.class);
        target = new StringSyncValue(multiblock::getTargetBeeSpecies);
        draft = new StringSyncValue(
            () -> targetDraft,
            value -> targetDraft = value.length() > 256 ? value.substring(0, 256) : value).allowC2S();
        valid = new BooleanSyncValue(multiblock::isTargetInputValid);
        pending = new IntSyncValue(multiblock::getPendingPrincessOutputs);
        parallel = new IntSyncValue(multiblock::getMaxParallelBreedingSteps);
        bonus = new IntSyncValue(multiblock::getMutationChanceBonus);
        energy = new LongSyncValue(multiblock::getEuPerBreedingAttempt);
        missing = new StringSyncValue(() -> multiblock.isAllTasksBlocked() ? multiblock.getMissingDroneSpecies() : "");
        sync.syncValue("beeTarget", target);
        sync.syncValue("beeDraft", draft);
        sync.syncValue("beeValid", valid);
        sync.syncValue("beePending", pending);
        sync.syncValue("beeParallel", parallel);
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
        GenericListSyncHandler<String> archiveSync = new GenericListSyncHandler<>(this::archiveSnapshot, value -> {
            archive = value;
            archivedSpecies = new HashSet<>(value);
        }, ByteBufUtils::readUTF8String, ByteBufUtils::writeUTF8String, String::equals, value -> value);
        GenericListSyncHandler<NBTTagCompound> routeSync = new GenericListSyncHandler<>(
            this::routeSnapshot,
            value -> { steps = value; },
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
        sync.syncValue("beeActiveSteps", new GenericListSyncHandler<>(this::activeSnapshot, value -> {
            activeSteps = value;
            activeSpecies = new HashSet<>();
            for (NBTTagCompound step : value) activeSpecies.add(step.getString("result"));
        },
            ByteBufUtils::readTag,
            ByteBufUtils::writeTag,
            NBTTagCompound::equals,
            value -> (NBTTagCompound) value.copy()));
    }

    private List<NBTTagCompound> activeSnapshot() {
        List<NBTTagCompound> snapshot = new ArrayList<>();
        for (BeeBreedingHelper.BreedingStep step : multiblock.getActiveBreedingSteps()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("parent1", step.parent1);
            tag.setString("parent2", step.parent2);
            tag.setString("result", step.result);
            tag.setDouble("chance", Math.min(100D, step.chance));
            snapshot.add(tag);
        }
        return snapshot;
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
            multiblock.getSpeciesArchive()
                .getSpecies());
        Collections.sort(nextArchive);
        if (!serverArchive.equals(nextArchive)) {
            serverArchive = nextArchive;
            serverReachable = BeeBreedingPlanner.findReachableSpecies(new HashSet<>(serverArchive));
        }
        String currentTarget = multiblock.getTargetBeeSpecies();
        if (!serverRouteTarget.equals(currentTarget)) {
            serverRouteTarget = currentTarget;
            serverRoute.clear();
        }
        Set<String> remaining = new HashSet<>();
        for (BeeBreedingHelper.BreedingStep step : multiblock.getBreedingSteps()) {
            remaining.add(step.result);
            serverRoute.put(step.result, step);
        }
        // Keep completed steps visible; remove unfinished branches discarded by replanning.
        serverRoute.entrySet()
            .removeIf(
                entry -> !remaining.contains(entry.getKey()) && !multiblock.getSpeciesArchive()
                    .hasSpecies(entry.getKey()));
        serverSteps = new ArrayList<>();
        for (BeeBreedingHelper.BreedingStep step : serverRoute.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("parent1", step.parent1);
            tag.setString("parent2", step.parent2);
            tag.setString("result", step.result);
            tag.setDouble("chance", Math.min(100.0D, step.chance + multiblock.getMutationChanceBonus()));
            serverSteps.add(tag);
        }
    }

    @Override
    protected int getBasePanelWidth() {
        return PAGE_WIDTH + 12;
    }

    @Override
    protected int getBasePanelHeight() {
        return PAGE_HEIGHT + 116;
    }

    @Override
    public Flow createMainColumn(ModularPanel panel, PanelSyncManager sync) {
        Flow tabs = Flow.row()
            .size(PAGE_WIDTH, 18)
            .marginBottom(4);
        tabs.child(tab(0, "run", GTGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_ON));
        tabs.child(tab(1, "archive", new ItemDrawable(Items.book)));
        tabs.child(tab(2, "reachable", new ItemDrawable(Items.wheat)));
        tabs.child(tab(3, "route", new ItemDrawable(Items.map)));
        return Flow.column()
            .padding(6)
            .child(tabs)
            .child(
                new PagedWidget<>().controller(pages)
                    .size(PAGE_WIDTH, PAGE_HEIGHT)
                    .addPage(operationPage(panel, sync))
                    .addPage(speciesPage(archiveView, "search", "no_matches"))
                    .addPage(speciesPage(reachableView, "reachable_hint", "no_reachable_matches"))
                    .addPage(routePage()))
            .child(
                Flow.column()
                    .size(PAGE_WIDTH, 76)
                    .marginTop(6)
                    .child(createInventoryRow(panel, sync)));
    }

    private PageButton tab(int index, String key, IDrawable icon) {
        IKey label = IKey.lang("gtnl.bee_breeder.tab_" + key)
            .scale(0.75f)
            .color(0xFF202020)
            .shadow(false);
        return new PageButton(index, pages).size(55, 18)
            .marginRight(index == 3 ? 0 : 4)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .overlay((context, x, y, width, height, theme) -> {
                icon.draw(context, x + 3, y + 2, 14, 14, theme);
                label.draw(context, x + 19, y + 3, 34, 12, theme);
            })
            .tooltipBuilder(t -> t.addLine(IKey.lang("gtnl.bee_breeder." + key)));
    }

    private ParentWidget<?> operationPage(ModularPanel panel, PanelSyncManager sync) {
        ParentWidget<?> page = new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT);
        page.child(
            new BeeSpeciesDropTextField().value(draft)
                .setMaxLength(256)
                .autoUpdateOnChange(true)
                .pos(0, 0)
                .size(PAGE_WIDTH - 48, 18)
                .tooltipBuilder(
                    t -> t.addLine(IKey.lang("gtnl.bee_breeder.target_hint"))
                        .addLine(IKey.lang("gtnl.bee_breeder.draft_hint"))));
        page.child(
            new ButtonWidget<>().pos(PAGE_WIDTH - 44, 0)
                .size(44, 18)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .hoverBackground(GTGuiTextures.BUTTON_STANDARD_PRESSED)
                .overlay(IKey.lang("gtnl.bee_breeder.apply"))
                .syncHandler("beeApply")
                .tooltipAutoUpdate(true)
                .tooltipBuilder(
                    t -> t.addLine(
                        IKey.lang(
                            valid.getBoolValue() ? "gtnl.bee_breeder.draft_hint"
                                : "gtnl.bee_breeder.invalid_target"))));
        page.child(
            fittedText(this::previewChance, 50, 22, PAGE_WIDTH - 60, 0.85F).tooltipAutoUpdate(true)
                .tooltipBuilder(
                    t -> t.addLine(tr("gtnl.bee_breeder.current_target", speciesOrEmpty(target.getStringValue())))));
        page.child(previewBee("parent1", 20, 38));
        page.child(text(() -> "+", 44, 41, 10));
        page.child(previewBee("parent2", 60, 38));
        page.child(
            breedingArrow(this::previewStep, this::previewProgress, 104, 38).tooltipAutoUpdate(true)
                .tooltipBuilder(t -> {
                    t.addLine(
                        tr(
                            "gtnl.bee_breeder.attempt_time",
                            progressTime.getIntValue() / 20D,
                            maxProgressTime.getIntValue() / 20D));
                    t.addLine(tr("gtnl.bee_breeder.energy", energy.getLongValue(), bonus.getIntValue()));
                }));
        page.child(previewBee("result", 156, 38));
        page.child(fittedText(() -> speciesOrEmpty(previewSpecies("result")), 180, 41, 52, 0.75F));
        page.child(
            fittedText(
                () -> tr("gtnl.bee_breeder.overview", pending.getIntValue(), archive.size(), parallel.getIntValue()),
                0,
                58,
                PAGE_WIDTH,
                0.8F).tooltipAutoUpdate(true)
                    .tooltipBuilder(
                        t -> t.addLine(routeProgressText())
                            .addLine(tr("gtnl.bee_breeder.energy", energy.getLongValue(), bonus.getIntValue()))));
        page.child(createTerminalRow(panel, sync).pos(0, 72));
        return page;
    }

    @Override
    protected int getTerminalRowWidth() {
        return PAGE_WIDTH;
    }

    @Override
    protected int getTerminalRowHeight() {
        return 42;
    }

    @Override
    protected int getTerminalWidgetWidth() {
        return PAGE_WIDTH;
    }

    @Override
    protected int getTerminalWidgetHeight() {
        return 42;
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager sync, ModularPanel panel) {
        return super.createTerminalTextWidget(sync, panel).paddingRight(64)
            .child(terminalText(this::missingText));
    }

    @Override
    protected Flow createTerminalRightCornerColumn(ModularPanel panel, PanelSyncManager sync) {
        return Flow.row()
            .coverChildren()
            .rightRel(0, 6, 0)
            .bottomRel(0, 6, 0)
            .childIf(multiblock.supportsShutdownReasonHoverable(), () -> createShutdownReasonHoverableTerminal(sync))
            .childIf(multiblock.supportsMaintenanceIssueHoverable(), () -> createMaintIssueHoverableTerminal(sync))
            .childIf(multiblock.supportsLogo(), () -> makeLogoWidget(sync, panel));
    }

    private ParentWidget<?> speciesPage(SpeciesListView view, String hintKey, String emptyKey) {
        ParentWidget<?> page = new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT);
        page.child(
            new TextFieldWidget().value(view.search)
                .setMaxLength(128)
                .autoUpdateOnChange(true)
                .pos(0, 0)
                .size(PAGE_WIDTH, 18)
                .tooltipBuilder(t -> t.addLine(IKey.lang("gtnl.bee_breeder." + hintKey))));
        for (int index = 0; index < SPECIES_PER_PAGE; index++) {
            final int slot = index;
            page.child(
                new SlotLikeButtonWidget(() -> beeIcon(view.uid(slot)))
                    .pos(7 + index % SPECIES_COLUMNS * 22, 24 + index / SPECIES_COLUMNS * 22)
                    .size(18)
                    .setEnabledIf(
                        widget -> !view.uid(slot)
                            .isEmpty())
                    .tooltipAutoUpdate(true)
                    .tooltipBuilder(t -> {
                        String uid = view.uid(slot);
                        if (!uid.isEmpty()) t.addLine(BeeBreedingHelper.getSpeciesDisplayName(uid))
                            .addLine(uid)
                            .addLine(IKey.lang("gtnl.bee_breeder.select_species"));
                    })
                    .onMousePressed(button -> {
                        if (button == 0) {
                            String uid = view.uid(slot);
                            if (!uid.isEmpty()) {
                                draft.setStringValue(uid);
                                pages.setPage(0);
                            }
                        }
                        return true;
                    }));
        }
        page.child(
            IKey.lang("gtnl.bee_breeder." + emptyKey)
                .asWidget()
                .pos(6, 38)
                .size(PAGE_WIDTH - 12, 44)
                .color(0xFF202020)
                .shadow(false)
                .setEnabledIf(
                    widget -> view.filtered()
                        .isEmpty()));
        page.child(
            new ButtonWidget<>().pos(0, 96)
                .size(18, 16)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .overlay(IKey.str("<"))
                .onMousePressed(button -> {
                    if (button == 0) view.page = Math.max(0, view.page - 1);
                    return true;
                }));
        page.child(
            new ButtonWidget<>().pos(PAGE_WIDTH - 18, 96)
                .size(18, 16)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .overlay(IKey.str(">"))
                .onMousePressed(button -> {
                    if (button == 0) view.page = Math.min(view.pageCount() - 1, view.page + 1);
                    return true;
                }));
        page.child(
            text(
                () -> tr(
                    "gtnl.bee_breeder.archive_count",
                    view.filtered()
                        .size(),
                    view.page + 1,
                    view.pageCount()),
                24,
                98,
                PAGE_WIDTH - 48).alignment(Alignment.CENTER));
        return page;
    }

    private ParentWidget<?> routePage() {
        ParentWidget<?> page = new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT);
        VerticalScrollData vertical = new VerticalScrollData();
        ScrollWidget<?> viewport = new ScrollWidget<>(vertical).showScrollShadows(false)
            .pos(0, 18)
            .size(PAGE_WIDTH, ROUTE_HEIGHT)
            .background(GTGuiTextures.BACKGROUND_STANDARD);
        routeWidget = new BreedingRouteWidget(viewport, vertical);
        viewport.child(routeWidget);
        page.child(progressArrow(this::routeProgress, 0, 0));
        page.child(
            fittedText(this::routeProgressText, 24, 3, PAGE_WIDTH - 48, 0.8F).tooltipAutoUpdate(true)
                .tooltipBuilder(
                    t -> t.addLine(IKey.lang("gtnl.bee_breeder.route_session"))
                        .addLine(IKey.lang("gtnl.bee_breeder.route_hint"))));
        page.child(
            new ButtonWidget<>().pos(PAGE_WIDTH - 18, 0)
                .size(18)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .overlay(new ItemDrawable(Items.compass).asIcon())
                .tooltipBuilder(t -> t.addLine(IKey.lang("gtnl.bee_breeder.focus_target")))
                .onMousePressed(button -> {
                    if (button == 0) routeWidget.focusTarget();
                    return true;
                }));
        page.child(viewport);
        page.child(
            IKey.lang("gtnl.bee_breeder.no_steps")
                .asWidget()
                .pos(12, 34)
                .size(PAGE_WIDTH - 24, 54)
                .color(0xFF202020)
                .shadow(false)
                .setEnabledIf(
                    widget -> target.getStringValue()
                        .isEmpty()));
        return page;
    }

    private double cycleProgress() {
        int duration = maxProgressTime.getIntValue();
        return !machineActive.getBoolValue() || duration <= 0 ? 0D
            : Math.max(0D, Math.min(1D, (double) progressTime.getIntValue() / duration));
    }

    private int completedRouteSteps() {
        int count = 0;
        for (NBTTagCompound step : steps) {
            if (archivedSpecies.contains(step.getString("result"))) count++;
        }
        return count;
    }

    private double routeProgress() {
        if (archivedSpecies.contains(target.getStringValue())) return 1D;
        return steps.isEmpty() ? 0D : (double) completedRouteSteps() / steps.size();
    }

    private String routeProgressText() {
        if (!target.getStringValue()
            .isEmpty() && archivedSpecies.contains(target.getStringValue())) {
            return tr("gtnl.bee_breeder.unlocked");
        }
        return tr("gtnl.bee_breeder.route_meter", completedRouteSteps(), steps.size());
    }

    private ItemStack beeIcon(String uid) {
        if (uid.isEmpty()) return null;
        if (!beeIcons.containsKey(uid)) beeIcons.put(uid, BeeBreedingHelper.createDrone(uid));
        return beeIcons.get(uid);
    }

    private String missingText() {
        String uid = missing.getStringValue();
        return uid.isEmpty() ? ""
            : EnumChatFormatting.RED + tr("gtnl.bee_breeder.missing", BeeBreedingHelper.getSpeciesDisplayName(uid));
    }

    private static String speciesOrEmpty(String uid) {
        return uid.isEmpty() ? tr("gtnl.bee_breeder.unset") : BeeBreedingHelper.getSpeciesDisplayName(uid);
    }

    private static Widget<?> fittedText(Supplier<String> value, int x, int y, int width, float scale) {
        return new Widget<>().pos(x, y)
            .size(width, 12)
            .overlay((context, left, top, w, h, theme) -> {
                String full = value.get();
                String label = full;
                if (context.getFontRenderer()
                    .getStringWidth(full) * scale > w) {
                    label = context.getFontRenderer()
                        .trimStringToWidth(full, (int) (w / scale) - 6) + "…";
                }
                GuiDraw.drawText(label, left, top + 2, scale, 0xFF202020, false);
            });
    }

    private static TextWidget<?> text(Supplier<String> value, int x, int y, int width) {
        return new TextWidget<>(IKey.dynamic(value)).pos(x, y)
            .size(width, 12)
            .alignment(Alignment.CenterLeft)
            .color(0xFF202020)
            .shadow(false);
    }

    private static TextWidget<?> terminalText(Supplier<String> value) {
        return new TextWidget<>(IKey.dynamic(value)).fullWidth()
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE);
    }

    private static String tr(String key, Object... args) {
        return StatCollector.translateToLocalFormatted(key, args);
    }

    private static ProgressWidget progressArrow(DoubleSupplier progress, int x, int y) {
        return new ProgressWidget().value(new DoubleValue.Dynamic(progress, null))
            .texture(GTGuiTextures.PROGRESSBAR_ARROW_STANDARD, 20)
            .direction(ProgressWidget.Direction.RIGHT)
            .pos(x, y)
            .size(20, 18);
    }

    private ProgressWidget breedingArrow(Supplier<NBTTagCompound> step, DoubleSupplier progress, int x, int y) {
        return progressArrow(progress, x, y).texture(ARROW_EMPTY, ARROW_FULL, 20)
            .onUpdateListener(widget -> {
                NBTTagCompound current = step.get();
                widget.texture(
                    ARROW_EMPTY,
                    current != null && archivedSpecies.contains(current.getString("result")) ? ARROW_COMPLETE
                        : ARROW_FULL,
                    20);
            });
    }

    private NBTTagCompound previewStep() {
        if (!activeSteps.isEmpty()) return activeSteps.get(0);
        for (NBTTagCompound step : steps) if (routeState(step).equals("ready")) return step;
        for (NBTTagCompound step : steps) if (!archivedSpecies.contains(step.getString("result"))) return step;
        for (NBTTagCompound step : steps) if (step.getString("result")
            .equals(target.getStringValue())) return step;
        return null;
    }

    private double previewProgress() {
        NBTTagCompound step = previewStep();
        return step == null ? cycleProgress() : stepProgress(step);
    }

    private String previewSpecies(String key) {
        NBTTagCompound step = previewStep();
        return step != null ? step.getString(key) : key.equals("result") ? target.getStringValue() : "";
    }

    private String previewChance() {
        if (!valid.getBoolValue()) return EnumChatFormatting.DARK_RED + tr("gtnl.bee_breeder.invalid_short");
        NBTTagCompound step = previewStep();
        if (step != null) return tr("gtnl.bee_breeder.chance", step.getDouble("chance"));
        return tr("gtnl.bee_breeder." + (archivedSpecies.contains(target.getStringValue()) ? "unlocked" : "unset"));
    }

    private SlotLikeButtonWidget previewBee(String key, int x, int y) {
        return new SlotLikeButtonWidget(() -> beeIcon(previewSpecies(key))).pos(x, y)
            .size(18)
            .tooltipAutoUpdate(true)
            .tooltipBuilder(t -> {
                String uid = previewSpecies(key);
                if (!uid.isEmpty()) t.addLine(BeeBreedingHelper.getSpeciesDisplayName(uid))
                    .addLine(uid);
            });
    }

    private String routeState(NBTTagCompound step) {
        String result = step.getString("result");
        if (archivedSpecies.contains(result)) return "unlocked";
        if (machineActive.getBoolValue() && activeSpecies.contains(result)) return "active";
        if (archivedSpecies.contains(step.getString("parent1")) && archivedSpecies.contains(step.getString("parent2")))
            return "ready";
        return "waiting";
    }

    private double stepProgress(NBTTagCompound step) {
        return switch (routeState(step)) {
            case "unlocked" -> 1D;
            case "active" -> cycleProgress();
            default -> 0D;
        };
    }

    private SlotLikeButtonWidget routeBee(String uid, int x, int y) {
        return new SlotLikeButtonWidget(() -> beeIcon(uid)).pos(x, y)
            .size(18)
            .tooltipAutoUpdate(true)
            .tooltipBuilder(
                t -> t.addLine(BeeBreedingHelper.getSpeciesDisplayName(uid))
                    .addLine(uid));
    }

    private NBTTagCompound currentStep(NBTTagCompound step) {
        for (NBTTagCompound active : activeSteps) {
            if (active.getString("result")
                .equals(step.getString("result"))) return active;
        }
        return step;
    }

    private void addRouteLink(BreedingRouteWidget canvas, RouteNode parent, RouteNode result, RouteLayout layout) {
        int fromX = parent.x + 9, fromY = parent.y - 2;
        int toX = result.x + 9, toY = result.y + 20;
        List<int[]> points = new ArrayList<>();
        points.add(new int[] { fromX, fromY });
        boolean blocked = layout.nodes.values()
            .stream()
            .anyMatch(node -> node != parent && node != result && crossesNode(fromX, fromY, toX, toY, node));
        if (blocked) {
            int side = fromX < ROUTE_WIDTH / 2 ? 5 : ROUTE_WIDTH - 5;
            points.add(new int[] { fromX, fromY - 8 });
            points.add(new int[] { side, fromY - 8 });
            points.add(new int[] { side, toY + 8 });
            points.add(new int[] { toX, toY + 8 });
        }
        points.add(new int[] { toX, toY });
        double length = 0D;
        for (int i = 1; i < points.size(); i++)
            length += Math.hypot(points.get(i)[0] - points.get(i - 1)[0], points.get(i)[1] - points.get(i - 1)[1]);
        double offset = 0D;
        for (int i = 1; i < points.size(); i++) {
            int[] start = points.get(i - 1), end = points.get(i);
            double segmentLength = Math.hypot(end[0] - start[0], end[1] - start[1]);
            if (segmentLength < 1D) continue;
            double total = length, before = offset;
            boolean arrowHead = i == points.size() - 1;
            UITexture empty = arrowHead ? ARROW_EMPTY : ARROW_EMPTY.getSubArea(0, 0, 0.5F, 1);
            UITexture full = arrowHead ? ARROW_FULL : ARROW_FULL.getSubArea(0, 0, 0.5F, 1);
            UITexture complete = full.withColorOverride(0xFF55DD55);
            ProgressWidget arrow = progressArrow(
                () -> Math.max(0D, Math.min(1D, (stepProgress(result.step) * total - before) / segmentLength)),
                start[0],
                start[1]).size((int) Math.ceil(segmentLength), 8)
                    .texture(empty, full, 20)
                    .transform((widget, stack) -> {
                        stack.rotateZ((float) Math.atan2(end[1] - start[1], end[0] - start[0]));
                        stack.translate(0, -4);
                    })
                    .onUpdateListener(
                        widget -> widget.texture(empty, archivedSpecies.contains(result.uid) ? complete : full, 20))
                    .tooltipAutoUpdate(true)
                    .tooltipBuilder(
                        t -> t.addLine(IKey.lang("gtnl.bee_breeder." + routeState(result.step)))
                            .addLine(tr("gtnl.bee_breeder.chance", currentStep(result.step).getDouble("chance"))));
            canvas.child(arrow);
            offset += segmentLength;
        }
    }

    private static boolean crossesNode(int x1, int y1, int x2, int y2, RouteNode node) {
        double first = Math.max(0D, Math.min((node.y - 3D - y1) / (y2 - y1), (node.y + 21D - y1) / (y2 - y1)));
        double last = Math.min(1D, Math.max((node.y - 3D - y1) / (y2 - y1), (node.y + 21D - y1) / (y2 - y1)));
        if (first > last) return false;
        double firstX = x1 + (x2 - x1) * first, lastX = x1 + (x2 - x1) * last;
        return Math.min(firstX, lastX) < node.x + 21 && Math.max(firstX, lastX) > node.x - 3;
    }

    private final class BreedingRouteWidget extends ParentWidget<BreedingRouteWidget> implements Interactable {

        private final ScrollWidget<?> viewport;
        private final VerticalScrollData vertical;
        private List<NBTTagCompound> lastSteps;
        private String lastTarget = "";
        private RouteLayout layout = new RouteLayout(Collections.emptyList(), "");
        private int dragY;
        private int startScroll;

        private BreedingRouteWidget(ScrollWidget<?> viewport, VerticalScrollData vertical) {
            this.viewport = viewport;
            this.vertical = vertical;
            pos(0, 0).size(ROUTE_WIDTH, ROUTE_HEIGHT);
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            String currentTarget = target.getStringValue();
            if (lastSteps == steps && lastTarget.equals(currentTarget)) return;
            boolean targetChanged = !lastTarget.equals(currentTarget);
            lastSteps = steps;
            lastTarget = currentTarget;
            layout = new RouteLayout(steps, currentTarget);
            removeAll();
            for (RouteNode node : layout.nodes.values()) {
                for (RouteNode parent : node.parents) addRouteLink(this, parent, node, layout);
            }
            for (RouteNode node : layout.nodes.values()) child(routeBee(node.uid, node.x, node.y).tooltipBuilder(t -> {
                t.addLine(BeeBreedingHelper.getSpeciesDisplayName(node.uid));
                t.addLine(node.uid);
                if (node.step != null) t.addLine(IKey.lang("gtnl.bee_breeder." + routeState(node.step)))
                    .addLine(tr("gtnl.bee_breeder.chance", currentStep(node.step).getDouble("chance")));
            }));
            size(ROUTE_WIDTH, layout.height);
            vertical.setScrollSize(layout.height);
            if (targetChanged) vertical.scrollTo(viewport.getScrollArea(), 0);
            else vertical.clamp(viewport.getScrollArea());
            viewport.scheduleResize();
            scheduleResize();
        }

        private void focusTarget() {
            RouteNode node = layout.nodes.get(target.getStringValue());
            vertical.scrollTo(viewport.getScrollArea(), node == null ? 0 : node.y - 10);
        }

        @Override
        public @NotNull Result onMousePressed(int button) {
            if (button != 0) return Result.IGNORE;
            dragY = getContext().getAbsMouseY();
            startScroll = vertical.getScroll();
            return Result.SUCCESS;
        }

        @Override
        public void onMouseDrag(int button, long timeSinceClick) {
            if (button == 0)
                vertical.scrollTo(viewport.getScrollArea(), startScroll + dragY - getContext().getAbsMouseY());
        }
    }

    private static final class RouteLayout {

        private final Map<String, RouteNode> nodes = new LinkedHashMap<>();
        private final int height;

        private RouteLayout(List<NBTTagCompound> steps, String target) {
            Map<String, NBTTagCompound> byResult = new HashMap<>();
            for (NBTTagCompound step : steps) byResult.put(step.getString("result"), step);
            ArrayDeque<RouteNode> pending = new ArrayDeque<>();
            if (!target.isEmpty()) pending.add(node(target));
            Set<String> visited = new HashSet<>();
            while (!pending.isEmpty()) {
                RouteNode result = pending.remove();
                if (!visited.add(result.uid)) continue;
                result.step = byResult.get(result.uid);
                if (result.step == null) continue;
                for (String key : new String[] { "parent1", "parent2" }) {
                    String uid = result.step.getString(key);
                    if (uid.isEmpty() || uid.equals(result.uid)) continue;
                    RouteNode parent = node(uid);
                    if (!result.parents.contains(parent)) {
                        result.parents.add(parent);
                        parent.children++;
                        pending.add(parent);
                    }
                }
            }
            for (RouteNode node : nodes.values()) if (node.children == 0) pending.add(node);
            while (!pending.isEmpty()) {
                RouteNode child = pending.remove();
                for (RouteNode parent : child.parents) {
                    parent.depth = Math.max(parent.depth, child.depth + 1);
                    if (--parent.children == 0) pending.add(parent);
                }
            }
            Map<Integer, List<RouteNode>> levels = new TreeMap<>();
            for (RouteNode node : nodes.values()) levels.computeIfAbsent(node.depth, ignored -> new ArrayList<>())
                .add(node);
            int top = 10;
            for (List<RouteNode> level : levels.values()) {
                for (int start = 0; start < level.size(); start += 4) {
                    int count = Math.min(4, level.size() - start);
                    for (int i = 0; i < count; i++) {
                        RouteNode node = level.get(start + i);
                        node.x = 14 + (ROUTE_WIDTH - 28) * (2 * i + 1) / (2 * count) - 9;
                        node.y = top;
                    }
                    top += 56;
                }
            }
            height = Math.max(ROUTE_HEIGHT, top - 28);
        }

        private RouteNode node(String uid) {
            return nodes.computeIfAbsent(uid, RouteNode::new);
        }
    }

    private static final class RouteNode {

        private final String uid;
        private final List<RouteNode> parents = new ArrayList<>(2);
        private NBTTagCompound step;
        private int children;
        private int depth;
        private int x;
        private int y;

        private RouteNode(String uid) {
            this.uid = uid;
        }
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
            return Math.max(1, (filtered().size() + SPECIES_PER_PAGE - 1) / SPECIES_PER_PAGE);
        }

        private String uid(int row) {
            List<String> values = filtered();
            int index = page * SPECIES_PER_PAGE + row;
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
