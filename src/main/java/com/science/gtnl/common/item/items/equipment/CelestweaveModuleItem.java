package com.science.gtnl.common.item.items.equipment;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.equipment.MetaLookup;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CelestweaveModuleItem extends EquipmentVariantItem<CelestweaveModule> {

    public CelestweaveModuleItem() {
        GTNLItemList.CelestweaveModule.set(new ItemStack(this));
    }

    @Override
    protected String name() {
        return "celestweave_module";
    }

    @Override
    protected CelestweaveModule[] variants() {
        return CelestweaveModule.values();
    }

    @Override
    protected MetaLookup<CelestweaveModule> lookup() {
        return CelestweaveModule.LOOKUP;
    }

    @Override
    protected boolean listed(CelestweaveModule module) {
        return !module.builtIn;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal(getUnlocalizedName(stack) + ".tooltip"));
        CelestweaveModule module = CelestweaveModule.of(stack);
        if (module == null) return;
        tooltip.add(StatCollector.translateToLocal("item.gtnl.celestweave_module.slots." + module.pieceMask));
        if (!module.holdsItem) return;
        ItemStack held = CelestweaveData.heldOf(stack);
        if (held != null) tooltip
            .add(StatCollector.translateToLocalFormatted("item.gtnl.celestweave_module.held", held.getDisplayName()));
    }
}
