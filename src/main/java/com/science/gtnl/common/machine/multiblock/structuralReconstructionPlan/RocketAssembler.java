package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static com.science.gtnl.loader.BlockLoader.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.render.tile.RocketAssemblerRenderer;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.render.IMTERenderer;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;

public class RocketAssembler extends GTMMultiMachineBase<RocketAssembler>
    implements ISurvivalConstructable, IMTERenderer {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String RA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/rocket_assembler";
    private static final int HORIZONTAL_OFF_SET = 8;
    private static final int VERTICAL_OFF_SET = 21;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(RA_STRUCTURE_FILE_PATH);

    public boolean enableRender = true;

    public RocketAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public RocketAssembler(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new RocketAssembler(this.mName);
    }

    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        if (!mMachine || !enableRender) return;
        RocketAssemblerRenderer.renderTileEntityAt(this, x, y, z, timeSinceLastTick);
    }

    @Override
    public void onValueUpdate(byte aValue) {
        mMachine = (aValue & 0x01) != 0;
        enableRender = (aValue & 0x02) != 0;
    }

    @Override
    public byte getUpdateData() {
        byte data = 0;
        if (mMachine) data |= 0x01;
        if (enableRender) data |= 0x02;
        return data;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISASSEMBLER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISASSEMBLER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISASSEMBLER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISASSEMBLER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings4, 1);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.RocketAssemblerRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("RocketAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RocketAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_RocketAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(17, 24, 16, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_RocketAssembler_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_RocketAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_RocketAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_RocketAssembler_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_RocketAssembler_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<RocketAssembler> getStructureDefinition() {
        return StructureDefinition.<RocketAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(metaCasing02, 3))
            .addElement('B', ofBlock(sBlockCasings2, 0))
            .addElement('C', ofBlock(sBlockCasings2, 3))
            .addElement('D', ofBlock(sBlockCasings2, 13))
            .addElement('E', ofBlock(sBlockCasings3, 10))
            .addElement(
                'F',
                buildHatchAdder(RocketAssembler.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy), ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings4, 1))))
            .addElement('G', ofBlock(sBlockCasings6, 1))
            .addElement('H', ofBlock(sBlockCasings6, 3))
            .addElement('I', ofFrame(Materials.Steel))
            .addElement('J', ofFrame(Materials.StainlessSteel))
            .addElement(
                'K',
                ofChain(
                    ofBlockAnyMeta(GCBlocks.landingPad, 0),
                    ofBlockAnyMeta(GCBlocks.landingPadFull, 0),
                    ofBlockAnyMeta(GCBlocks.fakeBlock),
                    isAir()))
            .addElement(
                'L',
                ofChain(
                    ofBlockAnyMeta(GCBlocks.landingPad, 0),
                    ofBlockAnyMeta(GCBlocks.landingPadFull, 0),
                    ofBlockAnyMeta(GCBlocks.fakeBlock),
                    isAir()))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        if (mCountCasing >= 1) {
            getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && f.isNotFlipped();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Override
            @Nonnull
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setHeatOC(getHeatOC())
                    .setMachineHeat(getMachineHeat())
                    .setHeatDiscount(getHeatDiscount())
                    .setAmperageOC(getAmperageOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }

            @Nonnull
            @Override
            public CalculationResult validateAndCalculateRecipe(@Nonnull GTRecipe recipe) {
                if (recipe.mSpecialItems == null) return super.validateAndCalculateRecipe(recipe);
                if (recipe.mSpecialItems instanceof ItemStack itemStack) {
                    for (ItemStack item : getAllStoredInputs()) {
                        if (GTUtility.areStacksEqual(itemStack, item, true))
                            return super.validateAndCalculateRecipe(recipe);
                    }
                    return CalculationResult.ofFailure(SimpleCheckRecipeResult.ofFailure("missing_schematic"));
                }

                return super.validateAndCalculateRecipe(recipe);
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.enableRender = !enableRender;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal("Info_Render_" + (this.enableRender ? "Enabled" : "Disabled")));
        }
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("enableRender", enableRender);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        enableRender = aNBT.getBoolean("enableRender");
    }
}
