package com.science.gtnl.common.machine.multiblock;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings10;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.api.IStackVault;
import com.science.gtnl.common.gui.modularui.SingularityDataHubGui;
import com.science.gtnl.common.gui.modularui.VaultTypeCountFormatter;
import com.science.gtnl.common.machine.hatch.VaultPortHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.enums.BlockIcons;

import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.TranslatableText;
import gregtech.api.util.GTUtility;
import gregtech.api.util.GTUtility.ItemId;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gtPlusPlus.core.block.ModBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.BlockGTCasingsTT;

public class SingularityDataHub extends MultiMachineBase<SingularityDataHub>
    implements ISurvivalConstructable, IStackVault {

    private static final TranslatableText VAULT_PORT_HATCH_NAME = TranslatableText.lang("VaultPortHatch");

    public static long MAX_DISTINCT_ITEMS = Long.MAX_VALUE - 1;
    public static long MAX_DISTINCT_FLUIDS = Long.MAX_VALUE - 1;

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

    private static final String STORE_STACKS_KEY = "STORE_STACKS";
    private static final char GUI_PAYLOAD_SEPARATOR = '\t';

    public Map<IAEStackType<?>, IItemList<?>> STORE_STACKS = new IdentityHashMap<>();
    private String typeCountPayloadForGui = "";

    public SingularityDataHub(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SingularityDataHub(String aName) {
        super(aName);
    }

    private <T extends IAEStack<T>> IItemList<T> registerStore(IAEStackType<T> type) {
        IItemList<T> list = type.createList();
        STORE_STACKS.put(type, list);
        return list;
    }

    @SuppressWarnings("unchecked")
    private <T extends IAEStack<T>> IItemList<T> getOrCreateStore(IAEStackType<T> type) {
        IItemList<T> store = (IItemList<T>) STORE_STACKS.get(type);
        if (store == null) {
            store = registerStore(type);
        }
        return store;
    }

    @Override
    public IStructureDefinition<SingularityDataHub> getStructureDefinition() {
        return StructureDefinition.<SingularityDataHub>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 18))
            .addElement(
                'B',
                StructureUtility.ofChain(
                    buildHatchAdder(SingularityDataHub.class)
                        .atLeast(
                            HatchElement.InputBus,
                            HatchElement.InputHatch,
                            HatchElement.Energy.or(HatchElement.ExoticEnergy))
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    buildHatchAdder(SingularityDataHub.class).hatchClass(VaultPortHatch.class)
                        .shouldReject(t -> t.portHatch != null)
                        .adder(SingularityDataHub::addPortBusToMachineList)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility
                        .onElementPass(x -> x.mCountCasing++, StructureUtility.ofBlock(sBlockCasingsTT, 4))))
            .addElement('C', StructureUtility.ofBlock(sBlockCasingsTT, 0))
            .addElement('D', StructureUtility.ofBlock(ModBlocks.blockCasings3Misc, 10))
            .addElement('E', StructureUtility.ofBlock(sBlockCasings10, 7))
            .addElement('F', StructureUtility.ofBlock(sBlockCasingsTT, 8))
            .addElement('G', StructureUtility.ofBlock(sBlockCasingsTT, 4))
            .addElement('H', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('I', StructureUtility.ofBlock(BlockQuantumGlass.INSTANCE, 0))
            .build();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SingularityDataHub(super.mName);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SingularityDataHubGui(this);
    }

    @Override
    @Deprecated
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements
            .widget(
                new TextWidget()
                    .setStringSupplier(
                        () -> VaultTypeCountFormatter
                            .createTypeCountText(typeCountPayloadForGui, "Info_SingularityDataHub_TypeCount"))
                    .setDefaultColor(COLOR_TEXT_WHITE.get())
                    .setEnabled(true))
            .widget(new FakeSyncWidget.StringSyncer(this::getTypeCountPayloadForGui, this::setTypeCountPayloadFromGui));
    }

    @Override
    public boolean supportsStackType(IAEStackType<?> type) {
        return type != null && AEStackTypeRegistry.getType(type.getId()) == type;
    }

    @Override
    public Iterable<IAEStackType<?>> getSupportedStackTypes() {
        return AEStackTypeRegistry.getAllTypes();
    }

    @Override
    public <T extends IAEStack<T>> IItemList<T> getStoredStacks(IAEStackType<T> type) {
        return getOrCreateStore(type);
    }

    @Override
    public <T extends IAEStack<T>> T getStoredStack(T stack) {
        if (stack == null || !supportsStackType(stack.getStackType())) return null;
        return getOrCreateStore(stack.getStackType()).findPrecise(stack);
    }

    @Override
    public long stackTypesCount(IAEStackType<?> type) {
        IItemList<?> store = STORE_STACKS.get(type);
        return store == null ? 0 : store.size();
    }

    @Override
    public long maxStackTypes(IAEStackType<?> type) {
        if (type == ITEM_STACK_TYPE) return MAX_DISTINCT_ITEMS;
        if (type == FLUID_STACK_TYPE) return MAX_DISTINCT_FLUIDS;
        return Long.MAX_VALUE - 1;
    }

    @Override
    public long capacityPerStack(IAEStackType<?> type) {
        if (type == ITEM_STACK_TYPE) return capacityPerItem;
        if (type == FLUID_STACK_TYPE) return capacityPerFluid;
        return Long.MAX_VALUE;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        if (checkStructure(true, getBaseMetaTileEntity())) {
            mStartUpCheck = -1;
            mUpdate = 200;
        }
        ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
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
            locked = !aBaseMetaTileEntity.isActive();
        }
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        setDoVoidExcess(!doVoidExcess);
        GTUtility.sendChatTrans(aPlayer, "Info_SingularityDataHub_AutoVoiding", doVoidExcess);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 100);
        checkHatchMin(errors, VAULT_PORT_HATCH_NAME, portHatch == null ? 0 : 1, 1);
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        wirelessMode = mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
        if (portHatch != null && portHatch.controller == null) portHatch.bind(this);
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
        return BlockGTCasingsTT.textureOffset + 4;
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
    public @NotNull CheckRecipeResult checkProcessing() {
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = GTValues.VP[9] / 20L;
        mMaxProgresstime = 20;

        ArrayList<ItemStack> inputItems = getStoredInputs();
        ArrayList<FluidStack> inputFluids = getStoredFluids();

        if (!inputItems.isEmpty()) {
            for (ItemStack aItem : inputItems) {
                ItemStack toDeplete = aItem.copy();
                toDeplete.stackSize = toIntAmount(injectStack(AEItemStack.create(aItem), true));
                depleteInput(toDeplete);
            }
        }

        if (!inputFluids.isEmpty()) {
            for (FluidStack aFluid : inputFluids) {
                FluidStack toDeplete = aFluid.copy();
                toDeplete.amount = toIntAmount(injectStack(AEFluidStack.create(aFluid), true));
                depleteInput(toDeplete, false);
            }
        }

        if (wirelessMode && addEUToGlobalEnergyMap(ownerUUID, -TierEU.RECIPE_MAX)) {
            lEUt = 0;
        } else if (lEUt > 0) {
            lEUt = -lEUt;
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
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(ItemId.createNoCopy(itemStack), itemStack);
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
                    .addIcon(BlockIcons.OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_SINGULARITY_DATA_HUB)
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
    public String[] getInfoData() {
        ArrayList<String> ll = new ArrayList<>();
        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            IItemList<?> store = STORE_STACKS.get(type);
            if (store == null || store.isEmpty()) continue;
            addStoredTypeInfo(ll, type, store);
        }

        ll.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_SingularityDataHub_OperationalData")
                + EnumChatFormatting.RESET);

        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            addOperationalTypeInfo(ll, type);
        }

        ll.add(StatCollector.translateToLocalFormatted("Info_SingularityDataHub_RunningCost", getActualEnergyUsage()));
        ll.add(StatCollector.translateToLocalFormatted("Info_SingularityDataHub_AutoVoiding", doVoidExcess));
        if (wirelessMode)
            ll.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
        ll.add(EnumChatFormatting.STRIKETHROUGH + "---------------------------------------------");

        return ll.toArray(new String[0]);
    }

    private <T extends IAEStack<T>> void addStoredTypeInfo(ArrayList<String> info, IAEStackType<T> type,
        IItemList<?> store) {
        info.add(
            EnumChatFormatting.YELLOW
                + StatCollector.translateToLocalFormatted("Info_SingularityDataHub_StoredStacks", getTypeName(type))
                + EnumChatFormatting.RESET);

        int index = 0;
        IItemList<T> typedStore = getOrCreateStore(type);
        for (T stack : typedStore) {
            if (stack == null) continue;
            long capacity = capacityPerStack(type);
            String amount = formatStackAmount(type, stack.getStackSize());
            String percentage = capacity > 0 && capacity < Long.MAX_VALUE
                ? String.valueOf(stack.getStackSize() * 100 / capacity)
                : "";
            info.add(
                MessageFormat.format("{0} - {1}: {2} ({3}%)", index++, stack.getDisplayName(), amount, percentage));
            if (index >= 32) break;
        }
    }

    private void addOperationalTypeInfo(ArrayList<String> info, IAEStackType<?> type) {
        String typeName = getTypeName(type);
        info.add(
            StatCollector.translateToLocalFormatted(
                "Info_SingularityDataHub_TypeUsed",
                typeName,
                formatStackAmount(type, getStoredAmount(type))));
        info.add(
            StatCollector.translateToLocalFormatted(
                "Info_SingularityDataHub_TypeCapacity",
                typeName,
                formatStackAmount(type, maxTotalCapacity(type))));
        info.add(
            StatCollector.translateToLocalFormatted(
                "Info_SingularityDataHub_PerTypeCapacity",
                typeName,
                formatStackAmount(type, capacityPerStack(type))));
        info.add(
            StatCollector.translateToLocalFormatted(
                "Info_SingularityDataHub_TypeUsedTypes",
                typeName,
                nf.format(stackTypesCount(type))));
        info.add(
            StatCollector.translateToLocalFormatted(
                "Info_SingularityDataHub_TypeTotalTypes",
                typeName,
                nf.format(maxStackTypes(type))));
    }

    @Override
    public long getActualEnergyUsage() {
        return wirelessMode ? TierEU.RECIPE_MAX / 20 : super.getActualEnergyUsage();
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        if (doVoidExcess) {
            aNBT.setBoolean("doVoidExcess", true);
        }
        if (!locked) {
            aNBT.setBoolean("locked", false);
        }

        if (!hasStoredStacks()) return;
        String uuid = Utils.ensureUUID(aNBT);
        NBTTagCompound storeRoot = new NBTTagCompound();
        writeStoredStacks(storeRoot);

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
        if (wirelessMode) {
            aNBT.setBoolean("wirelessMode", true);
        }
        if (doVoidExcess) {
            aNBT.setBoolean("doVoidExcess", true);
        }
        if (!locked) {
            aNBT.setBoolean("locked", false);
        }
        Utils.ensureUUID(aNBT);
        writeStoredStacks(aNBT);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        if (aNBT.hasKey("doVoidExcess")) {
            this.setDoVoidExcess(aNBT.getBoolean("doVoidExcess"));
        }
        if (aNBT.hasKey("locked")) {
            this.locked = aNBT.getBoolean("locked");
        }
        if (aNBT.hasKey("wirelessMode")) {
            wirelessMode = aNBT.getBoolean("wirelessMode");
        }
        if (aNBT.hasKey("storeUUID")) {
            String uuid = aNBT.getString("storeUUID");
            try {
                File worldDir = DimensionManager.getCurrentSaveRootDirectory();
                File dataDir = new File(worldDir, "data");
                File vaultFile = new File(dataDir, "ItemVault_" + uuid + ".dat");

                if (vaultFile.exists()) {
                    NBTTagCompound fileNBT = CompressedStreamTools.read(vaultFile);
                    readStoredStacks(fileNBT);

                    if (!vaultFile.delete()) {
                        System.err.println("Warning: Failed to delete vault file " + vaultFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        readStoredStacks(aNBT);
        super.loadNBTData(aNBT);
    }

    private void writeStoredStacks(NBTTagCompound tag) {
        NBTTagList genericStores = new NBTTagList();
        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            writeStore(genericStores, type);
        }
        if (genericStores.tagCount() > 0) {
            tag.setTag(STORE_STACKS_KEY, genericStores);
        }
    }

    private <T extends IAEStack<T>> void writeStore(NBTTagList genericStores, IAEStackType<T> type) {
        IItemList<T> store = getOrCreateStore(type);
        if (store.isEmpty()) return;

        NBTTagCompound storeTag = new NBTTagCompound();
        NBTTagList stackTags = new NBTTagList();
        for (T stack : store) {
            if (stack == null) continue;
            NBTTagCompound stackTag = new NBTTagCompound();
            stack.writeToNBTGeneric(stackTag);
            stackTags.appendTag(stackTag);
        }
        storeTag.setString("Type", type.getId());
        storeTag.setTag("Stacks", stackTags);
        genericStores.appendTag(storeTag);
    }

    private void readStoredStacks(NBTTagCompound tag) {
        if (tag.hasKey(STORE_STACKS_KEY)) {
            readGenericStoredStacks(tag.getTagList(STORE_STACKS_KEY, 10));
            return;
        }
        readLegacyStoredStacks(tag);
    }

    private void readGenericStoredStacks(NBTTagList genericStores) {
        for (int i = 0; i < genericStores.tagCount(); i++) {
            NBTTagCompound storeTag = genericStores.getCompoundTagAt(i);
            NBTTagList stacks = storeTag.getTagList("Stacks", 10);
            for (int j = 0; j < stacks.tagCount(); j++) {
                IAEStack<?> stack = IAEStack.fromNBTGeneric(stacks.getCompoundTagAt(j));
                if (stack != null) {
                    addStoredStack(stack);
                }
            }
        }
    }

    private void readLegacyStoredStacks(NBTTagCompound tag) {
        NBTTagList itemNbt = tag.getTagList("STORE_ITEM", 10);
        for (int i = 0; i < itemNbt.tagCount(); i++) {
            IAEItemStack stack = AEItemStack.loadItemStackFromNBT(itemNbt.getCompoundTagAt(i));
            if (stack != null) {
                getOrCreateStore(ITEM_STACK_TYPE).add(stack);
            }
        }

        NBTTagList fluidNbt = tag.getTagList("STORE_FLUID", 10);
        for (int i = 0; i < fluidNbt.tagCount(); i++) {
            IAEFluidStack stack = AEFluidStack.loadFluidStackFromNBT(fluidNbt.getCompoundTagAt(i));
            if (stack != null) {
                getOrCreateStore(FLUID_STACK_TYPE).add(stack);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addStoredStack(IAEStack<?> stack) {
        IAEStackType<IAEStack> type = (IAEStackType<IAEStack>) stack.getStackType();
        IItemList<IAEStack> store = getOrCreateStore(type);
        store.add(stack);
    }

    @Override
    public <T extends IAEStack<T>> long injectStack(T stack, boolean doInput) {
        if (locked || stack == null || !supportsStackType(stack.getStackType())) return 0;
        IItemList<T> store = getOrCreateStore(stack.getStackType());
        T stored = store.findPrecise(stack);
        long storedSize = stored == null ? 0 : stored.getStackSize();
        long capacity = capacityPerStack(stack.getStackType());
        long inputSize = stack.getStackSize();
        if (storedSize >= capacity) return doVoidExcess ? inputSize : 0;
        if (stored == null && store.size() >= maxStackTypes(stack.getStackType())) return 0;

        long inserted = Math.min(inputSize, capacity - storedSize);
        if (doInput && inserted > 0) {
            if (stored == null) {
                store.addStorage(
                    stack.copy()
                        .setStackSize(inserted));
            } else {
                stored.setStackSize(storedSize + inserted);
            }
            if (portHatch != null) {
                portHatch.postUpdate(stack, inserted);
            }
        }
        return doVoidExcess ? inputSize : inserted;
    }

    @Override
    public <T extends IAEStack<T>> long extractStack(T stack, boolean doOutput) {
        if (locked || stack == null || !supportsStackType(stack.getStackType())) return 0;
        T stored = getStoredStack(stack);
        if (stored == null) return 0;

        long extracted = Math.min(stored.getStackSize(), stack.getStackSize());
        if (doOutput && extracted > 0) {
            stored.setStackSize(stored.getStackSize() - extracted);
            if (portHatch != null) {
                portHatch.postUpdate(stack, -extracted);
            }
        }
        return extracted;
    }

    private int toIntAmount(long amount) {
        return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
    }

    private boolean hasStoredStacks() {
        Collection<IItemList<?>> stores = STORE_STACKS.values();
        for (IItemList<?> store : stores) {
            if (store != null && !store.isEmpty()) return true;
        }
        return false;
    }

    private BigInteger getStoredAmount(IAEStackType<?> type) {
        IItemList<?> store = STORE_STACKS.get(type);
        if (store == null || store.isEmpty()) return BigInteger.ZERO;
        BigInteger amount = BigInteger.ZERO;
        for (IAEStack<?> stack : store) {
            amount = amount.add(BigInteger.valueOf(stack.getStackSize()));
        }
        return amount;
    }

    private BigInteger maxTotalCapacity(IAEStackType<?> type) {
        long stackCapacity = capacityPerStack(type);
        long maxTypes = maxStackTypes(type);
        return BigInteger.valueOf(maxTypes)
            .multiply(BigInteger.valueOf(stackCapacity));
    }

    private String formatStackAmount(IAEStackType<?> type, BigInteger amount) {
        String suffix = type.getDisplayUnit();
        String formatted = nf.format(amount);
        return suffix == null || suffix.isEmpty() ? formatted : formatted + " " + suffix;
    }

    private String formatStackAmount(IAEStackType<?> type, long amount) {
        return formatStackAmount(type, BigInteger.valueOf(amount));
    }

    private String getTypeName(IAEStackType<?> type) {
        String displayName = type.getDisplayName();
        if (displayName != null && !displayName.isEmpty() && !displayName.equals(type.getId())) {
            return displayName;
        }
        String id = type.getId();
        if (StatCollector.canTranslate(id)) {
            return StatCollector.translateToLocal(id);
        }
        if (StatCollector.canTranslate(id + ".name")) {
            return StatCollector.translateToLocal(id + ".name");
        }
        return id;
    }

    public String getTypeCountPayloadForGui() {
        StringBuilder payload = new StringBuilder();
        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            appendTypeCountPayload(payload, type);
        }
        return payload.toString();
    }

    private void appendTypeCountPayload(StringBuilder payload, IAEStackType<?> type) {
        if (type == null) return;
        if (!payload.isEmpty()) {
            payload.append('\n');
        }
        payload.append(type.getId())
            .append(GUI_PAYLOAD_SEPARATOR)
            .append(stackTypesCount(type))
            .append(GUI_PAYLOAD_SEPARATOR)
            .append(maxStackTypes(type));
    }

    public void setTypeCountPayloadFromGui(String typeCountPayloadForGui) {
        this.typeCountPayloadForGui = typeCountPayloadForGui == null ? "" : typeCountPayloadForGui;
    }

    public String getSyncedTypeCountPayloadForGui() {
        return typeCountPayloadForGui;
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
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
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
