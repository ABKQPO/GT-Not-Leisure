package com.science.gtnl.api.topology;

import net.minecraft.item.ItemStack;

/**
 * One end of a link that carries a grid across a gap instead of through adjacent blocks: a wireless connector or hub,
 * a quantum ring, an ME tunnel mouth.
 * <p>
 * Both ends are nodes in their own right. The near end hangs off whatever it sits behind, the far end hangs off the
 * near end, and everything the far end reaches hangs off the far end in turn. That is what makes a hub fanning out to
 * several connectors read as the structure it actually is, rather than as a badge pinned to the network.
 */
public record TopologyRelay(int id, int network, int parent, GridCoord position, ItemStack icon,
    ItemStack dimensionIcon, TopologyLinkKind kind, boolean active) {

    /**
     * Parent value for a relay that hangs directly off its network rather than off another relay.
     */
    public static final int ROOT = -1;

    public boolean attachedToNetwork() {
        return parent == ROOT;
    }
}
