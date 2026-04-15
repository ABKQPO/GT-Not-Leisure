package com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

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

public class PolymerTwistingModule extends NanitesBaseModule<PolymerTwistingModule> {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PTM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/polymer_twisting_module";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PTM_STRUCTURE_FILE_PATH);

    public PolymerTwistingModule(String aName) {
        super(aName);
        isPolModule = true;
    }

    public PolymerTwistingModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        isPolModule = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PolymerTwistingModule(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 0);
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
    public IStructureDefinition<PolymerTwistingModule> getStructureDefinition() {
        return StructureDefinition.<PolymerTwistingModule>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureUtility.buildHatchAdder(PolymerTwistingModule.class)
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
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))))
            .addElement('B', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('C', GTStructureUtility.ofFrame(Materials.CosmicNeutronium))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 10))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 12))
            .addElement('G', StructureUtility.ofBlock(sBlockCasingsTT, 0))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockGlass1, 0))
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
