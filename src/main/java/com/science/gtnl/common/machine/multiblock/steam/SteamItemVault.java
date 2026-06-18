package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.IItemVault;
import com.science.gtnl.common.machine.hatch.VaultPortHatch;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;

import appeng.api.AEApi;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class SteamItemVault extends SteamMultiMachineBase<SteamItemVault>
    implements ISurvivalConstructable, IItemVault {

    public static final long MAX_DISTINCT_ITEMS = 1024;
    public static final BigInteger MAX_CAPACITY_ITEM = BigInteger.valueOf(640000)
        .multiply(BigInteger.valueOf(MAX_DISTINCT_ITEMS));
    public static final NumberFormat NF = NumberFormat.getNumberInstance();

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SIV_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_item_vault";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(SIV_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 0;

    public BigInteger capacityItem = MAX_CAPACITY_ITEM;
    public long capacityPerItem = capacityItem.divide(BigInteger.valueOf(MAX_DISTINCT_ITEMS))
        .longValue();
    public boolean locked = true;

    @Setter
    @Getter
    public boolean doVoidExcess = false;

    public VaultPortHatch portHatch = null;
    public IItemList<IAEItemStack> STORE_ITEM = AEApi.instance()
        .storage()
        .createItemList();

    public SteamItemVault(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SteamItemVault(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamItemVault(this.mName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        if (checkStructure(true, getBaseMetaTileEntity())) {
            this.mStartUpCheck = -1;
            this.mUpdate = 200;
        }
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide()) {
            this.locked = !aBaseMetaTileEntity.isActive();
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        if (portHatch != null) {
            portHatch = null;
        }
    }

    @Override
    public IStructureDefinition<SteamItemVault> getStructureDefinition() {
        return StructureDefinition.<SteamItemVault>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(SHAPE))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 29))
            .addElement('B', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement(
                'C',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamItemVault.class).casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamItemVault.class).casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamItemVault.class).casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    GTStructureUtility.buildHatchAdder(SteamItemVault.class)
                        .hatchClass(VaultPortHatch.class)
                        .shouldReject(t -> t.portHatch != null)
                        .adder(SteamItemVault::addPortBusToMachineList)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    GTStructureUtility.buildHatchAdder(SteamItemVault.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.InputHatch)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility
                        .onElementPass(x -> x.mCountCasing++, StructureUtility.ofBlock(BlockLoader.metaCasing02, 0))))
            .addElement('D', GTStructureUtility.chainAllGlasses())
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            return;
        }
        checkHatch(errors);
        if (portHatch != null && portHatch.controller == null) {
            portHatch.bind(this);
        }
        checkCasingMin(errors, mCountCasing, 30);
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = 128;
        mMaxProgresstime = 20;

        ArrayList<ItemStack> inputItems = getStoredInputs();
        if (!inputItems.isEmpty()) {
            for (ItemStack aItem : inputItems) {
                ItemStack toDeplete = aItem.copy();
                toDeplete.stackSize = this.injectItems(aItem, true);
                depleteInput(toDeplete);
            }
        }

        if ((!this.mOutputBusses.isEmpty() || !this.mSteamOutputs.isEmpty()) && STORE_ITEM.getFirstItem() != null) {
            IAEItemStack stack = STORE_ITEM.getFirstItem()
                .copy();
            stack.setStackSize(stack.getStackSize() - this.tryAddOutput(stack.getItemStack()).stackSize);
            if (stack.getStackSize() > 0) {
                this.extractItems(stack, true);
            }
        }

        if (this.lEUt > 0) {
            this.lEUt = -this.lEUt;
        }

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        Map<ItemId, ItemStack> inputsFromME = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInputBus tHatch : GTUtility.validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            assert tileEntity != null;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        inputsFromME.put(ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        for (MTEHatchSteamBusInput tHatch : GTUtility.validMTEList(mSteamInputs)) {
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            assert tileEntity != null;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    rList.add(itemStack);
                }
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    @Override
    public int getCasingTextureID() {
        return GTUtility.getTextureId((byte) 116, (byte) 32);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) {
                return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                    TextureFactory.builder()
                        .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_ITEM_VAULT)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamItemVaultRecipeType");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamItemVaultRecipeType"))
            .addInfo(StatCollector.translateToLocalFormatted("Tooltip_SteamItemVault_00", MAX_DISTINCT_ITEMS))
            .addInfo(StatCollector.translateToLocalFormatted("Tooltip_SteamItemVault_01", MAX_DISTINCT_ITEMS))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamItemVault_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamItemVault_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamItemVault_04"))
            .addInfo(StatCollector.translateToLocalFormatted("Tooltip_SteamItemVault_05", NF.format(MAX_CAPACITY_ITEM)))
            .beginStructureBlock(7, 11, 7, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SteamItemVault_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SteamItemVault_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>();
        info.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_SteamItemVault_StoredItems")
                + EnumChatFormatting.RESET);

        int index = 0;
        for (IAEItemStack tank : STORE_ITEM) {
            String localizedName = Objects.requireNonNull(
                tank.getItem()
                    .getItemStackDisplayName(tank.getItemStack()));
            String amount = NF.format(tank.getStackSize());
            String percentage = capacityPerItem > 0 ? String.valueOf(tank.getStackSize() * 100 / capacityPerItem) : "";
            info.add(MessageFormat.format("{0} - {1}: {2} ({3}%)", index++, localizedName, amount, percentage));
            if (index >= 32) {
                break;
            }
        }

        info.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_SteamItemVault_OperationalData")
                + EnumChatFormatting.RESET);
        info.add(
            StatCollector.translateToLocalFormatted("Info_SteamItemVault_ItemUsed", NF.format(getItemStoredAmount())));
        info.add(StatCollector.translateToLocalFormatted("Info_SteamItemVault_ItemTotal", NF.format(capacityItem)));
        info.add(
            StatCollector.translateToLocalFormatted("Info_SteamItemVault_PerItemCapacity", NF.format(capacityPerItem)));
        info.add(StatCollector.translateToLocalFormatted("Info_SteamItemVault_ItemUsedTypes", NF.format(itemsCount())));
        info.add(
            StatCollector.translateToLocalFormatted("Info_SteamItemVault_ItemTotalTypes", NF.format(maxItemCount())));
        info.add(StatCollector.translateToLocalFormatted("Info_SteamItemVault_RunningCost", getActualEnergyUsage()));
        info.add(StatCollector.translateToLocalFormatted("Info_SteamItemVault_AutoVoiding", doVoidExcess));
        info.add(EnumChatFormatting.STRIKETHROUGH + "---------------------------------------------");
        return info.toArray(new String[0]);
    }

    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        this.setDoVoidExcess(!doVoidExcess);
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("Info_SteamItemVault_AutoVoiding") + doVoidExcess);
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        aNBT.setByteArray("capacityItem", capacityItem.toByteArray());
        aNBT.setBoolean("doVoidExcess", doVoidExcess);
        aNBT.setBoolean("locked", locked);

        String uuid = ensureUUID(aNBT);
        NBTTagCompound storeRoot = new NBTTagCompound();
        NBTTagList itemNbt = new NBTTagList();
        for (IAEItemStack aeItem : STORE_ITEM) {
            NBTTagCompound nbt = new NBTTagCompound();
            aeItem.writeToNBT(nbt);
            itemNbt.appendTag(nbt);
        }
        storeRoot.setTag("STORE_ITEM", itemNbt);

        File worldDir = DimensionManager.getCurrentSaveRootDirectory();
        File dataDir = new File(worldDir, "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File storeFile = new File(dataDir, "ItemVault_" + uuid + ".dat");
        try {
            CompressedStreamTools.safeWrite(storeRoot, storeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setByteArray("capacityItem", capacityItem.toByteArray());
        aNBT.setBoolean("doVoidExcess", doVoidExcess);
        aNBT.setBoolean("locked", locked);
        ensureUUID(aNBT);
        NBTTagList itemNbt = new NBTTagList();
        aNBT.setTag("STORE_ITEM", itemNbt);
        for (IAEItemStack aeItem : STORE_ITEM) {
            NBTTagCompound nbt = new NBTTagCompound();
            aeItem.writeToNBT(nbt);
            itemNbt.appendTag(nbt);
        }
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        this.setCapacityItem(new BigInteger(aNBT.getByteArray("capacityItem")));
        this.setDoVoidExcess(aNBT.getBoolean("doVoidExcess"));
        this.locked = aNBT.getBoolean("locked");
        if (aNBT.hasKey("storeUUID")) {
            String uuid = aNBT.getString("storeUUID");
            try {
                File worldDir = DimensionManager.getCurrentSaveRootDirectory();
                File dataDir = new File(worldDir, "data");
                File vaultFile = new File(dataDir, "ItemVault_" + uuid + ".dat");

                if (vaultFile.exists()) {
                    NBTTagCompound fileNBT = CompressedStreamTools.read(vaultFile);
                    NBTTagList itemNbt = fileNBT.getTagList("STORE_ITEM", 10);
                    for (int i = 0; i < itemNbt.tagCount(); i++) {
                        STORE_ITEM.add(AEItemStack.loadItemStackFromNBT(itemNbt.getCompoundTagAt(i)));
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
        super.loadNBTData(aNBT);
    }

    @Override
    public long maxItemCount() {
        return MAX_DISTINCT_ITEMS;
    }

    @Override
    public boolean hasItem() {
        return true;
    }

    @Override
    public boolean hasFluid() {
        return false;
    }

    @Override
    protected boolean supportsCraftingMEBuffer() {
        return false;
    }

    @Override
    public boolean supportsPowerPanel() {
        return false;
    }

    @Override
    public int getTierRecipes() {
        return 0;
    }

    @Override
    public int injectItems(ItemStack aItem, boolean doInput) {
        if (locked) return 0;
        if (STORE_ITEM.size() >= MAX_DISTINCT_ITEMS) return 0;
        IAEItemStack aeItem = getStoredItem(aItem);
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
                if (portHatch != null) {
                    portHatch.postUpdateItem(aItem, capacityPerItem - size);
                }
            }
            return doVoidExcess ? aItem.stackSize : (int) (capacityPerItem - size);
        }

        if (doInput) {
            if (aeItem == null) {
                STORE_ITEM.addStorage(AEItemStack.create(aItem));
            } else {
                aeItem.setStackSize(size + aItem.stackSize);
            }
            if (portHatch != null) {
                portHatch.postUpdateItem(aItem, aItem.stackSize);
            }
        }
        return aItem.stackSize;
    }

    @Override
    public long injectItems(IAEItemStack aItem, boolean doInput) {
        if (locked) return 0;
        if (STORE_ITEM.size() >= MAX_DISTINCT_ITEMS) return 0;
        IAEItemStack aeItem = getStoredItem(aItem.getItemStack());
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
                if (portHatch != null) {
                    portHatch.postUpdateItem(aItem.getItemStack(), capacityPerItem - size);
                }
            }
            return doVoidExcess ? aItem.getStackSize() : (int) (capacityPerItem - size);
        }

        if (doInput) {
            if (aeItem == null) {
                STORE_ITEM.addStorage(aItem);
            } else {
                aeItem.setStackSize(size + aItem.getStackSize());
            }
            if (portHatch != null) {
                portHatch.postUpdateItem(aItem.getItemStack(), aItem.getStackSize());
            }
        }
        return aItem.getStackSize();
    }

    @Override
    public long extractItems(IAEItemStack aItem, boolean doOutput) {
        if (locked) return 0;
        IAEItemStack aeItem = getStoredItem(aItem.getItemStack());
        if (aeItem == null) return 0;
        long storedSize = aeItem.getStackSize();
        long requestSize = aItem.getStackSize();
        if (storedSize > requestSize) {
            if (doOutput) {
                aeItem.setStackSize(storedSize - requestSize);
                if (portHatch != null) {
                    portHatch.postUpdateItem(aItem.getItemStack(), -requestSize);
                }
            }
            return requestSize;
        }

        if (doOutput) {
            aeItem.setStackSize(0);
            if (portHatch != null) {
                portHatch.postUpdateItem(aItem.getItemStack(), -storedSize);
            }
        }
        return storedSize;
    }

    @Override
    public long itemsCount() {
        return STORE_ITEM.size();
    }

    @Override
    public IAEItemStack getStoredItem(@Nullable ItemStack aItem) {
        if (aItem == null) return null;
        return STORE_ITEM.findPrecise(AEItemStack.create(aItem));
    }

    @Override
    public boolean containsItems(ItemStack aItem) {
        return getStoredItem(aItem) != null;
    }

    @Override
    public IItemList<IAEItemStack> getStoreItems() {
        return STORE_ITEM;
    }

    public void setCapacityItem(BigInteger capacityItem) {
        if (capacityItem.compareTo(MAX_CAPACITY_ITEM) > 0) {
            this.capacityItem = MAX_CAPACITY_ITEM;
            this.capacityPerItem = Long.MAX_VALUE;
        } else {
            this.capacityItem = capacityItem;
            this.capacityPerItem = capacityItem.divide(BigInteger.valueOf(MAX_DISTINCT_ITEMS))
                .longValue();
        }
    }

    public BigInteger getItemStoredAmount() {
        BigInteger amount = BigInteger.ZERO;
        for (IAEItemStack item : STORE_ITEM) {
            amount = amount.add(BigInteger.valueOf(item.getStackSize()));
        }
        return amount;
    }

    private String ensureUUID(NBTTagCompound aNBT) {
        if (aNBT.hasKey("storeUUID")) {
            return aNBT.getString("storeUUID");
        }
        String uuid = UUID.randomUUID()
            .toString();
        aNBT.setString("storeUUID", uuid);
        return uuid;
    }

    public boolean addPortBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof VaultPortHatch vaultPortHatch) {
                if (this.portHatch != null) return false;
                this.portHatch = vaultPortHatch;
                this.portHatch.updateTexture(aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }
}
