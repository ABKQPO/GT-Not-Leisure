package com.science.gtnl.loader;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.common.block.blocks.tile.TileEntitySuperDualInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperInterface;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.common.part.PartSuperDualInterface;
import com.science.gtnl.common.part.PartSuperInterface;
import com.science.gtnl.common.render.model.MEChiselModel;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.CraftingUnitHandler;

import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.client.render.ItemRenderer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MELoader implements IContentLoader {

    @Override
    public String name() {
        return "me";
    }

    @Override
    public void init() {
        CraftingUnitHandler.register();
    }

    @Override
    public void postInit() {
        var interfaceTerminal = AEApi.instance()
            .registries()
            .interfaceTerminal();
        interfaceTerminal.register(SuperCraftingInputHatchME.class);
        interfaceTerminal.register(AssemblerMatrix.class);
        interfaceTerminal.register(PartSuperInterface.class);
        interfaceTerminal.register(TileEntitySuperInterface.class);
        interfaceTerminal.register(PartSuperDualInterface.class);
        interfaceTerminal.register(TileEntitySuperDualInterface.class);

        for (ItemStack superInterface : new ItemStack[] { GTNLItemList.PartSuperInterface.get(1),
            GTNLItemList.SuperInterface.get(1), GTNLItemList.PartSuperDualInterface.get(1),
            GTNLItemList.SuperDualInterface.get(1) }) {
            Upgrades.ADVANCED_BLOCKING.registerItem(superInterface, 1);
            Upgrades.CRAFTING.registerItem(superInterface, 1);
            Upgrades.FAKE_CRAFTING.registerItem(superInterface, 1);
            Upgrades.FUZZY.registerItem(superInterface, 1);
            Upgrades.LOCK_CRAFTING.registerItem(superInterface, 1);
        }

        Upgrades.CAPACITY.registerItem(GTNLItemList.PartActiveFormationPlane.get(1), 5);
        Upgrades.FUZZY.registerItem(GTNLItemList.PartActiveFormationPlane.get(1), 1);
        Upgrades.INVERTER.registerItem(GTNLItemList.PartActiveFormationPlane.get(1), 1);

        Upgrades.FUZZY.registerItem(GTNLItemList.PartUniversalStorageBus.get(1), 1);
        Upgrades.ORE_FILTER.registerItem(GTNLItemList.PartUniversalStorageBus.get(1), 1);
        Upgrades.INVERTER.registerItem(GTNLItemList.PartUniversalStorageBus.get(1), 1);
        Upgrades.CAPACITY.registerItem(GTNLItemList.PartUniversalStorageBus.get(1), 5);
        Upgrades.STICKY.registerItem(GTNLItemList.PartUniversalStorageBus.get(1), 1);

        for (ItemStack bus : new ItemStack[] { GTNLItemList.PartUniversalImportBus.get(1),
            GTNLItemList.PartUniversalExportBus.get(1) }) {
            Upgrades.FUZZY.registerItem(bus, 1);
            Upgrades.ORE_FILTER.registerItem(bus, 1);
            Upgrades.REDSTONE.registerItem(bus, 1);
            Upgrades.CAPACITY.registerItem(bus, 2);
            Upgrades.SPEED.registerItem(bus, 4);
            Upgrades.SUPERSPEED.registerItem(bus, 4);
            Upgrades.SUPERLUMINALSPEED.registerItem(bus, 4);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        MinecraftForge.EVENT_BUS.register(MEChiselModel.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(MEChiselModel.INSTANCE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientInit() {
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.direPatternEncoder), ItemRenderer.INSTANCE);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.superInterface), ItemRenderer.INSTANCE);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.superDualInterface), ItemRenderer.INSTANCE);
    }
}
