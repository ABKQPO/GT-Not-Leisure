package com.science.gtnl.common.packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.gui.GuiSuperDualInterfaceFluid;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import appeng.api.storage.data.IAEFluidStack;
import appeng.util.item.AEFluidStack;
import io.netty.buffer.ByteBuf;

public class SuperDualInterfaceFluidSyncPacket extends ClientboundPacket {

    private Map<Integer, IAEFluidStack> stacks = new HashMap<>();

    public SuperDualInterfaceFluidSyncPacket() {}

    public SuperDualInterfaceFluidSyncPacket(Map<Integer, IAEFluidStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    protected void read(ByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int key = buf.readInt();
            boolean present = buf.readBoolean();
            try {
                stacks.put(key, present ? AEFluidStack.loadFluidStackFromPacket(buf) : null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(stacks.size());
        for (Map.Entry<Integer, IAEFluidStack> entry : stacks.entrySet()) {
            buf.writeInt(entry.getKey());
            if (entry.getValue() == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                try {
                    entry.getValue()
                        .writeToPacket(buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void handleClient(Minecraft minecraft) {
        if (minecraft.currentScreen instanceof GuiSuperDualInterfaceFluid gui) {
            for (Map.Entry<Integer, IAEFluidStack> entry : stacks.entrySet()) {
                gui.update(entry.getKey(), entry.getValue());
            }
        }
    }
}
