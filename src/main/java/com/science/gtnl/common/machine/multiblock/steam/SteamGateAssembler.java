package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_STEAM_GATE_ASSEMBLER;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class SteamGateAssembler extends SteamMultiMachineBase<SteamGateAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SGA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_gate_assembler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SGA_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 10;
    private static final int VERTICAL_OFF_SET = 11;
    private static final int DEPTH_OFF_SET = 10;

    public SteamGateAssembler(String aName) {
        super(aName);
    }

    public SteamGateAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.steam_gate_assembler.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamGateAssembler(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_gate_assembler.recipe_type");
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int realBudget = elementBudget >= 200 ? elementBudget : Math.min(200, elementBudget * 5);
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            realBudget,
            env,
            false,
            true);
    }

    @Override
    public IStructureDefinition<SteamGateAssembler> getStructureDefinition() {
        return StructureDefinition.<SteamGateAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamGateAssembler.class)
                        .casingIndex(Casings.BronzePlatedBricks.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamGateAssembler.class).casingIndex(Casings.BronzePlatedBricks.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamGateAssembler.class).casingIndex(Casings.BronzePlatedBricks.getTextureId())
                        .hint(1)
                        .build(),
                    buildHatchAdder(SteamGateAssembler.class).casingIndex(Casings.BronzePlatedBricks.getTextureId())
                        .hint(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.Maintenance)
                        .buildAndChain(
                            StructureUtility
                                .onElementPass(x -> ++x.mCountCasing, Casings.BronzePlatedBricks.asElement()))))
            .addElement(
                'B',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamGateAssembler.class)
                        .casingIndex(Casings.SolidSteelMachineCasing.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamGateAssembler.class)
                        .casingIndex(Casings.SolidSteelMachineCasing.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamGateAssembler.class)
                        .casingIndex(Casings.SolidSteelMachineCasing.getTextureId())
                        .hint(1)
                        .build(),
                    buildHatchAdder(SteamGateAssembler.class)
                        .casingIndex(Casings.SolidSteelMachineCasing.getTextureId())
                        .hint(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.Maintenance)
                        .buildAndChain(
                            StructureUtility
                                .onElementPass(x -> ++x.mCountCasing, Casings.SolidSteelMachineCasing.asElement()))))
            .addElement('C', Casings.BronzeGearBoxCasing.asElement())
            .addElement('D', Casings.SteelGearBoxCasing.asElement())
            .addElement('E', Casings.BronzePipeCasing.asElement())
            .addElement('F', Casings.SteelPipeCasing.asElement())
            .addElement('G', Casings.BronzeFireboxCasing.asElement())
            .addElement('H', Casings.SteelFireboxCasing.asElement())
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            return;
        }
        checkHatch(errors);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.SteamGateAssemblerRecipes;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_STEAM_GATE_ASSEMBLER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_STEAM_GATE_ASSEMBLER)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_gate_assembler.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_gate_assembler.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_gate_assembler.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_gate_assembler.tooltip.3"))
            .beginStructureBlock(21, 20, 21, true)
            .toolTipFinisher();
        return tt;
    }
}
