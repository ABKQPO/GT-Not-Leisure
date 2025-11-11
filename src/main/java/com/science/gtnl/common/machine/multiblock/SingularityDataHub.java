package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.utils.enums.BlockIcons.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.*;
import static gregtech.common.misc.WirelessNetworkManager.*;
import static tectech.thing.casing.TTCasingsContainer.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.api.IItemVault;
import com.science.gtnl.common.machine.hatch.VaultPortHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;

import appeng.api.AEApi;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.block.BlockQuantumGlass;

public class SingularityDataHub extends MultiMachineBase<SingularityDataHub>
    implements ISurvivalConstructable, IItemVault {

    public static long MAX_DISTINCT_ITEMS = Long.MAX_VALUE - 1;
    public static long MAX_DISTINCT_FLUIDS = Long.MAX_VALUE - 1;

    public static BigInteger MAX_CAPACITY_ITEM = BigInteger.valueOf(MAX_DISTINCT_FLUIDS)
        .multiply(BigInteger.valueOf(MAX_DISTINCT_ITEMS));
    public static BigInteger MAX_CAPACITY_FLUID = BigInteger.valueOf(MAX_DISTINCT_FLUIDS)
        .multiply(BigInteger.valueOf(MAX_DISTINCT_FLUIDS));

    public long capacityPerItem = Long.MAX_VALUE;
    public long capacityPerFluid = Long.MAX_VALUE;

    public boolean wirelessMode = false;
    public boolean locked = true;
    @Setter
    @Getter
    public boolean doVoidExcess = false;
    public VaultPortHatch portHatch = null;
    public UUID ownerUUID;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SDH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/singularity_data_hub";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SDH_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 15;
    private static final int DEPTH_OFF_SET = 0;

    public static NumberFormat nf = NumberFormat.getNumberInstance();

    public IItemList<IAEItemStack> STORE_ITEM = AEApi.instance()
        .storage()
        .createItemList();

    public IItemList<IAEFluidStack> STORE_FLUID = AEApi.instance()
        .storage()
        .createFluidList();

    public SingularityDataHub(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SingularityDataHub(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SingularityDataHub(super.mName);
    }

    @Override
    public long maxItemCount() {
        return MAX_DISTINCT_ITEMS;
    }

    @Override
    public long maxFluidCount() {
        return MAX_DISTINCT_FLUIDS;
    }

    @Override
    public boolean hasItem() {
        return true;
    }

    @Override
    public boolean hasFluid() {
        return true;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        if (checkStructure(true)) {
            this.mStartUpCheck = -1;
            this.mUpdate = 200;
        }
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        super.onFirstTick(aBaseMetaTileEntity);
    }

    @Override
    public void onBlockDestroyed() {
        if (portHatch != null) {
            portHatch.unbind();
        }
        super.onBlockDestroyed();
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = TierEU.RECIPE_MAX / 20;
        mMaxProgresstime = 20;

        ArrayList<ItemStack> inputItems = getStoredInputs();
        ArrayList<FluidStack> inputFluids = getStoredFluids();

        if (!inputItems.isEmpty()) {
            for (ItemStack aItem : inputItems) {
                ItemStack toDeplete = aItem.copy();
                toDeplete.stackSize = this.injectItems(aItem, true);
                depleteInput(toDeplete);
            }
        }

        if (!inputFluids.isEmpty()) {
            for (FluidStack aFluid : inputFluids) {
                FluidStack toDeplete = aFluid.copy();
                toDeplete.amount = this.injectFluids(aFluid, true);
                depleteInput(toDeplete, false);
            }
        }

        if (wirelessMode && addEUToGlobalEnergyMap(ownerUUID, -TierEU.RECIPE_MAX)) {
            lEUt = 0;
        } else if (this.lEUt > 0) this.lEUt = -this.lEUt;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        Map<GTUtility.ItemId, ItemStack> inputsFromME = new HashMap<>();
        for (MTEHatchInputBus tHatch : validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(GTUtility.ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;

    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide()) {
            this.locked = !aBaseMetaTileEntity.isActive();
        }
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.setDoVoidExcess(!doVoidExcess);
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("Info_SingularityDataHub_AutoVoiding") + doVoidExcess);
    }

    @Override
    public IStructureDefinition<SingularityDataHub> getStructureDefinition() {
        return StructureDefinition.<SingularityDataHub>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(BlockLoader.metaCasing, 18))
            .addElement(
                'B',
                ofChain(
                    buildHatchAdder(SingularityDataHub.class).atLeast(InputBus, InputHatch, Energy.or(ExoticEnergy))
                        .dot(1)
                        .casingIndex(getCasingTextureID())
                        .build(),
                    buildHatchAdder(SingularityDataHub.class).hatchClass(VaultPortHatch.class)
                        .shouldReject(t -> t.portHatch != null)
                        .adder(SingularityDataHub::addPortBusToMachineList)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    onElementPass(x -> x.mCountCasing++, ofBlock(sBlockCasings1, 12))))
            .addElement('C', ofBlock(sBlockCasings1, 13))
            .addElement('D', ofBlock(sBlockCasings1, 14))
            .addElement('E', ofBlock(sBlockCasings10, 7))
            .addElement('F', ofBlock(sBlockCasings9, 14))
            .addElement('G', ofBlock(sBlockCasings9, 15))
            .addElement('H', ofBlock(sBlockCasingsTT, 6))
            .addElement('I', ofBlock(sBlockGlass1, 3))
            .addElement('J', ofBlock(BlockQuantumGlass.INSTANCE, 0))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 100;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        wirelessMode = mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
        if (portHatch.controller == null) portHatch.bind(this);
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && portHatch != null;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        wirelessMode = false;
        if (portHatch != null) {
            portHatch = null;
        }
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings1, 12);
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SingularityDataHubRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SingularityDataHub_07"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(15, 31, 15, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SingularityDataHub_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_SingularityDataHub_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_SINGULARITY_DATA_HUB)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public boolean supportsPowerPanel() {
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 0;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> ll = new ArrayList<>();
        ll.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_SingularityDataHub_StoredItems")
                + EnumChatFormatting.RESET);

        int i = 0;
        for (IAEItemStack tank : STORE_ITEM) {
            String localizedName = Objects.requireNonNull(
                tank.getItem()
                    .getItemStackDisplayName(tank.getItemStack()));
            String amount = nf.format(tank.getStackSize());
            String percentage = capacityPerItem > 0 ? String.valueOf(tank.getStackSize() * 100 / capacityPerItem) : "";
            ll.add(MessageFormat.format("{0} - {1}: {2} ({3}%)", i++, localizedName, amount, percentage));
            if (i >= 32) break;
        }

        ll.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_SingularityDataHub_StoredFluids")
                + EnumChatFormatting.RESET);

        int j = 0;
        for (IAEFluidStack tank : STORE_FLUID) {
            String localizedName = Objects.requireNonNull(
                tank.getFluid()
                    .getLocalizedName(tank.getFluidStack()));
            String amount = nf.format(tank.getStackSize());
            String percentage = capacityPerFluid > 0 ? String.valueOf(tank.getStackSize() * 100 / capacityPerFluid)
                : "";
            ll.add(MessageFormat.format("{0} - {1}: {2} ({3}%)", j++, localizedName, amount, percentage));
            if (j >= 32) break;
        }

        ll.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_SingularityDataHub_OperationalData")
                + EnumChatFormatting.RESET);

        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_ItemUsed", nf.format(getItemStoredAmount())));
        ll.add(
            StatCollector.translateToLocalFormatted("Info_SingularityDataHub_ItemTotal", nf.format(MAX_CAPACITY_ITEM)));
        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_PerItemCapacity", nf.format(capacityPerItem)));
        ll.add(
            StatCollector.translateToLocalFormatted("Info_SingularityDataHub_ItemUsedTypes", nf.format(itemsCount())));
        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_ItemTotalTypes", nf.format(maxItemCount())));

        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_FluidUsed", nf.format(getFluidStoredAmount())));
        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_FluidTotal", nf.format(MAX_CAPACITY_FLUID)));
        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_PerFluidCapacity", nf.format(capacityPerFluid)));
        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_FluidUsedTypes", nf.format(fluidsCount())));
        ll.add(
            StatCollector
                .translateToLocalFormatted("Info_SingularityDataHub_FluidTotalTypes", nf.format(maxFluidCount())));

        ll.add(StatCollector.translateToLocalFormatted("Info_SingularityDataHub_RunningCost", getActualEnergyUsage()));
        ll.add(StatCollector.translateToLocalFormatted("Info_SingularityDataHub_AutoVoiding", doVoidExcess));
        if (wirelessMode)
            ll.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
        ll.add(EnumChatFormatting.STRIKETHROUGH + "---------------------------------------------");

        return ll.toArray(new String[0]);
    }

    @Override
    public long getActualEnergyUsage() {
        return wirelessMode ? TierEU.RECIPE_MAX / 20 : super.getActualEnergyUsage();
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        aNBT.setBoolean("doVoidExcess", doVoidExcess);
        aNBT.setBoolean("locked", locked);

        String uuid = Utils.ensureUUID(aNBT);

        NBTTagCompound storeRoot = new NBTTagCompound();
        NBTTagList itemNbt = new NBTTagList();
        for (IAEItemStack aeItem : STORE_ITEM) {
            NBTTagCompound nbt = new NBTTagCompound();
            aeItem.writeToNBT(nbt);
            itemNbt.appendTag(nbt);
        }
        NBTTagList fluidNbt = new NBTTagList();
        for (IAEFluidStack aeFluid : STORE_FLUID) {
            NBTTagCompound nbt = new NBTTagCompound();
            aeFluid.writeToNBT(nbt);
            fluidNbt.appendTag(nbt);
        }
        storeRoot.setTag("STORE_ITEM", itemNbt);
        storeRoot.setTag("STORE_FLUID", fluidNbt);

        File worldDir = DimensionManager.getCurrentSaveRootDirectory();
        File dataDir = new File(worldDir, "data");
        if (!dataDir.exists()) dataDir.mkdirs();

        File storeFile = new File(dataDir, "ItemVault_" + uuid + ".dat");
        try {
            CompressedStreamTools.safeWrite(storeRoot, storeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setBoolean("wirelessMode", wirelessMode);
        aNBT.setBoolean("doVoidExcess", doVoidExcess);
        aNBT.setBoolean("locked", locked);
        Utils.ensureUUID(aNBT);
        NBTTagList itemNbt = new NBTTagList();
        aNBT.setTag("STORE_ITEM", itemNbt);
        NBTTagList fluidNbt = new NBTTagList();
        aNBT.setTag("STORE_FLUID", fluidNbt);
        for (IAEItemStack aeItem : STORE_ITEM) {
            var nbt = new NBTTagCompound();
            aeItem.writeToNBT(nbt);
            itemNbt.appendTag(nbt);
        }
        for (IAEFluidStack aeFluid : STORE_FLUID) {
            var nbt = new NBTTagCompound();
            aeFluid.writeToNBT(nbt);
            fluidNbt.appendTag(nbt);
        }
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        this.setDoVoidExcess(aNBT.getBoolean("doVoidExcess"));
        this.locked = aNBT.getBoolean("locked");
        wirelessMode = aNBT.getBoolean("wirelessMode");
        if (aNBT.hasKey("storeUUID")) {
            String uuid = aNBT.getString("storeUUID");
            try {
                File worldDir = DimensionManager.getCurrentSaveRootDirectory();
                File dataDir = new File(worldDir, "data");
                File vaultFile = new File(dataDir, "ItemVault_" + uuid + ".dat");

                if (vaultFile.exists()) {
                    NBTTagCompound fileNBT = CompressedStreamTools.read(vaultFile);
                    NBTTagList itemNbt = fileNBT.getTagList("STORE_ITEM", 10);
                    NBTTagList fluidNbt = fileNBT.getTagList("STORE_FLUID", 10);

                    for (int i = 0; i < itemNbt.tagCount(); i++) {
                        STORE_ITEM.add(AEItemStack.loadItemStackFromNBT(itemNbt.getCompoundTagAt(i)));
                    }

                    for (int i = 0; i < fluidNbt.tagCount(); i++) {
                        STORE_FLUID.add(AEFluidStack.loadFluidStackFromNBT(fluidNbt.getCompoundTagAt(i)));
                    }

                    if (!vaultFile.delete()) {
                        System.err.println("Warning: Failed to delete vault file " + vaultFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        NBTTagList itemNbt = aNBT.getTagList("STORE_ITEM", 10);
        if (itemNbt != null) {
            for (int i = 0; i < itemNbt.tagCount(); i++) {
                STORE_ITEM.add(AEItemStack.loadItemStackFromNBT(itemNbt.getCompoundTagAt(i)));
            }
        }
        NBTTagList fluidNbt = aNBT.getTagList("STORE_FLUID", 10);
        if (fluidNbt != null) {
            for (int i = 0; i < fluidNbt.tagCount(); i++) {
                STORE_FLUID.add(AEFluidStack.loadFluidStackFromNBT(fluidNbt.getCompoundTagAt(i)));
            }
        }
        super.loadNBTData(aNBT);
    }

    @Override
    public int injectItems(ItemStack aItem, boolean doInput) {
        if (locked) return 0;
        if (STORE_ITEM.size() >= MAX_DISTINCT_ITEMS) return 0;
        var aeItem = getStoredItem(aItem);
        long size = aeItem == null ? 0 : aeItem.getStackSize();
        if (size >= capacityPerItem) return doVoidExcess ? aItem.stackSize : 0;
        if (capacityPerItem - size < aItem.stackSize) {
            if (doInput) {
                if (aeItem == null) {
                    STORE_ITEM.addStorage(
                        AEItemStack.create(aItem)
                            .setStackSize(capacityPerItem - size));
                } else {
                    aeItem.setStackSize(capacityPerItem);
                }
                portHatch.postUpdateItem(aItem, capacityPerItem - size);
            }
            return doVoidExcess ? aItem.stackSize : (int) (capacityPerItem - size);
        } else {
            if (doInput) {
                if (aeItem == null) {
                    STORE_ITEM.addStorage(AEItemStack.create(aItem));
                } else {
                    aeItem.setStackSize(size + aItem.stackSize);
                }
                portHatch.postUpdateItem(aItem, aItem.stackSize);
            }
            return aItem.stackSize;
        }
    }

    @Override
    public long injectItems(IAEItemStack aItem, boolean doInput) {
        if (locked) return 0;
        if (STORE_ITEM.size() >= MAX_DISTINCT_ITEMS) return 0;
        var aeItem = getStoredItem(aItem.getItemStack());
        long size = aeItem == null ? 0 : aeItem.getStackSize();
        if (size >= capacityPerItem) return doVoidExcess ? aItem.getStackSize() : 0;
        if (capacityPerItem - size < aItem.getStackSize()) {
            if (doInput) {
                if (aeItem == null) {
                    STORE_ITEM.addStorage(
                        aItem.copy()
                            .setStackSize(capacityPerItem - size));
                } else {
                    aeItem.setStackSize(capacityPerItem);
                }
                portHatch.postUpdateItem(aItem.getItemStack(), capacityPerItem - size);
            }
            return doVoidExcess ? aItem.getStackSize() : (int) (capacityPerItem - size);
        } else {
            if (doInput) {
                if (aeItem == null) {
                    STORE_ITEM.addStorage(aItem);
                } else {
                    aeItem.setStackSize(size + aItem.getStackSize());
                }
                portHatch.postUpdateItem(aItem.getItemStack(), aItem.getStackSize());
            }
            return aItem.getStackSize();
        }
    }

    @Override
    public int injectFluids(FluidStack aFluid, boolean doInput) {
        if (locked) return 0;
        if (STORE_FLUID.size() >= MAX_DISTINCT_FLUIDS) return 0;
        var aeFluid = getStoredFluid(aFluid);
        long size = aeFluid == null ? 0 : aeFluid.getStackSize();
        if (size >= capacityPerFluid) return doVoidExcess ? aFluid.amount : 0;
        if (capacityPerFluid - size < aFluid.amount) {
            if (doInput) {
                if (aeFluid == null) {
                    STORE_FLUID.addStorage(
                        AEFluidStack.create(aFluid)
                            .setStackSize(capacityPerFluid - size));
                } else {
                    aeFluid.setStackSize(capacityPerFluid);
                }
                portHatch.postUpdateFluid(aFluid, capacityPerFluid - size);
            }
            return doVoidExcess ? aFluid.amount : (int) (capacityPerFluid - size);
        } else {
            if (doInput) {
                if (aeFluid == null) {
                    STORE_FLUID.addStorage(AEFluidStack.create(aFluid));
                } else {
                    aeFluid.setStackSize(size + aFluid.amount);
                }
                portHatch.postUpdateFluid(aFluid, capacityPerFluid - aFluid.amount);
            }
            return aFluid.amount;
        }
    }

    @Override
    public long injectFluids(IAEFluidStack aFluid, boolean doInput) {
        if (locked) return 0;
        if (STORE_FLUID.size() >= MAX_DISTINCT_FLUIDS) return 0;
        var aeFluid = getStoredFluid(aFluid.getFluidStack());
        long size = aeFluid == null ? 0 : aeFluid.getStackSize();
        if (size >= capacityPerFluid) return doVoidExcess ? aFluid.getStackSize() : 0;
        if (capacityPerFluid - size < aFluid.getStackSize()) {
            if (doInput) {
                if (aeFluid == null) {
                    STORE_FLUID.addStorage(
                        AEFluidStack.create(aFluid)
                            .setStackSize(capacityPerFluid - size));
                } else {
                    aeFluid.setStackSize(capacityPerFluid);
                }
                portHatch.postUpdateFluid(aFluid.getFluidStack(), capacityPerFluid - size);
            }
            return doVoidExcess ? aFluid.getStackSize() : capacityPerFluid - size;
        } else {
            if (doInput) {
                if (aeFluid == null) {
                    STORE_FLUID.addStorage(aFluid);
                } else {
                    aeFluid.setStackSize(size + aFluid.getStackSize());
                }
                portHatch.postUpdateFluid(aFluid.getFluidStack(), aFluid.getStackSize());
            }
            return aFluid.getStackSize();
        }
    }

    @Override
    public long extractItems(IAEItemStack aItem, boolean doOutput) {
        if (locked) return 0;
        var aeItem = getStoredItem(aItem.getItemStack());
        if (aeItem == null) return 0;
        long storedSize = aeItem.getStackSize();
        long requestSize = aItem.getStackSize();
        if (storedSize > requestSize) {
            if (doOutput) {
                aeItem.setStackSize(storedSize - requestSize);
                portHatch.postUpdateItem(aItem.getItemStack(), -requestSize);
            }
            return requestSize;
        } else {
            if (doOutput) {
                aeItem.setStackSize(0);
                portHatch.postUpdateItem(aItem.getItemStack(), -storedSize);
            }
            return storedSize;
        }
    }

    @Override
    public long extractFluids(IAEFluidStack aFluid, boolean doOutput) {
        if (locked) return 0;
        var aeFluid = getStoredFluid(aFluid.getFluidStack());
        if (aeFluid == null) return 0;
        long storedSize = aeFluid.getStackSize();
        long requestSize = aFluid.getStackSize();
        if (storedSize > requestSize) {
            if (doOutput) {
                aeFluid.setStackSize(storedSize - requestSize);
                portHatch.postUpdateFluid(aFluid.getFluidStack(), -requestSize);
            }
            return requestSize;
        } else {
            if (doOutput) {
                aeFluid.setStackSize(0);
                portHatch.postUpdateFluid(aFluid.getFluidStack(), -storedSize);
            }
            return storedSize;
        }
    }

    @Override
    public long itemsCount() {
        return STORE_ITEM.size();
    }

    @Override
    public long fluidsCount() {
        return STORE_FLUID.size();
    }

    @Override
    public IAEItemStack getStoredItem(@Nullable ItemStack aItem) {
        if (aItem == null) return null;
        return STORE_ITEM.findPrecise(AEItemStack.create(aItem));
    }

    @Override
    public IAEFluidStack getStoredFluid(@Nullable FluidStack aFluid) {
        if (aFluid == null) return null;
        return STORE_FLUID.findPrecise(AEFluidStack.create(aFluid));
    }

    @Override
    public boolean containsItems(ItemStack aItem) {
        return getStoredItem(aItem) != null;
    }

    @Override
    public boolean containsFluids(FluidStack aFluid) {
        return getStoredFluid(aFluid) != null;
    }

    public BigInteger getItemStoredAmount() {
        BigInteger amount = BigInteger.ZERO;
        for (IAEItemStack item : STORE_ITEM) {
            amount = amount.add(BigInteger.valueOf(item.getStackSize()));
        }
        return amount;
    }

    public BigInteger getFluidStoredAmount() {
        BigInteger amount = BigInteger.ZERO;
        for (IAEFluidStack fluid : STORE_FLUID) {
            amount = amount.add(BigInteger.valueOf(fluid.getStackSize()));
        }
        return amount;
    }

    @Override
    public IItemList<IAEItemStack> getStoreItems() {
        return STORE_ITEM;
    }

    @Override
    public IItemList<IAEFluidStack> getStoreFluids() {
        return STORE_FLUID;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {}

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {}

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

    public boolean addPortBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof VaultPortHatch vaultPortHatch) {
                if (portHatch != null) return false;
                portHatch = vaultPortHatch;
                portHatch.updateTexture(aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }
}
