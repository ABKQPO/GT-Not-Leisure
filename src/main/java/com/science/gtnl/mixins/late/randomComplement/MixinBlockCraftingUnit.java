package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.StatusMessage;
import com.science.gtnl.utils.BlockState;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.recipes.CraftingUnitHandler;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.security.ISecurityGrid;
import appeng.block.AEBaseTileBlock;
import appeng.block.crafting.BlockCraftingUnit;
import appeng.core.worlddata.WorldData;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.crafting.TileCraftingTile;
import appeng.util.Platform;
import lombok.val;

@SuppressWarnings("ALL")
@Mixin(value = BlockCraftingUnit.class, remap = false)
public class MixinBlockCraftingUnit extends AEBaseTileBlock {

    public MixinBlockCraftingUnit(Material mat) {
        super(mat);
    }

    @Inject(method = "onBlockActivated", at = @At("HEAD"), remap = true, cancellable = true)
    public void onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int side, float hitX, float hitY,
        float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (Platform.isServer()) {
            if (p instanceof FakePlayer) return;
            TileCraftingTile tg = this.getTileEntity(w, x, y, z);
            if (tg == null) return;
            boolean isBusy = tg.getCluster() instanceof CraftingCPUCluster
                && ((CraftingCPUCluster) tg.getCluster()).isBusy();
            ItemStack item = p.getHeldItem();
            BlockState blockState;
            if (p.isSneaking()) {
                // TODO: Rework CPU dismantling around security terminals without destroying the whole CPU.
                // if (item == null) {
                // if (isBusy) {
                // ScienceNotLeisure.network.sendTo(
                // new StatusMessage(new ChatComponentTranslation("error.rc.cpu")),
                // (EntityPlayerMP) p);
                // cir.setReturnValue(true);
                // } else if (CraftingUnitHandler
                // .isReplaceable(null, blockState = new BlockState(this, this.getDamageValue(w, x, y, z)))) {
                // var block = CraftingUnitHandler.getBaseUnit();
                // if (blockState.equals(block))return;
                // var tile = this.getTileEntity(w, x, y, z);
                // var proxy = ((IGridProxyable) tile).getProxy();
                // var node = proxy.getNode();
                // if (node == null) {
                // proxy.onReady();
                // node = proxy.getNode();
                // }
                // val cache = node.getGrid().getCache(ISecurityGrid.class);
                // if (cache instanceof ISecurityGrid isg && isg.hasPermission(p, SecurityPermissions.BUILD)) {
                // w.setBlock(x, y, z, block.block(), block.meta(), 2);
                // Utils.placeItemBackInInventory(p, CraftingUnitHandler.getMatchItem(blockState));
                // cir.setReturnValue(true);
                // }
                // }
                // }
            } else if (item != null && CraftingUnitHandler
                .isReplaceable(item, blockState = new BlockState(this, this.getDamageValue(w, x, y, z)))) {
                    if (isBusy) {
                        ScienceNotLeisure.network.sendTo(
                            new StatusMessage(new ChatComponentTranslation("error.rc.cpu")),
                            (EntityPlayerMP) p);
                        cir.setReturnValue(true);
                    } else {
                        var block = CraftingUnitHandler.getMatchBlock(item);
                        if (blockState.equals(block)) return;
                        var tile = this.getTileEntity(w, x, y, z);
                        var proxy = ((IGridProxyable) tile).getProxy();
                        var node = proxy.getNode();
                        if (node == null) {
                            proxy.onReady();
                            node = proxy.getNode();
                        }
                        val cache = node.getGrid()
                            .getCache(ISecurityGrid.class);
                        if (cache instanceof ISecurityGrid isg && isg.hasPermission(p, SecurityPermissions.BUILD)) {
                            w.setBlock(x, y, z, block.block(), block.meta(), 2);
                            tile = this.getTileEntity(w, x, y, z);
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
                            Utils.placeItemBackInInventory(p, CraftingUnitHandler.getMatchItem(blockState));
                            cir.setReturnValue(true);
                        }
                    }
                }
        }
    }
}
