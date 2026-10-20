package com.reavaritia.common.items;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.reavaritia.ReAvaritia;
import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.PlaySound;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.item.ItemUtils;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class InfinityTotem extends Item implements IBauble, SubtitleDisplay, PlaySound {

    public InfinityTotem() {
        this.setUnlocalizedName("reavaritia.infinity_totem");
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(ReAvaritia.RESOURCE_ROOT_ID + ":infinity_totem");
        this.setMaxDamage(99);
        this.setMaxStackSize(1);
        MinecraftForge.EVENT_BUS.register(this);
        ReAvaItemList.InfinityTotem.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("reavaritia.item.infinity_totem.tooltip.00"));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle() {
        IChatComponent component = new ChatComponentTranslation("reavaritia.item.infinity_totem.enable");
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void playSoundIfReady(EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, ReAvaritia.RESOURCE_ROOT_ID + ":" + "totem.enable", 1.0F, 1.0F);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer player) {

            // Check the player's inventory for the Infinity Totem.
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                handleTotem(stack, player);
            }

            // Check the player's baubles inventory for the Infinity Totem.
            if (ModList.Baubles.isModLoaded()) {
                IInventory baublesInventory = BaublesApi.getBaubles(player);
                if (baublesInventory != null) {
                    for (int i = 0; i < baublesInventory.getSizeInventory(); i++) {
                        ItemStack stack = baublesInventory.getStackInSlot(i);
                        handleTotem(stack, player);
                    }
                }
            }
        }
    }

    private void handleTotem(ItemStack stack, EntityPlayer player) {
        if (stack != null && stack.getItem() instanceof InfinityTotem) {

            if (stack.getTagCompound() == null) {
                stack.setTagCompound(new NBTTagCompound());
            }

            // Read or set the owner UUID.
            if (!stack.getTagCompound()
                .hasKey("ownerUUID")) {
                stack.getTagCompound()
                    .setString(
                        "ownerUUID",
                        player.getUniqueID()
                            .toString());
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entity == null) return;
        if (event.entity.worldObj.isRemote) return;
        if (event.entity instanceof EntityPlayer player) {

            // Check the player's inventory for the Infinity Totem.
            ItemStack stack = getTotemFromPlayer(player);

            // Check the player's baubles inventory for the Infinity Totem.
            if (ModList.Baubles.isModLoaded()) {
                if (stack == null) {
                    IInventory baublesInventory = BaublesApi.getBaubles(player);
                    if (baublesInventory != null) {
                        for (int i = 0; i < baublesInventory.getSizeInventory(); i++) {
                            ItemStack baubleStack = baublesInventory.getStackInSlot(i);
                            if (baubleStack != null && baubleStack.getItem() instanceof InfinityTotem) {
                                stack = baubleStack;
                                break;
                            }
                        }
                    }
                }
            }

            // Apply the Infinity Totem effect.
            if (stack != null && stack.getItemDamage() < stack.getMaxDamage()) {
                if (stack.getItemDamage() == stack.getMaxDamage() - 1) {
                    event.setCanceled(true);
                    triggerFinalEffect(player.worldObj, player, stack);
                    ItemUtils.removeItemFromPlayer(player, stack);
                } else {
                    event.setCanceled(true);
                    triggerNormalEffect(player.worldObj, player, stack);
                }
                showSubtitle();
                playSoundIfReady(player);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemExpire(ItemExpireEvent event) {
        if (event.entityItem.getEntityItem()
            .getItem() instanceof InfinityTotem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.entityItem.getEntityItem();
        if (stack.getItem() instanceof InfinityTotem) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            nbt.setString(
                "ownerUUID",
                event.player.getUniqueID()
                    .toString());
        }
    }

    private void returnItemToPlayerInventory(EntityItem entityItem) {
        if (entityItem.isDead) return;

        ItemStack stack = entityItem.getEntityItem()
            .copy();

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey("ownerUUID")) {
            UUID ownerUUID;
            try {
                ownerUUID = UUID.fromString(nbt.getString("ownerUUID"));
            } catch (IllegalArgumentException e) {
                ReAvaritia.LOG.warn("InfinityTotem: invalid ownerUUID in NBT, skipping return", e);
                return;
            }
            EntityPlayer player = entityItem.worldObj.func_152378_a(ownerUUID);

            if (player != null && !player.worldObj.isRemote) {
                boolean added = player.inventory.addItemStackToInventory(stack);
                if (added) {
                    entityItem.setDead();
                } else {
                    EntityItem newEntity = new EntityItem(
                        player.worldObj,
                        player.posX,
                        player.posY + 0.5,
                        player.posZ,
                        stack);
                    newEntity.delayBeforeCanPickup = 40;
                    player.worldObj.spawnEntityInWorld(newEntity);
                    entityItem.setDead();
                }
            }
        }
    }

    private ItemStack getTotemFromPlayer(EntityPlayer player) {
        // Check the held item.
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof InfinityTotem) {
            return heldItem;
        }

        // Check the inventory.
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof InfinityTotem) {
                return stack;
            }
        }

        // Check the baubles inventory.
        if (ModList.Baubles.isModLoaded()) {
            IInventory baublesInventory = BaublesApi.getBaubles(player);
            if (baublesInventory != null) {
                for (int i = 0; i < baublesInventory.getSizeInventory(); i++) {
                    ItemStack stack = baublesInventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof InfinityTotem) {
                        return stack;
                    }
                }
            }
        }

        return null;
    }

    private void triggerNormalEffect(World world, EntityPlayer player, ItemStack stack) {
        player.setHealth(10.0F);
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 130 * 20, 4));
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 20 * 20, 1));
        player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 35 * 20, 2));
        player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 55 * 20, 0));
        stack.damageItem(1, player);
    }

    private void triggerFinalEffect(World world, EntityPlayer player, ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        player.setHealth(player.getMaxHealth());
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 40 * 20, 1));
        player.addPotionEffect(new PotionEffect(Potion.jump.id, 40 * 20, 1));
        stack.damageItem(1, player);

        List<EntityLivingBase> entities = world
            .getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(50.0D, 50.0D, 50.0D));

        String ownerUUIDString = stack.getTagCompound()
            .getString("ownerUUID");
        if (ownerUUIDString == null || ownerUUIDString.isEmpty()) {
            stack.getTagCompound()
                .setString(
                    "ownerUUID",
                    player.getUniqueID()
                        .toString());
            ownerUUIDString = player.getUniqueID()
                .toString();
        }

        UUID ownerUUID = UUID.fromString(ownerUUIDString);

        for (EntityLivingBase entity : entities) {
            if (!entity.getUniqueID()
                .equals(ownerUUID)) {
                entity.attackEntityFrom(DamageSource.magic, 1000.0F);
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        ItemStack stack = entityItem.getEntityItem();
        if (stack == null || !(stack.getItem() instanceof InfinityTotem)) {
            return super.onEntityItemUpdate(entityItem);
        }

        // Destroy the item when its durability reaches zero.
        if (stack.getItemDamage() >= stack.getMaxDamage()) {
            entityItem.setDead();
            return true;
        }

        // Return the item to its owner when it falls below Y zero.
        if (entityItem.posY < 0D) {
            returnItemToPlayerInventory(entityItem);
            return true;
        }
        // Get the entity center coordinates.
        int centerX = (int) entityItem.posX;
        int centerY = (int) entityItem.posY;
        int centerZ = (int) entityItem.posZ;

        // Scan the surrounding 3x3 area.
        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int y = centerY - 2; y <= centerY + 2; y++) {
                for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                    Block block = entityItem.worldObj.getBlock(x, y, z);

                    // Check for cactus or lava.
                    if (block == Blocks.cactus || block == Blocks.lava || block == Blocks.flowing_lava) {
                        returnItemToPlayerInventory(entityItem);
                        return true; // Stop when cactus or lava is detected.
                    }
                }
            }
        }
        return super.onEntityItemUpdate(entityItem);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {

    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
