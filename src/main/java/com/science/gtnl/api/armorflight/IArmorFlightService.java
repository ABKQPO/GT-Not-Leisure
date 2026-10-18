package com.science.gtnl.api.armorflight;

import java.util.Collection;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IArmorFlightService {

    /**
     * Flies a suit from an anchor onto a player: every piece rises out of the anchor, launches one after another
     * (boots first, helmet last), arcs round to hover in front of the wearer, unfolds, snaps onto its body slot and
     * locks. Whatever the player wears in those slots is purged at once and flies back to the anchor.
     * <p>
     * The service takes ownership of every stack passed in. A stack that can not be worn in its slot, a delivery
     * whose owner logs out, dies or changes dimension, and every purged piece is handed to the anchor's
     * {@link IArmorFlightHome} if there is one, else to the player's inventory, else dropped - never deleted.
     * Server side only.
     *
     * @param player the wearer
     * @param suit   the pieces to deliver, by slot; null values are skipped
     * @param anchor where the pieces launch from and return to; beyond 64 blocks or in another dimension they
     *               arrive from the sky instead
     * @return ticks until the last piece is worn, or -1 when nothing was launched
     */
    default int summon(EntityPlayerMP player, Map<ArmorFlightPiece, ItemStack> suit, ArmorFlightAnchor anchor) {
        return summon(player, suit, anchor, ArmorFlightApproach.RANDOM);
    }

    /**
     * Same as {@link #summon(EntityPlayerMP, Map, ArmorFlightAnchor)}, choosing the side each piece docks from:
     * in front of the wearer (it hovers facing them, then snaps on), behind them (it swings round, hovers at their
     * back and closes onto them from behind), or a random side per piece.
     *
     * @param player   the wearer
     * @param suit     the pieces to deliver, by slot; null values are skipped
     * @param anchor   where the pieces launch from and return to
     * @param approach the side the pieces dock from
     * @return ticks until the last piece is worn, or -1 when nothing was launched
     */
    int summon(EntityPlayerMP player, Map<ArmorFlightPiece, ItemStack> suit, ArmorFlightAnchor anchor,
        ArmorFlightApproach approach);

    /**
     * Unlocks the listed worn pieces at the same instant: they blow off the body in all directions and then fly
     * back to the anchor, where its {@link IArmorFlightHome} (or the player's inventory) receives them.
     * Server side only.
     *
     * @param player the wearer
     * @param pieces the slots to purge; empty slots are skipped
     * @param anchor where the pieces fly to
     * @return the number of pieces purged
     */
    int purge(EntityPlayerMP player, Collection<ArmorFlightPiece> pieces, ArmorFlightAnchor anchor);

    /**
     * Whether the player has a delivery in flight.
     *
     * @param player the wearer
     * @return true while at least one piece is still on its way to this player
     */
    boolean isDelivering(EntityPlayerMP player);
}
