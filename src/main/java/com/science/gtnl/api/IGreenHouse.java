package com.science.gtnl.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseDynamicInventory;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseModes;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.VoidProtectionHelper;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;

public interface IGreenHouse extends IVoidable {

    IGregTechTileEntity getBaseMetaTileEntity();

    ArrayList<ItemStack> getStoredInputs();

    List<GreenHouseBucket> getBuckets();

    int getMaxProgressTime();

    void setMaxProgressTime(int time);

    void setLEUt(long lEUt);

    boolean isUseNoHumidity();

    void setUseNoHumidity(boolean useNoHumidity);

    int getSetupPhase();

    int getMaxSeedTypes();

    int getMaxSeedCount();

    void setSetupPhase(int setupPhase);

    GreenHouseMode getMode();

    void setMode(GreenHouseMode mode);

    ArrayList<MTEHatchOutputBus> getOutputBus();

    ArrayList<FluidStack> getStoredFluids();

    void updateSlots();

    int getWaterUsage();

    default ModularWindow createConfigurationWindow(final EntityPlayer player) {
        ModularWindow.Builder builder = ModularWindow.builder(200, 100);
        builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .setPos(5, 5)
                .setSize(16, 16))
            .widget(new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_Configuration")).setPos(25, 9))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setPos(185, 3))
            .widget(
                new Column().widget(
                    new CycleButtonWidget().setLength(3)
                        .setGetter(this::getSetupPhase)
                        .setSetter(val -> {
                            if (!(player instanceof EntityPlayerMP)) return;
                            tryChangeSetupPhase(player);
                        })
                        .addTooltip(
                            0,
                            new Text(StatCollector.translateToLocal("Info_EdenGarden_Operating"))
                                .color(Color.GREEN.dark(3)))
                        .addTooltip(
                            1,
                            new Text(StatCollector.translateToLocal("Info_EdenGarden_Input"))
                                .color(Color.YELLOW.dark(3)))
                        .addTooltip(
                            2,
                            new Text(StatCollector.translateToLocal("Info_EdenGarden_Output"))
                                .color(Color.YELLOW.dark(3)))
                        .setTextureGetter(
                            i -> i == 0
                                ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Operating"))
                                    .color(Color.GREEN.dark(3))
                                    .withFixedSize(70 - 18, 18, 15, 0)
                                : i == 1
                                    ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Input"))
                                        .color(Color.YELLOW.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0)
                                    : new Text(StatCollector.translateToLocal("Info_EdenGarden_Output"))
                                        .color(Color.YELLOW.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0))
                        .setBackground(
                            ModularUITextures.VANILLA_BACKGROUND,
                            GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                        .setSize(70, 18)
                        .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_SetupMode")))
                    .widget(
                        new CycleButtonWidget().setLength(2)
                            .setGetter(
                                () -> this.getMode()
                                    .getUIIndex())
                            .setSetter(val -> {
                                if (!(player instanceof EntityPlayerMP)) return;
                                tryChangeMode(player);
                            })
                            .addTooltip(
                                0,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                    .color(Color.RED.dark(3)))
                            .addTooltip(
                                1,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                    .color(Color.GREEN.dark(3)))
                            .setTextureGetter(
                                i -> i == 0
                                    ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                        .color(Color.RED.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0)
                                    : new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                        .color(Color.GREEN.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0))
                            .setBackground(
                                ModularUITextures.VANILLA_BACKGROUND,
                                GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                            .setSize(70, 18)
                            .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_IC2Mode")))
                    .widget(
                        new CycleButtonWidget().setLength(2)
                            .setGetter(() -> isUseNoHumidity() ? 1 : 0)
                            .setSetter(val -> {
                                if (!(player instanceof EntityPlayerMP)) return;
                                this.tryChangeHumidityMode(player);
                            })
                            .addTooltip(
                                0,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                    .color(Color.RED.dark(3)))
                            .addTooltip(
                                1,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                    .color(Color.GREEN.dark(3)))
                            .setTextureGetter(
                                i -> i == 0
                                    ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                        .color(Color.RED.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0)
                                    : new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                        .color(Color.GREEN.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0))
                            .setBackground(
                                ModularUITextures.VANILLA_BACKGROUND,
                                GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                            .setSize(70, 18)
                            .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode")))
                    .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                    .setPos(10, 30))
            .widget(
                new Column()
                    .widget(
                        new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_SetupMode")).setSize(100, 18))
                    .widget(new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_IC2Mode")).setSize(100, 18))
                    .widget(
                        new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode"))
                            .setSize(100, 18))
                    .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                    .setPos(80, 30))
            .widget(
                new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CROSS)
                    .setSize(18, 18)
                    .setPos(10, 30)
                    .addTooltip(new Text("Can't change configuration when running !").color(Color.RED.dark(3)))
                    .setEnabled(widget -> getBaseMetaTileEntity().isActive()));
        return builder.build();
    }

    default void tryChangeMode(EntityPlayer aPlayer) {
        if (this.getMaxProgressTime() > 0) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_Mode_Working"));
            return;
        }
        if (!this.getBuckets()
            .isEmpty()) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_Mode_HasSeeds"));
            return;
        }
        this.setMode(GreenHouseModes.getNextMode(this.getMode()));
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocalFormatted(
                "Info_EdenGarden_Mode_Change",
                this.getMode()
                    .getName()));
    }

    default void tryChangeSetupPhase(EntityPlayer aPlayer) {
        if (this.getMaxProgressTime() > 0) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_SetupPhase_Working"));
            return;
        }
        this.setSetupPhase(this.getSetupPhase() + 1);
        if (this.getSetupPhase() == 3) this.setSetupPhase(0);

        String phaseChangeMessage = StatCollector.translateToLocal("Info_EdenGarden_SetupPhase_Change") + " ";

        phaseChangeMessage += switch (this.getSetupPhase()) {
            case 0 -> StatCollector.translateToLocal("Info_EdenGarden_Operating");
            case 1 -> StatCollector.translateToLocal("Info_EdenGarden_Input");
            case 2 -> StatCollector.translateToLocal("Info_EdenGarden_Output");
            default -> StatCollector.translateToLocal("Info_EdenGarden_SetupPhase_Invalid");
        };

        GTUtility.sendChatToPlayer(aPlayer, phaseChangeMessage);
    }

    default void tryChangeHumidityMode(EntityPlayer aPlayer) {
        this.setUseNoHumidity(!this.isUseNoHumidity());
        if (this.isUseNoHumidity()) {
            GTUtility
                .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode_Enabled"));
        } else {
            GTUtility
                .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode_Disabled"));
        }
    }

    default GreenHouseDynamicInventory<GreenHouseBucket> getDynamicInventory() {
        return new GreenHouseDynamicInventory<>(
            128,
            60,
            this::getMaxSeedTypes,
            this::getMaxSeedCount,
            this.getBuckets()::size,
            this::getTotalSeedCount,
            this.getBuckets(),
            GreenHouseBucket::getSeedStack).allowInventoryInjection(this::addCrop)
                .allowInventoryExtraction((bucket, player) -> {
                    if (bucket == null) return null;
                    int maxRemove = bucket.getSeedStack()
                        .getMaxStackSize();
                    ItemStack[] outputs = bucket.tryRemoveSeed(maxRemove, false);
                    if (outputs == null || outputs.length == 0) return null;
                    ItemStack ret = outputs[0];
                    for (int i = 1; i < outputs.length; i++) {
                        ItemStack suppertItem = outputs[i];
                        if (!player.inventory.addItemStackToInventory(suppertItem)) {
                            player.entityDropItem(suppertItem, 0.f);
                        }
                    }
                    if (bucket.getSeedCount() <= 0) this.getBuckets()
                        .remove(bucket);
                    return ret;
                })
                .setEnabled(() -> this.getMaxProgressTime() == 0);
    }

    default int getTotalSeedCount() {
        return getBuckets().stream()
            .mapToInt(GreenHouseBucket::getSeedCount)
            .sum();
    }

    default boolean tryEmptyBucket(GreenHouseBucket bucket) {
        int totalSeeds = bucket.getSeedCount();
        if (totalSeeds <= 0) return true;

        for (MTEHatchOutputBus tHatch : GTUtility.validMTEList(getOutputBus())) {
            if (!(tHatch instanceof MTEHatchOutputBusME)) continue;
            for (ItemStack stack : bucket.tryRemoveSeed(totalSeeds, false)) {
                tHatch.storePartial(stack);
            }
            return true;
        }

        ItemStack[] simulated = bucket.tryRemoveSeed(1, true);
        VoidProtectionHelper helper = new VoidProtectionHelper().setMachine(this, true, false)
            .setItemOutputs(simulated)
            .setMaxParallel(totalSeeds)
            .build();

        if (helper.getMaxParallel() > 0) {
            for (ItemStack toOutput : bucket.tryRemoveSeed(helper.getMaxParallel(), false)) {
                for (MTEHatchOutputBus tHatch : GTUtility.validMTEList(getOutputBus())) {
                    if (tHatch.storePartial(toOutput)) break;
                }
            }
        }

        return bucket.getSeedCount() <= 0;
    }

    default boolean tryDrain(FluidStack toConsume, boolean drainPartial) {
        if (toConsume == null || toConsume.amount <= 0) return true;

        List<FluidStack> fluids = getStoredFluids();
        int remaining = toConsume.amount;

        for (FluidStack fluid : fluids) {
            if (!fluid.isFluidEqual(toConsume)) continue;
            int used = Math.min(remaining, fluid.amount);
            fluid.amount -= used;
            remaining -= used;
            if (remaining <= 0) break;
        }

        if (!drainPartial && remaining > 0 && !MainConfig.debug.enableDebugMode) return false;
        return remaining <= 0;
    }

    default CheckRecipeResult processSetupPhase() {
        if ((getSetupPhase() == 1 && getBuckets().size() >= getMaxSeedTypes())
            || (getSetupPhase() == 2 && getBuckets().isEmpty())) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (getSetupPhase() == 1) {
            for (ItemStack input : getStoredInputs()) {
                addCrop(input);
                if (getBuckets().size() >= getMaxSeedTypes()) break;
            }
        } else if (getSetupPhase() == 2) {
            Iterator<GreenHouseBucket> iter = getBuckets().iterator();
            while (iter.hasNext()) {
                GreenHouseBucket bucket = iter.next();
                if (tryEmptyBucket(bucket)) iter.remove();
                else {
                    this.setMaxProgressTime(20);
                    this.setLEUt(0);
                    return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                }
            }
        }

        this.updateSlots();
        this.setMaxProgressTime(10);
        this.setLEUt(0);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    default ItemStack addCrop(ItemStack input) {
        return addCrop(input, false) ? input : null;
    }

    default boolean addCrop(ItemStack input, boolean simulate) {
        if (input == null || input.stackSize <= 0) return true;
        if (simulate) input = input.copy();

        int addCap = Math.min(input.stackSize, getMaxSeedCount() - getTotalSeedCount());
        if (addCap <= 0) return false;

        ItemStack finalInput = input;
        boolean added = getBuckets().stream()
            .anyMatch(bucket -> bucket.tryAddSeed(this, finalInput, addCap, simulate) > 0);
        if (added) return input.stackSize <= 0;

        if (getBuckets().size() >= getMaxSeedTypes()) return false;
        GreenHouseBucket bucket = getMode().tryCreateNewBucket(this, input, addCap, simulate);
        if (bucket == null) return false;

        getBuckets().add(bucket);
        return input.stackSize <= 0;
    }

    default CheckRecipeResult validateBuckets() {
        if (getBuckets().size() > getMaxSeedTypes()) return SimpleCheckRecipeResult.ofFailure("EIG_slotoverflow");
        if (getTotalSeedCount() > getMaxSeedCount()) return SimpleCheckRecipeResult.ofFailure("EIG_seedOverflow");

        Iterator<GreenHouseBucket> iter = getBuckets().iterator();
        while (iter.hasNext()) {
            GreenHouseBucket bucket = iter.next();
            if (bucket.isValid() || bucket.revalidate(this)) continue;
            tryEmptyBucket(bucket);
            if (bucket.getSeedCount() <= 0) iter.remove();
        }

        if (getBuckets().isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;
        if (!tryDrain(new FluidStack(FluidRegistry.WATER, getTotalSeedCount() * getWaterUsage()), false))
            return SimpleCheckRecipeResult.ofFailure("EIG_missingwater");

        return null;
    }
}
