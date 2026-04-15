package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static kubatech.loaders.BlockLoader.defcCasingBlock;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.entity.EntityParticleBeam;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.render.tile.KerrNewmanHomogenizerRenderer;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.StructureError;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.render.IMTERenderer;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtnhlanth.common.register.LanthItemList;

public class KerrNewmanHomogenizer extends WirelessEnergyMultiMachineBase<KerrNewmanHomogenizer>
    implements IMTERenderer {

    private static final int HORIZONTAL_OFF_SET = 41;
    private static final int VERTICAL_OFF_SET = 12;
    private static final int DEPTH_OFF_SET = 7;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String KNH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/kerr_newman_homogenizer";
    private static final String[][] shape = StructureUtils.readStructureFromFile(KNH_STRUCTURE_FILE_PATH);

    public static double ROTATION_SPEED = 1.2;

    public double rotation = 0;
    public double prevRotation = 0;
    public boolean enableRender = true;

    @SideOnly(Side.CLIENT)
    public EntityParticleBeam beamUp, beamDown;

    public KerrNewmanHomogenizer(String aName) {
        super(aName);
    }

    public KerrNewmanHomogenizer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new KerrNewmanHomogenizer(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("KerrNewmanHomogenizerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(83, 36, 83, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        if (!mMachine || !enableRender) return;
        KerrNewmanHomogenizerRenderer.renderTileEntityAt(this, x, y, z, timeSinceLastTick);
    }

    @Override
    public void onValueUpdate(byte aValue) {
        enableRender = (aValue & 0x01) != 0;
        mMachine = (aValue & 0x02) != 0;
    }

    @Override
    public byte getUpdateData() {
        byte data = 0;
        if (enableRender) data |= 0x01;
        if (mMachine) data |= 0x02;
        return data;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        prevRotation = rotation;
        rotation = (rotation + ROTATION_SPEED) % 360d;

        if (aBaseMetaTileEntity.getWorld().isRemote) {
            if (!mMachine || !enableRender) {
                if (beamUp != null) beamUp.setDead();
                if (beamDown != null) beamDown.setDead();
                return;
            }
            double x = aBaseMetaTileEntity.getXCoord() + 0.5;
            double y = aBaseMetaTileEntity.getYCoord() + 0.5;
            double z = aBaseMetaTileEntity.getZCoord() + 0.5;

            ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
            Rotation rot = getExtendedFacing().getRotation();

            double offsetX = 34 * back.offsetX;
            double offsetY = 34 * back.offsetY;
            double offsetZ = 34 * back.offsetZ;

            double[] off = rotateOffset(back, rot);

            double startUpX = x + offsetX + off[0];
            double startUpY = y + offsetY + off[1];
            double startUpZ = z + offsetZ + off[2];

            double startDownX = x + offsetX - off[0];
            double startDownY = y + offsetY - off[1];
            double startDownZ = z + offsetZ - off[2];

            double beamEndX = x + offsetX;
            double beamEndY = y + offsetY;
            double beamEndZ = z + offsetZ;

            beamUp = ScienceNotLeisure.proxy
                .particleBeam(startUpX, startUpY, startUpZ, beamEndX, beamEndY, beamEndZ, 6, beamUp, true);
            if (beamUp != null) {
                beamUp.startX = startUpX;
                beamUp.startY = startUpY;
                beamUp.startZ = startUpZ;
                beamUp.endX = beamEndX;
                beamUp.endY = beamEndY;
                beamUp.endZ = beamEndZ;
            }

            beamDown = ScienceNotLeisure.proxy
                .particleBeam(startDownX, startDownY, startDownZ, beamEndX, beamEndY, beamEndZ, 6, beamDown, true);
            if (beamDown != null) {
                beamDown.startX = startDownX;
                beamDown.startY = startDownY;
                beamDown.startZ = startDownZ;
                beamDown.endX = beamEndX;
                beamDown.endY = beamEndY;
                beamDown.endZ = beamEndZ;
            }
        }
    }

    public double[] rotateOffset(ForgeDirection facing, Rotation rot) {
        double d = 5.5d;
        double x = 0, y = 0, z = 0;

        boolean vertical = facing.offsetY != 0;

        if (!vertical) {
            if (rot == Rotation.NORMAL || rot == Rotation.UPSIDE_DOWN) {
                y = d;
            } else {
                if (facing.offsetX != 0) z = d;
                else x = d;
            }
        } else {
            if (rot == Rotation.NORMAL || rot == Rotation.UPSIDE_DOWN) z = d;
            else x = d;
        }

        return new double[] { x, y, z };
    }

    public double getInterpolatedRotation(float partialTicks) {
        double delta = rotation - prevRotation;

        if (delta < -180.0) delta += 360.0;
        if (delta > 180.0) delta -= 360.0;

        return (prevRotation + delta * partialTicks) % 360.0;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("enableRender", enableRender);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("enableRender")) enableRender = aNBT.getBoolean("enableRender");
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7);
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.enableRender = !enableRender;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal("Info_Render_" + (this.enableRender ? "Enabled" : "Disabled")));
        }
        return true;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<KerrNewmanHomogenizer> getStructureDefinition() {
        return StructureDefinition.<KerrNewmanHomogenizer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(ModBlocks.blockCasings3Misc, 13))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(KerrNewmanHomogenizer.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('D', GTStructureUtility.ofFrame(Materials.TungstenCarbide))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(KerrNewmanHomogenizer.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))))
            .addElement(
                'F',
                GTStructureUtility.buildHatchAdder(KerrNewmanHomogenizer.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(defcCasingBlock, 7))))
            .addElement('G', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 2))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaCasing, 5))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 12))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 12))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasings11, 4))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 0))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 7))
            .addElement('N', StructureUtility.ofBlock(GregTechAPI.sBlockTintedGlass, 1))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('P', StructureUtility.ofBlock(BlockLoader.metaCasing, 4))
            .addElement('Q', GTStructureUtility.ofFrame(Materials.Trinium))
            .addElement('R', StructureUtility.ofBlock(Loaders.gravityStabilizationCasing, 0))
            .addElement('S', StructureUtility.ofBlock(ModBlocks.blockCasings6Misc, 0))
            .addElement('T', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('U', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('V', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 1))
            .addElement('W', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 11))
            .addElement(
                'X',
                GTStructureUtility.buildHatchAdder(KerrNewmanHomogenizer.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 12))))
            .addElement('Y', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement(
                'Z',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_N.getFrameBox(1)
                            .getItem())))
            .addElement('0', StructureUtility.ofBlock(ModBlocks.blockSpecialMultiCasings, 15))
            .addElement('1', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement(
                '2',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_C276.getFrameBox(1)
                            .getItem())))
            .addElement('3', GTStructureUtility.ofFrame(Materials.NaquadahAlloy))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return true;
    }

    @Override
    public void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.mixerNonCellRecipes;
    }

}
