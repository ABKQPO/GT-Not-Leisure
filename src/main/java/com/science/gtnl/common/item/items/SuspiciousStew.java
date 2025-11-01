package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.*;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.util.GTModHandler;

public class SuspiciousStew extends ItemFood {

    public static Map<String, PotionEffect> FLOWER_EFFECTS = new HashMap<>();

    public SuspiciousStew() {
        super(3, 0.6F, false);
        this.setUnlocalizedName("SuspiciousStew");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "SuspiciousStew");
        this.setMaxStackSize(1);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GTNLItemList.SuspiciousStew.set(new ItemStack(this, 1));
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("flowerKey")) {
            String key = stack.getTagCompound()
                .getString("flowerKey");
            PotionEffect effect = FLOWER_EFFECTS.get(key);
            if (effect != null) {
                player.addPotionEffect(new PotionEffect(effect));
            }
        }
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onEaten(stack, world, player);
        return new ItemStack(Items.bowl);
    }

    public static ItemStack createStewWithFlower(String flowerKey) {
        ItemStack stew = GTNLItemList.SuspiciousStew.get(1);
        stew.setTagCompound(new NBTTagCompound());
        stew.getTagCompound()
            .setString("flowerKey", flowerKey);
        return stew;
    }

    public static void registerFlower(Block flowerBlock, int meta, PotionEffect effect) {
        registerFlower(Item.getItemFromBlock(flowerBlock), meta, effect);
    }

    public static void registerFlower(Item flowerItem, int meta, PotionEffect effect) {
        String key = getFlowerKey(flowerItem, meta);
        FLOWER_EFFECTS.put(key, effect);

        GTModHandler.addShapelessCraftingRecipe(
            createStewWithFlower(key),
            new Object[] { new ItemStack(Items.bowl), new ItemStack(Blocks.red_mushroom),
                new ItemStack(Blocks.brown_mushroom), new ItemStack(flowerItem, 1, meta) });
    }

    public static String getFlowerKey(Item item, int meta) {
        return item.getUnlocalizedName() + ":" + meta;
    }
}
