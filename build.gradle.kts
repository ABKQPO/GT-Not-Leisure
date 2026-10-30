plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

minecraft {
    extraRunJvmArguments.addAll("-Xmx8G", "-Xms8G", "-Dgtnhlib.dumpkeys=true")
}

tasks.withType<JavaCompile>().configureEach {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}

sourceSets.test {
    compileClasspath += sourceSets.main.get().compileClasspath
}

tasks.register<JavaExec>("verifyCelestweave") {
    group = "verification"
    description = "Verify Celestweave data rules, packets, model, sound and lang resources without starting Minecraft"
    dependsOn(tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath + sourceSets.main.get().runtimeClasspath +
        sourceSets.main.get().compileClasspath
    mainClass.set("com.science.gtnl.client.celestweave.CelestweaveContractTest")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

tasks.register<JavaExec>("benchEquipment") {
    group = "verification"
    description = "Measure per-call cost and allocation of railgun and Celestweave data access, packet and NBT sizes"
    dependsOn(tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath + sourceSets.main.get().runtimeClasspath +
        sourceSets.main.get().compileClasspath
    mainClass.set("com.science.gtnl.client.celestweave.EquipmentPerfBench")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

tasks.register<JavaExec>("verifyGrandAssemblyLine") {
    group = "verification"
    description = "Verify Grand Assembly Line overclocking and large energy settlement without starting Minecraft"
    dependsOn(tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath + sourceSets.main.get().runtimeClasspath +
        sourceSets.main.get().compileClasspath
    mainClass.set("com.science.gtnl.common.machine.multiblock.GrandAssemblyLineOverclockContracts")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

tasks.named<Test>("test") {
    failOnNoDiscoveredTests.set(false)
    dependsOn("verifyCelestweave")
    dependsOn("verifyGrandAssemblyLine")
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
