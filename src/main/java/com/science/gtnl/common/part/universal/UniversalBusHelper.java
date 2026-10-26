package com.science.gtnl.common.part.universal;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;

import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Upgrades;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.parts.IPartHost;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import appeng.core.features.registries.entries.AEFluidTankHandler;
import appeng.tile.inventory.IAEStackInventory;
import appeng.util.IterationCounter;
import appeng.util.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class UniversalBusHelper {

    public static List<IAEStackType<?>> getTypes() {
        return AEStackTypeRegistry.getSortedTypes();
    }

    @SuppressWarnings("rawtypes")
    public static IMEInventory getStorageInventory(TileEntity target, ForgeDirection targetSide, IAEStackType<?> type,
        BaseActionSource source) {
        if (target == null) return null;
        final IExternalStorageHandler handler = AEApi.instance()
            .registries()
            .externalStorage()
            .getHandler(target, targetSide, type, source);
        return handler == null ? null : handler.getInventory(target, targetSide, type, source);
    }

    @SuppressWarnings("rawtypes")
    public static IMEInventory getTransferInventory(TileEntity target, ForgeDirection targetSide, IAEStackType<?> type,
        BaseActionSource source) {
        if (target == null) return null;
        if (type == FLUID_STACK_TYPE) {
            return target instanceof IFluidHandler
                ? AEFluidTankHandler.INSTANCE.getInventory(target, targetSide, type, source)
                : null;
        }
        if (isNetworkHost(target, targetSide)) return null;
        return getStorageInventory(target, targetSide, type, source);
    }

    public static boolean isNetworkHost(TileEntity target, ForgeDirection targetSide) {
        if (target instanceof ITileStorageMonitorable) return true;
        return target instanceof IPartHost host && host.getPart(targetSide) instanceof ITileStorageMonitorable;
    }

    public static long calculateOperations(IUpgradeableHost host) {
        long toSend = switch (host.getInstalledUpgrades(Upgrades.SPEED)) {
            case 1 -> 8;
            case 2 -> 32;
            case 3 -> 64;
            case 4 -> 96;
            default -> 1;
        };

        switch (host.getInstalledUpgrades(Upgrades.SUPERSPEED)) {
            case 1 -> toSend += 16;
            case 2 -> toSend += 16 * 8;
            case 3 -> toSend += 16 * 8 * 8;
            case 4 -> toSend += 16 * 8 * 8 * 8;
        }

        switch (host.getInstalledUpgrades(Upgrades.SUPERLUMINALSPEED)) {
            case 1 -> toSend += 131_072;
            case 2 -> toSend += 131_072 * 8;
            case 3 -> toSend += 131_072 * 8 * 8;
            case 4 -> toSend += 131_072 * 8 * 8 * 8;
        }

        return toSend;
    }

    public static Map<IAEStackType<?>, List<IAEStack<?>>> groupConfig(IAEStackInventory config, int slots) {
        final Map<IAEStackType<?>, List<IAEStack<?>>> result = new Object2ObjectLinkedOpenHashMap<>();
        for (int i = 0; i < config.getSizeInventory() && i < slots; i++) {
            final IAEStack<?> stack = config.getAEStackInSlot(i);
            if (stack == null) continue;
            result.computeIfAbsent(stack.getStackType(), t -> new ObjectArrayList<>())
                .add(stack);
        }
        return result;
    }

    public static boolean hasConfig(IAEStackInventory config, int slots) {
        for (int i = 0; i < config.getSizeInventory() && i < slots; i++) {
            if (config.getAEStackInSlot(i) != null) return true;
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<IAEStack<?>> listAvailable(IMEInventory inventory, IAEStackType<?> type) {
        final IItemList list = inventory.getAvailableItems(type.createList(), IterationCounter.fetchNewId());
        final List<IAEStack<?>> result = new ObjectArrayList<>();
        for (Object stack : list) {
            if (stack instanceof IAEStack<?>aes && aes.getStackSize() > 0) result.add(aes);
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static long importInto(IEnergySource energy, IMEInventory from, IMEInventory network, IAEStack<?> template,
        long amount, BaseActionSource source) {
        if (amount <= 0) return 0;
        final IAEStack request = template.copy();
        request.setStackSize(amount);

        final IAEStack available = from.extractItems(request, Actionable.SIMULATE, source);
        if (available == null || available.getStackSize() <= 0) return 0;

        final IAEStack notStored = network.injectItems(available.copy(), Actionable.SIMULATE, source);
        long canMove = available.getStackSize() - (notStored == null ? 0 : notStored.getStackSize());
        canMove = Math.min(canMove, getPowerLimitedAmount(energy, canMove, template.getAmountPerUnit()));
        if (canMove <= 0) return 0;

        request.setStackSize(canMove);
        final IAEStack extracted = from.extractItems(request, Actionable.MODULATE, source);
        if (extracted == null || extracted.getStackSize() <= 0) return 0;

        final IAEStack left = Platform.poweredInsert(energy, network, extracted, source);
        if (left == null || left.getStackSize() <= 0) return extracted.getStackSize();

        // Give back what the network refused; if the source refuses as well keep it in the network for free.
        final IAEStack returned = from.injectItems(left.copy(), Actionable.MODULATE, source);
        if (returned != null && returned.getStackSize() > 0) {
            network.injectItems(returned, Actionable.MODULATE, source);
        }
        return extracted.getStackSize() - left.getStackSize();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static long exportFrom(IEnergySource energy, IMEInventory network, IMEInventory to, IAEStack<?> template,
        long amount, BaseActionSource source) {
        if (amount <= 0) return 0;
        final IAEStack request = template.copy();
        request.setStackSize(amount);

        final IAEStack notFit = to.injectItems(request.copy(), Actionable.SIMULATE, source);
        final long canFit = amount - (notFit == null ? 0 : notFit.getStackSize());
        if (canFit <= 0) return 0;

        request.setStackSize(canFit);
        final IAEStack extracted = Platform.poweredExtraction(energy, network, request, source);
        if (extracted == null || extracted.getStackSize() <= 0) return 0;

        final IAEStack failed = to.injectItems(extracted.copy(), Actionable.MODULATE, source);
        if (failed == null || failed.getStackSize() <= 0) return extracted.getStackSize();

        network.injectItems(failed, Actionable.MODULATE, source);
        return extracted.getStackSize() - failed.getStackSize();
    }

    public static long getPowerLimitedAmount(IEnergySource energy, long amount, int amountPerUnit) {
        final double power = energy
            .extractAEPower(Platform.ceilDiv(amount, amountPerUnit), Actionable.SIMULATE, PowerMultiplier.CONFIG);
        return Math.min(amount, (long) (power * amountPerUnit + 0.9));
    }
}
