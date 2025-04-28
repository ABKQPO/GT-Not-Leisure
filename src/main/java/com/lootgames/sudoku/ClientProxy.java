package com.lootgames.sudoku;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.lootgames.sudoku.block.SudokuTile;
import com.lootgames.sudoku.sudoku.SudokuRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ru.timeconqueror.timecore.api.util.Hacks;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.bindTileEntitySpecialRenderer(SudokuTile.class, Hacks.safeCast(new SudokuRenderer()));
    }

    public static EntityPlayer player() {
        return Hacks.safeCast(Minecraft.getMinecraft().thePlayer);
    }

    public static World world() {
        return Hacks.safeCast(Minecraft.getMinecraft().theWorld);
    }
}
