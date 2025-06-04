package com.science.gtnl.common.machine.multiblock;

import static bartworks.common.loaders.ItemRegistry.bw_realglas;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Thaumcraft;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import tectech.thing.block.BlockQuantumGlass;

public class IndustrialArcaneAssembler extends MultiMachineBase<IndustrialArcaneAssembler>
    implements ISurvivalConstructable {

    protected static final int CASING_INDEX = ((BlockCasings1) sBlockCasings1).getTextureIndex(12);
    private static final int ShapedArcaneCrafting = 0;
    private static final int InfusionCrafting = 1;
    private static IStructureDefinition<IndustrialArcaneAssembler> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LAA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/industrial_arcane_assembler";
    public static String[][] shape = StructureUtils.readStructureFromFile(LAA_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 45;
    public final int VERTICAL_OFF_SET = 84;
    public final int DEPTH_OFF_SET = 45;

    public IndustrialArcaneAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public IndustrialArcaneAssembler(String aName) {
        super(aName);
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public float getSpeedBonus() {
        return 1;
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
        return CASING_INDEX;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return (machineMode == ShapedArcaneCrafting) ? RecipeRegister.IndustrialShapedArcaneCraftingRecipes
            : RecipeRegister.IndustrialInfusionCraftingRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            RecipeRegister.IndustrialShapedArcaneCraftingRecipes,
            RecipeRegister.IndustrialInfusionCraftingRecipes);
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
            .beginStructureBlock(91, 150, 91, true)
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_03"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_04"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_05"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_06"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_07"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_08"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_09"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_IndustrialArcaneAssembler_10"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EnergeticIndustrialArcaneAssembler_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<IndustrialArcaneAssembler> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<IndustrialArcaneAssembler>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    buildHatchAdder(IndustrialArcaneAssembler.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputBus, OutputBus, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings1, 12))))
                .addElement('B', ofBlock(sBlockCasings1, 13))
                .addElement('C', ofBlock(sBlockCasings10, 11))
                .addElement('D', ofBlock(sBlockCasings9, 11))
                .addElement('E', ofBlock(bw_realglas, 15))
                .addElement('F', ofBlock(sBlockGlass1, 2))
                .addElement('G', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement('I', ofBlockAnyMeta(Blocks.beacon))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);

        if (stackSize.stackSize > 1) {
            return this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN,
                stackSize,
                HORIZONTAL_OFF_SET,
                VERTICAL_OFF_SET,
                DEPTH_OFF_SET,
                realBudget,
                env,
                false,
                true);
        } else {
            return -1;
        }
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        ItemStack item = getControllerSlot();

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        return tCountCasing >= 3 && item != null
            && item.isItemEqual(GTModHandler.getModItem(Thaumcraft.ID, "WandCasting", 1, 9000));
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        if (aNBT.hasKey("Mode")) {
            machineMode = aNBT.getBoolean("Mode") ? ShapedArcaneCrafting : InfusionCrafting;
        }
        super.loadNBTData(aNBT);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("mode", machineMode);
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

    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("IndustrialArcaneAssembler_Mode_" + machineMode);
    }
}
