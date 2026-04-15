package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLStructureChannels;

import cpw.mods.fml.common.registry.GameRegistry;
import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtnhlanth.common.register.LanthItemList;

public class NanoAssemblerMarkL extends WirelessEnergyMultiMachineBase<NanoAssemblerMarkL> {

    private int mCasingTier;
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String VMC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/nano_assembler_mark_l";
    private static final String[][] shape = StructureUtils.readStructureFromFile(VMC_STRUCTURE_FILE_PATH);

    public NanoAssemblerMarkL(String aName) {
        super(aName);
    }

    public NanoAssemblerMarkL(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NanoAssemblerMarkL(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("NanoAssemblerMarkLRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IntegratedAssemblyFacility_01"))
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
            .beginStructureBlock(13, 10, 31, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_NanoAssemblerMarkL_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_NanoAssemblerMarkL_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_NanoAssemblerMarkL_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_NanoAssemblerMarkL_Casing"), 1)
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
    public IStructureDefinition<NanoAssemblerMarkL> getStructureDefinition() {
        return StructureDefinition.<NanoAssemblerMarkL>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(sBlockCasingsTT, 8))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement(
                'C',
                GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        (block, meta) -> block == Loaders.componentAssemblylineCasing ? meta : -1,
                        IntStream.range(0, 13)
                            .mapToObj(i -> Pair.of(Loaders.componentAssemblylineCasing, i))
                            .collect(Collectors.toList()),
                        -2,
                        (t, meta) -> t.mCasingTier = meta,
                        t -> t.mCasingTier)))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 11))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(NanoAssemblerMarkL.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('G', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('H', StructureUtility.ofBlock(sBlockCasingsTT, 4))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 8))
            .addElement('J', GTStructureUtility.ofFrame(Materials.Duranium))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockGlass1, 0))
            .addElement('L', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 9))
            .addElement(
                'N',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.TRINIUM_NAQUADAH_CARBON.getFrameBox(1)
                            .getItem())))
            .addElement(
                'O',
                StructureUtility.ofBlockAnyMeta(GameRegistry.findBlock(Mods.IndustrialCraft2.ID, "blockAlloyGlass")))
            .addElement('P', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('Q', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 1))
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
        return mCountCasing > 250;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mCasingTier >= 0;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mCasingTier = -2;
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
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("casingTier", mCasingTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mCasingTier = aNBT.getInteger("casingTier");
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

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.9, mCasingTier);
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.9, mCasingTier);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GoodGeneratorRecipeMaps.preciseAssemblerRecipes;
    }

}
