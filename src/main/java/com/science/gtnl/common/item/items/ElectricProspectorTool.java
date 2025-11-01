package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.ScienceNotLeisure.network;
import static com.science.gtnl.utils.item.ItemUtils.*;
import static gregtech.api.enums.Mods.VisualProspecting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.ItemStaticDataClientOnly;
import com.science.gtnl.common.packet.ProspectingPacket;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.item.MetaItemStackUtils;
import com.sinthoras.visualprospecting.VisualProspecting_API;

import bartworks.system.material.Werkstoff;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import detrav.utils.BartWorksHelper;
import detrav.utils.GTppHelper;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.items.MetaGeneratedTool;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.common.UndergroundOil;
import gregtech.common.blocks.BlockOresAbstract;
import gregtech.common.blocks.TileEntityOres;
import gregtech.common.pollution.Pollution;

public class ElectricProspectorTool extends Item {

    public String unlocalizedName = "ElectricProspectorTool";
    public static int[] DISTANCEINTS = new int[] { 0, 4, 25, 64 };
    public HashMap<String, Integer> ores;
    public int distTextIndex;
    public int mCosts = 1;
    public static final Map<Integer, Pair<Integer, Long>> mRangeMap = new HashMap<>();
    public static final Set<Integer> metaSet = new HashSet<>();

    public static String CHAT_MSG_SEPARATOR = EnumChatFormatting.STRIKETHROUGH + "--------------------";

    public ElectricProspectorTool() {
        super();
        this.setUnlocalizedName("ElectricProspectorTool");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "ElectricProspectorTool");
        this.setMaxStackSize(1);
        this.setMaxDamage(1);
    }

    public static ItemStack initItem(int aMeta, int aRange, long maxDamage) {
        mRangeMap.put(aMeta, Pair.of(aRange, maxDamage));
        ItemStack stack = MetaItemStackUtils.initMetaItemStack(aMeta, ItemLoader.electricProspectorTool, metaSet);
        setToolMaxDamage(stack, maxDamage);
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
        this.itemIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "ElectricProspectorTool/0");
        for (int meta : metaSet) {
            ItemStaticDataClientOnly.iconsMapElectricProspectorTool
                .put(meta, iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "ElectricProspectorTool/" + meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int aMetaData) {
        return ItemStaticDataClientOnly.iconsMapElectricProspectorTool.containsKey(aMetaData)
            ? ItemStaticDataClientOnly.iconsMapElectricProspectorTool.get(aMetaData)
            : ItemStaticDataClientOnly.iconsMapElectricProspectorTool.get(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> aList) {
        for (int meta : metaSet) {
            ItemStack stack = new ItemStack(ItemLoader.electricProspectorTool, 1, 0);

            setToolMaxDamage(
                stack,
                mRangeMap.get(meta)
                    .getRight());

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
            Pair<Integer, Long> rangeMap = mRangeMap.get(meta);
            if (rangeMap == null) return aStack;
            setToolMaxDamage(aStack, rangeMap.getRight());
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

            int cX = ((int) aPlayer.posX) >> 4;
            int cZ = ((int) aPlayer.posZ) >> 4;
            int size = rangeMap.getLeft();
            List<Chunk> chunks = new ArrayList<>();
            aPlayer.addChatMessage(new ChatComponentText("Scanning..."));

            for (int i = -size; i <= size; i++)
                for (int j = -size; j <= size; j++) if (i != -size && i != size && j != -size && j != size)
                    chunks.add(aWorld.getChunkFromChunkCoords(cX + i, cZ + j));
            size = size - 1;

            ProspectingPacket packet = new ProspectingPacket(
                cX,
                cZ,
                (int) aPlayer.posX,
                (int) aPlayer.posZ,
                size,
                data);
            String small_ore_keyword = StatCollector.translateToLocal("detrav.scanner.small_ore.keyword");
            for (Chunk c : chunks) {
                for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
                    int ySize = c.getHeightValue(x, z);
                    for (int y = 1; y < ySize; y++) {
                        switch (data) {
                            case 0, 1 -> {
                                Block tBlock = c.getBlock(x, y, z);
                                short tMetaID = (short) c.getBlockMetadata(x, y, z);
                                if (tBlock instanceof BlockOresAbstract) {
                                    TileEntity tTileEntity = c.getTileEntityUnsafe(x, y, z);
                                    if ((tTileEntity instanceof TileEntityOres)
                                        && ((TileEntityOres) tTileEntity).mNatural) {
                                        tMetaID = ((TileEntityOres) tTileEntity).getMetaData();
                                        try {
                                            String name = GTLanguageManager
                                                .getTranslation(tBlock.getUnlocalizedName() + "." + tMetaID + ".name");
                                            if (data != 1 && name.startsWith(small_ore_keyword)) continue;
                                            packet.addBlock(c.xPosition * 16 + x, y, c.zPosition * 16 + z, tMetaID);
                                        } catch (Exception e) {
                                            String name = tBlock.getUnlocalizedName() + ".";
                                            if (data != 1 && name.contains(".small.")) continue;
                                            packet.addBlock(c.xPosition * 16 + x, y, c.zPosition * 16 + z, tMetaID);
                                        }
                                    }
                                } else if (GTppHelper.isGTppBlock(tBlock)) {
                                    packet.addBlock(
                                        c.xPosition * 16 + x,
                                        y,
                                        c.zPosition * 16 + z,
                                        GTppHelper.getMetaFromBlock(tBlock));
                                } else if (BartWorksHelper.isOre(tBlock)) {
                                    if (data != 1 && BartWorksHelper.isSmallOre(tBlock)) continue;
                                    packet.addBlock(
                                        c.xPosition * 16 + x,
                                        y,
                                        c.zPosition * 16 + z,
                                        BartWorksHelper.getMetaFromBlock(c, x, y, z, tBlock));
                                } else if (data == 1) {
                                    ItemData tAssotiation = GTOreDictUnificator
                                        .getAssociation(new ItemStack(tBlock, 1, tMetaID));
                                    if ((tAssotiation != null) && (tAssotiation.mPrefix.toString()
                                        .startsWith("ore"))) {
                                        packet.addBlock(
                                            c.xPosition * 16 + x,
                                            y,
                                            c.zPosition * 16 + z,
                                            (short) tAssotiation.mMaterial.mMaterial.mMetaItemSubID);
                                    }
                                }
                            }
                            case 2 -> {
                                if ((x == 0) || (z == 0)) { // Skip doing the locations with the grid on them.
                                    break;
                                }
                                FluidStack fStack = UndergroundOil.undergroundOil(
                                    aWorld.getChunkFromBlockCoords(c.xPosition * 16 + x, c.zPosition * 16 + z),
                                    -1);
                                if (fStack.amount > 0) {
                                    packet.addBlock(
                                        c.xPosition * 16 + x,
                                        1,
                                        c.zPosition * 16 + z,
                                        (short) fStack.getFluidID());
                                    packet
                                        .addBlock(c.xPosition * 16 + x, 2, c.zPosition * 16 + z, (short) fStack.amount);
                                }
                            }
                            case 3 -> {
                                float polution = (float) getPollution(
                                    aWorld,
                                    c.xPosition * 16 + x,
                                    c.zPosition * 16 + z);
                                polution /= 2000000;
                                polution *= -0xFF;
                                if (polution > 0xFF) polution = 0xFF;
                                polution = 0xFF - polution;
                                packet.addBlock(c.xPosition * 16 + x, 1, c.zPosition * 16 + z, (short) polution);
                            }
                        }
                        if (data > 1) break;
                    }
                }
            }
            network.sendTo(packet, (EntityPlayerMP) aPlayer);
            if (!aPlayer.capabilities.isCreativeMode) {
                setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + (long) this.mCosts * chunks.size() / 4);
            }

            if (VisualProspecting.isModLoaded()) {
                if (data == 0 || data == 1) {
                    VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
                        (EntityPlayerMP) aPlayer,
                        VisualProspecting_API.LogicalServer.prospectOreVeinsWithinRadius(
                            aWorld.provider.dimensionId,
                            (int) aPlayer.posX,
                            (int) aPlayer.posZ,
                            size * 16),
                        new ArrayList<>());
                } else if (data == 2) {
                    VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
                        (EntityPlayerMP) aPlayer,
                        new ArrayList<>(),
                        VisualProspecting_API.LogicalServer.prospectUndergroundFluidsWithingRadius(
                            aWorld,
                            (int) aPlayer.posX,
                            (int) aPlayer.posZ,
                            size * 16));
                }
            }

            if (MetaGeneratedTool.getToolDamage(aStack) >= MetaGeneratedTool.getToolMaxDamage(aStack)) {
                if (aStack.stackSize > 0) aStack.stackSize--;
            }
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
        int data = getDetravData(aStack);
        if (data < 2) {
            if (aWorld.getBlock(aX, aY, aZ) == Blocks.bedrock) {
                if (!aWorld.isRemote) {
                    FluidStack fStack = UndergroundOil.undergroundOil(aWorld.getChunkFromBlockCoords(aX, aZ), -1);
                    addChatMassageByValue(aPlayer, fStack.amount, fStack.getLocalizedName());
                    if (!aPlayer.capabilities.isCreativeMode) {
                        setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + this.mCosts);
                    }
                }
            } else {
                if (!aWorld.isRemote) {
                    prospectSingleChunk(aStack, aPlayer, aWorld, aX, aY, aZ);
                }
            }
            if (aWorld.getBlock(aX, aY, aZ)
                .getMaterial() == Material.rock
                || aWorld.getBlock(aX, aY, aZ)
                    .getMaterial() == Material.ground
                || aWorld.getBlock(aX, aY, aZ) == GregTechAPI.sBlockOres1) {
                if (!aWorld.isRemote) {
                    prospectChunks(aStack, aPlayer, aWorld, aX, aY, aZ, new SplittableRandom(), 50);
                }
                return true;
            }
            return true;
        }
        if (data < 3) if (!aWorld.isRemote) {
            FluidStack fStack = UndergroundOil.undergroundOil(aWorld.getChunkFromBlockCoords(aX, aZ), -1);
            addChatMassageByValue(aPlayer, fStack.amount, fStack.getLocalizedName());
            if (!aPlayer.capabilities.isCreativeMode) {
                setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + this.mCosts);
            }
            return true;
        }
        if (!aWorld.isRemote) {
            if (!aStack.hasTagCompound()) aStack.setTagCompound(new NBTTagCompound());
            int meta = aStack.stackTagCompound.getInteger("toolMeta");
            Pair<Integer, Long> rangeMap = mRangeMap.get(meta);
            if (rangeMap == null) return true;
            setToolMaxDamage(aStack, rangeMap.getRight());
            int polution = getPollution(aWorld, aX, aZ);
            addChatMassageByValue(aPlayer, polution, "Pollution");
            if (MetaGeneratedTool.getToolDamage(aStack) >= MetaGeneratedTool.getToolMaxDamage(aStack)) {
                if (aStack.stackSize > 0) aStack.stackSize--;
            }
        }
        return true;
    }

    public void prospectChunks(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ,
        SplittableRandom aRandom, int chance) {
        int bX = aX;
        int bZ = aZ;

        ores = new HashMap<>();

        if (!aStack.hasTagCompound()) aStack.setTagCompound(new NBTTagCompound());
        int meta = aStack.stackTagCompound.getInteger("toolMeta");
        Pair<Integer, Long> rangeMap = mRangeMap.get(meta);
        if (rangeMap == null) return;

        int range = rangeMap.getLeft();
        if ((range % 2) == 0) {
            range += 1; // kinda not needed here, divide takes it out, but we put it back in with the range+1 in the
            // loop
        }
        range = range / 2; // Convert range from diameter to radius

        aPlayer.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GOLD + GTLanguageManager.sEnglishFile
                    .get("LanguageFile", "gt.scanner.prospecting", "Prospecting at ")
                    .getString() + EnumChatFormatting.BLUE + "(" + bX + ", " + bZ + ")"));
        for (int x = -(range); x < (range + 1); ++x) {
            aX = bX + (x * 16);
            for (int z = -(range); z < (range + 1); ++z) {

                aZ = bZ + (z * 16);
                int dist = x * x + z * z;

                for (distTextIndex = 0; distTextIndex < DISTANCEINTS.length; distTextIndex++) {
                    if (dist <= DISTANCEINTS[distTextIndex]) {
                        break;
                    }
                }
                if (MainConfig.enableDebugMode) aPlayer.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.YELLOW + "Chunk at "
                            + aX
                            + "|"
                            + aZ
                            + " to "
                            + (aX + 16)
                            + "|"
                            + (aZ + 16)
                            + StatCollector.translateToLocal("detrav.scanner.distance.texts." + distTextIndex)));
                processOreProspecting(
                    aStack,
                    aPlayer,
                    aWorld.getChunkFromBlockCoords(aX, aZ),
                    aWorld.getTileEntity(aX, aY, aZ),
                    GTOreDictUnificator.getAssociation(
                        new ItemStack(aWorld.getBlock(aX, aY, aZ), 1, aWorld.getBlockMetadata(aX, aY, aZ))),
                    aRandom,
                    chance);
            }
        }

        // List to hold unsorted scanner messages
        List<ChatComponentText> oreMessages = new ArrayList<>();

        for (String key : ores.keySet()) {
            int value = ores.get(key);
            appendChatMessageByValue(oreMessages, aPlayer, value, key);
        }

        // Define sort order by distance
        List<String> sortOrder = Arrays.asList(
            StatCollector.translateToLocal("detrav.scanner.distance.texts.4"),
            StatCollector.translateToLocal("detrav.scanner.distance.texts.3"),
            StatCollector.translateToLocal("detrav.scanner.distance.texts.2"),
            StatCollector.translateToLocal("detrav.scanner.distance.texts.1"),
            StatCollector.translateToLocal("detrav.scanner.distance.texts.0"));

        List<ChatComponentText> oreMessagesSorted = new ArrayList<>();
        oreMessagesSorted.add(new ChatComponentText(CHAT_MSG_SEPARATOR));

        // Sort ore messages by distance, separated by -----
        for (String oreFrequency : sortOrder) {
            for (ChatComponentText msg : oreMessages) {
                if (msg.getChatComponentText_TextValue()
                    .contains(oreFrequency)) {
                    oreMessagesSorted.add(msg);
                }
            }

            // Only append ----- separator if text has been added
            if (!oreMessagesSorted.get(oreMessagesSorted.size() - 1)
                .getChatComponentText_TextValue()
                .contains(CHAT_MSG_SEPARATOR)) {
                oreMessagesSorted.add(new ChatComponentText(CHAT_MSG_SEPARATOR));
            }
        }

        oreMessages.add(
            new ChatComponentText(EnumChatFormatting.WHITE + StatCollector.translateToLocal("detrav.scanner.success")));

        // Print the sorted messages
        for (ChatComponentText msg : oreMessagesSorted) {
            aPlayer.addChatMessage(msg);
        }

        if (VisualProspecting.isModLoaded()) {
            VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
                (EntityPlayerMP) aPlayer,
                VisualProspecting_API.LogicalServer.prospectOreVeinsWithinRadius(
                    aWorld.provider.dimensionId,
                    (int) aPlayer.posX,
                    (int) aPlayer.posZ,
                    range * 16),
                new ArrayList<>());
        }
    }

    // Used by Electric scanner when scanning the chunk whacked by the scanner. 100% chance find rate
    public void prospectSingleChunk(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        ores = new HashMap<>();
        aPlayer.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GOLD + StatCollector.translateToLocal(
                    "detrav.scanner.prospecting") + EnumChatFormatting.BLUE + "(" + aX + ", " + aZ + ")"));
        processOreProspecting(
            aStack,
            aPlayer,
            aWorld.getChunkFromBlockCoords(aX, aZ),
            aWorld.getTileEntity(aX, aY, aZ),
            GTOreDictUnificator
                .getAssociation(new ItemStack(aWorld.getBlock(aX, aY, aZ), 1, aWorld.getBlockMetadata(aX, aY, aZ))),
            new SplittableRandom(),
            1000);

        for (String key : ores.keySet()) {
            int value = ores.get(key);
            addChatMassageByValue(aPlayer, value, key);
        }

        if (VisualProspecting.isModLoaded()) {
            VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
                (EntityPlayerMP) aPlayer,
                VisualProspecting_API.LogicalServer.prospectOreVeinsWithinRadius(
                    aWorld.provider.dimensionId,
                    (int) aPlayer.posX,
                    (int) aPlayer.posZ,
                    0),
                new ArrayList<>());
        }
    }

    public void processOreProspecting(ItemStack aStack, EntityPlayer aPlayer, Chunk aChunk, TileEntity aTileEntity,
        ItemData tAssotiation, SplittableRandom aRandom, int chance) {
        if (aTileEntity != null) {
            if (aTileEntity instanceof TileEntityOres gt_entity) {
                short meta = gt_entity.getMetaData();
                String format = LanguageRegistry.instance()
                    .getStringLocalization("gt.blockores." + meta + ".name");
                String name = Materials.getLocalizedNameForItem(format, meta % 1000);
                addOreToHashMap(name, aPlayer);
                if (!aPlayer.capabilities.isCreativeMode) {
                    setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + this.mCosts);
                }
            }
        } else if (tAssotiation != null) {
            try {
                String name = tAssotiation.toString();
                addChatMassageByValue(aPlayer, -1, name);
                if (!aPlayer.capabilities.isCreativeMode) {
                    setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + this.mCosts);
                }
            } catch (Exception e) {
                addChatMassageByValue(aPlayer, -1, "ERROR, lol ^_^");
            }
        } else if (aRandom.nextInt(100) < chance) {
            int data = getDetravData(aStack);
            String small_ore_keyword = StatCollector.translateToLocal("detrav.scanner.small_ore.keyword");
            for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
                int ySize = aChunk.getHeightValue(x, z);
                for (int y = 1; y < ySize; y++) {

                    Block tBlock = aChunk.getBlock(x, y, z);
                    short tMetaID = (short) aChunk.getBlockMetadata(x, y, z);
                    if (tBlock instanceof BlockOresAbstract) {
                        TileEntity tTileEntity = aChunk.getTileEntityUnsafe(x, y, z);
                        if ((tTileEntity instanceof TileEntityOres) && ((TileEntityOres) tTileEntity).mNatural) {
                            tMetaID = ((TileEntityOres) tTileEntity).getMetaData();
                            try {
                                String format = LanguageRegistry.instance()
                                    .getStringLocalization(tBlock.getUnlocalizedName() + "." + tMetaID + ".name");
                                String name = Materials.getLocalizedNameForItem(format, tMetaID % 1000);
                                if (data != 1 && name.startsWith(small_ore_keyword)) continue;
                                addOreToHashMap(name, aPlayer);
                            } catch (Exception e) {
                                String name = tBlock.getUnlocalizedName() + ".";
                                if (data != 1 && name.contains(".small.")) continue;
                                addOreToHashMap(name, aPlayer);
                            }
                        }
                    } else if (GTppHelper.isGTppBlock(tBlock)) {
                        String name = GTppHelper.getGTppVeinName(tBlock);
                        if (!name.isEmpty()) addOreToHashMap(name, aPlayer);
                    } else if (BartWorksHelper.isOre(tBlock)) {
                        if (data != 1 && BartWorksHelper.isSmallOre(tBlock)) continue;
                        Werkstoff werkstoff = Werkstoff.werkstoffHashMap.getOrDefault(
                            (short) ((BartWorksHelper.getMetaFromBlock(aChunk, x, y, z, tBlock)) * -1),
                            null);
                        String type = BartWorksHelper.isSmallOre(tBlock) ? "oreSmall" : "ore";
                        String translated = GTLanguageManager.getTranslation("bw.blocktype." + type);
                        addOreToHashMap(translated.replace("%material", werkstoff.getLocalizedName()), aPlayer);
                    } else if (data == 1) {
                        tAssotiation = GTOreDictUnificator.getAssociation(new ItemStack(tBlock, 1, tMetaID));
                        if ((tAssotiation != null) && (tAssotiation.mPrefix.toString()
                            .startsWith("ore"))) {
                            try {
                                try {
                                    tMetaID = (short) tAssotiation.mMaterial.mMaterial.mMetaItemSubID;
                                    String format = LanguageRegistry.instance()
                                        .getStringLocalization("gt.blockores." + tMetaID + ".name");
                                    String name = Materials.getLocalizedNameForItem(format, tMetaID % 1000);
                                    addOreToHashMap(name, aPlayer);
                                } catch (Exception e1) {
                                    String name = tAssotiation.toString();
                                    addOreToHashMap(name, aPlayer);
                                }
                            } catch (Exception ignored) {}
                        }
                    }

                }
            }

            if (!aPlayer.capabilities.isCreativeMode) {
                setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + this.mCosts);
            }

        } else {
            if (MainConfig.enableDebugMode)
                aPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + " Failed on this chunk"));
            if (!aPlayer.capabilities.isCreativeMode) {
                setToolDamage(aStack, MetaGeneratedTool.getToolDamage(aStack) + this.mCosts / 4);
            }
        }
    }

    public void addOreToHashMap(String orename, EntityPlayer aPlayer) {
        String oreDistance = orename + StatCollector.translateToLocal("detrav.scanner.distance.texts." + distTextIndex); // orename
        if (!ores.containsKey(oreDistance)) {
            if (MainConfig.enableDebugMode) aPlayer
                .addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + " Adding to oremap " + oreDistance));
            ores.put(oreDistance, 1);
        } else {
            ores.compute(oreDistance, (k, val) -> val + 1);
        }
    }

    public void appendChatMessageByValue(List<ChatComponentText> chatMessageList, EntityPlayer aPlayer, int value,
        String name) {
        if (value < 0) {
            chatMessageList
                .add(new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.found.texts.6") + name));
        } else if (value < 1) {
            chatMessageList.add(new ChatComponentText(StatCollector.translateToLocal("detrav.scanner.found.texts.0")));
        } else if (value < 10) chatMessageList
            .add(new ChatComponentText(name + StatCollector.translateToLocal("detrav.scanner.found.texts.1")));
        else if (value < 30) chatMessageList
            .add(new ChatComponentText(name + StatCollector.translateToLocal("detrav.scanner.found.texts.2")));
        else if (value < 60) chatMessageList
            .add(new ChatComponentText(name + StatCollector.translateToLocal("detrav.scanner.found.texts.3")));
        else if (value < 100) chatMessageList
            .add(new ChatComponentText(name + StatCollector.translateToLocal("detrav.scanner.found.texts.4")));
        else chatMessageList
            .add(new ChatComponentText(name + StatCollector.translateToLocal("detrav.scanner.found.texts.5")));
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

}
