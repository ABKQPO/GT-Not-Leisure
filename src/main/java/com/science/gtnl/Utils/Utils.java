package com.science.gtnl.Utils;

import static com.science.gtnl.config.MainConfig.targetBlockSpecs;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.mojang.authlib.GameProfile;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.FMLCommonHandler;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.metatileentity.MetaTileEntity;

@SuppressWarnings("unused")
public final class Utils {

    public static final double LOG2 = Math.log(2);
    public static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);
    public static final BigInteger INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    public static final BigInteger BIG_INTEGER_100 = BigInteger.valueOf(100);
    // region about game

    public static boolean isClientSide() {
        return FMLCommonHandler.instance()
            .getSide()
            .isClient();
    }

    public static boolean isServerSide() {
        return FMLCommonHandler.instance()
            .getSide()
            .isServer();
    }

    /**
     * LV = 1, MAX = 14
     */
    public static int getCoilTier(HeatingCoilLevel coilLevel) {
        return coilLevel.getTier() + 1;
    }

    /**
     * One method to handle multi survivalBuildPiece at once.
     *
     * @param buildPieces All result of `survivalBuildPiece`.
     * @return If all result is -1, return -1. Otherwise, return the sum of all non-negative values.
     */
    public static int multiBuildPiece(int... buildPieces) {
        int out = 0x80000000;
        for (int v : buildPieces) {
            out &= (v & 0x80000000) | 0x7fffffff;
            if (v != -1) out += v;
        }
        return out < 0 ? -1 : out;
    }

    public static ItemStack addStringToStackName(ItemStack itemStack, String extra) {

        String originName = itemStack.getDisplayName();
        String newName = originName + " " + extra;
        itemStack.setStackDisplayName(newName);

        return itemStack;
    }

    // endregion

    // region about ItemStack
    public static boolean metaItemEqual(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a == b) return true;
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage();
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ItemStack newItemStack(Item aItem) {
        return new ItemStack(aItem, 1, 0);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ItemStack newItemStack(Block aBlock) {
        return new ItemStack(aBlock, 1, 0);
    }

    public static @NotNull ItemStack anErrorStack() {
        return newItemStack(Blocks.fire).setStackDisplayName(EnumChatFormatting.DARK_RED + "ERROR_STACK!!!");
    }

    public static ItemStack[] copyItemStackArray(ItemStack... array) {
        ItemStack[] result = new ItemStack[array.length];
        for (int i = 0; i < result.length; i++) {
            if (array[i] == null) continue;
            result[i] = array[i].copy();
        }
        return result;
    }

    public static ItemStack[] mergeItemStackArray(ItemStack[] array1, ItemStack[] array2) {
        if (array1 == null || array1.length < 1) {
            return array2;
        }
        if (array2 == null || array2.length < 1) {
            return array1;
        }
        ItemStack[] newArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);
        return newArray;
    }

    public static ItemStack[] mergeItemStackArrays(ItemStack[]... itemStacks) {
        return Arrays.stream(itemStacks)
            .filter(Objects::nonNull)
            .flatMap(Arrays::stream)
            .toArray(ItemStack[]::new);
    }

    public static <T> T[] mergeArray(T[] array1, T[] array2) {
        if (array1 == null || array1.length < 1) {
            return array2;
        }
        if (array2 == null || array2.length < 1) {
            return array1;
        }
        T[] newArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);
        return newArray;
    }

    public static <T> T[] mergeArrayss(/* @NotNull IntFunction<T[]> generator, */T[]... arrays) {
        IntFunction<T[]> generator = null;
        for (T[] array : arrays) {
            if (array == null) continue;
            generator = size -> (T[]) Array.newInstance(
                array.getClass()
                    .getComponentType(),
                size);
            break;
        }
        if (generator == null) return null;

        return Arrays.stream(arrays)
            .filter(a -> a != null && a.length > 0)
            .flatMap(Arrays::stream)
            .toArray(generator);
    }

    public static <T> T[] mergeArrays(T[]... arrays) {
        int totalLength = 0;
        T[] pattern = null;
        int indexFirstNotNull = -1;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i] == null || arrays[i].length < 1) continue;
            totalLength += arrays[i].length;
            if (pattern == null) {
                pattern = arrays[i];
                indexFirstNotNull = i;
            }
        }

        if (pattern == null) return null;

        T[] output = Arrays.copyOf(pattern, totalLength);
        int offset = pattern.length;
        for (int i = indexFirstNotNull; i < arrays.length; i++) {
            if (arrays[i] == null || arrays[i].length < 1) continue;
            if (arrays[i] != pattern) {
                System.arraycopy(arrays[i], 0, output, offset, arrays[i].length);
                offset += arrays[i].length;
            }
        }
        return output;
    }

    /**
     *
     * @param isa1 The ItemStack Array 1.
     * @param isa2 The ItemStack Array 2.
     * @return The elements of these two arrays are identical and in the same order.
     */
    public static boolean itemStackArrayEqualAbsolutely(ItemStack[] isa1, ItemStack[] isa2) {
        if (isa1.length != isa2.length) return false;
        for (int i = 0; i < isa1.length; i++) {
            if (!metaItemEqual(isa1[i], isa2[i])) return false;
            if (isa1[i].stackSize != isa2[i].stackSize) return false;
        }
        return true;
    }

    public static boolean itemStackArrayEqualFuzzy(ItemStack[] isa1, ItemStack[] isa2) {
        if (isa1.length != isa2.length) return false;
        for (ItemStack itemStack1 : isa1) {
            boolean flag = false;
            for (ItemStack itemStack2 : isa2) {
                if (metaItemEqual(itemStack1, itemStack2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }

    public static ItemStack copyAmount(int aAmount, ItemStack aStack) {
        if (isStackInvalid(aStack)) return null;
        ItemStack rStack = aStack.copy();
        // if (aAmount > 64) aAmount = 64;
        if (aAmount == -1) aAmount = 111;
        else if (aAmount < 0) aAmount = 0;
        rStack.stackSize = aAmount;
        return rStack;
    }

    public static boolean isStackValid(ItemStack aStack) {
        return (aStack != null) && aStack.getItem() != null && aStack.stackSize >= 0;
    }

    public static boolean isStackInvalid(ItemStack aStack) {
        return aStack == null || aStack.getItem() == null || aStack.stackSize < 0;
    }

    public static ItemStack setStackSize(ItemStack itemStack, int amount) {
        if (itemStack == null) return null;
        if (amount < 0) {
            ScienceNotLeisure.LOG
                .info("Error! Trying to set a item stack size lower than zero! " + itemStack + " to amount " + amount);
            return itemStack;
        }
        itemStack.stackSize = amount;
        return itemStack;
    }
    // endregion

    // region About FluidStack

    public static boolean fluidStackEqualAbsolutely(FluidStack[] fsa1, FluidStack[] fsa2) {
        if (fsa1.length != fsa2.length) return false;
        for (int i = 0; i < fsa1.length; i++) {
            if (!fluidEqual(fsa1[i], fsa2[i])) return false;
            if (fsa1[i].amount != fsa2[i].amount) return false;
        }
        return true;
    }

    public static boolean fluidStackEqualFuzzy(FluidStack[] fsa1, FluidStack[] fsa2) {
        if (fsa1.length != fsa2.length) return false;
        for (FluidStack fluidStack1 : fsa1) {
            boolean flag = false;
            for (FluidStack fluidStack2 : fsa2) {
                if (fluidEqual(fluidStack1, fluidStack2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }

    public static boolean fluidEqual(FluidStack a, FluidStack b) {
        return a.getFluid() == b.getFluid();
    }

    public static FluidStack setStackSize(FluidStack fluidStack, int amount) {
        if (fluidStack == null) return null;
        if (amount < 0) {
            ScienceNotLeisure.LOG
                .info("Error! Trying to set a item stack size lower than zero! " + fluidStack + " to amount " + amount);
            return fluidStack;
        }
        fluidStack.amount = amount;
        return fluidStack;
    }

    // endregion

    // region About Text
    public static String i18n(String key) {
        return translateToLocalFormatted(key);
    }

    // endregion

    // region Rewrites

    public static <T extends Collection<?>> T filterValidMTE(T metaTileEntities) {
        metaTileEntities.removeIf(o -> {
            if (o == null) {
                return true;
            }
            if (o instanceof MetaTileEntity mte) {
                return !mte.isValid();
            }
            return false;
        });
        return metaTileEntities;
    }

    // endregion

    // region Generals

    public static <T> T withNull(T main, T instead) {
        return null == main ? instead : main;
    }

    public static int safeInt(long number, int margin) {
        return number > Integer.MAX_VALUE - margin ? Integer.MAX_VALUE - margin : (int) number;
    }

    public static ItemStack[] sortNoNullArray(ItemStack... itemStacks) {
        if (itemStacks == null) return null;
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null) continue;
            list.add(itemStack);
        }
        if (list.isEmpty()) return new ItemStack[0];
        return list.toArray(new ItemStack[0]);
    }

    public static FluidStack[] sortNoNullArray(FluidStack... fluidStacks) {
        if (fluidStacks == null) return null;
        List<FluidStack> list = new ArrayList<>();
        for (FluidStack fluidStack : fluidStacks) {
            if (fluidStack == null) continue;
            list.add(fluidStack);
        }
        if (list.isEmpty()) return new FluidStack[0];
        return list.toArray(new FluidStack[0]);
    }

    public static Object[] sortNoNullArray(Object... objects) {
        if (objects == null) return null;
        List<Object> list = new ArrayList<>();
        for (Object object : objects) {
            if (object == null) continue;
            list.add(object);
        }
        if (list.isEmpty()) return new Object[0];
        return list.toArray(new Object[0]);
    }

    public static <T extends Collection<E>, E extends MetaTileEntity> T filterValidMTEs(T metaTileEntities) {
        metaTileEntities.removeIf(mte -> mte == null || !mte.isValid());
        return metaTileEntities;
    }

    public static int min(int... values) {
        Arrays.sort(values);
        return values[0];
    }

    public static int max(int... values) {
        Arrays.sort(values);
        return values[values.length - 1];
    }

    public static long min(long... values) {
        Arrays.sort(values);
        return values[0];
    }

    public static long max(long... values) {
        Arrays.sort(values);
        return values[values.length - 1];
    }

    public static double calculatePowerTier(double voltage) {
        return 1 + Math.max(0, (Math.log(voltage) / LOG2) - 5) / 2;
    }

    public static String repeatExclamation(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("!");
        }
        return sb.toString();
    }

    public static boolean checkSenderPermission(ICommandSender sender, int requiredLevel) {
        if (requiredLevel == 0) return true;
        if (sender instanceof RConConsoleSource || sender instanceof MinecraftServer) {
            return true;
        }

        if (sender instanceof CommandBlockLogic) {
            return requiredLevel < 4;
        }

        if (sender instanceof EntityPlayerMP player) {
            UserListOps userList = MinecraftServer.getServer()
                .getConfigurationManager()
                .func_152603_m();
            GameProfile profile = player.getGameProfile();
            UserListOpsEntry entry = (UserListOpsEntry) userList.func_152683_b(profile);
            if (entry != null) {
                return requiredLevel <= entry.func_152644_a();
            }
        }

        return false;
    }

    public static boolean isTargetBlock(Block block, int meta) {
        if (targetBlockSpecs == null) return false;
        if (block == null) {
            return false;
        }
        String blockId = Block.blockRegistry.getNameForObject(block);
        if (blockId == null) {
            return false;
        }

        for (String spec : targetBlockSpecs) {
            String[] parts = spec.split(":");
            if (parts.length == 2) {
                if (spec.equals(blockId)) {
                    return true;
                }
            } else if (parts.length == 3) {
                String idPart = parts[0] + ":" + parts[1];
                int desiredMeta;
                try {
                    desiredMeta = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid meta value in config: " + spec);
                    continue;
                }

                if (idPart.equals(blockId) && meta == desiredMeta) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class TargetInfo {

        public double x, y, z;
        public double distance;
        public EntityLivingBase entityTarget;

        public TargetInfo(double x, double y, double z, double distance) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.distance = distance;
            this.entityTarget = null;
        }

        public TargetInfo(EntityLivingBase entity, double distance) {
            this.entityTarget = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
            this.distance = distance;
        }

        public boolean isEntityTarget() {
            return entityTarget != null;
        }
    }
}
