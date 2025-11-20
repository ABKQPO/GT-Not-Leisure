package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Thaumcraft;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.casing.TTCasingsContainer.*;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

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
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new IndustrialArcaneAssembler(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings10, 3);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == ShapedArcaneCrafting) ? RecipePool.IndustrialShapedArcaneCraftingRecipes
            : RecipePool.IndustrialInfusionCraftingRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays
            .asList(RecipePool.IndustrialShapedArcaneCraftingRecipes, RecipePool.IndustrialInfusionCraftingRecipes);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("IndustrialArcaneAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(19, 19, 19, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<IndustrialArcaneAssembler> getStructureDefinition() {
        return StructureDefinition.<IndustrialArcaneAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings10, 7))
            .addElement('B', ofBlock(sBlockCasingsTT, 0))
            .addElement('C', ofBlock(sBlockCasings1, 13))
            .addElement('D', ofBlock(sBlockCasings8, 7))
            .addElement('E', ofBlock(sBlockCasings10, 6))
            .addElement('F', ofBlock(sBlockCasings10, 8))
            .addElement(
                'G',
                buildHatchAdder(IndustrialArcaneAssembler.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(Maintenance, InputBus, OutputBus, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings10, 3))))
            .addElement('H', ofBlock(sBlockCasingsTT, 4))
            .addElement('I', ofBlock(sBlockCasings9, 12))
            .addElement('J', ofFrame(Materials.Neutronium))
            .addElement('K', ofFrame(Materials.DarkIron))
            .addElement('L', ofBlock(sBlockGlass1, 2))
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
        return mCountCasing >= 25;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && GTUtility.areStacksEqual(
            getControllerSlot(),
            ItemUtils.getItemStack(
                Thaumcraft.ID,
                "WandCasting",
                1,
                9000,
                "{cap:\"matrix\",rod:\"infinity\",aer:999999900,aqua:999999900,ignis:999999900,ordo:999999900,perditio:999999900,terra:999999900}",
                null));
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        setMachineModeIcons();
        builder.widget(createModeSwitchButton(builder));
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("IndustrialArcaneAssembler_Mode_" + machineMode);
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
