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
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerGhostIngredientSlot;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
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
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.HorizontalScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
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
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

public final class LargeBeeBreederGui extends GTNLMultiBlockBaseGui<LargeBeeBreeder> {

    private static final int PAGE_WIDTH = 232;
    private static final int PAGE_HEIGHT = 114;
    private static final int SPECIES_COLUMNS = 10;
    private static final int SPECIES_ROWS = 3;
    private static final int SPECIES_PER_PAGE = SPECIES_COLUMNS * SPECIES_ROWS;
    private static final int TREE_HEIGHT = PAGE_HEIGHT - 18;
    private static final int COLOR_UNLOCKED = 0xFF6DBD75;
    private static final int COLOR_ACTIVE = 0xFFE6B84B;
    private static final int COLOR_READY = 0xFF62B6CB;
    private static final int COLOR_WAITING = 0xFF92999F;

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
    private BreedingTreeWidget treeWidget;

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
        sync.syncValue(
            "beeActiveSpecies",
            new GenericListSyncHandler<>(
                multiblock::getActiveBreedingSpecies,
                value -> activeSpecies = new HashSet<>(value),
                ByteBufUtils::readUTF8String,
                ByteBufUtils::writeUTF8String,
                String::equals,
                value -> value));
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
            new SlotLikeButtonWidget(() -> beeIcon(target.getStringValue())).pos(0, 22)
                .size(18)
                .tooltipAutoUpdate(true)
                .tooltipBuilder(
                    t -> t.addLine(tr("gtnl.bee_breeder.current_target", speciesOrEmpty(target.getStringValue())))
                        .addLine(tr("gtnl.bee_breeder.energy", energy.getLongValue(), bonus.getIntValue()))
                        .addLine(IKey.lang("gtnl.bee_breeder.route")))
                .onMousePressed(button -> {
                    if (button == 0) pages.setPage(3);
                    return true;
                }));
        page.child(
            fittedText(
                () -> valid.getBoolValue() ? speciesOrEmpty(target.getStringValue())
                    : EnumChatFormatting.DARK_RED + tr("gtnl.bee_breeder.invalid_short"),
                24,
                22,
                PAGE_WIDTH - 24,
                1F).tooltipAutoUpdate(true)
                    .tooltipBuilder(t -> {
                        t.addLine(tr("gtnl.bee_breeder.current_target", speciesOrEmpty(target.getStringValue())));
                        if (!valid.getBoolValue()) t.addLine(IKey.lang("gtnl.bee_breeder.invalid_target"));
                    }));
        page.child(
            fittedText(
                () -> tr("gtnl.bee_breeder.overview", pending.getIntValue(), archive.size(), parallel.getIntValue()),
                24,
                35,
                PAGE_WIDTH - 24,
                0.8F).tooltipAutoUpdate(true)
                    .tooltipBuilder(
                        t -> t
                            .addLine(
                                tr(
                                    "gtnl.bee_breeder.overview",
                                    pending.getIntValue(),
                                    archive.size(),
                                    parallel.getIntValue()))
                            .addLine(tr("gtnl.bee_breeder.energy", energy.getLongValue(), bonus.getIntValue()))));
        page.child(
            new MeterWidget(
                this::cycleProgress,
                () -> tr("gtnl.bee_breeder.attempt_progress", (int) (cycleProgress() * 100)),
                COLOR_ACTIVE).pos(0, 54)
                    .size(112, 12)
                    .tooltipAutoUpdate(true)
                    .tooltipBuilder(
                        t -> t
                            .addLine(
                                tr(
                                    "gtnl.bee_breeder.attempt_time",
                                    progressTime.getIntValue() / 20D,
                                    maxProgressTime.getIntValue() / 20D))
                            .addLine(tr("gtnl.bee_breeder.energy", energy.getLongValue(), bonus.getIntValue()))));
        page.child(
            new MeterWidget(this::routeProgress, this::routeProgressText, COLOR_UNLOCKED).pos(120, 54)
                .size(112, 12)
                .tooltipAutoUpdate(true)
                .tooltipBuilder(t -> t.addLine(IKey.lang("gtnl.bee_breeder.route_session"))));
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
        return super.createTerminalTextWidget(sync, panel).child(terminalText(this::missingText));
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
        HorizontalScrollData horizontal = new HorizontalScrollData();
        VerticalScrollData vertical = new VerticalScrollData();
        ScrollWidget<?> viewport = new ScrollWidget<>(vertical).showScrollShadows(false)
            .pos(0, 18)
            .size(PAGE_WIDTH, TREE_HEIGHT)
            .background(new Rectangle().color(0xFF20272B));
        viewport.getScrollArea()
            .setScrollDataX(horizontal);
        treeWidget = new BreedingTreeWidget(viewport, horizontal, vertical);
        viewport.child(treeWidget);
        page.child(
            new MeterWidget(this::routeProgress, this::routeProgressText, COLOR_UNLOCKED).pos(0, 1)
                .size(PAGE_WIDTH - 24, 14)
                .tooltipAutoUpdate(true)
                .tooltipBuilder(
                    t -> t.addLine(IKey.lang("gtnl.bee_breeder.route_session"))
                        .addLine(IKey.lang("gtnl.bee_breeder.tree_hint"))));
        page.child(
            new ButtonWidget<>().pos(PAGE_WIDTH - 18, 0)
                .size(18)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .overlay(new ItemDrawable(Items.compass).asIcon())
                .tooltipBuilder(t -> t.addLine(IKey.lang("gtnl.bee_breeder.focus_target")))
                .onMousePressed(button -> {
                    if (button == 0) treeWidget.focusTarget();
                    return true;
                }));
        page.child(viewport);
        page.child(
            IKey.lang("gtnl.bee_breeder.no_steps")
                .asWidget()
                .pos(12, 34)
                .size(PAGE_WIDTH - 24, 54)
                .color(0xFFFFFFFF)
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

    private static final class MeterWidget extends Widget<MeterWidget> {

        private final DoubleSupplier progress;
        private final Supplier<String> label;
        private final int color;

        private MeterWidget(DoubleSupplier progress, Supplier<String> label, int color) {
            this.progress = progress;
            this.label = label;
            this.color = color;
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> theme) {
            GuiDraw.drawRect(0, 0, getArea().width, getArea().height, 0xFF20272B);
            GuiDraw.drawRect(1, 1, (getArea().width - 2) * (float) progress.getAsDouble(), getArea().height - 2, color);
            GuiDraw.drawScaledAlignedTextInBox(
                label.get(),
                1,
                1,
                getArea().width - 2,
                getArea().height - 2,
                Alignment.CENTER,
                0.8f);
        }
    }

    private final class BreedingTreeWidget extends Widget<BreedingTreeWidget> implements Interactable {

        private final ScrollWidget<?> viewport;
        private final HorizontalScrollData horizontal;
        private final VerticalScrollData vertical;
        private final ItemDrawable item = new ItemDrawable();
        private List<NBTTagCompound> lastSteps;
        private String lastTarget = "";
        private TreeLayout layout = new TreeLayout(Collections.emptyList(), "");
        private TreeNode hovered;
        private boolean centerTarget;
        private int dragX;
        private int dragY;
        private int startScrollX;
        private int startScrollY;

        private BreedingTreeWidget(ScrollWidget<?> viewport, HorizontalScrollData horizontal,
            VerticalScrollData vertical) {
            this.viewport = viewport;
            this.horizontal = horizontal;
            this.vertical = vertical;
            size(PAGE_WIDTH, TREE_HEIGHT);
            tooltipAutoUpdate(true);
            tooltipBuilder(t -> {
                if (hovered == null) {
                    t.addLine(IKey.lang("gtnl.bee_breeder.tree_hint"))
                        .addLine(EnumChatFormatting.GREEN + tr("gtnl.bee_breeder.unlocked"))
                        .addLine(EnumChatFormatting.GOLD + tr("gtnl.bee_breeder.active"))
                        .addLine(EnumChatFormatting.AQUA + tr("gtnl.bee_breeder.ready"))
                        .addLine(EnumChatFormatting.GRAY + tr("gtnl.bee_breeder.waiting"));
                    return;
                }
                t.addLine(BeeBreedingHelper.getSpeciesDisplayName(hovered.uid))
                    .addLine(hovered.uid)
                    .addLine(tr("gtnl.bee_breeder." + state(hovered)));
                if (!hovered.parents.isEmpty()) {
                    t.addLine(tr("gtnl.bee_breeder.chance", hovered.chance));
                    for (TreeNode parent : hovered.parents) {
                        t.addLine(BeeBreedingHelper.getSpeciesDisplayName(parent.uid));
                    }
                }
                if (isActive(hovered)) t.addLine(
                    tr(
                        "gtnl.bee_breeder.attempt_time",
                        progressTime.getIntValue() / 20D,
                        maxProgressTime.getIntValue() / 20D));
            });
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            String currentTarget = target.getStringValue();
            if (lastSteps != steps || !lastTarget.equals(currentTarget)) {
                centerTarget |= !lastTarget.equals(currentTarget);
                lastSteps = steps;
                lastTarget = currentTarget;
                layout = new TreeLayout(steps, currentTarget);
                size(layout.width, layout.height);
                horizontal.setScrollSize(layout.width);
                vertical.setScrollSize(layout.height);
                horizontal.clamp(viewport.getScrollArea());
                vertical.clamp(viewport.getScrollArea());
                viewport.scheduleResize();
                scheduleResize();
            }
        }

        @Override
        public void onResized() {
            super.onResized();
            if (centerTarget) focusTarget();
        }

        private void focusTarget() {
            TreeNode node = layout.nodes.get(target.getStringValue());
            if (node == null) return;
            horizontal.scrollTo(viewport.getScrollArea(), node.x + TreeLayout.NODE_WIDTH / 2 - PAGE_WIDTH / 2);
            vertical.scrollTo(viewport.getScrollArea(), node.y + TreeLayout.NODE_HEIGHT / 2 - TREE_HEIGHT / 2);
            centerTarget = false;
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> theme) {
            hovered = null;
            int left = horizontal.getScroll();
            int top = vertical.getScroll();
            for (TreeNode node : layout.nodes.values()) {
                for (TreeNode parent : node.parents) drawConnector(parent, node, nodeColor(node));
            }
            for (TreeNode node : layout.nodes.values()) {
                if (node.x + TreeLayout.NODE_WIDTH < left || node.x > left + PAGE_WIDTH
                    || node.y + TreeLayout.NODE_HEIGHT < top
                    || node.y > top + TREE_HEIGHT) continue;
                int color = nodeColor(node);
                boolean selected = node.uid.equals(target.getStringValue());
                GuiDraw.drawRect(
                    node.x - 1,
                    node.y - 1,
                    TreeLayout.NODE_WIDTH + 2,
                    TreeLayout.NODE_HEIGHT + 2,
                    selected ? 0xFFE6B84B : 0xFF20272B);
                GuiDraw.drawRect(node.x, node.y, TreeLayout.NODE_WIDTH, TreeLayout.NODE_HEIGHT, color);
                GuiDraw.drawRect(
                    node.x + 1,
                    node.y + 1,
                    TreeLayout.NODE_WIDTH - 2,
                    TreeLayout.NODE_HEIGHT - 2,
                    0xFF303A40);
                item.setItem(beeIcon(node.uid))
                    .draw(context, node.x + 20, node.y + 2, 16, 16, theme.getTheme());
                String name = context.getFontRenderer()
                    .trimStringToWidth(BeeBreedingHelper.getSpeciesDisplayName(node.uid), 66);
                GuiDraw.drawText(name, node.x + 3, node.y + 20, 0.75f, 0xFFFFFFFF, false);
                if (isActive(node)) GuiDraw.drawRect(
                    node.x + 1,
                    node.y + TreeLayout.NODE_HEIGHT - 3,
                    (TreeLayout.NODE_WIDTH - 2) * (float) cycleProgress(),
                    2,
                    COLOR_ACTIVE);
                if (isHovering() && context.getMouseX() >= node.x
                    && context.getMouseX() < node.x + TreeLayout.NODE_WIDTH
                    && context.getMouseY() >= node.y
                    && context.getMouseY() < node.y + TreeLayout.NODE_HEIGHT) {
                    hovered = node;
                }
            }
        }

        private void drawConnector(TreeNode parent, TreeNode child, int color) {
            int fromX = parent.x + TreeLayout.NODE_WIDTH;
            int fromY = parent.y + TreeLayout.NODE_HEIGHT / 2;
            int toX = child.x;
            int toY = child.y + TreeLayout.NODE_HEIGHT / 2;
            int turnX = toX - 8;
            if (child.depth - parent.depth > 1) {
                int laneY = 3;
                GuiDraw.drawRect(fromX, fromY, 6, 1, color);
                GuiDraw.drawRect(fromX + 5, laneY, 1, fromY - laneY + 1, color);
                GuiDraw.drawRect(fromX + 5, laneY, turnX - fromX - 4, 1, color);
                GuiDraw.drawRect(turnX, laneY, 1, toY - laneY + 1, color);
            } else {
                GuiDraw.drawRect(fromX, fromY, turnX - fromX + 1, 1, color);
                GuiDraw.drawRect(turnX, Math.min(fromY, toY), 1, Math.abs(toY - fromY) + 1, color);
            }
            GuiDraw.drawRect(turnX, toY, toX - turnX, 1, color);
            GuiDraw.drawRect(toX - 3, toY - 2, 1, 5, color);
            GuiDraw.drawRect(toX - 2, toY - 1, 1, 3, color);
        }

        private boolean isActive(TreeNode node) {
            return machineActive.getBoolValue() && activeSpecies.contains(node.uid);
        }

        private String state(TreeNode node) {
            if (archivedSpecies.contains(node.uid)) return "unlocked";
            if (isActive(node)) return "active";
            if (!node.parents.isEmpty() && node.parents.stream()
                .allMatch(parent -> archivedSpecies.contains(parent.uid))) {
                return "ready";
            }
            return "waiting";
        }

        private int nodeColor(TreeNode node) {
            return switch (state(node)) {
                case "unlocked" -> COLOR_UNLOCKED;
                case "active" -> COLOR_ACTIVE;
                case "ready" -> COLOR_READY;
                default -> COLOR_WAITING;
            };
        }

        @Override
        public @NotNull Result onMousePressed(int button) {
            if (button != 0) return Result.IGNORE;
            dragX = getContext().getAbsMouseX();
            dragY = getContext().getAbsMouseY();
            startScrollX = horizontal.getScroll();
            startScrollY = vertical.getScroll();
            return Result.SUCCESS;
        }

        @Override
        public void onMouseDrag(int button, long timeSinceClick) {
            if (button != 0) return;
            horizontal.scrollTo(viewport.getScrollArea(), startScrollX + dragX - getContext().getAbsMouseX());
            vertical.scrollTo(viewport.getScrollArea(), startScrollY + dragY - getContext().getAbsMouseY());
        }
    }

    private static final class TreeLayout {

        private static final int NODE_WIDTH = 56;
        private static final int NODE_HEIGHT = 30;
        private final Map<String, TreeNode> nodes = new LinkedHashMap<>();
        private final int width;
        private final int height;

        private TreeLayout(List<NBTTagCompound> steps, String target) {
            for (NBTTagCompound step : steps) {
                TreeNode result = node(step.getString("result"));
                result.chance = step.getDouble("chance");
                for (String key : new String[] { "parent1", "parent2" }) {
                    TreeNode parent = node(step.getString(key));
                    if (!result.parents.contains(parent)) {
                        result.parents.add(parent);
                        parent.children.add(result);
                    }
                }
            }
            if (!target.isEmpty()) node(target);
            Map<TreeNode, Integer> pendingParents = new HashMap<>();
            ArrayDeque<TreeNode> ready = new ArrayDeque<>();
            for (TreeNode node : nodes.values()) {
                pendingParents.put(node, node.parents.size());
                if (node.parents.isEmpty()) ready.add(node);
            }
            while (!ready.isEmpty()) {
                TreeNode node = ready.remove();
                for (TreeNode child : node.children) {
                    child.depth = Math.max(child.depth, node.depth + 1);
                    if (pendingParents.merge(child, -1, Integer::sum) == 0) ready.add(child);
                }
            }
            Map<Integer, List<TreeNode>> columns = new TreeMap<>();
            int rows = 0;
            int depth = 0;
            for (TreeNode node : nodes.values()) {
                List<TreeNode> column = columns.computeIfAbsent(node.depth, ignored -> new ArrayList<>());
                column.add(node);
                rows = Math.max(rows, column.size());
                depth = Math.max(depth, node.depth);
            }
            for (List<TreeNode> column : columns.values()) {
                for (int row = 0; row < column.size(); row++) {
                    TreeNode node = column.get(row);
                    node.x = 6 + node.depth * 74;
                    node.y = 12 + (rows - column.size()) * 20 + row * 40;
                }
            }
            width = Math.max(PAGE_WIDTH - 8, 12 + depth * 74 + NODE_WIDTH);
            height = Math.max(TREE_HEIGHT - 8, 18 + rows * 40);
        }

        private TreeNode node(String uid) {
            return nodes.computeIfAbsent(uid, TreeNode::new);
        }
    }

    private static final class TreeNode {

        private final String uid;
        private final List<TreeNode> parents = new ArrayList<>(2);
        private final List<TreeNode> children = new ArrayList<>();
        private double chance;
        private int depth;
        private int x;
        private int y;

        private TreeNode(String uid) {
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
