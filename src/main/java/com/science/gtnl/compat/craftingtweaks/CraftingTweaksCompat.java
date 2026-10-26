package com.science.gtnl.compat.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.util.EnumFacing;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.container.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.compat.IModCompat;

public class CraftingTweaksCompat implements IModCompat {

    @Override
    public void postInit() {
        SimpleTweakProvider provider = CraftingTweaksAPI
            .registerSimpleProvider(ScienceNotLeisure.MODID, ContainerPortableAdvancedWorkbench.class);
        provider.setTweakRotate(true, true, 0, 0);
        provider.setTweakBalance(true, true, 0, 0);
        provider.setTweakClear(true, true, 0, 0);
        provider.setAlignToGrid(EnumFacing.WEST);
    }
}
