package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.GTValues.STEAM_PER_WATER;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.ItemList.Circuit_Integrated;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import gregtech.GTMod;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public abstract class LargeBoiler extends MTEEnhancedMultiBlockBase<LargeBoiler> implements ISurvivalConstructable {

    private boolean firstRun = true;
    private int integratedCircuitConfig = 0;
    private long excessWater = 0;
    private int excessFuel = 0;
    private int excessProjectedEU = 0;
    private int tCountCasing;
    private int mFireboxCasing;
    private static final String STRUCTURE_PIECE_MAIN = "main";

    public LargeBoiler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeBoiler(String aName) {
        super(aName);
    }

    public abstract Block getCasingBlock();

    public abstract byte getCasingMeta();

    public abstract byte getCasingTextureIndex();

    public abstract Block getPipeBlock();

    public abstract byte getPipeMeta();

    public abstract Block getFireboxBlock();

    public abstract byte getFireboxMeta();

    public abstract byte getFireboxTextureIndex();

    public abstract int getEUt();

    abstract int runtimeBoost(int mTime);

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    private void onCasingAdded() {
        tCountCasing++;
    }

    private void onFireboxAdded() {
        mFireboxCasing++;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_BOILER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_BOILER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { BlockIcons.getCasingTextureForId(getCasingTextureIndex()), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_LARGE_BOILER)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_BOILER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { BlockIcons.getCasingTextureForId(getCasingTextureIndex()) };
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.largeBoilerFakeFuels;
    }

    @Override
    protected boolean filtersFluid() {
        return false;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {

        if (Circuit_Integrated.isStackEqual(mInventory[1], true, true)) {
            int circuit_config = mInventory[1].getItemDamage();
            if (circuit_config >= 1 && circuit_config <= 25) {
                this.integratedCircuitConfig = circuit_config;
            }
        } else {
            this.integratedCircuitConfig = 0;
        }

        for (GTRecipe tRecipe : RecipeMaps.dieselFuels.getAllRecipes()) {
            FluidStack tFluid = GTUtility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true);
            if (tFluid != null && tRecipe.mSpecialValue > 1) {
                tFluid.amount = 1000;
                if (depleteInput(tFluid)) {
                    this.mEfficiencyIncrease = 10000;
                    this.mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(tRecipe.mSpecialValue / 2));
                    this.mEUt = adjustEUtForConfig(getEUt());
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }
            }
        }
        for (GTRecipe tRecipe : RecipeMaps.denseLiquidFuels.getAllRecipes()) {
            FluidStack tFluid = GTUtility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true);
            if (tFluid != null) {
                tFluid.amount = 1000;
                if (depleteInput(tFluid)) {
                    this.mEfficiencyIncrease = 10000;
                    this.mMaxProgresstime = adjustBurnTimeForConfig(
                        Math.max(1, runtimeBoost(tRecipe.mSpecialValue * 2)));
                    this.mEUt = adjustEUtForConfig(getEUt());
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }
            }
        }

        ArrayList<ItemStack> tInputList = getStoredInputs();
        if (!tInputList.isEmpty()) {
            for (ItemStack tInput : tInputList) {
                if (tInput != GTOreDictUnificator.get(OrePrefixes.bucket, Materials.Lava, 1)) {
                    if (GTUtility.getFluidForFilledItem(tInput, true) == null
                        && (this.mMaxProgresstime = GTModHandler.getFuelValue(tInput) / 80) > 0) {
                        this.excessFuel += GTModHandler.getFuelValue(tInput) % 80;
                        this.mMaxProgresstime += this.excessFuel / 80;
                        this.excessFuel %= 80;
                        this.mEfficiencyIncrease = 10000;
                        this.mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(this.mMaxProgresstime));
                        this.mEUt = adjustEUtForConfig(getEUt());
                        this.mOutputItems = new ItemStack[] { GTUtility.getContainerItem(tInput, true) };
                        tInput.stackSize -= 1;
                        updateSlots();
                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    }
                }
            }
        }
        this.mMaxProgresstime = 0;
        this.mEUt = 0;
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (this.mEUt > 0) {
            this.mEfficiency = 10000;

            int tGeneratedEU = (int) (this.mEUt * 2L * this.mEfficiency / 10000L);
            if (tGeneratedEU > 0) {

                long amount = (tGeneratedEU + STEAM_PER_WATER) / STEAM_PER_WATER;
                excessWater += amount * STEAM_PER_WATER - tGeneratedEU;
                amount -= excessWater / STEAM_PER_WATER;
                excessWater %= STEAM_PER_WATER;
                startRecipeProcessing();

                if (depleteInput(Materials.Water.getFluid(amount))
                    || depleteInput(GTModHandler.getDistilledWater(amount))) {
                    addOutput(GTModHandler.getSteam(tGeneratedEU));
                } else {
                    GTLog.exp.println("Boiler " + this.mName + " had no Water!");
                    explodeMultiblock();
                }

                endRecipeProcessing();
            }
            return true;
        }
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("excessFuel", excessFuel);
        aNBT.setLong("excessWater", excessWater);
        aNBT.setInteger("excessProjectedEU", excessProjectedEU);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        excessFuel = aNBT.getInteger("excessFuel");
        excessWater = aNBT.getLong("excessWater");
        excessProjectedEU = aNBT.getInteger("excessProjectedEU");
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (mProgresstime > 0 && firstRun) {
            firstRun = false;
            GTMod.achievements.issueAchievement(
                aBaseMetaTileEntity.getWorld()
                    .getPlayerEntityByName(aBaseMetaTileEntity.getOwnerName()),
                "extremepressure");
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public IStructureDefinition<LargeBoiler> getStructureDefinition() {
        return StructureDefinition.<LargeBoiler>builder()
            .addShape(
                STRUCTURE_PIECE_MAIN,
                transpose(
                    new String[][] { { "ccc", "ccc", "ccc" }, { "ccc", "cPc", "ccc" }, { "ccc", "cPc", "ccc" },
                        { "ccc", "cPc", "ccc" }, { "f~f", "fff", "fff" }, }))
            .addElement('P', lazy(t -> ofBlock(t.getPipeBlock(), t.getPipeMeta())))
            .addElement(
                'c',
                lazy(
                    t -> buildHatchAdder(LargeBoiler.class).atLeast(OutputHatch)
                        .casingIndex(t.getCasingTextureIndex())
                        .dot(2)
                        .buildAndChain(
                            onElementPass(LargeBoiler::onCasingAdded, ofBlock(t.getCasingBlock(), t.getCasingMeta())))))
            .addElement(
                'f',
                lazy(
                    t -> buildHatchAdder(LargeBoiler.class).atLeast(InputHatch, InputBus)
                        .casingIndex(t.getFireboxTextureIndex())
                        .dot(1)
                        .buildAndChain(
                            onElementPass(
                                LargeBoiler::onFireboxAdded,
                                ofBlock(t.getFireboxBlock(), t.getFireboxMeta())))))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        mFireboxCasing = 0;
        return checkPiece(STRUCTURE_PIECE_MAIN, 1, 4, 0) && tCountCasing >= 20 && mFireboxCasing >= 3;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    private int adjustEUtForConfig(int rawEUt) {
        int adjustedSteamOutput = rawEUt - 25 * integratedCircuitConfig;
        return Math.max(adjustedSteamOutput, 25);
    }

    private int getCorrectedMaxEfficiency(ItemStack itemStack) {
        return getMaxEfficiency(itemStack) - ((getIdealStatus() - getRepairStatus()) * 1000);
    }

    private int adjustBurnTimeForConfig(int rawBurnTime) {
        // Checks if the fuel is eligible for a super efficiency increase and if so, we want to immediately apply the
        // adjustment!
        // We also want to check that the fuel
        if (mEfficiencyIncrease <= 5000 && mEfficiency < getCorrectedMaxEfficiency(mInventory[1])) {
            return rawBurnTime;
        }
        int adjustedEUt = Math.max(25, getEUt() - 25 * integratedCircuitConfig);
        int adjustedBurnTime = (int) (rawBurnTime * (long) getEUt() / adjustedEUt);
        this.excessProjectedEU += getEUt() * rawBurnTime - adjustedEUt * adjustedBurnTime;
        adjustedBurnTime += this.excessProjectedEU / adjustedEUt;
        this.excessProjectedEU %= adjustedEUt;
        return adjustedBurnTime;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 4, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 1, 4, 0, elementBudget, env, false, true);
    }

    public static class LargeBoilerBronze extends LargeBoiler {

        public LargeBoilerBronze(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeBoilerBronze(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeBoilerBronze(this.mName);
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeBoilerRecipeType"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoilerBronze_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoiler_00"))
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(3, 5, 3, false)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_00"), 1)
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings1;
        }

        @Override
        public byte getCasingMeta() {
            return 10;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 10;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 12;
        }

        @Override
        public Block getFireboxBlock() {
            return GregTechAPI.sBlockCasings3;
        }

        @Override
        public byte getFireboxMeta() {
            return 13;
        }

        @Override
        public byte getFireboxTextureIndex() {
            return 45;
        }

        @Override
        public int getEUt() {
            return 1600;
        }

        @Override
        int runtimeBoost(int mTime) {
            return mTime * 2;
        }

    }

    public static class LargeBoilerSteel extends LargeBoiler {

        public LargeBoilerSteel(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeBoilerSteel(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeBoilerSteel(this.mName);
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeBoilerRecipeType"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoilerSteel_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoiler_00"))
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(3, 5, 3, false)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_00"), 1)
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getCasingMeta() {
            return 0;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 16;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 13;
        }

        @Override
        public Block getFireboxBlock() {
            return GregTechAPI.sBlockCasings3;
        }

        @Override
        public byte getFireboxMeta() {
            return 14;
        }

        @Override
        public byte getFireboxTextureIndex() {
            return 46;
        }

        @Override
        public int getEUt() {
            return 3200;
        }

        @Override
        int runtimeBoost(int mTime) {
            return mTime;
        }

    }

    public static class LargeBoilerTitanium extends LargeBoiler {

        public LargeBoilerTitanium(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeBoilerTitanium(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeBoilerTitanium(this.mName);
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeBoilerRecipeType"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoilerTitanium_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoiler_00"))
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(3, 5, 3, false)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_00"), 1)
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getCasingMeta() {
            return 2;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 50;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 14;
        }

        @Override
        public Block getFireboxBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getFireboxMeta() {
            return 3;
        }

        @Override
        public byte getFireboxTextureIndex() {
            return 51;
        }

        @Override
        public int getEUt() {
            return 6400;
        }

        @Override
        int runtimeBoost(int mTime) {
            return mTime * 4;
        }

    }

    public static class LargeBoilerTungstenSteel extends LargeBoiler {

        public LargeBoilerTungstenSteel(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeBoilerTungstenSteel(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeBoilerTungstenSteel(this.mName);
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeBoilerRecipeType"))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoilerTungstenSteel_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeBoiler_00"))
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(3, 5, 3, false)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_00"), 1)
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeBoiler_Casing_01"), 1)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getCasingMeta() {
            return 0;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 48;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 15;
        }

        @Override
        public Block getFireboxBlock() {
            return GregTechAPI.sBlockCasings3;
        }

        @Override
        public byte getFireboxMeta() {
            return 15;
        }

        @Override
        public byte getFireboxTextureIndex() {
            return 47;
        }

        @Override
        public int getEUt() {
            return 12800;
        }

        @Override
        int runtimeBoost(int mTime) {
            return mTime * 4;
        }
    }

}
