package com.science.gtnl.common.item.items.armor;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.EnumHelper;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.client.model.MajoArmorModel;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;

public abstract class MajoArmor extends ItemArmor implements IVisDiscountGear {

    private static final ArmorMaterial MAJO_MATERIAL = EnumHelper
        .addArmorMaterial("GTNL_MAJO", 0, new int[] { 7, 10, 0, 0 }, 25);

    private final String itemId;
    private final int visDiscount;

    protected MajoArmor(String itemId, int armorType, int visDiscount, GTNLItemList itemEntry) {
        super(MAJO_MATERIAL, 0, armorType);
        this.itemId = itemId;
        this.visDiscount = visDiscount;
        setMaxDamage(0);
        setMaxStackSize(1);
        setUnlocalizedName("gtnl." + itemId);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        setTextureName(RESOURCE_ROOT_ID + ":" + itemId);
        GameRegistry.registerItem(this, itemId);
        itemEntry.set(new ItemStack(this));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl." + itemId;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return visDiscount;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        String texture = armorType == 0 ? "majo_hat" : "majo_cloth";
        return RESOURCE_ROOT_ID + ":textures/models/armor/" + texture + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
        MajoArmorModel model = armorType == 0 ? MajoArmorModel.HAT : MajoArmorModel.ROBE;
        model.isSneak = entity.isSneaking();
        model.isRiding = entity.isRiding();
        model.isChild = entity.isChild();
        return model;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        addVisDiscountTooltip(tooltip);
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.gtnl." + itemId + ".tooltip"));
    }

    @SideOnly(Side.CLIENT)
    protected final void addVisDiscountTooltip(List<String> tooltip) {
        tooltip.add(EnumChatFormatting.DARK_PURPLE + getVisDiscountTooltipText());
    }

    @SideOnly(Side.CLIENT)
    protected final String getVisDiscountTooltipText() {
        return StatCollector.translateToLocalFormatted("item.gtnl.majo_armor.vis_discount", visDiscount);
    }
}
