package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cleanroommc.modularui.screen.ModularScreen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

/** Native containers and GL frames in an isolated integrated server. No physical input or TCP claim. */
@SideOnly(Side.CLIENT)
public final class ClientRuntimeTests extends RuntimeProxy {

    private enum Stage {
        MENU,
        WORLD,
        PREPARE,
        WAIT_TILE,
        OPEN,
        SCREEN,
        CLOSE,
        FINISH
    }

    private static final Logger LOG = LogManager.getLogger("GTNLClientTests");
    private final ConcurrentLinkedQueue<Runnable> serverWork = new ConcurrentLinkedQueue<>();
    private Path destination;
    private RuntimeReport report;
    private List<MachinePersistenceScenario> machines;
    private RuntimeFixture fixture;
    private Stage stage = Stage.MENU;
    private CompletableFuture<?> pending;
    private int index, elapsed, frameCount, expectedWindow;
    private GuiScreen observed;
    private boolean enabled;

    @Override
    public void initialize(Path destination) {
        if (!"client".equals(System.getenv("GTNL_TEST_PHASE"))) return;
        this.destination = destination;
        report = new RuntimeReport();
        machines = RuntimeCatalog.machines();
        String selected = System.getenv("GTNL_TEST_CASE");
        if (selected != null && !selected.isEmpty()) machines.removeIf(
            machine -> !selected.equals(
                machine.id()
                    .replace(".persistence", ".gui")));
        require(!machines.isEmpty(), "GUI scenario selection must not be empty");
        for (MachinePersistenceScenario machine : machines) {
            report.results.add(new RuntimeReport.Result(new RuntimeScenario() {

                public String id() {
                    return machine.id()
                        .replace(".persistence", ".gui");
                }

                public String scope() {
                    return "unformed-machine-native-gui-lifecycle";
                }

                public boolean tick(RuntimeFixture ignored, int ticks) {
                    return true;
                }
            }));
        }
        enabled = true;
        save();
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Runnable work;
        while ((work = serverWork.poll()) != null) work.run();
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (enabled && stage == Stage.SCREEN
            && event.phase == TickEvent.Phase.END
            && observed != null
            && Minecraft.getMinecraft().currentScreen == observed) frameCount++;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (!enabled || event.phase != TickEvent.Phase.END) return;
        Minecraft client = Minecraft.getMinecraft();
        elapsed++;
        if (index < report.results.size()) report.results.get(index).ticks++;
        try {
            switch (stage) {
                case MENU -> {
                    GuiScreen screen = client.currentScreen;
                    if (screen instanceof GuiMainMenu || (screen != null && screen.getClass()
                        .getName()
                        .equals("galaxyspace.core.gui.GSGuiMainMenu"))) {
                        client.gameSettings.guiScale = 2;
                        client.gameSettings.pauseOnLostFocus = false;
                        client.gameSettings.renderDistanceChunks = 3;
                        stage = Stage.WORLD;
                        elapsed = 0;
                        client.launchIntegratedServer(
                            "GTNLTestWorld",
                            "GTNLTestWorld",
                            new WorldSettings(663L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT));
                    } else require(elapsed < 2400, "client never reached a supported main menu");
                }
                case WORLD -> {
                    if (client.theWorld != null && client.thePlayer != null) beginNext();
                    else require(elapsed < 2400, "client did not enter the generated test world");
                }
                case PREPARE -> {
                    require(elapsed < 300, "server did not prepare the GUI fixture");
                    if (pending.isDone()) {
                        Opened opened = (Opened) pending.join();
                        RuntimeReport.Result result = report.results.get(index);
                        if (opened.skip != null) {
                            result.status = RuntimeReport.Status.SKIP;
                            result.detail = opened.skip;
                            completeCase(client);
                        } else {
                            stage = Stage.WAIT_TILE;
                            elapsed = 0;
                        }
                    }
                }
                case WAIT_TILE -> {
                    require(elapsed < 300, "placed machine did not synchronize to the client");
                    if (elapsed >= 10
                        && client.theWorld.getTileEntity(
                            RuntimeFixture.X,
                            RuntimeFixture.Y,
                            RuntimeFixture.Z) instanceof IGregTechTileEntity tile
                        && tile.getMetaTileID() == machines.get(index)
                            .metaId()
                        && tile.getMetaTileEntity() != null) {
                        pending = submit(this::open);
                        stage = Stage.OPEN;
                        elapsed = 0;
                    }
                }
                case OPEN -> {
                    require(elapsed < 200, "server did not activate the synchronized machine");
                    if (pending.isDone()) {
                        Opened opened = (Opened) pending.join();
                        RuntimeReport.Result result = report.results.get(index);
                        if (opened.skip != null) {
                            result.status = RuntimeReport.Status.SKIP;
                            result.detail = opened.skip;
                            completeCase(client);
                        } else {
                            expectedWindow = opened.windowId;
                            result.windowId = expectedWindow;
                            stage = Stage.SCREEN;
                            elapsed = 0;
                            observed = null;
                            frameCount = 0;
                        }
                    }
                }
                case SCREEN -> {
                    require(
                        elapsed < 200,
                        "server opened window " + expectedWindow + " but the client never rendered its container");
                    if (client.currentScreen instanceof GuiContainer screen
                        && screen.inventorySlots.windowId == expectedWindow) {
                        if (observed != screen) {
                            observed = screen;
                            frameCount = 0;
                        }
                        if (elapsed >= 12 && frameCount >= 4) captureAndClose(client, screen);
                    }
                }
                case CLOSE -> {
                    require(elapsed < 100, "server container did not close after the client close packet");
                    if (pending.isDone()) {
                        if ((Boolean) pending.join()) {
                            RuntimeReport.Result result = report.results.get(index);
                            result.status = RuntimeReport.Status.PASS;
                            result.detail += "; server confirmed close";
                            completeCase(client);
                        } else pending = submit(() -> player().openContainer == player().inventoryContainer);
                    }
                }
                case FINISH -> finish(client);
            }
        } catch (Exception | AssertionError | LinkageError error) {
            RuntimeReport.Result result = report.results.get(Math.min(index, report.results.size() - 1));
            result.status = RuntimeReport.Status.FAIL;
            result.detail = error.toString();
            LOG.error("GTNL client scenario failed: {}", result.id, error);
            if (stage == Stage.MENU || stage == Stage.WORLD) finish(client);
            else completeCase(client);
        }
    }

    private void captureAndClose(Minecraft client, GuiContainer screen) throws Exception {
        require(screen.width > 0 && screen.height > 0, "GUI has no drawable viewport");
        ModularScreen mui = ModularScreen.getCurrent();
        if (mui != null) {
            var area = mui.getMainPanel()
                .getArea();
            require(area.width > 0 && area.height > 0, "MUI2 main panel has no area");
            require(
                area.x >= 0 && area.y >= 0
                    && area.x + area.width <= screen.width
                    && area.y + area.height <= screen.height,
                "MUI2 main panel is clipped: " + area + " viewport=" + screen.width + "x" + screen.height);
        }
        RuntimeReport.Result result = report.results.get(index);
        String filename = String.format(java.util.Locale.ROOT, "%04d-%s.png", index, result.id);
        ScreenShotHelper.saveScreenshot(
            destination.getParent()
                .toFile(),
            filename,
            client.displayWidth,
            client.displayHeight,
            client.getFramebuffer());
        Path screenshot = destination.getParent()
            .resolve("screenshots")
            .resolve(filename);
        require(Files.isRegularFile(screenshot) && Files.size(screenshot) > 0, "no rendered screenshot was saved");
        result.screenshot = "screenshots/" + filename;
        result.screenClass = screen.getClass()
            .getName();
        result.detail = (mui == null ? "legacy/native" : "MUI2")
            + " window rendered; awaiting server close acknowledgement";
        client.thePlayer.closeScreen();
        stage = Stage.CLOSE;
        elapsed = 0;
        pending = submit(() -> player().openContainer == player().inventoryContainer);
    }

    private void beginNext() {
        if (index == machines.size()) {
            stage = Stage.FINISH;
            return;
        }
        report.results.get(index).status = RuntimeReport.Status.RUNNING;
        save();
        MachinePersistenceScenario machine = machines.get(index);
        pending = submit(() -> prepare(machine));
        stage = Stage.PREPARE;
        elapsed = 0;
    }

    private Opened prepare(MachinePersistenceScenario machine) {
        String skip = RuntimeCatalog.skipReason(machine);
        if (skip != null) return new Opened(0, skip);
        EntityPlayerMP player = player();
        WorldServer world = (WorldServer) player.worldObj;
        if (fixture == null) fixture = new RuntimeFixture(world);
        fixture.close();
        fixture.actor = player;
        player.closeContainer();
        for (int dx = -2; dx <= 2; dx++) for (int dz = 1; dz <= 5; dz++)
            world.setBlock(RuntimeFixture.X + dx, RuntimeFixture.Y - 1, RuntimeFixture.Z + dz, Blocks.stone);
        player.setPositionAndUpdate(RuntimeFixture.X + 0.5, RuntimeFixture.Y, RuntimeFixture.Z + 3.5);
        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        fixture.machine(machine.metaId(), 0);
        return new Opened(0, null);
    }

    private Opened open() {
        EntityPlayerMP player = player();
        WorldServer world = (WorldServer) player.worldObj;
        IGregTechTileEntity tile = (IGregTechTileEntity) world
            .getTileEntity(RuntimeFixture.X, RuntimeFixture.Y, RuntimeFixture.Z);
        require(tile.getTimer() > 5, "placed machine has not completed native initialization");
        world.getBlock(RuntimeFixture.X, RuntimeFixture.Y, RuntimeFixture.Z)
            .onBlockActivated(
                world,
                RuntimeFixture.X,
                RuntimeFixture.Y,
                RuntimeFixture.Z,
                player,
                tile.getFrontFacing()
                    .ordinal(),
                0.5F,
                0.5F,
                0.5F);
        if (player.openContainer == player.inventoryContainer) {
            require(
                !(tile.getMetaTileEntity() instanceof MTEMultiBlockBase),
                "multiblock controller did not open its required GUI");
            return new Opened(0, "This registered machine exposes no server container on activation");
        }
        return new Opened(player.openContainer.windowId, null);
    }

    private EntityPlayerMP player() {
        List<?> players = MinecraftServer.getServer()
            .getConfigurationManager().playerEntityList;
        require(players.size() == 1, "GUI test requires exactly its own integrated-server player");
        return (EntityPlayerMP) players.get(0);
    }

    private <T> CompletableFuture<T> submit(Supplier<T> action) {
        CompletableFuture<T> result = new CompletableFuture<>();
        serverWork.add(() -> {
            try {
                result.complete(action.get());
            } catch (Exception | AssertionError | LinkageError error) {
                result.completeExceptionally(error);
            }
        });
        return result;
    }

    private void completeCase(Minecraft client) {
        RuntimeReport.Result result = report.results.get(index);
        LOG.info("GTNL_GUI_CASE {} {} {}", result.id, result.status, result.detail);
        save();
        if (client.thePlayer != null) client.thePlayer.closeScreen();
        index++;
        beginNext();
    }

    private void finish(Minecraft client) {
        enabled = false;
        report.state = report.results.stream()
            .allMatch(r -> r.status == RuntimeReport.Status.PASS || r.status == RuntimeReport.Status.SKIP)
            && report.results.stream()
                .anyMatch(r -> r.status == RuntimeReport.Status.PASS) ? "PASS" : "FAIL";
        save();
        LOG.info(
            "GTNL_RUNTIME_COMPLETE run={} phase=client state={} scenarios={}",
            report.runId,
            report.state,
            machines.size());
        if (client.theWorld != null) {
            client.theWorld.sendQuittingDisconnectingPacket();
            client.loadWorld(null);
        }
        client.shutdown();
    }

    private void save() {
        try {
            report.write(destination);
        } catch (Exception error) {
            throw new IllegalStateException("Cannot save GUI test evidence", error);
        }
    }

    private record Opened(int windowId, String skip) {}
}
