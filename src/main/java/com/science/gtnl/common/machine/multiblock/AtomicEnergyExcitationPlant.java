package com.science.gtnl.common.machine.multiblock;

import static bartworks.system.material.WerkstoffLoader.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.ITierConverter;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.render.tile.AtomicEnergyExcitationPlantRenderer;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.FuelRefiningTierKey;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.render.IMTERenderer;
import kubatech.loaders.BlockLoader;

public class AtomicEnergyExcitationPlant extends GTMMultiMachineBase<AtomicEnergyExcitationPlant>
    implements ISurvivalConstructable, IMTERenderer {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_SPHERE = "sphere";
    private static final String STRUCTURE_PIECE_SPHERE_AIR = "sphere_air";
    private static final String AEEP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/atomic_energy_excitation_plant";
    private static final String AEEPS_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/atomic_energy_excitation_plant_sphere";
    private static final int HORIZONTAL_OFF_SET = 8;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 3;
    private static final int HORIZONTAL_OFF_SET_SPHERE = 4;
    private static final int VERTICAL_OFF_SET_SPHERE = 4;
    private static final int DEPTH_OFF_SET_SPHERE = -7;
    private static final String[][] shape = StructureUtils.readStructureFromFile(AEEP_STRUCTURE_FILE_PATH);
    private static final String[][] shapeSphere = StructureUtils.readStructureFromFile(AEEPS_STRUCTURE_FILE_PATH);
    private static final String[][] shapeSphereAir = StructureUtils.replaceLetters(shapeSphere, "L");

    public static final Block[] coils = new Block[] { Loaders.FRF_Coil_1, Loaders.FRF_Coil_2, Loaders.FRF_Coil_3,
        Loaders.FRF_Coil_4 };

    public int machineTier = -1;

    public boolean enableRender = true;
    public boolean isRenderActive;
    public float rotation = 0;

    public AtomicEnergyExcitationPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public AtomicEnergyExcitationPlant(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new AtomicEnergyExcitationPlant(this.mName);
    }

    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        AtomicEnergyExcitationPlantRenderer.renderTileEntity(this, x, y, z, timeSinceLastTick);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        rotation += 0.5F;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            enableRender = !enableRender;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector
                    .translateToLocal("AtomicEnergyExcitationPlant_Render_" + (enableRender ? "Enabled" : "Disabled")));
            if (mMachine) {
                if (enableRender) {
                    destroySphere();
                } else {
                    buildSphere();
                }
                isRenderActive = enableRender;
            }
        }
        return true;
    }

    public void destroySphere() {
        buildPiece(
            STRUCTURE_PIECE_SPHERE_AIR,
            null,
            false,
            HORIZONTAL_OFF_SET_SPHERE,
            VERTICAL_OFF_SET_SPHERE,
            DEPTH_OFF_SET_SPHERE);
    }

    public void buildSphere() {
        buildPiece(
            STRUCTURE_PIECE_SPHERE,
            null,
            false,
            HORIZONTAL_OFF_SET_SPHERE,
            VERTICAL_OFF_SET_SPHERE,
            DEPTH_OFF_SET_SPHERE);
    }

    public ChunkCoordinates getRenderPos() {
        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();

        int xOffset = 11 * back.offsetX;
        int yOffset = 11 * back.offsetY;
        int zOffset = 11 * back.offsetZ;

        return new ChunkCoordinates(xOffset, yOffset, zOffset);
    }

    public static ITierConverter<Integer> fieldCoilTierConverter() {
        return (block, meta) -> {
            for (int i = 0; i < coils.length; i++) {
                if (block.equals(coils[i])) {
                    return i + 1;
                }
            }
            return null;
        };
    }

    public static List<Pair<Block, Integer>> getAllFieldCoilTiers() {
        ArrayList<Pair<Block, Integer>> tiers = new ArrayList<>();
        for (Block coil : coils) {
            tiers.add(Pair.of(coil, 0));
        }
        return tiers;
    }

    public void setCoilTier(int tier) {
        this.machineTier = tier;
    }

    public int getCoilTier() {
        return this.machineTier;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
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
        return StructureUtils.getTextureIndex(sBlockCasings9, 11);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.FuelRefiningComplexRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("AtomicEnergyExcitationPlantRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(17, 29, 23, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AtomicEnergyExcitationPlant_Casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<AtomicEnergyExcitationPlant> getStructureDefinition() {
        return StructureDefinition.<AtomicEnergyExcitationPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addShape(STRUCTURE_PIECE_SPHERE, transpose(shapeSphere))
            .addShape(STRUCTURE_PIECE_SPHERE_AIR, transpose(shapeSphereAir))
            .addElement('A', chainAllGlasses())
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    ofBlocksTiered(
                        fieldCoilTierConverter(),
                        getAllFieldCoilTiers(),
                        -1,
                        AtomicEnergyExcitationPlant::setCoilTier,
                        AtomicEnergyExcitationPlant::getCoilTier)))
            .addElement('C', ofBlock(BlockLoader.defcCasingBlock, 7))
            .addElement(
                'D',
                GTStructureChannels.HEATING_COIL.use(
                    activeCoils(
                        ofCoil(
                            AtomicEnergyExcitationPlant::setMCoilLevel,
                            AtomicEnergyExcitationPlant::getMCoilLevel))))
            .addElement('E', ofBlock(sBlockCasings10, 7))
            .addElement(
                'F',
                buildHatchAdder(AtomicEnergyExcitationPlant.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        Maintenance,
                        InputBus,
                        InputHatch,
                        OutputHatch,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings9, 11))))
            .addElement('G', ofFrame(Materials.Neutronium))
            .addElement('H', ofBlock(BWBlockCasingsAdvanced, 31895))
            .addElement('I', ofBlock(BWBlockCasings, 31895))
            .addElement('J', ofBlock(sBlockMetal4, 13))
            .addElement('K', ofBlock(sBlockMetal4, 14))
            .addElement('L', isAir())
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        machineTier = -1;
        if (isRenderActive) {
            if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkPiece(
                STRUCTURE_PIECE_SPHERE_AIR,
                HORIZONTAL_OFF_SET_SPHERE,
                VERTICAL_OFF_SET_SPHERE,
                DEPTH_OFF_SET_SPHERE)) {
                buildSphere();
                isRenderActive = false;
                return false;
            }
        } else
            if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkPiece(
                STRUCTURE_PIECE_SPHERE,
                HORIZONTAL_OFF_SET_SPHERE,
                VERTICAL_OFF_SET_SPHERE,
                DEPTH_OFF_SET_SPHERE)) {
                    return false;
                }

        if (mCountCasing < 350 && !checkHatch()) return false;

        setupParameters();

        if (!isRenderActive && enableRender && mTotalRunTime > 0) {
            destroySphere();
            isRenderActive = true;
        }

        return true;
    }

    @Override
    public void onBlockDestroyed() {
        if (isRenderActive && mMachine) {
            buildSphere();
        }
        super.onBlockDestroyed();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("isRenderActive", isRenderActive);
        aNBT.setBoolean("enableRender", enableRender);
        aNBT.setInteger("mTier", machineTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        machineTier = aNBT.getInteger("mTier");
        isRenderActive = aNBT.getBoolean("isRenderActive");
        enableRender = aNBT.getBoolean("enableRender");
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        this.mHeatingCapacity = (int) getMCoilLevel().getHeat();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
        buildPiece(
            STRUCTURE_PIECE_SPHERE,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_SPHERE,
            VERTICAL_OFF_SET_SPHERE,
            DEPTH_OFF_SET_SPHERE);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);

        int built;
        built = survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            realBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        built += survivalBuildPiece(
            STRUCTURE_PIECE_SPHERE,
            stackSize,
            HORIZONTAL_OFF_SET_SPHERE,
            VERTICAL_OFF_SET_SPHERE,
            DEPTH_OFF_SET_SPHERE,
            realBudget,
            env,
            false,
            true);
        return built;
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public boolean getHeatDiscount() {
        return true;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                int recipeReq = recipe.getMetadataOrDefault(FuelRefiningTierKey.INSTANCE, 0);
                if (recipeReq > machineTier) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipeReq);
                }
                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(mHeatingCapacity)
                    .setHeatOC(getHeatOC())
                    .setPerfectOC(getPerfectOC())
                    .setHeatDiscount(getHeatDiscount())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.95, getMCoilLevel().getTier());
    }
}
