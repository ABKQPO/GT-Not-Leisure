package com.lootgames.sudoku.block;

import static ru.timeconqueror.lootgames.registry.LGBlocks.*;

import java.util.function.Supplier;

import cpw.mods.fml.common.registry.GameRegistry;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.GameMasterBlock;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;

public class SudokuBlocks {

    public static final GameMasterBlock SDK_MASTER = gameMaster(SudokuTile::new);

    public static final SudokuActivatorBlock SDK_ACTIVATOR = new SudokuActivatorBlock();

    public static void register() {

        regBlock(SDK_ACTIVATOR.setCreativeTab(LootGames.CREATIVE_TAB), "sdk_activator");
        regBlock(SDK_MASTER, "sdk_master");
        GameRegistry.registerTileEntity(SudokuTile.class, "sdk_master");
    }

    private static GameMasterBlock gameMaster(Supplier<GameMasterTile<?>> tileEntityFactory) {
        return new GameMasterBlock((blockState, world) -> tileEntityFactory.get());
    }
}
