package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.casing.TTCasingsContainer.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.hatch.NanitesInputBus;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLStructureChannels;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsElements;

public class TreeDiagram extends WirelessEnergyMultiMachineBase<TreeDiagram> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String TD_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/tree_diagram";
    private static final String[][] shape = StructureUtils.readStructureFromFile(TD_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 95;
    private static final int VERTICAL_OFF_SET = 26;
    private static final int DEPTH_OFF_SET = 69;

    public NanitesInputBus nanitesInputBus;

    public double euDiscount = 1;
    public double speedBonus = 1;
    public double failureBonus = 1;
    public double outputCoefficient = 1;
    public int nanitesParallel = 1;
    public int maxTierSkip = 1;

    public TreeDiagram(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TreeDiagram(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new TreeDiagram(this.mName);
    }

    @Override
    public IStructureDefinition<TreeDiagram> getStructureDefinition() {
        return StructureDefinition.<TreeDiagram>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                GTNLStructureChannels.STRUCTURE_RENDER.use(
                    ofBlocksTiered(
                        (block,
                            meta) -> block == Block.getBlockFromItem(
                                MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFrameBox(1)
                                    .getItem()) ? 1 : null,
                        ImmutableList.of(
                            Pair.of(
                                Block.getBlockFromItem(
                                    MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFrameBox(1)
                                        .getItem()),
                                0)),
                        -1,
                        (t, m) -> {},
                        t -> -1)))
            .addElement('B', ofBlock(sBlockCasings1, 6))
            .addElement('C', ofBlock(sBlockCasings1, 14))
            .addElement('D', ofBlock(sBlockCasings10, 7))
            .addElement('E', ofBlock(sBlockCasings10, 8))
            .addElement('F', ofBlock(sBlockCasings10, 11))
            .addElement('G', ofBlock(sBlockCasings2, 8))
            .addElement('H', ofBlock(sBlockCasings8, 0))
            .addElement(
                'I',
                buildHatchAdder(TreeDiagram.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        Maintenance,
                        InputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon,
                        CustomHatchElement.NanitesInputBus)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 2))))
            .addElement('J', ofBlock(sBlockCasings8, 7))
            .addElement('K', ofBlock(sBlockCasings8, 10))
            .addElement('L', ofBlock(sBlockCasings8, 12))
            .addElement('M', ofBlock(sBlockCasings9, 0))
            .addElement('N', ofBlock(sBlockCasings9, 1))
            .addElement('O', ofBlock(sBlockCasings9, 11))
            .addElement('P', ofBlock(sBlockCasings9, 12))
            .addElement('Q', ofBlock(sBlockCasingsBA0, 7))
            .addElement('R', ofBlock(sBlockCasingsNH, 10))
            .addElement('S', ofBlock(sBlockCasingsTT, 4))
            .addElement('T', ofBlock(sBlockCasingsTT, 6))
            .addElement('U', ofBlock(sBlockCasingsTT, 8))
            .addElement('V', ofBlock(ModBlocks.blockSpecialMultiCasings, 15))
            .addElement('W', ofBlock(BlockLoader.metaBlockGlow, 1))
            .addElement('X', ofBlock(BlockLoader.metaBlockGlow, 17))
            .addElement('Y', ofBlock(BlockLoader.metaBlockGlow, 25))
            .addElement('Z', ofBlock(BlockLoader.metaBlockGlow, 27))
            .addElement('0', ofBlock(BlockLoader.metaBlockGlow, 29))
            .addElement('1', ofBlock(BlockLoader.metaBlockGlow, 31))
            .addElement('2', ofBlock(BlockLoader.metaCasing, 7))
            .addElement('3', ofBlock(BlockLoader.metaCasing, 19))
            .addElement('4', ofBlock(BlockLoader.metaCasing02, 17))
            .build();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("TreeDiagramRecipes"))
            .addInfo(StatCollector.translateToLocal("Tooltip_TreeDiagram_00"))
            .addPerfectOCInfo()
            .addTecTechHatchInfo()
            .beginStructureBlock(194, 71, 184, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_TreeDiagram_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_TreeDiagram_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_TreeDiagram_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_TreeDiagram_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_TreeDiagram_Casing_00"))
            .addSubChannelUsage(GTNLStructureChannels.STRUCTURE_RENDER)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 2);
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        return super.checkProcessing();
    }

    @Override
    public void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setMaxTierSkips(maxTierSkip);
    }

    @Override
    public double getEUtDiscount() {
        return ((wirelessUpgrade ? 0.5 : 1) - (mParallelTier / 50.0)) * euDiscount;
    }

    @Override
    public double getDurationModifier() {
        return (1.0 / (wirelessUpgrade ? 2 : 1) - (Math.max(0, mParallelTier - 1) / 50.0)) * speedBonus;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.min(nanitesParallel, super.getMaxParallelRecipes());
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if (!GTNLStructureChannels.STRUCTURE_RENDER.hasValue(stackSize)) return;
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        if (!GTNLStructureChannels.STRUCTURE_RENDER.hasValue(stackSize)) return -1;

        int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);

        return this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            realBudget,
            env,
            false,
            true);
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 30;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.TreeDiagramRecipes;
    }

    public boolean addNanitesInputBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof NanitesInputBus bus) {
            bus.updateTexture(aBaseCasingIndex);
            bus.updateCraftingIcon(this.getMachineCraftingIcon());
            nanitesInputBus = bus;
            return true;
        }
        return false;
    }

    public enum CustomHatchElement implements IHatchElement<TreeDiagram> {

        NanitesInputBus("GT5U.MBTT.InputBus", TreeDiagram::addNanitesInputBusToMachineList, NanitesInputBus.class) {

            @Override
            public long count(TreeDiagram t) {
                return t.nanitesInputBus != null ? 1 : 0;
            }
        };

        private final String name;
        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<TreeDiagram> adder;

        @SafeVarargs
        CustomHatchElement(String name, IGTHatchAdder<TreeDiagram> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.name = name;
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public String getDisplayName() {
            return GTUtility.translate(name);
        }

        public IGTHatchAdder<? super TreeDiagram> adder() {
            return adder;
        }
    }

}
