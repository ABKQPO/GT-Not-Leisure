package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static kubatech.loaders.BlockLoader.defcCasingBlock;

import java.util.List;

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
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.entity.EntityParticleBeam;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.render.tile.KerrNewmanHomogenizerRenderer;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.render.IMTERenderer;
import gtPlusPlus.core.material.MaterialsAlloy;

@IMetaTileEntity.SkipGenerateDescription
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
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
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

    public IStructureDefinition<KerrNewmanHomogenizer> getStructureDefinition() {
        return StructureDefinition.<KerrNewmanHomogenizer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', Casings.AdvancedFusionCoil.asElement())
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
                    .hint(3)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, Casings.PressureContainmentCasing.asElement())))
            .addElement('C', Casings.ExtremeDensitySpaceBendingCasing.asElement())
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
                    .hint(7)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            Casings.AdvancedIridiumPlatedMachineCasing.asElement())))
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
                    .hint(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(defcCasingBlock, 7))))
            .addElement('G', Casings.HastelloyXStructuralBlock.asElement())
            .addElement('H', GTNLCasings.NeutroniumGearbox.asElement())
            .addElement('I', Casings.BackgroundRadiationAbsorbentCasing.asElement())
            .addElement('J', Casings.RadiationProofMachineCasing.asElement())
            .addElement('K', Casings.OsmiumItemPipeCasing.asElement())
            .addElement('L', Casings.PBIPipeCasing.asElement())
            .addElement('M', Casings.StabilizedNaquadahWaterPlantCasing.asElement())
            .addElement('N', StructureUtility.ofBlock(GregTechAPI.sBlockTintedGlass, 1))
            .addElement('O', Casings.ActiveNeutroniumCasing.asElement())
            .addElement('P', GTNLCasings.NeutroniumPipeCasing.asElement())
            .addElement('Q', GTStructureUtility.ofFrame(Materials.Trinium))
            .addElement('R', StructureUtility.ofBlock(Loaders.gravityStabilizationCasing, 0))
            .addElement('S', Casings.FusionMachineCasingMKIV.asElement())
            .addElement('T', Casings.ContainmentFieldGenerator.asElement())
            .addElement('U', Casings.ShieldedAcceleratorCasing.asElement())
            .addElement('V', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsDyson, 1))
            .addElement('W', Casings.HeatResistantTriniumPlatedCasing.asElement())
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
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(x -> ++x.mCountCasing, Casings.BulkProductionFrame.asElement())))
            .addElement('Y', Casings.DimensionalBridge.asElement())
            .addElement(
                'Z',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_N.getFrameBox(1)
                            .getItem())))
            .addElement('0', Casings.ReinforcedSCTurbineCasing.asElement())
            .addElement('1', Casings.DimensionalBridge.asElement())
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new KerrNewmanHomogenizer(this.mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.mixerNonCellRecipes;
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
    public int getCasingTextureID() {
        return Casings.AdvancedIridiumPlatedMachineCasing.getTextureId();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.kerr_newman_homogenizer.recipe_type"))
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
            .beginStructureBlock(83, 36, 83, true)
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.kerr_newman_homogenizer.casing"), 1)
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.kerr_newman_homogenizer.casing"), 1)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.kerr_newman_homogenizer.casing"), 1)
            .addOutputHatch("0+", StatCollector.translateToLocal("gtnl.machine.kerr_newman_homogenizer.casing"), 1)
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.kerr_newman_homogenizer.casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.enableRender = !enableRender;
            GTUtility.sendChatTrans(aPlayer, "gtnl.chat.render." + (this.enableRender ? "enabled" : "disabled"));
        }
        return true;
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

    public double[] rotateOffset(ForgeDirection facing, Rotation rot) {
        double d = 5.5d;
        double x = 0;
        double y = 0;
        double z = 0;

        boolean vertical = facing.offsetY != 0;

        if (!vertical) {
            if (rot == Rotation.NORMAL || rot == Rotation.UPSIDE_DOWN) {
                y = d;
            } else if (facing.offsetX != 0) {
                z = d;
            } else {
                x = d;
            }
        } else if (rot == Rotation.NORMAL || rot == Rotation.UPSIDE_DOWN) {
            z = d;
        } else {
            x = d;
        }

        return new double[] { x, y, z };
    }

    public double getInterpolatedRotation(float partialTicks) {
        double delta = rotation - prevRotation;

        if (delta < -180.0) delta += 360.0;
        if (delta > 180.0) delta -= 360.0;

        return (prevRotation + delta * partialTicks) % 360.0;
    }
}
