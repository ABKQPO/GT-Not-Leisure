package com.science.gtnl.client.topology;

import com.science.gtnl.api.topology.TopologyNetwork;

import appeng.api.networking.pathing.ControllerState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyPalette {

    public static final int BACKDROP_INNER = 0x111722;
    public static final int BACKDROP_OUTER = 0x07090E;
    public static final int METER_TRACK = 0x222736;

    public static final int TEXT = 0xE6E9F0;
    public static final int TEXT_MUTED = 0x7B8396;
    public static final int TEXT_DIM = 0x4A5163;

    public static final int ACCENT = 0x3FD0E8;
    public static final int ACCENT_DEEP = 0x1D6F80;
    public static final int GOOD = 0x5AD19A;
    public static final int WARN = 0xE8C05A;
    public static final int BAD = 0xE8685A;

    public static final int NODE_IDLE = 0x8FA2BD;
    public static final int NODE_SHELL = 0x0E131B;
    public static final int RING_GUIDE = 0x50607C;

    /**
     * Devices hang off their own network with a neutral slate spoke. Cross network links keep the saturated link kind
     * colours, so the two classes of line are never confused for one another.
     */
    public static final int SPOKE = 0x6E7F99;

    /**
     * The main network's own tint. It deliberately avoids the link palette so the root never reads as a link colour.
     */
    public static final int MAIN = 0xF2F7FF;
    public static final int CHROME_FILL = 0x1A2230;
    public static final int CHROME_ACTIVE = 0x24435A;

    private TopologyPalette() {}

    public static int health(TopologyNetwork network) {
        if (network.controller() == ControllerState.CONTROLLER_CONFLICT || !network.powered()) return BAD;
        if (network.booting() || network.starving() > 0 || network.online() < network.devices()) return WARN;
        return GOOD;
    }

    /**
     * Health always wins. Marking the main network with the accent colour before checking it would paint a conflicted
     * or starving main network the same calm blue as a healthy one, which is exactly when the warning matters most.
     */
    public static int outline(TopologyNetwork network) {
        int state = health(network);
        if (state != GOOD) return state;
        return network.isMain() ? MAIN : NODE_IDLE;
    }

    /**
     * Node tint by health alone. Healthy is green rather than the old accent blue, which keeps every node clear of the
     * cross network link hues.
     */
    public static int deviceState(boolean active, boolean channelOk) {
        if (!active) return BAD;
        return channelOk ? GOOD : WARN;
    }

    public static int availability(float ratio) {
        return ratio >= 0.999F ? GOOD : ratio >= 0.75F ? WARN : BAD;
    }

    public static int withAlpha(int rgb, float alpha) {
        int clamped = alpha <= 0 ? 0 : alpha >= 1 ? 255 : (int) (alpha * 255.0F + 0.5F);
        return (clamped << 24) | (rgb & 0xFFFFFF);
    }

    public static int opaque(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }
}
