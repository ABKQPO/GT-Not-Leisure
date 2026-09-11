package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.gui.recipe.RocketAssemblerBackend;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.render.tile.RocketAssemblerRenderer;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.render.IMTERenderer;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;

@IMetaTileEntity.SkipGenerateDescription
public class RocketAssembler extends GTMMultiMachineBase<RocketAssembler>
    implements ISurvivalConstructable, IMTERenderer {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String RA_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/rocket_assembler";
    private static final int HORIZONTAL_OFF_SET = 8;
    private static final int VERTICAL_OFF_SET = 21;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(RA_STRUCTURE_FILE_PATH);

    public boolean enableRender = true;

    public RocketAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public RocketAssembler(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new RocketAssembler(this.mName);
    }

    @Override
    public void onValueUpdate(byte aValue) {
        mMachine = (aValue & 0x01) != 0;
        enableRender = (aValue & 0x02) != 0;
    }

    @Override
    public byte getUpdateData() {
        byte data = 0;
        if (mMachine) data |= 0x01;
        if (enableRender) data |= 0x02;
        return data;
    }

    @Override
    public int getCasingTextureID() {
        return Casings.CleanStainlessSteelMachineCasing.getTextureId();
    }

    @Override
    public IStructureDefinition<RocketAssembler> getStructureDefinition() {
        return StructureDefinition.<RocketAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTNLCasings.StainlessSteelGearBox.asElement())
            .addElement('B', Casings.SolidSteelMachineCasing.asElement())
            .addElement('C', Casings.SteelGearBoxCasing.asElement())
            .addElement('D', Casings.SteelPipeCasing.asElement())
            .addElement('E', Casings.GrateMachineCasing.asElement())
            .addElement(
                'F',
                GTStructureUtility.buildHatchAdder(RocketAssembler.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            Casings.CleanStainlessSteelMachineCasing.asElement())))
            .addElement('G', Casings.HermeticCasing1.asElement())
            .addElement('H', Casings.HermeticCasing3.asElement())
            .addElement('I', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('J', GTStructureUtility.ofFrame(Materials.StainlessSteel))
            .addElement(
                'K',
                StructureUtility.ofChain(
                    StructureUtility.ofBlockAnyMeta(GCBlocks.landingPad, 0),
                    StructureUtility.ofBlockAnyMeta(GCBlocks.landingPadFull, 0),
                    StructureUtility.ofBlockAnyMeta(GCBlocks.fakeBlock),
                    StructureUtility.isAir()))
            .addElement(
                'L',
                StructureUtility.ofChain(
                    StructureUtility.ofBlockAnyMeta(GCBlocks.landingPad, 0),
                    StructureUtility.ofBlockAnyMeta(GCBlocks.landingPadFull, 0),
                    StructureUtility.ofBlockAnyMeta(GCBlocks.fakeBlock),
                    StructureUtility.isAir()))
            .build();
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated();
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
        checkCasingMin(errors, mCountCasing, 1);
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.RocketAssemblerRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @Override
            public @NotNull GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setHeatOC(getHeatOC())
                    .setMachineHeat(getMachineHeat())
                    .setHeatDiscount(getHeatDiscount())
                    .setAmperageOC(getAmperageOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }

            @NotNull
            @Override
            public CalculationResult validateAndCalculateRecipe(@NotNull GTRecipe recipe) {
                if (recipe == RocketAssemblerBackend.notFoundRecipe)
                    return CalculationResult.ofFailure(SimpleCheckRecipeResult.ofFailure("missing_schematic"));
                return super.validateAndCalculateRecipe(recipe);
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.rocket_assembler.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.rocket_assembler.info.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.gtm.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.rocket_assembler.info.1"))
            .addSupportAny()
            .beginStructureBlock(17, 24, 16, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.rocket_assembler.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.rocket_assembler.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.rocket_assembler.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.rocket_assembler.casing"))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.rocket_assembler.casing"))
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        if (!mMachine || !enableRender) return;
        RocketAssemblerRenderer.renderTileEntityAt(this, x, y, z, timeSinceLastTick);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.enableRender = !enableRender;
            GTUtility.sendChatTrans(aPlayer, "gtnl.chat.render." + (this.enableRender ? "enabled" : "disabled"));
        }
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("enableRender", enableRender);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("enableRender")) enableRender = aNBT.getBoolean("enableRender");
    }
}
