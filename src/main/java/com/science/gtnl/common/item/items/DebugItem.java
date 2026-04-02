package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import com.dreammaster.gthandler.CustomItemList;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.SimpleItem;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import it.unimi.dsi.fastutil.objects.Object2CharLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2CharMap;

public class DebugItem extends Item {

    public static Class<?>[] ITEMLIST;

    static {
        List<Class<?>> list = new ArrayList<>();
        list.add(GTNLItemList.class);
        list.add(ItemList.class);
        list.add(tectech.thing.CustomItemList.class);
        list.add(GregtechItemList.class);
        list.add(kubatech.api.enums.ItemList.class);
        if (Mods.NewHorizonsCoreMod.isModLoaded()) addNHItemList(list);
        ITEMLIST = list.toArray(new Class<?>[0]);
    }

    @Optional.Method(modid = "dreamcraft")
    public static void addNHItemList(List<Class<?>> list) {
        list.add(CustomItemList.class);
    }

    public static final char[] placeholder = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz"
        + "0123456789"
        + "()[]{}@#$%^&*:;<>?!").toCharArray();

    public DebugItem() {
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "DebugItem");
        this.setUnlocalizedName("DebugItem");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, getUnlocalizedName());
        GTNLItemList.DebugItem.set(new ItemStack(this, 1));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onItemRightBlock(PlayerInteractEvent event) {
        var world = event.world;
        if (world.isRemote) return;
        var player = event.entityPlayer;
        var item = player.getHeldItem();
        if (item == null) return;
        if (item.getItem() == this) {
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                TileEntity tile = world.getTileEntity(event.x, event.y, event.z);
                if (Mods.Avaritia.isModLoaded() && isDireCraftingTile(tile)) {
                    var inputs = getInputs(tile);
                    var log = getLog(inputs);
                    ScienceNotLeisure.LOG.info(log.toString());
                    player.addChatMessage(new ChatComponentTranslation("打印成功"));
                    event.setCanceled(true);
                }
            }
        }
    }

    @Optional.Method(modid = "Avaritia")
    private boolean isDireCraftingTile(TileEntity tile) {
        return tile instanceof TileEntityDireCrafting;
    }

    public static @NotNull StringBuilder getLog(SimpleItem[] inputs) {
        var log = new StringBuilder(
            "\nExtremeCraftingManager.getInstance()\n    .addExtremeShapedOreRecipe(\n——output——,\n");
        Object2CharMap<SimpleItem> map = new Object2CharLinkedOpenHashMap<>();

        for (int i = 0; i < 9; i++) {
            log.append("    \"");
            for (int j = 0; j < 9; j++) {
                var si = inputs[i * 9 + j];
                if (si == SimpleItem.empty) {
                    log.append("-");
                    continue;
                }
                if (map.containsKey(si)) {
                    log.append(map.getChar(si));
                    continue;
                }

                char c = (map.size() < placeholder.length) ? placeholder[map.size()] : '?';
                map.put(si, c);
                log.append(c);
            }
            log.append("\",\n");
        }

        int i = 0;
        for (Object2CharMap.Entry<SimpleItem> entry : map.object2CharEntrySet()) {
            char symbol = entry.getCharValue();
            SimpleItem si = entry.getKey();

            log.append("    '")
                .append(symbol)
                .append("',\n");
            ItemStack stack = si.toItemStack();
            String stackString = resolveStackToString(stack);
            log.append("    ")
                .append(stackString);
            if (++i == map.size()) {
                log.append("\n);");
            } else {
                log.append(",\n");
            }
        }
        return log;
    }

    public static String resolveStackToString(ItemStack stack) {
        if (stack == null) return "null";

        int[] oreIDs = OreDictionary.getOreIDs(stack);
        if (oreIDs.length > 0) {
            return OreDictionary.getOreName(oreIDs[0]);
        }

        for (Class<?> clazz : ITEMLIST) {
            try {
                for (var field : clazz.getFields()) {
                    Object obj = field.get(null);
                    String s = clazz.getSimpleName() + "." + field.getName() + ".get(1)";
                    if (obj instanceof ItemList itemList && itemList.hasBeenSet()) {
                        ItemStack target = itemList.get(1);
                        if (GTUtility.areStacksEqual(stack, target, true)) {
                            return s;
                        }
                    } else if (obj instanceof GTNLItemList itemList && itemList.hasBeenSet()) {
                        ItemStack target = itemList.get(1);
                        if (GTUtility.areStacksEqual(stack, target, true)) {
                            return s;
                        }
                    } else if (obj instanceof tectech.thing.CustomItemList itemList && itemList.hasBeenSet()) {
                        ItemStack target = itemList.get(1);
                        if (GTUtility.areStacksEqual(stack, target, true)) {
                            return s;
                        }
                    } else if (obj instanceof CustomItemList itemList && itemList.hasBeenSet()) {
                        ItemStack target = itemList.get(1);
                        if (GTUtility.areStacksEqual(stack, target, true)) {
                            return s;
                        }
                    } else if (obj instanceof GregtechItemList itemList && itemList.hasBeenSet()) {
                        ItemStack target = itemList.get(1);
                        if (GTUtility.areStacksEqual(stack, target, true)) {
                            return s;
                        }
                    } else if (obj instanceof kubatech.api.enums.ItemList itemList && itemList.hasBeenSet()) {
                        ItemStack target = itemList.get(1);
                        if (GTUtility.areStacksEqual(stack, target, true)) {
                            return s;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        String regName = Item.itemRegistry.getNameForObject(stack.getItem());
        if (regName != null && regName.startsWith("minecraft:")) {
            String name = regName.substring("minecraft:".length());
            try {
                Field f = Blocks.class.getField(name);
                return "new ItemStack(Blocks." + name + ")";
            } catch (NoSuchFieldException ignored) {}
            try {
                Field f = Items.class.getField(name);
                return "new ItemStack(Items." + name + ")";
            } catch (NoSuchFieldException ignored) {}
        }

        String[] parts = regName != null ? regName.split(":") : new String[] { "unknown", "unknown" };
        String modid = parts[0];
        String itemName = parts.length > 1 ? parts[1] : "unknown";

        String modEnumRef = null;
        for (Mods mod : Mods.values()) {
            if (mod.getID()
                .equalsIgnoreCase(modid)) {
                modEnumRef = "Mods." + mod.name() + ".ID";
                break;
            }
        }

        String modArg = modEnumRef != null ? modEnumRef : "\"" + modid + "\"";

        if (stack.getItemDamage() != 0) {
            return "GTModHandler.getModItem(" + modArg + ", \"" + itemName + "\", 1, " + stack.getItemDamage() + ")";
        } else {
            return "GTModHandler.getModItem(" + modArg + ", \"" + itemName + "\", 1)";
        }
    }

    @Optional.Method(modid = "Avaritia")
    public SimpleItem[] getInputs(TileEntity te) {
        var dc = (TileEntityDireCrafting) te;
        var inputs = new SimpleItem[81];
        for (int i = 1; i < dc.getSizeInventory(); i++) {
            ItemStack item = dc.getStackInSlot(i);
            inputs[i - 1] = SimpleItem.getInstance(item);
        }
        return inputs;
    }

}
