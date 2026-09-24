package com.science.gtnl.common.item.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.MapMaker;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.ItemStaticDataClientOnly;
import com.science.gtnl.common.packet.ProspectingPacket;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.item.MetaItemStackUtils;
import com.science.gtnl.utils.item.MetaTooltipUtils;
import com.sinthoras.visualprospecting.VisualProspecting_API;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;
import gregtech.api.items.MetaGeneratedTool;
import gregtech.api.objects.ItemData;
import gregtech.api.task.CooperativeScheduler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.common.UndergroundOil;
import gregtech.common.ores.OreManager;
import gregtech.common.pollution.Pollution;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntLongPair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ElectricProspectorTool extends Item {

    private static final int DEFAULT_SCAN_COLOR = 0xFF7D7D7D;
    private static final int MODE_FLUIDS = ProspectingPacket.MODE_FLUIDS;
    private static final int MODE_POLLUTION = ProspectingPacket.MODE_POLLUTION;
    private static final ScannerBlockResult UNSCANNABLE_BLOCK = new ScannerBlockResult(
        "",
        DEFAULT_SCAN_COLOR,
        "",
        false);

    public String unlocalizedName = "item.gtnl.electric_prospector_tool";
    public int mCosts = 1;
    public static final Int2ObjectMap<IntLongPair> RANGE_MAP = new Int2ObjectOpenHashMap<>();
    public static final IntSet META_SET = new IntOpenHashSet();
    public final Map<EntityPlayer, Future<?>> pendingScans = new MapMaker().weakValues()
        .makeMap();

    public ElectricProspectorTool() {
        super();
        this.setUnlocalizedName("gtnl.electric_prospector_tool");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "electric_prospector_tool");
        this.setMaxStackSize(1);
        this.setMaxDamage(1);
        GameRegistry.registerItem(this, "electric_prospector_tool");
    }

    public static ItemStack initItem(int aMeta, int aRange, long maxDamage) {
        RANGE_MAP.put(aMeta, IntLongPair.of(aRange, maxDamage));
        ItemStack stack = MetaItemStackUtils.initMetaItemStack(aMeta, ItemLoader.electricProspectorTool, META_SET);
        ItemUtils.setToolMaxDamage(stack, maxDamage);
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.stackTagCompound.setInteger("toolMeta", aMeta);
        return stack;
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
    public String getUnlocalizedName(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            return "item.gtnl.electric_prospector_tool";
        }
        return "item.gtnl.electric_prospector_tool." + itemStack.stackTagCompound.getInteger("toolMeta");
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.electric_prospector_tool";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.itemIcon = iconRegister
            .registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "electric_prospector_tool/0");
        MetaTooltipUtils.registerIcons(
            META_SET,
            ItemStaticDataClientOnly.ELECTRIC_PROSPECTOR_TOOL_ICONS,
            iconRegister,
            ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "electric_prospector_tool/");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int aMetaData) {
        return MetaTooltipUtils.getIcon(ItemStaticDataClientOnly.ELECTRIC_PROSPECTOR_TOOL_ICONS, aMetaData);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> aList) {
        for (int meta : META_SET) {
            ItemStack stack = new ItemStack(ItemLoader.electricProspectorTool, 1, 0);
            ItemUtils.setToolMaxDamage(
                stack,
                RANGE_MAP.get(meta)
                    .rightLong());
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.stackTagCompound.setInteger("toolMeta", meta);
            aList.add(stack);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack aStack, World aWorld, EntityPlayer aPlayer) {
        if (!aWorld.isRemote) {
            if (!aStack.hasTagCompound()) {
                aStack.setTagCompound(new NBTTagCompound());
            }
            int meta = aStack.stackTagCompound.getInteger("toolMeta");
            IntLongPair rangeMap = RANGE_MAP.get(meta);
            if (rangeMap == null) {
                return aStack;
            }
            ItemUtils.setToolMaxDamage(aStack, rangeMap.rightLong());

            Future<?> pendingScan = pendingScans.remove(aPlayer);
            if (pendingScan != null && !pendingScan.isDone()) {
                pendingScan.cancel(true);
                aPlayer.addChatMessage(new ChatComponentText("Cancelled pending scan"));
            }

            int data = getDetravData(aStack);
            if (aPlayer.isSneaking()) {
                data++;
                if (data > 3) {
                    data = 0;
                }
                aPlayer.addChatMessage(
                    new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.mode." + data)));
                setDetravData(aStack, data);
                return aStack;
            }

            final int scanMode = data;
            final int chunkX = ((int) aPlayer.posX) >> 4;
            final int chunkZ = ((int) aPlayer.posZ) >> 4;
            final int radius = Math.max(0, rangeMap.leftInt() - 1);
            final List<Chunk> chunks = collectScannableChunks(aWorld, chunkX, chunkZ, radius);
            aPlayer.addChatMessage(new ChatComponentText("Scanning..."));

            ProspectingPacket packet = new ProspectingPacket(
                chunkX,
                chunkZ,
                (int) aPlayer.posX,
                (int) aPlayer.posZ,
                radius,
                scanMode);
            if (!aPlayer.capabilities.isCreativeMode) {
                ItemUtils.setToolDamage(
                    aStack,
                    MetaGeneratedTool.getToolDamage(aStack) + (long) this.mCosts * chunks.size());
            }

            Future<?> task = CooperativeScheduler.INSTANCE.schedule(ctx -> {
                Long2ObjectOpenHashMap<ScannerBlockResult> blockCache = new Long2ObjectOpenHashMap<>();
                while (!ctx.shouldYield()) {
                    if (chunks.isEmpty()) {
                        ctx.stop(null);
                        break;
                    }
                    Chunk chunk = chunks.removeLast();
                    scanChunk(chunk, scanMode, packet, blockCache);
                }
            })
                .onFinished(ignored -> {
                    pendingScans.remove(aPlayer);
                    if (!packet.trimToPayloadLimit(ProspectingPacket.MAX_COMPRESSED_PAYLOAD_SIZE)) {
                        aPlayer.addChatMessage(
                            new ChatComponentText("Scan result was too large to send. Reduce range or use filtering."));
                        return;
                    }
                    ScienceNotLeisure.network.sendTo(packet, (EntityPlayerMP) aPlayer);
                    if (Mods.VisualProspecting.isModLoaded()) {
                        sendVisualProspectingResults(aWorld, (EntityPlayerMP) aPlayer, scanMode, radius);
                    }
                    if (MetaGeneratedTool.getToolDamage(aStack) >= MetaGeneratedTool.getToolMaxDamage(aStack)
                        && aStack.stackSize > 0) {
                        aStack.stackSize--;
                    }
                });
            pendingScans.put(aPlayer, task);
        }

        return aStack;
    }

    private static List<Chunk> collectScannableChunks(World world, int chunkX, int chunkZ, int radius) {
        int scanRadius = radius + 1;
        List<Chunk> chunks = new ArrayList<>((2 * scanRadius + 1) * (2 * scanRadius + 1));
        for (int offsetX = -scanRadius; offsetX <= scanRadius; offsetX++) {
            for (int offsetZ = -scanRadius; offsetZ <= scanRadius; offsetZ++) {
                if (offsetX != -scanRadius && offsetX != scanRadius
                    && offsetZ != -scanRadius
                    && offsetZ != scanRadius) {
                    chunks.add(world.getChunkFromChunkCoords(chunkX + offsetX, chunkZ + offsetZ));
                }
            }
        }
        return chunks;
    }

    private void scanChunk(Chunk chunk, int scanMode, ProspectingPacket packet,
        Long2ObjectOpenHashMap<ScannerBlockResult> blockCache) {
        switch (scanMode) {
            case ProspectingPacket.MODE_BIG_ORES, ProspectingPacket.MODE_ALL_ORES -> scanChunkOres(
                chunk,
                scanMode,
                packet,
                blockCache);
            case MODE_FLUIDS -> packet
                .addFluid(chunk.xPosition, chunk.zPosition, UndergroundOil.undergroundOil(chunk, -1));
            case MODE_POLLUTION -> packet.addPollution(chunk.xPosition, chunk.zPosition, Pollution.getPollution(chunk));
            default -> {}
        }
    }

    private void scanChunkOres(Chunk chunk, int scanMode, ProspectingPacket packet,
        Long2ObjectOpenHashMap<ScannerBlockResult> blockCache) {
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int surfaceY = chunk.getHeightValue(localX, localZ);
                LongOpenHashSet seenKeys = new LongOpenHashSet(8);
                for (int y = surfaceY - 1; y >= 1; y--) {
                    Block block = chunk.getBlock(localX, y, localZ);
                    int blockMeta = chunk.getBlockMetadata(localX, y, localZ);
                    if (!seenKeys.add(getBlockKey(block, blockMeta))) {
                        continue;
                    }
                    ScannerBlockResult result = resolveScannerBlock(blockCache, block, blockMeta, scanMode);
                    if (!result.scannable()) {
                        continue;
                    }
                    packet.addBlock(chunk.xPosition * 16 + localX, y, chunk.zPosition * 16 + localZ, block, blockMeta);
                }
            }
        }
    }

    private void sendVisualProspectingResults(World world, EntityPlayerMP player, int scanMode, int radius) {
        if (scanMode == ProspectingPacket.MODE_BIG_ORES || scanMode == ProspectingPacket.MODE_ALL_ORES) {
            sendVisualProspectingOreResults(world, player, (int) player.posX, (int) player.posZ, radius * 16);
        } else if (scanMode == MODE_FLUIDS) {
            sendVisualProspectingFluidResults(world, player, (int) player.posX, (int) player.posZ, radius * 16);
        }
    }

    private void sendVisualProspectingOreResults(World world, EntityPlayerMP player, int blockX, int blockZ,
        int blockRadius) {
        VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
            player,
            VisualProspecting_API.LogicalServer
                .prospectOreVeinsWithinRadius(world.provider.dimensionId, blockX, blockZ, blockRadius),
            Collections.emptyList());
    }

    private void sendVisualProspectingFluidResults(World world, EntityPlayerMP player, int blockX, int blockZ,
        int blockRadius) {
        VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
            player,
            Collections.emptyList(),
            VisualProspecting_API.LogicalServer
                .prospectUndergroundFluidsWithingRadius(world, blockX, blockZ, blockRadius));
    }

    public void addChatMassageByValue(EntityPlayer aPlayer, int value, String name) {
        if (value < 0) {
            aPlayer.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocalFormatted("detrav.scanner.found.texts.6", name)));
        } else if (value < 1) {
            aPlayer.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocalFormatted("detrav.scanner.found.texts.6", "")));
        } else {
            aPlayer.addChatMessage(
                new ChatComponentText(
                    StatCollector.translateToLocalFormatted("detrav.scanner.found.texts.6", name) + " " + value));
        }
    }

    @Override
    public boolean onItemUse(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int aSide,
        float hitX, float hitY, float hitZ) {
        if (aWorld.isRemote) {
            return true;
        }

        if (!aStack.hasTagCompound()) {
            aStack.setTagCompound(new NBTTagCompound());
        }
        int meta = aStack.stackTagCompound.getInteger("toolMeta");
        IntLongPair rangeMap = RANGE_MAP.get(meta);
        if (rangeMap == null) {
            return true;
        }
        ItemUtils.setToolMaxDamage(aStack, rangeMap.rightLong());

        int data = getDetravData(aStack);
        if (data < MODE_FLUIDS) {
            if (aWorld.getBlock(aX, aY, aZ) == Blocks.bedrock) {
                FluidStack fluidStack = UndergroundOil.undergroundOil(aWorld.getChunkFromBlockCoords(aX, aZ), -1);
                addChatMassageByValue(
                    aPlayer,
                    fluidStack == null ? 0 : fluidStack.amount,
                    getFluidDisplayName(fluidStack));
                damageTool(aStack, aPlayer, mCosts);
            } else {
                prospectSingleChunk(aStack, aPlayer, aWorld, aX, aY, aZ);
            }
            return true;
        }

        if (data < MODE_POLLUTION) {
            FluidStack fluidStack = UndergroundOil.undergroundOil(aWorld.getChunkFromBlockCoords(aX, aZ), -1);
            addChatMassageByValue(aPlayer, fluidStack == null ? 0 : fluidStack.amount, getFluidDisplayName(fluidStack));
            damageTool(aStack, aPlayer, mCosts);
            return true;
        }

        addChatMassageByValue(aPlayer, getPollution(aWorld, aX, aZ), "Pollution");
        return true;
    }

    public void prospectSingleChunk(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        Object2IntOpenHashMap<String> oreCounts = new Object2IntOpenHashMap<>();
        aPlayer.addChatMessage(
            new ChatComponentText(
                StatCollector.translateToLocal("detrav.scanner.prospecting") + " (" + aX + ", " + aZ + ")"));
        processOreProspecting(aStack, aPlayer, aWorld, aX, aY, aZ, oreCounts);

        for (Object2IntMap.Entry<String> entry : oreCounts.object2IntEntrySet()) {
            addChatMassageByValue(aPlayer, entry.getIntValue(), entry.getKey());
        }

        if (Mods.VisualProspecting.isModLoaded()) {
            sendVisualProspectingOreResults(aWorld, (EntityPlayerMP) aPlayer, aX, aZ, 0);
        }
    }

    public void processOreProspecting(ItemStack aStack, EntityPlayer aPlayer, World world, int x, int y, int z,
        Object2IntOpenHashMap<String> oreCounts) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        int scanMode = getDetravData(aStack);
        Long2ObjectOpenHashMap<ScannerBlockResult> blockCache = new Long2ObjectOpenHashMap<>();

        ScannerBlockResult targetResult = resolveScannerBlock(blockCache, block, meta, scanMode);
        if (targetResult.scannable()) {
            incrementOreCount(oreCounts, targetResult.displayName());
            damageTool(aStack, aPlayer, mCosts);
            return;
        }

        ItemData itemData = GTOreDictUnificator.getAssociation(new ItemStack(block, 1, meta));
        if (itemData != null) {
            try {
                addChatMassageByValue(aPlayer, -1, itemData.toString());
                damageTool(aStack, aPlayer, mCosts);
            } catch (RuntimeException e) {
                addChatMassageByValue(aPlayer, -1, "ERROR, lol ^_^");
            }
            return;
        }

        if (scanMode >= MODE_FLUIDS) {
            return;
        }

        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int surfaceY = chunk.getHeightValue(localX, localZ);
                LongOpenHashSet seenKeys = new LongOpenHashSet(8);
                for (int localY = surfaceY - 1; localY >= 1; localY--) {
                    Block chunkBlock = chunk.getBlock(localX, localY, localZ);
                    int chunkMeta = chunk.getBlockMetadata(localX, localY, localZ);
                    if (!seenKeys.add(getBlockKey(chunkBlock, chunkMeta))) {
                        continue;
                    }
                    if (resolveScannerBlock(blockCache, chunkBlock, chunkMeta, scanMode).scannable()) {
                        incrementOreCount(oreCounts, new ItemStack(chunkBlock, 1, chunkMeta).getDisplayName());
                    }
                }
            }
        }
        damageTool(aStack, aPlayer, mCosts);
    }

    public void incrementOreCount(Object2IntOpenHashMap<String> oreCounts, String oreName) {
        oreCounts.addTo(oreName, 1);
    }

    public String getFluidDisplayName(FluidStack fluidStack) {
        return fluidStack == null ? StatCollector.translateToLocal("gui.detrav.scanner.unknown_fluid")
            : fluidStack.getLocalizedName();
    }

    private ScannerBlockResult resolveScannerBlock(Long2ObjectOpenHashMap<ScannerBlockResult> blockCache, Block block,
        int meta, int scanMode) {
        long blockKey = getBlockKey(block, meta);
        ScannerBlockResult cached = blockCache.get(blockKey);
        if (cached != null) {
            return cached;
        }

        ScannerBlockResult result = resolveUncached(block, meta, scanMode);
        blockCache.put(blockKey, result);
        return result;
    }

    private ScannerBlockResult resolveUncached(Block block, int meta, int scanMode) {
        try (var info = OreManager.getOreInfo(block, meta)) {
            if (info != null && (scanMode == ProspectingPacket.MODE_ALL_ORES || !info.isSmall)) {
                return new ScannerBlockResult(
                    new ItemStack(block, 1, meta).getDisplayName(),
                    DEFAULT_SCAN_COLOR,
                    "",
                    true);
            }
        }
        return UNSCANNABLE_BLOCK;
    }

    private long getBlockKey(Block block, int meta) {
        return (((long) Block.getIdFromBlock(block)) << 32) | (meta & 0xFFFFFFFFL);
    }

    public void damageTool(ItemStack aStack, EntityPlayer aPlayer, int amount) {
        if (!aPlayer.capabilities.isCreativeMode) {
            ItemUtils.setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + amount);
            if (MetaGeneratedTool.getToolDamage(aStack) >= MetaGeneratedTool.getToolMaxDamage(aStack)
                && aStack.stackSize > 0) {
                aStack.stackSize--;
            }
        }
    }

    public static int getPollution(World aWorld, int aX, int aZ) {
        return Pollution.getPollution(aWorld.getChunkFromBlockCoords(aX, aZ));
    }

    public int getDetravData(ItemStack aStack) {
        NBTTagCompound nbt = aStack.getTagCompound();
        if (nbt != null && nbt.hasKey("DetravData")) {
            return nbt.getInteger("DetravData");
        }
        return 0;
    }

    public void setDetravData(ItemStack aStack, int data) {
        NBTTagCompound nbt = aStack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            aStack.setTagCompound(nbt);
        }
        nbt.setInteger("DetravData", data);
    }

    private record ScannerBlockResult(String displayName, int color, String materialName, boolean scannable) {}
}
