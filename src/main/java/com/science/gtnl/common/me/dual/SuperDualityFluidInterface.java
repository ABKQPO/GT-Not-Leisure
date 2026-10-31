package com.science.gtnl.common.me.dual;

import java.lang.reflect.Field;

import com.glodblock.github.inventory.AEFluidInventory;
import com.glodblock.github.util.DualityFluidInterface;

import appeng.api.storage.data.IAEFluidStack;
import appeng.helpers.IInterfaceHost;
import appeng.me.helpers.AENetworkProxy;
import lombok.Getter;

@Getter
public class SuperDualityFluidInterface extends DualityFluidInterface {

    private final int tankSlots;

    public SuperDualityFluidInterface(AENetworkProxy networkProxy, IInterfaceHost host, int tankSlots) {
        super(networkProxy, host);
        this.tankSlots = tankSlots;
        replaceInternalField("tanks", new AEFluidInventory(this, tankSlots, (int) TANK_CAPACITY));
        replaceInternalField("config", new AEFluidInventory(this, tankSlots, Integer.MAX_VALUE));
        replaceInternalField("requireWork", new IAEFluidStack[tankSlots]);
    }

    private void replaceInternalField(String fieldName, Object value) {
        try {
            Field field = DualityFluidInterface.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to replace DualityFluidInterface field: " + fieldName, e);
        }
    }
}
