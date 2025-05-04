package com.reavaritia.common.item;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import com.reavaritia.ReAvaCreativeTabs;
import com.reavaritia.ReAvaItemList;
import com.reavaritia.TextLocalization;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.objects.GTItemStack;
import gregtech.api.util.GTUtility;

public class InfinityBucket extends Item implements IFluidContainerItem {

    protected boolean Stop = false;
    private static final int BASE_MAX_TYPES = 128;
    private static final int MAX_FLUID_AMOUNT = Integer.MAX_VALUE;
    private static final int INFINITE_FLUID_AMOUNT = -1;
    private long lastUpdateTime = 0;

    public InfinityBucket() {
        setMaxStackSize(1);
        setUnlocalizedName("InfinityBucket");
        setTextureName(RESOURCE_ROOT_ID + ":" + "InfinityBucket");
        setCreativeTab(CreativeTabs.tabTools);
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        MinecraftForge.EVENT_BUS.register(this);
        registerFluidContainerData();
        ReAvaItemList.InfinityBucket.set(new ItemStack(this, 1));
    }

    private void registerFluidContainerData() {
        try {
            // 反射获取 GregTech 的容器映射
            Field sEmptyContainerToFluidToDataField = GTUtility.class.getDeclaredField("sEmptyContainerToFluidToData");
            sEmptyContainerToFluidToDataField.setAccessible(true);
            Map<GTItemStack, Map<String, FluidContainerRegistry.FluidContainerData>> sEmptyContainerToFluidToData = (Map<GTItemStack, Map<String, FluidContainerRegistry.FluidContainerData>>) sEmptyContainerToFluidToDataField
                .get(null);

            // 注册空桶（无 NBT）
            ItemStack emptyBucket = new ItemStack(this);
            GTItemStack emptyGTStack = new GTItemStack(emptyBucket);

            // 为所有流体注册填充数据
            for (Fluid fluid : FluidRegistry.getRegisteredFluids()
                .values()) {
                ItemStack filledBucket = createFilledBucket(fluid);
                FluidContainerRegistry.FluidContainerData data = new FluidContainerRegistry.FluidContainerData(
                    new FluidStack(fluid, 1000),
                    filledBucket,
                    emptyBucket,
                    false);
                Map<String, FluidContainerRegistry.FluidContainerData> fluidMap = sEmptyContainerToFluidToData
                    .computeIfAbsent(emptyGTStack, k -> new HashMap<>());
                fluidMap.put(fluid.getName(), data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ItemStack createFilledBucket(Fluid fluid) {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList fluids = new NBTTagList();
        NBTTagCompound fluidTag = new NBTTagCompound();
        fluidTag.setString("FluidName", fluid.getName());
        fluidTag.setInteger("Amount", 1000); // 初始填充量
        fluids.appendTag(fluidTag);
        nbt.setTag("Fluids", fluids);
        stack.setTagCompound(nbt);
        return stack;
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return getFirstFluid(container);
    }

    @Override
    public int getCapacity(ItemStack container) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) return 0;

        // 确保容器有有效的 NBT 数据
        NBTTagCompound nbt = container.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            container.setTagCompound(nbt);
        }

        // 获取或创建流体列表
        NBTTagList fluids = nbt.getTagList("Fluids", 10);
        String targetFluidName = resource.getFluid()
            .getName();
        int filledAmount = 0;

        // 1. 检查是否已有相同类型的流体，尝试合并
        for (int i = 0; i < fluids.tagCount(); i++) {
            NBTTagCompound fluidTag = fluids.getCompoundTagAt(i); // 使用 getCompoundTagAt
            String existingFluidName = fluidTag.getString("FluidName");
            if (existingFluidName.equals(targetFluidName)) {
                int currentAmount = fluidTag.getInteger("Amount");
                long total = (long) currentAmount + resource.amount;

                // 处理整数溢出
                if (total > Integer.MAX_VALUE) {
                    filledAmount = Integer.MAX_VALUE - currentAmount;
                    if (doFill) {
                        fluidTag.setInteger("Amount", Integer.MAX_VALUE);
                    }
                } else {
                    filledAmount = resource.amount;
                    if (doFill) {
                        fluidTag.setInteger("Amount", (int) total);
                    }
                }

                // 更新 NBT 数据（替换旧条目）
                if (doFill) {
                    NBTTagList newFluids = new NBTTagList();
                    for (int j = 0; j < fluids.tagCount(); j++) {
                        if (j == i) {
                            newFluids.appendTag(fluidTag); // 替换更新后的条目
                        } else {
                            // 关键修复：使用 getCompoundTagAt 替代 get
                            newFluids.appendTag(fluids.getCompoundTagAt(j));
                        }
                    }
                    nbt.setTag("Fluids", newFluids);
                }
                return filledAmount;
            }
        }

        // 2. 添加新类型流体（动态扩展最大类型数）
        int currentTypes = fluids.tagCount();
        int maxTypes = BASE_MAX_TYPES + currentTypes;

        if (currentTypes >= maxTypes) {
            return 0; // 已达最大类型限制
        }

        // 计算实际可填充量（不超过 Integer.MAX_VALUE）
        int fillable = Math.min(resource.amount, Integer.MAX_VALUE);

        if (doFill) {
            // 创建新流体条目
            NBTTagCompound newFluidTag = new NBTTagCompound();
            newFluidTag.setString("FluidName", targetFluidName);
            newFluidTag.setInteger("Amount", fillable);
            fluids.appendTag(newFluidTag);
            nbt.setTag("Fluids", fluids); // 更新 NBT
        }

        return fillable;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        NBTTagCompound nbt = container.getTagCompound();
        if (nbt == null) return null;

        NBTTagList fluids = nbt.getTagList("Fluids", 10);
        if (fluids.tagCount() == 0) return null;

        NBTTagCompound fluidTag = fluids.getCompoundTagAt(0);
        String fluidName = fluidTag.getString("FluidName");
        int currentAmount = fluidTag.getInteger("Amount");

        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) return null;

        int drainAmount = Math.min(currentAmount, maxDrain);
        FluidStack drained = new FluidStack(fluid, drainAmount);

        if (doDrain) {
            fluidTag.setInteger("Amount", currentAmount - drainAmount);
            if (fluidTag.getInteger("Amount") <= 0) {
                fluids.removeTag(0);
            }
            nbt.setTag("Fluids", fluids);
        }

        return drained;
    }

    public FluidStack getFirstFluid(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            NBTTagList fluids = nbt.getTagList("Fluids", 10);
            if (fluids.tagCount() > 0) {
                NBTTagCompound firstFluidTag = fluids.getCompoundTagAt(0);
                String fluidName = firstFluidTag.getString("FluidName");
                int amount = firstFluidTag.getInteger("Amount");
                Fluid fluid = FluidRegistry.getFluid(fluidName);
                if (fluid != null) {
                    return new FluidStack(fluid, amount);
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            cycleSelectedFluid(stack);
            return stack;
        }

        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Stop = false;
            int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
            boolean collected = tryCollectFluid(stack, world, x, y, z, player);
            if (!collected && !Stop) {
                tryPlaceFluid(stack, world, x, y, z, mop.sideHit, player);
            }
        }
        return stack;
    }

    private boolean tryCollectFluid(ItemStack stack, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IFluidHandler) {
            IFluidHandler tank = (IFluidHandler) te;
            return handleTankWithdraw(stack, tank, world, x, y, z);
        }

        Block block = world.getBlock(x, y, z);
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        NBTTagCompound nbt = stack.getTagCompound();

        if (fluid != null && nbt != null) {
            // 1. 检查是否为流体源块（仅允许收集源块）
            if (!isSourceBlock(world, x, y, z)) {
                return false; // 不是源块，无法收集
            }

            NBTTagList fluids = nbt.getTagList("Fluids", 10);
            int maxTypes = BASE_MAX_TYPES + fluids.tagCount();

            int existingIndex = -1;
            for (int i = 0; i < fluids.tagCount(); i++) {
                NBTTagCompound fluidTag = fluids.getCompoundTagAt(i);
                if (fluidTag.getString("FluidName")
                    .equals(fluid.getName())) {
                    existingIndex = i;
                    break;
                }
            }

            // 2. 处理流体合并或新增
            if (existingIndex != -1) {
                // 合并现有流体
                NBTTagCompound existingTag = fluids.getCompoundTagAt(existingIndex);
                int currentAmount = existingTag.getInteger("Amount");
                long total = (long) currentAmount + 1000;

                if (total > Integer.MAX_VALUE) {
                    int added = Integer.MAX_VALUE - currentAmount;
                    existingTag.setInteger("Amount", Integer.MAX_VALUE);
                    // 将剩余流体回填到世界（转换为流动方块）
                    if (added < 1000) {
                        FluidStack remaining = new FluidStack(fluid, 1000 - added);
                        Block fluidBlock = fluid.getBlock();
                        world.setBlock(x, y, z, fluidBlock, 7, 3); // 7 表示流动方块
                    } else {
                        world.setBlockToAir(x, y, z); // 完全收集，移除源块
                    }
                } else {
                    existingTag.setInteger("Amount", (int) total);
                    world.setBlockToAir(x, y, z); // 完全收集，移除源块
                }
            } else {
                // 新增流体类型
                if (fluids.tagCount() >= maxTypes) return false;
                NBTTagCompound newFluidTag = new NBTTagCompound();
                newFluidTag.setString("FluidName", fluid.getName());
                newFluidTag.setInteger("Amount", 1000);
                fluids.appendTag(newFluidTag);
                world.setBlockToAir(x, y, z); // 移除源块
            }

            // 3. 更新桶的NBT数据
            nbt.setTag("Fluids", fluids);
            nbt.setInteger("Selected", 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof IFluidHandler) {
                IFluidHandler tank = (IFluidHandler) te;

                if (player.isSneaking()) {
                    return handleTankDeposit(stack, tank);
                } else {
                    return handleTankWithdraw(stack, tank, world, x, y, z);
                }
            }
        }
        return false;
    }

    private int getMaxTypes(int currentTypes) {
        return BASE_MAX_TYPES + currentTypes;
    }

    private boolean handleTankDeposit(ItemStack stack, IFluidHandler tank) {
        NBTTagList fluids = getFluidList(stack);
        if (fluids.tagCount() == 0) return false;

        NBTTagCompound fluidTag = fluids.getCompoundTagAt(0);
        String fluidName = fluidTag.getString("FluidName");
        int storedAmount = fluidTag.getInteger("Amount");

        FluidStack fs = new FluidStack(
            FluidRegistry.getFluid(fluidName),
            storedAmount == INFINITE_FLUID_AMOUNT ? 1000 : Math.min(storedAmount, 1000));

        int transferred = tank.fill(ForgeDirection.UNKNOWN, fs, true);
        if (transferred > 0) {
            if (storedAmount != INFINITE_FLUID_AMOUNT) {
                int newStoredAmount = storedAmount - transferred;
                if (newStoredAmount < 0) {
                    int excessAmount = transferred - storedAmount;
                    fluidTag.setInteger("Amount", MAX_FLUID_AMOUNT);
                    tank.drain(ForgeDirection.UNKNOWN, excessAmount, true);
                } else {
                    fluidTag.setInteger("Amount", newStoredAmount);
                    if (fluidTag.getInteger("Amount") == 0) {
                        fluids.removeTag(0);
                        if (fluids.tagCount() == 0) {
                            clearFluids(stack);
                        }
                    }
                }
            }
            ensureNonEmptyFluids(fluids);
            showFluidInfo(fluidName, fluidTag.getInteger("Amount"));
            return true;
        }
        return false;
    }

    private boolean handleTankWithdraw(ItemStack stack, IFluidHandler tank, World world, int x, int y, int z) {
        FluidStack tankFluid = tank.drain(ForgeDirection.UNKNOWN, 1000, true);
        if (tankFluid == null) return false;

        NBTTagList fluids = getFluidList(stack);
        int maxTypes = getMaxTypes(fluids.tagCount());

        boolean exists = fluids.tagCount() > 0 && fluids.getCompoundTagAt(0)
            .getString("FluidName")
            .equals(
                tankFluid.getFluid()
                    .getName());

        if (!exists) {
            if (fluids.tagCount() >= maxTypes) return false;
            addNewFluidType(stack, tankFluid.getFluid(), tankFluid.amount);
            return true;
        }

        return mergeFluid(stack, tankFluid.getFluid(), tankFluid.amount, tank, x, y, z);
    }

    private void addNewFluidType(ItemStack stack, Fluid fluid, int amount) {
        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagList fluids = nbt.getTagList("Fluids", 10);

        NBTTagCompound newTag = new NBTTagCompound();
        newTag.setString("FluidName", fluid.getName());
        newTag.setInteger("Amount", Math.min(amount, Integer.MAX_VALUE));
        fluids.appendTag(newTag);

        nbt.setInteger("Selected", fluids.tagCount() - 1);
        ensureNonEmptyFluids(fluids);
    }

    private boolean mergeFluid(ItemStack stack, Fluid fluid, int amount, IFluidHandler tank, int x, int y, int z) {
        NBTTagList fluids = getFluidList(stack);
        for (int i = 0; i < fluids.tagCount(); i++) {
            NBTTagCompound tag = fluids.getCompoundTagAt(i);
            if (tag.getString("FluidName")
                .equals(fluid.getName())) {
                int current = tag.getInteger("Amount");
                if (current != INFINITE_FLUID_AMOUNT) {
                    if ((long) current + amount > MAX_FLUID_AMOUNT) {
                        int addAmount = MAX_FLUID_AMOUNT - current;
                        tag.setInteger("Amount", MAX_FLUID_AMOUNT);
                        int excessAmount = amount - addAmount;
                        FluidStack remaining = new FluidStack(fluid, excessAmount);
                        if (tank != null) {
                            tank.fill(ForgeDirection.UNKNOWN, remaining, true);
                        }
                        return false;
                    } else {
                        tag.setInteger("Amount", current + amount);
                    }
                    ensureNonEmptyFluids(fluids);
                    return true;
                }
            }
        }
        return false;
    }

    private void tryPlaceFluid(ItemStack stack, World world, int x, int y, int z, int side, EntityPlayer player) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return;

        NBTTagList fluids = nbt.getTagList("Fluids", 10);
        int selected = nbt.getInteger("Selected");
        if (fluids.tagCount() == 0 || selected >= fluids.tagCount()) return;

        NBTTagCompound fluidTag = fluids.getCompoundTagAt(selected);
        String fluidName = fluidTag.getString("FluidName");
        int amount = fluidTag.getInteger("Amount");
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) return;

        if (amount < 1000 && amount != INFINITE_FLUID_AMOUNT) {
            return;
        }

        ForgeDirection dir = ForgeDirection.getOrientation(side);
        int placeX = x + dir.offsetX;
        int placeY = y + dir.offsetY;
        int placeZ = z + dir.offsetZ;

        Block targetBlock = world.getBlock(placeX, placeY, placeZ);
        Fluid targetFluid = FluidRegistry.lookupFluidForBlock(targetBlock);
        boolean isFlowing = false;

        if (targetBlock.getMaterial()
            .isLiquid()) {
            try {
                Method isSource = targetBlock.getClass()
                    .getMethod("isSourceBlock", World.class, int.class, int.class, int.class);
                isFlowing = !(Boolean) isSource.invoke(targetBlock, world, placeX, placeY, placeZ);
            } catch (Exception e) {
                isFlowing = world.getBlockMetadata(placeX, placeY, placeZ) != 0;
            }
        }

        boolean canReplace = targetBlock.isAir(world, placeX, placeY, placeZ)
            || (targetFluid != null && !targetFluid.getName()
                .equals(fluid.getName()) && !isSourceBlock(world, placeX, placeY, placeZ) && isFlowing);

        if (canReplace) {
            Block fluidBlock = fluid.getBlock();
            if (fluidBlock != null && fluidBlock.canPlaceBlockAt(world, placeX, placeY, placeZ)) {
                world.setBlock(placeX, placeY, placeZ, fluidBlock, 0, 3);

                if (amount != INFINITE_FLUID_AMOUNT) {
                    int newAmount = amount - 1000;
                    if (newAmount < 0) newAmount = 0;
                    fluidTag.setInteger("Amount", newAmount);

                    if (newAmount == 0) {
                        fluids.removeTag(selected);
                        if (fluids.tagCount() == 0) {
                            clearFluids(stack);
                        } else {
                            nbt.setInteger("Selected", Math.max(0, selected - 1));
                        }
                    }
                    stack.setTagCompound(nbt);
                }
                showFluidInfo(fluidName, amount - 1000);
            }
        }
    }

    private boolean isSourceBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof IFluidBlock) {
            return ((IFluidBlock) block).canDrain(world, x, y, z);
        }
        return world.getBlockMetadata(x, y, z) == 0;
    }

    private void clearFluids(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Fluids", new NBTTagList());
        nbt.setInteger("Selected", 0);
        stack.setTagCompound(nbt);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List toolTip, boolean advanced) {
        toolTip.add(TextLocalization.Tooltip_InfinityBucket_00);

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            NBTTagList fluids = nbt.getTagList("Fluids", 10);
            if (fluids.tagCount() > 0) {
                NBTTagCompound fluidTag = fluids.getCompoundTagAt(0);
                String fluidName = fluidTag.getString("FluidName");
                int remainingAmount = fluidTag.getInteger("Amount");

                showFluidInfo(fluidName, remainingAmount);
            }
        }

        if (nbt == null) return;

        NBTTagList fluids = nbt.getTagList("Fluids", 10);

        if (fluids.tagCount() > 0) {
            NBTTagCompound first = fluids.getCompoundTagAt(0);
            addFluidToTooltip(first, toolTip, true);
        }

        for (int i = 1; i < fluids.tagCount(); i++) {
            addFluidToTooltip(fluids.getCompoundTagAt(i), toolTip, false);
        }
    }

    private void addFluidToTooltip(NBTTagCompound tag, List<String> tooltip, boolean isSelected) {
        String fluidName = tag.getString("FluidName");
        int amount = tag.getInteger("Amount");
        Fluid fluid = FluidRegistry.getFluid(fluidName);

        String displayName = (fluid != null) ? fluid.getLocalizedName() : fluidName;
        String amountText = (amount == INFINITE_FLUID_AMOUNT) ? "∞" : amount + "L";
        String prefix = isSelected ? "§a▶ " : "  ";

        tooltip.add(prefix + displayName + " §7: " + amountText);
    }

    @SideOnly(Side.CLIENT)
    private void showFluidInfo(String displayName, int remainingAmount) {
        String amountText = (remainingAmount == INFINITE_FLUID_AMOUNT) ? "∞" : remainingAmount + "L";

        IChatComponent component = new ChatComponentTranslation(
            TextLocalization.Tooltip_InfinityBucket_01,
            displayName,
            amountText);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));

        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            EntityPlayer player = event.entityPlayer;
            ItemStack stack = player.getHeldItem();

            if (stack != null && stack.getItem() == this && player.isSneaking()) {

                clearFluids(stack);
                player.addChatMessage(new ChatComponentTranslation("Tooltip_InfinityBucket_02"));
                event.setCanceled(true);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.thePlayer;
            if (player.getHeldItem() != null && player.getHeldItem()
                .getItem() == this) {
                ItemStack heldItem = player.getHeldItem();
                if (heldItem != null && heldItem.getItem() instanceof InfinityBucket) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime >= 500) {
                        NBTTagCompound nbt = heldItem.getTagCompound();
                        if (nbt != null) {
                            NBTTagList fluids = nbt.getTagList("Fluids", 10);
                            if (fluids.tagCount() > 0) {
                                NBTTagCompound fluidTag = fluids.getCompoundTagAt(0);
                                String fluidName = fluidTag.getString("FluidName");
                                int remainingAmount = fluidTag.getInteger("Amount");

                                showFluidInfo(fluidName, remainingAmount);
                            }
                        }
                        lastUpdateTime = currentTime;
                    }
                }
            }
        }
    }

    private void cycleSelectedFluid(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        NBTTagList fluids = nbt.getTagList("Fluids", 10);
        int fluidCount = fluids.tagCount();
        if (fluidCount == 0) return;

        NBTTagList newFluids = new NBTTagList();
        int currentIndex = nbt.getInteger("Selected");

        for (int i = currentIndex + 1; i < fluidCount; i++) {
            newFluids.appendTag(fluids.getCompoundTagAt(i));
        }

        for (int i = 0; i <= currentIndex; i++) {
            newFluids.appendTag(fluids.getCompoundTagAt(i));
        }

        nbt.setTag("Fluids", newFluids);
        nbt.setInteger("Selected", 0);
    }

    private NBTTagList getFluidList(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound()
                .setTag("Fluids", new NBTTagList());
        }
        return stack.getTagCompound()
            .getTagList("Fluids", 10);
    }

    private void ensureNonEmptyFluids(NBTTagList fluids) {
        if (fluids.tagCount() == 0) {
            fluids.appendTag(new NBTTagCompound());
        }
    }
}
