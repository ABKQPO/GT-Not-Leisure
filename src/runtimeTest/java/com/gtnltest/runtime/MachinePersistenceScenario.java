package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import java.util.Objects;
import java.util.UUID;

import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.utils.enums.GTNLMachineID;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/** Registration, placement and serialization coverage. This does not claim recipe production coverage. */
final class MachinePersistenceScenario implements RuntimeScenario {

    private final GTNLMachineID type;
    private final int variant;
    private IGregTechTileEntity tile;
    private Class<?> machineClass;
    private UUID owner;
    private ForgeDirection facing;

    int metaId() {
        return type.ID + variant;
    }

    MachinePersistenceScenario(GTNLMachineID type) {
        this(type, 0);
    }

    MachinePersistenceScenario(GTNLMachineID type, int variant) {
        this.type = type;
        this.variant = variant;
    }

    public String id() {
        return "machine." + type.name()
            .toLowerCase(java.util.Locale.ROOT) + (variant == 0 ? "" : ".variant_" + variant) + ".persistence";
    }

    public String scope() {
        return "registry-placement-nbt";
    }

    public void start(RuntimeFixture fixture) {
        tile = fixture.machine(type.ID + variant, 0);
    }

    public boolean tick(RuntimeFixture fixture, int ticks) {
        if (ticks == 2) {
            machineClass = tile.getMetaTileEntity()
                .getClass();
            owner = tile.getOwnerUuid();
            facing = tile.getFrontFacing();
            tile = fixture.reload(tile);
        }
        if (ticks < 4) return false;
        require(tile.getMetaTileID() == type.ID + variant, "machine ID changed across NBT reload");
        require(
            tile.getMetaTileEntity() != null && tile.getMetaTileEntity()
                .getClass() == machineClass,
            "machine class changed across NBT reload");
        require(Objects.equals(owner, tile.getOwnerUuid()), "machine owner changed across NBT reload");
        require(facing == tile.getFrontFacing(), "machine orientation changed across NBT reload");
        return true;
    }
}
