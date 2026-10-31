package com.science.gtnl.loader;

import com.science.gtnl.client.equipment.EquipmentClientInput;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EquipmentLoader implements IContentLoader {

    @Override
    public String name() {
        return "equipment";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        EquipmentClientInput.register();
        EquipmentTheme.register();
    }
}
