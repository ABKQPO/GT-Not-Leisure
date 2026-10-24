package com.science.gtnl.client.celestweave;

import com.science.gtnl.common.celestweave.CelestweaveCosmetic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICelestweaveStripEffect {

    /**
     * Sets the colour, and optionally binds a texture and texture coordinate generation, for one glow strip group.
     *
     * @param pose     the wearer's pose for this frame, carrying glow colour and brightness
     * @param cosmetic the chestplate's appearance settings, for effect parameters such as strip speed
     * @param seconds  the wearer's animation clock in seconds
     * @param boneY    the bone's pivot height, used to offset patterns between bones
     * @return true when the effect bound its own texture, so the caller must rebind the glow texture afterwards
     */
    boolean begin(CelestweavePose pose, CelestweaveCosmetic cosmetic, float seconds, float boneY);

    /**
     * Restores any GL state changed by {@link #begin}; called after the strip group is drawn.
     */
    default void end() {}
}
