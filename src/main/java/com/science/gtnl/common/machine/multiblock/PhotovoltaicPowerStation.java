package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings4;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public abstract class PhotovoltaicPowerStation extends MultiMachineBase<PhotovoltaicPowerStation>
    implements ISurvivalConstructable {

    protected int tCountCasing;
    protected int fuelConsumption;

    protected abstract long getOutputEUt();

    protected abstract int getCasingTextureIndex();

    protected abstract Block getCasingBlock();

    protected abstract int getCasingMeta();

    protected abstract Block getPhotovoltaicBlock();

    protected abstract int getPhotovoltaicMeta();

    protected static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PPS_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/photovoltaic_power_station";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PPS_STRUCTURE_FILE_PATH);
    protected static final int HORIZONTAL_OFF_SET = 4;
    protected static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 2;

    public PhotovoltaicPowerStation(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PhotovoltaicPowerStation(String aName) {
        super(aName);
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
    @NotNull
    public CheckRecipeResult checkProcessing() {
        for (FluidStack tFluid : getStoredFluids()) {
            if (tFluid.getFluid()
                .getName()
                .equals("ic2distilledwater")) {
                boolean notAirBlocks = false;
                IGregTechTileEntity base = this.getBaseMetaTileEntity();
                int xCoord = base.getXCoord(), yCoord = base.getYCoord(), zCoord = base.getZCoord();
                ForgeDirection front = base.getFrontFacing();

                for (int x = xCoord - (front.offsetX * 2) + 4; x >= xCoord - 4; x--) {
                    for (int z = zCoord + 4 - (front.offsetZ * 6); z >= zCoord - 4 + (front.offsetZ * 8); z--) {
                        if (getBaseMetaTileEntity().getWorld()
                            .getTopSolidOrLiquidBlock(x, z) > yCoord + 5) {
                            notAirBlocks = true;
                            break;
                        }
                    }
                }

                long output = getOutputEUt();
                if (notAirBlocks) output /= 2;
                if (base.getWorld()
                    .isRaining()) output /= 2;

                this.lEUt = output;
                this.mEfficiency = 10000;
                this.mProgresstime = 0;
                this.mMaxProgresstime = (int) (1024 * mConfigSpeedBoost);
                return CheckRecipeResultRegistry.GENERATING;
            }
        }

        this.lEUt = 0;
        this.mEfficiency = 0;
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);

        if (this.mProgresstime % 20 == 0 && mProgresstime != 0) {
            startRecipeProcessing();
            for (FluidStack tFluid : getStoredFluids()) {
                if (tFluid.getFluid()
                    .getName()
                    .equals("ic2distilledwater")) {
                    boolean success = drainFluid(this.mEUt / 4);
                    endRecipeProcessing();
                    if (!success) stopMachine(ShutDownReasonRegistry.NO_REPAIR);
                    return;
                }
            }
            endRecipeProcessing();
            stopMachine(ShutDownReasonRegistry.NO_REPAIR);
        }
    }

    private boolean drainFluid(int amount) {
        int remaining = amount;
        for (FluidStack fs : getStoredFluids()) {
            if (fs.getFluid()
                .getName()
                .equals("ic2distilledwater")) {
                int drained = Math.min(fs.amount, remaining);
                fs.amount -= drained;
                remaining -= drained;
                if (remaining <= 0) break;
            }
        }
        return remaining <= 0;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()
            && mMaintenanceHatches.size() == 1
            && tCountCasing >= 8;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 1;
    }

    @Override
    public String[] getInfoData() {
        return new String[] {
            StatCollector.translateToLocal("GT5U.engine.output") + ": "
                + EnumChatFormatting.RED
                + GTUtility.formatNumbers(mEUt)
                + EnumChatFormatting.RESET
                + " EU/t",
            StatCollector.translateToLocal("GT5U.engine.consumption") + ": "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(fuelConsumption)
                + EnumChatFormatting.RESET
                + " L/t" };
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
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(PhotovoltaicPowerStation.class).casingIndex(getCasingTextureIndex())
                    .dot(1)
                    .atLeast(Maintenance, InputHatch, Dynamo, Maintenance)
                    .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(getCasingBlock(), getCasingMeta()))))
            .addElement('B', ofFrame(Materials.StainlessSteel))
            .addElement('D', ofBlock(getPhotovoltaicBlock(), getPhotovoltaicMeta()))
            .build();
    }

    public static class EnergeticPhotovoltaicPowerStation extends PhotovoltaicPowerStation {

        public EnergeticPhotovoltaicPowerStation(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public EnergeticPhotovoltaicPowerStation(String aName) {
            super(aName);
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("PhotovoltaicPowerStationRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_01"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(9, 5, 7, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_EnergeticPhotovoltaicPowerStation_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_EnergeticPhotovoltaicPowerStation_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_EnergeticPhotovoltaicPowerStation_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        protected Block getCasingBlock() {
            return sBlockCasings2;
        }

        @Override
        protected int getCasingMeta() {
            return 0;
        }

        @Override
        protected Block getPhotovoltaicBlock() {
            return BlockLoader.metaCasing;
        }

        @Override
        protected int getPhotovoltaicMeta() {
            return 9;
        }

        @Override
        protected long getOutputEUt() {
            return TierEU.LV * 32;
        }

        @Override
        protected int getCasingTextureIndex() {
            return StructureUtils.getTextureIndex(sBlockCasings2, 0);
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
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("PhotovoltaicPowerStationRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_01"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(9, 5, 7, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_AdvancedPhotovoltaicPowerStation_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_AdvancedPhotovoltaicPowerStation_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AdvancedPhotovoltaicPowerStation_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        protected Block getCasingBlock() {
            return sBlockCasings4;
        }

        @Override
        protected int getCasingMeta() {
            return 2;
        }

        @Override
        protected Block getPhotovoltaicBlock() {
            return BlockLoader.metaCasing;
        }

        @Override
        protected int getPhotovoltaicMeta() {
            return 10;
        }

        @Override
        protected long getOutputEUt() {
            return TierEU.MV * 32;
        }

        @Override
        protected int getCasingTextureIndex() {
            return StructureUtils.getTextureIndex(sBlockCasings4, 2);
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
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("PhotovoltaicPowerStationRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_PhotovoltaicPowerStation_01"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(9, 5, 7, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_VibrantPhotovoltaicPowerStation_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_VibrantPhotovoltaicPowerStation_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_VibrantPhotovoltaicPowerStation_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        protected Block getCasingBlock() {
            return sBlockCasings4;
        }

        @Override
        protected int getCasingMeta() {
            return 0;
        }

        @Override
        protected Block getPhotovoltaicBlock() {
            return BlockLoader.metaCasing;
        }

        @Override
        protected int getPhotovoltaicMeta() {
            return 11;
        }

        @Override
        protected long getOutputEUt() {
            return TierEU.HV * 32;
        }

        @Override
        protected int getCasingTextureIndex() {
            return StructureUtils.getTextureIndex(sBlockCasings4, 0);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new VibrantPhotovoltaicPowerStation(this.mName);
        }
    }

}
