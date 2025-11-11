package com.reavaritia.common.item;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import com.reavaritia.ReAvaCreativeTabs;
import com.reavaritia.ReAvaItemList;
import com.reavaritia.common.SubtitleDisplay;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InfinityBucket extends Item implements IFluidContainerItem, SubtitleDisplay {

    public static int BASE_MAX_TYPES = Integer.MAX_VALUE;
    public static int MAX_FLUID_AMOUNT = Integer.MAX_VALUE;
    public static int INFINITE_FLUID_AMOUNT = -1;
    public long lastUpdateTime = 0;

    public InfinityBucket() {
        setMaxStackSize(1);
        setUnlocalizedName("InfinityBucket");
        setTextureName(RESOURCE_ROOT_ID + ":" + "InfinityBucket");
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        MinecraftForge.EVENT_BUS.register(this);
        ReAvaItemList.InfinityBucket.set(new ItemStack(this, 1));
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return getFirstFluid(container);
    }

    @Override
    public int getCapacity(ItemStack container) {
        return MAX_FLUID_AMOUNT;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) return 0;

        NBTTagList fluids = getFluidList(container);
        if (fluids == null) return 0;
        String fluidName = resource.getFluid()
            .getName();

        NBTTagCompound tagCompound = container.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("Selected")) {
            int selected = tagCompound.getInteger("Selected");
            if (selected >= 0 && selected < fluids.tagCount()) {
                NBTTagCompound tag = fluids.getCompoundTagAt(selected);
                if (fluidName.equals(tag.getString("FluidName"))) {
                    int current = tag.getInteger("Amount");
                    long total = (long) current + resource.amount;
                    int fillable = (total > MAX_FLUID_AMOUNT) ? MAX_FLUID_AMOUNT - current : resource.amount;

                    if (doFill) tag.setInteger("Amount", (int) Math.min(MAX_FLUID_AMOUNT, total));
                    return fillable;
                }
            }
        }

        for (int i = 0; i < fluids.tagCount(); i++) {
            NBTTagCompound tag = fluids.getCompoundTagAt(i);
            if (fluidName.equals(tag.getString("FluidName"))) {
                int current = tag.getInteger("Amount");
                long total = (long) current + resource.amount;
                int fillable = (total > MAX_FLUID_AMOUNT) ? MAX_FLUID_AMOUNT - current : resource.amount;

                if (doFill) tag.setInteger("Amount", (int) Math.min(MAX_FLUID_AMOUNT, total));
                return fillable;
            }
        }

        if (fluids.tagCount() >= BASE_MAX_TYPES) return 0;

        if (doFill) {
            NBTTagCompound newTag = new NBTTagCompound();
            newTag.setString("FluidName", fluidName);
            newTag.setInteger("Amount", resource.amount);
            fluids.appendTag(newTag);
        }

        return resource.amount;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) return null;

        NBTTagList fluids = getFluidList(container);
        if (fluids == null || fluids.tagCount() == 0) return null;

        NBTTagCompound nbt = container.getTagCompound();
        int selected = (nbt != null && nbt.hasKey("Selected")) ? nbt.getInteger("Selected") : 0;

        NBTTagCompound tag = null;

        if (selected >= 0 && selected < fluids.tagCount()) {
            tag = fluids.getCompoundTagAt(selected);
        }

        if (tag == null && fluids.tagCount() > 0) {
            tag = fluids.getCompoundTagAt(0);
            selected = 0;
        }

        if (tag == null) return null;

        Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
        if (fluid == null) return null;

        int amount = tag.getInteger("Amount");
        if (amount <= 0) return null;

        int drainAmount = Math.min(amount, maxDrain);
        FluidStack drained = new FluidStack(fluid, drainAmount);

        if (doDrain) {
            int remaining = amount - drainAmount;
            if (remaining <= 0) {
                NBTTagList copy = (NBTTagList) fluids.copy();
                copy.removeTag(selected);
                container.getTagCompound()
                    .setTag("Fluids", copy);
            } else {
                tag.setInteger("Amount", remaining);
            }
        }

        return drained;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            cycleSelectedFluid(stack);
            return stack;
        }

        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
            if (!tryCollectFluid(stack, world, x, y, z, player))
                tryPlaceFluid(stack, world, x, y, z, mop.sideHit, player);
        }
        return stack;
    }

    public boolean tryCollectFluid(ItemStack stack, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IFluidHandler handler) return handleTankWithdraw(stack, handler, world, x, y, z);

        Block block = world.getBlock(x, y, z);
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if (fluid == null || !isSourceBlock(world, x, y, z)) return false;

        NBTTagList fluids = getFluidList(stack);
        for (int i = 0; i < fluids.tagCount(); i++) {
            NBTTagCompound tag = fluids.getCompoundTagAt(i);
            if (fluid.getName()
                .equals(tag.getString("FluidName"))) {
                int current = tag.getInteger("Amount");
                long total = (long) current + 1000;
                tag.setInteger("Amount", (int) Math.min(MAX_FLUID_AMOUNT, total));
                world.setBlockToAir(x, y, z);
                return true;
            }
        }

        if (fluids.tagCount() >= BASE_MAX_TYPES) return false;

        NBTTagCompound newTag = new NBTTagCompound();
        newTag.setString("FluidName", fluid.getName());
        newTag.setInteger("Amount", 1000);
        fluids.appendTag(newTag);
        world.setBlockToAir(x, y, z);
        return true;
    }

    public boolean handleTankWithdraw(ItemStack stack, IFluidHandler tank, World world, int x, int y, int z) {
        FluidStack drained = tank.drain(ForgeDirection.UNKNOWN, 1000, true);
        if (drained == null) return false;

        NBTTagList fluids = getFluidList(stack);
        for (int i = 0; i < fluids.tagCount(); i++) {
            NBTTagCompound tag = fluids.getCompoundTagAt(i);
            if (drained.getFluid()
                .getName()
                .equals(tag.getString("FluidName"))) {
                int current = tag.getInteger("Amount");
                tag.setInteger("Amount", (int) Math.min(MAX_FLUID_AMOUNT, (long) current + drained.amount));
                return true;
            }
        }

        if (fluids.tagCount() >= BASE_MAX_TYPES) return false;
        NBTTagCompound newTag = new NBTTagCompound();
        newTag.setString(
            "FluidName",
            drained.getFluid()
                .getName());
        newTag.setInteger("Amount", drained.amount);
        fluids.appendTag(newTag);
        return true;
    }

    public void tryPlaceFluid(ItemStack stack, World world, int x, int y, int z, int side, EntityPlayer player) {
        NBTTagList fluids = getFluidList(stack);
        int selected = stack.getTagCompound()
            .getInteger("Selected");
        if (fluids.tagCount() == 0 || selected >= fluids.tagCount()) return;

        NBTTagCompound tag = fluids.getCompoundTagAt(selected);
        Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
        int amount = tag.getInteger("Amount");
        if (fluid == null || fluid.getBlock() == null) return;

        if (amount < 1000 && amount != INFINITE_FLUID_AMOUNT) return;

        ForgeDirection dir = ForgeDirection.getOrientation(side);
        int px = x + dir.offsetX, py = y + dir.offsetY, pz = z + dir.offsetZ;
        Block target = world.getBlock(px, py, pz);
        Material mat = target.getMaterial();

        if (world.provider.isHellWorld && fluid.getBlock() == Blocks.flowing_water) {
            world.playSoundEffect(
                px + .5,
                py + .5,
                pz + .5,
                "random.fizz",
                .5F,
                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * .8F);
            for (int i = 0; i < 8; i++)
                world.spawnParticle("largesmoke", px + Math.random(), py + Math.random(), pz + Math.random(), 0, 0, 0);
            return;
        }

        boolean canReplace = world.isAirBlock(px, py, pz) || (!mat.isSolid() && !mat.isLiquid());
        if (canReplace && fluid.getBlock()
            .canPlaceBlockAt(world, px, py, pz)) {
            world.setBlock(px, py, pz, fluid.getBlock(), 0, 3);

            if (amount != INFINITE_FLUID_AMOUNT) {
                int newAmount = amount - 1000;
                if (newAmount <= 0) fluids.removeTag(selected);
                else tag.setInteger("Amount", newAmount);
            }

            showSubtitle(fluid.getLocalizedName(), amount - 1000);
        }
    }

    public FluidStack getFirstFluid(ItemStack stack) {
        NBTTagList fluids = getFluidList(stack);
        if (fluids.tagCount() == 0) return null;

        NBTTagCompound tag = fluids.getCompoundTagAt(0);
        Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
        if (fluid == null) return null;

        return new FluidStack(fluid, tag.getInteger("Amount"));
    }

    public boolean isSourceBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block instanceof IFluidBlock fluidBlock ? fluidBlock.canDrain(world, x, y, z)
            : world.getBlockMetadata(x, y, z) == 0;
    }

    public NBTTagList getFluidList(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        if (!nbt.hasKey("Fluids")) nbt.setTag("Fluids", new NBTTagList());
        return nbt.getTagList("Fluids", 10);
    }

    public void cycleSelectedFluid(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return;

        NBTTagList fluids = getFluidList(stack);
        int count = fluids.tagCount();
        if (count <= 0) return;
        if (count == 1) {
            nbt.setInteger("Selected", 0);
            return;
        }

        int selected = nbt.getInteger("Selected");
        selected = (selected + 1) % count;
        nbt.setInteger("Selected", selected);
    }

    public NBTTagCompound getSelectedFluidTag(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return null;

        NBTTagList fluids = getFluidList(stack);
        if (fluids.tagCount() == 0) return null;

        int selected = nbt.getInteger("Selected");
        if (selected < 0 || selected >= fluids.tagCount()) {
            selected = 0;
        }

        return fluids.getCompoundTagAt(selected);
    }

    public FluidStack getSelectedFluid(ItemStack stack) {
        NBTTagCompound tag = getSelectedFluidTag(stack);
        if (tag == null) return null;

        Fluid fluid = FluidRegistry.getFluid(tag.getString("FluidName"));
        if (fluid == null) return null;

        int amount = tag.getInteger("Amount");
        return new FluidStack(fluid, amount);
    }

    @Override
    public void showSubtitle(String name, int amount) {
        String amtText = (amount == INFINITE_FLUID_AMOUNT) ? "∞" : amount + "L";
        IChatComponent comp = new ChatComponentTranslation("Tooltip_InfinityBucket_01", name, amtText);
        comp.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(comp.getFormattedText(), true);
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
        if (!(event.entity instanceof EntityPlayer player)) return;
        ItemStack stack = player.getHeldItem();
        if (stack == null || stack.getItem() != this) return;

        long now = System.currentTimeMillis();
        if (now - lastUpdateTime >= 500) {
            FluidStack fluid = getSelectedFluid(stack);
            if (fluid != null) showSubtitle(
                fluid.getFluid()
                    .getLocalizedName(),
                fluid.amount);
            lastUpdateTime = now;
        }
    }

    public void clearFluids(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Fluids", new NBTTagList());
        nbt.setInteger("Selected", 0);
        stack.setTagCompound(nbt);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal("Tooltip_InfinityBucket_00"));
        NBTTagList fluids = getFluidList(stack);
        int selected = stack.hasTagCompound() ? stack.getTagCompound()
            .getInteger("Selected") : 0;

        for (int i = 0; i < fluids.tagCount(); i++) {
            NBTTagCompound tag = fluids.getCompoundTagAt(i);
            String name = tag.getString("FluidName");
            int amount = tag.getInteger("Amount");
            Fluid fluid = FluidRegistry.getFluid(name);

            String displayName = (fluid != null) ? fluid.getLocalizedName() : name;
            String amtText = (amount == INFINITE_FLUID_AMOUNT) ? "∞" : amount + "L";
            String prefix = (i == selected) ? "§a▶ " : "  ";
            tooltip.add(prefix + displayName + " §7: " + amtText);
        }
    }
}
