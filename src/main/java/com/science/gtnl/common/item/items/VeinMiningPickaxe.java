package com.science.gtnl.common.item.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.reavaritia.utils.item.ItemStackWrapper;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.reavaritia.utils.item.ToolHelper;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.gui.VeinMiningPickaxeGui;
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
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.modularui2.GTModularScreen;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class VeinMiningPickaxe extends ItemPickaxe implements SubtitleDisplay, IGuiHolder<PlayerInventoryGuiData> {

    public boolean isEnable;
    private final List<ItemStack> capturedHarvestDrops = new ArrayList<>();
    private int harvestingX;
    private int harvestingY;
    private int harvestingZ;

    public VeinMiningPickaxe() {
        super(EnumHelper.addToolMaterial("VEIN", 15, 20000000, 15, 3, 10));
        this.setUnlocalizedName("gtnl.vein_mining_pickaxe");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "vein_mining_pickaxe");
        this.setMaxStackSize(1);
        this.setMaxDamage(20000000);
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        GameRegistry.registerItem(this, "vein_mining_pickaxe");
        GTNLItemList.VeinMiningPickaxe.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.vein_mining_pickaxe";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        NBTTagCompound tags = itemStack.getTagCompound();
        int range = 3;
        int amount = 32767;
        boolean chainEnabled = true;
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
            if (tags.hasKey("chainEnabled")) {
                chainEnabled = tags.getBoolean("chainEnabled");
            }
        }

        toolTip.add(StatCollector.translateToLocalFormatted("item.gtnl.vein_mining_pickaxe.max_block_gap", range));
        toolTip.add(StatCollector.translateToLocalFormatted("item.gtnl.vein_mining_pickaxe.max_vein_count", amount));
        toolTip.add(
            StatCollector.translateToLocal(
                chainEnabled ? "item.gtnl.vein_mining_pickaxe.chain.enabled"
                    : "item.gtnl.vein_mining_pickaxe.chain.disabled"));
        toolTip.add(
            StatCollector.translateToLocal(
                preciseMode ? "item.gtnl.vein_mining_pickaxe.precise_mode.enabled"
                    : "item.gtnl.vein_mining_pickaxe.precise_mode.disabled"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean("preciseMode");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean enabled = !isChainEnabled(stack);
        setChainEnabled(stack, enabled);
        if (world.isRemote) {
            player.swingItem();
            showSubtitle(
                enabled ? "item.gtnl.vein_mining_pickaxe.chain.enabled"
                    : "item.gtnl.vein_mining_pickaxe.chain.disabled");
        }
        return stack;
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
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new GTModularScreen(mainPanel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new VeinMiningPickaxeGui(data, syncManager).build();
    }

    public static int getRange(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("range") ? tag.getInteger("range") : 3;
    }

    public static int getAmount(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("amount") ? tag.getInteger("amount") : 32767;
    }

    public static boolean isPreciseMode(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getBoolean("preciseMode");
    }

    public static boolean isChainEnabled(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null || !tag.hasKey("chainEnabled") || tag.getBoolean("chainEnabled");
    }

    public static void setRange(ItemStack stack, int range) {
        getOrCreateTag(stack).setInteger(
            "range",
            Math.max(-1, Math.min(range, Math.max(-1, MainConfig.item.vein_miner_pickaxe.maxRange))));
    }

    public static void setAmount(ItemStack stack, int amount) {
        getOrCreateTag(stack).setInteger(
            "amount",
            Math.max(0, Math.min(amount, Math.max(0, MainConfig.item.vein_miner_pickaxe.maxAmount))));
    }

    public static void setPreciseMode(ItemStack stack, boolean preciseMode) {
        getOrCreateTag(stack).setBoolean("preciseMode", preciseMode);
    }

    public static void setChainEnabled(ItemStack stack, boolean enabled) {
        getOrCreateTag(stack).setBoolean("chainEnabled", enabled);
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        return tag;
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
                if (!isChainEnabled(stack)) return;
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
            isEnable = false;
            return;
        }
        World world = player.worldObj;
        LongArrayFIFOQueue positionQueue = new LongArrayFIFOQueue(256);
        IntArrayFIFOQueue gapQueue = new IntArrayFIFOQueue(256);
        Long2IntOpenHashMap queuedGap = new Long2IntOpenHashMap(256);
        queuedGap.defaultReturnValue(Integer.MAX_VALUE);
        int cleared = 0;
        int blocksSinceHunger = 0;
        int toolMaxDamage = Math.toIntExact(MetaGeneratedTool.getToolMaxDamage(stack));
        boolean silkTouch = EnchantmentHelper.getSilkTouchModifier(player);
        int fortune = EnchantmentHelper.getFortuneModifier(player);
        Object2IntOpenHashMap<ItemStackWrapper> mergedDrops = new Object2IntOpenHashMap<>();
        Set<String> targetOreNames = collectOreNames(targetBlock, targetMeta);
        IdentityHashMap<Block, Int2BooleanOpenHashMap> oreMatches = new IdentityHashMap<>();
        int cachedChunkX = Integer.MIN_VALUE;
        int cachedChunkZ = Integer.MIN_VALUE;
        int cachedSectionY = Integer.MIN_VALUE;
        Chunk cachedChunk = null;
        ExtendedBlockStorage cachedStorage = null;

        long origin = encodePosition(x, y, z);
        positionQueue.enqueue(origin);
        gapQueue.enqueue(0);
        queuedGap.put(origin, 0);

        while (positionQueue.size() > 0 && cleared < amount) {
            if (!player.isSneaking()) {
                isEnable = false;
                break;
            }

            long position = positionQueue.dequeueLong();
            int px = decodeX(position);
            int py = decodeY(position);
            int pz = decodeZ(position);
            int gap = gapQueue.dequeueInt();

            if (queuedGap.get(position) != gap) continue;
            queuedGap.put(position, -1);
            if (!world.blockExists(px, py, pz)) continue;

            int chunkX = px >> 4;
            int chunkZ = pz >> 4;
            int sectionY = py >> 4;
            if (chunkX != cachedChunkX || chunkZ != cachedChunkZ) {
                cachedChunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                cachedChunkX = chunkX;
                cachedChunkZ = chunkZ;
                cachedSectionY = Integer.MIN_VALUE;
            }
            if (sectionY != cachedSectionY) {
                ExtendedBlockStorage[] storage = cachedChunk.getBlockStorageArray();
                cachedStorage = sectionY >= 0 && sectionY < storage.length ? storage[sectionY] : null;
                cachedSectionY = sectionY;
            }

            Block block = cachedStorage == null ? Blocks.air : cachedStorage.getBlockByExtId(px & 15, py & 15, pz & 15);
            int meta = cachedStorage == null ? 0 : cachedStorage.getExtBlockMetadata(px & 15, py & 15, pz & 15);
            TileEntity tileEntity = block.hasTileEntity(meta) ? world.getTileEntity(px, py, pz) : null;

            boolean matches = false;
            if (block != Blocks.air && block.getBlockHardness(world, px, py, pz) >= 0) {
                if (tileEntity instanceof IGregTechTileEntity gtTE) {
                    meta = gtTE.getMetaTileID();
                }
                if (block == targetBlock && (!preciseMode || meta == targetMeta)) {
                    matches = true;
                } else {
                    matches = matchesOreDictionary(block, meta, targetOreNames, preciseMode, oreMatches);
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
                    silkTouch,
                    fortune);
                if (!player.capabilities.isCreativeMode) {
                    for (ItemStack drop : drops) {
                        if (drop == null) continue;
                        ItemStackWrapper dropKey = new ItemStackWrapper(drop);
                        mergedDrops.put(dropKey, mergedDrops.getInt(dropKey) + drop.stackSize);
                    }
                }

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

            enqueuePosition(positionQueue, gapQueue, queuedGap, px + 1, py, pz, gap);
            enqueuePosition(positionQueue, gapQueue, queuedGap, px - 1, py, pz, gap);
            enqueuePosition(positionQueue, gapQueue, queuedGap, px, py + 1, pz, gap);
            enqueuePosition(positionQueue, gapQueue, queuedGap, px, py - 1, pz, gap);
            enqueuePosition(positionQueue, gapQueue, queuedGap, px, py, pz + 1, gap);
            enqueuePosition(positionQueue, gapQueue, queuedGap, px, py, pz - 1, gap);
        }

        if (blocksSinceHunger > 0) {
            player.getFoodStats()
                .addExhaustion(1f);
        }

        if (!mergedDrops.isEmpty()) {
            ToolHelper.generateMatterCluster(world, player, mergedDrops);
        }
        isEnable = false;
    }

    private static long encodePosition(int x, int y, int z) {
        return (((long) x) & 0x3FFFFFFL) << 38 | (((long) y) & 0xFFFL) << 26 | (((long) z) & 0x3FFFFFFL);
    }

    private static void enqueuePosition(LongArrayFIFOQueue positions, IntArrayFIFOQueue gaps,
        Long2IntOpenHashMap queuedGap, int x, int y, int z, int gap) {
        if (y < 0 || y >= 256) return;
        long position = encodePosition(x, y, z);
        int previousGap = queuedGap.get(position);
        if (previousGap == -1 || gap >= previousGap) return;
        queuedGap.put(position, gap);
        positions.enqueue(position);
        gaps.enqueue(gap);
    }

    private static int decodeX(long position) {
        return ((int) (position >> 38) << 6) >> 6;
    }

    private static int decodeY(long position) {
        return (int) (position >> 26) & 0xFFF;
    }

    private static int decodeZ(long position) {
        return (int) (position << 38 >> 38);
    }

    private static Set<String> collectOreNames(Block block, int meta) {
        Set<String> names = new HashSet<>();
        for (int id : OreDictionary.getOreIDs(new ItemStack(block, 1, meta))) {
            names.add(OreDictionary.getOreName(id));
        }
        return names;
    }

    private static boolean matchesOreDictionary(Block block, int meta, Set<String> targetNames, boolean preciseMode,
        IdentityHashMap<Block, Int2BooleanOpenHashMap> cache) {
        if (targetNames.isEmpty()) return false;
        Int2BooleanOpenHashMap blockMatches = cache.get(block);
        if (blockMatches == null) {
            blockMatches = new Int2BooleanOpenHashMap();
            cache.put(block, blockMatches);
        }
        if (blockMatches.containsKey(meta)) return blockMatches.get(meta);
        boolean matches = false;
        for (int id : OreDictionary.getOreIDs(new ItemStack(block, 1, meta))) {
            String candidateName = OreDictionary.getOreName(id);
            if (preciseMode && targetNames.contains(candidateName)) {
                matches = true;
                break;
            }
            if (!preciseMode) {
                for (String targetName : targetNames) {
                    if ((candidateName.startsWith("ore") && targetName.startsWith("ore"))
                        || targetName.startsWith(candidateName)) {
                        matches = true;
                        break;
                    }
                }
            }
            if (matches) break;
        }
        blockMatches.put(meta, matches);
        return matches;
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

        beginDropCapture(x, y, z);
        try {
            if (player.theItemInWorldManager.tryHarvestBlock(x, y, z)) {
                drops.addAll(capturedHarvestDrops);
            }
        } finally {
            endDropCapture();
        }
        return drops;
    }

    private void beginDropCapture(int x, int y, int z) {
        capturedHarvestDrops.clear();
        harvestingX = x;
        harvestingY = y;
        harvestingZ = z;
    }

    private void endDropCapture() {
        capturedHarvestDrops.clear();
    }

    private List<ItemStack> copyDrops(List<ItemStack> drops) {
        List<ItemStack> copiedDrops = new ArrayList<>();
        for (ItemStack drop : drops) {
            if (drop != null && drop.stackSize > 0) {
                copiedDrops.add(drop.copy());
            }
        }
        return copiedDrops;
    }

    private void captureDrops(List<ItemStack> drops) {
        capturedHarvestDrops.addAll(copyDrops(drops));
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null) return;
        ItemStack heldItem = event.harvester.getCurrentEquippedItem();
        if (!(heldItem.getItem() instanceof VeinMiningPickaxe veinMiningPickaxe)) return;
        EntityPlayer harvester = event.harvester;
        if (harvester instanceof EntityPlayerMP && veinMiningPickaxe.isEnable) {
            if (veinMiningPickaxe.isCapturingDropAt(event.x, event.y, event.z)) {
                veinMiningPickaxe.captureDrops(event.drops);
            }
            event.drops.clear();
        }
    }

    private boolean isCapturingDropAt(int x, int y, int z) {
        return harvestingX == x && harvestingY == y && harvestingZ == z;
    }

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
