package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.ItemFluidContainer;

import com.gtnllib.utils.MathUtils;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.entity.EntityPortalGunOrb;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.EquipmentNet;
import com.science.gtnl.common.portalgun.PortalGunData;
import com.science.gtnl.common.portalgun.PortalGunDestination;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Why: DuraDisplay prints a charge percentage only for energy items; reporting shimmer as RF gives the gun the same
// percentage as the armor and the railgun, while it never takes in or gives out any energy.
public class PortalGun extends ItemFluidContainer implements IEnergyContainerItem {

    public PortalGun(String name, GTNLItemList entry) {
        super(0);
        this.setUnlocalizedName("gtnl." + name);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + name);
        this.setMaxStackSize(1);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, name);
        entry.set(new ItemStack(this, 1));
    }

    public static ItemStack held(EntityPlayer player) {
        ItemStack stack = player == null ? null : player.getCurrentEquippedItem();
        return stack != null && stack.getItem() instanceof PortalGun ? stack : null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote || !(player instanceof EntityPlayerMP playerMP)) return stack;
        MovingObjectPosition hit = getMovingObjectPositionFromPlayer(world, player, true);
        if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK && collectShimmer(stack, world, playerMP, hit))
            return stack;
        fire(stack, world, playerMP, player.isSneaking());
        return stack;
    }

    private boolean collectShimmer(ItemStack stack, World world, EntityPlayerMP player, MovingObjectPosition hit) {
        Block block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);
        if (!(block instanceof IFluidBlock fluidBlock) || fluidBlock.getFluid() != BlockLoader.shimmerFluid
            || !fluidBlock.canDrain(world, hit.blockX, hit.blockY, hit.blockZ)) return false;
        if (!canCollectShimmer(stack, world, player, hit)) return true;
        FluidStack available = fluidBlock.drain(world, hit.blockX, hit.blockY, hit.blockZ, false);
        if (available == null || fill(stack, available, false) < available.amount) {
            EquipmentNet.hotbar(player, "item.gtnl.portal_gun.shimmer_full");
            return true;
        }
        fill(stack, fluidBlock.drain(world, hit.blockX, hit.blockY, hit.blockZ, true), true);
        world.playSoundAtEntity(player, "random.splash", 0.5F, 1.4F);
        return true;
    }

    private static boolean canCollectShimmer(ItemStack stack, World world, EntityPlayer player,
        MovingObjectPosition hit) {
        if (!world.canMineBlock(player, hit.blockX, hit.blockY, hit.blockZ)
            || !player.canPlayerEdit(hit.blockX, hit.blockY, hit.blockZ, hit.sideHit, stack)) return false;
        // The initial use may be RIGHT_CLICK_AIR; protection handlers need the actual fluid coordinates.
        PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(
            player,
            PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,
            hit.blockX,
            hit.blockY,
            hit.blockZ,
            hit.sideHit,
            world);
        if (event.isCanceled() || event.useBlock == Result.DENY || event.useItem == Result.DENY) return false;
        // Draining removes the source block; interaction permission alone need not permit editing a claim.
        BlockEvent.BreakEvent breaking = new BlockEvent.BreakEvent(
            hit.blockX,
            hit.blockY,
            hit.blockZ,
            world,
            world.getBlock(hit.blockX, hit.blockY, hit.blockZ),
            world.getBlockMetadata(hit.blockX, hit.blockY, hit.blockZ),
            player);
        MinecraftForge.EVENT_BUS.post(breaking);
        return !breaking.isCanceled();
    }

    public void fire(ItemStack stack, World world, EntityPlayerMP player, boolean back) {
        PortalGunDestination target = back ? PortalGunData.previous(stack)
            : PortalGunData.favorite(stack, PortalGunData.selectedSlot(stack));
        if (target == null) {
            EquipmentNet
                .hotbar(player, back ? "item.gtnl.portal_gun.no_previous" : "item.gtnl.portal_gun.no_destination");
            return;
        }
        if (!reachable(world.provider.dimensionId) || !reachable(target.dimension())) {
            EquipmentNet.hotbar(player, "item.gtnl.portal_gun.dimension_blocked");
            return;
        }
        boolean creative = player.capabilities.isCreativeMode;
        int shimmer = creative ? 0
            : shimmerCost(
                target.dimension() == world.provider.dimensionId,
                target.distanceTo(player.posX, player.posY, player.posZ));
        FluidStack held = getFluid(stack);
        if (shimmer > 0 && (held == null || held.amount < shimmer)) {
            EquipmentNet.hotbar(player, "item.gtnl.portal_gun.no_shimmer", shimmer);
            return;
        }
        if (shimmer > 0) drain(stack, shimmer, true);
        world.spawnEntityInWorld(new EntityPortalGunOrb(world, player, target, PortalGunData.gunId(stack)));
        world.playSoundAtEntity(player, "mob.ghast.fireball", 0.6F, 1.6F);
        PortalGunData.setPrevious(stack, PortalGunDestination.of(player, ""));
    }

    public static void saved(EntityPlayerMP player, int slot, PortalGunDestination here) {
        EquipmentNet.hotbar(
            player,
            "item.gtnl.portal_gun.slot_saved",
            slot + 1,
            MathHelper.floor_double(here.x()),
            MathHelper.floor_double(here.y()),
            MathHelper.floor_double(here.z()));
    }

    public static boolean blocked(int dimension) {
        for (int id : MainConfig.item.portal_gun.dimensionBlacklist) if (id == dimension) return true;
        return false;
    }

    public static boolean reachable(int dimension) {
        // Why: DimensionManager.getProvider reads the loaded world, so it throws for a dimension nobody is in.
        return !blocked(dimension) && DimensionManager.isDimensionRegistered(dimension);
    }

    public static int shimmerCost(boolean sameDimension, double distance) {
        var config = MainConfig.item.portal_gun;
        if (!sameDimension) return config.maxShimmerPerShot;
        return (int) Math.min(config.maxShimmerPerShot, Math.ceil(Math.max(0, distance) / config.blocksPerShimmer));
    }

    @Override
    public int getCapacity(ItemStack container) {
        return MainConfig.item.portal_gun.shimmerCapacity;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        FluidStack shimmer = getFluid(container);
        return shimmer == null ? 0 : shimmer.amount;
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return getCapacity(container);
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null || resource.getFluid() != BlockLoader.shimmerFluid) return 0;
        // Why: ItemFluidContainer.fill limits by its capacity field, not by getCapacity
        capacity = getCapacity(container);
        return super.fill(container, resource, doFill);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        FluidStack shimmer = getFluid(stack);
        return 1
            - MathUtils.clampUnit((shimmer == null ? 0 : shimmer.amount) / (double) Math.max(1, getCapacity(stack)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> toolTip, boolean advancedToolTips) {
        FluidStack shimmer = getFluid(stack);
        toolTip.add(
            EnumChatFormatting.GREEN + StatCollector.translateToLocalFormatted(
                "item.gtnl.portal_gun.tooltip.shimmer",
                EquipmentEnergy.format(shimmer == null ? 0 : shimmer.amount),
                EquipmentEnergy.format(getCapacity(stack))));
        PortalGunDestination target = PortalGunData.favorite(stack, PortalGunData.selectedSlot(stack));
        toolTip.add(
            target == null
                ? EnumChatFormatting.YELLOW
                    + StatCollector.translateToLocal("item.gtnl.portal_gun.tooltip.no_destination")
                : EnumChatFormatting.AQUA + StatCollector
                    .translateToLocalFormatted("item.gtnl.portal_gun.tooltip.destination", target.describe()));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.portal_gun.tooltip.fire"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.portal_gun.tooltip.back"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.portal_gun.tooltip.wheel"));
        toolTip.add(StatCollector.translateToLocal("item.gtnl.portal_gun.tooltip.refill"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        items.add(new ItemStack(this));
        ItemStack full = new ItemStack(this);
        fill(full, new FluidStack(BlockLoader.shimmerFluid, getCapacity(full)), true);
        items.add(full);
    }
}
