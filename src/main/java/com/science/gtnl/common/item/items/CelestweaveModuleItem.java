package com.science.gtnl.common.item.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CelestweaveModuleItem extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public CelestweaveModuleItem() {
        setUnlocalizedName("gtnl.celestweave_module");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "celestweave_module");
        GTNLItemList.CelestweaveModule.set(new ItemStack(this));
    }

    public ItemStack stack(CelestweaveModule module) {
        return new ItemStack(this, 1, module.meta);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        CelestweaveModule module = CelestweaveModule.byMeta(stack.getItemDamage());
        return "item.gtnl.celestweave_module." + (module != null ? module : CelestweaveModule.byMeta(0)).id;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal(getUnlocalizedName(stack) + ".tooltip"));
        CelestweaveModule module = CelestweaveModule.of(stack);
        if (module == null) return;
        tooltip.add(StatCollector.translateToLocal("item.gtnl.celestweave_module.slots." + module.slots));
        if (!module.holdsItem) return;
        ItemStack held = CelestweaveData.heldOf(stack);
        if (held != null) tooltip
            .add(StatCollector.translateToLocalFormatted("item.gtnl.celestweave_module.held", held.getDisplayName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[CelestweaveModule.metaCount()];
        for (CelestweaveModule module : CelestweaveModule.values()) if (!module.builtIn) icons[module.meta] = register
            .registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":celestweave_module_" + module.id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        IIcon icon = icons[Math.max(0, Math.min(icons.length - 1, meta))];
        return icon != null ? icon : icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        for (CelestweaveModule module : CelestweaveModule.values()) if (!module.builtIn) items.add(stack(module));
    }
}
