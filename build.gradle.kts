plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

dependencies {
    // The game supplies these libraries; standalone listener tests need their runtime classes explicitly.
    testRuntimeOnly("it.unimi.dsi:fastutil:8.5.18")
    testRuntimeOnly("org.joml:joml:1.10.8") { isTransitive = false }
}

minecraft {
    extraRunJvmArguments.addAll("-Xmx8G", "-Xms8G", "-Dgtnhlib.dumpkeys=true")
}

tasks.withType<JavaCompile>().configureEach {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}

// Wireless regressions are executable main classes wired into check below, not JUnit tests.
// Gradle 9 otherwise fails before running them because the standard test task discovers no tests.
tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests.set(false)
}

fun registerWirelessTest(name: String, testClass: String): TaskProvider<JavaExec> {
    val task = tasks.register<JavaExec>(name) {
        group = "verification"
        description = "Runs $testClass regression checks."
        dependsOn(tasks.named("testClasses"))
        classpath = sourceSets["test"].runtimeClasspath
        mainClass.set(testClass)
    }
    tasks.named("check") { dependsOn(task) }
    return task
}

mapOf(
    "wirelessChannelPolicyTest" to "ChannelBudgetTest",
    "wirelessCardBindingTest" to "WirelessCardBindingTest",
    "wirelessClusterTopologyTest" to "PhysicalClusterTrackerTest",
    "wirelessEntrancePlannerTest" to "AutomaticEntrancePlannerTest",
    "wirelessLinkPersistenceTest" to "WirelessLinkPersistenceTest",
    "wirelessCardSelectionTest" to "WirelessCardSelectionTest",
    "wirelessCardInventoryTest" to "WirelessCardInventoryTest",
    "wirelessEventTest" to "WirelessEventTest",
    "wirelessNetworkStatisticsTest" to "WirelessNetworkStatisticsTest",
    "wirelessViewFilterTest" to "WirelessViewFilterTest",
    "wirelessTeleportTest" to "WirelessTeleportLandingTest"
).forEach { (name, testClass) ->
    registerWirelessTest(name, "com.science.gtnl.common.wireless.$testClass")
}

registerWirelessTest("wirelessMixinBootstrapTest", "com.science.gtnl.wirelesstest.WirelessMixinBootstrapTest").configure {
    description = "Applies the wireless mixins to the pinned AE2 and Minecraft classes without launching the game."
    dependsOn(tasks.named("downgradeMainClasses"))
    classpath = files(layout.buildDirectory.dir("tmp/downgradeMainClasses/main")) + sourceSets["test"].runtimeClasspath
    workingDir = layout.buildDirectory.dir("wireless-mixin-test").get().asFile
    doFirst { workingDir.mkdirs() }
}

val runConfigs = listOf(
    "runClient" to "run/client",
    "runClient17" to "run/client_new",
    "runClient21" to "run/client_new",
    "runClient25" to "run/client_new",
    "runServer" to "run/server",
    "runServer17" to "run/server_new",
    "runServer21" to "run/server_new",
    "runServer25" to "run/server_new"
)

runConfigs.forEach { (taskName, path) ->
    tasks.named<JavaExec>(taskName) {
        workingDir = file("${projectDir}/$path")
        doFirst {
            workingDir.mkdirs()
        }
    }
}
