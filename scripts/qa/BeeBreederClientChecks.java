package com.science.gtnl.qa;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerGhostIngredientSlot;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.PageButton;
import com.cleanroommc.modularui.widgets.PagedWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.machine.multiblock.LargeBeeBreeder;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLMachineID;
import com.science.gtnl.utils.machine.bee.BeeBreedingHelper;
import com.science.gtnl.utils.machine.bee.BeeBreedingPlanner;
import com.science.gtnl.utils.machine.bee.DronePool;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.RecipeMaps;

@Mod(modid = "beebreederqa", name = "Bee Breeder QA", version = "1", dependencies = "after:sciencenotleisure")
public final class BeeBreederClientChecks {

    private final StringBuilder report = new StringBuilder();
    private BaseMetaTileEntity tile;
    private LargeBeeBreeder machine;
    private MTEHatchInputBus input;
    private String forest;
    private String meadows;
    private String common;
    private String cultivated;
    private int ticks;
    private int frames;
    private int stage;
    private int clientTicks;
    private boolean launched;
    private boolean startupCaptured;
    private volatile boolean opened;
    private volatile boolean ready;
    private volatile boolean finished;
    private volatile boolean archiveCommon;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (Boolean.getBoolean("gtnl.beeBreeder.client.qa")) FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void client(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (finished) {
            mc.shutdown();
            return;
        }
        try {
            if (++clientTicks > 4000) throw new AssertionError("client timeout at stage " + stage);
            if (clientTicks == 100) {
                Files.writeString(
                    new File(output(), "startup-screen.txt").toPath(),
                    mc.currentScreen == null ? "none" : mc.currentScreen.getClass().getName());
            }
            mc.gameSettings.pauseOnLostFocus = false;
            mc.gameSettings.setSoundLevel(SoundCategory.MASTER, 0);
            mc.gameSettings.setSoundLevel(SoundCategory.MUSIC, 0);
            mc.gameSettings.renderDistanceChunks = 3;
            mc.gameSettings.limitFramerate = 60;
            if (mc.thePlayer != null) mc.ingameGUI.getChatGUI()
                .clearChatMessages();
            ready = mc.theWorld != null && mc.theWorld.getTileEntity(0, 10, 0) instanceof BaseMetaTileEntity base
                && base.getMetaTileEntity() instanceof LargeBeeBreeder;
            boolean mainMenu = mc.currentScreen instanceof GuiMainMenu || mc.currentScreen != null
                && "galaxyspace.core.gui.GSGuiMainMenu".equals(mc.currentScreen.getClass().getName());
            if (!launched && mc.theWorld == null && mainMenu) {
                launched = true;
                mc.launchIntegratedServer(
                    "bee-breeder-" + System.currentTimeMillis(),
                    "Bee Breeder QA",
                    new WorldSettings(746L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT));
            }
        } catch (Throwable error) {
            fail(error);
        }
    }

    @SubscribeEvent
    public void server(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || finished) return;
        var server = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        if (server == null || server.getConfigurationManager().playerEntityList.isEmpty()) return;
        try {
            EntityPlayerMP player = (EntityPlayerMP) server.getConfigurationManager().playerEntityList.get(0);
            if (++ticks == 1) setup(player);
            if (archiveCommon) {
                archiveCommon = false;
                if (!machine.mInputBusses.contains(input)) machine.mInputBusses.add(input);
                input.setInventorySlotContents(0, BeeBreedingHelper.createDrone(common));
                machine.checkProcessing();
            }
            if (ticks >= 70 && ready && !opened) {
                player.playerNetServerHandler.setPlayerLocation(0.5, 11, -3, 0, 10);
                require(machine.onRightclick(tile, player), "native right-click opens the MUI2 machine GUI");
                opened = true;
            }
        } catch (Throwable error) {
            fail(error);
        }
    }

    private void setup(EntityPlayerMP player) throws Exception {
        forest = BeeBreedingHelper.getSpeciesByName("Forest")
            .getUID();
        meadows = BeeBreedingHelper.getSpeciesByName("Meadows")
            .getUID();
        common = BeeBreedingHelper.getSpeciesByName("Common")
            .getUID();
        cultivated = BeeBreedingHelper.getSpeciesByName("Cultivated")
            .getUID();
        require(
            BeeBreedingPlanner.plan(cultivated, Set.of(forest, meadows))
                .getMissingSpecies()
                .isEmpty(),
            "Forestry multi-step route is reachable from the base parents");
        require(
            !BeeBreedingPlanner.plan(common, Set.of(forest))
                .getMissingSpecies()
                .isEmpty(),
            "missing root parent is reported");
        require(
            BeeBreedingPlanner.plan(common, Set.of(common))
                .getSteps()
                .isEmpty(),
            "archived targets need no mutations");
        List<String> reachable = BeeBreedingPlanner.findReachableSpecies(Set.of(forest, meadows));
        require(
            reachable.contains(common) && reachable.contains(cultivated),
            "reachable species include direct and multi-step descendants");
        require(!reachable.contains(forest) && !reachable.contains(meadows), "reachable species exclude the archive");
        require(
            BeeBreedingPlanner.findReachableSpecies(Set.of())
                .isEmpty(),
            "empty archive has no reachable species");
        require(
            !BeeBreedingPlanner.findReachableSpecies(Set.of(forest))
                .contains(common),
            "missing parent cannot produce a reachable target");
        require(
            !BeeBreedingPlanner.findReachableSpecies(Set.of(forest, meadows, common))
                .contains(common),
            "newly archived species leave the reachable list");
        for (String uid : reachable) {
            require(
                BeeBreedingPlanner.plan(uid, Set.of(forest, meadows))
                    .getMissingSpecies()
                    .isEmpty(),
                "reachable candidate has a complete plan: " + uid);
        }
        DronePool pool = new DronePool();
        pool.unlockSpecies(forest);
        pool.unlockSpecies(meadows);
        require(
            DronePool.fromNBT(pool.toNBT())
                .getAvailableSpecies()
                .equals(pool.getAvailableSpecies()),
            "archive NBT roundtrip");
        require(
            RecipeMaps.assemblerRecipes.getAllRecipes()
                .stream()
                .anyMatch(recipe -> {
                    for (ItemStack stack : recipe.mOutputs)
                        if (stack != null && stack.isItemEqual(GTNLItemList.LargeBeeBreeder.get(1))) return true;
                    return false;
                }),
            "controller assembler recipe is registered");

        tile = place(player, 0, GTNLMachineID.LARGE_BEE_BREEDER.ID);
        machine = (LargeBeeBreeder) tile.getMetaTileEntity();
        machine.fixAllIssues();
        BaseMetaTileEntity inputTile = place(
            player,
            2,
            ItemList.Hatch_Input_Bus_MV.get(1)
                .getItemDamage());
        input = (MTEHatchInputBus) inputTile.getMetaTileEntity();
        machine.mInputBusses.add(input);
        input.setInventorySlotContents(0, BeeBreedingHelper.createDrone(forest));
        input.setInventorySlotContents(1, BeeBreedingHelper.createPrincess(meadows));
        machine.setTargetBeeSpecies(common);
        require(
            machine.checkProcessing()
                .wasSuccessful(),
            "actual input bus starts breeding");
        require(
            machine.getPendingPrincessOutputs() == 1 && machine.getDronePool()
                .hasDrone(forest)
                && machine.getDronePool()
                    .hasDrone(meadows),
            "input bees consumed once into archive and pending output");
        machine.checkProcessing();
        require(machine.getPendingPrincessOutputs() == 1, "zero-size input stacks do not duplicate pending output");
        require(
            !machine.onRunningTick(null) && !machine.getDronePool()
                .hasDrone(common),
            "no energy does not settle a breeding attempt");

        NBTTagCompound saved = new NBTTagCompound();
        machine.saveNBTData(saved);
        LargeBeeBreeder restored = (LargeBeeBreeder) place(player, 4, GTNLMachineID.LARGE_BEE_BREEDER.ID)
            .getMetaTileEntity();
        restored.loadNBTData(saved);
        require(
            restored.getPendingPrincessOutputs() == 1 && restored.getDronePool()
                .hasDrone(forest)
                && restored.getTargetBeeSpecies()
                    .equals(common),
            "controller NBT restores target, archive and princess credits");
        machine.setTargetBeeSpecies("not_a_real_species");
        require(
            !machine.isTargetInputValid() && machine.getTargetBeeSpecies()
                .equals(common),
            "invalid target preserves previous selection");

        machine.getDronePool()
            .unlockSpecies(common);
        machine.setTargetBeeSpecies(common);
        require(
            machine.checkProcessing()
                .wasSuccessful() && machine.mOutputItems.length == 2,
            "archived target prepares princess and drone outputs");
        require(machine.getPendingPrincessOutputs() == 1, "princess credit remains until recipe completion");
        MTEHatchOutputBus output = (MTEHatchOutputBus) place(
            player,
            6,
            ItemList.Hatch_Output_Bus_MV.get(1).getItemDamage()).getMetaTileEntity();
        machine.mOutputBusses.add(output);
        machine.mProgresstime = machine.mMaxProgresstime - 1;
        machine.runActiveMachineTick(tile);
        require(
            machine.getPendingPrincessOutputs() == 0 && machine.mMaxProgresstime == 0,
            "GTNL active tick finishes the cycle and consumes one princess credit");
        int princesses = 0;
        int drones = 0;
        for (int slot = 0; slot < output.getSizeInventory(); slot++) {
            ItemStack stack = output.getStackInSlot(slot);
            if (BeeBreedingHelper.isPrincess(stack)) princesses += stack.stackSize;
            if (BeeBreedingHelper.isDrone(stack)) drones += stack.stackSize;
        }
        require(princesses == 1 && drones == 1, "GTNL output flush delivers one princess and one drone");
        Method completion = LargeBeeBreeder.class.getDeclaredMethod("outputAfterRecipe");
        completion.setAccessible(true);
        completion.invoke(machine);
        require(machine.getPendingPrincessOutputs() == 0, "completion is idempotent");

        // Restore a populated, unfinished route for visual and interaction checks.
        machine.loadNBTData(saved);
        machine.setTargetBeeSpecies(cultivated);
        machine.mProgresstime = 0;
        machine.mMaxProgresstime = 0;
        machine.mOutputItems = null;
        machine.createTooltip();
        require(true, "automatic structure tooltip accepts the custom controller");
        player.capabilities.allowFlying = true;
        player.capabilities.isFlying = true;
        player.sendPlayerAbilities();
        player.playerNetServerHandler.setPlayerLocation(0.5, 11, -3, 0, 10);
        Files.writeString(new File(output(), "core-result.txt").toPath(), "PASS\n" + report);
    }

    private BaseMetaTileEntity place(EntityPlayerMP player, int x, int id) {
        player.worldObj.setBlock(x, 10, 0, GregTechAPI.sBlockMachines, 0, 3);
        BaseMetaTileEntity base = (BaseMetaTileEntity) player.worldObj.getTileEntity(x, 10, 0);
        base.setInitialValuesAsNBT(null, (short) id);
        base.setFrontFacing(ForgeDirection.NORTH);
        base.setOwnerName(player.getCommandSenderName());
        base.setOwnerUuid(player.getUniqueID());
        base.disableWorking();
        return base;
    }

    @SubscribeEvent
    public void render(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || finished) return;
        try {
            if (!opened) {
                if (clientTicks >= 100 && !startupCaptured) {
                    capture("startup.png");
                    startupCaptured = true;
                }
                return;
            }
            Minecraft mc = Minecraft.getMinecraft();
            if (!(mc.currentScreen instanceof GuiContainerWrapper wrapper) || ++frames < 45) return;
            frames = 0;
            var panel = wrapper.getScreen()
                .getMainPanel();
            var sync = wrapper.getScreen()
                .getSyncManager()
                .getMainPSM();
            PagedWidget<?> pages = collect(panel, PagedWidget.class).get(0);
            List<PageButton> tabs = collect(panel, PageButton.class);
            require(
                tabs.size() == 4 && pages.getPages()
                    .size() == 4,
                "four MUI2 pages");
            switch (stage++) {
                case 0 -> {
                    require(panel.getArea().width == 360 && panel.getArea().height == 300, "panel bounds");
                    Files.writeString(new File(output(), "operation-layout.txt").toPath(), layout(panel, ""));
                    capture("bee-operation.png");
                    tabs.get(1)
                        .onMousePressed(0);
                }
                case 1 -> {
                    require(pages.getCurrentPageIndex() == 1, "archive tab switches locally");
                    capture("bee-archive.png");
                    search(pages, forest);
                }
                case 2 -> {
                    capture("bee-archive-search.png");
                    collect(pages.getCurrentPage(), ButtonWidget.class).get(0)
                        .onMousePressed(0);
                }
                case 3 -> {
                    require(pages.getCurrentPageIndex() == 0, "selecting archived species returns to operation");
                    require(
                        sync.findSyncHandler("beeDraft", StringSyncValue.class)
                            .getStringValue()
                            .equals(forest),
                        "archive selection fills UID draft");
                    require(
                        machine.getTargetBeeSpecies()
                            .equals(cultivated),
                        "draft selection does not alter machine target");
                    sync.findSyncHandler("beeDraft", StringSyncValue.class)
                        .setStringValue("not_a_real_species");
                }
                case 4 -> apply(pages);
                case 5 -> {
                    require(
                        !sync.findSyncHandler("beeValid", BooleanSyncValue.class)
                            .getBoolValue(),
                        "invalid target feedback reaches client");
                    require(
                        machine.getTargetBeeSpecies()
                            .equals(cultivated),
                        "invalid GUI target rejected on server");
                    capture("bee-invalid-target.png");
                    IWidget field = collect(pages.getCurrentPage(), TextFieldWidget.class).get(0);
                    @SuppressWarnings("unchecked")
                    RecipeViewerGhostIngredientSlot<ItemStack> drop = (RecipeViewerGhostIngredientSlot<ItemStack>) field;
                    require(
                        drop.handleDragAndDrop(BeeBreedingHelper.createDrone(common), 0),
                        "NEI bee ghost drag accepted");
                }
                case 6 -> {
                    require(
                        sync.findSyncHandler("beeDraft", StringSyncValue.class)
                            .getStringValue()
                            .equals(common),
                        "NEI drag keeps species UID");
                    apply(pages);
                }
                case 7 -> {
                    require(
                        machine.getTargetBeeSpecies()
                            .equals(common),
                        "Apply confirms target on server");
                    tabs.get(3)
                        .onMousePressed(0);
                }
                case 8 -> {
                    require(pages.getCurrentPageIndex() == 3, "route tab switches");
                    capture("bee-route.png");
                    tabs.get(0)
                        .onMousePressed(0);
                    sync.findSyncHandler("beeDraft", StringSyncValue.class)
                        .setStringValue(BeeBreedingHelper.getSpeciesDisplayName(cultivated));
                }
                case 9 -> apply(pages);
                case 10 -> {
                    require(
                        machine.getTargetBeeSpecies()
                            .equals(cultivated),
                        "localized name resolves to canonical UID before server confirmation");
                    tabs.get(3)
                        .onMousePressed(0);
                }
                case 11 -> {
                    capture("bee-route-multistep.png");
                    Files.writeString(new File(output(), "route-layout.txt").toPath(), layout(panel, ""));
                    tabs.get(2)
                        .onMousePressed(0);
                }
                case 12 -> {
                    require(pages.getCurrentPageIndex() == 2, "reachable tab switches");
                    List<?> candidates = sync.findSyncHandler("beeReachable", GenericListSyncHandler.class)
                        .getValue();
                    require(
                        candidates.contains(common) && candidates.contains(cultivated)
                            && !candidates.contains(forest)
                            && !candidates.contains(meadows),
                        "server synchronizes reachable but unarchived species");
                    capture("bee-reachable.png");
                    search(pages, common);
                }
                case 13 -> {
                    capture("bee-reachable-search.png");
                    collect(pages.getCurrentPage(), ButtonWidget.class).get(0)
                        .onMousePressed(0);
                }
                case 14 -> {
                    require(
                        pages.getCurrentPageIndex() == 0 && sync.findSyncHandler("beeDraft", StringSyncValue.class)
                            .getStringValue()
                            .equals(common),
                        "reachable search and selection fill the target draft");
                    require(
                        machine.getTargetBeeSpecies()
                            .equals(cultivated),
                        "reachable selection waits for Apply");
                    tabs.get(2)
                        .onMousePressed(0);
                    search(pages, "");
                    archiveCommon = true;
                }
                case 15 -> {
                    List<?> candidates = sync.findSyncHandler("beeReachable", GenericListSyncHandler.class)
                        .getValue();
                    List<?> archived = sync.findSyncHandler("beeArchive", GenericListSyncHandler.class)
                        .getValue();
                    require(
                        archived.contains(common) && !candidates.contains(common) && candidates.contains(cultivated),
                        "input bus archive update removes only the unlocked candidate from the live GUI");
                    capture("bee-reachable-updated.png");
                    Files.writeString(new File(output(), "client-result.txt").toPath(), "PASS\n" + report);
                    finished = true;
                }
            }
        } catch (Throwable error) {
            fail(error);
        }
    }

    private static void apply(PagedWidget<?> pages) {
        collect(pages.getCurrentPage(), ButtonWidget.class).get(0)
            .onMousePressed(0);
    }

    private static void search(PagedWidget<?> pages, String text) throws Exception {
        TextFieldWidget search = collect(pages.getCurrentPage(), TextFieldWidget.class).get(0);
        search.setText(text);
        Method changed = TextFieldWidget.class.getDeclaredMethod("onTextChanged");
        changed.setAccessible(true);
        changed.invoke(search);
    }

    private static <T> List<T> collect(IWidget widget, Class<T> type) {
        List<T> result = new ArrayList<>();
        if (type.isInstance(widget)) result.add(type.cast(widget));
        for (IWidget child : widget.getChildren()) result.addAll(collect(child, type));
        return result;
    }

    private static String layout(IWidget widget, String indent) {
        StringBuilder result = new StringBuilder(indent).append(
            widget.getClass()
                .getSimpleName())
            .append(" enabled=")
            .append(widget.isEnabled())
            .append(" ")
            .append(widget.getArea())
            .append('\n');
        for (IWidget child : widget.getChildren()) result.append(layout(child, indent + "  "));
        return result.toString();
    }

    private void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        report.append(message)
            .append('\n');
    }

    private static File output() {
        File output = new File(System.getProperty("gtnl.beeBreeder.client.qa.output"));
        output.mkdirs();
        return output;
    }

    private static void capture(String name) {
        Minecraft mc = Minecraft.getMinecraft();
        ScreenShotHelper.saveScreenshot(output(), name, mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
    }

    private void fail(Throwable error) {
        if (finished) return;
        error.printStackTrace();
        try {
            StringWriter trace = new StringWriter();
            error.printStackTrace(new PrintWriter(trace));
            Files.writeString(new File(output(), "client-result.txt").toPath(), "FAIL\n" + trace + "\n" + report);
        } catch (Exception ignored) {}
        finished = true;
    }
}
