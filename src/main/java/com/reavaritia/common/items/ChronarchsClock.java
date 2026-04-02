package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.common.entity.EntityChronarchClock;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChronarchsClock extends Item implements SubtitleDisplay, IBehaviorDispenseItem {

    private IIcon iconOn;

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

        if (world.getTotalWorldTime() - lastUsed < MainConfig.re_avaritia.chronarch_clock.chronarchsClockCooldown) {
            showSubtitle("Tooltip_ChronarchsClock_01");
            return stack;
        }

        if (!world.isRemote) {
            EntityChronarchClock point = new EntityChronarchClock(
                world,
                player.posX,
                player.posY,
                player.posZ,
                MainConfig.re_avaritia.chronarch_clock.chronarchsClockRadius,
                MainConfig.re_avaritia.chronarch_clock.chronarchsClockSpeedMultiplier,
                MainConfig.re_avaritia.chronarch_clock.chronarchsClockDurationTicks);
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
        if (!player.isSneaking()) return;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        if (currentTickrate < MainConfig.tickrate.maxTickrate && !player.worldObj.isRemote) {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200, 4));
            if (originalTickrate < 0f) {
                originalTickrate = TickrateAPI.getServerTickrate();
                currentTickrate = originalTickrate;
            }

            float delta = (MainConfig.tickrate.maxTickrate - originalTickrate) / BOOST_DURATION_TICKS;
            currentTickrate = Math.min(currentTickrate + delta, MainConfig.tickrate.maxTickrate);

            TickrateAPI.changeServerTickrate(currentTickrate);
            nbt.setBoolean("ClockActive", true);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
        player.removePotionEffect(Potion.moveSpeed.id);
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            nbt.setBoolean("ClockActive", false);
            nbt.setBoolean("ShaderApplied", false);
        }
        restoring = true;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !restoring || originalTickrate < 0f) return;

        float delta = (MainConfig.tickrate.maxTickrate - originalTickrate) / RESTORE_DURATION_TICKS;
        currentTickrate = Math.max(currentTickrate - delta, originalTickrate);

        TickrateAPI.changeServerTickrate(currentTickrate);

        if (currentTickrate <= originalTickrate + 0.5f) {
            TickrateAPI.changeServerTickrate(originalTickrate);
            currentTickrate = -1f;
            originalTickrate = -1f;
            restoring = false;
        }
    }

    @Override
    public ItemStack dispense(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();

        if (world.isRemote) {
            return stack;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        long lastUsed = nbt.getLong("LastUsed");

        if (world.getTotalWorldTime() - lastUsed < MainConfig.re_avaritia.chronarch_clock.chronarchsClockCooldown) {
            return stack;
        }

        EnumFacing facing = BlockDispenser.func_149937_b(source.getBlockMetadata());
        IPosition pos = BlockDispenser.func_149939_a(source);

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        EntityChronarchClock point = new EntityChronarchClock(
            world,
            x + facing.getFrontOffsetX() * 0.5,
            y + facing.getFrontOffsetY() * 0.5,
            z + facing.getFrontOffsetZ() * 0.5,
            MainConfig.re_avaritia.chronarch_clock.chronarchsClockRadius,
            MainConfig.re_avaritia.chronarch_clock.chronarchsClockSpeedMultiplier,
            MainConfig.re_avaritia.chronarch_clock.chronarchsClockDurationTicks);

        world.spawnEntityInWorld(point);

        nbt.setLong("LastUsed", world.getTotalWorldTime());

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(RESOURCE_ROOT_ID + ":ChronarchsClock");
        this.iconOn = register.registerIcon(RESOURCE_ROOT_ID + ":ChronarchsClock_On");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.getBoolean("ClockActive")) {
            return iconOn;
        }
        return super.getIcon(stack, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.getBoolean("ClockActive")) {
            return iconOn;
        }
        return super.getIconIndex(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean("ClockActive");
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
            .registerModEntity(EntityChronarchClock.class, "ChronarchClock", 1, ModList.ReAvaritia.ID, 64, 20, true);

    }
}
