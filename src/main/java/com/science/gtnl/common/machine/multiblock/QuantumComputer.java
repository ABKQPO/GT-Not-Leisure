package com.science.gtnl.common.machine.multiblock;

import static gregtech.api.enums.Textures.BlockIcons.*;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.ECPUCluster;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkCraftingCpuChange;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.WorldCoord;
import appeng.me.GridAccessException;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.interfaces.ISecondaryDescribable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTETooltipMultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

public class QuantumComputer extends MTETooltipMultiBlockBase
    implements IConstructable, ISecondaryDescribable, IActionHost, IGridProxyable, IAddGregtechLogo {

    /**
     * Maximum size of the quantum computer. Includes walls.
     */
    public static int MAX_SIZE = MainConfig.quantumComputerMaximumMultiblockSize;

    public static int CASING_INDEX = GTUtility.getTextureId((byte) 116, (byte) 42);

    public static Block CRAFTING_STORAGE = GameRegistry
        .findBlock(Mods.AppliedEnergistics2.ID, "tile.BlockCraftingStorage");
    public static Block ADV_CRAFTING_STORAGE = GameRegistry
        .findBlock(Mods.AppliedEnergistics2.ID, "tile.BlockAdvancedCraftingStorage");
    public static Block SINGULARITY_CRAFTING_STORAGE = GameRegistry
        .findBlock(Mods.AppliedEnergistics2.ID, "tile.BlockSingularityCraftingStorage");
    public static Block CRAFTING_PROCESSING_UNIT = GameRegistry
        .findBlock(Mods.AppliedEnergistics2.ID, "tile.BlockCraftingUnit");
    public static Block ADV_CRAFTING_PROCESSING_UNIT = GameRegistry
        .findBlock(Mods.AppliedEnergistics2.ID, "tile.BlockAdvancedCraftingUnit");

    // Extent in all directions. Specifically the offset from the controller to each wall.
    // Min values will always be negative, Max values positive.
    public int dxMin = 0, dxMax = 0, dzMin = 0, dzMax = 0, dyMin = 0;

    public int width;
    public int height;
    public int depth;

    public int casingCount;
    public int unitCount;
    public int coreCount;
    public int multiThreaderCount;
    public int dataEntanglerCount;
    public int singularityCraftingStorageCount;
    public long maximumStorage = 0;
    public int maximumParallel = 0;
    public long usedStorage = 0;
    public int usedParallel = 0;

    public long getMaximumStorage() {
        if (singularityCraftingStorageCount > 0) return Long.MAX_VALUE;
        return maximumStorage;
    }

    public QuantumComputer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public QuantumComputer(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new QuantumComputer(mName);
    }

    @Override
    public String[] getStructureDescription(ItemStack itemStack) {
        return new String[] { StatCollector.translateToLocal("Tooltip_QuantumComputer_10") };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("QuantumComputerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_11"))
            .addInfo(StatCollector.translateToLocal("Tooltip_QuantumComputer_12"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginVariableStructureBlock(3, MAX_SIZE, 3, MAX_SIZE, 3, MAX_SIZE, true)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        cpus.forEach(
            cluster -> ECPUCluster.from(cluster)
                .ec$markDestroyed());
        cpus.clear();
        postCPUClusterChangeEvent();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isAllowedToWork()) aBaseMetaTileEntity.disableWorking();
    }

    @Override
    public void onFirstTick(IGregTechTileEntity baseMetaTileEntity) {
        super.onFirstTick(baseMetaTileEntity);
        if (checkStructure(true)) {
            this.mStartUpCheck = -1;
            this.mUpdate = 200;
        }
        getProxy().onReady();
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return super.supportsSingleRecipeLocking();
    }

    @Override
    public boolean isRecipeLockingEnabled() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return super.supportsVoidProtection();
    }

    @Override
    public VoidingMode getVoidingMode() {
        return VoidingMode.VOID_NONE;
    }

    @Override
    public boolean supportsInputSeparation() {
        return super.supportsInputSeparation();
    }

    @Override
    public boolean isInputSeparationEnabled() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return super.supportsBatchMode();
    }

    @Override
    public boolean isBatchModeEnabled() {
        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {

        // structure bounds
        aNBT.setInteger("dxMin", this.dxMin);
        aNBT.setInteger("dxMax", this.dxMax);
        aNBT.setInteger("dzMin", this.dzMin);
        aNBT.setInteger("dzMax", this.dzMax);
        aNBT.setInteger("dyMin", this.dyMin);

        // dimensions
        aNBT.setInteger("width", this.width);
        aNBT.setInteger("height", this.height);
        aNBT.setInteger("depth", this.depth);

        // counts
        aNBT.setInteger("casingCount", this.casingCount);
        aNBT.setInteger("coreCount", this.coreCount);
        aNBT.setInteger("unitCount", this.unitCount);
        aNBT.setInteger("multiThreaderCount", this.multiThreaderCount);
        aNBT.setInteger("dataEntanglerCount", this.dataEntanglerCount);
        aNBT.setInteger("singularityCraftingStorageCount", this.singularityCraftingStorageCount);

        // storage / parallel
        aNBT.setLong("maximumStorage", getMaximumStorage());
        aNBT.setInteger("maximumParallel", this.maximumParallel);

        getProxy().writeToNBT(aNBT);
        writeCPUNBT(aNBT);

        super.saveNBTData(aNBT);
    }

    public void writeCPUNBT(final NBTTagCompound compound) {
        final NBTTagList clustersTag = new NBTTagList();
        cpus.forEach(cluster -> {
            ECPUCluster eCluster = ECPUCluster.from(cluster);
            NBTTagCompound clusterTag = new NBTTagCompound();
            cluster.writeToNBT(clusterTag);
            clusterTag.setLong("availableStorage", cluster.getAvailableStorage());
            clustersTag.appendTag(clusterTag);
        });
        compound.setTag("clusters", clustersTag);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {

        // structure bounds
        this.dxMin = aNBT.getInteger("dxMin");
        this.dxMax = aNBT.getInteger("dxMax");
        this.dzMin = aNBT.getInteger("dzMin");
        this.dzMax = aNBT.getInteger("dzMax");
        this.dyMin = aNBT.getInteger("dyMin");

        // dimensions
        this.width = aNBT.getInteger("width");
        this.height = aNBT.getInteger("height");
        this.depth = aNBT.getInteger("depth");

        // counts
        this.casingCount = aNBT.getInteger("casingCount");
        this.coreCount = aNBT.getInteger("coreCount");
        this.unitCount = aNBT.getInteger("unitCount");
        this.multiThreaderCount = aNBT.getInteger("multiThreaderCount");
        this.dataEntanglerCount = aNBT.getInteger("dataEntanglerCount");
        this.singularityCraftingStorageCount = aNBT.getInteger("singularityCraftingStorageCount");

        // storage / parallel
        this.maximumStorage = aNBT.getLong("maximumStorage");
        this.maximumParallel = aNBT.getInteger("maximumParallel");
        getProxy().readFromNBT(aNBT);
        readCPUNBT(aNBT);

        super.loadNBTData(aNBT);
    }

    public void readCPUNBT(final NBTTagCompound compound) {
        new ReferenceArrayList<>(cpus).forEach(CraftingCPUCluster::destroy);
        cpus.clear();

        final NBTTagList clustersTag = compound.getTagList("clusters", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < clustersTag.tagCount(); i++) {
            NBTTagCompound clusterTag = clustersTag.getCompoundTagAt(i);

            WorldCoord coord = getWorldCoord();
            CraftingCPUCluster cluster = new CraftingCPUCluster(coord, coord);
            ECPUCluster eCluster = ECPUCluster.from(cluster);
            eCluster.ec$setVirtualCPUOwner(this);
            eCluster.ec$setAvailableStorage(clusterTag.getLong("availableStorage"));
            cluster.readFromNBT(clusterTag);
            cpus.add(cluster);
        }
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return (facing.flag & (ForgeDirection.UP.flag | ForgeDirection.DOWN.flag)) == 0;
    }

    public QuantumComputerBlockType getBlockType(IGregTechTileEntity aBaseMetaTileEntity, int dx, int dy, int dz,
        boolean isCasings) {
        Block block = aBaseMetaTileEntity.getBlockOffset(dx, dy, dz);
        int meta = block.getDamageValue(
            aBaseMetaTileEntity.getWorld(),
            aBaseMetaTileEntity.getXCoord() + dx,
            aBaseMetaTileEntity.getYCoord() + dy,
            aBaseMetaTileEntity.getZCoord() + dz);

        if (isCasings) {
            if (block == BlockLoader.metaCasing02 && meta == 10) {
                return QuantumComputerBlockType.CASING;
            } else {
                return QuantumComputerBlockType.INVALID;
            }
        }

        if (block == BlockLoader.metaCasing02) {
            if (meta == 11) return QuantumComputerBlockType.UNIT;
            if (meta == 12) return QuantumComputerBlockType.CRAFTING_STORAGE_128M;
            if (meta == 13) return QuantumComputerBlockType.CRAFTING_STORAGE_256M;
            if (meta == 14) return QuantumComputerBlockType.DATA_ENTANGLER;
            if (meta == 15) return QuantumComputerBlockType.ACCELERATOR;
            if (meta == 16) return QuantumComputerBlockType.MULTI_THREADER;
            if (meta == 17) return QuantumComputerBlockType.CORE;
        }

        if (block == CRAFTING_STORAGE) {
            if (meta == 0) return QuantumComputerBlockType.CRAFTING_STORAGE_1K;
            if (meta == 1) return QuantumComputerBlockType.CRAFTING_STORAGE_4K;
            if (meta == 2) return QuantumComputerBlockType.CRAFTING_STORAGE_16K;
            if (meta == 3) return QuantumComputerBlockType.CRAFTING_STORAGE_64K;
        }

        if (block == ADV_CRAFTING_STORAGE) {
            if (meta == 0) return QuantumComputerBlockType.CRAFTING_STORAGE_256K;
            if (meta == 1) return QuantumComputerBlockType.CRAFTING_STORAGE_1024K;
            if (meta == 2) return QuantumComputerBlockType.CRAFTING_STORAGE_4096K;
            if (meta == 3) return QuantumComputerBlockType.CRAFTING_STORAGE_16384K;
        }

        if (block == SINGULARITY_CRAFTING_STORAGE) {
            return QuantumComputerBlockType.CRAFTING_STORAGE_SINGULARITY;
        }

        if (block == CRAFTING_PROCESSING_UNIT) {
            if (meta == 1) return QuantumComputerBlockType.CRAFTING_PROCESSING_UNIT_1;
            if (meta == 2) return QuantumComputerBlockType.CRAFTING_PROCESSING_UNIT_4;
            if (meta == 3) return QuantumComputerBlockType.CRAFTING_PROCESSING_UNIT_16;
        }

        if (block == ADV_CRAFTING_PROCESSING_UNIT) {
            if (meta == 0) return QuantumComputerBlockType.CRAFTING_PROCESSING_UNIT_64;
            if (meta == 1) return QuantumComputerBlockType.CRAFTING_PROCESSING_UNIT_256;
            if (meta == 2) return QuantumComputerBlockType.CRAFTING_PROCESSING_UNIT_1024;
            if (meta == 3) return QuantumComputerBlockType.CRAFTING_PROCESSING_UNIT_4096;
        }

        return QuantumComputerBlockType.INVALID;
    }

    /**
     * Add a block to the quantum computer which is at the specified offset. This properly increases the count of
     * casings/unit
     *
     * @return True on success (block was correctly added), false on failure (invalid block type).
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean addStructureBlock(IGregTechTileEntity aBaseMetaTileEntity, int dx, int dy, int dz,
        boolean isCasing) {

        switch (getBlockType(aBaseMetaTileEntity, dx, dy, dz, isCasing)) {

            case CASING:
                casingCount++;
                return true;

            case UNIT:
                unitCount++;
                return true;

            case CORE:
                maximumStorage = addToStorage(maximumStorage, 268435456L);
                maximumParallel = addToParallel(maximumParallel, 16384);
                unitCount++;
                coreCount++;
                return true;

            case CRAFTING_STORAGE_1K:
                maximumStorage = addToStorage(maximumStorage, 1024L);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_4K:
                maximumStorage = addToStorage(maximumStorage, 4L * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_16K:
                maximumStorage = addToStorage(maximumStorage, 16L * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_64K:
                maximumStorage = addToStorage(maximumStorage, 64L * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_256K:
                maximumStorage = addToStorage(maximumStorage, 256L * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_1024K:
                maximumStorage = addToStorage(maximumStorage, 1024L * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_4096K:
                maximumStorage = addToStorage(maximumStorage, 4096L * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_16384K:
                maximumStorage = addToStorage(maximumStorage, 16384L * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_128M:
                maximumStorage = addToStorage(maximumStorage, 128L * 1024 * 1024);
                unitCount++;
                return true;

            case CRAFTING_STORAGE_256M:
                maximumStorage = addToStorage(maximumStorage, 256L * 1024 * 1024);
                unitCount++;
                return true;

            case DATA_ENTANGLER:
                dataEntanglerCount++;
                unitCount++;
                return true;

            case CRAFTING_STORAGE_SINGULARITY:
                maximumStorage = Long.MAX_VALUE;
                singularityCraftingStorageCount++;
                unitCount++;
                return true;

            case CRAFTING_PROCESSING_UNIT_1:
                maximumParallel = addToParallel(maximumParallel, 1);
                unitCount++;
                return true;

            case CRAFTING_PROCESSING_UNIT_4:
                maximumParallel = addToParallel(maximumParallel, 4);
                unitCount++;
                return true;

            case CRAFTING_PROCESSING_UNIT_16:
                maximumParallel = addToParallel(maximumParallel, 16);
                unitCount++;
                return true;

            case CRAFTING_PROCESSING_UNIT_64:
                maximumParallel = addToParallel(maximumParallel, 64);
                unitCount++;
                return true;

            case CRAFTING_PROCESSING_UNIT_256:
                maximumParallel = addToParallel(maximumParallel, 256);
                unitCount++;
                return true;

            case CRAFTING_PROCESSING_UNIT_1024:
                maximumParallel = addToParallel(maximumParallel, 1024);
                unitCount++;
                return true;

            case CRAFTING_PROCESSING_UNIT_4096:
                maximumParallel = addToParallel(maximumParallel, 4096);
                unitCount++;
                return true;

            case ACCELERATOR:
                maximumParallel = addToParallel(maximumParallel, 16384);
                unitCount++;
                return true;

            case MULTI_THREADER:
                multiThreaderCount++;
                unitCount++;
                return true;

            case INVALID:
                if (MainConfig.enableDebugMode)
                    ScienceNotLeisure.LOG.info("Quantum Computer: Invalid block at offset ({}, {}, {}).", dx, dy, dz);
                return false;

            default:
                throw new IllegalArgumentException(
                    "Quantum Computer error: unknown block type at offset (" + dx + ", " + dy + ", " + dz + ").");
        }
    }

    public long addToStorage(long current, long increment) {
        if (current > Long.MAX_VALUE - increment) {
            return Long.MAX_VALUE;
        }
        return current + increment;
    }

    public int addToParallel(int current, int increment) {
        if (current > Integer.MAX_VALUE - increment) {
            return Integer.MAX_VALUE;
        }
        return current + increment;
    }

    /**
     * Find the horizontal size of the quantum computer. Populates values dxMin, dxMax, dzMin, and dzMax.
     *
     * @return True on success, false on failure (which means an invalid structure).
     */
    public boolean checkSize(IGregTechTileEntity aBaseMetaTileEntity) {

        // X direction (min)
        dxMin = -1;
        while (true) {
            int next = dxMin - 1;

            if (getBlockType(aBaseMetaTileEntity, next, 0, 0, true) == QuantumComputerBlockType.INVALID) {
                break;
            }

            if (next < -MAX_SIZE / 2) {
                return false;
            }

            dxMin = next;
        }

        // X direction (max)
        dxMax = 1;
        while (true) {
            int next = dxMax + 1;

            if (getBlockType(aBaseMetaTileEntity, next, 0, 0, true) == QuantumComputerBlockType.INVALID) {
                break;
            }

            if (next > MAX_SIZE / 2) {
                return false;
            }

            dxMax = next;
        }

        // controller must be centered (odd) or one of two center blocks (even)
        if (Math.abs(dxMin + dxMax) > 1) {
            return false;
        }

        // Z direction (min)
        dzMin = -1;
        while (true) {
            int next = dzMin - 1;

            if (getBlockType(aBaseMetaTileEntity, 0, 0, next, true) == QuantumComputerBlockType.INVALID) {
                break;
            }

            if (next < -MAX_SIZE / 2) {
                return false;
            }

            dzMin = next;
        }

        // Z direction (max)
        dzMax = 1;
        while (true) {
            int next = dzMax + 1;

            if (getBlockType(aBaseMetaTileEntity, 0, 0, next, true) == QuantumComputerBlockType.INVALID) {
                break;
            }

            if (next > MAX_SIZE / 2) {
                return false;
            }

            dzMax = next;
        }

        return Math.abs(dzMin + dzMax) <= 1;
    }

    /**
     * Checks whether the ceiling layer of the quantum computer is complete. Assumes that
     * {@link #checkSize(IGregTechTileEntity)} has already been run.
     *
     * @return True on success, false on failure.
     */
    @SuppressWarnings({ "BooleanMethodIsAlwaysInverted", "StatementWithEmptyBody" })
    public boolean checkCeiling(IGregTechTileEntity aBaseMetaTileEntity) {
        // Edges must be plascrete, everything else must be filters (except for the controller).
        for (int dx = dxMin; dx <= dxMax; ++dx) {
            for (int dz = dzMin; dz <= dzMax; ++dz) {
                if (dx == 0 && dz == 0) {
                    // Controller.
                } else if (dx == dxMin || dx == dxMax || dz == dzMin || dz == dzMax) {
                    // Edge.
                    if (!addStructureBlock(aBaseMetaTileEntity, dx, 0, dz, true)) return false;
                } else {
                    // Internal block.
                    if (!addStructureBlock(aBaseMetaTileEntity, dx, 0, dz, true)) return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks the floor of the quantum computer. Note that if this fails, it is not necessarily because the structure is
     * invalid, maybe the floor just isn't where we thought it was, and we're looking at a wall.
     *
     * @param dy Vertical offset of the floor from the controller.
     * @return True on success, false on failure.
     */
    public boolean checkFloor(IGregTechTileEntity aBaseMetaTileEntity, int dy) {
        for (int dx = dxMin + 1; dx <= dxMax - 1; ++dx) {
            for (int dz = dzMin + 1; dz <= dzMax - 1; ++dz) {
                switch (getBlockType(aBaseMetaTileEntity, dx, dy, dz, true)) {
                    case CASING:
                        casingCount++;
                        break;

                    case INVALID:
                        // Do not log an error, we might not be at the correct floor level yet.
                        return false;

                    default:
                        throw new IllegalArgumentException(
                            "Quantum Computer error: unknown block type at at offset (" + dx
                                + ", "
                                + dy
                                + ", "
                                + dz
                                + ").");
                }
            }
        }
        return true;
    }

    /**
     * Checks the walls of the quantum computer at a specified vertical offset.
     *
     * @param dy Vertical offset of the floor from the controller.
     * @return True on success, false on failure.
     */
    public boolean checkWall(IGregTechTileEntity aBaseMetaTileEntity, int dy) {
        for (int dx = dxMin + 1; dx <= dxMax - 1; ++dx) {
            if (!addStructureBlock(aBaseMetaTileEntity, dx, dy, dzMin, true)) return false;
            if (!addStructureBlock(aBaseMetaTileEntity, dx, dy, dzMax, true)) return false;
        }
        for (int dz = dzMin + 1; dz <= dzMax - 1; ++dz) {
            if (!addStructureBlock(aBaseMetaTileEntity, dxMin, dy, dz, true)) return false;
            if (!addStructureBlock(aBaseMetaTileEntity, dxMax, dy, dz, true)) return false;
        }

        return addStructureBlock(aBaseMetaTileEntity, dxMin, dy, dzMin, true)
            && addStructureBlock(aBaseMetaTileEntity, dxMin, dy, dzMax, true)
            && addStructureBlock(aBaseMetaTileEntity, dxMax, dy, dzMin, true)
            && addStructureBlock(aBaseMetaTileEntity, dxMax, dy, dzMax, true);
    }

    @Override
    public void clearHatches() {
        width = 0;
        height = 0;
        depth = 0;
        casingCount = 0;
        unitCount = 0;
        coreCount = 0;
        multiThreaderCount = 0;
        dataEntanglerCount = 0;
        singularityCraftingStorageCount = 0;
        maximumStorage = 0L;
        maximumParallel = 0;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        boolean b = checkMachine(aBaseMetaTileEntity);
        if (b) {
            getProxy().setValidSides(upDirection);
            if (this.virtualCPU == null) {
                createVirtualCPU();
            }
        } else {
            getProxy().setValidSides(emptyDirection);
            if (this.virtualCPU != null) {
                this.virtualCPU.destroy();
                this.virtualCPU = null;
            }
        }
        return b;
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity) {
        // Optimization: a vast majority of the time, the size of the CR won't change. Try checking it using the old
        // size, and only if that fails, try to find a new size.
        if (dyMin == 0 || !checkCeiling(aBaseMetaTileEntity)) {
            if (!checkSize(aBaseMetaTileEntity)) return false;
            if (!checkCeiling(aBaseMetaTileEntity)) return false;
        }

        // Check downward until we find a valid floor.
        // We check specifically internal blocks for a valid floor. This means that in most cases this check
        // immediately falls through, as the first block we check is already invalid (e.g., air or machine).
        for (dyMin = -1; dyMin >= -(MAX_SIZE - 1); --dyMin) {
            if (dyMin < -1 && checkFloor(aBaseMetaTileEntity, dyMin)) {
                // Found a valid floor. Add its edges and finish.
                for (int dx = dxMin; dx <= dxMax; ++dx) {
                    if (!addStructureBlock(aBaseMetaTileEntity, dx, dyMin, dzMin, true)) return false;
                    if (!addStructureBlock(aBaseMetaTileEntity, dx, dyMin, dzMax, true)) return false;
                }
                for (int dz = dzMin + 1; dz <= dzMax - 1; ++dz) {
                    if (!addStructureBlock(aBaseMetaTileEntity, dxMin, dyMin, dz, true)) return false;
                    if (!addStructureBlock(aBaseMetaTileEntity, dxMax, dyMin, dz, true)) return false;
                }
                break;
            } else {
                // Not floor yet, check for a wall.
                if (!checkWall(aBaseMetaTileEntity, dyMin)) {
                    dyMin = 0;
                    return false;
                }
            }
        }
        if (dyMin < -(MAX_SIZE - 1)) {
            if (MainConfig.enableDebugMode) ScienceNotLeisure.LOG.info("Quantum Computer: Too tall.");
            return false;
        }

        if (MainConfig.enableDebugMode) ScienceNotLeisure.LOG
            .info("Quantum Computer: Structure complete. Found {} casings, {} unit blocks.", casingCount, unitCount);

        width = dxMax - dxMin + 1;
        height = -dyMin + 1;
        depth = dzMax - dzMin + 1;

        if (casingCount != calculateCasingCount(width, height, depth) - 1) {
            return false;
        }

        for (int dy = dyMin + 1; dy < 0; ++dy) {
            for (int dx = dxMin + 1; dx <= dxMax - 1; ++dx) {
                for (int dz = dzMin + 1; dz <= dzMax - 1; dz++) {
                    if (!addStructureBlock(aBaseMetaTileEntity, dx, dy, dz, false)) return false;
                }
            }
        }

        if (unitCount != (width - 2) * (height - 2) * (depth - 2)) {
            return false;
        }

        if (multiThreaderCount > MainConfig.quantumComputerMaximumQuantumComputerMultiThreader
            || dataEntanglerCount > MainConfig.quantumComputerMaximumQuantumDataEntangler
            || coreCount > MainConfig.quantumComputerMaximumQuantumComputerCore) {
            return false;
        }

        for (int i = 0; i < dataEntanglerCount; i++) {
            if (maximumStorage > Long.MAX_VALUE / 4L) {
                maximumStorage = Long.MAX_VALUE;
                break;
            }
            maximumStorage *= 4L;
        }

        for (int i = 0; i < multiThreaderCount; i++) {
            if (maximumParallel > Integer.MAX_VALUE / 4) {
                maximumParallel = Integer.MAX_VALUE;
                break;
            }
            maximumParallel *= 4;
        }

        if (MainConfig.enableDebugMode) ScienceNotLeisure.LOG.info("Quantum Computer: Check successful.");

        return true;
    }

    public static int calculateCasingCount(int width, int height, int depth) {
        if (width < 1 || height < 1 || depth < 1) return 0;

        return 2 * (width * height + height * depth + width * depth) - 4 * (width + height + depth) + 8;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if ((sideDirection.flag & (ForgeDirection.UP.flag | ForgeDirection.DOWN.flag)) != 0) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX),
                active ? TextureFactory.of(TextureFactory.of(OVERLAY_ME_INPUT_HATCH_ACTIVE))
                    : TextureFactory.of(TextureFactory.of(OVERLAY_ME_INPUT_HATCH)) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX) };
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        final int i = Math.min(stackSize.stackSize, MAX_SIZE / 2);
        IGregTechTileEntity baseEntity = this.getBaseMetaTileEntity();
        World world = baseEntity.getWorld();
        int x = baseEntity.getXCoord();
        int y = baseEntity.getYCoord();
        int z = baseEntity.getZCoord();
        int yoff = Math.max(i * 2, 2);

        for (int X = x - i; X <= x + i; X++) {
            for (int Y = y - yoff; Y <= y; Y++) {
                for (int Z = z - i; Z <= z + i; Z++) {
                    if (X == x && Y == y && Z == z) continue;

                    boolean isWall = (X == x - i || X == x + i || Z == z - i || Z == z + i);
                    boolean isTop = (Y == y);
                    boolean isBottom = (Y == y - yoff);

                    if (isWall || isTop || isBottom) {
                        if (hintsOnly) StructureLibAPI.hintParticle(world, X, Y, Z, BlockLoader.metaCasing02, 10);
                        else world.setBlock(X, Y, Z, BlockLoader.metaCasing02, 10, 2);
                    }
                }
            }
        }

        for (int X = x - i + 1; X <= x + i - 1; X++) {
            for (int Y = y - yoff + 1; Y <= y - 1; Y++) {
                for (int Z = z - i + 1; Z <= z + i - 1; Z++) {
                    if (hintsOnly) StructureLibAPI.hintParticle(world, X, Y, Z, BlockLoader.metaCasing02, 11);
                    else world.setBlock(X, Y, Z, BlockLoader.metaCasing02, 11, 2);
                }
            }
        }
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        addGregTechLogo(builder);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_CIRCULATION)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements
            .widget(
                new TextWidget()
                    .setStringSupplier(
                        () -> StatCollector.translateToLocalFormatted("Info_QuantumComputer_00", width, height, depth))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setDefaultColor(COLOR_TEXT_WHITE.get()))
            .widget(
                new TextWidget()
                    .setStringSupplier(
                        () -> StatCollector.translateToLocalFormatted(
                            "Info_QuantumComputer_01",
                            GTUtility.formatNumbers(maximumParallel),
                            GTUtility.formatNumbers(usedParallel),
                            String.format(
                                "%.1f%%",
                                maximumParallel > 0 ? (double) usedParallel / maximumParallel * 100.0 : 0.0)))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setDefaultColor(COLOR_TEXT_WHITE.get()))
            .widget(
                new TextWidget()
                    .setStringSupplier(
                        () -> StatCollector.translateToLocalFormatted(
                            "Info_QuantumComputer_02",
                            GTUtility.scientificFormat(getMaximumStorage()),
                            usedStorage,
                            String.format(
                                "%.1f%%",
                                getMaximumStorage() > 0 ? (double) usedParallel / getMaximumStorage() * 100.0 : 0.0)))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setDefaultColor(COLOR_TEXT_WHITE.get()))
            .widget(new FakeSyncWidget.IntegerSyncer(() -> width, w -> width = w))
            .widget(new FakeSyncWidget.IntegerSyncer(() -> height, h -> height = h))
            .widget(new FakeSyncWidget.IntegerSyncer(() -> depth, d -> depth = d))
            .widget(new FakeSyncWidget.IntegerSyncer(() -> maximumParallel, parallel -> maximumParallel = parallel))
            // .widget(new FakeSyncWidget.IntegerSyncer(() -> getUsedBytes(), parallel -> maximumParallel = parallel))
            .widget(new FakeSyncWidget.LongSyncer(this::getMaximumStorage, storage -> maximumStorage = storage))
            .widget(new FakeSyncWidget.LongSyncer(this::getUsedBytes, storage -> usedStorage = storage));
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public IGridNode getActionableNode() {
        return getProxy().getNode();
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(
            getBaseMetaTileEntity().getWorld(),
            getBaseMetaTileEntity().getXCoord(),
            getBaseMetaTileEntity().getYCoord(),
            getBaseMetaTileEntity().getZCoord());
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        return getProxy().getNode();
    }

    public AECableType getCableConnectionType(ForgeDirection forgeDirection) {
        return AECableType.DENSE_COVERED;
    }

    private AENetworkProxy gridProxy;

    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            var bmte = getBaseMetaTileEntity();
            if (bmte instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(this, "proxy", GTNLItemList.AssemblerMatrix.get(1), true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                if (bmte.getWorld() != null) {
                    gridProxy.setOwner(
                        bmte.getWorld()
                            .getPlayerEntityByName(bmte.getOwnerName()));
                }
            }
        }
        return gridProxy;
    }

    public static final EnumSet<ForgeDirection> upDirection = EnumSet.of(ForgeDirection.UP);
    public static final EnumSet<ForgeDirection> emptyDirection = EnumSet.noneOf(ForgeDirection.class);

    @Override
    public void securityBreak() {

    }

    private boolean wasActive = false;

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        final boolean currentActive = isActive();
        if (this.wasActive != currentActive) {
            this.wasActive = currentActive;
            postCPUClusterChangeEvent();
        }
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        final boolean currentActive = isActive();
        if (this.wasActive != currentActive) {
            this.wasActive = currentActive;
            postCPUClusterChangeEvent();
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        getProxy().onChunkUnload();
    }

    @Override
    public void inValidate() {
        super.inValidate();
        getProxy().invalidate();
    }

    protected void postCPUClusterChangeEvent() {
        if (isActive()) {
            try {
                getProxy().getGrid()
                    .postEvent(new MENetworkCraftingCpuChange(getProxy().getNode()));
            } catch (final GridAccessException ignored) {}
        }
    }

    public boolean isActive() {
        return getProxy().isActive();
    }

    protected CraftingCPUCluster virtualCPU = null;
    protected final List<CraftingCPUCluster> cpus = new ReferenceArrayList<>();

    public boolean isVirtualCPU(Object cluster) {
        return virtualCPU == cluster;
    }

    public List<CraftingCPUCluster> getCPUs() {
        if (!isActive()) return ObjectLists.emptyList();

        if (cpus.isEmpty())
            return this.virtualCPU != null ? ObjectLists.singleton(this.virtualCPU) : ObjectLists.emptyList();

        final List<CraftingCPUCluster> clusters = new ReferenceArrayList<>(cpus);
        if (this.virtualCPU != null) {
            // Refresh machine source.
            ECPUCluster.from(this.virtualCPU)
                .ec$setVirtualCPUOwner(this);
            clusters.add(this.virtualCPU);
        }
        return clusters;
    }

    public void onVirtualCPUSubmitJob(final long usedBytes) {
        final boolean prevEmpty = cpus.isEmpty();

        ECPUCluster.from(virtualCPU)
            .ec$setVirtualCPUOwner(this);
        cpus.add(virtualCPU);

        if (prevEmpty) {
            markDirty();
        }

        ECPUCluster ecpuCluster = ECPUCluster.from(this.virtualCPU);
        ecpuCluster.ec$setAvailableStorage(usedBytes);
        this.virtualCPU = null;
        createVirtualCPU();
    }

    public long getAvailableBytes() {
        return getMaximumStorage() - getUsedBytes();
    }

    public long getUsedBytes() {
        return cpus.stream()
            .mapToLong(CraftingCPUCluster::getAvailableStorage)
            .sum();
    }

    public void createVirtualCPU() {
        final long availableBytes = getAvailableBytes();
        if (this.virtualCPU != null) {
            ECPUCluster eCluster = ECPUCluster.from(this.virtualCPU);
            eCluster.ec$setAvailableStorage(availableBytes);
            eCluster.ec$setAccelerators(maximumParallel);
            return;
        }

        WorldCoord pos = getWorldCoord();
        this.virtualCPU = new CraftingCPUCluster(pos, pos);
        ECPUCluster eCluster = ECPUCluster.from(this.virtualCPU);
        eCluster.ec$setVirtualCPUOwner(this);
        eCluster.ec$setAvailableStorage(availableBytes);
        eCluster.ec$setAccelerators(maximumParallel);

        this.postCPUClusterChangeEvent();
    }

    public WorldCoord getWorldCoord() {
        return new WorldCoord(
            getBaseMetaTileEntity().getXCoord(),
            getBaseMetaTileEntity().getYCoord(),
            getBaseMetaTileEntity().getZCoord());
    }

    public void onCPUDestroyed(final CraftingCPUCluster cluster) {
        cpus.remove(cluster);
        createVirtualCPU();
        postCPUClusterChangeEvent();
        if (cpus.isEmpty()) {
            markDirty();
        }
    }

    public enum QuantumComputerBlockType {
        CASING, // 量子计算机外壳
        UNIT, // 量子合成单元
        MULTI_THREADER, // 量子计算机多线程处理器
        DATA_ENTANGLER, // 量子数据纠缠器
        ACCELERATOR, // 量子计算机加速器
        CORE, // 量子计算机核心
        CRAFTING_STORAGE_1K, // 1k合成存储器
        CRAFTING_STORAGE_4K, // 4k合成存储器
        CRAFTING_STORAGE_16K, // 16k合成存储器
        CRAFTING_STORAGE_64K, // 64k合成存储器
        CRAFTING_STORAGE_256K, // 256k合成存储器
        CRAFTING_STORAGE_1024K, // 1024k合成存储器
        CRAFTING_STORAGE_4096K, // 4096k合成存储器
        CRAFTING_STORAGE_16384K, // 16384k合成存储器
        CRAFTING_STORAGE_128M, // 128M量子计算机合成存储器
        CRAFTING_STORAGE_256M, // 256M量子计算机合成存储器
        CRAFTING_STORAGE_SINGULARITY, // 奇点合成存储器
        CRAFTING_PROCESSING_UNIT_1, // 并行处理单元
        CRAFTING_PROCESSING_UNIT_4, // 4核并行处理单元
        CRAFTING_PROCESSING_UNIT_16, // 16核并行处理单元
        CRAFTING_PROCESSING_UNIT_64, // 64核并行处理单元
        CRAFTING_PROCESSING_UNIT_256, // 256核并行处理单元
        CRAFTING_PROCESSING_UNIT_1024, // 1024核并行处理单元
        CRAFTING_PROCESSING_UNIT_4096, // 4096核并行处理单元
        INVALID // 无效方块
    }
}
