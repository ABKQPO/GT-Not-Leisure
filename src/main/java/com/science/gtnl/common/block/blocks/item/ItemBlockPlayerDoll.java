package com.science.gtnl.common.block.blocks.item;

import java.util.Collections;
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
import net.minecraftforge.common.MinecraftForge;

import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.IItemWithModularUI;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.VanillaButtonWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.gui.modularui.GTUIInfos;
import gregtech.api.gui.modularui.GTUITextures;

public class ItemBlockPlayerDoll extends ItemBlock implements IItemWithModularUI {

    public static final byte RENDER_OFF = 0;
    public static final byte RENDER_CAPE = 1;
    public static final byte RENDER_ELYTRA = 2;

    public ItemBlockPlayerDoll(Block block) {
        super(block);
        MinecraftForge.EVENT_BUS.register(this);
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
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_02")
                        + EnumChatFormatting.GOLD
                        + capeUrl);
            }
        }

        if (tag.hasKey("RenderCapeMode", 1)) {
            byte renderMode = tag.getByte("RenderCapeMode");

            String renderStatus = switch (renderMode) {
                case 1 -> StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Cape");
                case 2 -> StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Elytra");
                default -> StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Off");
            };

            toolTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03")
                    + EnumChatFormatting.GOLD
                    + renderStatus);
        }

        if (advancedToolTips && tag.hasKey("SkinHttp", 8)) {
            String skinUrl = tag.getString("SkinHttp");
            if (!StringUtils.isNullOrEmpty(skinUrl)) {
                toolTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_01")
                        + EnumChatFormatting.GOLD
                        + skinUrl);
                return;
            }
        }

        if (advancedToolTips && tag.hasKey("SkullOwner", 8)) {
            String playerName = tag.getString("SkullOwner");
            if (!StringUtils.isNullOrEmpty(playerName)) {
                toolTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_00")
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
            GTUIInfos.openPlayerHeldItemUI(player);
        }
        return stack;
    }

    @Override
    public ModularWindow createWindow(UIBuildContext buildContext, ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBlockPlayerDoll)) return null;
        return new PlayerDollUIFactory(buildContext).createWindow();
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

    @Desugar
    public record PlayerDollUIFactory(UIBuildContext buildContext) {

        public ModularWindow createWindow() {
            ModularWindow.Builder builder = ModularWindow.builder(300, 97);
            ItemStack stack = buildContext.getPlayer().inventory.getCurrentItem();

            builder
                .widget(new FakeSyncWidget.StringSyncer(() -> getSkullOwner(stack), val -> setSkullOwner(stack, val)));
            builder.widget(new FakeSyncWidget.StringSyncer(() -> getSkinHttp(stack), val -> setSkinHttp(stack, val)));
            builder.widget(new FakeSyncWidget.StringSyncer(() -> getCapeHttp(stack), val -> setCapeHttp(stack, val)));
            builder.widget(new FakeSyncWidget.ByteSyncer(() -> getRenderMode(stack), val -> setRenderMode(stack, val)));

            builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);

            TextFieldWidget playerNameText = new TextFieldWidget();
            builder.widget(
                playerNameText.setGetter(() -> getSkullOwner(stack))
                    .setSetter(value -> setSkullOwner(stack, value))
                    .setTextColor(Color.WHITE.dark(1))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
                    .setPos(8, 8)
                    .setSize(77, 12))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_00")).setPos(88, 10));

            TextFieldWidget skinHttpText = new TextFieldWidget();
            builder.widget(
                skinHttpText.setGetter(() -> getSkinHttp(stack))
                    .setSetter(value -> setSkinHttp(stack, value))
                    .setTextColor(Color.WHITE.dark(1))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setScrollBar()
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
                    .setPos(8, 26)
                    .setSize(197, 12))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_02")).setPos(208, 28));

            TextFieldWidget capeHttpText = new TextFieldWidget();
            builder.widget(
                capeHttpText.setGetter(() -> getCapeHttp(stack))
                    .setSetter(value -> setCapeHttp(stack, value))
                    .setTextColor(Color.WHITE.dark(1))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setScrollBar()
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
                    .setPos(8, 44)
                    .setSize(197, 12))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_04")).setPos(208, 46));

            ButtonWidget renderCapeModeButton = new ButtonWidget();
            builder.widget(
                renderCapeModeButton.setOnClick((clickData, widget) -> cycleRenderMode(stack))
                    .setBackground(() -> {
                        byte mode = getRenderMode(stack);
                        if (mode == RENDER_CAPE) {
                            return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                                GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER };
                        }

                        if (mode == RENDER_ELYTRA) {
                            return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                                GTUITextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER };
                        }

                        return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                            GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT };
                    })
                    .dynamicTooltip(() -> Collections.singletonList(getRenderModeTooltip(stack)))
                    .setUpdateTooltipEveryTick(true)
                    .setPos(64, 66)
                    .setSize(16, 16))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_03")).setPos(85, 68));

            builder.widget(
                new VanillaButtonWidget().setDisplayString(StatCollector.translateToLocal("Tooltip_PlayerDoll_01"))
                    .setOnClick((clickData, widget) -> {
                        playerNameText.onRemoveFocus();
                        skinHttpText.onRemoveFocus();
                        capeHttpText.onRemoveFocus();
                        widget.getWindow()
                            .tryClose();
                    })
                    .setSynced(true, false)
                    .setPos(8, 62)
                    .setSize(48, 20));

            return builder.build();
        }

        public NBTTagCompound getOrCreateTag(ItemStack stack) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            return nbt;
        }

        public void cycleRenderMode(ItemStack stack) {
            byte mode = getRenderMode(stack);
            mode++;
            if (mode > RENDER_ELYTRA) {
                mode = RENDER_OFF;
            }
            setRenderMode(stack, mode);
        }

        public String getRenderModeTooltip(ItemStack stack) {
            return switch (getRenderMode(stack)) {
                case RENDER_CAPE -> StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Cape");
                case RENDER_ELYTRA -> StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Elytra");
                default -> StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Off");
            };
        }

        public byte getRenderMode(ItemStack stack) {
            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt.hasKey("RenderCapeMode", 1)) {
                    return nbt.getByte("RenderCapeMode");
                }
            }
            return RENDER_OFF;
        }

        public void setRenderMode(ItemStack stack, byte mode) {
            getOrCreateTag(stack).setByte("RenderCapeMode", mode);
        }

        public String getSkullOwner(ItemStack stack) {
            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt.hasKey("SkullOwner", 8)) {
                    return nbt.getString("SkullOwner");
                }
            }
            return "";
        }

        public void setSkullOwner(ItemStack stack, String playerName) {
            getOrCreateTag(stack).setString("SkullOwner", playerName);
        }

        public String getSkinHttp(ItemStack stack) {
            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt.hasKey("SkinHttp", 8)) {
                    return nbt.getString("SkinHttp");
                }
            }
            return "";
        }

        public void setSkinHttp(ItemStack stack, String skinHttp) {
            getOrCreateTag(stack).setString("SkinHttp", skinHttp);
        }

        public String getCapeHttp(ItemStack stack) {
            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt.hasKey("CapeHttp", 8)) {
                    return nbt.getString("CapeHttp");
                }
            }
            return "";
        }

        public void setCapeHttp(ItemStack stack, String skinHttp) {
            getOrCreateTag(stack).setString("CapeHttp", skinHttp);
        }
    }
}
