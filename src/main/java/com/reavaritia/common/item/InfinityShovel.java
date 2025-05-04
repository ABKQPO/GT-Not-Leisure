package com.reavaritia.common.item;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ForgeDirection;

import com.reavaritia.ReAvaCreativeTabs;
import com.reavaritia.ReAvaItemList;
import com.reavaritia.TextLocalization;
import com.reavaritia.common.SubtitleDisplay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.entity.EntityImmortalItem;

public class InfinityShovel extends ItemSpade implements SubtitleDisplay {

    private static final ToolMaterial INFINITY = EnumHelper.addToolMaterial("INFINITY", 32, 9999, 9999F, 50.0F, 200);
    public static final Material[] MATERIALS = new Material[] { Material.rock, Material.iron, Material.ice,
        Material.glass, Material.piston, Material.anvil, Material.grass, Material.ground, Material.sand, Material.snow,
        Material.craftedSnow, Material.clay };

    @SideOnly(Side.CLIENT)
    private IIcon Destroyer;

    public InfinityShovel() {
        super(INFINITY);
        this.setUnlocalizedName("InfinityShovel");
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "InfinityShovel");
        ReAvaItemList.InfinityShovel.set(new ItemStack(this, 1));
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        toolTip.add(TextLocalization.Tooltip_InfinityShovel_00);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        ItemStack pick = new ItemStack(this);
        list.add(pick);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumHelper.addRarity("COSMIC", EnumChatFormatting.RED, "Cosmic");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.itemIcon = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "InfinityShovel");
        this.Destroyer = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "InfinityDestroyer");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        boolean isDestroyerMode = stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("DestroyerMode");
        return isDestroyerMode ? Destroyer : itemIcon;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        return getIcon(stack, 0);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (tags == null) {
                tags = new NBTTagCompound();
                stack.setTagCompound(tags);
            }

            boolean isDestroyerMode = !tags.getBoolean("DestroyerMode");
            tags.setBoolean("DestroyerMode", isDestroyerMode);
            player.swingItem();

            if (world.isRemote) {
                String key = isDestroyerMode ? TextLocalization.Tooltip_Infinity_Mode_2
                    : TextLocalization.Tooltip_Infinity_Mode_1;
                showSubtitle(key);
            }
        }
        return stack;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        boolean isDestroyerMode = stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("DestroyerMode");

        return isDestroyerMode ? 5.0F
            : ForgeHooks.isToolEffective(stack, block, meta) ? efficiencyOnProperMaterial
                : Math.max(func_150893_a(stack, block), 6.0F);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        boolean isDestroyerMode = stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("DestroyerMode");

        if (isDestroyerMode) {
            MovingObjectPosition raycast = ToolHelper.raytraceFromEntity(player.worldObj, player, true, 16);
            if (raycast != null) {
                breakOtherBlock(player, stack, x, y, z, x, y, z, raycast.sideHit);
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle(String messageKey) {
        IChatComponent component = new ChatComponentTranslation(messageKey);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    private void breakOtherBlock(EntityPlayer player, ItemStack stack, int x, int y, int z, int originX, int originY,
        int originZ, int side) {
        World world = player.worldObj;
        Material mat = world.getBlock(x, y, z)
            .getMaterial();
        if (!ToolHelper.isRightMaterial(mat, MATERIALS)) return;

        ForgeDirection direction = ForgeDirection.getOrientation(side);
        int fortune = EnchantmentHelper.getFortuneModifier(player);
        boolean silk = EnchantmentHelper.getSilkTouchModifier(player);
        boolean doY = direction.offsetY == 0;

        ToolHelper.removeBlocksInIteration(
            player,
            stack,
            world,
            x,
            y,
            z,
            -8,
            doY ? -1 : -8,
            -8,
            8,
            doY ? 14 : 8,
            8,
            null,
            MATERIALS,
            silk,
            fortune,
            false);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityImmortalItem(world, location, itemstack);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }
}
