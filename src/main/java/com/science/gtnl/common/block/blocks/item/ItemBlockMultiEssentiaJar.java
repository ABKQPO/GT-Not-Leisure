package com.science.gtnl.common.block.blocks.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.gui.MultiEssentiaJarGui;
import com.science.gtnl.common.packet.OpenMultiEssentiaJarGuiPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.INetworkUpdatableItem;
import gregtech.api.interfaces.item.IPickBlockHandler;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.modularui2.GTModularScreen;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ItemBlockMultiEssentiaJar extends ItemBlock
    implements IPickBlockHandler, INetworkUpdatableItem, IGuiHolder<PlayerInventoryGuiData> {

    private static final int MAX_DISPLAYED_ASPECTS = 8;

    public static final String SELECTED_ASPECT_PACKET_KEY = "SelectedAspect";

    public static ChatComponentTranslation createServerAspectDisplay(EntityPlayer player, Aspect aspect, int amount) {

        boolean discovered = aspect != null
            && ThaumcraftApiHelper.hasDiscoveredAspect(player.getCommandSenderName(), aspect);

        if (!discovered) {
            return new ChatComponentTranslation("GTNL.gui.multi_essentia_jar.aspect_unknown", amount);
        }

        return new ChatComponentTranslation(
            "GTNL.gui.multi_essentia_jar.aspect",
            new ChatComponentTranslation("tc.aspect." + aspect.getTag()),
            aspect.getName(),
            amount);
    }

    public ItemBlockMultiEssentiaJar(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isSneaking()) return stack;

        if (!world.isRemote) {
            Aspect activeAspect = TileEntityMultiEssentiaJar.cycleActiveAspect(stack);
            sendActiveAspectStatus(player, stack, activeAspect);
            if (activeAspect != null) {
                player.inventoryContainer.detectAndSendChanges();
            }
        }
        player.swingItem();
        return stack;
    }

    @Override
    public boolean onPickBlock(ItemStack stack, EntityPlayer player) {
        if (TileEntityMultiEssentiaJar.getStoredAspects(stack)
            .visSize() <= 0) {
            player.addChatMessage(
                new ChatComponentTranslation("Info_MultiEssentiaJar_Empty", TileEntityMultiEssentiaJar.MAX_CAPACITY));
            return true;
        }

        ScienceNotLeisure.network.sendToServer(new OpenMultiEssentiaJarGuiPacket());
        return true;
    }

    public static void sendActiveAspectStatus(EntityPlayer player, ItemStack stack, Aspect activeAspect) {

        if (activeAspect == null) {
            player.addChatMessage(
                new ChatComponentTranslation("Info_MultiEssentiaJar_Empty", TileEntityMultiEssentiaJar.MAX_CAPACITY));
            return;
        }

        AspectList storedAspects = TileEntityMultiEssentiaJar.getStoredAspects(stack);

        player.addChatMessage(
            new ChatComponentTranslation(
                "Info_MultiEssentiaJar_ItemActive",
                createServerAspectDisplay(player, activeAspect, storedAspects.getAmount(activeAspect))));
    }

    @Override
    public boolean receive(ItemStack stack, EntityPlayerMP player, NBTTagCompound tag) {
        if (tag == null || !tag.hasKey(SELECTED_ASPECT_PACKET_KEY, Constants.NBT.TAG_STRING)) return true;

        Aspect selectedAspect = Aspect.getAspect(tag.getString(SELECTED_ASPECT_PACKET_KEY));
        if (TileEntityMultiEssentiaJar.setActiveAspect(stack, selectedAspect)) {
            player.inventoryContainer.detectAndSendChanges();
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new GTModularScreen(mainPanel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new MultiEssentiaJarGui(data.getUsedItemStack()).build();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "Tooltip_MultiEssentiaJar_Capacity",
                TileEntityMultiEssentiaJar.MAX_CAPACITY));
        tooltip.add(StatCollector.translateToLocal("Tooltip_MultiEssentiaJar_Transport"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_MultiEssentiaJar_Cycle"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_MultiEssentiaJar_Select"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_MultiEssentiaJar_Clear"));

        AspectList storedAspects = TileEntityMultiEssentiaJar.getStoredAspects(stack);

        Aspect activeAspect = TileEntityMultiEssentiaJar.getActiveAspect(stack);

        Aspect[] aspects = storedAspects.aspects.keySet()
            .stream()
            .filter(aspect -> aspect != null && storedAspects.getAmount(aspect) > 0)
            .sorted((first, second) -> {
                if (first == second) return 0;

                // 当前选中的源质始终置顶
                if (first == activeAspect) return -1;
                if (second == activeAspect) return 1;

                // 其余源质按照数量从多到少排列
                int amountComparison = Integer.compare(storedAspects.getAmount(second), storedAspects.getAmount(first));

                if (amountComparison != 0) {
                    return amountComparison;
                }

                // 数量相同时按照拉丁标签排序，保证顺序稳定
                return first.getTag()
                    .compareTo(second.getTag());
            })
            .toArray(Aspect[]::new);

        if (aspects.length == 0) return;

        int total = Arrays.stream(aspects)
            .mapToInt(storedAspects::getAmount)
            .sum();
        tooltip.add(StatCollector.translateToLocalFormatted("Tooltip_MultiEssentiaJar_Stored", total, aspects.length));

        for (int i = 0; i < Math.min(aspects.length, MAX_DISPLAYED_ASPECTS); i++) {
            Aspect aspect = aspects[i];
            String marker = aspect == activeAspect ? "§e▶ " : "§7  ";
            int amount = storedAspects.getAmount(aspect);

            tooltip.add(marker + getClientAspectDisplay(aspect, amount));
        }
        if (aspects.length > MAX_DISPLAYED_ASPECTS) {
            tooltip.add(
                StatCollector.translateToLocalFormatted(
                    "Tooltip_MultiEssentiaJar_More",
                    aspects.length - MAX_DISPLAYED_ASPECTS));
        }
    }

    @SideOnly(Side.CLIENT)
    private static String getClientAspectDisplay(Aspect aspect, int amount) {
        ItemStack ariAspectStack = new ItemStack(ModItems.itemAspect);
        ItemAspect.setAspect(ariAspectStack, aspect);

        // 由 ARI 判断并返回 Aqua 或“未知要素”
        String ariDisplayName = ariAspectStack.getDisplayName();
        String unknownName = StatCollector.translateToLocal("tc.aspect.unknown");

        if (unknownName.equals(ariDisplayName)) {
            return StatCollector.translateToLocalFormatted("GTNL.gui.multi_essentia_jar.aspect_unknown", amount);
        }

        return StatCollector.translateToLocalFormatted(
            "GTNL.gui.multi_essentia_jar.aspect",
            aspect.getLocalizedDescription(),
            ariDisplayName,
            amount);
    }

}
