package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.google.common.collect.ImmutableSet;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;

import gregtech.api.modularui2.GTGuiTextures;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;

public enum EternalGregTechWorkshopUpgrade {

    START,
    IGCC,
    STEM,
    CFCE,
    GISS,
    FDIM,
    SA,
    GPCI,
    REC,
    GEM,
    CTCDD,
    QGPIU,
    SEFCP,
    TCT,
    GGEBE,
    TPTP,
    DOP,
    CNTI,
    EPEC,
    IMKG,
    NDPE,
    POS,
    DOR,
    NGMS,
    SEDS,
    PA,
    CD,
    TSE,
    TBF,
    EE,
    END,

    ;

    public static final EternalGregTechWorkshopUpgrade[] VALUES = values();

    static final Set<EternalGregTechWorkshopUpgrade> SPLIT_UPGRADES;

    static {
        // Build upgrade data. Done here due to potential forward references

        // spotless:off

        START.build(b -> b
            .background(UpgradeColor.BLUE, MilestoneIcon.COMPOSITION)
            .windowSize(BGWindowSize.LARGE)
            .treePos(126, 56));

        IGCC.build(b -> b
            .prereqs(START)
            .cost(1)
            .background(UpgradeColor.BLUE, MilestoneIcon.CONVERSION)
            .treePos(126, 116));

        STEM.build(b -> b
            .prereqs(IGCC)
            .cost(1)
            .background(UpgradeColor.BLUE, MilestoneIcon.CATALYST)
            .treePos(96, 176));

        CFCE.build(b -> b
            .prereqs(IGCC)
            .cost(1)
            .background(UpgradeColor.BLUE, MilestoneIcon.CATALYST)
            .treePos(156, 176));

        GISS.build(b -> b
            .prereqs(STEM)
            .cost(1)
            .background(UpgradeColor.BLUE, MilestoneIcon.CHARGE)
            .treePos(66, 236));

        FDIM.build(b -> b
            .prereqs(STEM, CFCE)
            .cost(1)
            .background(UpgradeColor.BLUE, MilestoneIcon.COMPOSITION)
            .treePos(126, 236));

        SA.build(b -> b
            .prereqs(CFCE)
            .cost(1)
            .background(UpgradeColor.BLUE, MilestoneIcon.CONVERSION)
            .treePos(186, 236));

        GPCI.build(b -> b
            .prereqs(FDIM)
            .cost(2)
            .background(UpgradeColor.BLUE, MilestoneIcon.COMPOSITION)
            .treePos(126, 296));

        REC.build(b -> b
            .prereqs(GISS, FDIM)
            .requireAllPrereqs()
            .cost(2)
            .background(UpgradeColor.RED, MilestoneIcon.CHARGE)
            .treePos(56, 356));

        GEM.build(b -> b
            .prereqs(GPCI)
            .cost(2)
            .background(UpgradeColor.BLUE, MilestoneIcon.CATALYST)
            .treePos(126, 356));

        CTCDD.build(b -> b
            .prereqs(GPCI, SA)
            .requireAllPrereqs()
            .cost(2)
            .background(UpgradeColor.RED, MilestoneIcon.CONVERSION)
            .treePos(196, 356));

        QGPIU.build(b -> b
            .prereqs(REC, CTCDD)
            .cost(2)
            .background(UpgradeColor.BLUE, MilestoneIcon.CATALYST)
            .treePos(126, 416));

        SEFCP.build(b -> b
            .prereqs(QGPIU)
            .cost(3)
            .background(UpgradeColor.PURPLE, MilestoneIcon.CATALYST)
            .treePos(66, 476));

        TCT.build(b -> b
            .prereqs(QGPIU)
            .cost(3)
            .background(UpgradeColor.ORANGE, MilestoneIcon.CONVERSION)
            .treePos(126, 476));

        GGEBE.build(b -> b
            .prereqs(QGPIU)
            .cost(3)
            .background(UpgradeColor.GREEN, MilestoneIcon.CHARGE)
            .treePos(186, 476));

        TPTP.build(b -> b
            .prereqs(GGEBE)
            .cost(4)
            .background(UpgradeColor.GREEN, MilestoneIcon.CONVERSION)
            .treePos(246, 496));

        DOP.build(b -> b
            .prereqs(CNTI)
            .cost(4)
            .background(UpgradeColor.PURPLE, MilestoneIcon.CONVERSION)
            .treePos(6, 556));

        CNTI.build(b -> b
            .prereqs(SEFCP)
            .cost(3)
            .background(UpgradeColor.PURPLE, MilestoneIcon.CHARGE)
            .treePos(66, 536));

        EPEC.build(b -> b
            .prereqs(TCT)
            .cost(3)
            .background(UpgradeColor.ORANGE, MilestoneIcon.CONVERSION)
            .treePos(126, 536));

        IMKG.build(b -> b
            .prereqs(GGEBE)
            .cost(3)
            .background(UpgradeColor.GREEN, MilestoneIcon.CHARGE)
            .treePos(186, 536));

        NDPE.build(b -> b
            .prereqs(CNTI)
            .cost(3)
            .background(UpgradeColor.PURPLE, MilestoneIcon.CHARGE)
            .treePos(66, 596));

        POS.build(b -> b
            .prereqs(EPEC)
            .cost(3)
            .background(UpgradeColor.ORANGE, MilestoneIcon.CONVERSION)
            .treePos(126, 596));

        DOR.build(b -> b
            .prereqs(IMKG)
            .cost(3)
            .background(UpgradeColor.GREEN, MilestoneIcon.CONVERSION)
            .treePos(186, 596));

        NGMS.build(b -> b
            .prereqs(NDPE, POS, DOR)
            .cost(4)
            .background(UpgradeColor.BLUE, MilestoneIcon.CHARGE)
            .treePos(126, 656));

        SEDS.build(b -> b
            .prereqs(NGMS)
            .cost(5)
            .background(UpgradeColor.BLUE, MilestoneIcon.CONVERSION)
            .treePos(126, 718));

        PA.build(b -> b
            .prereqs(SEDS)
            .cost(6)
            .background(UpgradeColor.BLUE, MilestoneIcon.CONVERSION)
            .treePos(36, 758));

        CD.build(b -> b
            .prereqs(PA)
            .cost(7)
            .background(UpgradeColor.BLUE, MilestoneIcon.COMPOSITION)
            .treePos(36, 848));

        TSE.build(b -> b
            .prereqs(CD)
            .cost(8)
            .background(UpgradeColor.BLUE, MilestoneIcon.CATALYST)
            .treePos(126, 888));

        TBF.build(b -> b
            .prereqs(TSE)
            .cost(9)
            .background(UpgradeColor.BLUE, MilestoneIcon.CHARGE)
            .treePos(216, 848));

        EE.build(b -> b
            .prereqs(TBF)
            .cost(10)
            .background(UpgradeColor.BLUE, MilestoneIcon.COMPOSITION)
            .treePos(216, 758));

        END.build(b -> b
            .prereqs(EE)
            .cost(12)
            .background(UpgradeColor.BLUE, MilestoneIcon.COMPOSITION)
            .windowSize(BGWindowSize.LARGE)
            .treePos(126, 798));

        // spotless:on

        // Build split upgrade set
        SPLIT_UPGRADES = ImmutableSet.of(SEFCP, TCT, GGEBE);

        // Build inverse dependents mapping
        EnumMap<EternalGregTechWorkshopUpgrade, List<EternalGregTechWorkshopUpgrade>> dependencies = new EnumMap<>(
            EternalGregTechWorkshopUpgrade.class);
        for (EternalGregTechWorkshopUpgrade upgrade : VALUES) {
            for (EternalGregTechWorkshopUpgrade prerequisite : upgrade.prerequisites) {
                dependencies.computeIfAbsent(prerequisite, $ -> new ArrayList<>())
                    .add(upgrade);
            }
        }
        for (var entry : dependencies.entrySet()) {
            EternalGregTechWorkshopUpgrade upgrade = entry.getKey();
            List<EternalGregTechWorkshopUpgrade> deps = entry.getValue();
            if (deps != null) {
                upgrade.dependents = deps.toArray(new EternalGregTechWorkshopUpgrade[0]);
            }
        }
    }

    // Static tree linking
    @Getter
    private EternalGregTechWorkshopUpgrade[] prerequisites;
    private boolean requireAllPrerequisites;

    // Cost
    @Getter
    private int shardCost;
    private final List<ItemStack> extraCost = new ArrayList<>();

    // UI
    private UpgradeColor color;
    private MilestoneIcon icon;
    private BGWindowSize windowSize;
    private int treeX;
    private int treeY;

    // Pre-generated data
    @Getter
    private EternalGregTechWorkshopUpgrade[] dependents = new EternalGregTechWorkshopUpgrade[0];
    private final String name;
    private final String nameShort;
    private final String bodyText;
    private final String loreText;

    EternalGregTechWorkshopUpgrade() {
        this.name = "fog.upgrade.tt." + ordinal();
        this.nameShort = "fog.upgrade.tt.short." + ordinal();
        this.bodyText = "fog.upgrade.text." + ordinal();
        this.loreText = "fog.upgrade.lore." + ordinal();
    }

    private void build(UnaryOperator<Builder> u) {
        Builder b = u.apply(new Builder());

        this.prerequisites = b.prerequisites != null ? b.prerequisites.toArray(new EternalGregTechWorkshopUpgrade[0])
            : new EternalGregTechWorkshopUpgrade[0];
        this.requireAllPrerequisites = b.requireAllPrerequisites;
        this.shardCost = b.shardCost;
        this.color = b.color;
        this.icon = b.icon;
        this.windowSize = b.windowSize;
        this.treeX = b.treeX;
        this.treeY = b.treeY;
    }

    public void addExtraCost(ItemStack... cost) {
        if (extraCost.size() + cost.length > 20) {
            throw new IllegalArgumentException("Too many inputs for Godforge upgrade cost, cannot be more than 20!");
        }
        extraCost.addAll(Arrays.asList(cost));
    }

    public boolean requiresAllPrerequisites() {
        return requireAllPrerequisites;
    }

    public boolean hasExtraCost() {
        return !extraCost.isEmpty();
    }

    public ItemStack[] getExtraCost() {
        return extraCost.toArray(new ItemStack[0]);
    }

    @Deprecated
    public UITexture getBackground() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return color.getBackground();
    }

    @Deprecated
    public UITexture getOverlay() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return color.getOverlay();
    }

    public IDrawable getMui2Background() {
        return color.getMui2Background();
    }

    public IDrawable getMui2Overlay() {
        return color.getMui2Overlay();
    }

    public IDrawable getMui2Connector() {
        return color.getMui2Connector();
    }

    public IDrawable getMui2OpaqueConnector() {
        return color.getMui2OpaqueConnector();
    }

    public UpgradeColor getColorForGui() {
        return color;
    }

    public int getTreeXForGui() {
        return treeX;
    }

    public int getTreeYForGui() {
        return treeY;
    }

    @Deprecated
    public Pos2d getTreePos() {
        // TODO: Remove this MUI1 position getter after Eternal GregTech Workshop fallback windows are removed.
        return new Pos2d(treeX, treeY);
    }

    @Deprecated
    public UITexture getSymbol() {
        // TODO: Remove this MUI1 texture getter after Eternal GregTech Workshop fallback windows are removed.
        return icon.getSymbol();
    }

    public IDrawable getMui2Symbol() {
        return switch (icon) {
            case CHARGE -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CHARGE;
            case CONVERSION -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CONVERSION;
            case CATALYST -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CATALYST;
            case COMPOSITION -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_COMPOSITION;
        };
    }

    public float getSymbolWidthRatio() {
        return icon.getWidthRatio();
    }

    @Deprecated
    public Size getWindowSize() {
        // TODO: Remove this MUI1 size getter after Eternal GregTech Workshop fallback windows are removed.
        return windowSize.getWindowSize();
    }

    public int getWindowWidthForGui() {
        return windowSize.getWidth();
    }

    public int getWindowHeightForGui() {
        return windowSize.getHeight();
    }

    public int getLoreYPos() {
        return windowSize.getLoreY();
    }

    public String getNameText() {
        return StatCollector.translateToLocal(name);
    }

    public String getShortNameText() {
        return StatCollector.translateToLocal(nameShort);
    }

    public String getBodyText() {
        return StatCollector.translateToLocal(bodyText);
    }

    public String getLoreText() {
        return StatCollector.translateToLocal(loreText);
    }

    public static class Builder {

        // Tree linking
        private ObjectList<EternalGregTechWorkshopUpgrade> prerequisites;
        private boolean requireAllPrerequisites;

        // Cost
        private int shardCost;

        // UI
        private UpgradeColor color = UpgradeColor.BLUE;
        private MilestoneIcon icon = MilestoneIcon.CHARGE;
        private BGWindowSize windowSize = BGWindowSize.STANDARD;
        private int treeX;
        private int treeY;

        private Builder() {}

        public Builder prereqs(EternalGregTechWorkshopUpgrade... prereqs) {
            if (this.prerequisites != null) {
                throw new IllegalArgumentException(
                    "Cannot repeat calls to EternalGregTechWorkshopUpgrade$Builder#prereqs");
            }
            this.prerequisites = new ObjectArrayList<>(prereqs);
            return this;
        }

        public Builder requireAllPrereqs() {
            this.requireAllPrerequisites = true;
            return this;
        }

        // Cost
        public Builder cost(int shards) {
            this.shardCost = shards;
            return this;
        }

        // UI
        public Builder background(UpgradeColor color, MilestoneIcon icon) {
            this.color = color;
            this.icon = icon;
            return this;
        }

        public Builder windowSize(BGWindowSize windowSize) {
            this.windowSize = windowSize;
            return this;
        }

        public Builder treePos(int x, int y) {
            this.treeX = x;
            this.treeY = y;
            return this;
        }
    }

    @Getter
    public enum BGWindowSize {

        STANDARD(250, 250, 110),
        LARGE(300, 300, 85),

        ;

        private final int width;
        private final int height;
        private final int loreY;

        BGWindowSize(int width, int height, int loreY) {
            this.width = width;
            this.height = height;
            this.loreY = loreY;
        }

        @Deprecated
        public Size getWindowSize() {
            // TODO: Remove this MUI1 size getter after Eternal GregTech Workshop fallback windows are removed.
            return new Size(width, height);
        }

    }
}
