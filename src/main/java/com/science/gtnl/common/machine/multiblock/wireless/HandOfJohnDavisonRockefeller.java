package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtnhlanth.common.register.LanthItemList.FOCUS_MANIPULATION_CASING;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class HandOfJohnDavisonRockefeller extends WirelessEnergyMultiMachineBase<HandOfJohnDavisonRockefeller>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String HODR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/hand_of_john_davison_rockefeller";
    private static final String[][] shape = StructureUtils.readStructureFromFile(HODR_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 20;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;

    public int mSpeedCount = 0;

    public HandOfJohnDavisonRockefeller(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public HandOfJohnDavisonRockefeller(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.hand_of_john_davison_rockefeller.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HandOfJohnDavisonRockefeller(this.mName);
    }

    @Override
    public IStructureDefinition<HandOfJohnDavisonRockefeller> getStructureDefinition() {
        return StructureDefinition.<HandOfJohnDavisonRockefeller>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
            .addElement('B', GTNLCasings.NeutroniumPipeCasing.asElement())
            .addElement('C', StructureUtility.ofBlockAnyMeta(FOCUS_MANIPULATION_CASING))
            .addElement(
                'D',
                GTStructureUtility.buildHatchAdder(HandOfJohnDavisonRockefeller.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.PressureContainmentCasing.asElement())))
            .addElement('E', Casings.NeutroniumStabilizationCasing.asElement())
            .addElement('F', Casings.GrateMachineCasing.asElement())
            .addElement('G', Casings.MiningNeutroniumCasing.asElement())
            .addElement('H', GTStructureUtility.ofFrame(Materials.Tungsten))
            .addElement('I', Casings.RuggedBotmiumMachineCasing.asElement())
            .build();
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
        checkCasingMin(errors, mCountCasing, 30);
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mSpeedCount = 0;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        mSpeedCount = mGlassTier + GTUtility.getTier(this.getMaxInputVoltage());
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.chemicalPlantRecipes;
    }

    @Override
    public double getEUtDiscount() {
        double discount = 1.0;
        for (int i = 0; i < mSpeedCount; i++) {
            discount *= 0.95;
        }
        return super.getEUtDiscount() * discount;
    }

    @Override
    public double getDurationModifier() {
        double speedBoost = 1.0;
        for (int i = 0; i < mSpeedCount; i++) {
            speedBoost -= 0.025;
            if (speedBoost < 0.1) {
                speedBoost = 0.1;
                break;
            }
        }
        return super.getDurationModifier() * speedBoost;
    }

    @Override
    public int getCasingTextureID() {
        return Casings.PressureContainmentCasing.getTextureId();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAChemicalPlantActive)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.oMCAChemicalPlant)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.5"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.6"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.7"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.8"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.9"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.common.wireless.tooltip.10"))
            .addSupportAny()
            .beginStructureBlock(41, 9, 9, true)
            .addInputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.casing"))
            .addOutputHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.casing"))
            .addInputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.casing"))
            .addOutputBus(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.casing"))
            .addEnergyHatch(
                "0+",
                StatCollector.translateToLocal("gtnl.machine.hand_of_john_davison_rockefeller.tooltip.casing"))
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

}
