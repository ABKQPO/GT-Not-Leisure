package com.science.gtnl.api.stellar;

import java.util.UUID;

import net.minecraft.world.World;

import lombok.Getter;

@Getter
public class StellarIrisUpgradeEffectContext {

    private final World world;
    private final UUID teamLeaderId;
    private final StellarIrisUpgradeDefinition definition;

    public StellarIrisUpgradeEffectContext(World world, UUID teamLeaderId, StellarIrisUpgradeDefinition definition) {
        this.world = world;
        this.teamLeaderId = teamLeaderId;
        this.definition = definition;
    }

    public String getUpgradeId() {
        return definition.getId();
    }
}
