package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.gui.modularui.GTUIInfos;
import gregtech.api.gui.modularui.GTUITextures;

public class ItemBlockPlayerDoll extends ItemBlock implements IItemWithModularUI {

    public ItemBlockPlayerDoll(Block block) {
        super(block);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) return;
        if (tag.hasKey("CapeHttp", 8)) {
            String capeUrl = tag.getString("CapeHttp");
            if (!StringUtils.isNullOrEmpty(capeUrl)) {
                toolTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_02")
                        + EnumChatFormatting.GOLD
                        + capeUrl);
            }
        }

        if (tag.hasKey("enableElytra")) {
            boolean enableElytra = tag.getBoolean("enableElytra");
            String elytraStatus = enableElytra ? StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_On")
                : StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Off");
            toolTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03")
                    + EnumChatFormatting.GOLD
                    + elytraStatus);
        }

        if (tag.hasKey("SkinHttp", 8)) {
            String skinUrl = tag.getString("SkinHttp");
            if (!StringUtils.isNullOrEmpty(skinUrl)) {
                toolTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_01")
                        + EnumChatFormatting.GOLD
                        + skinUrl);
                return;
            }
        }

        if (tag.hasKey("SkullOwner", 8)) {
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

    @Desugar
    public record PlayerDollUIFactory(UIBuildContext buildContext) {

        public ModularWindow createWindow() {
            ModularWindow.Builder builder = ModularWindow.builder(300, 97);
            builder.widget(
                new FakeSyncWidget.StringSyncer(
                    () -> getSkullOwner(getCurrentItem()),
                    val -> setSkullOwner(getCurrentItem(), val)));
            builder.widget(
                new FakeSyncWidget.StringSyncer(
                    () -> getSkinHttp(getCurrentItem()),
                    val -> setSkinHttp(getCurrentItem(), val)));
            builder.widget(
                new FakeSyncWidget.StringSyncer(
                    () -> getCapeHttp(getCurrentItem()),
                    val -> setCapeHttp(getCurrentItem(), val)));
            builder.widget(
                new FakeSyncWidget.BooleanSyncer(
                    () -> getEnableElytra(getCurrentItem()),
                    val -> setEnableElytra(getCurrentItem(), val)));

            builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);

            TextFieldWidget playerNameField = new TextFieldWidget();
            builder.widget(
                playerNameField.setGetter(() -> getSkullOwner(getCurrentItem()))
                    .setSetter(value -> setSkullOwner(getCurrentItem(), value))
                    .setTextColor(Color.WHITE.dark(1))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
                    .setPos(8, 8)
                    .setSize(77, 12))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_00")).setPos(88, 10));

            TextFieldWidget skinHttpField = new TextFieldWidget();
            builder.widget(
                skinHttpField.setGetter(() -> getSkinHttp(getCurrentItem()))
                    .setSetter(value -> setSkinHttp(getCurrentItem(), value))
                    .setTextColor(Color.WHITE.dark(1))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setScrollBar()
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
                    .setPos(8, 26)
                    .setSize(197, 12))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_02")).setPos(208, 28));

            TextFieldWidget capeHttpField = new TextFieldWidget();
            builder.widget(
                capeHttpField.setGetter(() -> getCapeHttp(getCurrentItem()))
                    .setSetter(value -> setCapeHttp(getCurrentItem(), value))
                    .setTextColor(Color.WHITE.dark(1))
                    .setTextAlignment(Alignment.CenterLeft)
                    .setScrollBar()
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD.withOffset(-1, -1, 2, 2))
                    .setPos(8, 44)
                    .setSize(197, 12))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_04")).setPos(208, 46));

            builder.widget(
                new ButtonWidget().setOnClick(
                    (clickData, widget) -> { setEnableElytra(getCurrentItem(), !getEnableElytra(getCurrentItem())); })
                    .setPlayClickSoundResource(
                        () -> getEnableElytra(getCurrentItem()) ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        if (getEnableElytra(getCurrentItem())) {
                            return new IDrawable[] { GTUITextures.BUTTON_STANDARD_PRESSED,
                                GTUITextures.OVERLAY_BUTTON_POWER_SWITCH_ON };
                        } else {
                            return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                                GTUITextures.OVERLAY_BUTTON_POWER_SWITCH_OFF };
                        }
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(
                            () -> getEnableElytra(getCurrentItem()),
                            val -> { setEnableElytra(getCurrentItem(), val); }),
                        builder)
                    .setPos(64, 66)
                    .setSize(16, 16))
                .widget(new TextWidget(StatCollector.translateToLocal("Tooltip_PlayerDoll_03")).setPos(85, 68));

            builder.widget(
                new VanillaButtonWidget().setDisplayString(StatCollector.translateToLocal("Tooltip_PlayerDoll_01"))
                    .setOnClick((clickData, widget) -> {
                        playerNameField.onRemoveFocus();
                        skinHttpField.onRemoveFocus();
                        capeHttpField.onRemoveFocus();
                        widget.getWindow()
                            .tryClose();
                    })
                    .setSynced(false, false)
                    .setPos(8, 62)
                    .setSize(48, 20));

            return builder.build();
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
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                stack.setTagCompound(nbt = new NBTTagCompound());
            }
            nbt.setString("SkullOwner", playerName);
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
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                stack.setTagCompound(nbt = new NBTTagCompound());
            }
            nbt.setString("SkinHttp", skinHttp);
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
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                stack.setTagCompound(nbt = new NBTTagCompound());
            }
            nbt.setString("CapeHttp", skinHttp);
        }

        public boolean getEnableElytra(ItemStack stack) {
            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt.hasKey("enableElytra", 1)) {
                    return nbt.getBoolean("enableElytra");
                }
            }
            return false;
        }

        public void setEnableElytra(ItemStack stack, boolean enableElytra) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                stack.setTagCompound(nbt = new NBTTagCompound());
            }
            nbt.setBoolean("enableElytra", enableElytra);
        }

        public ItemStack getCurrentItem() {
            return buildContext.getPlayer().inventory.getCurrentItem();
        }
    }
}
