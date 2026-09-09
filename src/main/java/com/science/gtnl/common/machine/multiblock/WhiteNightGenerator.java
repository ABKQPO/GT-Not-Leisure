package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings9;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import kubatech.loaders.BlockLoader;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.casing.TTCasingsContainer;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class WhiteNightGenerator extends MultiMachineBase<WhiteNightGenerator> {

    private static final int HORIZONTAL_OFF_SET = 49;
    private static final int VERTICAL_OFF_SET = 55;
    private static final int DEPTH_OFF_SET = 26;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String WNG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/white_night_generator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(WNG_STRUCTURE_FILE_PATH);

    public boolean wirelessMode = false;
    public int multiTier = 0;
    public String ownerName;
    public UUID ownerUUID;
    public long currentOutputEU = 0;

    public WhiteNightGenerator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public WhiteNightGenerator(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.white_night_generator.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new WhiteNightGenerator(this.mName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (aBaseMetaTileEntity.isServerSide()) {
            this.ownerName = aBaseMetaTileEntity.getOwnerName();
            this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        wirelessMode = false;
        multiTier = 0;
        currentOutputEU = 0;
    }

    @Override
    public IStructureDefinition<WhiteNightGenerator> getStructureDefinition() {
        return StructureDefinition.<WhiteNightGenerator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureUtility.buildHatchAdder(WhiteNightGenerator.class)
                    .hint(1)
                    .atLeast(HatchElement.Maintenance, HatchElement.Dynamo)
                    .casingIndex(getCasingTextureID())
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 13))))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 1))
            .addElement('C', StructureUtility.ofBlock(BlockLoader.defcCasingBlock, 12))
            .addElement('D', StructureUtility.ofBlock(TTCasingsContainer.GodforgeCasings, 8))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsSE, 1))
            .addElement('G', GTStructureUtility.ofFrame(Materials.SixPhasedCopper))
            .addElement('H', StructureUtility.ofBlock(TTCasingsContainer.GodforgeCasings, 8))
            .addElement('I', StructureUtility.ofBlock(ModBlocks.blockCasings3Misc, 11))
            .addElement('J', StructureUtility.ofBlock(sBlockCasings9, 5))
            .addElement('K', StructureUtility.ofBlock(Loaders.gravityStabilizationCasing, 0))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 8))
            .addElement('M', StructureUtility.ofBlock(sBlockCasings9, 5))
            .addElement('N', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 14))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 14))
            .addElement('P', StructureUtility.ofBlock(sBlockCasings9, 5))
            .addElement('Q', GTStructureUtility.ofFrame(Materials.Kevlar))
            .build();
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        this.multiTier = getMultiTier();
        setupParameters();
        checkHatch(errors);
        if (multiTier <= 0) {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
            return;
        }
        checkCasingMin(errors, mCountCasing, 26);
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        wirelessMode = mDynamoHatches.isEmpty();
        currentOutputEU = 300L * multiTier;
    }

    @Override
    public void checkHatch(List<StructureError> errors) {
        this.multiTier = getMultiTier();
        super.checkHatch(errors);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        mMaxProgresstime = 6000;
        if (wirelessMode) {
            BigInteger eu = BigInteger.valueOf(this.currentOutputEU)
                .multiply(Utils.INTEGER_MAX_VALUE);
            if (!addEUToGlobalEnergyMap(ownerUUID, eu)) {
                return CheckRecipeResultRegistry.INTERNAL_ERROR;
            }
        } else {
            addEnergyOutput(this.currentOutputEU * Integer.MAX_VALUE);
        }
        return CheckRecipeResultRegistry.GENERATING;
    }

    public int getMultiTier() {
        if (GTUtility.areStacksEqual(
            getControllerSlot(),
            GTModHandler.getModItem(Mods.UniversalSingularities.ID, "universal.general.singularity", 1, 31))) {
            return 2;
        }
        if (GTUtility.areStacksEqual(
            getControllerSlot(),
            GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 1, 0))) {
            return 1;
        }
        return 0;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.white_night_generator.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.4"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.5"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.6"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.7"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.8"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.9"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.10"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.11"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.12"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.13"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.14"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.15"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.16"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.17"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.18"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.19"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.20"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.21"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.22"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.23"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.24"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.25"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.26"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.27"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.28"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.29"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.30"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.31"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.32"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.33"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.34"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.35"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.36"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.37"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.38"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.39"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.40"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.41"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.42"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.43"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.44"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.45"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.46"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.47"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.48"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.49"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.50"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.white_night_generator.tooltip.51"))
            .beginStructureBlock(99, 84, 48, false)
            .addStructureInfo(
                StatCollector.translateToLocal("gtnl.machine.real_artificial_star.structure.output_coefficient"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("isActive")) {
            currentTip.add(
                EnumChatFormatting.AQUA
                    + StatCollector.translateToLocal("gtnl.machine.real_artificial_star.info.current_generation")
                    + EnumChatFormatting.GOLD
                    + tag.getLong("currentOutputEU")
                    + EnumChatFormatting.RED
                    + " * "
                    + "1"
                    + EnumChatFormatting.GREEN
                    + " * 2147483647"
                    + EnumChatFormatting.RESET
                    + " EU / "
                    + "300"
                    + " s");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null && tileEntity.isActive()) {
            tag.setLong("currentOutputEU", currentOutputEU);
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("currentOutputEU", currentOutputEU);
        aNBT.setBoolean("wirelessMode", wirelessMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        currentOutputEU = aNBT.getLong("currentOutputEU");
        wirelessMode = aNBT.getBoolean("wirelessMode");
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings9, 5);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) {
                return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                    TextureFactory.builder()
                        .addIcon(TexturesGtBlock.Overlay_MatterFab_Active)
                        .extFacing()
                        .build() };
            }
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_MatterFab)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }
}
