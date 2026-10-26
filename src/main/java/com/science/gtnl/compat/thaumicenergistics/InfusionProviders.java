package com.science.gtnl.compat.thaumicenergistics;

import net.minecraft.tileentity.TileEntity;

import appeng.api.config.Actionable;
import appeng.api.storage.IMEMonitor;
import appeng.me.GridAccessException;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.storage.AEEssentiaStack;
import thaumicenergistics.common.storage.AEEssentiaStackType;
import thaumicenergistics.common.tiles.TileAdvancedInfusionProvider;
import thaumicenergistics.common.tiles.TileEssentiaProvider;
import thaumicenergistics.common.tiles.TileInfusionProvider;

public class InfusionProviders {

    public static final String INFUSION_PROVIDER_NAME_KEY = "thaumicenergistics.block.infusion.provider.name";
    public static final String ADVANCED_INFUSION_PROVIDER_NAME_KEY = "thaumicenergistics.block.advanced.infusion.provider.name";

    private InfusionProviders() {}

    public static boolean isAvailable() {
        return Mods.ThaumicEnergistics.isModLoaded();
    }

    public static boolean isEssentiaProvider(TileEntity tile) {
        return tile != null && isAvailable() && essentiaProvider(tile);
    }

    public static boolean isInfusionProvider(TileEntity tile) {
        return isAvailable() && infusionProvider(tile);
    }

    public static long networkAmount(TileEntity provider, Aspect aspect) {
        return isAvailable() ? amountInNetwork(provider, aspect) : 0;
    }

    public static boolean order(TileEntity provider, Aspect aspect, int amount) {
        return isAvailable() && orderEssentia(provider, aspect, amount);
    }

    public static boolean take(TileEntity provider, Aspect aspect, int amount) {
        return isAvailable() && takeEssentia(provider, aspect, amount);
    }

    public static long giveBack(TileEntity provider, Aspect aspect, long amount) {
        return amount > 0 && isAvailable() ? returnEssentia(provider, aspect, amount) : 0;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean essentiaProvider(TileEntity tile) {
        return tile instanceof TileEssentiaProvider;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean infusionProvider(TileEntity tile) {
        return tile instanceof TileInfusionProvider;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean usable(TileEntity provider) {
        if (provider instanceof TileAdvancedInfusionProvider advancedProvider) {
            return advancedProvider.matrices.isEmpty();
        }
        return provider instanceof TileInfusionProvider;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static long amountInNetwork(TileEntity provider, Aspect aspect) {
        return usable(provider) ? ((TileInfusionProvider) provider).getAspectAmountInNetwork(aspect) : 0;
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean orderEssentia(TileEntity provider, Aspect aspect, int amount) {
        return usable(provider) && ((TileInfusionProvider) provider).orderSomeEssentia(aspect, amount);
    }

    @Optional.Method(modid = "thaumicenergistics")
    private static boolean takeEssentia(TileEntity provider, Aspect aspect, int amount) {
        return provider instanceof TileInfusionProvider infusionProvider
            && infusionProvider.takeFromContainer(aspect, amount);
    }

    @Optional.Method(modid = "thaumicenergistics")
    @SuppressWarnings("unchecked")
    private static long returnEssentia(TileEntity provider, Aspect aspect, long amount) {
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
