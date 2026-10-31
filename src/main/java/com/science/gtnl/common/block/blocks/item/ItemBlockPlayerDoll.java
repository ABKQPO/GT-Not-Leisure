package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.PlayerDollGui;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.modularui2.GTModularScreen;

public class ItemBlockPlayerDoll extends ItemBlock implements IGuiHolder<PlayerInventoryGuiData> {

    public static final byte RENDER_OFF = 0;
    public static final byte RENDER_CAPE = 1;
    public static final byte RENDER_ELYTRA = 2;

    public ItemBlockPlayerDoll(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) return;
        if (advancedToolTips && tag.hasKey("CapeHttp", 8)) {
            String capeUrl = tag.getString("CapeHttp");
            if (!StringUtils.isNullOrEmpty(capeUrl)) {
                toolTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.cape_url")
                        + " "
                        + EnumChatFormatting.GOLD
                        + capeUrl);
            }
        }

        if (tag.hasKey("RenderCapeMode", 1)) {
            byte renderMode = tag.getByte("RenderCapeMode");

            String renderStatus = switch (renderMode) {
                case 1 -> StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode.cape");
                case 2 -> StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode.elytra");
                default -> StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode.off");
            };

            toolTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode")
                    + " "
                    + EnumChatFormatting.GOLD
                    + renderStatus);
        }

        if (advancedToolTips && tag.hasKey("SkinHttp", 8)) {
            String skinUrl = tag.getString("SkinHttp");
            if (!StringUtils.isNullOrEmpty(skinUrl)) {
                toolTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.skin_url")
                        + " "
                        + EnumChatFormatting.GOLD
                        + skinUrl);
                return;
            }
        }

        if (advancedToolTips && tag.hasKey("SkullOwner", 8)) {
            String playerName = tag.getString("SkullOwner");
            if (!StringUtils.isNullOrEmpty(playerName)) {
                toolTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.player_name")
                        + " "
                        + EnumChatFormatting.GOLD
                        + playerName);
            }
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> itemStacks) {
        super.getSubItems(item, creativeTabs, itemStacks);
        addDoll(itemStacks, "Huan_F", "§r幻方", RENDER_CAPE);
        addDoll(itemStacks, "Circulation_", "§r流光通明", RENDER_OFF);
        addDoll(itemStacks, "Psimo", "§r赛莫", RENDER_OFF);
        addDoll(itemStacks, "LuoYangYuLi", "§r落阳宇理", RENDER_OFF);
    }

    public void addDoll(List<ItemStack> list, String owner, String name, byte capeMode) {
        list.add(
            PlayerDollData.create()
                .skullOwner(owner)
                .displayName(name)
                .renderCapeMode(capeMode)
                .hideNEI(true)
                .build());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && getMovingObjectPositionFromPlayer(world, player, true) == null) {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new GTModularScreen(mainPanel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new PlayerDollGui(data).build();
    }

    public static NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }

    public static byte getRenderMode(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("RenderCapeMode", 1)) {
                return nbt.getByte("RenderCapeMode");
            }
        }
        return RENDER_OFF;
    }

    public static void setRenderMode(ItemStack stack, byte mode) {
        getOrCreateTag(stack).setByte("RenderCapeMode", mode);
    }

    public static class PlayerDollData {

        private final ItemStack stack = GTNLItemList.PlayerDoll.get(1);
        private final NBTTagCompound nbt;

        public PlayerDollData() {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }
            this.nbt = tag;
        }

        public static PlayerDollData create() {
            return new PlayerDollData();
        }

        public PlayerDollData skullOwner(String name) {
            nbt.setString("SkullOwner", name);
            return this;
        }

        public PlayerDollData skinHttp(String url) {
            nbt.setString("SkinHttp", url);
            return this;
        }

        public PlayerDollData capeHttp(String url) {
            nbt.setString("CapeHttp", url);
            return this;
        }

        public PlayerDollData renderCapeMode(byte value) {
            nbt.setByte("RenderCapeMode", value);
            return this;
        }

        public PlayerDollData hideNEI(boolean value) {
            nbt.setBoolean("HideNEI", value);
            return this;
        }

        public PlayerDollData displayName(String name) {
            stack.setStackDisplayName(name);
            return this;
        }

        public ItemStack build() {
            return stack;
        }
    }
}
