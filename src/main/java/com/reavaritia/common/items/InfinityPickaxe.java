package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ForgeDirection;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.entity.EntityImmortalItem;
import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.enums.Mods;

public class InfinityPickaxe extends ItemPickaxe implements SubtitleDisplay {

    @SideOnly(Side.CLIENT)
    private IIcon hammer;

    public InfinityPickaxe() {
        super(ToolHelper.INFINITY);
        this.setUnlocalizedName("InfinityPickaxe");
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "InfinityPickaxe");
        ReAvaItemList.InfinityPickaxe.set(new ItemStack(this, 1));
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_InfinityPickaxe_00"));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        ItemStack pick = new ItemStack(this);
        pick.addEnchantment(Enchantment.fortune, 10);
        list.add(pick);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumHelper.addRarity("COSMIC", EnumChatFormatting.RED, "Cosmic");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.itemIcon = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "InfinityPickaxe");
        this.hammer = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "InfinityHammer");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        boolean isHammerMode = stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("HammerMode");
        return isHammerMode ? hammer : itemIcon;
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

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack) < 10) {
                stack.addEnchantment(Enchantment.fortune, 10);
            }

            boolean isHammerMode = !tags.getBoolean("HammerMode");
            tags.setBoolean("HammerMode", isHammerMode);
            player.swingItem();

            if (world.isRemote) {
                String key = isHammerMode ? StatCollector.translateToLocal("Tooltip_Infinity_Mode_2")
                    : StatCollector.translateToLocal("Tooltip_Infinity_Mode_1");
                showSubtitle(key);
            }
        }
        return stack;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase player) {
        if (stack.getTagCompound() != null) {
            if (stack.getTagCompound()
                .getBoolean("HammerMode")) {
                if (!(victim instanceof EntityPlayer entityPlayer && LudicrousItems.isInfinite(entityPlayer))) {
                    int i = 10;
                    victim.addVelocity(
                        -MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F,
                        2.0D,
                        MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F);
                }
            }
        }
        return true;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        boolean isHammerMode = stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("HammerMode");

        return isHammerMode ? 5.0F
            : ForgeHooks.isToolEffective(stack, block, meta) ? efficiencyOnProperMaterial
                : Math.max(func_150893_a(stack, block), 6.0F);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        boolean isHammerMode = stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("HammerMode");

        if (!player.worldObj.isRemote && isHammerMode) {
            MovingObjectPosition raycast = ToolHelper.raytraceFromEntity(player.worldObj, player, true, 10);
            if (raycast != null) {
                breakOtherBlock(player, stack, x, y, z, raycast.sideHit);
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

    private void breakOtherBlock(EntityPlayer player, ItemStack stack, int x, int y, int z, int side) {
        World world = player.worldObj;
        Material mat = world.getBlock(x, y, z)
            .getMaterial();
        if (!ToolHelper.isRightMaterial(mat, ToolHelper.MATERIALS)) return;

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
            ToolHelper.MATERIALS,
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
        return Mods.Avaritia.isModLoaded() ? createImmortalItem(world, location, itemstack) : null;
    }

    @Optional.Method(modid = "Avaritia")
    public Entity createImmortalItem(World world, Entity location, ItemStack itemstack) {
        return new EntityImmortalItem(world, location, itemstack);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof EntityPlayer player) {
            if (player.getFoodStats()
                .needFood()
                || player.getFoodStats()
                    .getSaturationLevel() < 20.0F) {
                player.getFoodStats()
                    .addStats(20, 20.0F);
            }
        }
    }
}
