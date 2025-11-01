package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularUIContext;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.kentington.thaumichorizons.common.lib.potion.PotionVisRegen;
import com.science.gtnl.utils.enums.ModList;

import gregtech.api.enums.SoundResource;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import thaumcraft.common.config.Config;
import vazkii.botania.common.brew.ModPotions;

public class SteamBeaconModule extends SteamElevatorModule {

    private boolean hasSpeedEffect; // 速度效果
    private boolean hasStrengthEffect; // 力量效果
    private boolean hasJumpBoostEffect; // 跳跃提升效果
    private boolean hasResistanceEffect; // 抗性效果
    private boolean hasHealthRegenerationEffect; // 生命恢复
    private boolean hasNightVisionEffect; // 夜视
    private boolean hasHasteEffect; // 急迫
    private boolean hasFireResistanceEffect; // 防火
    private boolean hasWaterBreathingEffect; // 水下呼吸
    private boolean hasWarpWardEffect; // 扭曲守护
    private boolean hasFeatherFeetEffect; // 羽翼
    private boolean hasVisRegenEffect; // 灵气创生

    private boolean enableSpeedEffect; // 速度效果
    private boolean enableStrengthEffect; // 力量效果
    private boolean enableJumpBoostEffect; // 跳跃提升效果
    private boolean enableResistanceEffect; // 抗性效果
    private boolean enableHealthRegenerationEffect; // 生命恢复
    private boolean enableNightVisionEffect; // 夜视
    private boolean enableHasteEffect; // 急迫
    private boolean enableFireResistanceEffect; // 防火
    private boolean enableWaterBreathingEffect; // 水下呼吸
    private boolean enableFeatherFeetEffect; // 羽翼
    private boolean enableVisRegenEffect; // 灵气创生
    private boolean enableWarpWardEffect; // 扭曲守护
    private int activeEffectsCount = 0;
    private int machineEffectsCount = 0;
    private boolean canWork;
    private int runningTickCounter = 0;
    private final ItemStackHandler inputSlotHandler = new ItemStackHandler(1);
    private ItemStack storedWindowItems;

    public static final int CONFIG_WINDOW_ID = 12;

    public SteamBeaconModule(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public SteamBeaconModule(String aName, int aTier) {
        super(aName, aTier);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamBeaconModule(this.mName, this.mTier);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        NBTTagCompound windowStorageNBTTag = new NBTTagCompound();
        int storageIndex = 0;
        ItemStack itemStack = inputSlotHandler.getStackInSlot(0);
        if (itemStack != null) {
            windowStorageNBTTag.setInteger(storageIndex + "stacksizeOfStoredItems", itemStack.stackSize);
            aNBT.setTag(storageIndex + "storedItem", itemStack.writeToNBT(new NBTTagCompound()));
        }
        aNBT.setTag("windowStorage", windowStorageNBTTag);
        aNBT.setBoolean("enableSpeedEffect", enableSpeedEffect);
        aNBT.setBoolean("enableStrengthEffect", enableStrengthEffect);
        aNBT.setBoolean("enableJumpBoostEffect", enableJumpBoostEffect);
        aNBT.setBoolean("enableResistanceEffect", enableResistanceEffect);
        aNBT.setBoolean("enableHealthRegenerationEffect", enableHealthRegenerationEffect);
        aNBT.setBoolean("enableNightVisionEffect", enableNightVisionEffect);
        aNBT.setBoolean("enableHasteEffect", enableHasteEffect);
        aNBT.setBoolean("enableFireResistanceEffect", enableFireResistanceEffect);
        aNBT.setBoolean("enableWaterBreathingEffect", enableWaterBreathingEffect);
        aNBT.setBoolean("enableWarpWardEffect", enableWarpWardEffect);
        aNBT.setBoolean("enableFeatherFeetEffect", enableFeatherFeetEffect);
        aNBT.setBoolean("enableVisRegenEffect", enableVisRegenEffect);

        aNBT.setBoolean("canWork", canWork);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        // Stored items
        NBTTagCompound tempItemTag = aNBT.getCompoundTag("windowStorage");
        int stackSize = tempItemTag.getInteger(1 + "stacksizeOfStoredItems");
        ItemStack itemStack = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag(1 + "storedItem"));
        if (itemStack != null) {
            storedWindowItems = itemStack.splitStack(stackSize);
        }

        enableSpeedEffect = aNBT.getBoolean("enableSpeedEffect");
        enableStrengthEffect = aNBT.getBoolean("enableStrengthEffect");
        enableJumpBoostEffect = aNBT.getBoolean("enableJumpBoostEffect");
        enableResistanceEffect = aNBT.getBoolean("enableResistanceEffect");
        enableHealthRegenerationEffect = aNBT.getBoolean("enableHealthRegenerationEffect");
        enableNightVisionEffect = aNBT.getBoolean("enableNightVisionEffect");
        enableHasteEffect = aNBT.getBoolean("enableHasteEffect");
        enableFireResistanceEffect = aNBT.getBoolean("enableFireResistanceEffect");
        enableWaterBreathingEffect = aNBT.getBoolean("enableWaterBreathingEffect");
        enableWarpWardEffect = aNBT.getBoolean("enableWarpWardEffect");
        enableFeatherFeetEffect = aNBT.getBoolean("enableFeatherFeetEffect");
        enableVisRegenEffect = aNBT.getBoolean("enableVisRegenEffect");

        canWork = aNBT.getBoolean("canWork");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("enableSpeedEffect", enableSpeedEffect);
            tag.setBoolean("enableStrengthEffect", enableStrengthEffect);
            tag.setBoolean("enableJumpBoostEffect", enableJumpBoostEffect);
            tag.setBoolean("enableResistanceEffect", enableResistanceEffect);
            tag.setBoolean("enableHealthRegenerationEffect", enableHealthRegenerationEffect);
            tag.setBoolean("enableNightVisionEffect", enableNightVisionEffect);
            tag.setBoolean("enableHasteEffect", enableHasteEffect);
            tag.setBoolean("enableFireResistanceEffect", enableFireResistanceEffect);
            tag.setBoolean("enableWaterBreathingEffect", enableWaterBreathingEffect);
            tag.setBoolean("enableWarpWardEffect", enableWarpWardEffect);
            tag.setBoolean("enableFeatherFeetEffect", enableFeatherFeetEffect);
            tag.setBoolean("enableVisRegenEffect", enableVisRegenEffect);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("Info_SteamBeaconModule_02"));
        if (tag.getBoolean("enableSpeedEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_00"));
        }
        if (tag.getBoolean("enableStrengthEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_01"));
        }
        if (tag.getBoolean("enableJumpBoostEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_02"));
        }
        if (tag.getBoolean("enableResistanceEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_03"));
        }
        if (tag.getBoolean("enableHealthRegenerationEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_04"));
        }
        if (tag.getBoolean("enableNightVisionEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_05"));
        }
        if (tag.getBoolean("enableHasteEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_06"));
        }
        if (tag.getBoolean("enableFireResistanceEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_07"));
        }
        if (tag.getBoolean("enableWaterBreathingEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_08"));
        }
        if (tag.getBoolean("enableWarpWardEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_09"));
        }
        if (tag.getBoolean("enableFeatherFeetEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_10"));
        }
        if (tag.getBoolean("enableVisRegenEffect")) {
            currentTip
                .add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_11"));
        }
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamBeaconModuleRecipeType");
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamBeaconModuleRecipeType"));
        switch (mTier) {
            case 1 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModuleI_00"));
            case 2 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModuleII_00"));
            case 3 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModuleIII_00"));
        }
        tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModule_02") + (1 << (5 + mTier)))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeaconModule_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return null;
    }

    private boolean canActivateNewEffect() {
        return activeEffectsCount < mTier + 1;
    }

    private boolean canActivateNewEffectWork() {
        return activeEffectsCount < mTier + 2;
    }

    private void updateActiveEffectsCount() {
        activeEffectsCount = 0;

        if (hasSpeedEffect) activeEffectsCount++;
        if (hasStrengthEffect) activeEffectsCount++;
        if (hasJumpBoostEffect) activeEffectsCount++;
        if (hasResistanceEffect) activeEffectsCount++;
        if (hasHealthRegenerationEffect) activeEffectsCount++;
        if (hasNightVisionEffect) activeEffectsCount++;
        if (hasHasteEffect) activeEffectsCount++;
        if (hasFireResistanceEffect) activeEffectsCount++;
        if (hasWaterBreathingEffect) activeEffectsCount++;
        if (hasWarpWardEffect) activeEffectsCount++;
        if (hasFeatherFeetEffect) activeEffectsCount++;
        if (hasVisRegenEffect) activeEffectsCount++;
    }

    private void updateMachineEffectsCount() {
        machineEffectsCount = 0;

        if (enableSpeedEffect) machineEffectsCount++;
        if (enableStrengthEffect) machineEffectsCount++;
        if (enableJumpBoostEffect) machineEffectsCount++;
        if (enableResistanceEffect) machineEffectsCount++;
        if (enableHealthRegenerationEffect) machineEffectsCount++;
        if (enableNightVisionEffect) machineEffectsCount++;
        if (enableHasteEffect) machineEffectsCount++;
        if (enableFireResistanceEffect) machineEffectsCount++;
        if (enableWaterBreathingEffect) machineEffectsCount++;
        if (enableWarpWardEffect) machineEffectsCount++;
        if (enableFeatherFeetEffect) machineEffectsCount++;
        if (enableVisRegenEffect) machineEffectsCount++;
    }

    public void toggleSpeedEffect() {
        if (canActivateNewEffect() || hasSpeedEffect) {
            hasSpeedEffect = !hasSpeedEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleStrengthEffect() {
        if (canActivateNewEffect() || hasStrengthEffect) {
            hasStrengthEffect = !hasStrengthEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleJumpBoostEffect() {
        if (canActivateNewEffect() || hasJumpBoostEffect) {
            hasJumpBoostEffect = !hasJumpBoostEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleResistanceEffect() {
        if (canActivateNewEffect() || hasResistanceEffect) {
            hasResistanceEffect = !hasResistanceEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleHealthRegenerationEffect() {
        if (canActivateNewEffect() || hasHealthRegenerationEffect) {
            hasHealthRegenerationEffect = !hasHealthRegenerationEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleNightVisionEffect() {
        if (canActivateNewEffect() || hasNightVisionEffect) {
            hasNightVisionEffect = !hasNightVisionEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleHasteEffect() {
        if (canActivateNewEffect() || hasHasteEffect) {
            hasHasteEffect = !hasHasteEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleWaterBreathingEffect() {
        if (canActivateNewEffect() || hasWaterBreathingEffect) {
            hasWaterBreathingEffect = !hasWaterBreathingEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleFireResistanceEffect() {
        if (canActivateNewEffect() || hasFireResistanceEffect) {
            hasFireResistanceEffect = !hasFireResistanceEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleWarpWardEffect() {
        if (canActivateNewEffect() || hasWarpWardEffect) {
            hasWarpWardEffect = !hasWarpWardEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleFeatherFeetEffect() {
        if (canActivateNewEffect() || hasFeatherFeetEffect) {
            hasFeatherFeetEffect = !hasFeatherFeetEffect;
            updateActiveEffectsCount();
        }
    }

    public void toggleVisRegenEffect() {
        if (canActivateNewEffect() || hasVisRegenEffect) {
            hasVisRegenEffect = !hasVisRegenEffect;
            updateActiveEffectsCount();
        }
    }

    public boolean hasMachineCanWork() {
        return this.canWork;
    }

    public void setMachineCanWork(boolean canWork) {
        this.canWork = canWork;
    }

    public boolean hasSpeedEffect() {
        return hasSpeedEffect;
    }

    public void setSpeedEffect(boolean hasSpeedEffect) {
        this.hasSpeedEffect = hasSpeedEffect;
        updateActiveEffectsCount();
    }

    public boolean hasStrengthEffect() {
        return hasStrengthEffect;
    }

    public void setStrengthEffect(boolean hasStrengthEffect) {
        this.hasStrengthEffect = hasStrengthEffect;
        updateActiveEffectsCount();
    }

    public boolean hasJumpBoostEffect() {
        return hasJumpBoostEffect;
    }

    public void setJumpBoostEffect(boolean hasJumpBoostEffect) {
        this.hasJumpBoostEffect = hasJumpBoostEffect;
        updateActiveEffectsCount();
    }

    public boolean hasResistanceEffect() {
        return hasResistanceEffect;
    }

    public void setResistanceEffect(boolean hasResistanceEffect) {
        this.hasResistanceEffect = hasResistanceEffect;
        updateActiveEffectsCount();
    }

    public boolean hasHealthRegenerationEffect() {
        return hasHealthRegenerationEffect;
    }

    public void setHealthRegenerationEffect(boolean hasHealthRegeneration) {
        this.hasHealthRegenerationEffect = hasHealthRegeneration;
        updateActiveEffectsCount();
    }

    public boolean hasNightVisionEffect() {
        return hasNightVisionEffect;
    }

    public void setNightVisionEffect(boolean hasNightVision) {
        this.hasNightVisionEffect = hasNightVision;
        updateActiveEffectsCount();
    }

    public boolean hasHasteEffect() {
        return hasHasteEffect;
    }

    public void setHasteEffect(boolean hasHaste) {
        this.hasHasteEffect = hasHaste;
        updateActiveEffectsCount();
    }

    public boolean hasFireResistanceEffect() {
        return hasFireResistanceEffect;
    }

    public void setFireResistanceEffect(boolean hasFireResistance) {
        this.hasFireResistanceEffect = hasFireResistance;
        updateActiveEffectsCount();
    }

    public boolean hasWaterBreathingEffect() {
        return hasWaterBreathingEffect;
    }

    public void setWaterBreathingEffect(boolean hasWaterBreathingEffect) {
        this.hasWaterBreathingEffect = hasWaterBreathingEffect;
        updateActiveEffectsCount();
    }

    public boolean hasWarpWardEffect() {
        return hasWarpWardEffect;
    }

    public void setWarpWardEffect(boolean hasWarpWardEffect) {
        this.hasWarpWardEffect = hasWarpWardEffect;
        updateActiveEffectsCount();
    }

    public boolean hasFeatherFeetEffect() {
        return hasFeatherFeetEffect;
    }

    public void setFeatherFeetEffect(boolean hasFeatherFeetEffect) {
        this.hasFeatherFeetEffect = hasFeatherFeetEffect;
        updateActiveEffectsCount();
    }

    public boolean hasVisRegenEffect() {
        return hasVisRegenEffect;
    }

    public void setVisRegenEffect(boolean hasVisRegenEffect) {
        this.hasVisRegenEffect = hasVisRegenEffect;
        updateActiveEffectsCount();
    }

    public boolean enableSpeedEffect() {
        return enableSpeedEffect;
    }

    public void setEnableSpeedEffect(boolean enableSpeedEffect) {
        this.enableSpeedEffect = enableSpeedEffect;
        updateActiveEffectsCount();
    }

    public boolean enableStrengthEffect() {
        return enableStrengthEffect;
    }

    public void setEnableStrengthEffect(boolean enableStrengthEffect) {
        this.enableStrengthEffect = enableStrengthEffect;
        updateActiveEffectsCount();
    }

    public boolean enableJumpBoostEffect() {
        return enableJumpBoostEffect;
    }

    public void setEnableJumpBoostEffect(boolean enableJumpBoostEffect) {
        this.enableJumpBoostEffect = enableJumpBoostEffect;
        updateActiveEffectsCount();
    }

    public boolean enableResistanceEffect() {
        return enableResistanceEffect;
    }

    public void setEnableResistanceEffect(boolean enableResistanceEffect) {
        this.enableResistanceEffect = enableResistanceEffect;
        updateActiveEffectsCount();
    }

    public boolean enableHealthRegenerationEffect() {
        return enableHealthRegenerationEffect;
    }

    public void setEnableHealthRegenerationEffect(boolean enableHealthRegeneration) {
        this.enableHealthRegenerationEffect = enableHealthRegeneration;
        updateActiveEffectsCount();
    }

    public boolean enableNightVisionEffect() {
        return enableNightVisionEffect;
    }

    public void setEnableNightVisionEffect(boolean enableNightVision) {
        this.enableNightVisionEffect = enableNightVision;
        updateActiveEffectsCount();
    }

    public boolean enableHasteEffect() {
        return enableHasteEffect;
    }

    public void setEnableHasteEffect(boolean enableHaste) {
        this.enableHasteEffect = enableHaste;
        updateActiveEffectsCount();
    }

    public boolean enableFireResistanceEffect() {
        return enableFireResistanceEffect;
    }

    public void setEnableFireResistanceEffect(boolean enableFireResistance) {
        this.enableFireResistanceEffect = enableFireResistance;
        updateActiveEffectsCount();
    }

    public boolean enableWaterBreathingEffect() {
        return enableWaterBreathingEffect;
    }

    public void setEnableWaterBreathingEffect(boolean enableWaterBreathing) {
        this.enableWaterBreathingEffect = enableWaterBreathing;
        updateActiveEffectsCount();
    }

    public boolean enableWarpWardEffect() {
        return enableWarpWardEffect;
    }

    public void setEnableWarpWardEffect(boolean enableWarpWardEffect) {
        this.enableWarpWardEffect = enableWarpWardEffect;
        updateActiveEffectsCount();
    }

    public boolean enableFeatherFeetEffect() {
        return enableFeatherFeetEffect;
    }

    public void setEnableFeatherFeetEffect(boolean enableFeatherFeetEffect) {
        this.enableFeatherFeetEffect = enableFeatherFeetEffect;
        updateActiveEffectsCount();
    }

    public boolean enableVisRegenEffect() {
        return enableVisRegenEffect;
    }

    public void setEnableVisRegenEffect(boolean enableVisRegenEffect) {
        this.enableVisRegenEffect = enableVisRegenEffect;
        updateActiveEffectsCount();
    }

    private boolean isValidItem(ItemStack itemStack) {
        return itemStack.getItem() == Items.iron_ingot || itemStack.getItem() == Items.gold_ingot
            || itemStack.getItem() == Items.diamond
            || itemStack.getItem() == Items.emerald;
    }

    private int setMaxEffectLevel() {
        return Math.min(mTier, recipeOcCount);
    }

    @Override
    public int getMachineEffectRange() {
        return switch (mTier) {
            case 1 -> 64;
            case 2 -> 128;
            case 3 -> 256;
            default -> 0;
        };
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        updateMachineEffectsCount();
        if (!canWork) {
            this.lEUt = 0;
            this.mEfficiency = 0;
            this.mMaxProgresstime = 0;
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        this.lEUt = machineEffectsCount * V[3] * Math.max(1, setMaxEffectLevel() * 2);
        this.mEfficiency = 10000;
        this.mMaxProgresstime = 1000;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        runningTickCounter++;
        if (runningTickCounter % 40 == 0) {
            final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
            if (tileEntity == null) {
                return super.onRunningTick(stack);
            }
            int x = tileEntity.getXCoord();
            int y = tileEntity.getYCoord();
            int z = tileEntity.getZCoord();
            World world = tileEntity.getWorld();

            List<EntityPlayer> playersInRange = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox(
                    x - getMachineEffectRange(),
                    y - getMachineEffectRange(),
                    z - getMachineEffectRange(),
                    x + getMachineEffectRange(),
                    y + getMachineEffectRange(),
                    z + getMachineEffectRange()));

            for (EntityPlayer player : playersInRange) {
                double distance = player.getDistance(x, y, z);
                if (distance <= getMachineEffectRange()) {
                    if (enableSpeedEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 300, setMaxEffectLevel()));
                        setEnableSpeedEffect(enableSpeedEffect); // 更新启用状态
                    }
                    if (enableStrengthEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 300, setMaxEffectLevel()));
                        setEnableStrengthEffect(enableStrengthEffect); // 更新启用状态
                    }
                    if (enableJumpBoostEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.jump.id, 300, setMaxEffectLevel()));
                        setEnableJumpBoostEffect(enableJumpBoostEffect); // 更新启用状态
                    }
                    if (enableResistanceEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 300, setMaxEffectLevel()));
                        setEnableResistanceEffect(enableResistanceEffect); // 更新启用状态
                    }
                    if (enableHealthRegenerationEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 300, setMaxEffectLevel()));
                        setEnableHealthRegenerationEffect(enableHealthRegenerationEffect); // 更新启用状态
                    }
                    if (enableNightVisionEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, setMaxEffectLevel()));
                        setEnableNightVisionEffect(enableNightVisionEffect); // 更新启用状态
                    }
                    if (enableHasteEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 300, setMaxEffectLevel()));
                        setEnableHasteEffect(enableHasteEffect); // 更新启用状态
                    }
                    if (enableFireResistanceEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 300, setMaxEffectLevel()));
                        setEnableFireResistanceEffect(enableFireResistanceEffect); // 更新启用状态
                    }
                    if (enableWaterBreathingEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 300, setMaxEffectLevel()));
                        setEnableWaterBreathingEffect(enableWaterBreathingEffect); // 更新启用状态
                    }
                    if (enableWarpWardEffect) {
                        player.addPotionEffect(new PotionEffect(Config.potionWarpWardID, 300, setMaxEffectLevel()));
                        setEnableWarpWardEffect(enableWarpWardEffect); // 更新启用状态
                    }
                    if (enableFeatherFeetEffect) {
                        player.addPotionEffect(new PotionEffect(ModPotions.featherfeet.id, 300, setMaxEffectLevel()));
                        setEnableFeatherFeetEffect(enableFeatherFeetEffect); // 更新启用状态
                    }
                    if (enableVisRegenEffect) {
                        player.addPotionEffect(new PotionEffect(PotionVisRegen.instance.id, 300, setMaxEffectLevel()));
                        setEnableVisRegenEffect(enableVisRegenEffect); // 更新启用状态
                    }
                }
            }
        }
        return super.onRunningTick(stack);
    }

    public static final UITexture SPEED_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 0, 198, 18, 216);
    public static final UITexture STRENGTH_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 72, 198, 90, 216);
    public static final UITexture JUMP_BOOST_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 36, 216, 54, 234);
    public static final UITexture RESISTANCE_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 108, 216, 126, 234);
    public static final UITexture HEALTH_REGENERATION_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 126, 198, 144, 216);
    public static final UITexture NIGHT_VISION_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 72, 216, 90, 234);
    public static final UITexture HASTE_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 36, 198, 54, 216);
    public static final UITexture WATER_BREATHING_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 0, 234, 18, 252);
    public static final UITexture FIRE_RESISTANCE_EFFECT = UITexture
        .partly(Minecraft.resourceDomain, "gui/container/inventory", 256, 256, 126, 216, 144, 234);
    public static final UITexture WARP_WARD_EFFECT = UITexture
        .partly(Thaumcraft.resourceDomain, "misc/potions", 256, 256, 54, 234, 72, 252);
    public static final UITexture FEATHER_FEET_EFFECT = UITexture
        .partly(Botania.resourceDomain, "gui/potions", 256, 256, 18, 198, 36, 216);
    public static final UITexture VIS_REGEN_EFFECT = UITexture
        .partly(ThaumicHorizons.resourceDomain, "misc/potions", 256, 256, 54, 198, 72, 216);

    public static final UITexture BEACON_MATERIAL = UITexture
        .fullImage(ModList.ScienceNotLeisure.resourceDomain, "gui/picture/steam_beacon");

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(CONFIG_WINDOW_ID, this::createBeaconConfigWindow);
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::hasSpeedEffect, val -> hasSpeedEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::hasJumpBoostEffect, val -> hasJumpBoostEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::hasStrengthEffect, val -> hasStrengthEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::hasResistanceEffect, val -> hasResistanceEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(
                this::hasHealthRegenerationEffect,
                val -> hasHealthRegenerationEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::hasNightVisionEffect, val -> hasNightVisionEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::hasHasteEffect, val -> hasHasteEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(this::hasFireResistanceEffect, val -> hasFireResistanceEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::hasWarpWardEffect, val -> hasWarpWardEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::enableSpeedEffect, val -> enableSpeedEffect = val));
        builder
            .widget(new FakeSyncWidget.BooleanSyncer(this::enableJumpBoostEffect, val -> enableJumpBoostEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::enableStrengthEffect, val -> enableStrengthEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(this::enableResistanceEffect, val -> enableResistanceEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(
                this::enableHealthRegenerationEffect,
                val -> enableHealthRegenerationEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(this::enableNightVisionEffect, val -> enableNightVisionEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::enableHasteEffect, val -> enableHasteEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(
                this::enableFireResistanceEffect,
                val -> enableFireResistanceEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(
                this::enableWaterBreathingEffect,
                val -> enableWaterBreathingEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::enableWarpWardEffect, val -> enableWarpWardEffect = val));
        builder.widget(
            new FakeSyncWidget.BooleanSyncer(this::enableFeatherFeetEffect, val -> enableFeatherFeetEffect = val));
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::enableVisRegenEffect, val -> enableVisRegenEffect = val));

        super.addUIWidgets(builder, buildContext);

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(CONFIG_WINDOW_ID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_BATCH_MODE_ON);
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_00"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 94)
            .setSize(16, 16));
    }

    public ModularWindow createBeaconConfigWindow(final EntityPlayer player) {
        final int WIDTH = 100;
        final int HEIGHT = 116;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        inputSlotHandler.insertItem(0, storedWindowItems, false);
        storedWindowItems = null;
        updateGuiMachineEffect();

        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.BottomRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)
                        .subtract(0, 10)));
        builder.widget(
            TextWidget.localised("Info_SteamBeaconModule_00")
                .setPos(0, 0)
                .setSize(100, 18))
            .widget(
                new DrawableWidget().setDrawable(BEACON_MATERIAL)
                    .setSize(83, 18)
                    .setPos(8, 90))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleSpeedEffect())
                    .setPlayClickSoundResource(
                        () -> hasSpeedEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasSpeedEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(SPEED_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::hasSpeedEffect, this::setSpeedEffect), builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_00"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(6, 18)
                    .setSize(16, 16))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleStrengthEffect())
                    .setPlayClickSoundResource(
                        () -> hasStrengthEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasStrengthEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(STRENGTH_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(this::hasStrengthEffect, this::setStrengthEffect),
                        builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_01"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(24, 18)
                    .setSize(16, 16))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleJumpBoostEffect())
                    .setPlayClickSoundResource(
                        () -> hasJumpBoostEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasJumpBoostEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(JUMP_BOOST_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(this::hasJumpBoostEffect, this::setJumpBoostEffect),
                        builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_02"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(42, 18)
                    .setSize(16, 16))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleResistanceEffect())
                    .setPlayClickSoundResource(
                        () -> hasResistanceEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasResistanceEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(RESISTANCE_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(this::hasResistanceEffect, this::setResistanceEffect),
                        builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_03"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(6, 36)
                    .setSize(16, 16))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleHealthRegenerationEffect())
                    .setPlayClickSoundResource(
                        () -> hasHealthRegenerationEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasHealthRegenerationEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(HEALTH_REGENERATION_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(
                            this::hasHealthRegenerationEffect,
                            this::setHealthRegenerationEffect),
                        builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_04"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(24, 36)
                    .setSize(16, 16))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleNightVisionEffect())
                    .setPlayClickSoundResource(
                        () -> hasNightVisionEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasNightVisionEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(NIGHT_VISION_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(this::hasNightVisionEffect, this::setNightVisionEffect),
                        builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_05"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(42, 36)
                    .setSize(16, 16));
        if (mTier > 1) {
            builder.widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleHasteEffect())
                    .setPlayClickSoundResource(
                        () -> hasHasteEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasHasteEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(HASTE_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::hasHasteEffect, this::setHasteEffect), builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_06"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(6, 54)
                    .setSize(16, 16))
                .widget(
                    new ButtonWidget().setOnClick((clickData, widget) -> toggleFireResistanceEffect())
                        .setPlayClickSoundResource(
                            () -> hasFireResistanceEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                                : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                        .setBackground(() -> {
                            List<UITexture> ret = new ArrayList<>();
                            if (hasFireResistanceEffect()) {
                                ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                            } else {
                                ret.add(GTUITextures.BUTTON_STANDARD);
                            }
                            ret.add(FIRE_RESISTANCE_EFFECT);
                            return ret.toArray(new IDrawable[0]);
                        })
                        .attachSyncer(
                            new FakeSyncWidget.BooleanSyncer(
                                this::hasFireResistanceEffect,
                                this::setFireResistanceEffect),
                            builder)
                        .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_07"))
                        .setTooltipShowUpDelay(TOOLTIP_DELAY)
                        .setPos(24, 54)
                        .setSize(16, 16))
                .widget(
                    new ButtonWidget().setOnClick((clickData, widget) -> toggleWaterBreathingEffect())
                        .setPlayClickSoundResource(
                            () -> hasWaterBreathingEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                                : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                        .setBackground(() -> {
                            List<UITexture> ret = new ArrayList<>();
                            if (hasWaterBreathingEffect()) {
                                ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                            } else {
                                ret.add(GTUITextures.BUTTON_STANDARD);
                            }
                            ret.add(WATER_BREATHING_EFFECT);
                            return ret.toArray(new IDrawable[0]);
                        })
                        .attachSyncer(
                            new FakeSyncWidget.BooleanSyncer(
                                this::hasWaterBreathingEffect,
                                this::setWaterBreathingEffect),
                            builder)
                        .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_08"))
                        .setTooltipShowUpDelay(TOOLTIP_DELAY)
                        .setPos(42, 54)
                        .setSize(16, 16));
        }
        if (mTier > 2) {
            builder.widget(
                new ButtonWidget().setOnClick((clickData, widget) -> toggleWarpWardEffect())
                    .setPlayClickSoundResource(
                        () -> hasWarpWardEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        List<UITexture> ret = new ArrayList<>();
                        if (hasWarpWardEffect()) {
                            ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        } else {
                            ret.add(GTUITextures.BUTTON_STANDARD);
                        }
                        ret.add(WARP_WARD_EFFECT);
                        return ret.toArray(new IDrawable[0]);
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(this::hasWarpWardEffect, this::setWarpWardEffect),
                        builder)
                    .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_09"))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setPos(6, 72)
                    .setSize(16, 16))
                .widget(
                    new ButtonWidget().setOnClick((clickData, widget) -> toggleFeatherFeetEffect())
                        .setPlayClickSoundResource(
                            () -> hasFeatherFeetEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                                : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                        .setBackground(() -> {
                            List<UITexture> ret = new ArrayList<>();
                            if (hasFeatherFeetEffect()) {
                                ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                            } else {
                                ret.add(GTUITextures.BUTTON_STANDARD);
                            }
                            ret.add(FEATHER_FEET_EFFECT);
                            return ret.toArray(new IDrawable[0]);
                        })
                        .attachSyncer(
                            new FakeSyncWidget.BooleanSyncer(this::hasFeatherFeetEffect, this::setFeatherFeetEffect),
                            builder)
                        .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_10"))
                        .setTooltipShowUpDelay(TOOLTIP_DELAY)
                        .setPos(24, 72)
                        .setSize(16, 16))
                .widget(
                    new ButtonWidget().setOnClick((clickData, widget) -> toggleVisRegenEffect())
                        .setPlayClickSoundResource(
                            () -> hasVisRegenEffect() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                                : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                        .setBackground(() -> {
                            List<UITexture> ret = new ArrayList<>();
                            if (hasVisRegenEffect()) {
                                ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                            } else {
                                ret.add(GTUITextures.BUTTON_STANDARD);
                            }
                            ret.add(VIS_REGEN_EFFECT);
                            return ret.toArray(new IDrawable[0]);
                        })
                        .attachSyncer(
                            new FakeSyncWidget.BooleanSyncer(this::hasVisRegenEffect, this::setVisRegenEffect),
                            builder)
                        .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_Effect_11"))
                        .setTooltipShowUpDelay(TOOLTIP_DELAY)
                        .setPos(42, 72)
                        .setSize(16, 16));
        }

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (canActivateNewEffectWork() && !widget.isClient()) {
                if (payCost(inputSlotHandler)) {
                    setMachineEffect();
                    setMachineCanWork(true);
                    reopenWindow(widget, CONFIG_WINDOW_ID);
                }
            } else {
                setMachineCanWork(false);
            }
        })
            .setPlayClickSoundResource(SoundResource.GUI_BUTTON_UP.resourceLocation)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_CHECKMARK);
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::hasMachineCanWork, this::setMachineCanWork), builder)
            .addTooltip(StatCollector.translateToLocal("Info_SteamBeaconModule_01"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(66, 37)
            .setSize(16, 16))
            .widget(
                SlotGroup.ofItemHandler(inputSlotHandler, 1)
                    .startFromSlot(0)
                    .endAtSlot(0)
                    .background(getGUITextureSet().getItemSlot())
                    .build()
                    .setPos(65, 18));
        return builder.build();
    }

    public void reopenWindow(Widget widget, int windowId) {
        if (!widget.isClient()) {
            ModularUIContext ctx = widget.getContext();
            if (ctx.isWindowOpen(windowId)) {
                ctx.closeWindow(windowId);
            }
            ctx.openSyncedWindow(windowId);
        }
    }

    public void setMachineEffect() {
        setEnableSpeedEffect(hasSpeedEffect);
        setEnableStrengthEffect(hasStrengthEffect);
        setEnableJumpBoostEffect(hasJumpBoostEffect);
        setEnableResistanceEffect(hasResistanceEffect);
        setEnableHealthRegenerationEffect(hasHealthRegenerationEffect);
        setEnableNightVisionEffect(hasNightVisionEffect);
        setEnableHasteEffect(hasHasteEffect);
        setEnableFireResistanceEffect(hasFireResistanceEffect);
        setEnableWaterBreathingEffect(hasWaterBreathingEffect);
        setEnableWarpWardEffect(hasWarpWardEffect);
        setEnableFeatherFeetEffect(hasFeatherFeetEffect);
        setEnableVisRegenEffect(hasVisRegenEffect);
    }

    public void updateGuiMachineEffect() {
        setStrengthEffect(enableStrengthEffect);
        setSpeedEffect(enableSpeedEffect);
        setJumpBoostEffect(enableJumpBoostEffect);
        setResistanceEffect(enableResistanceEffect);
        setHealthRegenerationEffect(enableHealthRegenerationEffect);
        setNightVisionEffect(enableNightVisionEffect);
        setHasteEffect(enableHasteEffect);
        setFireResistanceEffect(enableFireResistanceEffect);
        setWaterBreathingEffect(enableWaterBreathingEffect);
        setWarpWardEffect(enableWarpWardEffect);
        setFeatherFeetEffect(enableFeatherFeetEffect);
        setVisRegenEffect(enableVisRegenEffect);
    }

    public boolean payCost(ItemStackHandler handler) {
        ItemStack inputStack = handler.getStackInSlot(0);
        if (inputStack == null) return false;
        if (isValidItem(inputStack)) {
            handler.extractItem(0, 1, false);
            return true;
        }
        return false;
    }

}
