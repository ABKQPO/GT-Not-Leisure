package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_CACTUS_WONDER;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_CACTUS_WONDER_ACTIVE;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.StructureError;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class SteamCactusWonder extends SteamMultiMachineBase<SteamCactusWonder> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_MAIN_SURVIVAL = "nei";
    private static final String SCW_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_cactus_wonder";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SCW_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 2;

    public SteamCactusWonder(String aName) {
        super(aName);
    }

    public SteamCactusWonder(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new SteamCactusWonder(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamCactusWonderRecipeType");
    }

    public int currentSteam;
    public ItemStack currentOffer;
    public long fueledAmount = 0;
    public static final ItemStack[] possibleInputs = { GregtechItemList.CactusCharcoal.get(1),
        GregtechItemList.BlockCactusCharcoal.get(1), GregtechItemList.CompressedCactusCharcoal.get(1),
        GregtechItemList.DoubleCompressedCactusCharcoal.get(1), GregtechItemList.TripleCompressedCactusCharcoal.get(1),
        GregtechItemList.QuadrupleCompressedCactusCharcoal.get(1),
        GregtechItemList.QuintupleCompressedCactusCharcoal.get(1), GregtechItemList.CactusCoke.get(1),
        GregtechItemList.BlockCactusCoke.get(1), GregtechItemList.CompressedCactusCoke.get(1),
        GregtechItemList.DoubleCompressedCactusCoke.get(1), GregtechItemList.TripleCompressedCactusCoke.get(1),
        GregtechItemList.QuadrupleCompressedCactusCoke.get(1), GregtechItemList.QuintupleCompressedCactusCoke.get(1) };
    public static final long[] totalValue = { 8_000L, 90_000L, 1_012_500L, 11_390_625L, 128_144_531L, 1_441_625_977L,
        16_218_292_236L, 16_000L, 180_000L, 2_025_000L, 22_781_250L, 256_289_063L, 2_883_251_953L, 32_436_584_473L };
    public static final int[] steamType = { 1, 1, 1, 2, 2, 3, 3, 1, 1, 1, 2, 2, 3, 3 };

    public IStructureDefinition<SteamCactusWonder> getStructureDefinition() {
        return StructureDefinition.<SteamCactusWonder>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addShape(
                STRUCTURE_PIECE_MAIN_SURVIVAL,
                Arrays.stream(transpose(shape))
                    .map(
                        sa -> Arrays.stream(sa)
                            .map(s -> s.replaceAll("E", " "))
                            .toArray(String[]::new))
                    .toArray(String[][]::new))
            .addElement('A', chainAllGlasses())
            .addElement('B', ofBlock(GregTechAPI.sBlockCasings2, 12))
            .addElement(
                'C',
                ofChain(
                    buildHatchAdder(SteamCactusWonder.class)
                        .atLeast(SteamHatchElement.InputBus_Steam, InputBus, OutputHatch)
                        .casingIndex(10)
                        .dot(1)
                        .buildAndChain(),
                    ofBlock(GregTechAPI.sBlockCasings3, 13)))
            .addElement('D', ofFrame(Materials.Steel))
            .addElement('E', ofBlock(Blocks.cactus, 0))
            .addElement('F', ofBlock(Blocks.sand, 0))
            .build();
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings1, 10);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        ITexture[] rTexture;
        if (side == facing) {
            rTexture = new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings1, 10)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_CACTUS_WONDER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_CACTUS_WONDER_ACTIVE)
                    .extFacing()
                    .glow()
                    .build() };
        } else {
            rTexture = new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings1, 10)) };
        }
        return rTexture;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN_SURVIVAL,
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {}

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        startRecipeProcessing();
        if (aBaseMetaTileEntity.isAllowedToWork()) {
            if (aTick % 20 == 0) {
                addFuel();
            }
            outputSteam();
        }
        endRecipeProcessing();
    }

    public void addFuel() {
        ArrayList<ItemStack> storedInputs = getStoredInputs();
        for (ItemStack stack : storedInputs) {
            for (int i = 0; i < 14; i++) {
                if (stack.isItemEqual(possibleInputs[i])) {
                    if (currentOffer == null) {
                        currentOffer = stack;
                        fueledAmount += totalValue[i] * stack.stackSize;
                        currentSteam = steamType[i];
                        this.depleteInput(stack);
                    } else if (stack.isItemEqual(currentOffer)) {
                        fueledAmount += totalValue[i] * stack.stackSize;
                        this.depleteInput(stack);
                    }
                }
            }

        }
    }

    public void outputSteam() {
        if (fueledAmount > 0) {
            if (currentSteam == 1) {
                addOutput(Materials.Steam.getGas((int) Math.min(320000, fueledAmount)));
                fueledAmount -= (int) Math.min(320000, fueledAmount);
            } else if (currentSteam == 2) {
                addOutput(FluidUtils.getSuperHeatedSteam((int) Math.min(640000, fueledAmount)));
                fueledAmount -= (int) Math.min(640000, fueledAmount);
            } else if (currentSteam == 3) {
                addOutput(Materials.DenseSupercriticalSteam.getGas((int) Math.min(2560000, fueledAmount)));
                fueledAmount -= (int) Math.min(2560000, fueledAmount);
            }

            if (fueledAmount <= 0) {
                fueledAmount = 0;
                currentOffer = null;
            }
        }
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);

        screenElements
            .widget(
                new TextWidget()
                    .setStringSupplier(
                        () -> EnumChatFormatting.WHITE + StatCollector.translateToLocal("Tooltip_SteamCactusWonder_06")
                            + EnumChatFormatting.YELLOW
                            + numberFormat.format(fueledAmount))
                    .setTextAlignment((Alignment.CenterLeft)))
            .widget(new FakeSyncWidget.LongSyncer(() -> fueledAmount, val -> fueledAmount = val));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.CactusWonderFakeRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCactusWonder_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCactusWonder_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCactusWonder_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCactusWonder_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCactusWonder_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamCactusWonder_05"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 11, 9, true)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("fuel", fueledAmount);
        aNBT.setInteger("steam", currentSteam);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        fueledAmount = aNBT.getLong("fuel");
        currentSteam = aNBT.getInteger("steam");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_MACERATOR_OP;
    }

}
