package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.common.greenhouse.GreenHouseRecipeInfo;
import com.science.gtnl.common.gui.modularui.multiblock.SteamApiaryModuleGui;
import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.utils.machine.MachineUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.apiculture.genetics.Bee;
import forestry.plugins.PluginApiculture;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kubatech.api.utils.ItemUtils;

@IMetaTileEntity.SkipGenerateDescription
public class SteamApiaryModule extends SteamElevatorModuleBase {

    private static final int RUN_TICKS = 6000;

    public int mMaxSlots = 8;
    public ObjectArrayList<BeeSimulator> mStorage = new ObjectArrayList<>();

    public static final int MODE_PRIMARY_INPUT = 0;
    public static final int MODE_PRIMARY_OUTPUT = 1;
    public static final int MODE_PRIMARY_OPERATING = 2;
    public static final int INVENTORY_WIDTH = 128;
    public static final int INVENTORY_HEIGHT = 60;

    public int mPrimaryMode = MODE_PRIMARY_INPUT;
    public Object2DoubleOpenHashMap<ItemId> dropProgress = new Object2DoubleOpenHashMap<>();
    private static final double BASE_DROP_ACCELERATION = 32_00d;

    public boolean isInInventory = true;

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
        refreshMaxSlots();
        return 6 + recipeOcCount;
    }

    public void refreshMaxSlotsForGui() {
        refreshMaxSlots();
    }

    public boolean isBeeInventoryLockedForGui() {
        return mPrimaryMode == MODE_PRIMARY_OPERATING && mMaxProgresstime > 0;
    }

    private void refreshMaxSlots() {
        mMaxSlots = 8 << Math.min(4, clampRecipeOcCount(recipeOcCount));
    }

    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatTrans(aPlayer, "gtnl.machine.message.mode_running");
            return;
        }
        mPrimaryMode++;
        if (mPrimaryMode == 3) mPrimaryMode = 0;
        switch (mPrimaryMode) {
            case 0:
                GTUtility.sendChatTrans(aPlayer, "gtnl.machine.message.primary_mode.input");
                break;
            case 1:
                GTUtility.sendChatTrans(aPlayer, "gtnl.machine.message.primary_mode.output");
                break;
            case 2:
                GTUtility.sendChatTrans(aPlayer, "gtnl.machine.message.primary_mode.operating");
                break;
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        IGregTechTileEntity mte = getBaseMetaTileEntity();
        if (mte.getWorld().isRemote) return;
        for (BeeSimulator beeSimulator : mStorage) {
            dropStoredStack(mte, beeSimulator.queenStack);
        }
        mStorage.clear();
    }

    private void dropStoredStack(IGregTechTileEntity mte, ItemStack stack) {
        if (GTUtility.isStackInvalid(stack)) return;
        EntityItem entityitem = new EntityItem(
            mte.getWorld(),
            mte.getXCoord(),
            mte.getYCoord(),
            mte.getZCoord(),
            stack.copy());
        entityitem.delayBeforeCanPickup = 10;
        mte.getWorld()
            .spawnEntityInWorld(entityitem);
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
        ObjectArrayList<String> info = new ObjectArrayList<>(Arrays.asList(super.getInfoData()));
        String modeKey = mPrimaryMode == 0 ? "kubatech.infodata.mia.running_mode.input"
            : mPrimaryMode == 1 ? "kubatech.infodata.mia.running_mode.output"
                : "kubatech.infodata.mia.running_mode.operating.normal";
        info.add(
            IGregTechDeviceInformation.encode(
                "kubatech.infodata.running_mode.fmt",
                EnumChatFormatting.GOLD,
                IGregTechDeviceInformation.translatable(modeKey)));
        info.add(
            IGregTechDeviceInformation.encode(
                "kubatech.infodata.mia.running_mode.bee_storage",
                "" + EnumChatFormatting.GOLD + mStorage.size() + EnumChatFormatting.RESET,
                (mStorage.size() > mMaxSlots ? EnumChatFormatting.DARK_RED.toString()
                    : EnumChatFormatting.GOLD.toString()) + mMaxSlots + EnumChatFormatting.RESET));
        Object2IntMap<String> infos = new Object2IntOpenHashMap<>();
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
        return "gtnl.machine.steam_apiary_module.recipe_type";
    }

    @Override
    public TooltipStats getTooltipStats() {
        return null;
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        return ImmutableMap.of("run_seconds", RUN_TICKS / SECONDS);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        refreshMaxSlots();
        if (mPrimaryMode < 2) { // input and output mode
            if (mPrimaryMode == MODE_PRIMARY_INPUT && mStorage.size() < mMaxSlots) {
                World w = getBaseMetaTileEntity().getWorld();
                List<ItemStack> inputs = getStoredInputs();
                for (ItemStack input : inputs) {
                    if (BeeManager.beeRoot.getType(input) == EnumBeeType.QUEEN) {
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
                List<ItemStack> inputs = getStoredInputs();

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

                List<ItemStack> stacks = new ObjectArrayList<>();
                for (int i = 0, mStorageSize = Math.min(mStorage.size(), mMaxSlots); i < mStorageSize; i++) {
                    SteamApiaryModule.BeeSimulator beeSimulator = mStorage.get(i);
                    stacks.addAll(beeSimulator.getDrops(this, BASE_DROP_ACCELERATION * boosted));
                }

                this.lEUt = -GTValues.V[4] * mMaxSlots;
                this.mEfficiency = 10000;
                this.mEfficiencyIncrease = 10000;
                this.mMaxProgresstime = RUN_TICKS;
                this.mOutputItems = mergeOutputStacks(stacks);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    private static ItemStack[] mergeOutputStacks(List<ItemStack> stacks) {
        Object2IntOpenHashMap<ItemId> countMap = new Object2IntOpenHashMap<>();
        Object2ObjectOpenHashMap<ItemId, ItemStack> stackMap = new Object2ObjectOpenHashMap<>();
        for (ItemStack stack : stacks) {
            ItemId id = ItemId.createNoCopy(stack);
            countMap.addTo(id, stack.stackSize);
            stackMap.putIfAbsent(id, stack);
        }

        ItemStack[] result = new ItemStack[countMap.size()];
        int index = 0;
        for (Object2IntMap.Entry<ItemId> entry : Object2IntMaps.fastIterable(countMap)) {
            ItemStack merged = stackMap.get(entry.getKey())
                .copy();
            merged.stackSize = entry.getIntValue();
            result[index++] = merged;
        }
        return result;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        openGui(aPlayer);
        return true;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamApiaryModuleGui(this);
    }

    public Object2IntOpenHashMap<ItemId> getOutputItemCounts(ItemStack[] outputItems) {
        return GreenHouseRecipeInfo.outputItemCounts(outputItems);
    }

    public <Y> void tryOutputAll(@Nullable List<Y> list, @Nullable Function<Y, List<ItemStack>> mappingFunction) {
        if (list == null || list.isEmpty() || mappingFunction == null) return;
        int emptySlots = 0;
        boolean ignoreEmptiness = false;
        for (MTEHatchOutputBus outputBus : mOutputBusses) {
            if (MachineUtils.isMEOutputBus(outputBus)) {
                ignoreEmptiness = true;
                break;
            }
            for (int j = 0; j < outputBus.getSizeInventory(); j++)
                if (outputBus.isValidSlot(j)) if (outputBus.getStackInSlot(j) == null) emptySlots++;
        }
        if (emptySlots == 0 && !ignoreEmptiness) return;
        // Use iterator removal to avoid repeated head-shift costs on List.
        for (var iterator = list.iterator(); iterator.hasNext();) {
            Y pendingOutput = iterator.next();
            List<ItemStack> toOutputNow = mappingFunction.apply(pendingOutput);
            if (toOutputNow == null) {
                iterator.remove();
                continue;
            }
            if (!ignoreEmptiness && emptySlots < toOutputNow.size()) break;
            emptySlots -= toOutputNow.size();
            iterator.remove();
            for (ItemStack stack : toOutputNow) {
                addOutput(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MEGA_INDUSTRIAL_APIARY_LOOP;
    }

    public static class BeeSimulator {

        public ItemStack queenStack;
        public List<BeeDrop> drops = new ObjectArrayList<>();
        public List<BeeDrop> specialDrops = new ObjectArrayList<>();
        public boolean isValid;
        public float beeSpeed;
        public float maxBeeCycles;
        public String speciesKey;
        public static IBeekeepingMode mode;
        public static Map<ItemId, ItemStack> dropstacks = new Object2ObjectOpenHashMap<>();

        public BeeSimulator(ItemStack queenStack, World world, float t) {
            isValid = false;
            this.queenStack = queenStack.copy();
            this.queenStack.stackSize = 1;
            generate(world, t);
            isValid = true;
            queenStack.stackSize--;
        }

        public BeeSimulator(NBTTagCompound tag) {
            queenStack = ItemUtils.readItemStackFromNBT(tag.getCompoundTag("queenStack"));
            isValid = tag.getBoolean("isValid");
            drops = new ObjectArrayList<>();
            specialDrops = new ObjectArrayList<>();
            for (int i = 0, isize = tag.getInteger("dropssize"); i < isize; i++)
                drops.add(new BeeDrop(tag.getCompoundTag("drops" + i)));
            for (int i = 0, isize = tag.getInteger("specialDropssize"); i < isize; i++)
                specialDrops.add(new BeeDrop(tag.getCompoundTag("specialDrops" + i)));
            beeSpeed = tag.getFloat("beeSpeed");
            maxBeeCycles = tag.getFloat("maxBeeCycles");
            var queen = BeeManager.beeRoot.getMember(this.queenStack);
            if (queen != null) {
                var genome = queen.getGenome();
                speciesKey = genome.getPrimary()
                    .getUID() + "\0"
                    + genome.getSecondary()
                        .getUID()
                    + "\0"
                    + beeSpeed;
            }
        }

        public NBTTagCompound toNBTTagCompound() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("queenStack", ItemUtils.writeItemStackToNBT(queenStack));
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
            if (mode == null) mode = BeeManager.beeRoot.getBeekeepingMode(world);
            drops.clear();
            specialDrops.clear();
            if (BeeManager.beeRoot.getType(this.queenStack) != EnumBeeType.QUEEN) return;

            var queen = BeeManager.beeRoot.getMember(this.queenStack);
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
            speciesKey = primary.getUID() + "\0"
                + genome.getSecondary()
                    .getUID()
                + "\0"
                + beeSpeed;
        }

        public List<ItemStack> getDrops(final SteamApiaryModule machine, final double timePassed) {
            if (mode == null) mode = BeeManager.beeRoot.getBeekeepingMode(
                machine.getBaseMetaTileEntity()
                    .getWorld());
            float tier = 6 + machine.recipeOcCount;
            drops.forEach(d -> {
                d.updateTVar(tier);
                machine.dropProgress.addTo(d.id, d.getAmount(timePassed / 550d));
                if (!dropstacks.containsKey(d.id)) dropstacks.put(d.id, d.stack);
            });
            specialDrops.forEach(d -> {
                d.updateTVar(tier);
                machine.dropProgress.addTo(d.id, d.getAmount(timePassed / 550d));
                if (!dropstacks.containsKey(d.id)) dropstacks.put(d.id, d.stack);
            });

            List<ItemStack> currentDrops = new ObjectArrayList<>();
            machine.dropProgress.object2DoubleEntrySet()
                .fastForEach(e -> {
                    double v = e.getDoubleValue();
                    while (v >= 1.0) {
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

            private static final float MAX_PRODUCTION_MODIFIER_FROM_UPGRADES = 17.19926784f;

            public ItemStack stack;
            public double chance;
            public double amount;
            public float beeSpeed;
            public float t;
            public ItemId id;

            public BeeDrop(ItemStack stack, double chance, float beeSpeed, float t) {
                this.stack = stack;
                this.chance = chance;
                this.beeSpeed = beeSpeed;
                this.t = t;
                this.id = ItemId.createNoCopy(stack);
                evaluate();
            }

            public void updateTVar(float t) {
                if (this.t != t || amount <= 0) {
                    this.t = t;
                    evaluate();
                }
            }

            public void evaluate() {
                if (mode == null) {
                    amount = chance;
                    return;
                }
                float productionModifier = MAX_PRODUCTION_MODIFIER_FROM_UPGRADES + mode.getBeeModifier()
                    .getProductionModifier(null, MAX_PRODUCTION_MODIFIER_FROM_UPGRADES);
                amount = Bee.getFinalChance((float) chance, beeSpeed, productionModifier, t);
            }

            public double getAmount(double speedModifier) {
                return amount * speedModifier;
            }

            public BeeDrop(NBTTagCompound tag) {
                stack = ItemUtils.readItemStackFromNBT(tag.getCompoundTag("stack"));
                chance = tag.getDouble("chance");
                amount = tag.getDouble("amount");
                beeSpeed = tag.getFloat("beeSpeed");
                t = tag.getFloat("t");
                id = ItemId.createNoCopy(stack);
            }

            public NBTTagCompound toNBTTagCompound() {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setTag("stack", ItemUtils.writeItemStackToNBT(stack));
                tag.setDouble("chance", chance);
                tag.setDouble("amount", amount);
                tag.setFloat("beeSpeed", beeSpeed);
                tag.setFloat("t", t);
                return tag;
            }
        }
    }
}
