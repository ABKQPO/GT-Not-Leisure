package com.science.gtnl.compat.botania;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.IManaDissolvable;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.recipe.RecipeManaInfusion;

@EventBusSubscriber
public class ManaPoolEvents {

    private ManaPoolEvents() {}

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj == null) return;

        if (player.worldObj.isRemote) return;

        ItemStack held = player.getHeldItem();
        if (held == null) return;

        TileEntity te = player.worldObj.getTileEntity(event.x, event.y, event.z);

        if (!(te instanceof IManaPool pool)) return;

        boolean didSomething = false;

        if (held.getItem() instanceof IManaDissolvable dissolvable) {
            int before = held.stackSize;

            EntityItem fakeItem = new EntityItem(player.worldObj, event.x + 0.5, event.y + 1.0, event.z + 0.5, held);

            dissolvable.onDissolveTick(pool, held, fakeItem);

            if (held.stackSize != before) {
                didSomething = true;
            }
        } else if (held.getItem() instanceof IManaItem) {
            didSomething = tryManaTransfer(te, held);
        } else {
            didSomething = tryInfusion(player, pool, held);
        }

        if (didSomething) {
            event.setCanceled(true);
        }
    }

    public static boolean tryInfusion(EntityPlayer player, IManaPool pool, ItemStack stack) {
        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
            if (!recipe.matches(stack)) continue;

            int manaPer = recipe.getManaToConsume();
            if (manaPer <= 0) return false;

            int maxByMana = pool.getCurrentMana() / manaPer;
            int maxByItems = stack.stackSize;

            int times = Math.min(maxByMana, maxByItems);

            if (times <= 0) return false;

            pool.recieveMana(-manaPer * times);

            stack.stackSize -= times;
            if (stack.stackSize <= 0) {
                player.setCurrentItemOrArmor(0, null);
            }

            ItemStack out = recipe.getOutput()
                .copy();
            out.stackSize *= times;

            EntityItem outputItem = new EntityItem(player.worldObj, player.posX, player.posY + 0.5, player.posZ, out);

            player.worldObj.spawnEntityInWorld(outputItem);
            return true;
        }

        return false;
    }

    public static boolean tryManaTransfer(TileEntity pool, ItemStack stack) {
        IManaItem manaItem = (IManaItem) stack.getItem();
        IManaPool manaPool = (IManaPool) pool;

        boolean outputting = manaPool.isOutputtingPower();
        int transferRate = manaItem.getMaxMana(stack);

        if (outputting) {
            if (!manaItem.canReceiveManaFromPool(stack, pool)) return false;

            int space = transferRate - manaItem.getMana(stack);
            if (space <= 0) return false;

            int available = manaPool.getCurrentMana();
            if (available <= 0) return false;

            int move = Math.min(transferRate, Math.min(space, available));
            manaItem.addMana(stack, move);
            manaPool.recieveMana(-move);

        } else if (pool instanceof ISparkAttachable attachable) {
            if (!manaItem.canExportManaToPool(stack, pool)) return false;

            int available = manaItem.getMana(stack);
            if (available <= 0) return false;

            int space = attachable.getAvailableSpaceForMana();
            if (space <= 0) return false;

            int move = Math.min(transferRate, Math.min(space, available));
            manaItem.addMana(stack, -move);
            manaPool.recieveMana(move);
        }
        return true;
    }
}
