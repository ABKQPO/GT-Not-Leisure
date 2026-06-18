package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.gui.modularui.GTNLMultiBlockBaseGui;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class IndustrialArcaneAssembler extends MultiMachineBase<IndustrialArcaneAssembler>
    implements ISurvivalConstructable {

    public static final int ShapedArcaneCrafting = 0;
    public static final int InfusionCrafting = 1;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LAA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/industrial_arcane_assembler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LAA_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 9;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 3;

    public IndustrialArcaneAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public IndustrialArcaneAssembler(String aName) {
        super(aName);
    }

    @Override
    public IStructureDefinition<IndustrialArcaneAssembler> getStructureDefinition() {
        return StructureDefinition.<IndustrialArcaneAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('B', StructureUtility.ofBlock(sBlockCasingsTT, 0))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 6))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 8))
            .addElement(
                'G',
                GTStructureUtility.buildHatchAdder(IndustrialArcaneAssembler.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))))
            .addElement('H', StructureUtility.ofBlock(sBlockCasingsTT, 4))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 12))
            .addElement('J', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement('K', GTStructureUtility.ofFrame(Materials.DarkIron))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockGlass1, 2))
            .build();
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 25);
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == ShapedArcaneCrafting ? GTNLRecipeMaps.IndustrialShapedArcaneCraftingRecipes
            : GTNLRecipeMaps.IndustrialInfusionCraftingRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTNLRecipeMaps.IndustrialShapedArcaneCraftingRecipes,
            GTNLRecipeMaps.IndustrialInfusionCraftingRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void checkHatch(List<StructureError> errors) {
        super.checkHatch(errors);
        if (!GTUtility.areStacksEqual(
            getControllerSlot(),
            ItemUtils.getItemStack(
                Mods.Thaumcraft.ID,
                "WandCasting",
                1,
                9000,
                "{cap:\"matrix\",rod:\"infinity\",aer:999999900,aqua:999999900,ignis:999999900,ordo:999999900,perditio:999999900,terra:999999900}",
                null))) {
            errors.add(GTNLStructureErrors.invalidHatchConfiguration());
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 3);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("IndustrialArcaneAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_02"))
            .addTecTechHatchInfo()
            .beginStructureBlock(19, 19, 19, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNLMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("IndustrialArcaneAssembler_Mode_" + machineMode);
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        // TODO: Remove this mui1 fallback after the Industrial Arcane Assembler GUI no longer supports mui1 startup
        // paths.
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new IndustrialArcaneAssembler(mName);
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
