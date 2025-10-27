package com.science.gtnl.common.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregtech.api.util.GTUtility;
import vazkii.botania.common.Botania;

public class ItemInfinityItem extends Item implements IFluidContainerItem {

    public Block block;
    public Fluid fluid;
    public boolean isBlockItem;
    public static ExecutorService FLUID_CLEAR_EXECUTOR = Executors.newFixedThreadPool(1);

    public ItemInfinityItem(String name, Block block, Fluid fluid, GTNLItemList itemList) {
        this(name, block, fluid, true, itemList);
    }

    public ItemInfinityItem(String name, Block block, boolean isBlockItem, GTNLItemList itemList) {
        this(name, block, null, isBlockItem, itemList);
    }

    public ItemInfinityItem(String name, Block block, GTNLItemList itemList) {
        this(name, block, null, true, itemList);
    }

    public ItemInfinityItem(String name, Fluid fluid, boolean isBlockItem, GTNLItemList itemList) {
        this(name, null, fluid, isBlockItem, itemList);
    }

    public ItemInfinityItem(String name, Fluid fluid, GTNLItemList itemList) {
        this(name, null, fluid, true, itemList);
    }

    public ItemInfinityItem(String name, Block block, Fluid fluid, boolean isBlockItem, GTNLItemList itemList) {
        this.isBlockItem = isBlockItem;
        this.block = block;
        this.fluid = fluid;
        this.setUnlocalizedName(name);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + name);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setMaxStackSize(1);
        itemList.set(new ItemStack(this));
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public FluidStack getFluid(ItemStack aStack) {
        if (fluid != null) return new FluidStack(fluid, Integer.MAX_VALUE);
        return null;
    }

    @Override
    public int getCapacity(ItemStack container) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        return GTUtility.areFluidsEqual(resource, new FluidStack(fluid, 1)) ? resource.amount : 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (fluid != null) return new FluidStack(fluid, maxDrain);
        return null;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote || !isBlockItem) return false;

        x += Facing.offsetsXForSide[side];
        y += Facing.offsetsYForSide[side];
        z += Facing.offsetsZForSide[side];

        Material material = world.getBlock(x, y, z)
            .getMaterial();
        Material blockMaterial = this.block.getMaterial();
        boolean flag = !material.isSolid();
        Block block = world.getBlock(x, y, z);
        if (!world.isAirBlock(x, y, z) && !flag) {
            return false;
        } else {
            if (flag && !material.isLiquid() && block != this.block) {
                world.func_147480_a(x, y, z, true);
            }
            if (blockMaterial.isLiquid() || this.block.canPlaceBlockAt(world, x, y, z)) {
                world.setBlock(x, y, z, this.block, 0, 3);
                world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    this.block.stepSound.getStepResourcePath(),
                    (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
                    this.block.stepSound.getPitch() * 0.8F);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!(stack.getItem() == ItemLoader.superstrongSponge)) {
            return stack;
        }
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack;

        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;

        if (!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, mop.sideHit, stack)) return stack;

        Block targetBlock = world.getBlock(x, y, z);
        if (!(targetBlock instanceof BlockFluidBase || targetBlock instanceof BlockLiquid)) return stack;

        if (player.isSneaking()) {
            FLUID_CLEAR_EXECUTOR.submit(() -> clearConnectedFluid(world, x, y, z, targetBlock, 327670));
        } else {
            world.setBlockToAir(x, y, z);
            playSound(world, x, y, z, targetBlock);
        }

        return stack;
    }

    public void playSound(World world, int x, int y, int z, Block block) {
        world.playSoundEffect(
            x + 0.5,
            y + 0.5,
            z + 0.5,
            block.stepSound.getStepResourcePath(),
            (block.stepSound.getVolume() + 1.0F) / 2.0F,
            block.stepSound.getPitch() * 0.8F);
    }

    public void clearConnectedFluid(World world, int x, int y, int z, Block fluidBlock, int limit) {
        FLUID_CLEAR_EXECUTOR.submit(() -> {
            Queue<int[]> queue = new ArrayDeque<>();
            Set<Long> visited = new HashSet<>();
            List<int[]> blocksToClear = new ArrayList<>();

            queue.add(new int[] { x, y, z });

            while (!queue.isEmpty() && blocksToClear.size() < limit) {
                int[] pos = queue.poll();
                int px = pos[0], py = pos[1], pz = pos[2];
                long key = (((long) px) & 0x3FFFFFF) << 38 | (((long) py) & 0xFFF) << 26 | (((long) pz) & 0x3FFFFFF);
                if (visited.contains(key)) continue;
                visited.add(key);

                if (!world.blockExists(px, py, pz)) continue;

                Block block = world.getBlock(px, py, pz);
                if (block == fluidBlock) {
                    blocksToClear.add(new int[] { px, py, pz });
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) == 1) {
                                    queue.add(new int[] { px + dx, py + dy, pz + dz });
                                }
                            }
                        }
                    }
                }
            }

            MainThreadTaskQueue.schedule(() -> {
                for (int[] pos : blocksToClear) {
                    int px = pos[0], py = pos[1], pz = pos[2];
                    world.setBlockToAir(px, py, pz);
                    playSound(world, px, py, pz, fluidBlock);
                }
            });
        });
    }

    public static class MainThreadTaskQueue {

        public static final Queue<Runnable> TASK_QUEUE = new ConcurrentLinkedQueue<>();

        public static void schedule(Runnable task) {
            TASK_QUEUE.add(task);
        }

        public static void runAllTasks() {
            Runnable task;
            while ((task = TASK_QUEUE.poll()) != null) {
                try {
                    task.run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MainThreadTaskQueue.runAllTasks();
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player.worldObj.isRemote) return;

        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemInfinityItem) {
            event.setCanceled(true);
        }
    }

    public static final HashSet<UUID> playersWithReach = new HashSet<>();

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) return;

        ItemStack held = player.getHeldItem();
        boolean holdingInfinity = held != null && held.getItem() instanceof ItemInfinityItem;
        UUID uuid = player.getUniqueID();

        if (holdingInfinity) {
            if (!playersWithReach.contains(uuid)) {
                Botania.proxy.setExtraReach(player, 2F);
                playersWithReach.add(uuid);
            }
        } else {
            if (playersWithReach.contains(uuid)) {
                Botania.proxy.setExtraReach(player, -2F);
                playersWithReach.remove(uuid);
            }
        }
    }

}
