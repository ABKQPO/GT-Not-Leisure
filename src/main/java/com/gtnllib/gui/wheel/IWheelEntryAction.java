package com.gtnllib.gui.wheel;

@FunctionalInterface
public interface IWheelEntryAction {

    /**
     * Runs when the entry is chosen, by click in tap mode or by releasing the hold key in hold mode.
     * The wheel starts its closing animation right after this returns.
     */
    void trigger(WheelActionContext context);
}
