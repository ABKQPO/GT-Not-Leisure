package com.science.gtnl.common.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.TextLocalization;
import com.science.gtnl.Utils.TextUtils;
import com.science.gtnl.common.block.Casings.BasicBlocks;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import bartworks.API.BorosilicateGlass;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.objects.GTRenderedTexture;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.blocks.BlockCasings9;
import tectech.thing.casing.TTCasingsContainer;

public class NeutroniumWireCutting extends MultiMachineBase<NeutroniumWireCutting> implements ISurvivalConstructable {

    protected GTRecipe lastRecipeToBuffer;

    public byte mGlassTier = 0;

    public static final int HORIZONTAL_OFF_SET = 3;
    public static final int VERTICAL_OFF_SET = 10;
    public static final int DEPTH_OFF_SET = 0;

    public int tCountCasing = 0;

    public int casing;

    public IStructureDefinition<NeutroniumWireCutting> STRUCTURE_DEFINITION = null;

    public static final String STRUCTURE_PIECE_MAIN = "main";

    public static final String ICF_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/neutronium_wire_cutting"; // 文件路径

    public String[][] shape;

    public NeutroniumWireCutting(String aName) {
        super(aName);
        this.shape = StructureUtils.readStructureFromFile(ICF_STRUCTURE_FILE_PATH);
    }

    public NeutroniumWireCutting(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        this.shape = StructureUtils.readStructureFromFile(ICF_STRUCTURE_FILE_PATH);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NeutroniumWireCutting(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.LapotronChipRecipeType)
            .addInfo(TextLocalization.Tooltip_LapotronChip_00)
            .addInfo(TextLocalization.Tooltip_LapotronChip_01)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(177, 121, 177, true)
            .addInputBus(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addInputHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addOutputHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addEnergyHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .addMaintenanceHatch(TextLocalization.Tooltip_LapotronChip_Casing, 1)
            .toolTipFinisher(TextUtils.SQY);
        return tt;
    }

    protected void updateHatchTexture() {
        for (MTEHatch h : mInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
    }

    public int getCasingTextureID() {
        return ((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(13);
    }

    protected GTRenderedTexture LCgetFrontOverlay() {
        return new GTRenderedTexture(Textures.BlockIcons.OVERLAY_DTPF_OFF);
    }

    protected GTRenderedTexture LCgetFrontOverlayActive() {
        return new GTRenderedTexture(Textures.BlockIcons.OVERLAY_DTPF_ON);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final ForgeDirection side,
        final ForgeDirection facing, final int aColorIndex, final boolean aActive, final boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                aActive ? LCgetFrontOverlayActive() : LCgetFrontOverlay() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<NeutroniumWireCutting> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<NeutroniumWireCutting>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', BorosilicateGlass.ofBoroGlass((byte) 0, (t, v) -> t.mGlassTier = v, t -> t.mGlassTier))
                .addElement('B', ofBlock(BasicBlocks.MetaBlockCasing, 2))
                .addElement('C', ofBlockAnyMeta(GameRegistry.findBlock("IC2", "blockAlloyGlass")))
                .addElement('D', ofBlock(sBlockCasings10, 6))
                .addElement('E', ofBlock(sBlockCasings10, 7))
                .addElement('F', ofBlock(sBlockCasings10, 11))
                .addElement('G', ofBlock(sBlockCasings3, 11))
                .addElement('H', ofBlock(sBlockCasings4, 10))
                .addElement('I', ofBlock(sBlockCasings8, 7))
                .addElement('J', ofBlock(sBlockCasings9, 3))
                .addElement('K', ofBlock(sBlockCasings9, 6))
                .addElement(
                    'L',
                    buildHatchAdder(NeutroniumWireCutting.class)
                        .atLeast(InputBus, OutputBus, InputHatch, Energy, Energy.or(ExoticEnergy))
                        .casingIndex(((BlockCasings9) GregTechAPI.sBlockCasings9).getTextureIndex(12))
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.casing, ofBlock(sBlockCasings9, 12))))
                .addElement('M', ofBlock(TTCasingsContainer.sBlockCasingsTT, 0))
                .addElement('N', ofBlock(TTCasingsContainer.sBlockCasingsTT, 6))
                .addElement('O', ofFrame(Materials.Neutronium))
                .addElement('P', ofBlockAnyMeta(GameRegistry.findBlock("miscutils", "blockFrameGtHastelloyN")))
                .addElement('Q', ofBlock(BasicBlocks.MetaBlockCasing, 4))
                .addElement('R', ofBlock(BasicBlocks.MetaBlockCasing, 5))
                .build();
        }
        return STRUCTURE_DEFINITION;
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
        return this.survivialBuildPiece(
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

    public boolean checkHatches() {
        return !mInputHatches.isEmpty() && !mInputBusses.isEmpty()
            && !mOutputBusses.isEmpty()
            && mOutputHatches.isEmpty();
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        tCountCasing = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        if (tCountCasing >= 100 && checkHatches()) {
            updateHatchTexture();
            return true;
        }
        return false;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.cutterRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setSpeedBonus(1F / 5F)
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return ((mGlassTier + GTUtility.getTier(this.getMaxInputVoltage())) ^ 2);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("mGlassTier", mGlassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mGlassTier = aNBT.getByte("mGlassTier");
    }

    @Override
    public String[] getInfoData() {
        final String running = (this.mMaxProgresstime > 0 ? "Machine running" : "Machine stopped");
        final String maintenance = (this.getIdealStatus() == this.getRepairStatus() ? "No Maintenance issues"
            : "Needs Maintenance");
        String tSpecialText;

        if (lastRecipeToBuffer != null && lastRecipeToBuffer.mOutputs[0].getDisplayName() != null) {
            tSpecialText = "Currently processing: " + lastRecipeToBuffer.mOutputs[0].getDisplayName();
        } else {
            tSpecialText = "Currently processing: Nothing";
        }

        return new String[] { "Industrial Cutting Factory", running, maintenance, tSpecialText };
    }

}
