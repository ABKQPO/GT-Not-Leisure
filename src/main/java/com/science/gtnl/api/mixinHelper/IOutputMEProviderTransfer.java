package com.science.gtnl.api.mixinHelper;

import appeng.api.storage.data.IAEStack;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;

public interface IOutputMEProviderTransfer<T extends IAEStack<T>> {

    boolean gtnl$transferCacheTo(MTEHatchOutputMEBase<T> targetProvider);
}
