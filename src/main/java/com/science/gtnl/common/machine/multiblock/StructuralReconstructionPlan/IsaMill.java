package com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.GTPlusPlus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;
import static gtPlusPlus.core.block.ModBlocks.blockCasings5Misc;
import static org.lwjgl.LWJGLUtil.log;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.Utils.item.TextUtils;
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.common.recipe.IsaMillTierKey;
import com.science.gtnl.common.recipe.RecipeRegister;

import bartworks.API.BorosilicateGlass;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gtPlusPlus.core.item.chemistry.general.ItemGenericChemBase;
import gtPlusPlus.core.util.math.MathUtils;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.nbthandlers.MTEHatchMillingBalls;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class IsaMill extends GTMMultiMachineBase<IsaMill> implements ISurvivalConstructable {

    protected static final int CASING_INDEX = TAE.GTPP_INDEX(2);
    private int mCasing;
    public byte glassTier = 0;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<IsaMill> STRUCTURE_DEFINITION = null;
    public static final String IM_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/isa_mill";
    public final int horizontalOffSet = 2;
    public final int verticalOffSet = 3;
    public final int depthOffSet = 0;
    public static String[][] shape;

    private final ArrayList<MTEHatchMillingBalls> mMillingBallBuses = new ArrayList<>();

    public IsaMill(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        shape = StructureUtils.readStructureFromFile(IM_STRUCTURE_FILE_PATH);
    }

    public IsaMill(String aName) {
        super(aName);
        shape = StructureUtils.readStructureFromFile(IM_STRUCTURE_FILE_PATH);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.IsaMillRecipeType)
            .addInfo(TextLocalization.Tooltip_IsaMill_00)
            .addInfo(TextLocalization.Tooltip_IsaMill_01)
            .addInfo(TextLocalization.Tooltip_IsaMill_02)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_02)
            .addInfo(TextLocalization.Tooltip_GTMMultiMachine_03)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(5, 5, 9, true)
            .addInputHatch(TextLocalization.Tooltip_IsaMill_Casing, 1)
            .addInputBus(TextLocalization.Tooltip_IsaMill_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_IsaMill_Casing, 1)
            .addEnergyHatch(TextLocalization.Tooltip_IsaMill_Casing, 1)
            .addMaintenanceHatch(TextLocalization.Tooltip_IsaMill_Casing, 1)
            .toolTipFinisher(TextUtils.SNL + TextUtils.SQY + " §rX " + TextUtils.SRP);
        return tt;
    }

    @Override
    public IStructureDefinition<IsaMill> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<IsaMill>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    withChannel(
                        "glass",
                        BorosilicateGlass.ofBoroGlass(
                            (byte) 0,
                            (byte) 1,
                            Byte.MAX_VALUE,
                            (te, t) -> te.glassTier = t,
                            te -> te.glassTier)))
                .addElement(
                    'B',
                    ofChain(
                        buildHatchAdder(IsaMill.class).adder(IsaMill::addMillingBallsHatch)
                            .hatchClass(MTEHatchMillingBalls.class)
                            .shouldReject(t -> !t.mMillingBallBuses.isEmpty())
                            .casingIndex(CASING_INDEX)
                            .dot(1)
                            .build(),
                        buildHatchAdder(IsaMill.class).atLeast(InputBus, OutputBus, InputHatch, Maintenance, Energy)
                            .casingIndex(CASING_INDEX)
                            .dot(1)
                            .build(),
                        onElementPass(x -> ++x.mCasing, ofBlock(blockCasings5Misc, 0))))
                .addElement('C', ofBlock(blockCasings5Misc, 1))
                .addElement('D', ofBlock(blockCasings5Misc, 2))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasing = 0;
        glassTier = 0;
        ParallelTier = 0;
        mMillingBallBuses.clear();

        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet) && checkHatch()) {
            return false;
        }

        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (glassTier < VoltageIndex.UHV & mEnergyHatch.mTier > glassTier) {
                return false;
            }
        }

        ParallelTier = getParallelTier(aStack);
        return mCasing >= 48 && mMillingBallBuses.size() == 1;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return getMaxEfficiency(aStack) > 0;
    }

    private boolean addMillingBallsHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchMillingBalls) {
                return addToMachineListInternal(mMillingBallBuses, aMetaTileEntity, aBaseCasingIndex);
            }
        }
        return false;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {

        final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchMillingBalls) {
            return addToMachineListInternal(mMillingBallBuses, aMetaTileEntity, aBaseCasingIndex);
        }
        return super.addToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.IsaMillRecipes;
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialMultiMachineActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAIndustrialMultiMachine)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    protected int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (this.mUpdate == 1 || this.mStartUpCheck == 1) {
                this.mMillingBallBuses.clear();
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new IsaMill(this.mName);
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
    public ArrayList<ItemStack> getStoredInputs() {
        ArrayList<ItemStack> tItems = super.getStoredInputs();
        for (MTEHatchMillingBalls tHatch : validMTEList(mMillingBallBuses)) {
            ArrayList<ItemStack> aHatchContent = tHatch.getContentUsageSlots();
            if (!aHatchContent.isEmpty()) {
                tItems.addAll(aHatchContent);
            }
        }
        return tItems;
    }

    public int getMaxBallDurability(ItemStack aStack) {
        return ItemGenericChemBase.getMaxBallDurability(aStack);
    }

    private ItemStack findMillingBall() {
        if (mMillingBallBuses.size() != 1) {
            return null;
        }
        MTEHatchMillingBalls aBus = mMillingBallBuses.get(0);
        if (aBus != null) {
            ArrayList<ItemStack> aAvailableItems = aBus.getContentUsageSlots();
            if (!aAvailableItems.isEmpty()) {
                for (ItemStack aBall : aAvailableItems) {
                    if (aBall != null) {
                        return aBall;
                    }
                }
            }
        }

        return null;
    }

    private void damageMillingBall(ItemStack aStack) {
        if (MathUtils.randFloat(0, 10000000) / 10000000f < (1.2f - (0.2 * 1))) {
            int damage = getMillingBallDamage(aStack) + 1;
            log("damage milling ball " + damage);
            if (damage >= getMaxBallDurability(aStack)) {
                log("consuming milling ball");
                aStack.stackSize -= 1;
            } else {
                setDamage(aStack, damage);
            }
        } else {
            log("not damaging milling ball");
        }
    }

    private int getMillingBallDamage(ItemStack aStack) {
        return ItemGenericChemBase.getMillingBallDamage(aStack);
    }

    private void setDamage(ItemStack aStack, int aAmount) {
        ItemGenericChemBase.setMillingBallDamage(aStack, aAmount);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            final Item MillingBall = GameRegistry.findItem(GTPlusPlus.ID, "item.BasicGenericChemItem");

            ItemStack millingBall;
            final ItemStack AluminaMillingBall = new ItemStack(MillingBall, 1, 7);
            final ItemStack SoapstoneMillingBall = new ItemStack(MillingBall, 1, 8);

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                int recipeReq = recipe.getMetadataOrDefault(IsaMillTierKey.INSTANCE, 0);
                millingBall = findMillingBall();
                if (millingBall != null) {
                    if (recipeReq == 1) {
                        if (millingBall.isItemEqual(AluminaMillingBall)) {
                            return CheckRecipeResultRegistry.SUCCESSFUL;
                        }
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    } else if (recipeReq == 2) {
                        if (millingBall.isItemEqual(SoapstoneMillingBall)) {
                            return CheckRecipeResultRegistry.SUCCESSFUL;
                        }
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }
                    return super.validateRecipe(recipe);
                }
                return SimpleCheckRecipeResult.ofFailure("no_milling_ball");
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                CheckRecipeResult result = super.process();
                if (result.wasSuccessful()) {
                    damageMillingBall(millingBall);
                }
                return result;
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setEUtDiscount(1 - (ParallelTier / 50.0))
                    .setSpeedBoost(1 - (ParallelTier / 200.0));
            }

        }.enablePerfectOverclock()
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
}
