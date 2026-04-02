package com.lootgames.sudoku;

import static com.lootgames.sudoku.Sudoku.*;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lootgames.sudoku.block.SudokuBlocks;
import com.lootgames.sudoku.packet.SPSSyncBoard;
import com.lootgames.sudoku.packet.SPSSyncCell;
import com.lootgames.sudoku.packet.SPSudokuResetNumber;
import com.lootgames.sudoku.packet.SPSudokuSpawnLevelBeatParticles;
import com.lootgames.sudoku.sudoku.GameSudoku;
import com.lootgames.sudoku.sudoku.SudokuOverlayHandler;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gregtech.api.enums.Mods;
import ru.timeconqueror.lootgames.api.LootGamesAPI;

@Deprecated
@Mod(modid = MODID, version = "0.0.1", name = MODNAME, acceptedMinecraftVersions = "1.7.10")
public class Sudoku {

    @Mod.Instance(ModList.ModIds.SUDOKU)
    public static Sudoku instance;
    public static final String MODID = ModList.ModIds.SUDOKU;
    public static final String MODNAME = "LootGamesSudoku";
    public static final String VERSION = "0.0.1";
    public static final String Arthor = "HFstudio";
    public static final String RESOURCE_ROOT_ID = ModList.ModIds.SUDOKU;
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.lootgames.sudoku.ClientProxy", serverSide = "com.lootgames.sudoku.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        if (Mods.LootGames.isModLoaded()) loadInit(event);
    }

    @Optional.Method(modid = "lootgames")
    public void loadInit(FMLInitializationEvent event) {
        proxy.init(event);

        LootGamesAPI.regServerPacket(SPSSyncCell.class);
        LootGamesAPI.regServerPacket(SPSSyncBoard.class);
        LootGamesAPI.regServerPacket(SPSudokuSpawnLevelBeatParticles.class);
        LootGamesAPI.regServerPacket(SPSudokuResetNumber.class);

        LootGamesAPI.registerGameGenerator(new GameSudoku.Factory());
    }

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (Mods.LootGames.isModLoaded()) loadPreInit(event);
    }

    @Optional.Method(modid = "lootgames")
    public void loadPreInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        SudokuBlocks.register();
        MinecraftForge.EVENT_BUS.register(new SudokuOverlayHandler());
    }
}
