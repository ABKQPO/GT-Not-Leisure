package com.science.gtnl.common.item.items.armor;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoulCardboardArmor extends ItemArmor {

    public static final ArmorMaterial SOUL_CARDBOARD_MATERIAL = EnumHelper
        .addArmorMaterial("SOUL_CARDBOARD", 20, new int[] { 2, 5, 4, 1 }, 20);

    private static final int RENDER_INDEX = 2;

    static {
        MinecraftForge.EVENT_BUS.register(new SoulCardboardArmorHandler());
    }

    private final String itemId;

    public SoulCardboardArmor(String itemId, int armorType, GTNLItemList itemEntry) {
        super(SOUL_CARDBOARD_MATERIAL, RENDER_INDEX, armorType);
        this.itemId = itemId;
        setUnlocalizedName("gtnl." + itemId);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        setTextureName(RESOURCE_ROOT_ID + ":" + itemId);
        GameRegistry.registerItem(this, itemId);
        itemEntry.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl." + itemId;
    }

    public static boolean isCardboardBox(ItemStack stack) {
        return stack != null && BlockLoader.cardboardBox != null
            && stack.getItem() == Item.getItemFromBlock(BlockLoader.cardboardBox);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return isCardboardBox(repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return RESOURCE_ROOT_ID + ":textures/models/armor/soul_cardboard_layer_" + (slot == 2 ? 2 : 1) + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> toolTip, boolean advancedToolTips) {
        toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.gtnl.soul_cardboard.tooltip.0"));
        toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.gtnl.soul_cardboard.tooltip.1"));
        toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.gtnl.soul_cardboard.tooltip.2"));
    }
}
