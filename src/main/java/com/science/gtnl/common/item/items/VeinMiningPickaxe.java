package com.science.gtnl.common.item.items;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.github.bsideup.jabel.Desugar;
import com.reavaritia.utils.item.ItemStackWrapper;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.MetaGeneratedTool;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class VeinMiningPickaxe extends ItemPickaxe implements SubtitleDisplay {

    public boolean isEnable;

    public VeinMiningPickaxe() {
        super(EnumHelper.addToolMaterial("VEIN", 15, 20000000, 15, 3, 10));
        this.setUnlocalizedName("VeinMiningPickaxe");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "VeinMiningPickaxe");
        this.setMaxStackSize(1);
        this.setMaxDamage(20000000);
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        GameRegistry.registerItem(this, getUnlocalizedName());
        GTNLItemList.VeinMiningPickaxe.set(new ItemStack(this, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        NBTTagCompound tags = itemStack.getTagCompound();
        int range = 3;
        int amount = 32767;
        boolean preciseMode = false;

        if (tags != null) {
            if (tags.hasKey("range")) {
                range = Math.max(0, Math.min(MainConfig.item.vein_miner_pickaxe.maxRange, tags.getInteger("range")));
            }
            if (tags.hasKey("amount")) {
                amount = Math.max(0, Math.min(MainConfig.item.vein_miner_pickaxe.maxAmount, tags.getInteger("amount")));
            }
            if (tags.hasKey("preciseMode")) {
                preciseMode = tags.getBoolean("preciseMode");
            }
        }

        toolTip.add(StatCollector.translateToLocalFormatted("Tooltip_VeinMiningPickaxe_00", range));
        toolTip.add(StatCollector.translateToLocalFormatted("Tooltip_VeinMiningPickaxe_01", amount));
        toolTip.add(
            StatCollector.translateToLocal(
                preciseMode ? "Tooltip_VeinMiningPickaxe_PreciseMode_On"
                    : "Tooltip_VeinMiningPickaxe_PreciseMode_Off"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean("preciseMode");
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        ItemUtils.setToolDamage(stack, damage);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return Math.toIntExact(MetaGeneratedTool.getToolDamage(stack));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Math.toIntExact(MetaGeneratedTool.getToolMaxDamage(stack));
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        return true;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        return 20;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> aList) {
        ItemStack stack = new ItemStack(ItemLoader.veinMiningPickaxe, 1);
        ItemUtils.setToolMaxDamage(stack, 20000000);
        aList.add(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (tags == null) {
                tags = new NBTTagCompound();
                stack.setTagCompound(tags);
            }

            boolean isPreciseMode = !tags.getBoolean("preciseMode");
            tags.setBoolean("preciseMode", isPreciseMode);
            player.swingItem();

            if (world.isRemote) {
                String key = isPreciseMode ? StatCollector.translateToLocal("Tooltip_VeinMiningPickaxe_PreciseMode_On")
                    : StatCollector.translateToLocal("Tooltip_VeinMiningPickaxe_PreciseMode_Off");
                showSubtitle(key);
            }
        }
        return stack;
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        World world = player.worldObj;
        if (world.isRemote || !(player instanceof EntityPlayerMP playerMP)) return;
        if (playerMP.isSneaking()) {
            ItemStack stack = playerMP.getCurrentEquippedItem();
            if (stack == null || !(stack.getItem() instanceof VeinMiningPickaxe)) {
                isEnable = false;
                return;
            }
            int range = 3;
            int amount = 32767;
            boolean preciseMode = false;

            NBTTagCompound tags = stack.getTagCompound();
            if (tags != null) {
                if (tags.hasKey("range")) {
                    range = Math
                        .max(-1, Math.min(MainConfig.item.vein_miner_pickaxe.maxRange, tags.getInteger("range")));
                }
                if (tags.hasKey("preciseMode")) {
                    preciseMode = tags.getBoolean("preciseMode");
                }
                if (tags.hasKey("amount")) {
                    amount = Math
                        .max(0, Math.min(MainConfig.item.vein_miner_pickaxe.maxAmount, tags.getInteger("amount")));
                }
            }

            Block block = event.block;
            int meta = world.getBlockMetadata(event.x, event.y, event.z);

            if (block != null && range >= 0 && !isEnable) {
                isEnable = true;
                clearConnectedBlocks(
                    playerMP,
                    stack,
                    event.x,
                    event.y,
                    event.z,
                    block,
                    meta,
                    range,
                    amount,
                    preciseMode);
            }
        }
    }

    public void clearConnectedBlocks(EntityPlayerMP player, ItemStack stack, int x, int y, int z, Block targetBlock,
        int targetMeta, int maxGap, int amount, boolean preciseMode) {
        if (player.getFoodStats()
            .getFoodLevel() <= 0
            && player.getFoodStats()
                .getSaturationLevel() <= 0f
            && !player.capabilities.isCreativeMode) {
            return;
        }
        World world = player.worldObj;
        Queue<Node> queue = new ArrayDeque<>();
        LongSet visited = new LongOpenHashSet();
        int cleared = 0;
        int blocksSinceHunger = 0;
        int toolMaxDamage = Math.toIntExact(MetaGeneratedTool.getToolMaxDamage(stack));
        List<ItemStack> allDrops = new ArrayList<>();

        queue.add(new Node(x, y, z, 0));

        while (!queue.isEmpty() && cleared < amount) {
            if (!player.isSneaking()) {
                isEnable = false;
                break;
            }

            Node node = queue.poll();
            int px = node.x, py = node.y, pz = node.z;
            int gap = node.gap;

            long key = (((long) px) & 0x3FFFFFF) << 38 | (((long) py) & 0xFFF) << 26 | (((long) pz) & 0x3FFFFFF);
            if (!visited.add(key)) continue;
            if (!world.blockExists(px, py, pz)) continue;

            Block block = world.getBlock(px, py, pz);
            TileEntity tileEntity = world.getTileEntity(px, py, pz);
            int meta = world.getBlockMetadata(px, py, pz);

            boolean matches = false;
            if (block.getBlockHardness(world, x, y, z) >= 0) {
                if (tileEntity instanceof IGregTechTileEntity gtTE) {
                    meta = gtTE.getMetaTileID();
                }
                if (block == targetBlock && (!preciseMode || meta == targetMeta)) {
                    matches = true;
                } else {
                    ItemStack stackAt = new ItemStack(block, 1, meta);
                    int[] oreIds = OreDictionary.getOreIDs(stackAt);
                    for (int id : oreIds) {
                        String name = OreDictionary.getOreName(id);
                        int[] targetIds = OreDictionary.getOreIDs(new ItemStack(targetBlock, 1, targetMeta));
                        for (int tid : targetIds) {
                            String tname = OreDictionary.getOreName(tid);
                            if (preciseMode) {
                                if (tname.equals(name)) {
                                    matches = true;
                                    break;
                                }
                            } else {
                                if ((name.startsWith("ore") && tname.startsWith("ore")) || tname.startsWith(name)) {
                                    matches = true;
                                    break;
                                }
                            }
                        }

                        if (matches) break;
                    }
                }
            }

            if (matches) {
                List<ItemStack> drops = removeBlockAndGetDrops(
                    player,
                    stack,
                    world,
                    px,
                    py,
                    pz,
                    block,
                    EnchantmentHelper.getSilkTouchModifier(player),
                    EnchantmentHelper.getFortuneModifier(player));
                if (!player.capabilities.isCreativeMode) allDrops.addAll(drops);

                cleared++;
                blocksSinceHunger++;
                gap = 0;

                if (blocksSinceHunger >= 50) {
                    blocksSinceHunger = 0;
                    player.getFoodStats()
                        .addExhaustion(1f);
                }

                if (player.worldObj.rand.nextFloat() < 0.5f && !player.capabilities.isCreativeMode) {
                    if (toolMaxDamage > 0) {
                        if (MetaGeneratedTool.getToolDamage(stack) + 1 >= toolMaxDamage) {
                            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.break", 1.0F, 1.0F);
                            if (stack.stackSize > 0) stack.stackSize--;
                            isEnable = false;
                            break;
                        } else {
                            ItemUtils.setToolDamage(stack, MetaGeneratedTool.getToolDamage(stack) + 1);
                        }
                    }
                }

            } else {
                if (gap >= maxGap) continue;
                gap++;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) == 1) {
                            queue.add(new Node(px + dx, py + dy, pz + dz, gap));
                        }
                    }
                }
            }
        }

        if (blocksSinceHunger > 0) {
            player.getFoodStats()
                .addExhaustion(1f);
        }

        Object2IntOpenHashMap<ItemStackWrapper> merged = new Object2IntOpenHashMap<>();
        for (ItemStack drop : allDrops) {
            if (drop == null) continue;
            ItemStackWrapper key = new ItemStackWrapper(drop);
            merged.put(key, merged.getOrDefault(key, 0) + drop.stackSize);
        }
        for (Object2IntMap.Entry<ItemStackWrapper> entry : merged.object2IntEntrySet()) {
            ItemStack dropStack = entry.getKey()
                .stack()
                .copy();
            dropStack.stackSize = entry.getIntValue();

            EntityItem entityItem = new EntityItem(world, player.posX, player.posY + 1, player.posZ, dropStack);
            entityItem.delayBeforeCanPickup = 0;
            entityItem.motionX = 0;
            entityItem.motionY = 0;
            entityItem.motionZ = 0;
            world.spawnEntityInWorld(entityItem);
        }
        isEnable = false;
    }

    public List<ItemStack> removeBlockAndGetDrops(EntityPlayerMP player, ItemStack stack, World world, int x, int y,
        int z, Block block, boolean silk, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        if (!world.blockExists(x, y, z)) return drops;

        Block blk = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (blk == null || blk.isAir(world, x, y, z)) return drops;
        if (block != null && blk != block) return drops;

        float hardness = blk.getBlockHardness(world, x, y, z);
        if (hardness < 0) return drops;

        if (player.theItemInWorldManager.tryHarvestBlock(x, y, z)) {
            if (silk) {
                Item item = Item.getItemFromBlock(blk);
                if (item != null) {
                    drops.add(new ItemStack(item, 1, blk.damageDropped(meta)));
                }
            } else {
                drops.addAll(blk.getDrops(world, x, y, z, meta, fortune));
            }
        }
        return drops;
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null) return;
        ItemStack heldItem = event.harvester.getCurrentEquippedItem();
        if (!(heldItem.getItem() instanceof VeinMiningPickaxe veinMiningPickaxe)) return;
        EntityPlayer harvester = event.harvester;
        if (harvester instanceof EntityPlayerMP && veinMiningPickaxe.isEnable) {
            event.drops.clear();
        }
    }

    @Desugar
    private record Node(int x, int y, int z, int gap) {}

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle(String messageKey) {
        IChatComponent component = new ChatComponentTranslation(messageKey);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle(String messageKey, int range) {
        IChatComponent component = new ChatComponentTranslation(messageKey, range);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }
}
