package com.science.gtnl.common.wireless;

import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessCopiedLinks.CopiedLink;

public interface IWirelessCopiedLink {

    Map<ForgeDirection, CopiedLink> gtnl$copiedLinks();

    void gtnl$setCopiedLinks(Map<ForgeDirection, CopiedLink> links);
}
