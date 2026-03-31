package com.science.gtnl.common.part;

import java.io.IOException;

import net.minecraft.item.ItemStack;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.events.MENetworkBootingStatusChange;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.me.GridAccessException;
import appeng.parts.AEBasePart;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

public abstract class GTNLBasePartState extends AEBasePart implements IPowerChannelState {

    public static final int POWERED_FLAG = 1;

    @Getter
    @Setter
    public int clientFlags = 0; // sent as byte.

    public GTNLBasePartState(ItemStack is) {
        super(is);
    }

    @MENetworkEventSubscribe
    public void chanRender(final MENetworkChannelsChanged c) {
        this.getHost()
            .markForUpdate();
    }

    @MENetworkEventSubscribe
    public void powerRender(final MENetworkPowerStatusChange c) {
        this.getHost()
            .markForUpdate();
    }

    @MENetworkEventSubscribe
    public void bootingRender(final MENetworkBootingStatusChange bs) {
        this.getHost()
            .markForUpdate();
    }

    @Override
    public void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);

        this.setClientFlags(0);

        try {
            if (this.getProxy()
                .getEnergy()
                .isNetworkPowered()) {
                this.setClientFlags(this.clientFlags | POWERED_FLAG);
            }

            this.setClientFlags(this.populateFlags(this.clientFlags));
        } catch (final GridAccessException e) {
            // meh
        }

        data.writeByte((byte) this.clientFlags);
    }

    public int populateFlags(final int cf) {
        return cf;
    }

    @Override
    public boolean readFromStream(final ByteBuf data) throws IOException {
        final var eh = super.readFromStream(data);

        final var old = this.clientFlags;
        this.setClientFlags(data.readByte());

        return eh || old != this.clientFlags;
    }

    @Override
    public boolean isPowered() {
        return (this.clientFlags & POWERED_FLAG) == POWERED_FLAG;
    }

    @Override
    public boolean isActive() {
        return this.isPowered();
    }
}
