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
import net.minecraft.util.EnumChatFormatting;
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
import gregtech.api.interfaces.IOreMaterial;
import gregtech.api.items.MetaGeneratedTool;
import gregtech.api.objects.ItemData;
import gregtech.api.task.CooperativeScheduler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.common.UndergroundOil;
import gregtech.common.ores.OreInfo;
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
    private static final ScannerBlockResult UNSCANNABLE_BLOCK = new ScannerBlockResult(null, 0, false);

    public String unlocalizedName = "ElectricProspectorTool";
    public int mCosts = 1;
    public static final Int2ObjectMap<IntLongPair> RANGE_MAP = new Int2ObjectOpenHashMap<>();
    public static final IntSet META_SET = new IntOpenHashSet();
    public final Map<EntityPlayer, Future<?>> pendingScans = new MapMaker().weakValues()
        .makeMap();

    public ElectricProspectorTool() {
        super();
        this.setUnlocalizedName("ElectricProspectorTool");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "ElectricProspectorTool");
        this.setMaxStackSize(1);
        this.setMaxDamage(1);
        GameRegistry.registerItem(this, getUnlocalizedName());
    }

    public static ItemStack initItem(int aMeta, int aRange, long maxDamage) {
        RANGE_MAP.put(aMeta, IntLongPair.of(aRange, maxDamage));
        ItemStack stack = MetaItemStackUtils.initMetaItemStack(aMeta, ItemLoader.electricProspectorTool, META_SET);
        ItemUtils.setToolMaxDamage(stack, maxDamage);
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
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
        if (!itemStack.hasTagCompound()) return "ElectricProspectorTool";
        int meta = itemStack.stackTagCompound.getInteger("toolMeta");
        return "item.ElectricProspectorTool." + meta;
    }

    @Override
    public String getUnlocalizedName() {
        return "ElectricProspectorTool";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.itemIcon = iconRegister
            .registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "ElectricProspectorTool/0");
        MetaTooltipUtils.registerIcons(
            META_SET,
            ItemStaticDataClientOnly.ELECTRIC_PROSPECTOR_TOOL_ICONS,
            iconRegister,
            ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + "ElectricProspectorTool/");
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

            if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
            stack.stackTagCompound.setInteger("toolMeta", meta);

            aList.add(stack);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack aStack, World aWorld, EntityPlayer aPlayer) {
        if (!aWorld.isRemote) {
            if (!aStack.hasTagCompound()) aStack.setTagCompound(new NBTTagCompound());
            int meta = aStack.stackTagCompound.getInteger("toolMeta");
            IntLongPair rangeMap = RANGE_MAP.get(meta);
            if (rangeMap == null) return aStack;
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

            int cX = ((int) aPlayer.posX) >> 4;
            int cZ = ((int) aPlayer.posZ) >> 4;
            int radius = Math.max(0, rangeMap.leftInt() - 1);
            int chunkCapacity = Math.max(0, (radius * 2 + 1) * (radius * 2 + 1));
            List<Chunk> chunks = new ArrayList<>(chunkCapacity);
            aPlayer.addChatMessage(new ChatComponentText("Scanning..."));

            int scanRadius = radius + 1;
            for (int i = -scanRadius; i <= scanRadius; i++) {
                for (int j = -scanRadius; j <= scanRadius; j++) {
                    if (i != -scanRadius && i != scanRadius && j != -scanRadius && j != scanRadius) {
                        chunks.add(aWorld.getChunkFromChunkCoords(cX + i, cZ + j));
                    }
                }
            }

            ProspectingPacket packet = new ProspectingPacket(
                cX,
                cZ,
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
                Long2ObjectOpenHashMap<ScannerBlockResult> displayCache = new Long2ObjectOpenHashMap<>();
                while (!ctx.shouldYield()) {
                    if (chunks.isEmpty()) {
                        ctx.stop(null);
                        break;
                    }
                    Chunk c = chunks.removeLast();
                    switch (scanMode) {
                        case ProspectingPacket.MODE_BIG_ORES, ProspectingPacket.MODE_ALL_ORES -> {
                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {
                                    int height = c.getHeightValue(x, z);
                                    LongOpenHashSet seenDisplayKeys = new LongOpenHashSet(8);
                                    for (int y = height - 1; y >= 1; y--) {
                                        Block block = c.getBlock(x, y, z);
                                        int blockMeta = c.getBlockMetadata(x, y, z);
                                        long displayKey = getDisplayKey(block, blockMeta);
                                        if (!seenDisplayKeys.add(displayKey)) {
                                            continue;
                                        }
                                        ScannerBlockResult displayData = resolveScannerBlock(
                                            displayCache,
                                            block,
                                            blockMeta,
                                            scanMode);
                                        if (!displayData.scannable()) {
                                            continue;
                                        }
                                        packet.addBlock(
                                            c.xPosition * 16 + x,
                                            y,
                                            c.zPosition * 16 + z,
                                            displayData.displayName(),
                                            displayData.color());
                                    }
                                }
                            }
                        }
                        case ProspectingPacket.MODE_FLUIDS -> packet
                            .addFluid(c.xPosition, c.zPosition, UndergroundOil.undergroundOil(c, -1));
                        case ProspectingPacket.MODE_POLLUTION -> packet
                            .addPollution(c.xPosition, c.zPosition, Pollution.getPollution(c));
                        default -> {}
                    }
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
                        if (scanMode == ProspectingPacket.MODE_BIG_ORES
                            || scanMode == ProspectingPacket.MODE_ALL_ORES) {
                            sendVisualProspectingOreResults(
                                aWorld,
                                (EntityPlayerMP) aPlayer,
                                (int) aPlayer.posX,
                                (int) aPlayer.posZ,
                                radius * 16);
                        } else if (scanMode == ProspectingPacket.MODE_FLUIDS) {
                            sendVisualProspectingFluidResults(
                                aWorld,
                                (EntityPlayerMP) aPlayer,
                                (int) aPlayer.posX,
                                (int) aPlayer.posZ,
                                radius * 16);
                        }
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

    public void addChatMassageByValue(EntityPlayer aPlayer, int value, String name) {
        if (value < 0) {
            aPlayer.addChatMessage(
                new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.found.texts.6") + name));
        } else if (value < 1) {
            aPlayer
                .addChatMessage(new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.found.texts.6")));
        } else aPlayer.addChatMessage(
            new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.found.texts.6") + name + " " + value));
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
        if (data < ProspectingPacket.MODE_FLUIDS) {
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

        if (data < ProspectingPacket.MODE_POLLUTION) {
            FluidStack fluidStack = UndergroundOil.undergroundOil(aWorld.getChunkFromBlockCoords(aX, aZ), -1);
            addChatMassageByValue(aPlayer, fluidStack == null ? 0 : fluidStack.amount, getFluidDisplayName(fluidStack));
            damageTool(aStack, aPlayer, mCosts);
            return true;
        }

        int pollution = getPollution(aWorld, aX, aZ);
        addChatMassageByValue(aPlayer, pollution, "Pollution");
        return true;
    }

    // Used by Electric scanner when scanning the chunk whacked by the scanner. 100% chance find rate
    public void prospectSingleChunk(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        Object2IntOpenHashMap<String> oreCounts = new Object2IntOpenHashMap<>();
        aPlayer.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GOLD + StatCollector.translateToLocal(
                    "detrav.scanner.prospecting") + EnumChatFormatting.BLUE + "(" + aX + ", " + aZ + ")"));
        processOreProspecting(aStack, aPlayer, aWorld, aX, aY, aZ, oreCounts);

        for (Object2IntMap.Entry<String> entry : oreCounts.object2IntEntrySet()) {
            addChatMassageByValue(aPlayer, entry.getIntValue(), entry.getKey());
        }

        if (Mods.VisualProspecting.isModLoaded()) {
            sendVisualProspectingOreResults(aWorld, (EntityPlayerMP) aPlayer, aX, aZ, 0);
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

    public void processOreProspecting(ItemStack aStack, EntityPlayer aPlayer, World world, int x, int y, int z,
        Object2IntOpenHashMap<String> oreCounts) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        ItemStack blockStack = new ItemStack(block, 1, meta);
        int scanMode = getDetravData(aStack);
        Long2ObjectOpenHashMap<ScannerBlockResult> displayCache = new Long2ObjectOpenHashMap<>();

        ScannerBlockResult targetDisplay = resolveScannerBlock(displayCache, block, meta, scanMode);
        if (targetDisplay.scannable()) {
            incrementOreCount(oreCounts, targetDisplay.displayName());
            damageTool(aStack, aPlayer, mCosts);
            return;
        }

        ItemData itemData = GTOreDictUnificator.getAssociation(blockStack);
        if (itemData != null) {
            try {
                addChatMassageByValue(aPlayer, -1, itemData.toString());
                damageTool(aStack, aPlayer, mCosts);
            } catch (Exception e) {
                addChatMassageByValue(aPlayer, -1, "ERROR, lol ^_^");
            }
            return;
        }

        if (scanMode >= ProspectingPacket.MODE_FLUIDS) {
            return;
        }

        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        for (int chunkX = 0; chunkX < 16; chunkX++) {
            for (int chunkZ = 0; chunkZ < 16; chunkZ++) {
                int maxY = chunk.getHeightValue(chunkX, chunkZ);
                LongOpenHashSet seenDisplayKeys = new LongOpenHashSet(8);
                for (int chunkY = maxY - 1; chunkY >= 1; chunkY--) {
                    Block chunkBlock = chunk.getBlock(chunkX, chunkY, chunkZ);
                    int chunkMeta = chunk.getBlockMetadata(chunkX, chunkY, chunkZ);
                    long displayKey = getDisplayKey(chunkBlock, chunkMeta);
                    if (!seenDisplayKeys.add(displayKey)) {
                        continue;
                    }

                    ScannerBlockResult chunkDisplay = resolveScannerBlock(
                        displayCache,
                        chunkBlock,
                        chunkMeta,
                        scanMode);
                    if (chunkDisplay.scannable()) {
                        incrementOreCount(oreCounts, chunkDisplay.displayName());
                        continue;
                    }

                    ItemData chunkItemData = GTOreDictUnificator
                        .getAssociation(new ItemStack(chunkBlock, 1, chunkMeta));
                    if (chunkItemData != null && chunkItemData.mPrefix.toString()
                        .startsWith("ore")) {
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

    public int rgbaToColor(short[] rgba) {
        if (rgba == null || rgba.length < 3) {
            return DEFAULT_SCAN_COLOR;
        }
        return (0xFF << 24) | ((rgba[0] & 0xFF) << 16) | ((rgba[1] & 0xFF) << 8) | (rgba[2] & 0xFF);
    }

    private ScannerBlockResult resolveScannerBlock(Long2ObjectOpenHashMap<ScannerBlockResult> displayCache, Block block,
        int meta, int scanMode) {
        long displayKey = getDisplayKey(block, meta);
        ScannerBlockResult cached = displayCache.get(displayKey);
        if (cached != null) {
            return cached;
        }

        ScannerBlockResult result = UNSCANNABLE_BLOCK;
        try (OreInfo<IOreMaterial> info = OreManager.getOreInfo(block, meta)) {
            if (info != null && info.isNatural && (scanMode == ProspectingPacket.MODE_ALL_ORES || !info.isSmall)) {
                result = new ScannerBlockResult(
                    new ItemStack(block, 1, meta).getDisplayName(),
                    info.material == null ? DEFAULT_SCAN_COLOR : rgbaToColor(info.material.getRGBA()),
                    true);
            }
        }

        displayCache.put(displayKey, result);
        return result;
    }

    private long getDisplayKey(Block block, int meta) {
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

    private record ScannerBlockResult(String displayName, int color, boolean scannable) {}
}
