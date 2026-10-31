package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.google.common.collect.ImmutableMap;
import com.science.gtnl.common.gui.modularui.multiblock.SteamBeaconModuleGui;
import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.compat.thaumichorizons.ThaumicHorizonsPotions;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import thaumcraft.common.config.Config;
import vazkii.botania.common.brew.ModPotions;

@IMetaTileEntity.SkipGenerateDescription
public class SteamBeaconModule extends SteamElevatorModuleBase {

    public static final String[] TIER_TITLES = { "basic", "advanced", "elite" };

    public static final int CONFIG_WINDOW_ID = 14;

    private boolean hasSpeedEffect;
    private boolean hasStrengthEffect;
    private boolean hasJumpBoostEffect;
    private boolean hasResistanceEffect;
    private boolean hasHealthRegenerationEffect;
    private boolean hasNightVisionEffect;
    private boolean hasHasteEffect;
    private boolean hasFireResistanceEffect;
    private boolean hasWaterBreathingEffect;
    private boolean hasWarpWardEffect;
    private boolean hasFeatherFeetEffect;
    private boolean hasVisRegenEffect;

    private boolean enableSpeedEffect;
    private boolean enableStrengthEffect;
    private boolean enableJumpBoostEffect;
    private boolean enableResistanceEffect;
    private boolean enableHealthRegenerationEffect;
    private boolean enableNightVisionEffect;
    private boolean enableHasteEffect;
    private boolean enableFireResistanceEffect;
    private boolean enableWaterBreathingEffect;
    private boolean enableFeatherFeetEffect;
    private boolean enableVisRegenEffect;
    private boolean enableWarpWardEffect;
    private int activeEffectsCount = 0;
    private int machineEffectsCount = 0;
    private boolean canWork;
    private int runningTickCounter = 0;
    private final ItemStackHandler inputSlotHandler = new ItemStackHandler(1);
    private ItemStack storedWindowItems;

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
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamBeaconModuleGui(this);
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
        currentTip.add(
            EnumChatFormatting.BLUE + StatCollector.translateToLocal("gtnl.waila.steam_beacon_module.enabled_effects"));
        if (tag.getBoolean("enableSpeedEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.0"));
        }
        if (tag.getBoolean("enableStrengthEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.1"));
        }
        if (tag.getBoolean("enableJumpBoostEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.2"));
        }
        if (tag.getBoolean("enableResistanceEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.3"));
        }
        if (tag.getBoolean("enableHealthRegenerationEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.4"));
        }
        if (tag.getBoolean("enableNightVisionEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.5"));
        }
        if (tag.getBoolean("enableHasteEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.6"));
        }
        if (tag.getBoolean("enableFireResistanceEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.7"));
        }
        if (tag.getBoolean("enableWaterBreathingEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.8"));
        }
        if (tag.getBoolean("enableWarpWardEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.9"));
        }
        if (tag.getBoolean("enableFeatherFeetEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN
                    + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.10"));
        }
        if (tag.getBoolean("enableVisRegenEffect")) {
            currentTip.add(
                EnumChatFormatting.GREEN
                    + StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.effect.11"));
        }
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.recipe_type");
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        return ImmutableMap.of(
            "tier_title",
            TIER_TITLES[Math.min(Math.max(mTier - 1, 0), TIER_TITLES.length - 1)],
            "range",
            getMachineEffectRange());
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
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

    public int getBeaconTierForGui() {
        return mTier;
    }

    public ItemStackHandler getBeaconInputSlotHandlerForGui() {
        return inputSlotHandler;
    }

    public boolean isValidBeaconPaymentItemForGui(ItemStack itemStack) {
        return isValidItem(itemStack);
    }

    public void prepareBeaconConfigForGui() {
        inputSlotHandler.insertItem(0, storedWindowItems, false);
        storedWindowItems = null;
        updateGuiMachineEffect();
    }

    public boolean applyBeaconConfigFromGui() {
        if (!canActivateNewEffectWork()) {
            setMachineCanWork(false);
            return false;
        }
        if (!payCost(inputSlotHandler)) {
            return false;
        }
        setMachineEffect();
        setMachineCanWork(true);
        return true;
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
        Item item = itemStack.getItem();
        return item == Items.iron_ingot || item == Items.gold_ingot || item == Items.diamond || item == Items.emerald;
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

        this.lEUt = machineEffectsCount * GTValues.V[3] * Math.max(1, setMaxEffectLevel() * 2);
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
                        setEnableSpeedEffect(enableSpeedEffect); // Refresh enabled state.
                    }
                    if (enableStrengthEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 300, setMaxEffectLevel()));
                        setEnableStrengthEffect(enableStrengthEffect); // Refresh enabled state.
                    }
                    if (enableJumpBoostEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.jump.id, 300, setMaxEffectLevel()));
                        setEnableJumpBoostEffect(enableJumpBoostEffect); // Refresh enabled state.
                    }
                    if (enableResistanceEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 300, setMaxEffectLevel()));
                        setEnableResistanceEffect(enableResistanceEffect); // Refresh enabled state.
                    }
                    if (enableHealthRegenerationEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 300, setMaxEffectLevel()));
                        setEnableHealthRegenerationEffect(enableHealthRegenerationEffect); // Refresh enabled state.
                    }
                    if (enableNightVisionEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, setMaxEffectLevel()));
                        setEnableNightVisionEffect(enableNightVisionEffect); // Refresh enabled state.
                    }
                    if (enableHasteEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 300, setMaxEffectLevel()));
                        setEnableHasteEffect(enableHasteEffect); // Refresh enabled state.
                    }
                    if (enableFireResistanceEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 300, setMaxEffectLevel()));
                        setEnableFireResistanceEffect(enableFireResistanceEffect); // Refresh enabled state.
                    }
                    if (enableWaterBreathingEffect) {
                        player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 300, setMaxEffectLevel()));
                        setEnableWaterBreathingEffect(enableWaterBreathingEffect); // Refresh enabled state.
                    }
                    if (enableWarpWardEffect) {
                        player.addPotionEffect(new PotionEffect(Config.potionWarpWardID, 300, setMaxEffectLevel()));
                        setEnableWarpWardEffect(enableWarpWardEffect); // Refresh enabled state.
                    }
                    if (enableFeatherFeetEffect) {
                        player.addPotionEffect(new PotionEffect(ModPotions.featherfeet.id, 300, setMaxEffectLevel()));
                        setEnableFeatherFeetEffect(enableFeatherFeetEffect); // Refresh enabled state.
                    }
                    if (ThaumicHorizonsPotions.hasVisRegen() && enableVisRegenEffect) {
                        player.addPotionEffect(
                            new PotionEffect(ThaumicHorizonsPotions.visRegenId(), 300, setMaxEffectLevel()));
                        setEnableVisRegenEffect(enableVisRegenEffect); // Refresh enabled state.
                    }
                }
            }
        }
        return super.onRunningTick(stack);
    }

    @Override
    public boolean supportsPowerPanel() {
        return false;
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
