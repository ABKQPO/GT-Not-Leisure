package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static forestry.api.apiculture.BeeManager.beeRoot;
import static gregtech.api.metatileentity.BaseTileEntity.*;
import static gregtech.api.util.GTUtility.*;
import static kubatech.api.gui.KubaTechUITextures.*;
import static kubatech.api.utils.ItemUtils.readItemStackFromNBT;
import static kubatech.api.utils.ItemUtils.writeItemStackToNBT;
import static net.minecraft.util.StatCollector.*;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.MainAxisAlignment;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ITileWithModularUI;
import com.gtnewhorizons.modularui.api.screen.ModularUIContext;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.builder.UIBuilder;
import com.gtnewhorizons.modularui.common.builder.UIInfo;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularGui;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularUIContainer;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.gui.CircularGaugeDrawable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.plugins.PluginApiculture;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.SoundResource;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import kubatech.api.DynamicInventory;

public class SteamApiaryModule extends SteamElevatorModule {

    public int mMaxSlots = 8;
    public final ArrayList<BeeSimulator> mStorage = new ArrayList<>();
    public static final int CONFIGURATION_WINDOW_ID = 10;

    public static final int MODE_PRIMARY_INPUT = 0;
    public static final int MODE_PRIMARY_OUTPUT = 1;
    public static final int MODE_PRIMARY_OPERATING = 2;
    public int mPrimaryMode = MODE_PRIMARY_INPUT;

    public HashMap<GTUtility.ItemId, Double> dropProgress = new HashMap<>();

    public SteamApiaryModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 6);
    }

    public SteamApiaryModule(String aName) {
        super(aName, 6);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamApiaryModule(this.mName);
    }

    @Override
    public int getTierRecipes() {
        return 6 + recipeOcCount;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatToPlayer(aPlayer, "Can't change mode when running !");
            return;
        }
        mPrimaryMode++;
        if (mPrimaryMode == 3) mPrimaryMode = 0;
        switch (mPrimaryMode) {
            case 0:
                GTUtility.sendChatToPlayer(aPlayer, "Changed primary mode to: Input mode");
                break;
            case 1:
                GTUtility.sendChatToPlayer(aPlayer, "Changed primary mode to: Output mode");
                break;
            case 2:
                GTUtility.sendChatToPlayer(aPlayer, "Changed primary mode to: Operating mode");
                break;
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if (getBaseMetaTileEntity().isServerSide())
            tryOutputAll(mStorage, s -> Collections.singletonList(s.queenStack));
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mMaxSlots", mMaxSlots);
        aNBT.setInteger("mPrimaryMode", mPrimaryMode);
        aNBT.setInteger("mStorageSize", mStorage.size());
        for (int i = 0; i < mStorage.size(); i++) aNBT.setTag(
            "mStorage." + i,
            mStorage.get(i)
                .toNBTTagCompound());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mMaxSlots = aNBT.getInteger("mMaxSlots");
        mPrimaryMode = aNBT.getInteger("mPrimaryMode");
        for (int i = 0, isize = aNBT.getInteger("mStorageSize"); i < isize; i++)
            mStorage.add(new SteamApiaryModule.BeeSimulator(aNBT.getCompoundTag("mStorage." + i)));
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        info.add(
            StatCollector.translateToLocal("kubatech.infodata.running_mode") + " "
                + EnumChatFormatting.GOLD
                + (mPrimaryMode == 0 ? StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.input")
                    : (mPrimaryMode == 1 ? StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.output")
                        : StatCollector.translateToLocal("kubatech.infodata.mia.running_mode.operating.normal"))));
        info.add(
            StatCollector.translateToLocalFormatted(
                "kubatech.infodata.mia.running_mode.bee_storage",
                "" + EnumChatFormatting.GOLD + mStorage.size() + EnumChatFormatting.RESET,
                (mStorage.size() > mMaxSlots ? EnumChatFormatting.DARK_RED.toString()
                    : EnumChatFormatting.GOLD.toString()) + mMaxSlots + EnumChatFormatting.RESET));
        HashMap<String, Integer> infos = new HashMap<>();
        for (int i = 0; i < mStorage.size(); i++) {
            StringBuilder builder = new StringBuilder();
            if (i > mMaxSlots) builder.append(EnumChatFormatting.DARK_RED);
            builder.append(EnumChatFormatting.GOLD);
            SteamApiaryModule.BeeSimulator beeSimulator = mStorage.get(i);
            builder.append(beeSimulator.queenStack.getDisplayName());
            infos.merge(builder.toString(), 1, Integer::sum);
        }
        infos.forEach((key, value) -> info.add("x" + value + ": " + key));

        return info.toArray(new String[0]);
    }

    @Override
    public String getMachineType() {
        return "SteamApiaryModuleRecipeType";
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamApiaryModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamApiaryModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamApiaryModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamApiaryModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamApiaryModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamApiaryModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamApiaryModule_05"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (mPrimaryMode < 2) { // input and output mode
            if (mPrimaryMode == MODE_PRIMARY_INPUT && mStorage.size() < mMaxSlots) {
                World w = getBaseMetaTileEntity().getWorld();
                ArrayList<ItemStack> inputs = getStoredInputs();
                for (ItemStack input : inputs) {
                    if (beeRoot.getType(input) == EnumBeeType.QUEEN) {
                        SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(
                            input,
                            w,
                            6 + recipeOcCount);
                        if (bs.isValid) {
                            mStorage.add(bs);
                        }
                    }
                    if (mStorage.size() >= mMaxSlots) break;
                }
                updateSlots();
            } else if (mPrimaryMode == MODE_PRIMARY_OUTPUT && !mStorage.isEmpty()) { // output mode
                tryOutputAll(mStorage, s -> Collections.singletonList(s.queenStack));
            } else {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
            mMaxProgresstime = 10;
            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            lEUt = 0;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        } else if (mPrimaryMode == MODE_PRIMARY_OPERATING) {
            if (mMaxSlots > 0 && !mStorage.isEmpty()) {

                if (mStorage.size() > mMaxSlots) return SimpleCheckRecipeResult.ofFailure("MegaApiary_slotoverflow");

                int maxConsume = mStorage.size() * 40;
                int toConsume = maxConsume;
                ArrayList<ItemStack> inputs = getStoredInputs();

                for (ItemStack input : inputs) {
                    if (!input.isItemEqual(PluginApiculture.items.royalJelly.getItemStack(1))) continue;
                    int consumed = Math.min(input.stackSize, toConsume);
                    toConsume -= consumed;
                    input.stackSize -= consumed;
                    if (toConsume == 0) break;
                }
                double boosted = 1d;
                if (toConsume != maxConsume) {
                    boosted += (((double) maxConsume - (double) toConsume) / (double) maxConsume) * 2d;
                    this.updateSlots();
                }

                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0, mStorageSize = Math.min(mStorage.size(), mMaxSlots); i < mStorageSize; i++) {
                    SteamApiaryModule.BeeSimulator beeSimulator = mStorage.get(i);
                    stacks.addAll(beeSimulator.getDrops(this, 64_00d * boosted));
                }

                this.lEUt = -(long) (GTValues.V[4] * mMaxSlots * 0.99);
                this.mEfficiency = 10000;
                this.mEfficiencyIncrease = 10000;
                this.mMaxProgresstime = 300;
                this.mOutputItems = stacks.toArray(new ItemStack[0]);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public static class BeeSimulator {

        public ItemStack queenStack;
        public List<BeeDrop> drops = new ArrayList<>();
        public List<BeeDrop> specialDrops = new ArrayList<>();
        public boolean isValid;
        public float beeSpeed;
        public float maxBeeCycles;
        public static IBeekeepingMode mode;
        public static Map<GTUtility.ItemId, ItemStack> dropstacks = new HashMap<>();

        public BeeSimulator(ItemStack queenStack, World world, float t) {
            isValid = false;
            this.queenStack = queenStack.copy();
            this.queenStack.stackSize = 1;
            generate(world, t);
            isValid = true;
            queenStack.stackSize--;
        }

        public BeeSimulator(NBTTagCompound tag) {
            queenStack = readItemStackFromNBT(tag.getCompoundTag("queenStack"));
            isValid = tag.getBoolean("isValid");
            drops = new ArrayList<>();
            specialDrops = new ArrayList<>();
            for (int i = 0, isize = tag.getInteger("dropssize"); i < isize; i++)
                drops.add(new BeeDrop(tag.getCompoundTag("drops" + i)));
            for (int i = 0, isize = tag.getInteger("specialDropssize"); i < isize; i++)
                specialDrops.add(new BeeDrop(tag.getCompoundTag("specialDrops" + i)));
            beeSpeed = tag.getFloat("beeSpeed");
            maxBeeCycles = tag.getFloat("maxBeeCycles");
        }

        public NBTTagCompound toNBTTagCompound() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("queenStack", writeItemStackToNBT(queenStack));
            tag.setBoolean("isValid", isValid);
            tag.setInteger("dropssize", drops.size());
            for (int i = 0; i < drops.size(); i++) tag.setTag(
                "drops" + i,
                drops.get(i)
                    .toNBTTagCompound());
            tag.setInteger("specialDropssize", specialDrops.size());
            for (int i = 0; i < specialDrops.size(); i++) tag.setTag(
                "specialDrops" + i,
                specialDrops.get(i)
                    .toNBTTagCompound());
            tag.setFloat("beeSpeed", beeSpeed);
            tag.setFloat("maxBeeCycles", maxBeeCycles);
            return tag;
        }

        public void generate(World world, float t) {
            if (mode == null) mode = beeRoot.getBeekeepingMode(world);
            drops.clear();
            specialDrops.clear();
            if (beeRoot.getType(this.queenStack) != EnumBeeType.QUEEN) return;

            var queen = beeRoot.getMember(this.queenStack);
            var genome = queen.getGenome();

            beeSpeed = genome.getSpeed();

            IAlleleBeeSpecies primary = genome.getPrimary();

            genome.getPrimary()
                .getProductChances()
                .forEach((key, value) -> drops.add(new BeeDrop(key, value, beeSpeed, t)));
            genome.getSecondary()
                .getProductChances()
                .forEach((key, value) -> drops.add(new BeeDrop(key, value / 2f, beeSpeed, t)));
            primary.getSpecialtyChances()
                .forEach((key, value) -> specialDrops.add(new BeeDrop(key, value, beeSpeed, t)));
        }

        public List<ItemStack> getDrops(final SteamApiaryModule machine, final double timePassed) {
            drops.forEach(d -> {
                machine.dropProgress.merge(d.id, d.getAmount(timePassed / 550d), Double::sum);
                if (!dropstacks.containsKey(d.id)) dropstacks.put(d.id, d.stack);
            });
            specialDrops.forEach(d -> {
                machine.dropProgress.merge(d.id, d.getAmount(timePassed / 550d), Double::sum);
                if (!dropstacks.containsKey(d.id)) dropstacks.put(d.id, d.stack);
            });

            List<ItemStack> currentDrops = new ArrayList<>();
            machine.dropProgress.entrySet()
                .forEach(e -> {
                    double v = e.getValue();
                    while (v > 1.0) {
                        int size = Math.min((int) v, 64);
                        ItemStack stack = dropstacks.get(e.getKey())
                            .copy();
                        stack.stackSize = size;
                        currentDrops.add(stack);
                        v -= size;
                        e.setValue(v);
                    }
                });
            return currentDrops;
        }

        public static class BeeDrop {

            public ItemStack stack;
            public double chance;
            public float beeSpeed;
            public float t;
            public GTUtility.ItemId id;

            public BeeDrop(ItemStack stack, double chance, float beeSpeed, float t) {
                this.stack = stack;
                this.chance = chance;
                this.beeSpeed = beeSpeed;
                this.t = t;
                this.id = GTUtility.ItemId.createNoCopy(stack);
            }

            public double getAmount(double speedModifier) {
                return chance * speedModifier;
            }

            public BeeDrop(NBTTagCompound tag) {
                stack = readItemStackFromNBT(tag.getCompoundTag("stack"));
                chance = tag.getDouble("chance");
                beeSpeed = tag.getFloat("beeSpeed");
                t = tag.getFloat("t");
                id = GTUtility.ItemId.createNoCopy(stack);
            }

            public NBTTagCompound toNBTTagCompound() {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setTag("stack", writeItemStackToNBT(stack));
                tag.setDouble("chance", chance);
                tag.setFloat("beeSpeed", beeSpeed);
                tag.setFloat("t", t);
                return tag;
            }
        }
    }

    public static final UIInfo<?, ?> apiaryUI = createMetaTileEntityUI();

    public static UIInfo<?, ?> createMetaTileEntityUI() {
        return UIBuilder.of()
            .container((player, world, x, y, z) -> {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity) {
                    IMetaTileEntity mte = ((BaseMetaTileEntity) te).getMetaTileEntity();
                    if (!(mte instanceof SteamApiaryModule)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return new MUIContainer(
                        new ModularUIContext(buildContext, te::markDirty),
                        window,
                        (SteamApiaryModule) mte);
                }
                return null;
            })
            .gui((player, world, x, y, z) -> {
                if (!world.isRemote) return null;
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity) {
                    IMetaTileEntity mte = ((BaseMetaTileEntity) te).getMetaTileEntity();
                    if (!(mte instanceof SteamApiaryModule)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return new ModularGui(
                        new MUIContainer(new ModularUIContext(buildContext, null), window, (SteamApiaryModule) mte));
                }
                return null;
            })
            .build();
    }

    public void addConfigurationWidgets(DynamicPositionedRow configurationElements, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(CONFIGURATION_WINDOW_ID, this::createConfigurationWindow);
        configurationElements.setSynced(false);
        configurationElements.widget(
            new ButtonWidget().setOnClick(
                (clickData, widget) -> {
                    if (!widget.isClient()) widget.getContext()
                        .openSyncedWindow(CONFIGURATION_WINDOW_ID);
                })
                .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .addTooltip(StatCollector.translateToLocal("kubatech.gui.text.configuration"))
                .setSize(16, 16));
    }

    public static class MUIContainer extends ModularUIContainer {

        final WeakReference<SteamApiaryModule> parent;

        public MUIContainer(ModularUIContext context, ModularWindow mainWindow, SteamApiaryModule mte) {
            super(context, mainWindow);
            parent = new WeakReference<>(mte);
        }

        @Override
        public ItemStack transferStackInSlot(EntityPlayer aPlayer, int aSlotIndex) {
            if (!(aPlayer instanceof EntityPlayerMP)) return super.transferStackInSlot(aPlayer, aSlotIndex);
            final Slot s = getSlot(aSlotIndex);
            if (s == null) return super.transferStackInSlot(aPlayer, aSlotIndex);
            if (aSlotIndex >= 36) return super.transferStackInSlot(aPlayer, aSlotIndex);
            final ItemStack aStack = s.getStack();
            if (aStack == null) return super.transferStackInSlot(aPlayer, aSlotIndex);
            SteamApiaryModule mte = parent.get();
            if (mte == null) return super.transferStackInSlot(aPlayer, aSlotIndex);
            if (mte.mStorage.size() >= mte.mMaxSlots) return super.transferStackInSlot(aPlayer, aSlotIndex);
            if (beeRoot.getType(aStack) == EnumBeeType.QUEEN) {
                if (mte.mMaxProgresstime > 0) {
                    GTUtility.sendChatToPlayer(aPlayer, EnumChatFormatting.RED + "Can't insert while running !");
                    return super.transferStackInSlot(aPlayer, aSlotIndex);
                }
                World w = mte.getBaseMetaTileEntity()
                    .getWorld();
                SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(
                    aStack,
                    w,
                    6 + mte.recipeOcCount);
                if (bs.isValid) {
                    mte.mStorage.add(bs);
                    s.putStack(null);
                    detectAndSendChanges();
                    return null;
                }
            }
            return super.transferStackInSlot(aPlayer, aSlotIndex);
        }
    }

    public static final int INVENTORY_WIDTH = 128;
    public static final int INVENTORY_HEIGHT = 60;
    public static final int INVENTORY_X = 10;
    public static final int INVENTORY_Y = 16;
    public static final int INVENTORY_BORDER_WIDTH = 3;

    DynamicInventory<SteamApiaryModule.BeeSimulator> dynamicInventory = new DynamicInventory<>(
        INVENTORY_WIDTH,
        INVENTORY_HEIGHT,
        () -> mMaxSlots,
        mStorage,
        s -> s.queenStack).allowInventoryInjection(input -> {
            World w = getBaseMetaTileEntity().getWorld();
            SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(input, w, 6 + recipeOcCount);
            if (bs.isValid) {
                mStorage.add(bs);
                return input;
            }
            return null;
        })
            .allowInventoryExtraction(mStorage::remove)
            .allowInventoryReplace((i, stack) -> {
                if (stack.stackSize != 1) return null;
                World w = getBaseMetaTileEntity().getWorld();
                SteamApiaryModule.BeeSimulator bs = new SteamApiaryModule.BeeSimulator(stack, w, 6 + recipeOcCount);
                if (bs.isValid) {
                    SteamApiaryModule.BeeSimulator removed = mStorage.remove(i);
                    mStorage.add(i, bs);
                    return removed.queenStack;
                }
                return null;
            })
            .setEnabled(() -> this.mMaxProgresstime == 0);

    public boolean isInInventory = true;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {

        buildContext.addSyncedWindow(OC_WINDOW_ID, this::createRecipeOcCountWindow);
        builder.widget(new FakeSyncWidget.LongSyncer(this::getTotalSteamCapacityLong, val -> uiSteamCapacity = val));
        builder.widget(new FakeSyncWidget.LongSyncer(this::getLongTotalSteamStored, val -> uiSteamStored = val));
        builder.widget(
            new FakeSyncWidget.IntegerSyncer(this::getTotalSteamStoredOfAnyType, val -> uiSteamStoredOfAllTypes = val));

        isInInventory = !getBaseMetaTileEntity().isActive();
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85)
                .setEnabled(w -> !isInInventory));

        final int backgroundPadding = INVENTORY_BORDER_WIDTH * 2;
        builder.widget(
            new DrawableWidget().setDrawable(APIARY_INVENTORY_BACKGROUND)
                .setPos(INVENTORY_X - INVENTORY_BORDER_WIDTH, INVENTORY_Y - INVENTORY_BORDER_WIDTH)
                .setSize(INVENTORY_WIDTH + backgroundPadding, INVENTORY_HEIGHT + backgroundPadding)
                .setEnabled(w -> isInInventory));

        builder.widget(
            dynamicInventory.asWidget(builder, buildContext)
                .setPos(INVENTORY_X, INVENTORY_Y)
                .setEnabled(w -> isInInventory));

        builder.widget(
            new CycleButtonWidget().setToggle(() -> isInInventory, i -> isInInventory = i)
                .setTextureGetter(
                    i -> i == 0 ? new Text(StatCollector.translateToLocal("kubatech.gui.text.inventory"))
                        : new Text(StatCollector.translateToLocal("kubatech.gui.text.status")))
                .setBackground(GTUITextures.BUTTON_STANDARD)
                .setPos(140, 91)
                .setSize(55, 16));

        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, null);
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements)
                .setPos(10, 7)
                .setSize(182, 79)
                .setEnabled(w -> !isInInventory));

        builder.widget(createPowerSwitchButton(builder))
            .widget(createVoidExcessButton(builder))
            .widget(createInputSeparationButton(builder))
            .widget(createBatchModeButton(builder))
            .widget(createLockToSingleRecipeButton(builder))
            .widget(createStructureUpdateButton(builder));

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(OC_WINDOW_ID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_BATCH_MODE_ON);
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(translateToLocal("Info_SteamMachine_00"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 112)
            .setSize(16, 16));

        builder.widget(
            new DrawableWidget().setDrawable(this.tierMachine == 2 ? STEAM_GAUGE_STEEL_BG : STEAM_GAUGE_BG)
                .dynamicTooltip(() -> {
                    List<String> ret = new ArrayList<>();
                    ret.add(
                        StatCollector.translateToLocal("AllSteamCapacity") + uiSteamStored
                            + "/"
                            + uiSteamCapacity
                            + "L");
                    if (uiSteamStored == 0 && uiSteamStoredOfAllTypes != 0) {
                        ret.add(EnumChatFormatting.RED + "Found steam of wrong type!");
                    }
                    return ret;
                })
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setUpdateTooltipEveryTick(true)
                .setSize(64, 42)
                .setPos(-64, 100));

        builder.widget(
            new DrawableWidget().setDrawable(new CircularGaugeDrawable(() -> (float) uiSteamStored / uiSteamCapacity))
                .setPos(-64 + 21, 100 + 21)
                .setSize(18, 4));

        builder.widget(
            new ItemDrawable(GTNLItemList.FakeItemSiren.get(1)).asWidget()
                .setPos(-64 + 21 - 7, 100 - 20)
                .setEnabled(w -> uiSteamStored == 0));
    }

    @Override
    public ModularWindow createRecipeOcCountWindow(EntityPlayer player) {
        final int WIDTH = 158;
        final int HEIGHT = 52;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.BottomRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)
                        .subtract(0, 10)));
        builder.widget(
            TextWidget.localised("Info_SteamMachine_00")
                .setPos(3, 4)
                .setSize(150, 20))
            .widget(
                new NumericWidget().setSetter(val -> recipeOcCount = clampRecipeOcCount((int) val))
                    .setGetter(() -> {
                        mMaxSlots = 8 << Math.min(4, recipeOcCount);
                        return clampRecipeOcCount(recipeOcCount);
                    })
                    .setBounds(0, Integer.MAX_VALUE)
                    .setDefaultValue(0)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(150, 18)
                    .setPos(4, 25)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .attachSyncer(
                        new FakeSyncWidget.IntegerSyncer(
                            () -> clampRecipeOcCount(recipeOcCount),
                            (val) -> recipeOcCount = clampRecipeOcCount(val)),
                        builder));
        return builder.build();
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        apiaryUI.open(
            aPlayer,
            aBaseMetaTileEntity.getWorld(),
            aBaseMetaTileEntity.getXCoord(),
            aBaseMetaTileEntity.getYCoord(),
            aBaseMetaTileEntity.getZCoord());
        return true;
    }

    private HashMap<ItemStack, Double> GUIDropProgress = new HashMap<>();

    protected ModularWindow createConfigurationWindow(final EntityPlayer player) {
        ModularWindow.Builder builder = ModularWindow.builder(200, 100);
        builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);

        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .setPos(5, 5)
                .setSize(16, 16))
            .widget(new TextWidget(StatCollector.translateToLocal("kubatech.gui.text.configuration")).setPos(25, 9))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setPos(185, 3));

        // 主模式选择：输入、输出、运行
        builder.widget(
            new Column().widget(
                new CycleButtonWidget().setLength(3)
                    .setGetter(() -> mPrimaryMode)
                    .setSetter(val -> {
                        if (this.mMaxProgresstime > 0) {
                            GTUtility.sendChatToPlayer(player, "Can't change mode when running !");
                            return;
                        }
                        mPrimaryMode = val;
                        if (!(player instanceof EntityPlayerMP)) return;
                        switch (mPrimaryMode) {
                            case 0:
                                GTUtility.sendChatToPlayer(player, "Changed primary mode to: Input mode");
                                break;
                            case 1:
                                GTUtility.sendChatToPlayer(player, "Changed primary mode to: Output mode");
                                break;
                            case 2:
                                GTUtility.sendChatToPlayer(player, "Changed primary mode to: Operating mode");
                                break;
                        }
                    })
                    .addTooltip(
                        0,
                        new Text(StatCollector.translateToLocal("kubatech.gui.text.input")).color(Color.YELLOW.dark(3)))
                    .addTooltip(
                        1,
                        new Text(StatCollector.translateToLocal("kubatech.gui.text.output"))
                            .color(Color.YELLOW.dark(3)))
                    .addTooltip(
                        2,
                        new Text(StatCollector.translateToLocal("kubatech.gui.text.operating"))
                            .color(Color.GREEN.dark(3)))
                    .setTextureGetter(
                        i -> i == 0
                            ? new Text(StatCollector.translateToLocal("kubatech.gui.text.input"))
                                .color(Color.YELLOW.dark(3))
                            : i == 1
                                ? new Text(StatCollector.translateToLocal("kubatech.gui.text.output"))
                                    .color(Color.YELLOW.dark(3))
                                : new Text(StatCollector.translateToLocal("kubatech.gui.text.operating"))
                                    .color(Color.GREEN.dark(3)))
                    .setBackground(
                        ModularUITextures.VANILLA_BACKGROUND,
                        GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                    .setSize(70, 18)
                    .addTooltip(StatCollector.translateToLocal("kubatech.gui.text.mia.primary_mode")))
                .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                .setPos(10, 30));

        builder.widget(
            new Column().widget(
                new TextWidget(StatCollector.translateToLocal("kubatech.gui.text.mia.primary_mode")).setSize(100, 18))
                .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                .setPos(100, 30));

        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CROSS)
                .setSize(18, 18)
                .setPos(10, 30)
                .addTooltip(
                    new Text(StatCollector.translateToLocal("GT5U.gui.text.cannot_change_when_running"))
                        .color(Color.RED.dark(3)))
                .setEnabled(widget -> getBaseMetaTileEntity().isActive()));

        return builder.build();
    }

    @Override
    protected Widget generateCurrentRecipeInfoWidget() {
        DynamicPositionedColumn processingDetails = new DynamicPositionedColumn();
        if (mOutputItems == null || GUIDropProgress == null) return processingDetails;
        LinkedHashMap<ItemStack, Double> sortedMap = GUIDropProgress.entrySet()
            .stream()
            .sorted(
                Comparator.comparingInt(
                    (Map.Entry<ItemStack, Double> entry) -> Arrays.stream(mOutputItems)
                        .filter(s -> s.isItemEqual(entry.getKey()))
                        .mapToInt(i -> i.stackSize)
                        .sum())
                    .reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (Map.Entry<ItemStack, Double> drop : sortedMap.entrySet()) {
            assert mOutputItems != null;
            int outputSize = Arrays.stream(mOutputItems)
                .filter(s -> s.isItemEqual(drop.getKey()))
                .mapToInt(i -> i.stackSize)
                .sum();
            if (outputSize != 0) {
                Long itemCount = (long) outputSize;
                String itemName = drop.getKey()
                    .getDisplayName();
                String itemAmountString = EnumChatFormatting.WHITE + " x "
                    + EnumChatFormatting.GOLD
                    + formatShortenedLong(itemCount)
                    + EnumChatFormatting.WHITE
                    + appendRate(false, itemCount, true);
                String lineText = EnumChatFormatting.AQUA + truncateText(itemName, 20) + itemAmountString;
                String lineTooltip = EnumChatFormatting.AQUA + itemName + "\n" + appendRate(false, itemCount, false);

                processingDetails.widget(
                    new MultiChildWidget().addChild(
                        new ItemDrawable(
                            drop.getKey()
                                .copy()).asWidget()
                                    .setSize(8, 8)
                                    .setPos(0, 0))
                        .addChild(
                            new TextWidget(lineText).setTextAlignment(Alignment.CenterLeft)
                                .addTooltip(lineTooltip)
                                .setPos(10, 1)));
            }
        }
        return processingDetails;
    }

    @Override
    protected void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        screenElements.widget(new FakeSyncWidget<>(() -> {
            HashMap<ItemStack, Double> ret = new HashMap<>();
            HashMap<GTUtility.ItemId, Double> dropProgress = new HashMap<>();

            for (Map.Entry<GTUtility.ItemId, Double> drop : this.dropProgress.entrySet()) {
                dropProgress.merge(drop.getKey(), drop.getValue(), Double::sum);
            }

            for (Map.Entry<GTUtility.ItemId, Double> drop : dropProgress.entrySet()) {
                ret.put(BeeSimulator.dropstacks.get(drop.getKey()), drop.getValue());
            }
            return ret;
        }, h -> GUIDropProgress = h, (buffer, h) -> {
            buffer.writeVarIntToBuffer(h.size());
            for (Map.Entry<ItemStack, Double> itemStackDoubleEntry : h.entrySet()) {
                try {
                    buffer.writeItemStackToBuffer(itemStackDoubleEntry.getKey());
                    buffer.writeDouble(itemStackDoubleEntry.getValue());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, buffer -> {
            int len = buffer.readVarIntFromBuffer();
            HashMap<ItemStack, Double> ret = new HashMap<>(len);
            for (int i = 0; i < len; i++) {
                try {
                    ret.put(buffer.readItemStackFromBuffer(), buffer.readDouble());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return ret;
        }));
        super.drawTexts(screenElements, inventorySlot);
    }

    public <Y> void tryOutputAll(@Nullable List<Y> list, @Nullable Function<Y, List<ItemStack>> mappingFunction) {
        if (list == null || list.isEmpty() || mappingFunction == null) return;
        int emptySlots = 0;
        boolean ignoreEmptiness = false;
        for (MTEHatchOutputBus i : mOutputBusses) {
            if (i instanceof MTEHatchOutputBusME) {
                ignoreEmptiness = true;
                break;
            }
            for (int j = 0; j < i.getSizeInventory(); j++)
                if (i.isValidSlot(j)) if (i.getStackInSlot(j) == null) emptySlots++;
        }
        if (emptySlots == 0 && !ignoreEmptiness) return;
        while (!list.isEmpty()) {
            List<ItemStack> toOutputNow = mappingFunction.apply(list.get(0));
            if (toOutputNow == null) {
                list.remove(0);
                continue;
            }
            if (!ignoreEmptiness && emptySlots < toOutputNow.size()) break;
            emptySlots -= toOutputNow.size();
            list.remove(0);
            for (ItemStack stack : toOutputNow) {
                addOutput(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MEGA_INDUSTRIAL_APIARY_LOOP;
    }
}
