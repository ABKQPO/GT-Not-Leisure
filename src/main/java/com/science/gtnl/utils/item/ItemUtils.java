package com.science.gtnl.utils.item;

import static com.science.gtnl.utils.enums.ModList.Baubles;
import static gregtech.api.enums.Mods.AE2FluidCraft;
import static gregtech.api.enums.Mods.Botania;
import static gregtech.api.util.GTModHandler.getModItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.reavaritia.common.item.InfinityTotem;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.WirelessPickBlock;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;

import baubles.api.BaublesApi;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;

public class ItemUtils {

    public static final Random rand = new Random();

    public static final UITexture PICTURE_GTNL_LOGO = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/picture/logo");

    public static final UITexture PICTURE_GTNL_STEAM_LOGO = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/picture/steam_logo");

    public static Materials[] TIER = { Materials.LV, Materials.MV, Materials.HV, Materials.EV, Materials.IV,
        Materials.LuV, Materials.ZPM, Materials.UV, Materials.UHV, Materials.UEV, Materials.UIV, Materials.UMV,
        Materials.UXV, Materials.MAX };

    public static Materials[] TIER_MATERIAL = { Materials.Steel, // LV
        Materials.Aluminium, // MV
        Materials.StainlessSteel, // HV
        Materials.Titanium, // EV
        Materials.TungstenSteel, // IV
        Materials.Iridium, // LuV
        Materials.NaquadahAlloy, // ZPM
        Materials.Osmium, // UV
        Materials.Neutronium, // UHV
        Materials.Bedrockium, // UEV
        Materials.BlackPlutonium, // UIV
        MaterialsUEVplus.SpaceTime, // UMV
        MaterialsUEVplus.MagMatter, // UXV
        MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter // MAX
    };

    public static final ItemList[] ELECTRIC_MOTOR = { ItemList.Electric_Motor_LV, ItemList.Electric_Motor_MV,
        ItemList.Electric_Motor_HV, ItemList.Electric_Motor_EV, ItemList.Electric_Motor_IV, ItemList.Electric_Motor_LuV,
        ItemList.Electric_Motor_ZPM, ItemList.Electric_Motor_UV, ItemList.Electric_Motor_UHV,
        ItemList.Electric_Motor_UEV, ItemList.Electric_Motor_UIV, ItemList.Electric_Motor_UMV,
        ItemList.Electric_Motor_UXV, ItemList.Electric_Motor_MAX };

    public static final ItemList[] ELECTRIC_PUMP = { ItemList.Electric_Pump_LV, ItemList.Electric_Pump_MV,
        ItemList.Electric_Pump_HV, ItemList.Electric_Pump_EV, ItemList.Electric_Pump_IV, ItemList.Electric_Pump_LuV,
        ItemList.Electric_Pump_ZPM, ItemList.Electric_Pump_UV, ItemList.Electric_Pump_UHV, ItemList.Electric_Pump_UEV,
        ItemList.Electric_Pump_UIV, ItemList.Electric_Pump_UMV, ItemList.Electric_Pump_UXV,
        ItemList.Electric_Pump_MAX };

    public static final ItemList[] FLUID_REGULATOR = { ItemList.FluidRegulator_LV, ItemList.FluidRegulator_MV,
        ItemList.FluidRegulator_HV, ItemList.FluidRegulator_EV, ItemList.FluidRegulator_IV, ItemList.FluidRegulator_LuV,
        ItemList.FluidRegulator_ZPM, ItemList.FluidRegulator_UV, ItemList.FluidRegulator_UHV,
        ItemList.FluidRegulator_UEV, ItemList.FluidRegulator_UIV, ItemList.FluidRegulator_UMV,
        ItemList.FluidRegulator_UXV, ItemList.FluidRegulator_MAX };

    public static final ItemList[] CONVEYOR_MODULE = { ItemList.Conveyor_Module_LV, ItemList.Conveyor_Module_MV,
        ItemList.Conveyor_Module_HV, ItemList.Conveyor_Module_EV, ItemList.Conveyor_Module_IV,
        ItemList.Conveyor_Module_LuV, ItemList.Conveyor_Module_ZPM, ItemList.Conveyor_Module_UV,
        ItemList.Conveyor_Module_UHV, ItemList.Conveyor_Module_UEV, ItemList.Conveyor_Module_UIV,
        ItemList.Conveyor_Module_UMV, ItemList.Conveyor_Module_UXV, ItemList.Conveyor_Module_MAX };

    public static final ItemList[] ELECTRIC_PISTON = { ItemList.Electric_Piston_LV, ItemList.Electric_Piston_MV,
        ItemList.Electric_Piston_HV, ItemList.Electric_Piston_EV, ItemList.Electric_Piston_IV,
        ItemList.Electric_Piston_LuV, ItemList.Electric_Piston_ZPM, ItemList.Electric_Piston_UV,
        ItemList.Electric_Piston_UHV, ItemList.Electric_Piston_UEV, ItemList.Electric_Piston_UIV,
        ItemList.Electric_Piston_UMV, ItemList.Electric_Piston_UXV, ItemList.Electric_Piston_MAX };

    public static final ItemList[] ROBOT_ARM = { ItemList.Robot_Arm_LV, ItemList.Robot_Arm_MV, ItemList.Robot_Arm_HV,
        ItemList.Robot_Arm_EV, ItemList.Robot_Arm_IV, ItemList.Robot_Arm_LuV, ItemList.Robot_Arm_ZPM,
        ItemList.Robot_Arm_UV, ItemList.Robot_Arm_UHV, ItemList.Robot_Arm_UEV, ItemList.Robot_Arm_UIV,
        ItemList.Robot_Arm_UMV, ItemList.Robot_Arm_UXV, ItemList.Robot_Arm_MAX };

    public static final ItemList[] EMITTER = { ItemList.Emitter_LV, ItemList.Emitter_MV, ItemList.Emitter_HV,
        ItemList.Emitter_EV, ItemList.Emitter_IV, ItemList.Emitter_LuV, ItemList.Emitter_ZPM, ItemList.Emitter_UV,
        ItemList.Emitter_UHV, ItemList.Emitter_UEV, ItemList.Emitter_UIV, ItemList.Emitter_UMV, ItemList.Emitter_UXV,
        ItemList.Emitter_MAX };

    public static final ItemList[] SENSOR = { ItemList.Sensor_LV, ItemList.Sensor_MV, ItemList.Sensor_HV,
        ItemList.Sensor_EV, ItemList.Sensor_IV, ItemList.Sensor_LuV, ItemList.Sensor_ZPM, ItemList.Sensor_UV,
        ItemList.Sensor_UHV, ItemList.Sensor_UEV, ItemList.Sensor_UIV, ItemList.Sensor_UMV, ItemList.Sensor_UXV,
        ItemList.Sensor_MAX };

    public static final ItemList[] FIELD_GENERATOR = { ItemList.Field_Generator_LV, ItemList.Field_Generator_MV,
        ItemList.Field_Generator_HV, ItemList.Field_Generator_EV, ItemList.Field_Generator_IV,
        ItemList.Field_Generator_LuV, ItemList.Field_Generator_ZPM, ItemList.Field_Generator_UV,
        ItemList.Field_Generator_UHV, ItemList.Field_Generator_UEV, ItemList.Field_Generator_UIV,
        ItemList.Field_Generator_UMV, ItemList.Field_Generator_UXV, ItemList.Field_Generator_MAX };

    public static final ItemList[] HULL = { ItemList.Hull_LV, ItemList.Hull_MV, ItemList.Hull_HV, ItemList.Hull_EV,
        ItemList.Hull_IV, ItemList.Hull_LuV, ItemList.Hull_ZPM, ItemList.Hull_UV, ItemList.Hull_MAX, ItemList.Hull_UEV,
        ItemList.Hull_UIV, ItemList.Hull_UMV, ItemList.Hull_UXV, ItemList.Hull_MAXV };

    public static final Materials[] CABLE = { Materials.Tin, // LV
        Materials.Copper, // MV
        Materials.Gold, // HV
        Materials.Aluminium, // EV
        Materials.Tungsten, // IV
        Materials.VanadiumGallium, // LuV
        Materials.Naquadah, // ZPM
        Materials.NaquadahAlloy, // UV
        Materials.Bedrockium, // UHV
        Materials.Draconium, // UEV
        Materials.NetherStar, // UIV
        Materials.Quantium, // UMV
        Materials.BlackPlutonium, // UXV
        Materials.DraconiumAwakened, // MAX
    };

    public static NBTTagCompound writeItemStackToNBT(ItemStack stack) {
        NBTTagCompound compound = new NBTTagCompound();

        stack.writeToNBT(compound);
        compound.setInteger("IntCount", stack.stackSize);

        return compound;
    }

    public static ItemStack readItemStackFromNBT(NBTTagCompound compound) {
        ItemStack stack = ItemStack.loadItemStackFromNBT(compound);

        if (stack == null) return null;

        if (compound.hasKey("IntCount")) stack.stackSize = compound.getInteger("IntCount");

        return stack;
    }

    public static void removeItemFromPlayer(EntityPlayer player, ItemStack stack) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack inventoryStack = player.inventory.getStackInSlot(i);
            if (inventoryStack != null && inventoryStack.getItem() instanceof InfinityTotem
                && inventoryStack == stack) {
                player.inventory.setInventorySlotContents(i, null);
                return;
            }
        }

        if (Baubles.isModLoaded()) {
            IInventory baublesInventory = BaublesApi.getBaubles(player);
            if (baublesInventory != null) {
                for (int i = 0; i < baublesInventory.getSizeInventory(); i++) {
                    ItemStack baubleStack = baublesInventory.getStackInSlot(i);
                    if (baubleStack != null && baubleStack.getItem() instanceof InfinityTotem && baubleStack == stack) {
                        baublesInventory.setInventorySlotContents(i, null);
                        return;
                    }
                }
            }
        }
    }

    public static Block getBlockFromItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBlock block) {
            return Block.getBlockFromItem(block);
        }
        return Blocks.air;
    }

    public static ItemStack getItemStack(ItemStack baseStack, String aNBTString, ItemStack aReplacement) {
        if (baseStack == null) return aReplacement;
        try {
            baseStack.stackTagCompound = (NBTTagCompound) JsonToNBT.func_150315_a(aNBTString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return baseStack;
    }

    public static ItemStack getItemStack(String aModID, String aItem, long aAmount, int aMeta, String aNBTString) {
        ItemStack s = getModItem(aModID, aItem, aAmount, aMeta);
        try {
            s.stackTagCompound = (NBTTagCompound) JsonToNBT.func_150315_a(aNBTString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    public static ItemStack getItemStack(String aModID, String aItem, long aAmount, int aMeta, String aNBTString,
        ItemStack aReplacement) {
        ItemStack itemStack = getModItem(aModID, aItem, aAmount, aMeta);
        if (itemStack == null) return aReplacement;
        try {
            itemStack.stackTagCompound = (NBTTagCompound) JsonToNBT.func_150315_a(aNBTString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return itemStack;
    }

    public static ItemStack getSpecialFlower(String typeName, int amount) {
        ItemStack stack = GTModHandler.getModItem(Botania.ID, "specialFlower", amount);
        if (stack == null) return null;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        tag.setString("type", typeName);
        return stack;
    }

    public static ItemStack getSpecialFlower(String typeName) {
        ItemStack stack = GTModHandler.getModItem(Botania.ID, "specialFlower", 1);
        if (stack == null) return null;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        tag.setString("type", typeName);
        return stack;
    }

    public static ItemStack getFluidPacket(FluidStack fluid, int amount) {
        if (fluid == null) return new ItemStack(Blocks.fire);
        ItemStack packet = GTModHandler.getModItem(AE2FluidCraft.ID, "fluid_packet", 1);
        NBTTagCompound tag = packet.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            packet.setTagCompound(tag);
        }

        NBTTagCompound fluidTag = new NBTTagCompound();
        String fluidName = fluid.getFluid()
            .getName();

        fluidTag.setString("FluidName", fluidName);
        fluidTag.setInteger("Amount", amount);

        tag.setTag("FluidStack", fluidTag);
        return packet;
    }

    public static ItemStack getIntegratedCircuitPlus(int config) {
        return GTNLItemList.CircuitIntegratedPlus.getWithDamage(0, config);
    }

    public static boolean setToolDamage(ItemStack aStack, long aDamage) {
        if (aStack == null) return false;

        NBTTagCompound tag = aStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            aStack.setTagCompound(tag);
        }

        NBTTagCompound toolStats;
        if (tag.hasKey("GT.ToolStats")) {
            toolStats = tag.getCompoundTag("GT.ToolStats");
        } else {
            toolStats = new NBTTagCompound();
            tag.setTag("GT.ToolStats", toolStats);
        }

        toolStats.setLong("Damage", aDamage);
        return true;
    }

    public static boolean setToolMaxDamage(ItemStack aStack, long aMaxDamage) {
        if (aStack == null) return false;

        NBTTagCompound tag = aStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            aStack.setTagCompound(tag);
        }

        NBTTagCompound toolStats;
        if (tag.hasKey("GT.ToolStats")) {
            toolStats = tag.getCompoundTag("GT.ToolStats");
        } else {
            toolStats = new NBTTagCompound();
            tag.setTag("GT.ToolStats", toolStats);
        }

        toolStats.setLong("MaxDamage", aMaxDamage);

        return true;
    }

    public static boolean canBeEnchanted(ItemStack stack) {
        return canBeEnchanted(stack, 30, false);
    }

    public static boolean canBeEnchanted(ItemStack stack, boolean allowExisting) {
        return canBeEnchanted(stack, 30, allowExisting);
    }

    public static boolean canBeEnchanted(ItemStack stack, int level, boolean allowExisting) {
        if (stack == null || level <= 0) return false;
        if (!allowExisting && stack.isItemEnchanted()) return false;

        List<EnchantmentData> enchantments = EnchantmentHelper.buildEnchantmentList(rand, stack, level);
        return enchantments != null && !enchantments.isEmpty();
    }

    public static ItemStack enchantItem(ItemStack stack, int level) {
        return enchantItem(stack, level, false);
    }

    public static ItemStack enchantItem(ItemStack stack, int level, boolean allowExisting) {
        if (stack == null || level <= 0) return stack;
        if (!allowExisting && stack.isItemEnchanted()) return stack;

        boolean isBook = stack.getItem() == Items.book;
        List<EnchantmentData> enchantments = EnchantmentHelper.buildEnchantmentList(rand, stack, level);

        if (enchantments == null || enchantments.isEmpty()) return stack;

        if (isBook) {
            stack.func_150996_a(Items.enchanted_book);
        }

        int skipIndex = isBook && enchantments.size() > 1 ? rand.nextInt(enchantments.size()) : -1;

        for (int i = 0; i < enchantments.size(); i++) {
            EnchantmentData data = enchantments.get(i);

            if (!isBook || i != skipIndex) {
                if (isBook) {
                    Items.enchanted_book.addEnchantment(stack, data);
                } else {
                    stack.addEnchantment(data.enchantmentobj, data.enchantmentLevel);
                }
            }
        }

        return stack;
    }

    public static ItemStack applyRandomEnchantments(ItemStack stack, int count, int minLevel, int maxLevel) {
        return applyRandomEnchantments(stack, count, minLevel, maxLevel, false);
    }

    public static ItemStack applyRandomEnchantments(ItemStack stack, int count, int minLevel, int maxLevel,
        boolean allowExisting) {
        if (stack == null || count <= 0) return stack;
        if (!allowExisting && stack.isItemEnchanted()) return stack;

        List<Enchantment> available = new ArrayList<>();
        for (Enchantment ench : Enchantment.enchantmentsList) {
            if (ench != null && ench.canApply(stack)) {
                available.add(ench);
            }
        }

        if (available.isEmpty()) return stack;

        Collections.shuffle(available, rand);

        int applyCount = Math.min(count, available.size());

        for (int i = 0; i < applyCount; i++) {
            Enchantment ench = available.get(i);
            int level = rand.nextInt(ench.getMaxLevel() - ench.getMinLevel() + 1) + ench.getMinLevel();
            level = Math.max(minLevel, level);
            level = Math.min(maxLevel, level);

            stack.addEnchantment(ench, level);
        }

        return stack;
    }

    public static ItemStack getEnchantedBook(Object... enchants) {
        ItemStack book = new ItemStack(Items.enchanted_book);
        for (int i = 0; i < enchants.length - 1; i += 2) {
            if (enchants[i] instanceof Enchantment && enchants[i + 1] instanceof Integer) {
                Items.enchanted_book
                    .addEnchantment(book, new EnchantmentData((Enchantment) enchants[i], (Integer) enchants[i + 1]));
            }
        }
        return book;
    }

    public static ItemStack getPlayerSkull(String playerName) {
        ItemStack skullStack = new ItemStack(Items.skull, 1, 3);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("SkullOwner", playerName);
        skullStack.setTagCompound(tag);

        return skullStack;
    }

    public static final boolean isBackHandIns = Mods.Backhand.isModLoaded();

    public static boolean placeItemInHotbar(EntityPlayer player, ItemStack result, boolean isCreative, boolean useAE) {
        if (result == null) return false;
        Minecraft mc = Minecraft.getMinecraft();
        InventoryPlayer inv = player.inventory;
        int backHandSlot = isBackHandIns ? 1 : 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result)) {
                inv.currentItem = i;
                return true;
            }
        }

        int foundSlot = -1;
        for (int i = 9; i < inv.mainInventory.length + backHandSlot; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result)) {
                foundSlot = i;
                break;
            }
        }

        if (foundSlot != -1) {
            int emptyHotbar = -1;
            if (inv.getStackInSlot(inv.currentItem) == null) {
                emptyHotbar = inv.currentItem;
            } else {
                for (int i = 0; i < 9; i++) {
                    if (inv.getStackInSlot(i) == null) {
                        emptyHotbar = i;
                        break;
                    }
                }
            }

            if (emptyHotbar != -1) {
                int windowId = player.inventoryContainer.windowId;
                mc.playerController.windowClick(windowId, foundSlot, 0, 0, player);
                mc.playerController.windowClick(windowId, emptyHotbar + 36, 0, 0, player);
                inv.currentItem = emptyHotbar;
            } else {
                int slot = inv.currentItem;
                int windowId = player.inventoryContainer.windowId;
                mc.playerController.windowClick(windowId, foundSlot, 0, 0, player);
                mc.playerController.windowClick(windowId, slot + 36, 0, 0, player);
                inv.currentItem = slot;

                int emptySlot = inv.getFirstEmptyStack();
                if (emptySlot != -1) {
                    mc.playerController.windowClick(windowId, emptySlot, 0, 0, player);
                }
            }
            return true;
        } else if (useAE && !isCreative) {
            int emptyHotbar = -1;
            if (inv.getStackInSlot(inv.currentItem) == null) {
                emptyHotbar = inv.currentItem;
            } else {
                for (int i = 0; i < 9; i++) {
                    if (inv.getStackInSlot(i) == null) {
                        emptyHotbar = i;
                        break;
                    }
                }
            }
            if (player.isSneaking()) {
                result.stackSize = 1;
            } else {
                result.stackSize = result.getMaxStackSize();
            }

            if (emptyHotbar != -1) {
                inv.currentItem = emptyHotbar;
                ScienceNotLeisure.network.sendToServer(new WirelessPickBlock(result, inv.currentItem));
                return true;
            } else {
                int slot = inv.currentItem;
                int windowId = player.inventoryContainer.windowId;
                mc.playerController.windowClick(windowId, slot + 36, 0, 0, player);
                int emptySlot = inv.getFirstEmptyStack();
                if (emptySlot != -1) {
                    mc.playerController.windowClick(windowId, emptySlot, 0, 0, player);
                }
                ScienceNotLeisure.network.sendToServer(new WirelessPickBlock(result, inv.currentItem));
                return true;
            }
        }

        if (isCreative) {
            int emptyHotbar = -1;
            if (inv.getStackInSlot(inv.currentItem) == null) {
                emptyHotbar = inv.currentItem;
            } else {
                for (int i = 0; i < 9; i++) {
                    if (inv.getStackInSlot(i) == null) {
                        emptyHotbar = i;
                        break;
                    }
                }
            }
            if (emptyHotbar != -1) {
                int slotId = player.inventoryContainer.inventorySlots.size() - 9 + emptyHotbar;
                inv.setInventorySlotContents(emptyHotbar, result);
                inv.currentItem = emptyHotbar;
                mc.playerController.sendSlotPacket(result, slotId - backHandSlot);
                return true;
            } else {
                int slot = inv.currentItem;
                int slotIdHotbar = player.inventoryContainer.inventorySlots.size() - 9 + slot;
                inv.setInventorySlotContents(slot, result);
                mc.playerController.sendSlotPacket(result, slotIdHotbar - backHandSlot);
                return true;
            }
        }

        int emptyHotbar = -1;
        if (inv.getStackInSlot(inv.currentItem) == null) {
            emptyHotbar = inv.currentItem;
        } else {
            for (int i = 0; i < 9; i++) {
                if (inv.getStackInSlot(i) == null) {
                    emptyHotbar = i;
                    break;
                }
            }
        }

        if (emptyHotbar != -1) {
            int windowId = player.inventoryContainer.windowId;
            inv.setInventorySlotContents(emptyHotbar, result);
            inv.currentItem = emptyHotbar;
            mc.playerController.windowClick(windowId, emptyHotbar + 36, 0, 0, player);
            return true;
        } else {
            int slot = inv.currentItem;
            int windowId = player.inventoryContainer.windowId;
            mc.playerController.windowClick(windowId, slot + 36, 0, 0, player);
            mc.playerController.windowClick(windowId, slot + 36, 0, 0, player);

            int emptySlot = inv.getFirstEmptyStack();
            if (emptySlot != -1) {
                mc.playerController.windowClick(windowId, emptySlot, 0, 0, player);
            }
            return true;
        }
    }

}
