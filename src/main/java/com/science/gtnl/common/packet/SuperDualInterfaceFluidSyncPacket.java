package com.science.gtnl.common.packet;

import java.io.IOException;

import net.minecraft.client.Minecraft;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.client.gui.GuiSuperDualInterfaceFluid;

import appeng.api.storage.data.IAEFluidStack;
import appeng.util.item.AEFluidStack;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class SuperDualInterfaceFluidSyncPacket extends ClientboundPacket {

    private Int2ObjectMap<IAEFluidStack> stacks = new Int2ObjectOpenHashMap<>();

    public SuperDualInterfaceFluidSyncPacket() {}

    public SuperDualInterfaceFluidSyncPacket(Int2ObjectMap<IAEFluidStack> stacks) {
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
        for (Int2ObjectMap.Entry<IAEFluidStack> entry : Int2ObjectMaps.fastIterable(stacks)) {
            buf.writeInt(entry.getIntKey());
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
            for (Int2ObjectMap.Entry<IAEFluidStack> entry : Int2ObjectMaps.fastIterable(stacks)) {
                gui.update(entry.getIntKey(), entry.getValue());
            }
        }
    }
}
