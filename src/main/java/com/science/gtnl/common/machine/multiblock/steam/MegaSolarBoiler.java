package com.science.gtnl.common.machine.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_MEGA_SOLAR_BOILER;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

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
import com.science.gtnl.loader.BlockLoader;
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
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class MegaSolarBoiler extends SteamMultiMachineBase<MegaSolarBoiler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String MSB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/mega_solar_boiler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(MSB_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 10;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 1;

    public MegaSolarBoiler(String aName) {
        super(aName);
    }

    public MegaSolarBoiler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new MegaSolarBoiler(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("MegaSolarBoilerRecipeType");
    }

    @Override
    public IStructureDefinition<MegaSolarBoiler> getStructureDefinition() {
        return StructureDefinition.<MegaSolarBoiler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', chainAllGlasses())
            .addElement('B', ofBlock(sBlockCasings1, 10))
            .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 0))
            .addElement('D', ofBlock(GregTechAPI.sBlockCasings2, 12))
            .addElement('E', ofBlock(GregTechAPI.sBlockCasings2, 13))
            .addElement('F', ofBlock(BlockLoader.metaBlockColumn, 3))
            .addElement(
                'G',
                ofChain(
                    buildHatchAdder(MegaSolarBoiler.class)
                        .atLeast(SteamHatchElement.InputBus_Steam, InputBus, InputHatch, OutputHatch)
                        .casingIndex(10)
                        .dot(1)
                        .buildAndChain(),
                    ofBlock(sBlockCasings1, 10)))
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
            if (aActive) {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_SOLAR_BOILER)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_SOLAR_BOILER)
                        .extFacing()
                        .glow()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_SOLAR_BOILER)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_FRONT_MEGA_SOLAR_BOILER)
                        .extFacing()
                        .glow()
                        .build() };
            }
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {}

    public String state;

    @Override
    public void runMachine(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.runMachine(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isAllowedToWork()) {
            if (aBaseMetaTileEntity.getWorld()
                .isDaytime() && depleteInputReal(Materials.Water.getFluid(30), true)) {
                addOutput(Materials.Steam.getGas(4800));
                depleteInputReal(Materials.Water.getFluid(30));
                state = "Boiling! :D";
            } else {
                state = "Idle :(";
            }

        }

    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);

        screenElements.widget(
            new TextWidget().setStringSupplier(() -> EnumChatFormatting.GREEN + state)
                .setTextAlignment((Alignment.CenterLeft)))
            .widget(new FakeSyncWidget.StringSyncer(() -> state, val -> state = val));
    }

    public void depleteInputReal(FluidStack aLiquid) {
        depleteInputReal(aLiquid, false);
    }

    public boolean depleteInputReal(FluidStack aLiquid, boolean simulate) {
        if (aLiquid == null) return false;
        for (MTEHatchInput tHatch : validMTEList(mInputHatches)) {
            setHatchRecipeMap(tHatch);
            FluidStack tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, false);
            if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                if (simulate) {
                    return true;
                }
                tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, aLiquid, true);
                return tLiquid != null && tLiquid.amount >= aLiquid.amount;
            }
        }
        return false;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaSolarBoiler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaSolarBoiler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_MegaSolarBoiler_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(21, 5, 7, true)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_MACERATOR_OP;
    }

}
