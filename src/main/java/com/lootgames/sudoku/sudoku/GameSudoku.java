package com.lootgames.sudoku.sudoku;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.lootgames.sudoku.Sudoku;
import com.lootgames.sudoku.block.SudokuBlocks;
import com.lootgames.sudoku.config.ConfigSudoku;
import com.lootgames.sudoku.packet.SPSSyncBoard;
import com.lootgames.sudoku.packet.SPSSyncCell;
import com.lootgames.sudoku.packet.SPSudokuResetNumber;
import com.lootgames.sudoku.packet.SPSudokuSpawnLevelBeatParticles;

import lombok.Getter;
import lombok.Setter;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.api.util.RewardUtils;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.utils.MouseClickType;
import ru.timeconqueror.lootgames.utils.future.BlockPos;
import ru.timeconqueror.lootgames.utils.future.WorldExt;
import ru.timeconqueror.lootgames.utils.sanity.Sounds;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

public class GameSudoku extends BoardLootGame<GameSudoku> {

    public int currentLevel = 1; // 难度等级 1-4
    @Getter
    public final SudokuBoard board; // 数独棋盘对象
    @Getter
    @Setter
    public ConfigSudoku.ConfigSudokuSnapshot configSnapshot = null;
    public int ticks;

    public GameSudoku() {
        board = new SudokuBoard();
    }

    @Override
    public void onPlace() {
        setupInitialStage(new StageWaiting());
        if (isServerSide()) {
            // 从配置读取各级别挖空数量
            configSnapshot = Sudoku.SUDOKU.snapshot();
            // 生成第一关谜题
            int blanks = configSnapshot.getStage1()
                .blanksCount();
            board.generate(blanks);
        }
        super.onPlace();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (isClientSide()) {
            // 客户端暂用空快照，占位以便后续同步
            configSnapshot = ConfigSudoku.ConfigSudokuSnapshot.stub();
        }
    }

    @Override
    public int getCurrentBoardSize() {
        return SudokuBoard.SIZE;
    }

    @Override
    public int getAllocatedBoardSize() {
        return SudokuBoard.SIZE;
    }

    public static class Factory implements ILootGameFactory {

        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos) {
            BlockPos floorCenterPos = puzzleMasterPos.offset(0, -2, 0);
            WorldExt.setBlock(world, floorCenterPos, SudokuBlocks.SDK_ACTIVATOR);
        }
    }

    public void onLevelSuccessfullyFinished() {
        if (currentLevel < 4) {
            sendUpdatePacketToNearby(new SPSudokuSpawnLevelBeatParticles());
            sendToNearby(new ChatComponentTranslation("msg.com.lootgames.stage_complete"));
            WorldExt.playSoundServerly(getWorld(), getGameCenter(), Sounds.PLAYER_LEVELUP, 0.75F, 1.0F);

            currentLevel++;
            int blanks = configSnapshot.getStageByIndex(currentLevel)
                .blanksCount();
            board.generate(blanks);
            saveAndSync();
        } else {
            triggerGameWin();
        }
    }

    @Override
    public void triggerGameWin() {
        super.triggerGameWin();
        RewardUtils.spawnFourStagedReward(
            (WorldServer) getWorld(),
            this,
            getGameCenter(),
            currentLevel,
            LGConfigs.REWARDS.rewardsMinesweeper);
    }

    @Override
    public void triggerGameLose() {
        super.triggerGameLose();
        WorldExt.explode(
            getWorld(),
            null,
            getGameCenter().getX(),
            getGameCenter().getY() + 1.5,
            getGameCenter().getZ(),
            9,
            true);
    }

    @Override
    public void writeNBT(NBTTagCompound nbt, SerializationType type) {
        super.writeNBT(nbt, type);
        nbt.setTag("board", board.writeNBT());
        nbt.setInteger("current_level", currentLevel);
        nbt.setInteger("ticks", ticks);
        nbt.setTag("config_snapshot", ConfigSudoku.ConfigSudokuSnapshot.serialize(configSnapshot));
    }

    @Override
    public void readNBT(NBTTagCompound nbt, SerializationType type) {
        super.readNBT(nbt, type);
        board.readNBT(nbt.getCompoundTag("board"));
        currentLevel = nbt.getInteger("current_level");
        ticks = nbt.getInteger("ticks");
        configSnapshot = ConfigSudoku.ConfigSudokuSnapshot.deserialize(nbt.getCompoundTag("config_snapshot"));
    }

    @Override
    public void onStageUpdate(BoardStage oldStage, BoardStage newStage, boolean shouldDelayPacketSending) {
        ticks = 0;
        super.onStageUpdate(oldStage, newStage, shouldDelayPacketSending);
    }

    @Override
    public BoardStage createStageFromNBT(String id, NBTTagCompound tag, SerializationType type) {
        if (StageWaiting.ID.equals(id)) return new StageWaiting();
        throw new IllegalArgumentException("Unknown stage: " + id);
    }

    /**
     * 等待玩家输入阶段：单击生成/右键循环填数
     */
    public class StageWaiting extends BoardStage {

        public static final String ID = "waiting";

        @Override
        public void onClick(EntityPlayer player, Pos2i pos, MouseClickType type) {
            if (!isServerSide()) return;
            if (!board.isGenerated()) {
                int blanks = configSnapshot.getStageByIndex(currentLevel)
                    .blanksCount();
                board.generate(blanks);
                sendUpdatePacketToNearby(new SPSSyncBoard(GameSudoku.this, board));
                board.setLastClickTime(getWorld().getTotalWorldTime());
                return;
            }
            if (type == MouseClickType.LEFT) {
                board.cycleValueMinus(pos);
            } else if (type == MouseClickType.RIGHT) {
                board.cycleValueAdd(pos);
            }
            board.setLastClickTime(getWorld().getTotalWorldTime());
            sendUpdatePacketToNearby(new SPSSyncCell(pos, board.getPlayerValue(pos), getWorld().getTotalWorldTime()));
            save();
            if (board.checkWin()) {
                onLevelSuccessfullyFinished();
            }
        }

        @Override
        public void onTick() {
            if (isServerSide()) {
                if (board.getLastClickTime() > 0
                    && getWorld().getTotalWorldTime() - board.getLastClickTime() >= currentLevel * 1200L) {
                    if (currentLevel > 1) {
                        triggerGameWin();
                    } else {
                        triggerGameLose();
                    }
                    sendUpdatePacketToNearby(new SPSudokuResetNumber());
                    saveAndSync();
                }
            }
        }

        @Override
        public String getID() {
            return ID;
        }
    }
}
