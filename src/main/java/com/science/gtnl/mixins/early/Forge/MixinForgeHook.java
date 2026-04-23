package com.science.gtnl.mixins.early.Forge;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.ScienceNotLeisure;

@Mixin(value = ForgeHooks.class, remap = false)
public class MixinForgeHook {

    @Inject(method = "onPlaceItemIntoWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preOnPlaceItemIntoWorldRewrite(ItemStack itemstack, EntityPlayer player, World world, int x,
        int y, int z, int side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (world.isRemote) return;
        if (itemstack == null) return;
        int meta = itemstack.getItemDamage();
        int size = itemstack.stackSize;
        NBTTagCompound nbt = null;
        if (itemstack.getTagCompound() != null) {
            nbt = (NBTTagCompound) itemstack.getTagCompound()
                .copy();
        }

        if (!(itemstack.getItem() instanceof ItemBucket)) {
            world.captureBlockSnapshots = true;
        }

        boolean flag = itemstack.getItem()
            .onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
        world.captureBlockSnapshots = false;

        if (flag) {
            int newMeta = itemstack.getItemDamage();
            int newSize = itemstack.stackSize;
            NBTTagCompound newNBT = null;
            if (itemstack.getTagCompound() != null) {
                newNBT = (NBTTagCompound) itemstack.getTagCompound()
                    .copy();
            }

            net.minecraftforge.event.world.BlockEvent.PlaceEvent placeEvent = null;
            List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) world.capturedBlockSnapshots.clone();
            world.capturedBlockSnapshots.clear();

            itemstack.setItemDamage(meta);
            itemstack.stackSize = size;
            itemstack.setTagCompound(nbt);

            if (blockSnapshots.size() > 1) {
                placeEvent = ForgeEventFactory
                    .onPlayerMultiBlockPlace(player, blockSnapshots, ForgeDirection.getOrientation(side));
            } else if (blockSnapshots.size() == 1) {
                placeEvent = ForgeEventFactory
                    .onPlayerBlockPlace(player, blockSnapshots.get(0), ForgeDirection.getOrientation(side));
            }

            if (placeEvent != null && (placeEvent.isCanceled())) {
                flag = false;
                world.restoringBlockSnapshots = true;
                for (BlockSnapshot blocksnapshot : blockSnapshots) {
                    blocksnapshot.restore(true, false);
                }
                world.restoringBlockSnapshots = false;
            } else {
                itemstack.setItemDamage(newMeta);
                itemstack.stackSize = newSize;
                itemstack.setTagCompound(newNBT);

                for (BlockSnapshot blocksnapshot : blockSnapshots) {
                    int blockX = blocksnapshot.x;
                    int blockY = blocksnapshot.y;
                    int blockZ = blocksnapshot.z;
                    int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
                    int updateFlag = blocksnapshot.flag;
                    Block oldBlock = blocksnapshot.replacedBlock;
                    Block newBlock = world.getBlock(blockX, blockY, blockZ);
                    if (newBlock != null && !(newBlock.hasTileEntity(metadata))) {
                        newBlock.onBlockAdded(world, blockX, blockY, blockZ);
                    }
                    world.markAndNotifyBlock(blockX, blockY, blockZ, null, oldBlock, newBlock, updateFlag);

                    if (itemstack.hasTagCompound()) {
                        NBTTagCompound itemNBT = itemstack.getTagCompound();
                        if (itemNBT.hasKey("BlockEntityTag", 10)) {
                            NBTTagCompound blockEntityTag = itemNBT.getCompoundTag("BlockEntityTag");
                            TileEntity tileentity = world.getTileEntity(blockX, blockY, blockZ);
                            if (tileentity != null) {
                                blockEntityTag.setInteger("x", blockX);
                                blockEntityTag.setInteger("y", blockY);
                                blockEntityTag.setInteger("z", blockZ);
                                try {
                                    tileentity.readFromNBT(blockEntityTag);
                                    tileentity.markDirty();
                                } catch (Throwable e) {
                                    ScienceNotLeisure.LOG.error("Failed to restore TileEntity NBT from held item", e);
                                }
                            }
                        }

                    }
                }
                player.addStat(StatList.objectUseStats[Item.getIdFromItem(itemstack.getItem())], 1);
            }
        }

        world.capturedBlockSnapshots.clear();
        cir.setReturnValue(flag);
        cir.cancel();
    }
}
