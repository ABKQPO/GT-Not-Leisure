package com.science.gtnl.compat.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.blay09.mods.craftingtweaks.api.SimpleTweakProvider;
import net.minecraft.util.EnumFacing;

import com.science.gtnl.common.gui.container.portableWorkbench.ContainerPortableAdvancedWorkbench;
import com.science.gtnl.compat.IModCompat;
import com.science.gtnl.utils.enums.ModList;

public class CraftingTweaksCompat implements IModCompat {

    @Override
    public void postInit() {
        SimpleTweakProvider provider = CraftingTweaksAPI
            .registerSimpleProvider(ModList.ScienceNotLeisure.ID, ContainerPortableAdvancedWorkbench.class);
        provider.setTweakRotate(true, true, 0, 0);
        provider.setTweakBalance(true, true, 0, 0);
        provider.setTweakClear(true, true, 0, 0);
        provider.setAlignToGrid(EnumFacing.WEST);
    }
}
