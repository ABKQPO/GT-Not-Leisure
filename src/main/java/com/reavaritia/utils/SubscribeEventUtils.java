package com.reavaritia.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.reavaritia.common.ItemLoader;
import com.reavaritia.common.items.BlazeAxe;
import com.reavaritia.common.items.BlazePickaxe;
import com.reavaritia.common.items.BlazeShovel;
import com.reavaritia.common.items.BlazeSword;
import com.reavaritia.common.items.InfinityAxe;
import com.reavaritia.common.items.InfinityHoe;
import com.reavaritia.common.items.InfinityPickaxe;
import com.reavaritia.common.items.InfinityShovel;
import com.reavaritia.common.items.InfinitySword;
import com.reavaritia.common.items.MatterCluster;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fox.spiteful.avaritia.Config;
import fox.spiteful.avaritia.LudicrousEvents;
import fox.spiteful.avaritia.LudicrousText;
import gregtech.api.enums.Mods;

public class SubscribeEventUtils {

    public static final String[] trash = new String[] { "dirt", "sand", "gravel", "cobblestone", "netherrack" };

    @SubscribeEvent
    public void onPlayerMine(PlayerInteractEvent event) {
        if (!(Mods.Avaritia.isModLoaded() && getBedrockBreak()) || event.face == -1
            || event.world.isRemote
            || event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK
            || event.entityPlayer.getHeldItem() == null
            || event.entityPlayer.capabilities.isCreativeMode) return;
        Block block = event.world.getBlock(event.x, event.y, event.z);
        int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
        if (block.getBlockHardness(event.entityPlayer.worldObj, event.x, event.y, event.z) <= -1
            && event.entityPlayer.getHeldItem()
                .getItem() instanceof InfinityPickaxe
            && (block.getMaterial() == Material.rock || block.getMaterial() == Material.iron)) {

            if (event.entityPlayer.getHeldItem()
                .getTagCompound() != null && event.entityPlayer.getHeldItem()
                    .getTagCompound()
                    .getBoolean("HammerMode")) {
                ItemLoader.InfinityPickaxe
                    .onBlockStartBreak(event.entityPlayer.getHeldItem(), event.x, event.y, event.z, event.entityPlayer);
            } else {

                if (block.quantityDropped(event.world.rand) == 0) {
                    ItemStack drop = block.getPickBlock(
                        ToolHelper.raytraceFromEntity(event.world, event.entityPlayer, true, 10),
                        event.world,
                        event.x,
                        event.y,
                        event.z,
                        event.entityPlayer);
                    if (drop == null) drop = new ItemStack(block, 1, meta);
                    ToolHelper.dropItem(drop, event.entityPlayer.worldObj, event.x, event.y, event.z);
                } else block.harvestBlock(event.world, event.entityPlayer, event.x, event.y, event.z, meta);
                event.entityPlayer.worldObj.setBlockToAir(event.x, event.y, event.z);
                event.world.playAuxSFX(2001, event.x, event.y, event.z, Block.getIdFromBlock(block) + (meta << 12));
            }
        }
    }

    @Optional.Method(modid = "Avaritia")
    public boolean getBedrockBreak() {
        return Config.bedrockBreaker;
    }

    @SubscribeEvent
    public void handleExtraLuck(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null) return;
        if (event.harvester.getHeldItem() == null) return;
        ItemStack held = event.harvester.getHeldItem();
        if (held.getItem() instanceof InfinityPickaxe) {
            if (Mods.Avaritia.isModLoaded()) extraLuck(event, 5);
            NBTTagCompound nbtPickaxe = held.getTagCompound();
            if (nbtPickaxe != null && nbtPickaxe.getBoolean("HammerMode")
                && ToolHelper.hammering.contains(event.harvester)
                && ToolHelper.hammerdrops.containsKey(event.harvester)
                && ToolHelper.hammerdrops.get(event.harvester) != null) {

                ToolHelper.hammerdrops.get(event.harvester)
                    .addAll(event.drops);
                event.drops.clear();
            }
        } else if (held.getItem() instanceof InfinityShovel) {

            NBTTagCompound nbtShovel = held.getTagCompound();
            if (nbtShovel != null && nbtShovel.getBoolean("DestroyerMode")
                && ToolHelper.hammering.contains(event.harvester)
                && ToolHelper.hammerdrops.containsKey(event.harvester)
                && ToolHelper.hammerdrops.get(event.harvester) != null) {

                ArrayList<ItemStack> garbage = new ArrayList<>();
                for (ItemStack drop : event.drops) {
                    if (isGarbage(drop)) garbage.add(drop);
                }
                for (ItemStack junk : garbage) {
                    event.drops.remove(junk);
                }
                ToolHelper.hammerdrops.get(event.harvester)
                    .addAll(event.drops);
                event.drops.clear();
            }
        } else if (held.getItem() instanceof InfinityAxe) {

            if (ToolHelper.hammering.contains(event.harvester) && ToolHelper.hammerdrops.containsKey(event.harvester)
                && ToolHelper.hammerdrops.get(event.harvester) != null) {

                ToolHelper.hammerdrops.get(event.harvester)
                    .addAll(event.drops);
                event.drops.clear();
            }
        }
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester != null && event.harvester.getHeldItem() != null
            && event.harvester.getHeldItem()
                .getItem() instanceof InfinityHoe) {

            if (event.block == Blocks.melon_block || event.block == Blocks.pumpkin
                || event.block instanceof BlockCocoa) {

                event.drops.clear();
            }
        }
    }

    private static boolean isGarbage(ItemStack drop) {
        for (int id : OreDictionary.getOreIDs(drop)) {
            for (String ore : trash) {
                if (OreDictionary.getOreName(id)
                    .equals(ore)) return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public void onGetHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        if (player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof InfinitySword && player.isUsingItem()) event.setCanceled(true);
        if (Mods.Avaritia.isModLoaded() && InfinitySword.isInfinite(player)
            && !event.source.damageType.equals("infinity")) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onAttacked(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer) return;
        if (player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof InfinitySword && player.isUsingItem()) event.setCanceled(true);
        if (Mods.Avaritia.isModLoaded() && InfinitySword.isInfinite(player)
            && !event.source.damageType.equals("infinity")) event.setCanceled(true);
    }

    @SubscribeEvent
    public void diggity(PlayerEvent.BreakSpeed event) {
        if (event.entityPlayer.getHeldItem() != null) {
            ItemStack held = event.entityPlayer.getHeldItem();
            if (held.getItem() instanceof InfinityPickaxe || held.getItem() instanceof InfinityShovel) {
                if (!event.entityPlayer.onGround) event.newSpeed *= 5;
                if (!event.entityPlayer.isInsideOfMaterial(Material.water)
                    && !EnchantmentHelper.getAquaAffinityModifier(event.entityPlayer)) event.newSpeed *= 5;
                if (held.getTagCompound() != null) {
                    if (held.getTagCompound()
                        .getBoolean("HammerMode")
                        || held.getTagCompound()
                            .getBoolean("DestroyerMode")) {
                        event.newSpeed *= 0.5;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void canHarvest(PlayerEvent.HarvestCheck event) {
        if (event.entityPlayer.getHeldItem() != null) {
            ItemStack held = event.entityPlayer.getHeldItem();
            if (held.getItem() instanceof InfinityShovel && event.block.getMaterial() == Material.rock) {
                if (held.getTagCompound() != null && held.getTagCompound()
                    .getBoolean("DestroyerMode") && isGarbageBlock(event.block)) event.success = true;
            }
        }
    }

    private static boolean isGarbageBlock(Block block) {
        for (int id : OreDictionary.getOreIDs(new ItemStack(block, 1))) {
            String ore = OreDictionary.getOreName(id);
            if (ore.equals("cobblestone") || ore.equals("stone") || ore.equals("netherrack")) return true;
        }

        return false;
    }

    @SubscribeEvent
    public void clusterClustererererer(EntityItemPickupEvent event) {
        if (event.entityPlayer != null && event.item.getEntityItem()
            .getItem() instanceof MatterCluster) {
            ItemStack stack = event.item.getEntityItem();
            EntityPlayer player = event.entityPlayer;

            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                if (stack.stackSize == 0) {
                    break;
                }
                ItemStack slot = player.inventory.mainInventory[i];
                if (slot != null && slot.getItem() != null && slot.getItem() instanceof MatterCluster) {
                    MatterCluster.mergeClusters(stack, slot);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockHarvestBlazeAxe(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null) return;

        ItemStack heldItem = event.harvester.getCurrentEquippedItem();
        if (!(heldItem.getItem() instanceof BlazeAxe)) return;

        boolean smeltingActive = isSmeltingModeActive(heldItem);
        if (!smeltingActive) return;

        Block block = event.block;
        int meta = event.blockMetadata;
        ItemStack blockStack = new ItemStack(block, 1, meta);
        ItemStack smeltResult = FurnaceRecipes.smelting()
            .getSmeltingResult(blockStack);
        if (smeltResult == null) return;

        int totalCount = 0;
        for (ItemStack drop : event.drops) {
            totalCount += drop.stackSize;
        }

        event.drops.clear();
        ItemStack result = smeltResult.copy();
        result.stackSize = totalCount;
        event.drops.add(result);
    }

    public boolean isSmeltingModeActive(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean("SmeltingMode");
    }

    @SubscribeEvent
    public void onBlockHarvestHoe(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null) return;

        ItemStack heldItem = event.harvester.getCurrentEquippedItem();
        if (!(heldItem.getItem() instanceof BlazePickaxe)) return;

        boolean smeltingActive = isSmeltingModeActive(heldItem);
        if (!smeltingActive) return;

        Block block = event.block;
        int meta = event.blockMetadata;
        ItemStack blockStack = new ItemStack(block, 1, meta);
        ItemStack smeltResult = FurnaceRecipes.smelting()
            .getSmeltingResult(blockStack);
        if (smeltResult == null) return;

        int totalCount = 0;
        for (ItemStack drop : event.drops) {
            totalCount += drop.stackSize;
        }

        event.drops.clear();
        ItemStack result = smeltResult.copy();
        result.stackSize = totalCount;
        event.drops.add(result);
    }

    @SubscribeEvent
    public void onBlockHarvestBlazePickaxe(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null) return;

        ItemStack heldItem = event.harvester.getCurrentEquippedItem();
        if (!(heldItem.getItem() instanceof BlazePickaxe)) return;

        boolean smeltingActive = isSmeltingModeActive(heldItem);
        if (!smeltingActive) return;

        Block block = event.block;
        int meta = event.blockMetadata;
        ItemStack blockStack = new ItemStack(block, 1, meta);
        ItemStack smeltResult = FurnaceRecipes.smelting()
            .getSmeltingResult(blockStack);
        if (smeltResult == null) return;

        int totalCount = 0;
        for (ItemStack drop : event.drops) {
            totalCount += drop.stackSize;
        }

        event.drops.clear();
        ItemStack result = smeltResult.copy();
        result.stackSize = totalCount;
        event.drops.add(result);
    }

    @SubscribeEvent
    public void onBlockHarvestBlazeShovel(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null) return;

        ItemStack heldItem = event.harvester.getCurrentEquippedItem();
        if (!(heldItem.getItem() instanceof BlazeShovel)) return;

        Block block = event.block;
        List<ItemStack> drops = event.drops;

        boolean transformed = false;
        for (int i = 0; i < drops.size(); i++) {
            ItemStack drop = drops.get(i);
            if (drop.getItem() == Item.getItemFromBlock(Blocks.dirt)) {
                drops.set(i, new ItemStack(Blocks.netherrack, drop.stackSize));
                transformed = true;
            } else if (drop.getItem() == Item.getItemFromBlock(Blocks.sand)) {
                drops.set(i, new ItemStack(Blocks.soul_sand, drop.stackSize));
                transformed = true;
            } else if (drop.getItem() == Item.getItemFromBlock(Blocks.gravel)) {
                drops.set(i, new ItemStack(Blocks.netherrack, drop.stackSize));
                transformed = true;
            }
        }

        if (!transformed) {
            boolean smeltingActive = isSmeltingModeActive(heldItem);
            if (smeltingActive) {
                int meta = event.blockMetadata;
                ItemStack blockStack = new ItemStack(block, 1, meta);
                ItemStack smeltResult = FurnaceRecipes.smelting()
                    .getSmeltingResult(blockStack);
                if (smeltResult != null) {
                    int totalCount = 0;
                    for (ItemStack drop : drops) {
                        totalCount += drop.stackSize;
                    }

                    event.drops.clear();
                    ItemStack result = smeltResult.copy();
                    result.stackSize = totalCount;
                    event.drops.add(result);
                }
            }
        }

        heldItem.damageItem(1, event.harvester);
    }

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        if (event.entity instanceof EntitySkeleton) {
            Entity attacker = event.source.getEntity();

            if (attacker instanceof EntityPlayer player) {
                ItemStack heldItem = player.getHeldItem();

                if (heldItem != null && heldItem.getItem() instanceof BlazeSword) {

                    Iterator<EntityItem> iterator = event.drops.iterator();
                    while (iterator.hasNext()) {
                        EntityItem item = iterator.next();
                        ItemStack stack = item.getEntityItem();
                        if (stack.getItem() == Items.skull) {
                            iterator.remove();
                        }
                    }

                    event.drops.add(
                        new EntityItem(
                            event.entity.worldObj,
                            event.entity.posX,
                            event.entity.posY,
                            event.entity.posZ,
                            new ItemStack(Items.skull, 1, 1)));
                }
            }
        }
    }

    // Item
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event.itemStack.getItem() instanceof InfinitySword) {
            for (int x = 0; x < event.toolTip.size(); x++) {
                if (event.toolTip.get(x)
                    .contains(StatCollector.translateToLocal("attribute.name.generic.attackDamage"))
                    || event.toolTip.get(x)
                        .contains(StatCollector.translateToLocal("Attack Damage"))) {
                    var damage = StatCollector.translateToLocal("Damage_InfinitySword");
                    if (Mods.Avaritia.isModLoaded()) damage = makeFabulous(damage);
                    event.toolTip.set(
                        x,
                        EnumChatFormatting.BLUE + "+"
                            + damage
                            + " "
                            + EnumChatFormatting.BLUE
                            + StatCollector.translateToLocal("attribute.name.generic.attackDamage"));
                    return;
                }
            }
        }
    }

    @Optional.Method(modid = "Avaritia")
    public static String makeFabulous(String text) {
        return LudicrousText.makeFabulous(text);
    }

    @Optional.Method(modid = "Avaritia")
    public static void extraLuck(BlockEvent.HarvestDropsEvent event, int mult) {
        LudicrousEvents.extraLuck(event, mult);
    }
}
