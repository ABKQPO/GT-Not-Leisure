package com.science.gtnl.common.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.SimpleItem;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap;

public class ItemDebug extends Item {

    public static final ItemDebug INSTANCE = new ItemDebug();
    private static final char[] placeholder = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private ItemDebug() {
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "debug");
        this.setUnlocalizedName("debug");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
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
                if (world.getTileEntity(event.x, event.y, event.z) instanceof TileEntityDireCrafting dc) {
                    var inputs = getInputs(dc);
                    var log = getLog(inputs);
                    ScienceNotLeisure.LOG.info(log.toString());
                    player.addChatMessage(new ChatComponentTranslation("打印成功"));
                    event.setCanceled(true);
                }
            }
        }
    }

    private static @NotNull StringBuilder getLog(SimpleItem[] inputs) {
        var log = new StringBuilder(
            "\nCapture input:ExtremeCraftingManager.getInstance()\n.addExtremeShapedOreRecipe(\n——output——,\n");
        Object2CharMap<SimpleItem> map = new Object2CharOpenHashMap<>();
        for (int i = 0; i < 9; i++) {
            log.append("\"");
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
                var c = placeholder[map.size()];
                map.put(si, c);
                log.append(c);
            }
            log.append("\",\n");
        }
        int i = 0;
        for (Object2CharMap.Entry<SimpleItem> entry : map.object2CharEntrySet()) {
            log.append("\"")
                .append(entry.getCharValue())
                .append("\",\n");
            var si = entry.getKey();
            var ss = Item.itemRegistry.getNameForObject(si.item())
                .split(":");
            var modid = ss[0];
            var name = ss[1];
            if (si.nbt() == null || si.nbt()
                .func_150296_c()
                .isEmpty()) {
                log.append("GTModHandler.getModItem(")
                    .append("\"")
                    .append(modid)
                    .append("\"")
                    .append(',')
                    .append("\"")
                    .append(name)
                    .append("\"");
                if (si.meta() != 0) {
                    log.append(",1,")
                        .append(si.meta());
                }
            } else {
                log.append("ItemUtils.getItemStack(")
                    .append("\"")
                    .append(modid)
                    .append("\"")
                    .append(',')
                    .append("\"")
                    .append(name)
                    .append("\"")
                    .append(",1,")
                    .append(si.meta())
                    .append(",\"")
                    .append(
                        si.nbt()
                            .toString()
                            .replaceAll("\"", "&\""))
                    .append("\"");
            }
            if (++i == map.size()) {
                log.append(")\n);");
            } else {
                log.append(")\n,");
            }
        }
        return log;
    }

    private SimpleItem[] getInputs(TileEntityDireCrafting dc) {
        var inputs = new SimpleItem[81];
        for (int i = 1; i < dc.getSizeInventory(); i++) {
            ItemStack item = dc.getStackInSlot(i);
            inputs[i - 1] = SimpleItem.getInstance(item);
        }
        return inputs;
    }

}
