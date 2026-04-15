package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Collection;

import net.minecraft.init.Blocks;
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
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.StructureError;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class BrickedBlastFurnace extends SteamMultiMachineBase<BrickedBlastFurnace> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String BBF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/bricked_blast_furnace";
    private static final String[][] shape = StructureUtils.readStructureFromFile(BBF_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 12;
    private static final int DEPTH_OFF_SET = 0;

    public BrickedBlastFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public BrickedBlastFurnace(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new BrickedBlastFurnace(this.mName);
    }

    @Override
    public boolean getPerfectOC() {
        return false;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public boolean supportsSteamOC() {
        return false;
    }

    @Override
    public boolean supportsSteamCapacityUI() {
        return false;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("BrickBlastFurnaceRecipeType");
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_INACTIVE)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("BrickBlastFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_04"))
            .beginStructureBlock(15, 14, 15, true)
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_Casing_00"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_Casing_01"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_Casing_02"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_Casing_03"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_BrickBlastFurnace_Casing_04"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<BrickedBlastFurnace> getStructureDefinition() {
        return StructureDefinition.<BrickedBlastFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 13))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(BrickedBlastFurnace.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        SteamHatchElement.InputBus_Steam,
                        HatchElement.InputBus,
                        SteamHatchElement.OutputBus_Steam,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 15))))
            .addElement('C', GTStructureUtility.ofFrame(Materials.Bronze))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 10))
            .addElement('E', StructureUtility.ofBlockAnyMeta(Blocks.stonebrick))
            .build();
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 15);
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        return mCountCasing >= 350;
    }

    @Override
    public void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {}

    @Override
    public int getMaxParallelRecipes() {
        return 16777216;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.primitiveBlastRecipes;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        CheckRecipeResult result = super.checkProcessing();
        if (!result.wasSuccessful()) return result;
        mMaxProgresstime = 128;
        return result;
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }
}
