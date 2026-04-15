package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;

public class PlatinumBasedTreatment extends MultiMachineBase<PlatinumBasedTreatment> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PBT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/platinum_based_treatment";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PBT_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 15;
    private static final int DEPTH_OFF_SET = 0;

    public PlatinumBasedTreatment(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PlatinumBasedTreatment(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new PlatinumBasedTreatment(this.mName);
    }

    @Override
    public IStructureDefinition<PlatinumBasedTreatment> getStructureDefinition() {
        return StructureDefinition.<PlatinumBasedTreatment>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 11))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sSolenoidCoilCasings, 3))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 13))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 14))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 0))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement(
                'H',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility
                            .ofCoil(PlatinumBasedTreatment::setMCoilLevel, PlatinumBasedTreatment::getMCoilLevel))))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('K', GTStructureUtility.ofFrame(Materials.BlackSteel))
            .addElement('L', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 5))
            .addElement('M', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 6))
            .addElement('N', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 11))
            .addElement(
                'O',
                GTStructureUtility.buildHatchAdder(PlatinumBasedTreatment.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputHatch,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(ModBlocks.blockCasings3Misc, 2))))
            .addElement('P', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 0))
            .addElement('Q', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 5))
            .addElement(
                'R',
                HatchElement.Muffler.newAny(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 11), 6))
            .build();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("PlatinumBasedTreatmentRecipes"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_05"))
            .addPerfectOCInfo()
            .addTecTechHatchInfo()
            .beginStructureBlock(15, 17, 18, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_Casing_00"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_Casing_00"))
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_PlatinumBasedTreatment_Casing_01"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)) };
    }

    @Override
    public int getCasingTextureID() {
        return TAE.getIndexFromPage(2, 2);
    }

    @Override
    public void updateHatchTexture() {
        super.updateHatchTexture();
        for (MTEHatch h : mMufflerHatches)
            h.updateTexture(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 11));
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public double getEUtDiscount() {
        return 1.0 - getMCoilLevel().getTier() * 0.05;
    }

    @Override
    public double getDurationModifier() {
        return 1.0 - getMCoilLevel().getTier() * 0.05;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        return GTUtility.getTier(this.getMaxInputVoltage()) * 4 + 8;
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
    public boolean checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 30;
    }

    @Override
    public boolean checkHatch() {
        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV && mEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        for (MTEHatch mExoticEnergyHatch : this.mExoticEnergyHatches) {
            if (mGlassTier < VoltageIndex.UHV && mExoticEnergyHatch.mTier > mGlassTier) {
                return false;
            }
        }
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None && mMufflerHatches.size() == 6;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.PlatinumBasedTreatmentRecipes;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MEGA_BLAST_FURNACE_LOOP;
    }

}
