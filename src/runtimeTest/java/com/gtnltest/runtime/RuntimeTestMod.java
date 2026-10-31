package com.gtnltest.runtime;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/** This mod is packaged only into build/test-mods, never into a release artifact. */
@Mod(
    modid = "gtnlruntimetests",
    name = "GTNL Runtime Tests",
    version = "1",
    dependencies = "required-after:sciencenotleisure")
public final class RuntimeTestMod {

    @cpw.mods.fml.common.SidedProxy(
        clientSide = "com.gtnltest.runtime.ClientRuntimeTests",
        serverSide = "com.gtnltest.runtime.RuntimeProxy")
    public static RuntimeProxy proxy;

    private static final Logger LOG = LogManager.getLogger("GTNLRuntimeTests");
    private RuntimeReport report;
    private Path destination;
    private List<RuntimeScenario> cases;
    private RuntimeFixture fixture;
    private int index, ticks;
    private boolean ready, started;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!"1".equals(System.getenv("GTNL_RUNTIME_TESTS")))
            throw new IllegalStateException("Runtime test mod is only for an explicitly owned test run");
        destination = Path.of(System.getenv("GTNL_TEST_REPORT"));
        report = new RuntimeReport();
        proxy.initialize(destination);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        if ("client".equals(report.phase)) return;
        cases = RuntimeCatalog.scenarios(report.phase);
        String selected = System.getenv("GTNL_TEST_CASE");
        if (selected != null && !selected.isEmpty()) cases.removeIf(scenario -> !selected.equals(scenario.id()));
        RuntimeReport.require(!cases.isEmpty(), "runtime scenario selection must not be empty");
        for (RuntimeScenario scenario : cases) report.results.add(new RuntimeReport.Result(scenario));
        fixture = new RuntimeFixture(
            MinecraftServer.getServer()
                .worldServerForDimension(0));
        save();
        ready = true;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (!ready || event.phase != TickEvent.Phase.END) return;
        if (index == cases.size()) {
            ready = false;
            report.state = report.results.stream()
                .anyMatch(
                    r -> r.status == RuntimeReport.Status.FAIL || r.status == RuntimeReport.Status.TIMEOUT
                        || r.status == RuntimeReport.Status.NOT_RUN) ? "FAIL" : "PASS";
            save();
            LOG.info(
                "GTNL_RUNTIME_COMPLETE run={} phase={} state={} scenarios={}",
                report.runId,
                report.phase,
                report.state,
                cases.size());
            MinecraftServer.getServer()
                .initiateShutdown();
            return;
        }
        RuntimeScenario scenario = cases.get(index);
        RuntimeReport.Result result = report.results.get(index);
        try {
            if (!started) {
                String skip = RuntimeCatalog.skipReason(scenario);
                if (skip != null) {
                    result.status = RuntimeReport.Status.SKIP;
                    result.detail = skip;
                    finish(scenario, result);
                    return;
                }
                result.status = RuntimeReport.Status.RUNNING;
                save();
                scenario.start(fixture);
                started = true;
            }
            result.ticks = ++ticks;
            if (ticks > scenario.timeoutTicks()) {
                result.status = RuntimeReport.Status.TIMEOUT;
                result.detail = "Scenario exceeded " + scenario.timeoutTicks() + " server ticks";
                finish(scenario, result);
            } else if (scenario.tick(fixture, ticks)) {
                result.status = RuntimeReport.Status.PASS;
                finish(scenario, result);
            }
        } catch (Exception | AssertionError | LinkageError error) {
            result.status = RuntimeReport.Status.FAIL;
            StringWriter trace = new StringWriter();
            error.printStackTrace(new PrintWriter(trace));
            result.detail = trace.toString();
            finish(scenario, result);
        }
    }

    private void finish(RuntimeScenario scenario, RuntimeReport.Result result) {
        try {
            scenario.close(fixture);
        } catch (Exception | AssertionError | LinkageError error) {
            result.status = RuntimeReport.Status.FAIL;
            result.detail += "\nScenario cleanup failed: " + error;
        }
        try {
            fixture.close();
        } catch (Exception | AssertionError | LinkageError error) {
            result.status = RuntimeReport.Status.FAIL;
            result.detail += "\nFixture cleanup failed: " + error;
        }
        LOG.info("GTNL_RUNTIME_CASE {} {} {}", result.id, result.status, result.detail);
        save();
        index++;
        ticks = 0;
        started = false;
    }

    @Mod.EventHandler
    public void serverStopped(cpw.mods.fml.common.event.FMLServerStoppedEvent event) {
        if (!ready) return;
        ready = false;
        report.state = "FAIL";
        if (index < report.results.size()) {
            RuntimeReport.Result current = report.results.get(index);
            current.status = RuntimeReport.Status.FAIL;
            current.detail += "\nServer stopped before the scenario completed; inspect its log and crash report";
        }
        save();
    }

    private void save() {
        try {
            report.write(destination);
        } catch (Exception error) {
            throw new IllegalStateException("Cannot persist runtime test evidence", error);
        }
    }
}
