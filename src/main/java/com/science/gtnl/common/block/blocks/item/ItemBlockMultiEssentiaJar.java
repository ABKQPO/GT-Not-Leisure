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
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.block.blocks.BlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.gui.MultiEssentiaJarGui;
import com.science.gtnl.utils.AspectTooltipUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.INetworkUpdatableItem;
import gregtech.api.interfaces.item.IPickBlockHandler;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.modularui2.GTModularScreen;
import gregtech.crossmod.backhand.Backhand;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ItemBlockMultiEssentiaJar extends ItemBlock
    implements IPickBlockHandler, INetworkUpdatableItem, IGuiHolder<PlayerInventoryGuiData> {

    private static final int MAX_DISPLAYED_ASPECTS = 8;

    public static final String CYCLE_PREVIOUS_ASPECT_PACKET_KEY = "CyclePreviousAspect";

    public ItemBlockMultiEssentiaJar(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isSneaking()) return stack;

        if (!world.isRemote) {
            cycleHeldAspect(stack, player, false);
        }
        player.swingItem();
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean onPickBlock(ItemStack stack, EntityPlayer player) {
        if (TileEntityMultiEssentiaJar.getStoredAspects(stack)
            .visSize() <= 0) {
            AspectTooltipUtils.sendEmptyJarStatus(player, TileEntityMultiEssentiaJar.MAX_CAPACITY);
            return true;
        }

        // 通过 MUI2 自带的客户端→服务端打开机制请求打开手持罐 GUI
        GuiFactories.playerInventory()
            .openFromPlayerInventoryClient(getUsedSlot(player, stack));
        return true;
    }

    @SideOnly(Side.CLIENT)
    private static int getUsedSlot(EntityPlayer player, ItemStack stack) {
        if (player.getCurrentEquippedItem() == stack) return player.inventory.currentItem;
        if (Backhand.getOffhandItem(player) == stack) return Backhand.getOffhandSlot(player);
        return player.inventory.currentItem;
    }

    @Override
    public boolean receive(ItemStack stack, EntityPlayerMP player, NBTTagCompound tag) {
        if (tag == null || !tag.hasKey(CYCLE_PREVIOUS_ASPECT_PACKET_KEY, Constants.NBT.TAG_BYTE)) return true;

        cycleHeldAspect(stack, player, true);
        return true;
    }

    private static void cycleHeldAspect(ItemStack stack, EntityPlayer player, boolean previous) {
        Aspect previousAspect = TileEntityMultiEssentiaJar.getActiveAspect(stack);
        Aspect activeAspect = previous ? TileEntityMultiEssentiaJar.cyclePreviousActiveAspect(stack)
            : TileEntityMultiEssentiaJar.cycleActiveAspect(stack);

        if (activeAspect != null && activeAspect != previousAspect) {
            BlockMultiEssentiaJar.playEssentiaSlosh(player);
        }

        sendActiveAspectStatus(player, stack, activeAspect);

        if (activeAspect != null) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    private static void sendActiveAspectStatus(EntityPlayer player, ItemStack stack, Aspect activeAspect) {
        if (activeAspect == null) {
            AspectTooltipUtils.sendEmptyJarStatus(player, TileEntityMultiEssentiaJar.MAX_CAPACITY);
            return;
        }

        AspectList storedAspects = TileEntityMultiEssentiaJar.getStoredAspects(stack);

        player.addChatMessage(
            new ChatComponentTranslation(
                "Info_MultiEssentiaJar_ItemActive",
                AspectTooltipUtils
                    .createServerAspectDisplay(player, activeAspect, storedAspects.getAmount(activeAspect))));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new GTModularScreen(mainPanel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new MultiEssentiaJarGui(data.getUsedItemStack(), syncManager).build();
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
        tooltip.add(StatCollector.translateToLocal("Tooltip_MultiEssentiaJar_Filter"));

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

            tooltip.add(marker + AspectTooltipUtils.getClientAspectDisplay(aspect, amount));
        }
        if (aspects.length > MAX_DISPLAYED_ASPECTS) {
            tooltip.add(
                StatCollector.translateToLocalFormatted(
                    "Tooltip_MultiEssentiaJar_More",
                    aspects.length - MAX_DISPLAYED_ASPECTS));
        }
    }

}
