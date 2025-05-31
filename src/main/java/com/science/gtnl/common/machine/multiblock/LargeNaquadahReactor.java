package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.NAQUADAH_REACTOR_SOLID_FRONT;
import static gregtech.api.enums.Textures.BlockIcons.NAQUADAH_REACTOR_SOLID_FRONT_ACTIVE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.blockCasings4Misc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;
import static tectech.thing.metaTileEntity.multi.base.TTMultiblockBase.HatchElement.DynamoMulti;

import java.math.BigInteger;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipeRegister;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.blocks.BlockCasings8;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class LargeNaquadahReactor extends TTMultiblockBase implements IConstructable, ISurvivalConstructable {

    private int tCountCasing;
    private boolean Oxygen = false;
    private int multiplier = 1;
    private long setEUt = 0;
    private static IStructureDefinition<LargeNaquadahReactor> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String LNR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_naquadah_reactor";
    public static String[][] shape = StructureUtils.readStructureFromFile(LNR_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 12;
    public final int VERTICAL_OFF_SET = 12;
    public final int DEPTH_OFF_SET = 0;
    protected static final int CASING_INDEX = ((BlockCasings8) sBlockCasings8).getTextureIndex(10);

    public LargeNaquadahReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeNaquadahReactor(String aName) {
        super(aName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeNaquadahReactorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(25, 25, 9, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
            .addDynamoHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeNaquadahReactor> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeNaquadahReactor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BlockLoader.MetaCasing, 4))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 5))
                .addElement(
                    'C',
                    buildHatchAdder(LargeNaquadahReactor.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputHatch, OutputHatch, Dynamo.or(DynamoMulti), Maintenance)
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings8, 10))))
                .addElement('D', ofBlock(sBlockCasingsTT, 0))
                .addElement('E', ofFrame(Materials.Naquadria))
                .addElement('F', ofFrame(Materials.Trinium))
                .addElement('G', ofBlock(blockCasings4Misc, 10))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(NAQUADAH_REACTOR_SOLID_FRONT_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(NAQUADAH_REACTOR_SOLID_FRONT)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.NaquadahReactorRecipes;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing_EM() {
        boolean fuelTierI = false;
        boolean fuelTierII = false;
        boolean hydrogen = false;
        boolean oxygenPlasma = false;
        boolean nitrogenPlasma = false;
        Oxygen = false;
        setEUt = 524288;
        multiplier = 1;

        List<FluidStack> tFluids = getStoredFluids();
        if (tFluids.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        int count = 0;
        for (FluidStack fs : tFluids) {
            if (count >= 6) break;
            Fluid fluid = fs.getFluid();
            switch (fluid.getName()) {
                case "naquadah based liquid fuel mki" -> fuelTierI = true;
                case "naquadah based liquid fuel mkii" -> fuelTierII = true;
                case "hydrogen" -> hydrogen = true;
                case "plasma.oxygen" -> oxygenPlasma = true;
                case "plasma.nitrogen" -> nitrogenPlasma = true;
                case "oxygen" -> Oxygen = true;
            }
            count++;
        }

        if (!fuelTierI && !fuelTierII) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (!(oxygenPlasma ^ nitrogenPlasma ^ hydrogen)) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (mOutputFluids == null) {
            mOutputFluids = new FluidStack[1];
        }

        try {
            if (fuelTierI) {
                if (hydrogen) {
                    int fuelAvailable = getFluidAmount(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(1));
                    int hydroAvailable = getFluidAmount(Materials.Hydrogen.getGas(1));
                    int maxFuelMulti = Math.min(fuelAvailable / 16, 4);
                    int maxHydroMulti = Math.min(hydroAvailable / 80, 4);
                    multiplier = Math.min(maxFuelMulti, maxHydroMulti);

                    if (multiplier < 1) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    // 尝试消耗液体
                    boolean success = drainFluid(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(1), 16 * multiplier)
                        && drainFluid(Materials.Hydrogen.getGas(1), 80 * multiplier);
                    if (!success) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    // 生成耗尽燃料并直接赋值给 mOutputFluids
                    mOutputFluids[0] = GGMaterial.naquadahBasedFuelMkIDepleted.getFluidOrGas(160 * multiplier);
                } else if (oxygenPlasma) {
                    int fuelAvailable = getFluidAmount(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(1));
                    int oxyPlasmaAvailable = getFluidAmount(Materials.Oxygen.getPlasma(1));
                    int maxFuelMulti = Math.min(fuelAvailable / 160, 4);
                    int maxOxyMulti = Math.min(oxyPlasmaAvailable / 40, 4);
                    multiplier = Math.min(maxFuelMulti, maxOxyMulti);

                    if (multiplier < 1) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    // 尝试消耗液体
                    boolean success = drainFluid(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(1), 160 * multiplier)
                        && drainFluid(Materials.Oxygen.getPlasma(1), 40 * multiplier);
                    if (!success) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    // 生成耗尽燃料并直接赋值给 mOutputFluids
                    mOutputFluids[0] = GGMaterial.naquadahBasedFuelMkIDepleted.getFluidOrGas(160 * multiplier);
                }
            } else if (fuelTierII) {
                if (hydrogen) {
                    int fuelAvailable = getFluidAmount(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(1));
                    int hydroAvailable = getFluidAmount(Materials.Hydrogen.getGas(1));
                    int maxFuelMulti = Math.min(fuelAvailable / 16, 4);
                    int maxHydroMulti = Math.min(hydroAvailable / 80, 4);
                    multiplier = Math.min(maxFuelMulti, maxHydroMulti);

                    if (multiplier < 1) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    boolean success = drainFluid(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(1), 16 * multiplier)
                        && drainFluid(Materials.Hydrogen.getGas(1), 80 * multiplier);
                    if (!success) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    mOutputFluids[0] = GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(160 * multiplier);
                } else if (nitrogenPlasma) {
                    int fuelAvailable = getFluidAmount(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(1));
                    int nitroPlasmaAvailable = getFluidAmount(Materials.Nitrogen.getPlasma(1));
                    int maxFuelMulti = Math.min(fuelAvailable / 160, 4);
                    int maxNitroMulti = Math.min(nitroPlasmaAvailable / 40, 4);
                    multiplier = Math.min(maxFuelMulti, maxNitroMulti);

                    if (multiplier < 1) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    boolean success = drainFluid(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(1), 160 * multiplier)
                        && drainFluid(Materials.Nitrogen.getPlasma(1), 40 * multiplier);
                    if (!success) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    mOutputFluids[0] = GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(160 * multiplier);
                }
            }
        } catch (Exception e) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        int baseTime = 0;
        if (fuelTierI) {
            if (hydrogen) baseTime = 875;
            else if (oxygenPlasma) baseTime = 14000;
        } else if (fuelTierII) {
            if (hydrogen) baseTime = 1250;
            else if (nitrogenPlasma) baseTime = 20000;
        }
        this.mMaxProgresstime = baseTime;

        setEUt *= multiplier;
        if (Oxygen) {
            this.mMaxProgresstime /= 16;
            setEUt *= 16;
        }

        this.mEfficiency = 10000;
        this.mProgresstime = 0;
        return CheckRecipeResultRegistry.GENERATING;
    }

    private int getFluidAmount(FluidStack fluidToCheck) {
        if (fluidToCheck == null) return 0;

        int total = 0;
        for (FluidStack storedFluid : getStoredFluids()) {
            if (storedFluid != null && storedFluid.isFluidEqual(fluidToCheck)) {
                total += storedFluid.amount;
            }
        }
        return total;
    }

    private boolean drainFluid(FluidStack fluidToDrain, int amount) {
        if (fluidToDrain == null || amount <= 0) return false;

        int remaining = amount;
        for (FluidStack storedFluid : getStoredFluids()) {
            if (storedFluid != null && storedFluid.isFluidEqual(fluidToDrain)) {
                int drained = Math.min(storedFluid.amount, remaining);
                storedFluid.amount -= drained;
                remaining -= drained;
                if (remaining <= 0) break;
            }
        }
        return remaining <= 0;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if ((this.mProgresstime + 1) % 20 == 0 && this.mProgresstime > 0) {
            startRecipeProcessing();

            boolean success = true;
            if (Oxygen) {
                success = drainFluid(Materials.Oxygen.getGas(1), 2000);
            }

            endRecipeProcessing();

            if (!success) {
                stopMachine(ShutDownReasonRegistry.NONE);
                return false;
            }

            BigInteger euPerSecond = BigInteger.valueOf(setEUt)
                .multiply(BigInteger.valueOf(20));

            for (MTEHatchDynamo eDynamo : super.mDynamoHatches) {
                if (eDynamo == null || !eDynamo.isValid()) continue;

                BigInteger canAccept = BigInteger.valueOf(eDynamo.maxEUStore())
                    .subtract(BigInteger.valueOf(eDynamo.getEUVar()));

                BigInteger actualTransfer = euPerSecond.min(canAccept);

                if (actualTransfer.compareTo(BigInteger.ZERO) > 0) {
                    if (actualTransfer.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                        stopMachine(ShutDownReasonRegistry.STRUCTURE_INCOMPLETE);
                        return false;
                    }
                    long transfer = actualTransfer.longValueExact();

                    eDynamo.setEUVar(eDynamo.getEUVar() + transfer);
                    euPerSecond = euPerSecond.subtract(actualTransfer);
                    if (euPerSecond.compareTo(BigInteger.ZERO) <= 0) break;
                }
            }

            if (euPerSecond.compareTo(BigInteger.ZERO) > 0) {
                for (MTEHatchDynamoMulti eDynamo : eDynamoMulti) {
                    if (eDynamo == null || !eDynamo.isValid()) continue;

                    BigInteger canAccept = BigInteger.valueOf(eDynamo.maxEUStore())
                        .subtract(BigInteger.valueOf(eDynamo.getEUVar()));

                    BigInteger actualTransfer = euPerSecond.min(canAccept);

                    if (actualTransfer.compareTo(BigInteger.ZERO) > 0) {
                        if (actualTransfer.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                            stopMachine(ShutDownReasonRegistry.STRUCTURE_INCOMPLETE);
                            return false;
                        }
                        long transfer = actualTransfer.longValueExact();

                        eDynamo.setEUVar(eDynamo.getEUVar() + transfer);
                        euPerSecond = euPerSecond.subtract(actualTransfer);
                        if (euPerSecond.compareTo(BigInteger.ZERO) <= 0) break;
                    }
                }
            }
            if (euPerSecond.compareTo(BigInteger.ZERO) > 0) {
                stopMachine(ShutDownReasonRegistry.STRUCTURE_INCOMPLETE);
                return false;
            }
        }
        return super.onRunningTick(stack);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("SetEUt")) {
            currentTip.add(
                StatCollector.translateToLocal("LargeNaquadahReactor.Generates.0") + EnumChatFormatting.WHITE
                    + tag.getLong("SetEUt")
                    + " EU/t"
                    + EnumChatFormatting.RESET);
        }
        if (tag.hasKey("Oxygen")) {
            currentTip.add(
                StatCollector.translateToLocal("LargeNaquadahReactor.Generates.1") + EnumChatFormatting.WHITE
                    + tag.getBoolean("Oxygen")
                    + EnumChatFormatting.RESET);
        }
    }

    @Override
    public String[] getInfoData() {
        String[] info = super.getInfoData();
        info[4] = StatCollector.translateToLocal("LargeNaquadahReactor.Generates") + EnumChatFormatting.RED
            + GTUtility.formatNumbers(Math.abs(this.setEUt))
            + EnumChatFormatting.RESET
            + " EU/t";
        return info;
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
            && mMaintenanceHatches.size() == 1
            && tCountCasing >= 110;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeNaquadahReactor(this.mName);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("SetEUt", setEUt);
        aNBT.setBoolean("Oxygen", Oxygen);
        aNBT.setInteger("Multiplier", multiplier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        setEUt = aNBT.getLong("SetEUt");
        Oxygen = aNBT.getBoolean("Oxygen");
        multiplier = aNBT.getInteger("Multiplier");
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
}
