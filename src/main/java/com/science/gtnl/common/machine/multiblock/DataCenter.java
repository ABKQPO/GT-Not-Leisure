package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings10;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.GregTechAPI.sBlockGlass1;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static tectech.thing.metaTileEntity.multi.base.TTMultiblockBase.HatchElement.EnergyMulti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe.RecipeAssemblyLine;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.WirelessComputationPacket;
import gtnhlanth.common.register.LanthItemList;
import tectech.mechanics.dataTransport.ALRecipeDataPacket;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.casing.TTCasingsContainer;
import tectech.thing.metaTileEntity.hatch.MTEHatchDataItemsInput;
import tectech.thing.metaTileEntity.hatch.MTEHatchDataItemsOutput;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessDataItemsOutput;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class DataCenter extends TTMultiblockBase implements ISurvivalConstructable {

    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 7;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String DC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/data_center";
    private static final String[][] shape = StructureUtils.readStructureFromFile(DC_STRUCTURE_FILE_PATH);

    public ArrayList<MTEHatchDataItemsOutput> mStacksDataOutputs = new ArrayList<>();
    public ArrayList<MTEHatchWirelessDataItemsOutput> mWirelessStacksDataOutputs = new ArrayList<>();
    public ArrayList<MTEHatchDataAccess> mDataAccessHatches = new ArrayList<>();
    public boolean slave = false;
    public boolean wirelessModeEnabled = false;

    public DataCenter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        useLongPower = true;
    }

    public DataCenter(String aName) {
        super(aName);
        useLongPower = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DataCenter(mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("DataCenterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_DataCenter_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_DataCenter_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_DataCenter_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_DataCenter_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(15, 9, 15, true)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_DataCenter_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_DataCenter_Casing"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("gt.blockmachines.hatch.dataoutass.tier.07.name"),
                StatCollector.translateToLocal("tt.keyword.Structure.AnyComputerCasing"),
                1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("tt.keyword.Structure.DataAccessHatch"),
                StatCollector.translateToLocal("tt.keyword.Structure.AnyHighPowerCasing"),
                2)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        mDataAccessHatches.clear();
        mStacksDataOutputs.clear();
        mWirelessStacksDataOutputs.clear();
        slave = false;
        return structureCheck_EM(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing_EM() {
        if (!mDataAccessHatches.isEmpty() && (!mStacksDataOutputs.isEmpty() || !mWirelessStacksDataOutputs.isEmpty())) {
            lEUt = -(30720L * (mStacksDataOutputs.size() + mWirelessStacksDataOutputs.size())
                + 1024L * mDataAccessHatches.size());
            mMaxProgresstime = 20;
            mEfficiencyIncrease = 10000;
            return SimpleCheckRecipeResult.ofSuccess("providing_data");
        }
        return SimpleCheckRecipeResult.ofFailure("no_data");
    }

    @Override
    public void outputAfterRecipe_EM() {
        HashSet<RecipeAssemblyLine> availableRecipes = new HashSet<>();

        for (MTEHatchDataAccess dataAccess : GTUtility.validMTEList(mDataAccessHatches)) {
            availableRecipes.addAll(dataAccess.getAssemblyLineRecipes());
        }

        if (!availableRecipes.isEmpty()) {
            RecipeAssemblyLine[] recipeArray = availableRecipes.toArray(new RecipeAssemblyLine[0]);

            for (MTEHatchDataItemsOutput hatch : GTUtility.validMTEList(mStacksDataOutputs)) {
                hatch.q = new ALRecipeDataPacket(recipeArray);
            }

            if (wirelessModeEnabled) {
                for (MTEHatchWirelessDataItemsOutput hatch : GTUtility.validMTEList(mWirelessStacksDataOutputs)) {
                    hatch.dataPacket = new ALRecipeDataPacket(recipeArray);
                }
            }
        } else {
            for (MTEHatchDataItemsOutput hatch : GTUtility.validMTEList(mStacksDataOutputs)) {
                hatch.q = null;
            }

            for (MTEHatchWirelessDataItemsOutput hatch : GTUtility.validMTEList(mWirelessStacksDataOutputs)) {
                hatch.dataPacket = null;
            }
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_HIGH_FREQ;
    }

    public boolean addDataBankHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }

        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }

        if (aMetaTileEntity instanceof MTEHatchWirelessDataItemsOutput hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            return mWirelessStacksDataOutputs.add(hatch);
        }

        if (aMetaTileEntity instanceof MTEHatchDataItemsOutput hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            return mStacksDataOutputs.add(hatch);
        }

        if (aMetaTileEntity instanceof MTEHatchDataAccess hatch
            && !(aMetaTileEntity instanceof MTEHatchDataItemsInput)) {
            hatch.updateTexture(aBaseCasingIndex);
            return mDataAccessHatches.add(hatch);
        }

        if (aMetaTileEntity instanceof MTEHatchDataItemsInput hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            slave = true;
            return mDataAccessHatches.add(hatch);
        }

        return false;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            wirelessModeEnabled = !wirelessModeEnabled;
            if (wirelessModeEnabled) {
                GTUtility.sendChatToPlayer(aPlayer, "Wireless mode enabled");
                WirelessComputationPacket.enableWirelessNetWork(getBaseMetaTileEntity());
            } else {
                GTUtility.sendChatToPlayer(aPlayer, "Wireless mode disabled");
                WirelessComputationPacket.disableWirelessNetWork(getBaseMetaTileEntity());
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("wirelessModeEnabled", wirelessModeEnabled);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("wirelessModeEnabled")) {
            wirelessModeEnabled = aNBT.getBoolean("wirelessModeEnabled");
        } else {
            wirelessModeEnabled = false;
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM(
            STRUCTURE_PIECE_MAIN,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            stackSize,
            hintsOnly);
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
    public IStructureDefinition<DataCenter> getStructure_EM() {
        return StructureDefinition.<DataCenter>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(DataCenter.class).atLeast(DataBankHatches.DataStick)
                    .casingIndex(getCasingTextureID())
                    .dot(2)
                    .buildAndChain(TTCasingsContainer.sBlockCasingsTT, 0))
            .addElement('B', ofBlock(TTCasingsContainer.sBlockCasingsTT, 3))
            .addElement(
                'C',
                buildHatchAdder(DataCenter.class)
                    .atLeast(
                        Maintenance,
                        Energy,
                        EnergyMulti,
                        Dynamo.or(ExoticEnergy),
                        DataBankHatches.OutboundConnector,
                        DataBankHatches.InboundConnector,
                        DataBankHatches.WirelessOutboundConnector)
                    .casingIndex(getCasingTextureID() + 1)
                    .dot(1)
                    .buildAndChain(TTCasingsContainer.sBlockCasingsTT, 1))
            .addElement('D', ofBlock(sBlockCasings8, 7))
            .addElement('E', ofBlock(sBlockCasings10, 9))
            .addElement('F', ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement('G', ofBlock(TTCasingsContainer.sBlockCasingsTT, 4))
            .addElement('H', ofBlock(TTCasingsContainer.sBlockCasingsTT, 2))
            .addElement('I', ofBlock(BlockLoader.metaCasing, 4))
            .addElement('J', ofBlock(sBlockGlass1, 1))
            .addElement('K', ofFrame(Materials.Naquadria))
            .build();
    }

    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset;
    }

    public enum DataBankHatches implements IHatchElement<DataCenter> {

        DataStick(MTEHatchDataAccess.class) {

            @Override
            public long count(DataCenter t) {
                return t.mDataAccessHatches.size();
            }
        },
        OutboundConnector(MTEHatchDataItemsOutput.class) {

            @Override
            public long count(DataCenter t) {
                return t.mStacksDataOutputs.size();
            }
        },
        InboundConnector(MTEHatchDataItemsInput.class) {

            @Override
            public long count(DataCenter t) {
                return t.mDataAccessHatches.size();
            }
        },
        WirelessOutboundConnector(MTEHatchWirelessDataItemsOutput.class) {

            @Override
            public long count(DataCenter t) {
                return t.mWirelessStacksDataOutputs.size();
            }
        };

        private final List<? extends Class<? extends IMetaTileEntity>> mteClasses;

        @SafeVarargs
        DataBankHatches(Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public IGTHatchAdder<? super DataCenter> adder() {
            return DataCenter::addDataBankHatchToMachineList;
        }
    }

    @Override
    public boolean isSafeVoidButtonEnabled() {
        return false;
    }
}
