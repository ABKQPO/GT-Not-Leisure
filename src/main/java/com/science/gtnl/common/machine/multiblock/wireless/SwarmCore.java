package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

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
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.gui.modularui.SwarmCoreGui;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import bartworks.common.loaders.ItemRegistry;
import goodgenerator.items.GGMaterial;
import goodgenerator.util.ItemRefer;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
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
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.LanthItemList;
import lombok.Getter;
import tectech.thing.CustomItemList;

@IMetaTileEntity.SkipGenerateDescription
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
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.SpaceTime, 32),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Creon, 64),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Mellion, 64),
            GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UMV, 64),
            GTUtility.copyAmountUnsafe(64, ItemList.Field_Generator_UIV.get(1)),
            GTModHandler.getModItem(Mods.DraconicEvolution.ID, "chaoticCore", 32),
            GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 64),
            GTOreDictUnificator.get(OrePrefixes.nanite, Materials.SixPhasedCopper, 64),
            GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Gold, 64) },
        { GTUtility.copyAmountUnsafe(1024, ItemList.FieldEnergyAbsorberCasing.get(1)),
            GTUtility.copyAmountUnsafe(1024, ItemList.LoadbearingDistributionCasing.get(1)),
            GTUtility.copyAmountUnsafe(1024, ItemList.MagneticAnchorCasing.get(1)),
            GTUtility.copyAmountUnsafe(1024, ItemList.PrecisionFieldSyncCasing.get(1)),
            GTUtility.copyAmountUnsafe(128, ItemList.NaniteFramework.get(1)),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.MagMatter, 8),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.MHDCSM, 64),
            GTUtility.copyAmountUnsafe(128, ItemList.Field_Generator_UXV.get(1)),
            GTUtility.copyAmountUnsafe(32, GTNLItemList.TransdimensionalMnemonicMatrix.get(1)),
            GTUtility.copyAmountUnsafe(16, ItemList.Transdimensional_Alignment_Matrix.get(1)),
            GTUtility.copyAmountUnsafe(8, CustomItemList.astralArrayFabricator.get(1)),
            GTUtility.copyAmountUnsafe(8, ItemList.Black_Hole_Stabilizer.get(1)),
            GTOreDictUnificator.get(OrePrefixes.nanite, Materials.MagMatter, 8),
            GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Eternity, 64),
            GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 64) } };

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
    public int getUpgradeProgress() {
        return machineTier;
    }

    @Override
    public void setUpgradeProgress(int progress) {
        machineTier = Math.clamp(progress, 1, REQUIRED_ITEMS.length + 1);
    }

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
        if (machineTier > 1) aNBT.setInteger("machineTier", machineTier);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        saveUpgradeNBTData(aNBT);
        if (machineTier > 1) aNBT.setInteger("machineTier", machineTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("machineTier")) machineTier = aNBT.getInteger("machineTier");
        loadUpgradeNBTData(aNBT);
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        dropStoredUpgradeItems(getBaseMetaTileEntity());
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
    public int getMaximumUpgradePreviewLevel() {
        return REQUIRED_ITEMS.length - 1;
    }

    @Override
    public int getUpgradeDisplayPageCount() {
        return REQUIRED_ITEMS.length;
    }

    @Override
    public int getCurrentUpgradeDisplayPage() {
        return Math.clamp(machineTier - 1, 0, REQUIRED_ITEMS.length - 1);
    }

    @Override
    public ItemStack[] getUpgradeDisplayItems(int displayPage) {
        if (displayPage < 0 || displayPage >= REQUIRED_ITEMS.length) return new ItemStack[0];
        return REQUIRED_ITEMS[displayPage];
    }

    @Override
    public int[] getUpgradeDisplayPaidCosts(int displayPage) {
        if (displayPage < 0 || displayPage >= upgradePaidCosts.length) return new int[0];
        return upgradePaidCosts[displayPage];
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
        return StatCollector.translateToLocal("gtnl.machine.swarm_core.upgrade_required");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.swarm_core.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.swarm_core.info.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.5"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.6"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.7"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.8"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.9"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.10"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.swarm_core.info.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.swarm_core.info.2"))
            .addSupportAny()
            .beginStructureBlock(41, 54, 41, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.swarm_core.casing"))
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.swarm_core.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.swarm_core.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.swarm_core.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.swarm_core.casing"))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.swarm_core.casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return Casings.RadiantNaquadahAlloyCasing.getTextureId();
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
            .addElement('A', Casings.HollowCasing.asElement())
            .addElement('B', Casings.ExtremeDensitySpaceBendingCasing.asElement())
            .addElement('C', Casings.HighPowerCasing.asElement())
            .addElement('D', GTNLCasings.HyperCore.asElement())
            .addElement('E', Casings.ContainmentFieldGenerator.asElement())
            .addElement('F', Casings.DimensionalBridge.asElement())
            .addElement('G', GTStructureUtility.activeCoils(Casings.EternalCoilBlock.asElement()))
            .addElement('H', Casings.QuarkExclusionCasing.asElement())
            .addElement('I', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement('J', Casings.MolecularCasing.asElement())
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
                    .hint(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.RadiantNaquadahAlloyCasing.asElement())))
            .addElement('L', GTNLCasings.FusionGlass.asElement())
            .addElement('M', Casings.ActiveNeutroniumCasing.asElement())
            .addElement('N', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement('O', Casings.ElectronPermeableNeutroniumCoatedGlass.asElement())
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 201);
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

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SwarmCoreGui(this);
    }

    public int getMachineTierForGui() {
        return machineTier;
    }

    public void setMachineTierFromGui(int machineTier) {
        this.machineTier = machineTier;
    }

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after the Swarm Core upgrade GUI is fully ported to mui2.
        super.addUIWidgets(builder, buildContext);
        createUpgradeButton(builder, buildContext);
    }

    @Override
    @Deprecated
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        // TODO: Remove this mui1 fallback after the Swarm Core terminal text is fully ported to mui2.
        super.drawTexts(screenElements, inventorySlot);
        screenElements
            .widget(
                new TextWidget().setStringSupplier(
                    () -> StatCollector.translateToLocalFormatted("gtnl.machine.swarm_core.current_tier", machineTier))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(true))
            .widget(
                new FakeSyncWidget.IntegerSyncer(() -> machineTier, tier -> machineTier = tier).setSynced(true, false));
    }

}
