package com.science.gtnl.client.topology;

import com.science.gtnl.api.topology.TopologyNetwork;

import appeng.api.networking.pathing.ControllerState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyPalette {

    public static final int BACKDROP_INNER = 0x111722;
    public static final int BACKDROP_OUTER = 0x07090E;
    public static final int PANEL = 0x11141B;
    public static final int PANEL_RAISED = 0x151922;
    public static final int PANEL_HOVER = 0x161B24;
    public static final int PANEL_SELECTED = 0x142028;
    public static final int STROKE = 0x232834;
    public static final int DIVIDER = 0x1A1E28;
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

    private TopologyPalette() {}

    public static int health(TopologyNetwork network) {
        if (network.controller() == ControllerState.CONTROLLER_CONFLICT || !network.powered()) return BAD;
        if (network.booting() || network.starving() > 0 || network.online() < network.devices()) return WARN;
        return GOOD;
    }

    public static int outline(TopologyNetwork network) {
        if (network.isMain()) return ACCENT;
        int state = health(network);
        return state == GOOD ? NODE_IDLE : state;
    }

    public static int occupancy(float ratio) {
        return ratio > 0.92F ? BAD : ratio > 0.7F ? WARN : GOOD;
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

    public static int mix(int from, int to, float progress) {
        float t = progress <= 0 ? 0 : Math.min(1, progress);
        int red = (int) (((from >> 16) & 0xFF) + (((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * t);
        int green = (int) (((from >> 8) & 0xFF) + (((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * t);
        int blue = (int) ((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
        return (red << 16) | (green << 8) | blue;
    }
}
