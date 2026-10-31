package com.gtnltest.runtime;

import com.cleanroommc.modularui.api.widget.IWidget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class GuiTestSupport {

    private GuiTestSupport() {}

    static IWidget find(IWidget root, String name) {
        if (name.equals(root.getName())) return root;
        for (IWidget child : root.getChildren()) {
            IWidget match = find(child, name);
            if (match != null) return match;
        }
        return null;
    }

    static int count(IWidget root, Class<? extends IWidget> type) {
        int total = type.isInstance(root) ? 1 : 0;
        for (IWidget child : root.getChildren()) total += count(child, type);
        return total;
    }
}
