package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.Utils.TextHandler.texter;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.gtnhintergalactic.block.IGBlocks;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.TextLocalization;
import com.science.gtnl.Utils.TextUtils;
import com.science.gtnl.Utils.Utils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;

import galaxyspace.core.register.GSBlocks;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IWirelessEnergyHatchInformation;
import gregtech.api.metatileentity.implementations.MTEHatch;
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

public class WhiteNightGenerator extends MultiMachineBase<WhiteNightGenerator>
    implements IWirelessEnergyHatchInformation {

    protected boolean wirelessMode = false;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public IStructureDefinition<WhiteNightGenerator> STRUCTURE_DEFINITION = null;
    public static final String WNG_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/white_night_generator";
    public String[][] shape;
    public static final int CASING_INDEX = ((BlockCasings10) GregTechAPI.sBlockCasings10).getTextureIndex(13);
    public int multiTier = 0;
    public String ownerName;
    public UUID ownerUUID;
    public long currentOutputEU = 0;
    public int tCountCasing = 0;

    public WhiteNightGenerator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        this.shape = StructureUtils.readStructureFromFile(WNG_STRUCTURE_FILE_PATH);
    }

    public WhiteNightGenerator(String aName) {
        super(aName);
        this.shape = StructureUtils.readStructureFromFile(WNG_STRUCTURE_FILE_PATH);
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

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
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
            if (checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet))
                this.multiTier = getMultiTier(aStack);
        }

        if (tCountCasing <= 1) {
            updateHatchTexture();
            return false;
        }
        wirelessMode = mDynamoHatches.isEmpty();
        currentOutputEU = 300L * multiTier;
        return multiTier > 0;
    }

    protected void updateHatchTexture() {
        for (MTEHatch h : mDynamoHatches) h.updateTexture(CASING_INDEX);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivialBuildPiece(
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

    public final int horizontalOffSet = 49;
    public final int verticalOffSet = 55;
    public final int depthOffSet = 26;

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
                .addElement('B', ofBlock(GSBlocks.DysonSwarmBlocks, 1))
                .addElement('C', ofBlock(BlockLoader.defcCasingBlock, 12))
                .addElement('D', ofBlock(TTCasingsContainer.GodforgeCasings, 8))
                .addElement('E', ofBlock(GregTechAPI.sBlockCasings10, 11))
                .addElement('F', ofBlock(IGBlocks.SpaceElevatorCasing, 1))
                .addElement('G', ofFrame(MaterialsUEVplus.SixPhasedCopper))
                .addElement('H', ofBlock(TTCasingsContainer.GodforgeCasings, 8))
                .addElement('I', ofBlock(ModBlocks.blockCasings3Misc, 11))
                .addElement('J', ofBlock(GregTechAPI.sBlockCasings9, 5))
                .addElement('K', ofBlock(Loaders.gravityStabilizationCasing, 0))
                .addElement('L', ofBlock(GSBlocks.DysonSwarmBlocks, 8))
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
        tt.addMachineType(TextLocalization.Tooltip_WhiteNightGenerator_MachineType)
            .addInfo(TextLocalization.Tooltip_WhiteNightGenerator_00)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(99, 84, 48, false)
            .addStructureInfo(TextLocalization.Tooltip_RealArtificialStar_02_01)
            .toolTipFinisher(TextUtils.SNL);
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
    protected int getMaxParallelRecipes() {
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

    public int getCasingTextureID() {
        return ((BlockCasings9) GregTechAPI.sBlockCasings9).getTextureIndex(5);
    }
}
