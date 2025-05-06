package com.reavaritia.common.item;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import com.reavaritia.ReAvaCreativeTabs;
import com.reavaritia.ReAvaItemList;
import com.reavaritia.ReAvaritia;
import com.reavaritia.common.SubtitleDisplay;
import com.reavaritia.common.entity.EntityChronarchPoint;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChronarchsClock extends Item implements SubtitleDisplay {

    public ChronarchsClock() {
        this.setUnlocalizedName("ChronarchsClock");
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "ChronarchsClock");
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        this.setMaxStackSize(1);
        ReAvaItemList.ChronarchsClock.set(new ItemStack(this, 1));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumHelper.addRarity("COSMIC", EnumChatFormatting.RED, "Cosmic");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advanced) {
        list.add(StatCollector.translateToLocal("Tooltip_ChronarchsClock_00"));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            return stack;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        long lastUsed = nbt.getLong("LastUsed");

        if (world.getTotalWorldTime() - lastUsed < MainConfig.ChronarchsClockCooldown) {
            showSubtitle("Tooltip_ChronarchsClock_01");
            return stack;
        }

        if (!world.isRemote) {
            EntityChronarchPoint point = new EntityChronarchPoint(
                world,
                player.posX,
                player.posY,
                player.posZ,
                MainConfig.ChronarchsClockRadius,
                MainConfig.ChronarchsClockSpeedMultiplier,
                MainConfig.ChronarchsClockDurationTicks);
            world.spawnEntityInWorld(point);
        }

        nbt.setLong("LastUsed", world.getTotalWorldTime());
        return stack;
    }

    private static float originalTickrate = -1f;
    private static float currentTickrate = -1f;
    private static boolean restoring = false;

    private static final int BOOST_DURATION_TICKS = 20 * 20;
    private static final int RESTORE_DURATION_TICKS = 20 * 20;

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (player.worldObj.isRemote || !player.isSneaking()) return;

        if (originalTickrate < 0f) {
            originalTickrate = TickrateAPI.getServerTickrate();
            currentTickrate = originalTickrate;
        }

        restoring = false;

        float delta = (MainConfig.maxTickrate - originalTickrate) / BOOST_DURATION_TICKS;
        currentTickrate = Math.min(currentTickrate + delta, MainConfig.maxTickrate);

        TickrateAPI.changeTickrate(currentTickrate);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
        if (!world.isRemote) {
            restoring = true;
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !restoring || originalTickrate < 0f) return;

        float delta = (MainConfig.maxTickrate - originalTickrate) / RESTORE_DURATION_TICKS;
        currentTickrate = Math.max(currentTickrate - delta, originalTickrate);

        TickrateAPI.changeTickrate(currentTickrate);

        if (currentTickrate <= originalTickrate + 0.5f) {
            TickrateAPI.changeTickrate(originalTickrate);
            currentTickrate = -1f;
            originalTickrate = -1f;
            restoring = false;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle(String messageKey) {
        IChatComponent component = new ChatComponentTranslation(messageKey);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    public static void registerEntity() {
        EntityRegistry
            .registerModEntity(EntityChronarchPoint.class, "ChronarchPoint", 1, ReAvaritia.instance, 64, 20, true);

    }
}
