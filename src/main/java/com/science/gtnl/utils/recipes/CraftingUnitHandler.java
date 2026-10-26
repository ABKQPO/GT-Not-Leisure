package com.science.gtnl.utils.recipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import com.gtnllib.utils.GameUtils;
import com.mojang.authlib.GameProfile;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.StatusMessage;
import com.science.gtnl.utils.BlockState;
import com.science.gtnl.utils.SimpleItem;

import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.networking.security.ISecurityGrid;
import appeng.block.crafting.BlockCraftingUnit;
import appeng.core.worlddata.WorldData;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.crafting.TileCraftingTile;
import appeng.util.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CraftingUnitHandler {

    private static final Object2ObjectMap<SimpleItem, BlockState> CraftingUnitItemMap = new Object2ObjectOpenHashMap<>();

    private static final Object2ObjectMap<BlockState, ItemStack> CraftingUnitBlockMap = new Object2ObjectOpenHashMap<>();

    @Getter
    private static final BlockState baseUnit;

    static {
        var def = AEApi.instance()
            .definitions()
            .blocks()
            .craftingUnit();
        baseUnit = new BlockState(
            def.maybeBlock()
                .get(),
            def.maybeStack(1)
                .get()
                .getItemDamage());
    }

    public static void addMatch(ItemStack item, BlockState blockState) {
        CraftingUnitItemMap.put(SimpleItem.getInstance(item), blockState);
        CraftingUnitBlockMap.put(blockState, item);
    }

    public static void addMatch(ItemStack item, IBlockDefinition block) {
        addMatch(item, new BlockState(block));
    }

    public static boolean isReplaceable(ItemStack item, BlockState block) {
        return (baseUnit.equals(block) || CraftingUnitBlockMap.containsKey(block))
            && (item == null || CraftingUnitItemMap.containsKey(SimpleItem.getInstance(item)));
    }

    public static BlockState getMatchBlock(ItemStack item) {
        return CraftingUnitItemMap.get(SimpleItem.getInstance(item));
    }

    public static ItemStack getMatchItem(BlockState block) {
        var item = CraftingUnitBlockMap.get(block);
        if (item != null) {
            item = item.copy();
            item.stackSize = 1;
        }
        return item;
    }

    public static void register() {
        var def = AEApi.instance()
            .definitions();
        var materials = def.materials();
        var blocks = def.blocks();
        addMatch(
            materials.cell1kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage1k());
        addMatch(
            materials.cell4kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage4k());
        addMatch(
            materials.cell16kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage16k());
        addMatch(
            materials.cell64kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage64k());
        addMatch(
            materials.cell256kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage256k());
        addMatch(
            materials.cell1024kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage1024k());
        addMatch(
            materials.cell4096kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage4096k());
        addMatch(
            materials.cell16384kPart()
                .maybeStack(1)
                .get(),
            blocks.craftingStorage16384k());
    }

    // Right-clicking a crafting unit with another unit's item swaps the block in place when the CPU is idle and the
    // player may build on the network. Returns whether the click was used.
    public static boolean replaceUnit(BlockCraftingUnit unit, World w, int x, int y, int z, EntityPlayer p) {
        if (!Platform.isServer() || p instanceof FakePlayer) return false;
        TileCraftingTile tg = unit.getTileEntity(w, x, y, z);
        if (tg == null) return false;
        ItemStack item = p.getHeldItem();
        // TODO: sneak-clicking with an empty hand should take a unit back out without breaking the CPU.
        if (p.isSneaking() || item == null) return false;
        BlockState blockState = new BlockState(unit, unit.getDamageValue(w, x, y, z));
        if (!CraftingUnitHandler.isReplaceable(item, blockState)) return false;

        boolean isBusy = tg.getCluster() instanceof CraftingCPUCluster
            && ((CraftingCPUCluster) tg.getCluster()).isBusy();
        if (isBusy) {
            ScienceNotLeisure.network
                .sendTo(new StatusMessage(new ChatComponentTranslation("error.rc.cpu")), (EntityPlayerMP) p);
            return true;
        }
        var block = CraftingUnitHandler.getMatchBlock(item);
        if (blockState.equals(block)) return false;
        var tile = unit.getTileEntity(w, x, y, z);
        var proxy = ((IGridProxyable) tile).getProxy();
        var node = proxy.getNode();
        if (node == null) {
            proxy.onReady();
            node = proxy.getNode();
        }
        var cache = node.getGrid()
            .getCache(ISecurityGrid.class);
        if (!(cache instanceof ISecurityGrid isg) || !isg.hasPermission(p, SecurityPermissions.BUILD)) return false;

        w.setBlock(x, y, z, block.block(), block.meta(), 2);
        tile = unit.getTileEntity(w, x, y, z);
        proxy = ((IGridProxyable) tile).getProxy();
        node = proxy.getNode();
        if (node == null) {
            proxy.onReady();
            node = proxy.getNode();
            final GameProfile profile = p.getGameProfile();
            node.setPlayerID(
                WorldData.instance()
                    .playerData()
                    .getPlayerID(profile));
        }
        item.stackSize--;
        GameUtils.placeItemBackInInventory(p, CraftingUnitHandler.getMatchItem(blockState));
        return true;
    }
}
