package com.science.gtnl.compat.bogosorter;

import com.cleanroommc.bogosorter.BogoSortAPI;
import com.science.gtnl.common.gui.container.portableWorkbench.ContainerPortableAvaritiaddonsChest;
import com.science.gtnl.common.gui.container.portableWorkbench.ContainerPortableChest;
import com.science.gtnl.compat.IModCompat;

public class BogoSorterCompat implements IModCompat {

    @Override
    public void postInit() {
        BogoSortAPI.INSTANCE.addGenericCompat(ContainerPortableChest.class);
        BogoSortAPI.INSTANCE.addGenericCompat(ContainerPortableAvaritiaddonsChest.class);
    }
}
