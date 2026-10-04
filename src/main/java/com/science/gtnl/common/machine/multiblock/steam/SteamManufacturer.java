package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class SteamManufacturer extends SteamMultiMachineBase<SteamManufacturer> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_manufacturer";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SM_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 5;
    private static final int DEPTH_OFF_SET = 0;

    private boolean enableHVRecipe = false;

    public SteamManufacturer(String aName) {
        super(aName);
    }

    public SteamManufacturer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.steam_manufacturer.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamManufacturer(this.mName);
    }

    @Override
    public IStructureDefinition<SteamManufacturer> getStructureDefinition() {
        return StructureDefinition.<SteamManufacturer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTNLCasings.BreelPipeCasing.asElement())
            .addElement('B', GTNLCasings.HydraulicAssemblingCasing.asElement())
            .addElement(
                'C',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamManufacturer.class)
                        .casingIndex(GTNLCasings.BreelPlatedCasing.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamManufacturer.class)
                        .casingIndex(GTNLCasings.BreelPlatedCasing.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamManufacturer.class).casingIndex(GTNLCasings.BreelPlatedCasing.getTextureId())
                        .hint(1)
                        .build(),
                    GTStructureUtility.buildHatchAdder(SteamManufacturer.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            HatchElement.InputBus,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.OutputBus,
                            HatchElement.Maintenance)
                        .casingIndex(GTNLCasings.BreelPlatedCasing.getTextureId())
                        .hint(1)
                        .buildAndChain(),
                    GTNLCasings.BreelPlatedCasing.asElement()))
            .addElement('D', Casings.SteelGearBoxCasing.asElement())
            .addElement('E', GTStructureUtility.ofFrame(Materials.Steel))
            .build();
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            return;
        }
        checkHatch(errors);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.SteamManufacturerRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 4;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / 2.0;
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        enableHVRecipe = isHVAssembler(getControllerSlot());
        return super.checkProcessing();
    }

    public boolean isHVAssembler(ItemStack stack) {
        return ItemList.Machine_HV_Assembler.isStackEqual(stack, false, true);
    }

    public long getMaxAssemblerRecipeVoltage() {
        return GTValues.V[enableHVRecipe ? VoltageIndex.HV : VoltageIndex.MV];
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @Override
            public @NotNull Stream<GTRecipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
                return Stream.concat(
                    super.findRecipeMatches(map),
                    super.findRecipeMatches(RecipeMaps.assemblerRecipes)
                        .filter(recipe -> recipe.mEUt <= getMaxAssemblerRecipeVoltage()));
            }

            @Override
            public @NotNull GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(configSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTNLRecipeMaps.SteamManufacturerRecipes, RecipeMaps.assemblerRecipes);
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(GTValues.V[9]);
        // We need to trick the GT_ParallelHelper we have enough amps for all recipe parallels.
        logic.setAvailableAmperage(getTrueParallel());
        logic.setAmperageOC(false);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
                    TextureFactory.builder()
                        .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_MANUFACTURER_ACTIVE)
                        .extFacing()
                        .build() };
            } else {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
                    TextureFactory.builder()
                        .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_MANUFACTURER)
                        .extFacing()
                        .build() };
            }
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)) };
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_manufacturer.recipe_type");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_manufacturer.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_manufacturer.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_manufacturer.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_manufacturer.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_manufacturer.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_manufacturer.tooltip.5"))
            .beginStructureBlock(9, 7, 7, true)
            .toolTipFinisher();
        return tt;
    }
}
