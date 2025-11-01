package com.science.gtnl.common.item.steamRocket;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.dreammaster.gthandler.CustomItemList;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.recipe.gtnl.RocketAssemblerRecipes;

import galaxyspace.core.inventory.InventorySchematic;
import galaxyspace.core.inventory.container.rocket.ContainerSchematic;
import galaxyspace.core.inventory.slot.SlotSchematic;
import galaxyspace.core.inventory.slot.SlotSchematicChest;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult;

public class ContainerSchematicSteamRocket extends ContainerSchematic {

    public ContainerSchematicSteamRocket(InventoryPlayer inventory, int x, int y, int z) {
        super(new InventorySchematic(22), inventory, x, y, z);

        // OUT
        this.addSlotToContainer(
            new SlotRocketBenchResult(inventory.player, this.craftMatrix, this.craftResult, 0, 134, 73));

        // GEAR
        // Lander
        this.addSlotToContainer(
            new SlotSchematic(
                this.craftMatrix,
                1,
                134,
                10,
                x,
                y,
                z,
                inventory.player,
                GTModHandler.getModItem(Mods.StevesCarts2.ID, "CartModule", 1, 38)
                    .getItem(),
                38));
        // Control Computer
        this.addSlotToContainer(
            new SlotSchematic(
                this.craftMatrix,
                2,
                134,
                28,
                x,
                y,
                z,
                inventory.player,
                CustomItemList.EngineCore.getItem()));
        // Fuel Canisters
        this.addSlotToContainer(
            new SlotSchematic(
                this.craftMatrix,
                3,
                116,
                19,
                x,
                y,
                z,
                inventory.player,
                GTModHandler.getModItem(Mods.IronTanks.ID, "diamondTank", 1)
                    .getItem()));
        this.addSlotToContainer(
            new SlotSchematic(
                this.craftMatrix,
                4,
                152,
                19,
                x,
                y,
                z,
                inventory.player,
                GTModHandler.getModItem(Mods.IronTanks.ID, "diamondTank", 1)
                    .getItem()));
        // Fuel Canisters (unused)
        this.addSlotToContainer(new SlotSchematic(this.craftMatrix, 5, 116, 37, x, y, z, inventory.player, null, -1));
        this.addSlotToContainer(new SlotSchematic(this.craftMatrix, 6, 152, 37, x, y, z, inventory.player, null, -1));
        // Chest
        this.addSlotToContainer(new SlotSchematicChest(this.craftMatrix, 21, 134, 46, x, y, z, inventory.player));

        // ROCKET
        // nose cone
        this.addSlotToContainer(
            new SlotSchematic(
                this.craftMatrix,
                7,
                53,
                19,
                x,
                y,
                z,
                inventory.player,
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.BlackSteel, 1)
                    .getItem(),
                6000 + Materials.BlackSteel.mMetaItemSubID));
        // body
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlotToContainer(
                    new SlotSchematic(
                        this.craftMatrix,
                        8 + i * 2 + j,
                        44 + j * 18,
                        37 + i * 18,
                        x,
                        y,
                        z,
                        inventory.player,
                        MaterialPool.CompressedSteam.get(OrePrefixes.plateSuperdense, 1)
                            .getItem(),
                        MaterialPool.CompressedSteam.getmID()));
            }
        }
        // engine
        this.addSlotToContainer(
            new SlotSchematic(
                this.craftMatrix,
                16,
                53,
                109,
                x,
                y,
                z,
                inventory.player,
                GTModHandler.getModItem(Mods.GraviSuite.ID, "itemSimpleItem", 1, 6)
                    .getItem(),
                6));
        // fins
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlotToContainer(
                    new SlotSchematic(
                        this.craftMatrix,
                        17 + i * 2 + j,
                        26 + j * 54,
                        91 + i * 18,
                        x,
                        y,
                        z,
                        inventory.player,
                        GTModHandler.getModItem(Mods.Railcraft.ID, "machine.beta", 1, 7)
                            .getItem(),
                        7));
            }
        }

        // PLAYER INV
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 196));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 138 + i * 18));
            }
        }
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public ItemStack findMatchingRecipe() {
        return RocketAssemblerRecipes.findMatchingSpaceshipSteamRecipe(this.craftMatrix);
    }
}
