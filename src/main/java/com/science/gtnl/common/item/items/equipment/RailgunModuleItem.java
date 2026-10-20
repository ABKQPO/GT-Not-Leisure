package com.science.gtnl.common.item.items.equipment;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.equipment.MetaLookup;
import com.science.gtnl.common.railgun.RailgunModuleType;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RailgunModuleItem extends EquipmentVariantItem<RailgunModuleType> {

    public RailgunModuleItem() {
        GTNLItemList.RailgunModule.set(new ItemStack(this));
    }

    @Override
    protected String name() {
        return "railgun_module";
    }

    @Override
    protected RailgunModuleType[] variants() {
        return RailgunModuleType.values();
    }

    @Override
    protected MetaLookup<RailgunModuleType> lookup() {
        return RailgunModuleType.LOOKUP;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal(getUnlocalizedName(stack) + ".tooltip.0"));
        tooltip.add(StatCollector.translateToLocal("item.gtnl.railgun.module_install"));
    }
}
