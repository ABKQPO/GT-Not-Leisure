package com.science.gtnl.utils.machine.greenHouseManager;

import static com.science.gtnl.utils.item.ItemUtils.readItemStackFromNBT;
import static kubatech.kubatech.error;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.screen.ITileWithModularUI;
import com.gtnewhorizons.modularui.api.screen.ModularUIContext;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.builder.UIBuilder;
import com.gtnewhorizons.modularui.common.builder.UIInfo;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularGui;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularUIContainer;
import com.science.gtnl.api.IGreenHouse;

import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public abstract class GreenHouseMode {

    public static int EIG_BALANCE_IC2_ACCELERATOR_TIER = VoltageIndex.EV;
    public static int EIG_BALANCE_REGULAR_MODE_MIN_TIER = VoltageIndex.EV;
    public static int CONFIGURATION_WINDOW_ID = 10;

    public abstract int getUIIndex();

    public abstract String getName();

    public abstract int getMinVoltageTier();

    public abstract int getMinGlassTier();

    public abstract int getStartingSlotCount();

    public abstract int getSlotPerTierMultiplier();

    public abstract int getSlotCount(int machineTier);

    public abstract int getSeedCapacityPerSlot();

    public abstract int getWeedEXMultiplier();

    public abstract int getMaxFertilizerUsagePerSeed();

    public abstract double getFertilizerBoost();

    public abstract MultiblockTooltipBuilder addTooltipInfo(MultiblockTooltipBuilder builder);

    /**
     * Used to resolve factory type to an identifier.
     */
    private final HashMap<String, IGreenHouseBucketFactory> factories;
    /**
     * A way to have other mods submit custom buckets that can be prioritized over our default buckets
     */
    private final LinkedList<IGreenHouseBucketFactory> orderedFactories;

    public GreenHouseMode() {
        this.factories = new HashMap<>();
        this.orderedFactories = new LinkedList<>();
    }

    /**
     * Adds a bucket factory to the EIG mode and gives it a low priority. Factories with using existing IDs will
     * overwrite each other.
     *
     * @param factory The bucket factory to add.
     */
    public void addLowPriorityFactory(IGreenHouseBucketFactory factory) {
        String factoryId = factory.getNBTIdentifier();
        dealWithDuplicateFactoryId(factoryId);
        // add factory as lowest priority
        this.factories.put(factoryId, factory);
        this.orderedFactories.addLast(factory);
    }

    /**
     * Adds a bucket factory to the EIG mode and gives it a high priority. Factories with using existing IDs will
     * overwrite each other.
     *
     * @param factory The bucket factory to add.
     */
    public void addHighPriorityFactory(IGreenHouseBucketFactory factory) {
        String factoryId = factory.getNBTIdentifier();
        dealWithDuplicateFactoryId(factoryId);
        // add factory as lowest priority
        this.factories.put(factoryId, factory);
        this.orderedFactories.addFirst(factory);
    }

    /**
     * A standardized way to deal with duplicate factory type identifiers.
     *
     * @param factoryId The ID of the factory
     */
    private void dealWithDuplicateFactoryId(String factoryId) {
        if (this.factories.containsKey(factoryId)) {
            // TODO: Check with devs to see if they want a throw instead.
            error("Duplicate EIG bucket index detected!!!: " + factoryId);
            // remove duplicate from ordered list
            this.orderedFactories.remove(this.factories.get(factoryId));
        }
    }

    /**
     * Attempts to create a new bucket from a given item. Returns if the item cannot be inserted into the EIG.
     *
     * @see IGreenHouseBucketFactory#tryCreateBucket(IGreenHouse, ItemStack)
     * @param greenhouse The {@link IGreenHouse} that will contain the seed.
     * @param input      The {@link ItemStack} for the input item.
     * @param maxConsume The maximum amount of items to consume.
     * @param simulate   Whether to actually consume the seed.
     * @return Null if no bucket could be created from the item.
     */
    public GreenHouseBucket tryCreateNewBucket(IGreenHouse greenhouse, ItemStack input, int maxConsume,
        boolean simulate) {
        // Validate inputs
        if (input == null) return null;
        maxConsume = Math.min(input.stackSize, maxConsume);
        if (maxConsume <= 0) return null;
        for (IGreenHouseBucketFactory factory : this.orderedFactories) {
            GreenHouseBucket bucket = factory.tryCreateBucket(greenhouse, input);
            if (bucket == null || !bucket.isValid()) continue;
            if (!simulate) input.stackSize--;
            maxConsume--;
            bucket.tryAddSeed(greenhouse, input, maxConsume, simulate);
            return bucket;
        }
        return null;
    }

    /**
     * Restores the buckets of an EIG for the given mode.
     *
     * @see IGreenHouseBucketFactory#restore(NBTTagCompound)
     * @param bucketNBTList The
     */
    public void restoreBuckets(NBTTagList bucketNBTList, List<GreenHouseBucket> loadTo) {
        for (int i = 0; i < bucketNBTList.tagCount(); i++) {
            // validate nbt
            NBTTagCompound bucketNBT = bucketNBTList.getCompoundTagAt(i);
            if (bucketNBT.hasNoTags()) {
                error("Empty nbt bucket found in EIG nbt.");
                continue;
            }
            if (!bucketNBT.hasKey("type", 8)) {
                error("Failed to identify bucket type in EIG nbt.");
                continue;
            }
            // identify bucket type
            String bucketType = bucketNBT.getString("type");
            IGreenHouseBucketFactory factory = factories.getOrDefault(bucketType, null);
            if (factory == null) {
                error("failed to find EIG bucket factory for type: " + bucketType);
                continue;
            }
            // restore bucket
            loadTo.add(factory.restore(bucketNBT));
        }
    }

    public static class EIGMigrationHolder {

        public final ItemStack seed;
        public final ItemStack supportBlock;
        public final boolean useNoHumidity;
        public int count;
        public boolean isValid;

        public EIGMigrationHolder(NBTTagCompound nbt) {
            this.seed = readItemStackFromNBT(nbt.getCompoundTag("input"));
            if (this.seed != null) {
                this.count = this.seed.stackSize;
                this.seed.stackSize = 1;
            }
            this.supportBlock = nbt.hasKey("undercrop", 10) ? readItemStackFromNBT(nbt.getCompoundTag("undercrop"))
                : null;
            this.useNoHumidity = nbt.getBoolean("noHumidity");
            this.isValid = true;
        }

        public String getKey() {
            if (this.supportBlock == null) return seed.toString();
            return "(" + this.seed.toString() + "," + this.supportBlock + ")";
        }
    }

    public static class MUIContainer_Greenhouse extends ModularUIContainer {

        final WeakReference<? extends IGreenHouse> parent;

        public MUIContainer_Greenhouse(ModularUIContext context, ModularWindow mainWindow, IGreenHouse mte) {
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
            IGreenHouse mte = parent.get();
            if (mte == null) return super.transferStackInSlot(aPlayer, aSlotIndex);
            if (mte.getMaxProgressTime() > 0) {
                GTUtility.sendChatToPlayer(
                    aPlayer,
                    EnumChatFormatting.RED + StatCollector.translateToLocal("Info_EdenGarden_00"));
                return super.transferStackInSlot(aPlayer, aSlotIndex);
            }

            mte.addCrop(aStack);
            if (aStack.stackSize <= 0) s.putStack(null);
            else s.putStack(aStack);
            detectAndSendChanges();
            return null;
        }
    }

    @FunctionalInterface
    public interface ContainerConstructor<T extends IGreenHouse> {

        ModularUIContainer of(ModularUIContext context, ModularWindow mainWindow, T multiBlock);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IGreenHouse> UIInfo<?, ?> createGreenhouseUI(
        ContainerConstructor<T> containerConstructor) {
        return UIBuilder.of()
            .container((player, world, x, y, z) -> {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity baseMetaTileEntity) {
                    IMetaTileEntity mte = baseMetaTileEntity.getMetaTileEntity();
                    if (!(mte instanceof IGreenHouse)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return containerConstructor.of(new ModularUIContext(buildContext, te::markDirty), window, (T) mte);
                }
                return null;
            })
            .gui((player, world, x, y, z) -> {
                if (!world.isRemote) return null;
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity baseMetaTileEntity) {
                    IMetaTileEntity mte = baseMetaTileEntity.getMetaTileEntity();
                    if (!(mte instanceof IGreenHouse)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return new ModularGui(
                        containerConstructor.of(new ModularUIContext(buildContext, null), window, (T) mte));
                }
                return null;
            })
            .build();
    }
}
