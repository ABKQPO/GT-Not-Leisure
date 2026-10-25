package com.science.gtnl.common.recipe.thaumcraft;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizon.structurelib.structure.adders.ITileAdder;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaInputHatch;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.structure.StructureTooltipHooks;

import appeng.api.config.Actionable;
import appeng.api.storage.IMEMonitor;
import appeng.me.GridAccessException;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumicenergistics.common.storage.AEEssentiaStack;
import thaumicenergistics.common.storage.AEEssentiaStackType;
import thaumicenergistics.common.tiles.TileAdvancedInfusionProvider;
import thaumicenergistics.common.tiles.TileInfusionProvider;

public class EssentiaSupply {

    private static final int COMMIT_COOLDOWN_TICKS = 200;
    private static final String INFUSION_PROVIDER_NAME_KEY = "thaumicenergistics.block.infusion.provider.name";
    private static final String ADVANCED_INFUSION_PROVIDER_NAME_KEY = "thaumicenergistics.block.advanced.infusion.provider.name";

    private final String machineName;
    private final List<TileEntityEssentiaHatch> hatches = new ArrayList<>();
    private final List<TileEntity> providers = new ArrayList<>();
    private long commitBlockedUntil;

    public EssentiaSupply(String machineName) {
        this.machineName = machineName;
    }

    public static <T> IStructureElement<T> hatchElement(BiPredicate<T, TileEntityEssentiaHatch> adder, Block hintBlock,
        int hintMeta) {
        return StructureTooltipHooks.describeAsHatch(
            StructureUtility.ofSpecificTileAdder(adder, TileEntityEssentiaHatch.class, hintBlock, hintMeta),
            BlockLoader.essentiaHatch.getUnlocalizedName() + ".name",
            BlockLoader.multiEssentiaInputHatch.getUnlocalizedName() + ".name");
    }

    public static <T> IStructureElement<T> providerElement(ITileAdder<T> adder, Block hintBlock, int hintMeta) {
        IStructureElement<T> element = StructureUtility.ofTileAdder(adder, hintBlock, hintMeta);
        // Why: providers only count when Thaumic Energistics is loaded, and their names come from its lang files.
        if (!Mods.ThaumicEnergistics.isModLoaded()) return element;
        return StructureTooltipHooks
            .describeAsHatch(element, INFUSION_PROVIDER_NAME_KEY, ADVANCED_INFUSION_PROVIDER_NAME_KEY);
    }

    public void clear() {
        hatches.clear();
        providers.clear();
    }

    public boolean isEmpty() {
        return hatches.isEmpty() && providers.isEmpty();
    }

    public boolean addHatch(TileEntityEssentiaHatch hatch) {
        if (hatch == null || hatch.isInvalid() || hatches.contains(hatch)) return false;
        return hatches.add(hatch);
    }

    public boolean addProvider(TileEntity tile) {
        if (tile == null || tile.isInvalid() || providers.contains(tile)) return false;
        return Mods.ThaumicEnergistics.isModLoaded() && isInfusionProvider(tile) && providers.add(tile);
    }

    public boolean isCommitBlocked(long worldTime) {
        return worldTime < commitBlockedUntil;
    }

    public int getAffordableCrafts(AspectList requiredAspects, int limit) {
        if (requiredAspects == null || limit <= 0) return 0;

        Aspect[] aspects = snapshotAspects(requiredAspects);
        if (aspects == null) return 0;

        long affordable = limit;
        for (Aspect aspect : aspects) {
            if (aspect == null) continue;

            int amount = requiredAspects.getAmount(aspect);
            if (amount <= 0) continue;

            long available = getStoredInHatches(aspect) + getMaximumProviderAmount(aspect);
            affordable = Math.min(affordable, available / amount);
            if (affordable <= 0) return 0;
        }
        return (int) affordable;
    }

    public boolean consume(AspectList requiredAspects, int crafts, long worldTime) {
        if (requiredAspects == null || crafts <= 0) return false;

        Aspect[] aspects = snapshotAspects(requiredAspects);
        if (aspects == null) return false;

        int count = aspects.length;
        long[] fromHatches = new long[count];
        long[] fromProvider = new long[count];
        TileEntity[] selectedProviders = new TileEntity[count];

        for (int i = 0; i < count; i++) {
            Aspect aspect = aspects[i];
            if (aspect == null) continue;

            int amount = requiredAspects.getAmount(aspect);
            if (amount <= 0) continue;

            long required = (long) amount * crafts;
            fromHatches[i] = Math.min(required, getStoredInHatches(aspect));
            fromProvider[i] = required - fromHatches[i];
            if (fromProvider[i] <= 0) continue;

            selectedProviders[i] = findProvider(aspect, fromProvider[i]);
            if (selectedProviders[i] == null || fromProvider[i] > Integer.MAX_VALUE) return false;
        }

        for (int i = 0; i < count; i++) {
            if (fromProvider[i] <= 0) continue;
            if (takeFromProvider(selectedProviders[i], aspects[i], (int) fromProvider[i])) continue;

            long lost = 0;
            for (int j = 0; j < i; j++) {
                if (fromProvider[j] > 0) lost += refund(selectedProviders[j], aspects[j], fromProvider[j]);
            }
            commitBlockedUntil = worldTime + COMMIT_COOLDOWN_TICKS;
            ScienceNotLeisure.LOG.warn(
                "{}: essentia provider failed to supply {}, returned the essentia already taken ({} could not be placed), pausing for {} ticks",
                machineName,
                aspects[i].getTag(),
                lost,
                COMMIT_COOLDOWN_TICKS);
            return false;
        }

        for (int i = 0; i < count; i++) {
            if (fromHatches[i] > 0) drainHatches(aspects[i], fromHatches[i]);
        }
        return true;
    }

    public void requestShortfall(AspectList requiredAspects, int crafts) {
        if (requiredAspects == null || crafts <= 0 || !Mods.ThaumicEnergistics.isModLoaded()) return;

        Aspect[] aspects = snapshotAspects(requiredAspects);
        if (aspects == null) return;

        for (Aspect aspect : aspects) {
            if (aspect == null) continue;

            int amount = requiredAspects.getAmount(aspect);
            if (amount <= 0) continue;

            long shortfall = (long) amount * crafts - getStoredInHatches(aspect) - getMaximumProviderAmount(aspect);
            if (shortfall <= 0) continue;

            int requested = (int) Math.min(Integer.MAX_VALUE, shortfall);
            requestFromHatches(aspect, requested);
            orderFromProviders(aspect, requested);
        }
    }

    private void requestFromHatches(Aspect aspect, int amount) {
        for (TileEntityEssentiaHatch hatch : hatches) {
            if (hatch instanceof TileEntityMultiEssentiaInputHatch multiHatch && !hatch.isInvalid()
                && multiHatch.requestEssentia(aspect, amount)) {
                return;
            }
        }
    }

    private void orderFromProviders(Aspect aspect, int amount) {
        for (TileEntity provider : providers) {
            if (provider == null || provider.isInvalid()) continue;
            if (orderFromProviderCompat(provider, aspect, amount)) return;
        }
    }

    private Aspect[] snapshotAspects(AspectList requiredAspects) {
        try {
            return requiredAspects.getAspects();
        } catch (ConcurrentModificationException e) {
            ScienceNotLeisure.LOG
                .warn("{}: concurrent modification in recipe aspects, treating as insufficient", machineName, e);
            return null;
        }
    }

    private long getStoredInHatches(Aspect aspect) {
        long stored = 0;
        for (TileEntityEssentiaHatch hatch : hatches) {
            if (hatch == null || hatch.isInvalid()) continue;
            stored += hatch.containerContains(aspect);
        }
        return stored;
    }

    private void drainHatches(Aspect aspect, long amount) {
        long remaining = amount;
        for (TileEntityEssentiaHatch hatch : hatches) {
            if (remaining <= 0) return;
            if (hatch == null || hatch.isInvalid()) continue;

            int removed = (int) Math.min(remaining, hatch.containerContains(aspect));
            if (removed > 0 && hatch.reduceStoredEssentia(aspect, removed)) remaining -= removed;
        }
    }

    private long refund(TileEntity provider, Aspect aspect, long amount) {
        long remaining = amount - returnToProvider(provider, aspect, amount);
        return remaining - refundToHatches(aspect, remaining);
    }

    private int refundToHatches(Aspect aspect, long amount) {
        int remaining = (int) Math.min(Integer.MAX_VALUE, amount);
        int total = remaining;
        for (TileEntityEssentiaHatch hatch : hatches) {
            if (remaining <= 0) break;
            if (hatch == null || hatch.isInvalid()) continue;
            remaining = hatch.addToContainer(aspect, remaining);
        }
        return total - remaining;
    }

    private long returnToProvider(TileEntity provider, Aspect aspect, long amount) {
        return amount > 0 && Mods.ThaumicEnergistics.isModLoaded() ? returnToProviderCompat(provider, aspect, amount)
            : 0;
    }

    private long getMaximumProviderAmount(Aspect aspect) {
        long maximum = 0;
        for (TileEntity provider : providers) {
            if (provider == null || provider.isInvalid()) continue;
            maximum = Math.max(maximum, getProviderAmount(provider, aspect));
        }
        return maximum;
    }

    private TileEntity findProvider(Aspect aspect, long required) {
        TileEntity selected = null;
        long maximum = 0;
        for (TileEntity provider : providers) {
            if (provider == null || provider.isInvalid()) continue;

            long available = getProviderAmount(provider, aspect);
            if (available >= required && available > maximum) {
                selected = provider;
                maximum = available;
            }
        }
        return selected;
    }

    private long getProviderAmount(TileEntity provider, Aspect aspect) {
        return Mods.ThaumicEnergistics.isModLoaded() ? getProviderAmountCompat(provider, aspect) : 0;
    }

    private boolean takeFromProvider(TileEntity provider, Aspect aspect, int amount) {
        return Mods.ThaumicEnergistics.isModLoaded() && takeFromProviderCompat(provider, aspect, amount);
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean isInfusionProvider(TileEntity tile) {
        return tile instanceof TileInfusionProvider;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean isUsableProviderCompat(TileEntity provider) {
        if (provider instanceof TileAdvancedInfusionProvider advancedProvider) {
            return advancedProvider.matrices.isEmpty();
        }
        return provider instanceof TileInfusionProvider;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static long getProviderAmountCompat(TileEntity provider, Aspect aspect) {
        return isUsableProviderCompat(provider) ? ((TileInfusionProvider) provider).getAspectAmountInNetwork(aspect)
            : 0;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean orderFromProviderCompat(TileEntity provider, Aspect aspect, int amount) {
        return isUsableProviderCompat(provider) && ((TileInfusionProvider) provider).orderSomeEssentia(aspect, amount);
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean takeFromProviderCompat(TileEntity provider, Aspect aspect, int amount) {
        return provider instanceof TileInfusionProvider infusionProvider
            && infusionProvider.takeFromContainer(aspect, amount);
    }

    @Optional.Method(modid = "thaumicenergistics")
    @SuppressWarnings("unchecked")
    private static long returnToProviderCompat(TileEntity provider, Aspect aspect, long amount) {
        if (!(provider instanceof TileInfusionProvider infusionProvider)) return 0;
        try {
            IMEMonitor<AEEssentiaStack> monitor = (IMEMonitor<AEEssentiaStack>) infusionProvider.getProxy()
                .getStorage()
                .getMEMonitor(AEEssentiaStackType.ESSENTIA_STACK_TYPE);
            if (monitor == null) return 0;
            AEEssentiaStack rejected = monitor.injectItems(
                new AEEssentiaStack(aspect, amount),
                Actionable.MODULATE,
                infusionProvider.getMachineSource());
            return amount - (rejected == null ? 0 : rejected.getStackSize());
        } catch (GridAccessException e) {
            return 0;
        }
    }
}
