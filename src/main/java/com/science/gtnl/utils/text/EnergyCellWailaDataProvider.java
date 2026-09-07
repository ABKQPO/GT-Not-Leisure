package com.science.gtnl.utils.text;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.google.common.base.Optional;

import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.parts.IPart;
import appeng.core.localization.WailaText;
import appeng.integration.modules.waila.BaseWailaDataProvider;
import appeng.integration.modules.waila.part.PartAccessor;
import appeng.integration.modules.waila.part.Tracer;
import appeng.util.Platform;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class EnergyCellWailaDataProvider extends BaseWailaDataProvider {

    protected static final String CURRENT_POWER_TAG = "gtnlEnergyCellCurrentPower";
    protected static final String MAXIMUM_POWER_TAG = "gtnlEnergyCellMaximumPower";

    protected final PartAccessor partAccessor = new PartAccessor();
    protected final Tracer tracer = new Tracer();

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentToolTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        IAEPowerStorage storage = getPowerStoragePart(accessor.getTileEntity(), accessor.getPosition());
        if (storage == null) return currentToolTip;
        if (storage.getAEMaxPower() <= 0.0) return currentToolTip;

        NBTTagCompound data = accessor.getNBTData();
        if (!data.hasKey(CURRENT_POWER_TAG) || !data.hasKey(MAXIMUM_POWER_TAG)) return currentToolTip;
        ((ITaggedList<String, String>) currentToolTip).removeEntries("RFEnergyStorage");
        currentToolTip
            .add(formatWaila(data.getLong(CURRENT_POWER_TAG) / 100.0, data.getLong(MAXIMUM_POWER_TAG) / 100.0));
        return currentToolTip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x,
        int y, int z) {
        MovingObjectPosition position = tracer.retraceBlock(world, player, x, y, z);
        IAEPowerStorage storage = getPowerStoragePart(tile, position);
        if (storage == null) return tag;

        tag.setLong(CURRENT_POWER_TAG, (long) (100.0 * storage.getAECurrentPower()));
        tag.setLong(MAXIMUM_POWER_TAG, (long) (100.0 * storage.getAEMaxPower()));
        return tag;
    }

    protected IAEPowerStorage getPowerStoragePart(TileEntity tile, MovingObjectPosition position) {
        if (tile == null || position == null) return null;
        Optional<IPart> maybePart = partAccessor.getMaybePart(tile, position);
        if (!maybePart.isPresent() || !(maybePart.get() instanceof IAEPowerStorage storage)) return null;
        return storage;
    }

    protected static String formatWaila(double currentPower, double maximumPower) {
        long current = (long) (100.0 * currentPower);
        long maximum = (long) (100.0 * maximumPower);
        return WailaText.Contains.getLocal() + ": "
            + Platform.formatPowerLong(current, false)
            + " / "
            + Platform.formatPowerLong(maximum, false);
    }
}
