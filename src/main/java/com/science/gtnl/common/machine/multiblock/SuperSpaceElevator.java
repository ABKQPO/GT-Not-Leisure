package com.science.gtnl.common.machine.multiblock;

import static bartworks.common.loaders.ItemRegistry.bw_realglas;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineClasses.WirelessEnergyMultiMachineBase.ZERO_STRING;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static gregtech.common.misc.WirelessNetworkManager.getUserEU;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.ItemUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ISecondaryDescribable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GTChunkManager;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import gtnhintergalactic.config.IGConfig;
import gtnhintergalactic.gui.IG_UITextures;
import gtnhintergalactic.tile.TileEntitySpaceElevatorCable;
import gtnhintergalactic.tile.multi.elevator.ElevatorUtil;
import gtnhintergalactic.tile.multi.elevatormodules.TileEntityModuleBase;
import kubatech.loaders.BlockLoader;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import tectech.thing.gui.TecTechUITextures;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class SuperSpaceElevator extends TTMultiblockBase
    implements IConstructable, ISecondaryDescribable, ISurvivalConstructable {

    /** List of project modules in this elevator */
    public ArrayList<TileEntityModuleBase> mProjectModuleHatches = new ArrayList<>();
    /** TE of the cable */
    protected TileEntitySpaceElevatorCable elevatorCable;
    /** Motor tier of the Space Elevator */
    protected int motorTier = 0;
    /** Flag if the chunks of the machine are loaded by it */
    private boolean isLoadedChunk;
    /** Interval in which the modules will be supplied with power in ticks */
    private static final int MODULE_CHARGE_INTERVAL = 20;
    /** Multiplier for the internal EU buffer */
    private static final int INTERNAL_BUFFER_MULTIPLIER = 256;

    public static final int CASING_INDEX_BASE = 32 * 128;
    private static final String STRUCTURE_PIECE_MAIN = "main_base";
    private static final String STRUCTURE_PIECE_EXTENDED = "main_extended";
    private static final int STRUCTURE_PIECE_MAIN_HOR_OFFSET = 32;
    private static final int STRUCTURE_PIECE_MAIN_VERT_OFFSET = 49;
    private static final int STRUCTURE_PIECE_MAIN_DEPTH_OFFSET = 28;
    private static final int STRUCTURE_PIECE_EXTENDED_HOR_OFFSET = 32;
    private static final int STRUCTURE_PIECE_EXTENDED_VERT_OFFSET = -4;
    private static final int STRUCTURE_PIECE_EXTENDED_DEPTH_OFFSET = 28;
    private static final String SSEB_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/super_space_elevator_base";
    private static final String SSEE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/super_space_elevator_extended";
    private static final String[][] shapeBase = StructureUtils.readStructureFromFile(SSEB_STRUCTURE_FILE_PATH);
    private static final String[][] shapeExtended = StructureUtils.readStructureFromFile(SSEE_STRUCTURE_FILE_PATH);

    private int tCountCasing = 0;
    private boolean wirelessMode = false;
    private UUID ownerUUID;
    private String costingEUText = ZERO_STRING;

    public SuperSpaceElevator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        useLongPower = true;
    }

    protected SuperSpaceElevator(String aName) {
        super(aName);
        useLongPower = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperSpaceElevator(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SuperSpaceElevatorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(65, 53, 65, true)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_Casing"))
            .addDynamoHatch(StatCollector.translateToLocal("Tooltip_SuperSpaceElevator_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        motorTier = aNBT.getInteger("motorTier");
        wirelessMode = aNBT.getBoolean("wirelessMode");
        super.loadNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("motorTier", motorTier);
        aNBT.setBoolean("wirelessMode", wirelessMode);
        super.saveNBTData(aNBT);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currentTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("wirelessMode", wirelessMode);
            if (wirelessMode) tag.setString("costingEUText", costingEUText);
        }
    }

    @Override
    public long maxEUStore() {
        return INTERNAL_BUFFER_MULTIPLIER * super.maxEUStore();
    }

    public int getNumberOfModules() {
        return mProjectModuleHatches != null ? mProjectModuleHatches.size() : 0;
    }

    public int getChunkX() {
        return getBaseMetaTileEntity().getXCoord() >> 4;
    }

    public int getChunkZ() {
        return getBaseMetaTileEntity().getZCoord() >> 4;
    }

    @Override
    public IStructureDefinition<? extends TTMultiblockBase> getStructure_EM() {
        return StructureDefinition.<SuperSpaceElevator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeBase))
            .addShape(STRUCTURE_PIECE_EXTENDED, transpose(shapeExtended))
            .addElement('A', ofBlock(com.science.gtnl.loader.BlockLoader.MetaCasing, 18))
            .addElement('B', ofBlock(sBlockCasingsSE, 2))
            .addElement('C', ofBlock(sBlockCasingsTT, 0))
            .addElement(
                'D',
                withChannel(
                    "motor",
                    StructureUtility.ofBlocksTiered(
                        ElevatorUtil.motorTierConverter(),
                        ElevatorUtil.getMotorTiers(),
                        0,
                        SuperSpaceElevator::setMotorTier,
                        SuperSpaceElevator::getMotorTier)))
            .addElement('E', ofFrame(Materials.Neutronium))
            .addElement(
                'F',
                buildHatchAdder(SuperSpaceElevator.class).atLeast(Energy.or(ExoticEnergy), Dynamo)
                    .casingIndex(CASING_INDEX_BASE)
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasingsSE, 0))))
            .addElement('G', ofBlock(sBlockCasingsDyson, 9))
            .addElement('H', ofBlock(sBlockCasingsSE, 1))
            .addElement('I', ofBlock(sBlockCasings1, 12))
            .addElement('J', ofBlock(BlockLoader.defcCasingBlock, 7))
            .addElement('K', ofBlock(bw_realglas, 14))
            .addElement(
                'L',
                buildHatchAdder(SuperSpaceElevator.class)
                    .atLeast(
                        InputBus,
                        InputHatch,
                        OutputHatch,
                        OutputBus,
                        HatchElement.EnergyMulti,
                        HatchElement.DynamoMulti,
                        HatchElement.InputData,
                        HatchElement.OutputData)
                    .casingIndex(CASING_INDEX_BASE)
                    .dot(1)
                    .buildAndChain(sBlockCasingsSE, 0))
            .addElement(
                'M',
                HatchElementBuilder.<SuperSpaceElevator>builder()
                    .atLeast(ProjectModuleElement.ProjectModule)
                    .casingIndex(CASING_INDEX_BASE)
                    .dot(1)
                    .buildAndChain(sBlockCasingsSE, 0))
            .addElement(
                'N',
                ElevatorUtil.ofBlockAdder(SuperSpaceElevator::addCable, GregTechAPI.sSpaceElevatorCable, 0))
            .addElement('O', ofBlock(com.science.gtnl.loader.BlockLoader.MetaBlockGlow, 31))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            STRUCTURE_PIECE_MAIN_HOR_OFFSET,
            STRUCTURE_PIECE_MAIN_VERT_OFFSET,
            STRUCTURE_PIECE_MAIN_DEPTH_OFFSET);

        int tMotorTier = Math.min(stackSize.stackSize, 6);
        if (tMotorTier > 1) {
            for (int i = 0; i < tMotorTier - 1; i++) {
                this.buildPiece(
                    STRUCTURE_PIECE_EXTENDED,
                    stackSize,
                    hintsOnly,
                    STRUCTURE_PIECE_EXTENDED_HOR_OFFSET,
                    STRUCTURE_PIECE_EXTENDED_VERT_OFFSET - i * 6,
                    STRUCTURE_PIECE_EXTENDED_DEPTH_OFFSET);
            }
        }
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;

        int tMotorTier = Math.min(stackSize.stackSize, 6);

        int built = 0;

        built = this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            STRUCTURE_PIECE_MAIN_HOR_OFFSET,
            STRUCTURE_PIECE_MAIN_VERT_OFFSET,
            STRUCTURE_PIECE_MAIN_DEPTH_OFFSET,
            elementBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        if (tMotorTier > 1) {
            for (int i = 0; i < tMotorTier - 1; i++) {
                built = this.survivalBuildPiece(
                    STRUCTURE_PIECE_EXTENDED,
                    stackSize,
                    STRUCTURE_PIECE_EXTENDED_HOR_OFFSET,
                    STRUCTURE_PIECE_EXTENDED_VERT_OFFSET - i * 6,
                    STRUCTURE_PIECE_EXTENDED_DEPTH_OFFSET,
                    elementBudget,
                    env,
                    false,
                    true);

                if (built >= 0) return built;
            }
        }

        return built;
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mProjectModuleHatches.clear();
        elevatorCable = null;
        motorTier = 0;
        wirelessMode = false;

        if (!structureCheck_EM(
            STRUCTURE_PIECE_MAIN,
            STRUCTURE_PIECE_MAIN_HOR_OFFSET,
            STRUCTURE_PIECE_MAIN_VERT_OFFSET,
            STRUCTURE_PIECE_MAIN_DEPTH_OFFSET)) {
            if (elevatorCable != null) {
                elevatorCable.setShouldRender(false);
            }
            return false;
        }

        if (motorTier > 1) {
            int actualExtensionLayers = 0;

            while (actualExtensionLayers < motorTier) {
                if (!structureCheck_EM(
                    STRUCTURE_PIECE_EXTENDED,
                    STRUCTURE_PIECE_EXTENDED_HOR_OFFSET,
                    STRUCTURE_PIECE_EXTENDED_VERT_OFFSET - actualExtensionLayers * 6,
                    STRUCTURE_PIECE_EXTENDED_DEPTH_OFFSET)) {
                    break;
                }

                actualExtensionLayers++;
            }
        }

        if (elevatorCable != null) {
            elevatorCable.setShouldRender(true);
        }

        if (motorTier > 2 && mExoticEnergyHatches.isEmpty() && mEnergyHatches.isEmpty()) wirelessMode = true;

        return tCountCasing > 1000;
    }

    public boolean addProjectModuleToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof TileEntityModuleBase moduleBase) {
            return mProjectModuleHatches.add(moduleBase);
        }
        return false;
    }

    public boolean addCable(Block block, int aBaseCasingIndex, World world, int x, int y, int z) {
        // Check if the cable block is valid and can see the sky
        if (block != GregTechAPI.sSpaceElevatorCable || world == null) {
            return false;
        }
        if (!world.canBlockSeeTheSky(x, y + 1, z)) {
            return false;
        }

        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileEntitySpaceElevatorCable) {
            elevatorCable = (TileEntitySpaceElevatorCable) te;
            return true;
        }

        return false;
    }

    public void setMotorTier(int tier) {
        motorTier = tier;
    }

    public int getMotorTier() {
        return motorTier;
    }

    @Override
    public void onRemoval() {
        if (elevatorCable != null) {
            elevatorCable.setShouldRender(false);
        }
        if (mProjectModuleHatches != null && !mProjectModuleHatches.isEmpty()) {
            for (TileEntityModuleBase projectModule : mProjectModuleHatches) {
                projectModule.disconnect();
            }
        }
        super.onRemoval();
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTick == 1) {
                SpaceProjectManager.checkOrCreateTeam(aBaseMetaTileEntity.getOwnerUuid());
            }
            if (!aBaseMetaTileEntity.isAllowedToWork()) {
                // if machine has stopped, stop chunkloading
                GTChunkManager.releaseTicket((TileEntity) aBaseMetaTileEntity);
                isLoadedChunk = false;
            } else if (!isLoadedChunk) {
                // load a 5x5 area when machine is running
                GTChunkManager.releaseTicket((TileEntity) aBaseMetaTileEntity);
                int offX = aBaseMetaTileEntity.getFrontFacing().offsetX;
                int offZ = aBaseMetaTileEntity.getFrontFacing().offsetZ;
                for (int i = -2; i < 3; i++) {
                    for (int j = -2; j < 3; j++) {
                        GTChunkManager.requestChunkLoad(
                            (TileEntity) aBaseMetaTileEntity,
                            new ChunkCoordIntPair(getChunkX() + offX + i, getChunkZ() + offZ + j));
                    }
                }
                this.isLoadedChunk = true;
            } else {
                if (elevatorCable != null && IGConfig.spaceElevator.isCableRenderingEnabled
                    && elevatorCable.getAnimation() == TileEntitySpaceElevatorCable.ClimberAnimation.NO_ANIMATION
                    && aTick % 2000 == 0) {
                    elevatorCable.startAnimation(TileEntitySpaceElevatorCable.ClimberAnimation.DELIVER_ANIMATION);
                }
            }

            // Charge project modules
            if (getBaseMetaTileEntity().isAllowedToWork()) {
                if (aTick % MODULE_CHARGE_INTERVAL == 0) {
                    if (!mProjectModuleHatches.isEmpty()) {
                        long perModuleEnergy;
                        if (wirelessMode) {
                            perModuleEnergy = Long.MAX_VALUE;
                        } else {
                            perModuleEnergy = getEUVar() / mProjectModuleHatches.size() * MODULE_CHARGE_INTERVAL;
                        }
                        for (TileEntityModuleBase projectModule : mProjectModuleHatches) {
                            if (projectModule.getNeededMotorTier() <= motorTier) {
                                projectModule.connect();
                                if (wirelessMode && getUserEU(ownerUUID).compareTo(BigInteger.ZERO) > 0) {
                                    long used = projectModule.increaseStoredEU(perModuleEnergy);
                                    costingEUText = GTUtility.formatNumbers(used);
                                    addEUToGlobalEnergyMap(ownerUUID, -used);
                                } else {
                                    long tAvailableEnergy = getEUVar();
                                    if (tAvailableEnergy > 0) {
                                        long used = projectModule
                                            .increaseStoredEU(Math.min(perModuleEnergy, tAvailableEnergy));
                                        setEUVar(Math.max(0, tAvailableEnergy - used));
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (!mProjectModuleHatches.isEmpty()) {
                    for (TileEntityModuleBase projectModule : mProjectModuleHatches) {
                        projectModule.disconnect();
                    }
                }
            }
            if (mEfficiency < 0) mEfficiency = 0;
            fixAllIssues();
        }
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_WHOOUM;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        if (getBaseMetaTileEntity().isAllowedToWork()) {
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = 10;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        mEfficiencyIncrease = 0;
        mMaxProgresstime = 0;
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX_BASE),
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX_BASE) };
    }

    @Override
    public boolean doesBindPlayerInventory() {
        return false;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(150, 154));
    }

    @Override
    protected void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        screenElements.setSynced(false)
            .setSpace(0)
            .setPos(10, 7);

        screenElements
            .widget(
                new TextWidget(GTUtility.trans("138", "Incomplete Structure.")).setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(widget -> !mMachine))
            .widget(new FakeSyncWidget.BooleanSyncer(() -> mMachine, val -> mMachine = val));

        screenElements.widget(
            new TextWidget(StatCollector.translateToLocal("gt.blockmachines.multimachine.ig.elevator.gui.ready"))
                .setDefaultColor(COLOR_TEXT_WHITE.get())
                .setEnabled(widget -> mMachine));

        screenElements
            .widget(
                TextWidget
                    .dynamicString(
                        () -> StatCollector.translateToLocal(
                            "gt.blockmachines.multimachine.ig.elevator.gui.numOfModules") + ": " + getNumberOfModules())
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(widget -> getBaseMetaTileEntity().isAllowedToWork()));
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);

        // Teleportation button
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.getContext()
                .isClient()) {
                if (getBaseMetaTileEntity().isAllowedToWork() && motorTier > 0) {
                    EntityPlayer player = widget.getContext()
                        .getPlayer();
                    if (player instanceof EntityPlayerMP playerBase) {
                        final GCPlayerStats stats = GCPlayerStats.get(playerBase);
                        stats.coordsTeleportedFromX = playerBase.posX;
                        stats.coordsTeleportedFromZ = playerBase.posZ;
                        try {
                            WorldUtil.toCelestialSelection(
                                playerBase,
                                stats,
                                250,
                                GuiCelestialSelection.MapMode.TELEPORTATION);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        })
            .setPlayClickSound(false)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(TecTechUITextures.BUTTON_STANDARD_16x16);
                ret.add(IG_UITextures.OVERLAY_BUTTON_PLANET_TELEPORT);
                return ret.toArray(new IDrawable[0]);
            })
            .setPos(174, 156)
            .setSize(16, 16)
            .addTooltip(GCCoreUtil.translate("ig.button.travel"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY));
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    protected ButtonWidget createSafeVoidButton() {
        return null;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    public enum ProjectModuleElement implements IHatchElement<SuperSpaceElevator> {

        ProjectModule(SuperSpaceElevator::addProjectModuleToMachineList, TileEntityModuleBase.class) {

            @Override
            public long count(SuperSpaceElevator tileEntity) {
                return tileEntity.mProjectModuleHatches.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<SuperSpaceElevator> adder;

        @SafeVarargs
        ProjectModuleElement(IGTHatchAdder<SuperSpaceElevator> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super SuperSpaceElevator> adder() {
            return adder;
        }
    }

}
