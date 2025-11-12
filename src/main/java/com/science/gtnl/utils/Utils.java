package com.science.gtnl.utils;

import static com.science.gtnl.config.MainConfig.targetBlockSpecs;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.mojang.authlib.GameProfile;
import com.science.gtnl.utils.machine.FluidTankG;
import com.science.gtnl.utils.machine.ItemStackG;

import cpw.mods.fml.common.FMLCommonHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

@SuppressWarnings("unused")
public class Utils {

    public static final double LOG2 = Math.log(2);
    public static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);
    public static final BigInteger INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    public static final BigInteger BIG_INTEGER_100 = BigInteger.valueOf(100);
    public static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    public static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    public static final String ZERO_STRING = "0";

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

    public static boolean isClientThreaded() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient();
    }

    public static <C extends Collection<E>, E extends MetaTileEntity, T extends E> List<T> filterValidMTEs(
        C metaTileEntities, Class<T> targetClass) {
        List<T> result = new ArrayList<>();
        for (E mte : metaTileEntities) {
            if (mte != null && mte.isValid() && targetClass.isInstance(mte)) {
                result.add(targetClass.cast(mte));
            }
        }
        return result;
    }

    public static long toLongSafe(BigInteger value) {
        if (value == null) return 0L;
        if (value.compareTo(LONG_MAX) > 0) {
            return Long.MAX_VALUE;
        }
        if (value.compareTo(LONG_MIN) < 0) {
            return Long.MIN_VALUE;
        }
        return value.longValue();
    }

    public static boolean addStacksToList(@NotNull Collection<ItemStack> list, @NotNull ItemStack itemStack,
        long amount) {
        if (amount < 0L) {
            throw new IllegalArgumentException("Code is trying to set item stack size a negative number.");
        } else if (amount <= 2147483647L) {
            return list.add(GTUtility.copyAmountUnsafe((int) amount, itemStack));
        } else {
            long toAdd;
            for (toAdd = amount; toAdd > 2147483647L; toAdd -= 2147483647L) {
                list.add(GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, itemStack));
            }

            return list.add(GTUtility.copyAmountUnsafe((int) toAdd, itemStack));
        }
    }

    public static boolean addStacksToList(@NotNull Collection<FluidStack> list, @NotNull FluidStack fluidStackStack,
        long amount) {
        if (amount < 0L) {
            throw new IllegalArgumentException("Code is trying to set item stack size a negative number.");
        } else if (amount <= 2147483647L) {
            return list.add(GTUtility.copyAmount((int) amount, fluidStackStack));
        } else {
            long toAdd;
            for (toAdd = amount; toAdd > 2147483647L; toAdd -= 2147483647L) {
                list.add(GTUtility.copyAmount(Integer.MAX_VALUE, fluidStackStack));
            }

            return list.add(GTUtility.copyAmount((int) toAdd, fluidStackStack));
        }
    }

    public static boolean areItemsValid(ItemStack... stacks) {
        return stacks != null && stacks.length >= 1
            && Arrays.stream(stacks)
                .allMatch(GTUtility::isStackValid);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ItemStack newItemStack(Item aItem) {
        return new ItemStack(aItem, 1, 0);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ItemStack newItemStack(Block aBlock) {
        return new ItemStack(aBlock, 1, 0);
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

    @SafeVarargs
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

    public static List<ItemStack> mergeAndSplitStacks(List<ItemStack> stacks) {
        List<ItemStack> uniqueStacks = new ArrayList<>();
        Object2LongOpenHashMap<ItemStack> counts = new Object2LongOpenHashMap<>(stacks.size());

        for (ItemStack stack : stacks) {
            if (stack == null) continue;

            boolean merged = false;

            for (ItemStack existing : uniqueStacks) {
                if (GTUtility.areStacksEqual(stack, existing)) {
                    counts.put(existing, counts.getLong(existing) + stack.stackSize);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                ItemStack copy = stack.copy();
                uniqueStacks.add(copy);
                counts.put(copy, copy.stackSize);
            }
        }

        return uniqueStacks.stream()
            .flatMap(stack -> {
                long total = counts.getLong(stack);
                long chunks = (total + (long) Integer.MAX_VALUE - 1) / (long) Integer.MAX_VALUE;

                return LongStream.range(0, chunks)
                    .mapToObj(i -> {
                        ItemStack copy = stack.copy();
                        copy.stackSize = (int) Math.min(total - i * (long) Integer.MAX_VALUE, Integer.MAX_VALUE);
                        return copy;
                    });
            })
            .collect(Collectors.toList());
    }

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

    public static String ensureUUID(NBTTagCompound aNBT) {
        if (aNBT.hasKey("storeUUID")) {
            return aNBT.getString("storeUUID");
        } else {
            String uuid = UUID.randomUUID()
                .toString();
            aNBT.setString("storeUUID", uuid);
            return uuid;
        }
    }

    public static VargsFunction<ItemStack[], ItemStack[]> filterStack = (s) -> Arrays.stream(s)
        .flatMap(Arrays::stream)
        .filter(a -> a != null)
        .toArray(ItemStack[]::new);

    public interface VargsFunction<T, R> {

        R apply(T... t);
    }

    public static ItemStack[] flat(ItemStackG[] mStoredItemInternal2) {
        return Arrays.stream(mStoredItemInternal2)
            .filter(Objects::nonNull)
            .flatMap(s -> Arrays.stream(s.flat()))
            .toArray(ItemStack[]::new);

    }

    public static FluidStack[] flat(FluidTankG[] mStoredItemInternal2) {
        return Arrays.stream(mStoredItemInternal2)
            .flatMap(s -> Arrays.stream(s.flat()))
            .toArray(FluidStack[]::new);

    }

    public static MovingObjectPosition rayTraceBlock(EntityPlayer player, double reachDistance) {
        World world = player.getEntityWorld();

        float partialTicks = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        double posX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double posY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + 1.62D - player.yOffset;
        double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        Vec3 startVec = Vec3.createVectorHelper(posX, posY, posZ);

        float fYawRad = (float) Math.toRadians(-yaw) - (float) Math.PI;
        float fPitchRad = (float) Math.toRadians(-pitch);
        float cosPitch = -MathHelper.cos(fPitchRad);
        float sinPitch = MathHelper.sin(fPitchRad);
        float cosYaw = MathHelper.cos(fYawRad);
        float sinYaw = MathHelper.sin(fYawRad);

        double dirX = sinYaw * cosPitch;
        double dirY = sinPitch;
        double dirZ = cosYaw * cosPitch;

        Vec3 endVec = startVec.addVector(dirX * reachDistance, dirY * reachDistance, dirZ * reachDistance);

        return world.rayTraceBlocks(startVec, endVec, true);
    }

    public static MovingObjectPosition rayTrace(EntityPlayer p, boolean hitBlocks, boolean hitEntities,
        boolean hitEntityItem, double range) {
        final World w = p.getEntityWorld();

        final float f = 1.0F;
        float f1 = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * f;
        final float f2 = p.prevRotationYaw + (p.rotationYaw - p.prevRotationYaw) * f;
        final double d0 = p.prevPosX + (p.posX - p.prevPosX) * f;
        final double d1 = p.prevPosY + (p.posY - p.prevPosY) * f + 1.62D - p.yOffset;
        final double d2 = p.prevPosZ + (p.posZ - p.prevPosZ) * f;
        final Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        final float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        final float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        final float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        final float f6 = MathHelper.sin(-f1 * 0.017453292F);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;

        final Vec3 vec31 = vec3.addVector(f7 * range, f6 * range, f8 * range);

        final AxisAlignedBB bb = AxisAlignedBB
            .getBoundingBox(
                Math.min(vec3.xCoord, vec31.xCoord),
                Math.min(vec3.yCoord, vec31.yCoord),
                Math.min(vec3.zCoord, vec31.zCoord),
                Math.max(vec3.xCoord, vec31.xCoord),
                Math.max(vec3.yCoord, vec31.yCoord),
                Math.max(vec3.zCoord, vec31.zCoord))
            .expand(16, 16, 16);

        Entity entity = null;
        double closest = 9999999.0D;
        if (hitEntities) {
            final List<Entity> list = w.getEntitiesWithinAABBExcludingEntity(p, bb);

            for (Entity o : list) {

                if (!o.isDead && o != p && (hitEntityItem || !(o instanceof EntityItem))) {
                    if (o.isEntityAlive()) {
                        if (o.riddenByEntity == p) {
                            continue;
                        }

                        f1 = 0.3F;
                        final AxisAlignedBB boundingBox = o.boundingBox.expand(f1, f1, f1);
                        final MovingObjectPosition movingObjectPosition = boundingBox.calculateIntercept(vec3, vec31);

                        if (movingObjectPosition != null) {
                            final double nd = vec3.squareDistanceTo(movingObjectPosition.hitVec);

                            if (nd < closest) {
                                entity = o;
                                closest = nd;
                            }
                        }
                    }
                }
            }
        }

        MovingObjectPosition pos = null;
        Vec3 vec = null;

        if (hitBlocks) {
            vec = Vec3.createVectorHelper(d0, d1, d2);
            pos = w.rayTraceBlocks(vec3, vec31, true);
        }

        if (entity != null && pos != null && pos.hitVec.squareDistanceTo(vec) > closest) {
            pos = new MovingObjectPosition(entity);
        } else if (entity != null && pos == null) {
            pos = new MovingObjectPosition(entity);
        }

        return pos;
    }

    public static boolean hasPermission(ICommandSender sender, int permissionLevel) {
        if (sender instanceof CommandBlockLogic || sender instanceof MinecraftServer
            || sender instanceof RConConsoleSource) {
            return true;
        }
        if (sender instanceof EntityPlayer player) {
            MinecraftServer server = MinecraftServer.getServer();
            GameProfile profile = player.getGameProfile();

            if (server.getConfigurationManager()
                .func_152596_g(profile)) {
                UserListOpsEntry entry = (UserListOpsEntry) server.getConfigurationManager()
                    .func_152603_m()
                    .func_152683_b(profile);

                return entry != null ? entry.func_152644_a() >= permissionLevel
                    : server.getOpPermissionLevel() >= permissionLevel;
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

    public static void placeItemBackInInventory(EntityPlayer player, ItemStack stack) {
        if (stack == null || stack.stackSize == 0) return;

        if (!player.inventory.addItemStackToInventory(stack)) {
            player.func_146097_a(stack, false, false);
        } else if (stack.stackSize > 0) {
            player.func_146097_a(stack, false, false);
        }

        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        }
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
