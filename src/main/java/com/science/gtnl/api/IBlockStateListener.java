package com.science.gtnl.api;

/**
 * Old name of the block-change listener, kept so addons built against earlier GTNL versions still link.
 * <p>
 * The listener now lives in GTNLLib; implement {@link com.gtnllib.world.IBlockStateListener} in new code. Every
 * implementation of this interface is also accepted wherever the GTNLLib listener is.
 *
 * @deprecated use {@link com.gtnllib.world.IBlockStateListener}
 */
@Deprecated
public interface IBlockStateListener extends com.gtnllib.world.IBlockStateListener {
}
