package com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

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

public class BioengineeringModule extends NanitesBaseModule<BioengineeringModule> {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String BM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/bioengineering_module";
    private static final String[][] shape = StructureUtils.readStructureFromFile(BM_STRUCTURE_FILE_PATH);

    public BioengineeringModule(String aName) {
        super(aName);
        isBioModule = true;
    }

    public BioengineeringModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        isBioModule = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new BioengineeringModule(this.mName);
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
    public IStructureDefinition<BioengineeringModule> getStructureDefinition() {
        return StructureDefinition.<BioengineeringModule>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(BioengineeringModule.class)
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
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockReinforced, 2))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 13))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 4))
            .addElement('F', GTStructureUtility.ofFrame(Materials.CosmicNeutronium))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 12))
            .addElement('I', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('K', GTStructureUtility.ofFrame(Materials.PulsatingIron))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 1))
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
