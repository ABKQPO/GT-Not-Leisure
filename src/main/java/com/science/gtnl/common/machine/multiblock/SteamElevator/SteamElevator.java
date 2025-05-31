package com.science.gtnl.common.machine.multiblock.SteamElevator;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.Utils.steam.SteamWirelessNetworkManager.addSteamToGlobalSteamMap;
import static com.science.gtnl.Utils.steam.SteamWirelessNetworkManager.getUserSteam;
import static com.science.gtnl.common.machine.multiMachineClasses.WirelessEnergyMultiMachineBase.ZERO_STRING;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.validMTEList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.multiMachineClasses.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
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
import gregtech.common.blocks.BlockCasings2;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import gtnhintergalactic.gui.IG_UITextures;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import tectech.thing.gui.TecTechUITextures;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class SteamElevator extends SteamMultiMachineBase<SteamElevator> implements ISurvivalConstructable {

    public ArrayList<SteamElevatorModule> mModuleHatches = new ArrayList<>();
    private boolean isLoadedChunk;
    private boolean wirelessMode = false;
    private UUID ownerUUID;
    private String costingEUText = ZERO_STRING;

    protected static Textures.BlockIcons.CustomIcon ScreenOFF;
    protected static Textures.BlockIcons.CustomIcon ScreenON;

    public static final int HORIZONTAL_OFF_SET = 17;
    public static final int VERTICAL_OFF_SET = 39;
    public static final int DEPTH_OFF_SET = 14;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_elevator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SE_STRUCTURE_FILE_PATH);

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenOFF = new Textures.BlockIcons.CustomIcon("iconsets/EM_CONTROLLER");
        ScreenON = new Textures.BlockIcons.CustomIcon("iconsets/EM_CONTROLLER_ACTIVE");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamElevatorRecipeType");
    }

    public SteamElevator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SteamElevator(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamElevator(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamElevator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamElevator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamElevator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamElevator_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamElevator_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(35, 43, 35, false)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SteamElevator_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<SteamElevator> getStructureDefinition() {
        return StructureDefinition.<SteamElevator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                ofChain(
                    buildSteamWirelessInput(SteamElevator.class)
                        .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 25))
                        .dot(1)
                        .build(),
                    buildSteamBigInput(SteamElevator.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 25))
                        .dot(1)
                        .build(),
                    buildSteamInput(SteamElevator.class).casingIndex(GTUtility.getTextureId((byte) 116, (byte) 25))
                        .dot(1)
                        .buildAndChain(BlockLoader.MetaCasing, 25)))
            .addElement('B', ofBlock(BlockLoader.MetaCasing, 31))
            .addElement('C', ofBlock(sBlockCasings1, 10))
            .addElement('D', ofBlock(sBlockCasings2, 0))
            .addElement('E', ofBlock(sBlockCasings3, 14))
            .addElement('F', ofFrame(Materials.Steel))
            .addElement('G', ofBlock(Blocks.brick_block, 0))
            .addElement(
                'H',
                buildHatchAdder(SteamElevator.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        SteamHatchElement.InputBus_Steam,
                        SteamHatchElement.OutputBus_Steam,
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus)
                    .buildAndChain(sBlockCasings2, 0))
            .addElement(
                'I',
                HatchElementBuilder.<SteamElevator>builder()
                    .atLeast(SteamModuleElement.SteamModule)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(sBlockCasings2, 0))
            .addElement('J', ofBlock(Blocks.stonebrick, 0))
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
        mModuleHatches.clear();
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        getCasingTextureID();
        updateHatchTexture();
        return true;
    }

    public boolean addModuleToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof SteamElevatorModule moduleBase) {
            return mModuleHatches.add(moduleBase);
        }
        return false;
    }

    @Override
    public void onRemoval() {
        if (mModuleHatches != null && !mModuleHatches.isEmpty()) {
            for (SteamElevatorModule projectModule : mModuleHatches) {
                projectModule.disconnect();
            }
        }
        super.onRemoval();
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
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
            }

            // Charge modules
            if (getBaseMetaTileEntity().isAllowedToWork()) {
                if (aTick % 20 == 0) {
                    if (!mModuleHatches.isEmpty()) {
                        long perModuleEnergy;
                        if (wirelessMode) {
                            perModuleEnergy = Long.MAX_VALUE;
                        } else {
                            perModuleEnergy = getEUVar() / mModuleHatches.size() * 20;
                        }
                        for (SteamElevatorModule mModule : mModuleHatches) {
                            mModule.connect();
                            if (wirelessMode && getUserSteam(ownerUUID).compareTo(BigInteger.ZERO) > 0) {
                                long used = mModule.increaseStoredEU(perModuleEnergy);
                                costingEUText = GTUtility.formatNumbers(used);
                                addSteamToGlobalSteamMap(ownerUUID, -used);
                            } else {
                                long tAvailableEnergy = getEUVar();
                                if (tAvailableEnergy > 0) {
                                    long used = mModule.increaseStoredEU(Math.min(perModuleEnergy, tAvailableEnergy));
                                    setEUVar(Math.max(0, tAvailableEnergy - used));
                                }
                            }

                        }
                    }
                }
            } else {
                if (!mModuleHatches.isEmpty()) {
                    for (SteamElevatorModule mModule : mModuleHatches) {
                        mModule.disconnect();
                    }
                }
            }
            if (mEfficiency < 0) mEfficiency = 0;
            fixAllIssues();
        }
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        consumeBestAvailableSteam();
        return true;
    }

    public void consumeBestAvailableSteam() {
        long euCanAccept = maxEUStore() - getEUVar();
        if (euCanAccept <= 0) return;

        SteamTypes bestSteam = null;
        int bestAmount = 0;

        for (SteamTypes type : SteamTypes.getSupportedTypes()) {
            int total = getAvailableAmount(type.fluid);
            if (total > bestAmount) {
                bestAmount = total;
                bestSteam = type;
            }
        }

        if (bestSteam == null) return;

        long potentialEU = (long) bestAmount * bestSteam.efficiencyFactor;

        long actualEU = Math.min(potentialEU, euCanAccept);

        int steamToConsume = (int) Math.ceil((double) actualEU / bestSteam.efficiencyFactor);

        int actualDrained = depleteInput(bestSteam.fluid, steamToConsume);
        if (actualDrained > 0) {
            long euGenerated = (long) actualDrained * bestSteam.efficiencyFactor;
            setEUVar(getEUVar() + Math.min(euGenerated, euCanAccept));
        }
    }

    public int getAvailableAmount(Fluid fluid) {
        int total = 0;

        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            FluidStack stack = tHatch.getFluid();
            if (stack != null && stack.getFluid() == fluid) {
                total += stack.amount;
            }
        }

        for (CustomFluidHatch tHatch : validMTEList(mSteamBigInputFluids)) {
            FluidStack stack = tHatch.getFluid();
            if (stack != null && stack.getFluid() == fluid) {
                total += stack.amount;
            }
        }

        for (CustomFluidHatch tHatch : validMTEList(mSteamWirelessInputFluids)) {
            FluidStack stack = tHatch.getFluid();
            if (stack != null && stack.getFluid() == fluid) {
                total += stack.amount;
            }
        }

        return total;
    }

    public int depleteInput(Fluid fluid, int maxDrainAmount) {
        int totalDrained = 0;

        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            if (totalDrained >= maxDrainAmount) break;

            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && tLiquid.getFluid() == fluid) {
                int remaining = maxDrainAmount - totalDrained;
                FluidStack drained = tHatch.drain(remaining, true);
                if (drained != null) totalDrained += drained.amount;
            }
        }

        for (CustomFluidHatch tHatch : validMTEList(mSteamBigInputFluids)) {
            if (totalDrained >= maxDrainAmount) break;

            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && tLiquid.getFluid() == fluid) {
                int remaining = maxDrainAmount - totalDrained;
                FluidStack drained = tHatch.drain(remaining, true);
                if (drained != null) totalDrained += drained.amount;
            }
        }

        for (CustomFluidHatch tHatch : validMTEList(mSteamWirelessInputFluids)) {
            if (totalDrained >= maxDrainAmount) break;

            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && tLiquid.getFluid() == fluid) {
                int remaining = maxDrainAmount - totalDrained;
                FluidStack drained = tHatch.drain(remaining, true);
                if (drained != null) totalDrained += drained.amount;
            }
        }

        return totalDrained;
    }

    public int getChunkX() {
        return getBaseMetaTileEntity().getXCoord() >> 4;
    }

    public int getChunkZ() {
        return getBaseMetaTileEntity().getZCoord() >> 4;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public int getTierRecipes() {
        return 1;
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_WHOOUM;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
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
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                new TTRenderedExtendedFacingTexture(aActive ? ScreenON : ScreenOFF) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    protected int getCasingTextureID() {
        return ((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0);
    }

    @Override
    public boolean doesBindPlayerInventory() {
        return false;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_STEAM_LOGO)
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
        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.BACKGROUND_SCREEN_BLUE_NO_INVENTORY)
                .setPos(4, 4)
                .setSize(190, 171));

        final SlotWidget inventorySlot = new SlotWidget(new BaseSlot(inventoryHandler, 1) {

            @Override
            public int getSlotStackLimit() {
                return getInventoryStackLimit();
            }
        });

        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, inventorySlot);
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements)
                .setPos(0, 7)
                .setSize(190, 165));

        builder.widget(createPowerSwitchButton(builder))
            .widget(createStructureUpdateButton(builder));

        // Teleportation button
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.getContext()
                .isClient()) {
                if (getBaseMetaTileEntity().isAllowedToWork()) {
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
                                0,
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

        addGregTechLogo(builder);
    }

    @Override
    public ButtonWidget createPowerSwitchButton(IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            if (isAllowedToWork()) {
                disableWorking();
            } else {
                enableWorking();
            }
        })
            .setPlayClickSoundResource(
                () -> isAllowedToWork() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                    : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
            .setBackground(() -> {
                if (isAllowedToWork()) {
                    return new IDrawable[] { GTUITextures.BUTTON_STANDARD_PRESSED,
                        GTUITextures.OVERLAY_BUTTON_POWER_SWITCH_ON };
                } else {
                    return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                        GTUITextures.OVERLAY_BUTTON_POWER_SWITCH_OFF };
                }
            })
            .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::isAllowedToWork, val -> {
                if (val) enableWorking();
                else disableWorking();
            }), builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.power_switch"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 172)
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    @Override
    public ButtonWidget createStructureUpdateButton(IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget()
            .setOnClick((clickData, widget) -> { if (getStructureUpdateTime() <= -20) setStructureUpdateTime(1); })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                if (getStructureUpdateTime() > -20) {
                    ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                } else {
                    ret.add(GTUITextures.BUTTON_STANDARD);
                }
                ret.add(GTUITextures.OVERLAY_BUTTON_STRUCTURE_UPDATE);
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(
                new FakeSyncWidget.IntegerSyncer(this::getStructureUpdateTime, this::setStructureUpdateTime),
                builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.structure_update"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 140)
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    public long maxEUStore() {
        return 256000000;
    }

    public int getNumberOfModules() {
        return mModuleHatches != null ? mModuleHatches.size() : 0;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        wirelessMode = aNBT.getBoolean("wirelessMode");
        super.loadNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
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
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentSteamCost")
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

    public enum SteamModuleElement implements IHatchElement<SteamElevator> {

        SteamModule(SteamElevator::addModuleToMachineList, SteamElevatorModule.class) {

            @Override
            public long count(SteamElevator tileEntity) {
                return tileEntity.mModuleHatches.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<SteamElevator> adder;

        @SafeVarargs
        SteamModuleElement(IGTHatchAdder<SteamElevator> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super SteamElevator> adder() {
            return adder;
        }
    }
}
