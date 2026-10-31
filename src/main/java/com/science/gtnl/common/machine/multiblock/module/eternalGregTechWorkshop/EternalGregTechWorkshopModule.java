package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_GOD_FORGE_CONTROLLER;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.gui.modularui.multiblock.EternalGregTechWorkshopModuleGui;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import bartworks.common.loaders.ItemRegistry;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;

public abstract class EternalGregTechWorkshopModule extends MultiMachineBase<EternalGregTechWorkshopModule> {

    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String EGTWM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/module";
    public static final String[][] shape = StructureUtils.readStructureFromFile(EGTWM_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final IStructureDefinition<EternalGregTechWorkshopModule> STRUCTURE_DEFINITION = StructureDefinition
        .<EternalGregTechWorkshopModule>builder()
        .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
        .addElement('A', StructureUtility.ofBlock(ItemRegistry.bw_realglas, 6))
        .addElement('B', StructureUtility.ofBlock(Loaders.componentAssemblylineCasing, 12))
        .addElement('C', Casings.DimensionalInjectionCasing.asElement())
        .addElement('D', Casings.DimensionalBridge.asElement())
        .addElement('E', Casings.ExtremeDensitySpaceBendingCasing.asElement())
        .addElement('F', Casings.ParticleBeamGuidancePipeCasing.asElement())
        .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSEMotor, 4))
        .addElement('H', GTStructureUtility.ofFrame(Materials.NaquadahAlloy))
        .addElement('I', Casings.SingularityReinforcedStellarShieldingCasing.asElement())
        .addElement('J', Casings.CelestialMatterGuidanceCasing.asElement())
        .addElement(
            'K',
            StructureUtility.ofChain(
                StructureUtility.isAir(),
                GTStructureUtility.buildHatchAdder(EternalGregTechWorkshopModule.class)
                    .atLeast(
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch)
                    .casingIndex(960)
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            Casings.SingularityReinforcedStellarShieldingCasing.asElement()))))
        .build();

    public UUID ownerUUID;
    public boolean isConnected = false;
    public double mEUtDiscount = 1;
    public double mSpeedBoost = 1;
    @Setter
    @Getter
    public long maxUseEUt = 0;
    @Setter
    @Getter
    public BigInteger powerTally = BigInteger.ZERO;
    @Getter
    @Setter
    public long recipeTally = 0;

    public long EUt = 0;
    public int currentParallel = 0;

    public EternalGregTechWorkshopModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public EternalGregTechWorkshopModule(String aName) {
        super(aName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && isConnected) {
            super.onPostTick(aBaseMetaTileEntity, aTick);
            if (mEfficiency < 0) mEfficiency = 0;
        }
    }

    @Override
    public IStructureDefinition<EternalGregTechWorkshopModule> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
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
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setAvailableVoltage(getMaxUseEUt());
                setAvailableAmperage(1);
                return super.process();
            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                if (!addEUToGlobalEnergyMap(ownerUUID, -calculatedEut * duration)) {
                    return CheckRecipeResultRegistry.insufficientPower(calculatedEut * duration);
                }

                addToPowerTally(
                    BigInteger.valueOf(calculatedEut)
                        .multiply(BigInteger.valueOf(duration)));
                addToRecipeTally(calculatedParallels);

                currentParallel = calculatedParallels;
                EUt = calculatedEut;
                calculatedEut = 0;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setMachineHeat(getHeat())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getCasingTextureID() {
        return 960;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_GOD_FORGE_CONTROLLER)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_GOD_FORGE_CONTROLLER)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public String[] getInfoData() {
        ObjectArrayList<String> str = new ObjectArrayList<>();
        str.add(
            IGregTechDeviceInformation.encode(
                "GT5U.infodata.progress",
                EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(mProgresstime / 20) + EnumChatFormatting.RESET,
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(mMaxProgresstime / 20)
                    + EnumChatFormatting.RESET));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.currently_using",
                EnumChatFormatting.RED + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(EUt) : "0")
                    + EnumChatFormatting.RESET));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.max_parallel",
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(Integer.MAX_VALUE)));
        str.add(
            IGregTechDeviceInformation.encode(
                "GT5U.infodata.parallel.current",
                EnumChatFormatting.YELLOW
                    + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(currentParallel) : "0")));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.capacity.heat",
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(mHeatingCapacity)));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.multiplier.recipe_time",
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(mSpeedBoost)));
        return str.toArray(new String[0]);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new EternalGregTechWorkshopModuleGui(this);
    }

    public boolean isConnectedForGui() {
        return isConnected;
    }

    @Override
    public void saveNBTData(NBTTagCompound NBT) {
        NBT.setBoolean("isConnected", isConnected);
        NBT.setLong("maxUseEUt", maxUseEUt);
        NBT.setLong("recipeTally", recipeTally);
        NBT.setByteArray("powerTally", powerTally.toByteArray());
        super.saveNBTData(NBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound NBT) {
        isConnected = NBT.getBoolean("isConnected");
        maxUseEUt = NBT.getLong("maxUseEUt");
        recipeTally = NBT.getLong("recipeTally");
        powerTally = new BigInteger(NBT.getByteArray("powerTally"));
        super.loadNBTData(NBT);
    }

    @Override
    public double getEUtDiscount() {
        return mEUtDiscount;
    }

    public void setEUtDiscount(double discount) {
        mEUtDiscount = discount;
    }

    public void setHeat(int heat) {
        mHeatingCapacity = heat;
    }

    public int getHeat() {
        return mHeatingCapacity;
    }

    @Override
    public double getDurationModifier() {
        return mSpeedBoost;
    }

    public void setDurationModifier(double boost) {
        mSpeedBoost = boost;
    }

    @Override
    public int getMaxParallelRecipes() {
        return maxParallel;
    }

    public void setMaxParallel(int parallel) {
        maxParallel = parallel;
    }

    public void connect() {
        isConnected = true;
    }

    public void disconnect() {
        isConnected = false;
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

    public void addToPowerTally(BigInteger amount) {
        powerTally = powerTally.add(amount);
    }

    public void addToRecipeTally(long amount) {
        recipeTally += amount;
    }

    public static void queryMilestoneStats(EternalGregTechWorkshopModule module, EternalGregTechWorkshop egtw) {
        egtw.addTotalPowerConsumed(module.powerTally);
        module.setPowerTally(BigInteger.ZERO);
        egtw.addTotalRecipesProcessed(module.recipeTally);
        module.setRecipeTally(0);
    }
}
