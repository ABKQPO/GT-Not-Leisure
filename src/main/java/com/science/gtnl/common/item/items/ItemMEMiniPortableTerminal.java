package com.science.gtnl.common.item.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

import com.glodblock.github.loader.recipe.WirelessTerminalEnergyRecipe;
import com.glodblock.github.loader.recipe.WirelessTerminalQuantumBridgeRecipe;
import com.glodblock.github.util.NameConst;
import com.gtnewhorizon.gtnhlib.compat.BaublesCompat;
import com.gtnewhorizon.gtnhlib.item.ItemStackNBT;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.meminiterminal.MiniTerminalSessionService;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.config.PinsRows;
import appeng.core.sync.GuiBridge;
import appeng.items.contents.PinsHolder;
import appeng.items.tools.powered.ToolWirelessTerminal;
import appeng.util.Platform;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.common.container.InventoryBaubles;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMEMiniPortableTerminal extends ToolWirelessTerminal {

    private static final String ID_TAG = "gtnlToolboxId";
    private static final String PICKUP_TAG = "gtnlToolboxPickupToME";
    private static final Map<EntityPlayer, Map<Integer, ItemStack>> WORN_SNAPSHOTS = new WeakHashMap<>();

    public ItemMEMiniPortableTerminal() {
        setUnlocalizedName("gtnl.me_mini_portable_terminal");
        setTextureName("appliedenergistics2:ToolWirelessTerminal");
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "me_toolbox");
        GTNLItemList.MEMiniPortableTerminal.set(new ItemStack(this));
        AEApi.instance()
            .registries()
            .wireless()
            .registerWirelessHandler(this);
    }

    public static boolean isMiniTerminal(ItemStack stack) {
        return stack != null && stack.stackSize == 1 && stack.getItem() instanceof ItemMEMiniPortableTerminal;
    }

    public static boolean isPickupToMEEnabled(ItemStack stack) {
        return isMiniTerminal(stack) && ItemStackNBT.getBoolean(stack, PICKUP_TAG);
    }

    public static void setPickupToMEEnabled(ItemStack stack, boolean enabled) {
        if (isMiniTerminal(stack)) ItemStackNBT.setBoolean(stack, PICKUP_TAG, enabled);
    }

    @Override
    public boolean canHandle(ItemStack stack) {
        return isMiniTerminal(stack) && stack.getItem() == this;
    }

    @Override
    public boolean hasInfinityPower(ItemStack stack) {
        return ItemStackNBT.getBoolean(stack, infinityEnergyCard);
    }

    @Override
    public boolean hasInfinityRange(ItemStack stack) {
        return ItemStackNBT.getBoolean(stack, infinityBoosterCard);
    }

    public static UUID getTerminalId(ItemStack stack) {
        if (!isMiniTerminal(stack)) return null;
        String id = ItemStackNBT.getString(stack, ID_TAG);
        if (id.isEmpty()) return null;
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static UUID ensureIdentity(ItemStack stack, EntityPlayer player) {
        UUID id = getTerminalId(stack);
        if (id == null && isMiniTerminal(stack) && !player.worldObj.isRemote) {
            id = UUID.randomUUID();
            ItemStackNBT.setString(stack, ID_TAG, id.toString());
            player.inventory.markDirty();
        }
        return id;
    }

    public static int findTerminalSlot(EntityPlayer player, UUID id) {
        if (id == null || id.equals(getTerminalId(player.inventory.getItemStack()))) return -1;
        int found = -1;
        for (int slot : getTerminalSlots(player)) {
            if (!id.equals(getTerminalId(getTerminalStack(player, slot)))) continue;
            if (found >= 0) return -1;
            found = slot;
        }
        return found;
    }

    public static boolean isTerminalDuplicated(EntityPlayer player, UUID id) {
        if (id == null) return false;
        int count = 0;
        for (int slot : getTerminalSlots(player)) {
            if (id.equals(getTerminalId(getTerminalStack(player, slot))) && ++count > 1) return true;
        }
        return false;
    }

    public static int findFirstTerminalSlot(EntityPlayer player) {
        List<Integer> slots = getTerminalSlots(player);
        return slots.isEmpty() ? -1 : slots.get(0);
    }

    public static List<Integer> getTerminalSlots(EntityPlayer player) {
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (isMiniTerminal(player.inventory.mainInventory[slot])) slots.add(slot);
        }
        IInventory worn = Platform.isBaublesLoaded ? BaublesCompat.getBaubles(player) : null;
        if (worn != null) {
            for (int slot = 0; slot < worn.getSizeInventory(); slot++) {
                if (isMiniTerminal(worn.getStackInSlot(slot))) slots.add(Platform.baublesSlotsOffset + slot);
            }
        }
        return slots;
    }

    public static ItemStack getTerminalStack(EntityPlayer player, int slot) {
        ItemStack stack;
        if (slot >= Platform.baublesSlotsOffset) {
            IInventory worn = Platform.isBaublesLoaded ? BaublesCompat.getBaubles(player) : null;
            if (worn == null || slot - Platform.baublesSlotsOffset >= worn.getSizeInventory()) return null;
            stack = worn.getStackInSlot(slot - Platform.baublesSlotsOffset);
        } else {
            if (slot < 0 || slot >= player.inventory.mainInventory.length) return null;
            stack = player.inventory.getStackInSlot(slot);
        }
        return isMiniTerminal(stack) ? stack : null;
    }

    public static void initializePins(ItemStack stack, EntityPlayer player) {
        if (!isMiniTerminal(stack) || player.worldObj.isRemote) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            NBTTagList players = tag.getTagList("pins", NBT.TAG_COMPOUND);
            String playerId = player.getPersistentID()
                .toString();
            for (int index = 0; index < players.tagCount(); index++) {
                if (playerId.equals(
                    players.getCompoundTagAt(index)
                        .getString("playerId")))
                    return;
            }
        }
        PinsHolder holder = new PinsHolder(stack);
        holder.getPinsInv(player);
        holder.setPinsRows(player, holder.getCraftingPinsRows(player), PinsRows.TWO);
        player.inventory.markDirty();
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        super.onUpdate(stack, world, entity, slot, held);
        if (!world.isRemote && entity instanceof EntityPlayer player) ensureIdentity(stack, player);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player instanceof EntityPlayerMP serverPlayer) {
            MiniTerminalSessionService.end(serverPlayer);
            ensureIdentity(stack, player);
            initializePins(stack, player);
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public void openGui(ItemStack stack, World world, EntityPlayer player, Object mode) {
        if (!(player instanceof EntityPlayerMP serverPlayer)) return;
        MiniTerminalSessionService.end(serverPlayer);
        UUID id = ensureIdentity(stack, player);
        int slot = findTerminalSlot(player, id);
        if (slot < 0) return;
        initializePins(stack, player);
        Platform.openGUI(player, null, ForgeDirection.UNKNOWN, GuiBridge.GUI_ME, slot);
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase wearer) {
        if (wearer instanceof EntityPlayerMP player) {
            WORN_SNAPSHOTS.remove(player);
            ensureIdentity(stack, player);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase wearer) {
        if (wearer instanceof EntityPlayerMP player) WORN_SNAPSHOTS.remove(player);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase wearer) {
        if (!(wearer instanceof EntityPlayerMP player)) return;
        ensureIdentity(stack, player);
        if (!(BaublesCompat.getBaubles(player) instanceof InventoryBaubles worn)) return;
        Map<Integer, ItemStack> snapshots = WORN_SNAPSHOTS.computeIfAbsent(player, key -> new HashMap<>());

        for (int slot = 0; slot < worn.getSizeInventory(); slot++) {
            if (worn.getStackInSlot(slot) != stack) continue;
            if (!ItemStack.areItemStacksEqual(snapshots.get(slot), stack)) {
                worn.syncSlotToClients(slot);
                snapshots.put(slot, stack.copy());
            }
            return;
        }
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return new String[] { BaubleExpandedSlots.universalType };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addCheckedInformation(ItemStack stack, EntityPlayer player, List<String> lines, boolean advanced) {
        super.addCheckedInformation(stack, player, lines, advanced);
        if (GuiScreen.isCtrlKeyDown()) {
            lines.add(NameConst.i18n(NameConst.TT_WIRELESS_INSTALLED));
            if (hasInfinityRange(stack)) lines.add(
                "  " + EnumChatFormatting.GOLD
                    + WirelessTerminalQuantumBridgeRecipe.getQuantumBridgeCard()
                        .getDisplayName());
            if (hasInfinityPower(stack)) lines.add(
                "  " + EnumChatFormatting.GOLD
                    + WirelessTerminalEnergyRecipe.getEnergyCard()
                        .getDisplayName());
        } else {
            lines.add(NameConst.i18n(NameConst.TT_CTRL_FOR_MORE));
        }
        if (GuiScreen.isShiftKeyDown()) {
            lines.add(StatCollector.translateToLocal("gtnl.meminiterminal.tooltip.usage"));
            lines
                .add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("gtnl.meminiterminal.tooltip.inventory"));
            lines.add(StatCollector.translateToLocal("gtnl.meminiterminal.tooltip.full"));
        } else {
            lines.add(NameConst.i18n(NameConst.TT_SHIFT_FOR_MORE));
        }
    }
}
