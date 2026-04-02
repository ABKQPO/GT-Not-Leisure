package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.input.Keyboard;

import com.rwtema.extrautils.gui.GuiTradingPost;
import com.science.gtnl.api.IItemStackExtra;
import com.science.gtnl.api.IKeyHandler;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;
import lombok.val;
import tectech.thing.CustomItemList;

public class Stick extends Item implements IItemStackExtra, IKeyHandler {

    public IIcon defaultIcon;
    public static String id = RESOURCE_ROOT_ID + ":" + "Stick";

    public Stick() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("Stick");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "Stick");
        this.setHasSubtypes(true);
        GameRegistry.registerItem(this, getUnlocalizedName());
        GTNLItemList.Stick.set(new ItemStack(this, 1));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onTooltip(ItemTooltipEvent event) {
        if (!MainConfig.item.stick.enableStickItem) return;
        val item = event.itemStack;
        if (item.getItem() == this && !isShiftDown()) {
            val fake = getDisguisedStack(item);
            val list = event.toolTip;
            String[] oldList = list.toArray(new String[0]);
            byte i = 0;
            list.clear();
            for (String s : oldList) {
                if (i == 0) {
                    String name = fake.getDisplayName();
                    if (fake.hasDisplayName()) {
                        name = EnumChatFormatting.ITALIC + name + EnumChatFormatting.RESET;
                    }
                    int ii;
                    if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
                        String s1 = "";
                        if (!name.isEmpty()) {
                            name = name + " (";
                            s1 = ")";
                        }
                        ii = Item.getIdFromItem(fake.getItem());
                        if (fake.getHasSubtypes()) {
                            name = name + String.format("#%04d/%d%s", ii, fake.getItemDamage(), s1);
                        } else {
                            name = name + String.format("#%04d%s", ii, s1);
                        }
                    } else if (!fake.hasDisplayName() && fake.getItem() == Items.filled_map) {
                        name = name + " #" + fake.getItemDamage();
                    }
                    list.add(name);
                    i++;
                    continue;
                }
                if (i == 1) {
                    fake.getItem()
                        .addInformation(
                            fake,
                            event.entityPlayer,
                            list,
                            Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
                    i++;
                }
                if (id.equals(s)) {
                    list.add(Item.itemRegistry.getNameForObject(fake.getItem()));
                    continue;
                }
                list.add(s);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.defaultIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "Stick");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        if (isShiftDown() || !MainConfig.item.stick.enableStickItem) return defaultIcon;
        ItemStack disguised = getDisguisedStack(stack);
        if (disguised != null) {
            return disguised.getIconIndex();
        }
        return defaultIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        if (isShiftDown() || !MainConfig.item.stick.enableStickItem) return defaultIcon;
        ItemStack disguised = getDisguisedStack(stack);
        if (disguised != null) {
            return disguised.getIconIndex();
        }
        return defaultIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return defaultIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int p_82790_2_) {
        if (isShiftDown() || !MainConfig.item.stick.enableStickItem) {
            return super.getColorFromItemStack(stack, p_82790_2_);
        }
        ItemStack disguised = getDisguisedStack(stack);
        if (disguised != null) {
            return Objects.requireNonNull(disguised.getItem())
                .getColorFromItemStack(stack, p_82790_2_);
        }
        return super.getColorFromItemStack(stack, p_82790_2_);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (isShiftDown() || !MainConfig.item.stick.enableStickItem) {
            return super.getItemStackDisplayName(stack);
        }
        ItemStack disguised = getDisguisedStack(stack);
        if (disguised != null) {
            return disguised.getDisplayName();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public int getDamage(ItemStack stack) {
        if (isShiftDown() || !MainConfig.item.stick.enableStickItem) {
            return super.getDamage(stack);
        }
        ItemStack disguised = getDisguisedStack(stack);
        if (disguised != null) {
            return disguised.getItemDamage();
        }
        return super.getDamage(stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (isShiftDown() || !MainConfig.item.stick.enableStickItem) {
            return super.getRarity(stack);
        }
        ItemStack disguised = getDisguisedStack(stack);
        if (disguised != null) {
            return disguised.getRarity();
        }
        return super.getRarity(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumberExtra(ItemStack stack) {
        if (isShiftDown() || !MainConfig.item.stick.enableStickItem) {
            return super.getSpriteNumber();
        }
        ItemStack disguised = getDisguisedStack(stack);
        if (disguised != null) {
            return disguised.getItemSpriteNumber();
        }
        return super.getSpriteNumber();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
        if (stack.hasTagCompound() && isShiftDown() && MainConfig.item.stick.enableStickItem) {
            ItemStack disguised = getDisguisedStack(stack);
            if (disguised != null) {
                boolean enchanted = stack.getTagCompound()
                    .hasKey("Enchanted")
                    && stack.getTagCompound()
                        .getBoolean("Enchanted");
                list.add(
                    StatCollector.translateToLocalFormatted(
                        enchanted ? "Tooltip_Stick_01" : "Tooltip_Stick_00",
                        disguised.getDisplayName()));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("Enchanted");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isShiftDown() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiMerchant) {
            return false;
        }

        if (Mods.ExtraUtilities.isModLoaded() && isGuiTradingPost(Minecraft.getMinecraft().currentScreen)) {
            return false;
        }

        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    @Optional.Method(modid = "ExtraUtilities")
    public boolean isGuiTradingPost(GuiScreen screen) {
        return screen instanceof GuiTradingPost;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(setDisguisedStack(new ItemStack(Items.diamond)));
        list.add(setDisguisedStack(new ItemStack(Blocks.diamond_block)));
        list.add(setDisguisedStack(CustomItemList.Machine_Multi_EyeOfHarmony.get(1)));
        list.add(setDisguisedStack(CustomItemList.Machine_Multi_ForgeOfGods.get(1)));
        list.add(setDisguisedStack(GTNLItemList.SatietyRing.get(1)));
        list.add(setDisguisedStack(GTNLItemList.VeinMiningPickaxe.get(1)));

        if (Mods.SGCraft.isModLoaded()) {
            list.add(setDisguisedStack(GTModHandler.getModItem(Mods.SGCraft.ID, "stargateRing", 1, 0)));
            list.add(setDisguisedStack(GTModHandler.getModItem(Mods.SGCraft.ID, "stargateRing", 1, 1)));
            list.add(setDisguisedStack(GTModHandler.getModItem(Mods.SGCraft.ID, "stargateBase", 1)));
        }
    }

    public static ItemStack getDisguisedStack(ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        NBTTagCompound tag = stack.getTagCompound();

        String id = tag.getString("ID");
        int meta = tag.getInteger("Meta");

        Item item = (Item) Item.itemRegistry.getObject(id);
        if (item != null) {
            return new ItemStack(item, 1, meta);
        }
        return new ItemStack(Blocks.fire, 1);
    }

    public static ItemStack setDisguisedStack(ItemStack disguised) {
        return setDisguisedStack(disguised, 1, false);
    }

    public static ItemStack setDisguisedStack(ItemStack disguised, int amount, boolean enchanted) {
        if (disguised == null || disguised.getItem() == null) {
            return new ItemStack(Blocks.fire);
        }

        ItemStack stack = new ItemStack(ItemLoader.stick, amount);

        NBTTagCompound tag = new NBTTagCompound();

        String regName = Item.itemRegistry.getNameForObject(disguised.getItem());
        if (regName == null) {
            return stack;
        }

        tag.setString("ID", regName);
        tag.setInteger("Meta", disguised.getItemDamage());
        tag.setBoolean("Enchanted", enchanted);

        stack.setTagCompound(tag);
        return stack;
    }
}
