package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public abstract class PhotovoltaicPowerStation extends MultiMachineBase<PhotovoltaicPowerStation>
    implements ISurvivalConstructable {

    public static FluidStack DISTILLED_WATER = GTModHandler.getDistilledWater(1);

    public abstract long getOutputEUt();

    public abstract int getCasingTextureIndex();

    public abstract Block getCasingBlock();

    public abstract int getCasingMeta();

    public abstract Block getPhotovoltaicBlock();

    public abstract int getPhotovoltaicMeta();

    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PPS_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/photovoltaic_power_station";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PPS_STRUCTURE_FILE_PATH);
    public static final int HORIZONTAL_OFF_SET = 4;
    public static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 2;

    public PhotovoltaicPowerStation(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PhotovoltaicPowerStation(String aName) {
        super(aName);
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
    public IStructureDefinition<PhotovoltaicPowerStation> getStructureDefinition() {
        return StructureDefinition.<PhotovoltaicPowerStation>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureUtility.buildHatchAdder(PhotovoltaicPowerStation.class)
                    .casingIndex(getCasingTextureIndex())
                    .hint(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.Dynamo,
                        HatchElement.Maintenance)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(getCasingBlock(), getCasingMeta()))))
            .addElement('B', GTStructureUtility.ofFrame(Materials.StainlessSteel))
            .addElement('D', StructureUtility.ofBlock(getPhotovoltaicBlock(), getPhotovoltaicMeta()))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            return;
        }
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 4);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        for (FluidStack tFluid : getStoredFluids()) {
            if (!GTUtility.areFluidsEqual(tFluid, DISTILLED_WATER)) continue;
            boolean notAirBlocks = false;
            IGregTechTileEntity base = getBaseMetaTileEntity();
            int xCoord = base.getXCoord();
            int yCoord = base.getYCoord();
            int zCoord = base.getZCoord();
            ForgeDirection front = base.getFrontFacing();
            World world = base.getWorld();

            for (int x = xCoord - (front.offsetX * 2) + 4; x >= xCoord - 4; x--) {
                for (int z = zCoord + 4 - (front.offsetZ * 6); z >= zCoord - 4 + (front.offsetZ * 8); z--) {
                    if (world.getTopSolidOrLiquidBlock(x, z) > yCoord + 5) {
                        notAirBlocks = true;
                        break;
                    }
                }
            }

            long output = getOutputEUt();
            if (notAirBlocks) output /= 2;
            if (world.isRaining()) output /= 2;

            lEUt = output;
            mEfficiency = 10000;
            mProgresstime = 0;
            mMaxProgresstime = (int) (1024 * mConfigSpeedBoost);
            return CheckRecipeResultRegistry.GENERATING;
        }

        lEUt = 0;
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if ((mProgresstime + 1) % 20 == 0) {
            startRecipeProcessing();
            if (!depleteInput(GTModHandler.getDistilledWater(lEUt / 4))) {
                stopMachine(ShutDownReasonRegistry.NONE);
                endRecipeProcessing();
                return false;
            }
            endRecipeProcessing();
        }
        return super.onRunningTick(stack);
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(getCasingBlock(), getCasingMeta());
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                TextureFactory.builder()
                    .addIcon(aActive ? TexturesGtBlock.oMCDSolarTowerActive : TexturesGtBlock.oMCDSolarTower)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()) };
    }

    @Override
    public String[] getInfoData() {
        return new String[] {
            StatCollector.translateToLocal("GT5U.engine.output") + ": "
                + EnumChatFormatting.RED
                + NumberFormatUtil.formatNumber(lEUt)
                + EnumChatFormatting.RESET
                + " EU/t",
            StatCollector.translateToLocal("GT5U.engine.consumption") + ": "
                + EnumChatFormatting.YELLOW
                + NumberFormatUtil.formatNumber(lEUt / 4)
                + EnumChatFormatting.RESET
                + " L/t" };
    }

    public static class EnergeticPhotovoltaicPowerStation extends PhotovoltaicPowerStation {

        public EnergeticPhotovoltaicPowerStation(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public EnergeticPhotovoltaicPowerStation(String aName) {
            super(aName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("PhotovoltaicPowerStationRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_01"))
                .beginStructureBlock(9, 5, 7, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_EnergeticPhotovoltaicPowerStation_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_EnergeticPhotovoltaicPowerStation_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_EnergeticPhotovoltaicPowerStation_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public int getCasingMeta() {
            return 0;
        }

        @Override
        public Block getPhotovoltaicBlock() {
            return BlockLoader.metaCasing;
        }

        @Override
        public int getPhotovoltaicMeta() {
            return 9;
        }

        @Override
        public long getOutputEUt() {
            return TierEU.LV * 32;
        }

        @Override
        public int getCasingTextureIndex() {
            return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings2, 0);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new EnergeticPhotovoltaicPowerStation(this.mName);
        }
    }

    public static class AdvancedPhotovoltaicPowerStation extends PhotovoltaicPowerStation {

        public AdvancedPhotovoltaicPowerStation(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public AdvancedPhotovoltaicPowerStation(String aName) {
            super(aName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("PhotovoltaicPowerStationRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_01"))
                .beginStructureBlock(9, 5, 7, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_AdvancedPhotovoltaicPowerStation_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_AdvancedPhotovoltaicPowerStation_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AdvancedPhotovoltaicPowerStation_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public int getCasingMeta() {
            return 2;
        }

        @Override
        public Block getPhotovoltaicBlock() {
            return BlockLoader.metaCasing;
        }

        @Override
        public int getPhotovoltaicMeta() {
            return 10;
        }

        @Override
        public long getOutputEUt() {
            return TierEU.MV * 32;
        }

        @Override
        public int getCasingTextureIndex() {
            return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 2);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new AdvancedPhotovoltaicPowerStation(this.mName);
        }
    }

    public static class VibrantPhotovoltaicPowerStation extends PhotovoltaicPowerStation {

        public VibrantPhotovoltaicPowerStation(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public VibrantPhotovoltaicPowerStation(String aName) {
            super(aName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("PhotovoltaicPowerStationRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_01"))
                .beginStructureBlock(9, 5, 7, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_VibrantPhotovoltaicPowerStation_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_VibrantPhotovoltaicPowerStation_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_VibrantPhotovoltaicPowerStation_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public int getCasingMeta() {
            return 0;
        }

        @Override
        public Block getPhotovoltaicBlock() {
            return BlockLoader.metaCasing;
        }

        @Override
        public int getPhotovoltaicMeta() {
            return 11;
        }

        @Override
        public long getOutputEUt() {
            return TierEU.HV * 32;
        }

        @Override
        public int getCasingTextureIndex() {
            return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 0);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new VibrantPhotovoltaicPowerStation(this.mName);
        }
    }

}
