package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.api.IControllerUpgrade;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import bartworks.common.loaders.ItemRegistry;
import goodgenerator.items.GGMaterial;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.LanthItemList;
import lombok.Getter;
import tectech.thing.CustomItemList;

public class SwarmCore extends WirelessEnergyMultiMachineBase<SwarmCore> implements IControllerUpgrade {

    private static final int HORIZONTAL_OFF_SET = 20;
    private static final int VERTICAL_OFF_SET = 47;
    private static final int DEPTH_OFF_SET = 8;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/swarm_core";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SC_STRUCTURE_FILE_PATH);

    public static final ItemStack[][] REQUIRED_ITEMS = new ItemStack[][] {
        { GTUtility.copyAmountUnsafe(64, ItemList.RadiationProofPhotolithographicFrameworkCasing.get(1)),
            GTUtility.copyAmountUnsafe(64, ItemList.ReinforcedPhotolithographicFrameworkCasing.get(1)),
            GTUtility.copyAmountUnsafe(64, GregtechItemList.GTPP_Casing_UHV.get(1)),
            GTUtility.copyAmountUnsafe(64, GregtechItemList.NeutronShieldingCore.get(1)),
            GTUtility.copyAmountUnsafe(64, MaterialsElements.STANDALONE.HYPOGEN.getFrameBox(1)),
            ItemList.Electric_Motor_UEV.get(32), ItemList.Emitter_UEV.get(8), ItemList.Sensor_UEV.get(8),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.TengamAttuned, 32),
            GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 16),
            GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUEV, 64),
            GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Glowstone, 16),
            GTUtility.copyAmountUnsafe(16, GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.nanite, 1)),
            GTUtility.copyAmountUnsafe(16, GGMaterial.metastableOganesson.get(OrePrefixes.nanite, 1)) },
        { GTUtility.copyAmountUnsafe(128, ItemRefer.MagneticFluxCasing.get(1)),
            GTUtility.copyAmountUnsafe(128, GregtechItemList.InfinityInfusedManipulator.get(1)),
            GTUtility.copyAmountUnsafe(128, GregtechItemList.InfinityInfusedShieldingCore.get(1)),
            GTModHandler.getModItem(Mods.GalacticraftAmunRa.ID, "tile.baseBlockRock", 48, 14),
            GTUtility.copyAmountUnsafe(128, ItemRefer.GravityStabilizationCasing.get(1)),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.SpaceTime, 32),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Creon, 64),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Mellion, 64),
            GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UMV, 64),
            GTUtility.copyAmountUnsafe(64, ItemList.Field_Generator_UIV.get(1)),
            GTModHandler.getModItem(Mods.DraconicEvolution.ID, "chaoticCore", 32),
            GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 64),
            GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.SixPhasedCopper, 64),
            GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Gold, 64) },
        { GTUtility.copyAmountUnsafe(1024, ItemList.FieldEnergyAbsorberCasing.get(1)),
            GTUtility.copyAmountUnsafe(1024, ItemList.LoadbearingDistributionCasing.get(1)),
            GTUtility.copyAmountUnsafe(1024, ItemList.MagneticAnchorCasing.get(1)),
            GTUtility.copyAmountUnsafe(1024, ItemList.PrecisionFieldSyncCasing.get(1)),
            GTUtility.copyAmountUnsafe(128, ItemList.NaniteFramework.get(1)),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.MagMatter, 8),
            GTOreDictUnificator
                .get(OrePrefixes.plateSuperdense, MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter, 64),
            GTUtility.copyAmountUnsafe(128, ItemList.Field_Generator_UXV.get(1)),
            GTUtility.copyAmountUnsafe(32, GTNLItemList.TransdimensionalMnemonicMatrix.get(1)),
            GTUtility.copyAmountUnsafe(16, ItemList.Transdimensional_Alignment_Matrix.get(1)),
            GTUtility.copyAmountUnsafe(8, CustomItemList.astralArrayFabricator.get(1)),
            GTUtility.copyAmountUnsafe(8, ItemList.Black_Hole_Stabilizer.get(1)),
            GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.MagMatter, 8),
            GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.Eternity, 64),
            GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.Universium, 64) } };

    @Getter
    public ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    @Getter
    public ItemStackHandler upgradeInputSlotHandler = new ItemStackHandler(16);
    public int[][] upgradePaidCosts = new int[][] { new int[REQUIRED_ITEMS[0].length],
        new int[REQUIRED_ITEMS[1].length], new int[REQUIRED_ITEMS[2].length] };
    public int machineTier = 1;

    public SwarmCore(String aName) {
        super(aName);
    }

    public SwarmCore(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SwarmCore(this.mName);
    }

    @Override
    public int[] getUpgradePaidCosts() {
        return upgradePaidCosts[Math.min(machineTier - 1, 2)];
    }

    @Override
    public boolean isUpgradeConsumed() {
        return machineTier >= 4;
    }

    @Override
    public void setUpgradeConsumed(boolean upgradeConsumed) {}

    @Override
    public boolean tryConsumeItems() {
        boolean result = IControllerUpgrade.super.tryConsumeItems();
        if (result && machineTier < 4) machineTier++;
        return result;
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        saveUpgradeNBTData(aNBT);
        aNBT.setInteger("machineTier", machineTier);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        saveUpgradeNBTData(aNBT);
        aNBT.setInteger("machineTier", machineTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        loadUpgradeNBTData(aNBT);
        machineTier = aNBT.getInteger("machineTier");
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        dropStoredUpgradeItems(getBaseMetaTileEntity());
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        createUpgradeButton(builder, buildContext);
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements
            .widget(
                new TextWidget()
                    .setStringSupplier(() -> StatCollector.translateToLocalFormatted("Info_SwarmCore_01", machineTier))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(true))
            .widget(
                new FakeSyncWidget.IntegerSyncer(() -> machineTier, tier -> machineTier = tier).setSynced(true, false));
    }

    @Override
    public ItemStack[] getUpgradeRequiredItems() {
        return REQUIRED_ITEMS[Math.min(machineTier - 1, 2)];
    }

    @Override
    public ItemStack[] getPreviewUpgradeRequiredItems() {
        return getPreviewUpgradeRequiredItems(1);
    }

    @Override
    public int getMaxPreviewUpgradeLevel() {
        return Math.max(0, REQUIRED_ITEMS.length - machineTier);
    }

    @Override
    public ItemStack[] getPreviewUpgradeRequiredItems(int previewLevel) {
        int previewIndex = machineTier - 1 + previewLevel;
        if (previewIndex >= REQUIRED_ITEMS.length) return new ItemStack[0];
        return REQUIRED_ITEMS[previewIndex];
    }

    @Override
    public int[] getPreviewUpgradePaidCosts() {
        return getPreviewUpgradePaidCosts(1);
    }

    @Override
    public int[] getPreviewUpgradePaidCosts(int previewLevel) {
        int previewIndex = machineTier - 1 + previewLevel;
        if (previewIndex >= upgradePaidCosts.length) return new int[0];
        return upgradePaidCosts[previewIndex];
    }

    @Override
    public String getUpgradeButtonTooltip() {
        return StatCollector.translateToLocal("Info_SwarmCore_00");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SwarmCoreRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SwarmCore_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SwarmCore_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SwarmCore_02"))
            .addTecTechHatchInfo()
            .beginStructureBlock(41, 54, 41, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SwarmCore_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_SwarmCore_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_SwarmCore_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SwarmCore_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SwarmCore_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_SwarmCore_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<SwarmCore> getStructureDefinition() {
        return StructureDefinition.<SwarmCore>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(sBlockCasingsTT, 8))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('C', StructureUtility.ofBlock(sBlockCasingsTT, 0))
            .addElement('D', StructureUtility.ofBlock(BlockLoader.metaCasing, 18))
            .addElement('E', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement('G', GTStructureUtility.activeCoils(StructureUtility.ofBlock(GregTechAPI.sBlockCasings5, 13)))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 2))
            .addElement('I', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement('J', StructureUtility.ofBlock(sBlockCasingsTT, 4))
            .addElement(
                'K',
                GTStructureUtility.buildHatchAdder(SwarmCore.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        CustomHatchElement.ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))))
            .addElement('L', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('N', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockGlass1, 1))
            .addElement('P', GTStructureUtility.ofFrame(Materials.NaquadahAlloy))
            .addElement('Q', StructureUtility.ofBlock(ItemRegistry.bw_realglas2, 0))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing > 200;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mSpecialValue > machineTier) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
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
    public void setProcessingLogicPower(ProcessingLogic logic) {
        if (wirelessMode) {
            logic.setAvailableVoltage(Integer.MAX_VALUE);
            logic.setAvailableAmperage((8L << (2 * mParallelTier)) - 2L);
            logic.setAmperageOC(false);
            logic.enablePerfectOverclock();
        } else {
            boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty()
                && getMaxInputAmps() <= 4;
            logic.setAvailableVoltage(getMaxInputEu());
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(!useSingleAmp);
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.nanoForgeRecipes;
    }

}
