package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnllib.utils.MathUtils;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.gui.modularui.multiblock.SteamElevatorGui;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.common.steam.SteamWirelessNetworkManager;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.enums.SteamTypes;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GTChunkManager;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

@IMetaTileEntity.SkipGenerateDescription
public class SteamElevator extends SteamMultiMachineBase<SteamElevator> implements ISurvivalConstructable {

    private static final int HORIZONTAL_OFF_SET = 17;
    private static final int VERTICAL_OFF_SET = 39;
    private static final int DEPTH_OFF_SET = 14;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SE_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_elevator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SE_STRUCTURE_FILE_PATH);
    public ObjectArrayList<SteamElevatorModuleBase> mModuleHatches = new ObjectArrayList<>();
    public boolean isLoadedChunk;
    public boolean wirelessMode = false;
    public String costingEUText = MathUtils.ZERO_STRING;

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
    public void onRemoval() {
        if (mModuleHatches != null && !mModuleHatches.isEmpty()) {
            for (SteamElevatorModuleBase projectModule : mModuleHatches) {
                projectModule.disconnect();
            }
        }
        super.onRemoval();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTick == 1) {
                TeamNetworkManager.getTeamId(aBaseMetaTileEntity.getOwnerUuid());
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
                        long usedSteam = 0;
                        if (wirelessMode) {
                            perModuleEnergy = Long.MAX_VALUE;
                        } else {
                            perModuleEnergy = getEUVar() / mModuleHatches.size();
                        }
                        for (SteamElevatorModuleBase mModule : mModuleHatches) {
                            mModule.connect();
                            if (wirelessMode && SteamWirelessNetworkManager.getUserSteam(ownerUUID)
                                .compareTo(BigInteger.ZERO) > 0) {
                                long used = mModule.increaseStoredEU(perModuleEnergy);
                                SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, -used);
                                usedSteam += used;
                            } else {
                                long tAvailableEnergy = getEUVar();
                                if (tAvailableEnergy > 0) {
                                    long used = mModule.increaseStoredEU(Math.min(perModuleEnergy, tAvailableEnergy));
                                    setEUVar(Math.max(0, tAvailableEnergy - used));
                                }
                            }

                        }
                        costingEUText = NumberFormatUtil.formatNumber(usedSteam);
                    }
                }
            } else {
                if (!mModuleHatches.isEmpty()) {
                    for (SteamElevatorModuleBase mModule : mModuleHatches) {
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

    @Override
    public IStructureDefinition<SteamElevator> getStructureDefinition() {
        return StructureDefinition.<SteamElevator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamElevator.class)
                        .casingIndex(GTNLCasings.SteelReinforcedWood.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamElevator.class).casingIndex(GTNLCasings.SteelReinforcedWood.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamElevator.class).casingIndex(GTNLCasings.SteelReinforcedWood.getTextureId())
                        .hint(1)
                        .buildAndChain(BlockLoader.metaCasing, 25)))
            .addElement('B', GTNLCasings.SteamCompactPipeCasing.asElement())
            .addElement('C', Casings.BronzePlatedBricks.asElement())
            .addElement('D', Casings.SolidSteelMachineCasing.asElement())
            .addElement('E', Casings.SteelFireboxCasing.asElement())
            .addElement('F', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('G', StructureUtility.ofBlock(Blocks.brick_block, 0))
            .addElement(
                'H',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(SteamElevator.class)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.InputHatch,
                            HatchElement.OutputHatch,
                            HatchElement.InputBus,
                            HatchElement.OutputBus)
                        .buildAndChain(GregTechAPI.sBlockCasings2, 0),
                    buildSteamWirelessInput(SteamElevator.class)
                        .casingIndex(GTNLCasings.SteelReinforcedWood.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamElevator.class).casingIndex(GTNLCasings.SteelReinforcedWood.getTextureId())
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamElevator.class).casingIndex(GTNLCasings.SteelReinforcedWood.getTextureId())
                        .hint(1)
                        .buildAndChain(BlockLoader.metaCasing, 25)))
            .addElement(
                'I',
                HatchElementBuilder.<SteamElevator>builder()
                    .atLeast(SteamModuleElement.SteamModule)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(GregTechAPI.sBlockCasings2, 0))
            .addElement('J', StructureUtility.ofBlock(Blocks.stonebrick, 0))
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
        wirelessMode = false;
        mModuleHatches.clear();
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        if (mSteamInputFluids.isEmpty() && mSteamBigInputFluids.isEmpty() && mSteamWirelessInputFluids.isEmpty()) {
            wirelessMode = true;
        }
        updateHatchTexture();
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

    public int getAvailableAmount(GTUtility.FluidId fluid) {
        int total = 0;

        for (MTEHatchCustomFluidBase tHatch : GTUtility.validMTEList(mSteamInputFluids)) {
            FluidStack stack = tHatch.getFluid();
            if (stack != null && fluid.matches(stack.getFluid())) {
                total += stack.amount;
            }
        }

        for (CustomFluidHatch tHatch : GTUtility.validMTEList(mSteamBigInputFluids)) {
            FluidStack stack = tHatch.getFluid();
            if (stack != null && fluid.matches(stack.getFluid())) {
                total += stack.amount;
            }
        }

        for (CustomFluidHatch tHatch : GTUtility.validMTEList(mSteamWirelessInputFluids)) {
            FluidStack stack = tHatch.getFluid();
            if (stack != null && fluid.matches(stack.getFluid())) {
                total += stack.amount;
            }
        }

        return total;
    }

    public int depleteInput(GTUtility.FluidId fluid, int maxDrainAmount) {
        int totalDrained = 0;

        for (MTEHatchCustomFluidBase tHatch : GTUtility.validMTEList(mSteamInputFluids)) {
            if (totalDrained >= maxDrainAmount) break;

            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && fluid.matches(tLiquid.getFluid())) {
                int remaining = maxDrainAmount - totalDrained;
                FluidStack drained = tHatch.drain(remaining, true);
                if (drained != null) totalDrained += drained.amount;
            }
        }

        for (CustomFluidHatch tHatch : GTUtility.validMTEList(mSteamBigInputFluids)) {
            if (totalDrained >= maxDrainAmount) break;

            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && fluid.matches(tLiquid.getFluid())) {
                int remaining = maxDrainAmount - totalDrained;
                FluidStack drained = tHatch.drain(remaining, true);
                if (drained != null) totalDrained += drained.amount;
            }
        }

        for (CustomFluidHatch tHatch : GTUtility.validMTEList(mSteamWirelessInputFluids)) {
            if (totalDrained >= maxDrainAmount) break;

            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && fluid.matches(tLiquid.getFluid())) {
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

    public boolean addModuleToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof SteamElevatorModuleBase moduleBase) {
            return mModuleHatches.add(moduleBase);
        }
        return false;
    }

    @Override
    public int getTierRecipes() {
        return 1;
    }

    @Override
    public SoundResource getActivitySoundLoop() {
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
                new TTRenderedExtendedFacingTexture(
                    aActive ? BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE
                        : BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return Casings.SolidSteelMachineCasing.getTextureId();
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamElevatorGui(this);
    }

    @Override
    public boolean supportsSteamOC() {
        return false;
    }

    @Override
    public boolean supportsSteamCapacityUI() {
        return false;
    }

    public boolean isMachineForGui() {
        return mMachine;
    }

    public boolean isAllowedToWorkForGui() {
        return getBaseMetaTileEntity().isAllowedToWork();
    }

    public int getNumberOfModulesForGui() {
        return getNumberOfModules();
    }

    public void openCelestialSelection(EntityPlayer player) {
        if (!isAllowedToWorkForGui() || !(player instanceof EntityPlayerMP playerBase)) {
            return;
        }

        GCPlayerStats stats = GCPlayerStats.get(playerBase);
        stats.coordsTeleportedFromX = playerBase.posX;
        stats.coordsTeleportedFromZ = playerBase.posZ;
        try {
            WorldUtil.toCelestialSelection(playerBase, stats, 0, GuiCelestialSelection.MapMode.TELEPORTATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean doesBindPlayerInventory() {
        return false;
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
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_elevator.recipe_type");
    }

    @Override
    public TooltipStats getTooltipStats() {
        return null;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
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
            currentTip
                .add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("gtnl.waila.wireless.mode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.wireless.current_steam_cost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " L");
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

        SteamModule(SteamElevator::addModuleToMachineList, SteamElevatorModuleBase.class) {

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

        @Override
        public IGTHatchAdder<? super SteamElevator> adder() {
            return adder;
        }
    }
}
