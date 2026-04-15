package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static kubatech.loaders.BlockLoader.defcCasingBlock;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import bartworks.common.loaders.ItemRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
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
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.casing.BlockGTCasingsTT;

public class SmeltingMixingFurnace extends WirelessEnergyMultiMachineBase<SmeltingMixingFurnace> {

    public static final FluidStack[] valid_fuels = { MaterialsUEVplus.ExcitedDTCC.getFluid(1L),
        MaterialsUEVplus.ExcitedDTPC.getFluid(1L), MaterialsUEVplus.ExcitedDTRC.getFluid(1L),
        MaterialsUEVplus.ExcitedDTEC.getFluid(1L), MaterialsUEVplus.ExcitedDTSC.getFluid(1L) };

    public static final ItemStack martix = ItemList.Transdimensional_Alignment_Matrix.get(1);

    private static final int HORIZONTAL_OFF_SET = 8;
    private static final int VERTICAL_OFF_SET = 14;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SMF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/smelting_mixing_furnace";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SMF_STRUCTURE_FILE_PATH);
    public static final int MACHINEMODE_SMF = 0;
    public static final int MACHINEMODE_DTPF = 1;
    public boolean enableMnemonic = false;

    public SmeltingMixingFurnace(String aName) {
        super(aName);
    }

    public SmeltingMixingFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SmeltingMixingFurnace(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SmeltingMixingFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_03"))
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
            .beginStructureBlock(17, 17, 33, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SmeltingMixingFurnace_Casing"), 1)
            .toolTipFinisher();
        return tt;
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
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset;
    }

    @Override
    public IStructureDefinition<SmeltingMixingFurnace> getStructureDefinition() {
        return StructureDefinition.<SmeltingMixingFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(ItemRegistry.bw_realglas2, 0))
            .addElement('B', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('C', StructureUtility.ofBlock(BlockLoader.metaCasing, 7))
            .addElement('D', StructureUtility.ofBlock(defcCasingBlock, 7))
            .addElement('E', StructureUtility.ofBlock(defcCasingBlock, 10))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 12))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 13))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 12))
            .addElement('L', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('M', StructureUtility.ofBlock(sBlockCasingsTT, 8))
            .addElement('N', GTStructureUtility.ofFrame(Materials.Infinity))
            .addElement('O', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 4))
            .addElement('P', StructureUtility.ofBlock(ModBlocks.blockSpecialMultiCasings, 11))
            .addElement('Q', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 12))
            .addElement(
                'R',
                GTStructureUtility.buildHatchAdder(SmeltingMixingFurnace.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++this.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 0))))
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
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!enableMnemonic) {
            ItemStack heldItem = aPlayer.getHeldItem();
            if (GTUtility.areStacksEqual(heldItem, GTNLItemList.TransdimensionalMnemonicMatrix.get(1), true)) {
                aPlayer.setCurrentItemOrArmor(0, ItemUtils.depleteStack(heldItem, 1));
                enableMnemonic = true;
                return true;
            }
        }
        return super.onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        aNBT.setBoolean("enableMnemonic", enableMnemonic);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("enableMnemonic", enableMnemonic);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        enableMnemonic = aNBT.getBoolean("enableMnemonic");
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == MACHINEMODE_SMF) ? GTNLRecipeMaps.SmeltingMixingFurnaceRecipes
            : RecipeMaps.plasmaForgeRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTNLRecipeMaps.SmeltingMixingFurnaceRecipes, RecipeMaps.plasmaForgeRecipes);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (this.getRecipeMap() == RecipeMaps.plasmaForgeRecipes) {
            if (!GTUtility.areStacksEqual(martix, getControllerSlot())) return CheckRecipeResultRegistry.NO_RECIPE;
        }
        return super.checkProcessing();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public Stream<GTRecipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
                if (map == null) return Stream.empty();
                if (!enableMnemonic || map != RecipeMaps.plasmaForgeRecipes) return super.findRecipeMatches(map);

                // Allow recipes to start without having 100% of the catalyst required, aka discount > 0%
                FluidStack[] queryFluids = new FluidStack[inputFluids.length];
                for (int i = 0; i < inputFluids.length; i++) {
                    queryFluids[i] = new FluidStack(inputFluids[i].getFluid(), inputFluids[i].amount);
                    for (FluidStack fuel : valid_fuels) {
                        if (queryFluids[i].isFluidEqual(fuel)) {
                            queryFluids[i].amount = (int) Math
                                .min(Math.round(queryFluids[i].amount * 2d), Integer.MAX_VALUE);
                            break;
                        }
                    }
                }

                return map.findRecipeQuery()
                    .items(inputItems)
                    .fluids(queryFluids)
                    .specialSlot(specialSlotItem)
                    .cachedRecipe(lastRecipe)
                    .findAll();
            }

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > GTValues.V[Math.min(mParallelTier + 1, 14)] * 4
                    && machineMode != MACHINEMODE_DTPF) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(recipeAfterAdjustments(recipe));
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipeAfterAdjustments(recipe))
                    .setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .enablePerfectOC();
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    public GTRecipe recipeAfterAdjustments(@NotNull GTRecipe recipe) {
        if (!enableMnemonic) return recipe;
        GTRecipe tRecipe = recipe.copy();
        for (int i = 0; i < recipe.mFluidInputs.length; i++) {
            for (FluidStack fuel : valid_fuels) {
                if (tRecipe.mFluidInputs[i].isFluidEqual(fuel)) {
                    tRecipe.mFluidInputs[i].amount = (int) Math.round(tRecipe.mFluidInputs[i].amount * 0.5);
                    return tRecipe;
                }
            }
        }
        return tRecipe;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        if (wirelessMode) {
            logic.setAvailableVoltage(
                machineMode == MACHINEMODE_DTPF ? Integer.MAX_VALUE * 8L : GTValues.V[Math.min(mParallelTier + 1, 14)]);
            logic.setAvailableAmperage(8L << (2L * mParallelTier) - 2L);
            logic.setAmperageOC(true);
            logic.enablePerfectOverclock();
        } else {
            boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty()
                && getMaxInputAmps() <= 4;
            logic.setAvailableVoltage(
                machineMode == MACHINEMODE_DTPF ? getMachineVoltageLimit() * getMaxInputAmps()
                    : getMachineVoltageLimit());
            logic.setAvailableAmperage((machineMode == MACHINEMODE_DTPF || useSingleAmp) ? 1 : getMaxInputAmps());
            logic.setAmperageOC(!useSingleAmp);
        }
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount();
    }

    @Override
    public double getDurationModifier() {
        return 1 * Math.pow(0.75, mParallelTier);
    }

    @Override
    public String[] getInfoData() {
        if (!enableMnemonic) return super.getInfoData();
        String[] original = super.getInfoData();
        List<String> list = new ArrayList<>(Arrays.asList(original));
        list.add(StatCollector.translateToLocal("Info_PlasmaForge_00"));
        return list.toArray(new String[0]);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (!tag.getBoolean("enableMnemonic")) return;
        currentTip.add(StatCollector.translateToLocal("Info_PlasmaForge_00"));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setBoolean("enableMnemonic", enableMnemonic);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 2;
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("SmeltingMixingFurnace_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("SmeltingMixingFurnace_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

}
