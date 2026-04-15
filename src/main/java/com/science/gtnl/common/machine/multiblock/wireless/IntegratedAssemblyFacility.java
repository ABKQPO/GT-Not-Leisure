package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.dreammaster.gthandler.CustomItemList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.api.IControllerUpgrade;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLStructureChannels;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import cpw.mods.fml.common.Optional;
import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import goodgenerator.items.GGMaterial;
import goodgenerator.loader.Loaders;
import goodgenerator.util.ItemRefer;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtnhlanth.common.register.LanthItemList;
import lombok.Getter;
import lombok.Setter;

public class IntegratedAssemblyFacility extends WirelessEnergyMultiMachineBase<IntegratedAssemblyFacility>
    implements IControllerUpgrade {

    private static final int HORIZONTAL_OFF_SET = 8;
    private static final int VERTICAL_OFF_SET = 10;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String IAF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/integrated_assembly_facility";
    private static final String[][] shape = StructureUtils.readStructureFromFile(IAF_STRUCTURE_FILE_PATH);

    private static final int MACHINEMODE_ASSEM = 0;
    private static final int MACHINEMODE_COMPO = 1;

    public static final ItemStack[] REQUIRED_ITEMS = new ItemStack[] { ItemRefer.Component_Assembly_Line.get(64),
        GTNLItemList.ComponentAssembler.get(64),
        GTOreDictUnificator.get(OrePrefixes.frameGt, MaterialsUEVplus.TranscendentMetal, 64),
        ItemList.Robot_Arm_UIV.get(64), ItemList.Field_Generator_UIV.get(32), ItemList.Sensor_UIV.get(32),
        ItemList.Emitter_UIV.get(32), GTNLItemList.EnhancementCore.get(16),
        GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.TranscendentMetal, 16),
        Mods.NewHorizonsCoreMod.isModLoaded() ? getPikoCircuit() : null,
        GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUIV, 32),
        GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Mellion, 8),
        GGMaterial.shirabon.get(OrePrefixes.plateSuperdense, 8) };

    @Optional.Method(modid = "dreamcraft")
    public static ItemStack getPikoCircuit() {
        return CustomItemList.PikoCircuit.get(32);
    }

    @Getter
    public ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    @Getter
    public ItemStackHandler upgradeInputSlotHandler = new ItemStackHandler(16);
    @Getter
    public int[] upgradePaidCosts = new int[REQUIRED_ITEMS.length];

    @Getter
    @Setter
    public boolean upgradeConsumed = false;

    public int mCasingTier;

    public IntegratedAssemblyFacility(String aName) {
        super(aName);
    }

    public IntegratedAssemblyFacility(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new IntegratedAssemblyFacility(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("IntegratedAssemblyFacilityRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_02"))
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
            .addTecTechHatchInfo()
            .beginStructureBlock(47, 13, 19, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<IntegratedAssemblyFacility> getStructureDefinition() {
        return StructureDefinition.<IntegratedAssemblyFacility>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement(
                'B',
                GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        (block, meta) -> block == Loaders.componentAssemblylineCasing ? meta : -1,
                        IntStream.range(0, 13)
                            .mapToObj(i -> Pair.of(Loaders.componentAssemblylineCasing, i))
                            .collect(Collectors.toList()),
                        -2,
                        (t, meta) -> t.mCasingTier = meta,
                        t -> t.mCasingTier)))
            .addElement(
                'C',
                GTStructureUtility.buildHatchAdder(IntegratedAssemblyFacility.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7))
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 6))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 1))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 12))
            .addElement('G', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('I', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('J', StructureUtility.ofBlock(LanthItemList.NIOBIUM_CAVITY_CASING, 0))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('L', StructureUtility.ofBlock(LanthItemList.COOLANT_DELIVERY_CASING, 0))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSE, 1))
            .addElement('N', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('O', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 31))
            .addElement('P', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 9))
            .addElement('Q', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('R', GTStructureUtility.ofFrame(Materials.CosmicNeutronium))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing > 1000;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mCasingTier = -2;
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        dropStoredUpgradeItems(getBaseMetaTileEntity());
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mCasingTier >= 0;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.95, mGlassTier) * Math.pow(0.95, mCasingTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.95, mGlassTier) * Math.pow(0.95, mCasingTier);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > GTValues.V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                if (machineMode == MACHINEMODE_COMPO && !upgradeConsumed) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                if (machineMode == MACHINEMODE_COMPO && recipe.mSpecialValue > mCasingTier + 1) {
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
    public long getMachineVoltageLimit() {
        if (mCasingTier < 0) return 0;
        if (wirelessMode) {
            if (mCasingTier >= 10) {
                return GTValues.V[Math.min(mParallelTier + 1, 14)];
            } else {
                return GTValues.V[Math.min(Math.min(mParallelTier + 1, mCasingTier + 4), 14)];
            }
        } else if (mCasingTier >= 10) {
            return GTValues.V[mEnergyHatchTier];
        } else {
            return GTValues.V[Math.min(mCasingTier + 4, mEnergyHatchTier)];
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        int base = super.getMaxParallelRecipes();
        if (machineMode == MACHINEMODE_COMPO) {
            base >>= 4;
            if (base < 1) base = 1;
        }

        return base;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == MACHINEMODE_ASSEM ? RecipeMaps.assemblerRecipes
            : GoodGeneratorRecipeMaps.componentAssemblyLineRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.assemblerRecipes, GoodGeneratorRecipeMaps.componentAssemblyLineRecipes);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 2;
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("IntegratedAssemblyFacility_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("IntegratedAssemblyFacility_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
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
        aNBT.setInteger("mGlassTier", mGlassTier);
        aNBT.setInteger("casingTier", mCasingTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        loadUpgradeNBTData(aNBT);
        mGlassTier = aNBT.getInteger("mGlassTier");
        mCasingTier = aNBT.getInteger("casingTier");
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        createUpgradeButton(builder, buildContext);
    }

    @Override
    public ItemStack[] getUpgradeRequiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public String getUpgradeButtonTooltip() {
        return StatCollector.translateToLocal("Info_IntegratedAssemblyFacility_00");
    }

    @Override
    public String[] getInfoData() {
        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 1];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = StatCollector.translateToLocal("scanner.info.CASS.tier")
            + (mCasingTier >= 0 ? GTValues.VN[mCasingTier + 1] : "None!");
        return ret;
    }

}
