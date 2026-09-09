package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.render.IHaloRenderItem;

@Optional.Interface(iface = "fox.spiteful.avaritia.render.IHaloRenderItem", modid = "Avaritia")
public class TestItem extends Item implements IHaloRenderItem {

    public IIcon[] halo;

    public TestItem() {
        super();
        this.setUnlocalizedName("gtnl.test_item");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "test_item");
        GameRegistry.registerItem(this, "test_item");
        GTNLItemList.TestItem.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.test_item";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        super.registerIcons(ir);
        halo = new IIcon[1];
        halo[0] = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "halonoise");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {

        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.0"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.1"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.2"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.3"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.4"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.5"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.6"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.7"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.8"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.9"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.10"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.11"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.12"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.test_item.tooltip.13"));

    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            playerIn.addPotionEffect(new PotionEffect(EffectLoader.awe.id, 6000, 1));
        }

        stack.splitStack(1);
        return stack;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public boolean drawHalo(ItemStack stack) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public IIcon getHaloTexture(ItemStack stack) {
        return halo[0];
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public int getHaloSize(ItemStack stack) {
        return 10;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public boolean drawPulseEffect(ItemStack stack) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public int getHaloColour(ItemStack stack) {
        return 0xE6FFFFFF;
    }

}
