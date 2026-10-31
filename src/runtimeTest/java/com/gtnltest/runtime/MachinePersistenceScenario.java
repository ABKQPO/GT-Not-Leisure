package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import java.util.Objects;
import java.util.UUID;

import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/** Registration, placement and serialization coverage. This does not claim recipe production coverage. */
final class MachinePersistenceScenario implements RuntimeScenario {

    private final MachineCatalogEntry machine;
    private IGregTechTileEntity tile;
    private Class<?> machineClass;
    private UUID owner;
    private ForgeDirection facing;
    private boolean reloaded;

    MachinePersistenceScenario(MachineCatalogEntry machine) {
        this.machine = machine;
    }

    public String id() {
        return machine.persistenceId();
    }

    public String skipReason() {
        return machine.skipReason();
    }

    public String scope() {
        return "registry-placement-nbt";
    }

    public void start(RuntimeFixture fixture) {
        tile = fixture.machine(machine.metaId(), 0);
    }

    public boolean tick(RuntimeFixture fixture, int ticks) {
        if (tile.getTimer() <= 5) return false;
        if (!reloaded) {
            machineClass = tile.getMetaTileEntity()
                .getClass();
            owner = tile.getOwnerUuid();
            facing = tile.getFrontFacing();
            tile = fixture.reload(tile);
            reloaded = true;
            return false;
        }
        require(tile.getMetaTileID() == machine.metaId(), "machine ID changed across NBT reload");
        require(
            tile.getMetaTileEntity() != null && tile.getMetaTileEntity()
                .getClass() == machineClass,
            "machine class changed across NBT reload");
        require(Objects.equals(owner, tile.getOwnerUuid()), "machine owner changed across NBT reload");
        require(facing == tile.getFrontFacing(), "machine orientation changed across NBT reload");
        return true;
    }
}
