package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.Utils.item.TextHandler.texter;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.EternalSingularity;
import static gregtech.api.enums.Mods.UniversalSingularities;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
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
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.Utils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings10;
import gregtech.common.blocks.BlockCasings9;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import kubatech.loaders.BlockLoader;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.casing.TTCasingsContainer;

public class WhiteNightGenerator extends MultiMachineBase<WhiteNightGenerator> {

    protected boolean wirelessMode = false;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static IStructureDefinition<WhiteNightGenerator> STRUCTURE_DEFINITION = null;
    public static final String WNG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/white_night_generator";
    public static String[][] shape = StructureUtils.readStructureFromFile(WNG_STRUCTURE_FILE_PATH);
    public static final int CASING_INDEX = ((BlockCasings10) GregTechAPI.sBlockCasings10).getTextureIndex(13);
    public int multiTier = 0;
    public String ownerName;
    public UUID ownerUUID;
    public long currentOutputEU = 0;
    public int tCountCasing = 0;

    public WhiteNightGenerator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public WhiteNightGenerator(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new WhiteNightGenerator(this.mName);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("isActive")) {
            currentTip.add(
                EnumChatFormatting.AQUA + texter("Current Generating : ", "Waila.RealArtificialStar.1")
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
        if (tileEntity != null) {
            if (tileEntity.isActive()) {
                tag.setLong("currentOutputEU", currentOutputEU);
            }
        }
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (aBaseMetaTileEntity.isServerSide()) {
            this.ownerName = aBaseMetaTileEntity.getOwnerName();
            this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        mMaxProgresstime = 6000;
        if (wirelessMode) {
            BigInteger eu = BigInteger.valueOf((long) this.currentOutputEU)
                .multiply(Utils.INTEGER_MAX_VALUE);
            if (!addEUToGlobalEnergyMap(ownerUUID, eu)) {
                return CheckRecipeResultRegistry.INTERNAL_ERROR;
            }
        } else {
            addEnergyOutput(this.currentOutputEU * Integer.MAX_VALUE);
        }
        return CheckRecipeResultRegistry.GENERATING;
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

    public int getMultiTier(ItemStack inventory) {
        if (inventory == null) return 0;
        return inventory
            .isItemEqual(GTModHandler.getModItem(UniversalSingularities.ID, "universal.general.singularity", 1, 31)) ? 2
                : inventory.isItemEqual(GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 1, 0)) ? 1
                    : 0;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        wirelessMode = false;
        tCountCasing = 0;
        multiTier = 0;
        currentOutputEU = 0;

        if (aStack != null) {
            if (checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET))
                this.multiTier = getMultiTier(aStack);
        }

        wirelessMode = mDynamoHatches.isEmpty();
        currentOutputEU = 300L * multiTier;
        return multiTier > 0 && tCountCasing > 25;
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

    public final int HORIZONTAL_OFF_SET = 49;
    public final int VERTICAL_OFF_SET = 55;
    public final int DEPTH_OFF_SET = 26;

    @Override
    public IStructureDefinition<WhiteNightGenerator> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<WhiteNightGenerator>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    buildHatchAdder(WhiteNightGenerator.class).atLeast(Dynamo)
                        .dot(1)
                        .casingIndex(CASING_INDEX)
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(GregTechAPI.sBlockCasings10, 13))))
                .addElement('B', ofBlock(GregTechAPI.sBlockCasingsDyson, 1))
                .addElement('C', ofBlock(BlockLoader.defcCasingBlock, 12))
                .addElement('D', ofBlock(TTCasingsContainer.GodforgeCasings, 8))
                .addElement('E', ofBlock(GregTechAPI.sBlockCasings10, 11))
                .addElement('F', ofBlock(GregTechAPI.sBlockCasingsSE, 1))
                .addElement('G', ofFrame(MaterialsUEVplus.SixPhasedCopper))
                .addElement('H', ofBlock(TTCasingsContainer.GodforgeCasings, 8))
                .addElement('I', ofBlock(ModBlocks.blockCasings3Misc, 11))
                .addElement('J', ofBlock(GregTechAPI.sBlockCasings9, 5))
                .addElement('K', ofBlock(Loaders.gravityStabilizationCasing, 0))
                .addElement('L', ofBlock(GregTechAPI.sBlockCasingsDyson, 8))
                .addElement('M', ofBlock(GregTechAPI.sBlockCasings9, 5))
                .addElement('N', ofBlock(GregTechAPI.sBlockCasings10, 14))
                .addElement('O', ofBlock(GregTechAPI.sBlockCasings10, 14))
                .addElement('P', ofBlock(GregTechAPI.sBlockCasings9, 5))
                .addElement('Q', ofFrame(MaterialsKevlar.Kevlar))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_MachineType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_07"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_10"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_11"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_12"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_13"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_14"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_15"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_16"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_17"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_18"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_19"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_20"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_21"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_22"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_23"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_24"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_25"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_26"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_27"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_28"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_29"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_30"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_31"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_32"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_33"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_34"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_35"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_36"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_37"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_38"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_39"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_40"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_41"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_42"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_43"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_44"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_45"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_46"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_47"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_48"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_49"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_50"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WhiteNightGenerator_51"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(99, 84, 48, false)
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_RealArtificialStar_02_01"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
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

    @Override
    public int getCasingTextureID() {
        return ((BlockCasings9) GregTechAPI.sBlockCasings9).getTextureIndex(5);
    }
}
