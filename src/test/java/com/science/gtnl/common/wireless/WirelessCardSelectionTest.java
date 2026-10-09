package com.science.gtnl.common.wireless;

import java.util.List;

import com.science.gtnl.common.wireless.WirelessCardSelection.Candidate;

public final class WirelessCardSelectionTest {

    public static void main(String[] args) {
        var enabled = new Candidate<>("enabled", false, true, true, true);
        var disabled = new Candidate<>("disabled", false, true, true, false);
        var unbound = new Candidate<>("unbound", false, true, false, true);
        var foreign = new Candidate<>("foreign", true, false, true, true);
        check(
            WirelessCardSelection.select(List.of(), true)
                .card() == null,
            "No card means no auto binding");
        check(
            "enabled".equals(
                WirelessCardSelection.select(List.of(disabled, unbound, foreign, enabled), true)
                    .card()),
            "Only owned, bound, enabled cards participate in placement");
        var second = new Candidate<>("second", false, true, true, true);
        var multiple = WirelessCardSelection.select(List.of(enabled, second), true);
        check(multiple.card() == null && multiple.ambiguous(), "Multiple candidates never choose an arbitrary slot");
        var held = new Candidate<>("held", true, true, true, true);
        check(
            "held".equals(
                WirelessCardSelection.select(List.of(enabled, second, held), true)
                    .card()),
            "An eligible held card disambiguates placement");
        var heldDisabled = new Candidate<>("held-disabled", true, true, true, false);
        check(
            "enabled".equals(
                WirelessCardSelection.select(List.of(heldDisabled, enabled), true)
                    .card()),
            "A disabled held card cannot auto-connect");
        check(
            "held-disabled".equals(
                WirelessCardSelection.select(List.of(enabled, heldDisabled), false)
                    .card()),
            "The key can turn a disabled held card back on");
        check(
            "unbound".equals(
                WirelessCardSelection.select(List.of(unbound), false)
                    .card()),
            "An unbound card can configure its mode before binding");
        check(
            WirelessCardSelection.select(List.of(enabled, disabled), false)
                .ambiguous(),
            "Toggle selection includes disabled cards rather than guessing which one the player meant");
        check(
            WirelessCardSelection.select(List.of(foreign), false)
                .card() == null,
            "Another player's held card cannot be toggled");
        System.out.println(
            "WirelessCardSelectionTest: ownership, held priority, disabled/unbound cards and ambiguity passed.");
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
