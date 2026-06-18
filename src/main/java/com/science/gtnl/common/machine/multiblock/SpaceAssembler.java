package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.api.IControllerUpgrade;
import com.science.gtnl.common.gui.modularui.SpaceAssemblerGui;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.recipes.RecipeUtil;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.GTStructureChannels;
import gtnhintergalactic.recipe.IGRecipeMaps;
import lombok.Getter;
import lombok.Setter;
import tectech.thing.casing.BlockGTCasingsTT;

public class SpaceAssembler extends GTMMultiMachineBase<SpaceAssembler>
    implements ISurvivalConstructable, IControllerUpgrade {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/space_assembler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SA_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;

    public static final ItemStack[] REQUIRED_ITEMS = new ItemStack[] { ItemList.SpaceElevatorModuleAssemblerT3.get(4),
        GTUtility.copyAmountUnsafe(320, ItemList.SpaceElevatorBaseCasing.get(1)), ItemList.Robot_Arm_UXV.get(32) };

    @Getter
    public ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    @Getter
    public ItemStackHandler upgradeInputSlotHandler = new ItemStackHandler(16);
    @Getter
    public int[] upgradePaidCosts = new int[REQUIRED_ITEMS.length];

    @Getter
    @Setter
    public boolean upgradeConsumed = false;

    public SpaceAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SpaceAssembler(String aName) {
        super(aName);
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        saveUpgradeNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        saveUpgradeNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        loadUpgradeNBTData(aNBT);
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        dropStoredUpgradeItems(getBaseMetaTileEntity());
    }

    @Override
    public ItemStack[] getUpgradeRequiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public String getUpgradeButtonTooltip() {
        return StatCollector.translateToLocal("Info_SpaceAssembler_00");
    }

    @Override
    public IStructureDefinition<SpaceAssembler> getStructureDefinition() {
        return StructureDefinition.<SpaceAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSEMotor, 2))
            .addElement('D', StructureUtility.ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(SpaceAssembler.class)
                    .casingIndex(BlockGTCasingsTT.textureOffset + 3)
                    .hint(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 3))))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 10);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return IGRecipeMaps.spaceAssemblerRecipes;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (RecipeUtil.isValidForSpaceStation(getBaseMetaTileEntity().getWorld().provider.dimensionId)
            || RecipeUtil.isValidForMothership(getBaseMetaTileEntity().getWorld().provider.dimensionId)) {
            return super.checkProcessing();
        }
        return RecipeUtil.NOT_IN_SPACE_STATION;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                int moduleTier = recipe.getMetadataOrDefault(IGRecipeMaps.MODULE_TIER, 0);
                if (moduleTier >= 3 && !upgradeConsumed) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public void checkEnergyHatch(List<StructureError> errors) {}

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0) * Math.pow(0.90, mGlassTier);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SpaceAssembler(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 13);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SpaceAssemblerGui(this);
    }

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this MUI1 fallback after the upgrade window is fully ported to MUI2.
        super.addUIWidgets(builder, buildContext);
        createUpgradeButton(builder, buildContext);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SpaceAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SpaceAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SpaceAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SpaceAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SpaceAssembler_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(11, 11, 11, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_SpaceAssembler_Casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

}
