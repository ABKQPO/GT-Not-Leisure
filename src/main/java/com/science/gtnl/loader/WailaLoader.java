package com.science.gtnl.loader;

import java.util.Arrays;
import java.util.List;

import net.minecraft.tileentity.TileEntity;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.common.block.blocks.tile.TileEntityCardboardBox;
import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.text.CardboardBoxWailaDataProvider;
import com.science.gtnl.utils.text.EnergyCellWailaDataProvider;
import com.science.gtnl.utils.text.PlayerDollWailaDataProvider;

import appeng.tile.AEBaseTile;
import codechicken.multipart.TileMultipart;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import gregtech.api.enums.Mods;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaLoader {

    public static void callbackRegister(IWailaRegistrar registrar) {
        List<ProviderEntry<?>> entries = Arrays.asList(
            new ProviderEntry<>(new PlayerDollWailaDataProvider(), TileEntityPlayerDoll.class),
            new ProviderEntry<>(new CardboardBoxWailaDataProvider(), TileEntityCardboardBox.class));

        for (ProviderEntry<?> entry : entries) {
            registrar.registerBodyProvider(entry.provider(), entry.clazz());
            registrar.registerNBTProvider(entry.provider(), entry.clazz());
            registrar.registerTailProvider(entry.provider(), entry.clazz());
        }

        registerEnergyCellProvider(registrar, AEBaseTile.class);
        if (ModList.ForgeMultipart.isModLoaded()) registerForgeMultipartEnergyCellProvider(registrar);
    }

    public static void register() {
        FMLInterModComms.sendMessage(Mods.Waila.ID, "register", WailaLoader.class.getName() + ".callbackRegister");
    }

    @Optional.Method(modid = ModList.ModIds.FORGE_MULTIPART)
    public static void registerForgeMultipartEnergyCellProvider(IWailaRegistrar registrar) {
        registerEnergyCellProvider(registrar, TileMultipart.class);
    }

    public static void registerEnergyCellProvider(IWailaRegistrar registrar, Class<?> hostClass) {
        EnergyCellWailaDataProvider provider = new EnergyCellWailaDataProvider();
        registrar.registerBodyProvider(provider, hostClass);
        registrar.registerNBTProvider(provider, hostClass);
    }

    @Desugar
    public record ProviderEntry<T extends TileEntity> (IWailaDataProvider provider, Class<T> clazz) {}

}
