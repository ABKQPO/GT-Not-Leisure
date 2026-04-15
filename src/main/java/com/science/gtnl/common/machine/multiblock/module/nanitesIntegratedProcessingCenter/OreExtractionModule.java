package com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter;

import static bartworks.common.loaders.ItemRegistry.bw_realglas;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gtPlusPlus.core.block.ModBlocks.blockCasings4Misc;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;

public class OreExtractionModule extends NanitesBaseModule<OreExtractionModule> {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String OEM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/ore_extraction_module";
    private static final String[][] shape = StructureUtils.readStructureFromFile(OEM_STRUCTURE_FILE_PATH);

    public OreExtractionModule(String aName) {
        super(aName);
        isOreModule = true;
    }

    public OreExtractionModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        isOreModule = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OreExtractionModule(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 0);
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
    public IStructureDefinition<OreExtractionModule> getStructureDefinition() {
        return StructureDefinition.<OreExtractionModule>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 15))
            .addElement('C', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('E', StructureUtility.ofBlock(BlockLoader.metaCasing, 8))
            .addElement('F', GTStructureUtility.ofFrame(Materials.CosmicNeutronium))
            .addElement(
                'G',
                GTStructureUtility.buildHatchAdder(OreExtractionModule.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 0))))
            .addElement('H', StructureUtility.ofBlock(blockCasings4Misc, 11))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 8))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 12))
            .addElement('M', GTStructureUtility.ofFrame(Materials.Invar))
            .addElement('N', StructureUtility.ofBlock(BlockLoader.metaCasing, 12))
            .addElement('O', StructureUtility.ofBlock(bw_realglas, 14))
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
}
