package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.mojang.authlib.GameProfile;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/** Only used in the runner-created GTNLTestWorld; all placed blocks are tracked for cleanup. */
public final class RuntimeFixture implements AutoCloseable {

    public static final int X = 128, Y = 96, Z = 128;
    public final WorldServer world;
    public net.minecraft.entity.player.EntityPlayer actor;
    private final List<int[]> positions = new ObjectArrayList<>();

    public RuntimeFixture(WorldServer world) {
        require(
            "GTNLTestWorld".equals(
                world.getWorldInfo()
                    .getWorldName()),
            "runtime fixtures require the isolated test world");
        this.world = world;
        actor = FakePlayerFactory.get(world, new GameProfile(new UUID(0, 663), "[GTNLTests]"));
        actor.setPosition(X, Y, Z + 5);
        actor.rotationYaw = 180;
        world.getGameRules()
            .setOrCreateGameRule("doMobSpawning", "false");
        world.getGameRules()
            .setOrCreateGameRule("doDaylightCycle", "false");
    }

    public IGregTechTileEntity machine(int id, int offset) {
        return machineAt(id, X + offset, Y, Z);
    }

    public IGregTechTileEntity machineAt(int id, int x, int y, int z) {
        require(GregTechAPI.METATILEENTITIES[id] != null, "unregistered machine " + id);
        ItemStack stack = GregTechAPI.METATILEENTITIES[id].getStackForm(1);
        require(stack != null && stack.getItem() instanceof ItemBlock, "machine has no placeable item: " + id);
        positions.add(new int[] { x, y, z });
        world.setBlockToAir(x, y, z);
        require(
            ((ItemBlock) stack.getItem())
                .placeBlockAt(stack, actor, world, x, y, z, 1, 0.5F, 0.5F, 0.5F, stack.getItemDamage()),
            "machine placement failed: " + id);
        TileEntity tile = world.getTileEntity(x, y, z);
        require(tile instanceof IGregTechTileEntity, "placement did not produce a GregTech tile: " + id);
        IGregTechTileEntity result = (IGregTechTileEntity) tile;
        require(
            result.getMetaTileID() == id && result.getMetaTileEntity() != null,
            "placed machine has wrong registry ID: " + id);
        return result;
    }

    public void trackStructure(int centerX, int centerY, int centerZ, int radius) {
        for (int x = centerX - radius; x <= centerX + radius; x++)
            for (int y = centerY - radius; y <= centerY + radius; y++)
                for (int z = centerZ - radius; z <= centerZ + radius; z++)
                    if (!world.isAirBlock(x, y, z)) positions.add(new int[] { x, y, z });
    }

    public IGregTechTileEntity reload(IGregTechTileEntity original) {
        TileEntity tile = (TileEntity) original;
        NBTTagCompound data = new NBTTagCompound();
        tile.writeToNBT(data);
        tile.onChunkUnload();
        world.removeTileEntity(tile.xCoord, tile.yCoord, tile.zCoord);
        TileEntity restored = TileEntity.createAndLoadEntity(data);
        require(restored instanceof IGregTechTileEntity, "saved tile must resolve through Minecraft's tile registry");
        world.setTileEntity(tile.xCoord, tile.yCoord, tile.zCoord, restored);
        return (IGregTechTileEntity) restored;
    }

    @Override
    public void close() {
        for (int[] position : positions) world.setBlockToAir(position[0], position[1], position[2]);
        positions.clear();
    }
}
