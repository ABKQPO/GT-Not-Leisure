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

val wirelessChannelPolicyTest = tasks.register<JavaExec>("wirelessChannelPolicyTest") {
    group = "verification"
    description = "Checks wireless controller geometry and shared channel budget policy."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.ChannelBudgetTest")
}

val wirelessMixinBootstrapTest = tasks.register<JavaExec>("wirelessMixinBootstrapTest") {
    group = "verification"
    description = "Applies the wireless mixin to the pinned AE2 class without launching Minecraft."
    dependsOn(tasks.named("testClasses"), tasks.named("downgradeMainClasses"))
    classpath = files(layout.buildDirectory.dir("tmp/downgradeMainClasses/main")) + sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.wirelesstest.WirelessMixinBootstrapTest")
    workingDir = layout.buildDirectory.dir("wireless-mixin-test").get().asFile
    doFirst { workingDir.mkdirs() }
}

val wirelessCardBindingTest = tasks.register<JavaExec>("wirelessCardBindingTest") {
    group = "verification"
    description = "Checks frequency card saved NBT and owner identity."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessCardBindingTest")
}

val wirelessClusterTopologyTest = tasks.register<JavaExec>("wirelessClusterTopologyTest") {
    group = "verification"
    description = "Checks physical cluster split, merge, conflicting provenance and recovery."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.PhysicalClusterTrackerTest")
}

val wirelessEntrancePlannerTest = tasks.register<JavaExec>("wirelessEntrancePlannerTest") {
    group = "verification"
    description = "Checks automatic entrance feedback, queued pathing, budget gates and rollback."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.AutomaticEntrancePlannerTest")
}

val wirelessLinkPersistenceTest = tasks.register<JavaExec>("wirelessLinkPersistenceTest") {
    group = "verification"
    description = "Checks saved wireless node provenance, chunk reload, conflicts and unlink."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessLinkPersistenceTest")
}

tasks.named("check") {
    dependsOn(wirelessChannelPolicyTest, wirelessMixinBootstrapTest, wirelessCardBindingTest, wirelessClusterTopologyTest, wirelessEntrancePlannerTest, wirelessLinkPersistenceTest, "wirelessCardSelectionTest")
}

tasks.register<JavaExec>("wirelessCardSelectionTest") {
    group = "verification"
    description = "Checks automatic placement and toggle card selection and owner restrictions."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessCardSelectionTest")
}

val wirelessCardInventoryTest = tasks.register<JavaExec>("wirelessCardInventoryTest") {
    group = "verification"
    description = "Checks main and Baubles inventory stack identity for frequency cards."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessCardInventoryTest")
}

tasks.named("check") { dependsOn(wirelessCardInventoryTest) }

val wirelessEventTest = tasks.register<JavaExec>("wirelessEventTest") {
    group = "verification"
    description = "Checks event-driven topology scheduling and arbitrary-coordinate listener cleanup."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessEventTest")
}

tasks.named("check") { dependsOn(wirelessEventTest) }

val wirelessNetworkStatisticsTest = tasks.register<JavaExec>("wirelessNetworkStatisticsTest") {
    group = "verification"
    description = "Checks GUI device counts, power versus channel status and unsettled pathing."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessNetworkStatisticsTest")
}

tasks.named("check") { dependsOn(wirelessNetworkStatisticsTest) }

val wirelessViewFilterTest = tasks.register<JavaExec>("wirelessViewFilterTest") {
    group = "verification"
    description = "Checks combined name, coordinate, dimension and state filters."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessViewFilterTest")
}

tasks.named("check") { dependsOn(wirelessViewFilterTest) }

val wirelessTeleportTest = tasks.register<JavaExec>("wirelessTeleportTest") {
    group = "verification"
    description = "Checks bounded teleport landing search and rejection of unavailable landing space."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.science.gtnl.common.wireless.WirelessTeleportLandingTest")
}

tasks.named("check") { dependsOn(wirelessTeleportTest) }

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
