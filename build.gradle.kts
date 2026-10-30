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

val contractSuites = mapOf(
    "verifyCelestweave" to "com.science.gtnl.client.celestweave.CelestweaveContractTest",
    "verifyGTNLLib" to "com.gtnllib.GTNLLibContractTest",
    "verifyArchitecture" to "com.science.gtnl.ArchitectureContractTest",
    "verifyMachines" to "com.science.gtnl.MachineContractTest",
    "verifyWireless" to "com.science.gtnl.WirelessContractTest"
)

contractSuites.forEach { (taskName, entryPoint) ->
    tasks.register<JavaExec>(taskName) {
        group = "verification"
        description = "Run the $taskName contract suite without starting Minecraft"
        dependsOn(tasks.testClasses)
        classpath = sourceSets.test.get().runtimeClasspath + sourceSets.main.get().runtimeClasspath +
            sourceSets.main.get().compileClasspath
        mainClass.set(entryPoint)
        javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    }
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
    dependsOn(contractSuites.keys)
    dependsOn("verifyGrandAssemblyLine")
}

// Compile the embedded library without GTNL/GT classes or unrelated mod jars on its classpath.
// Minecraft/Forge are still the convention plugin's patched output; this is not a standalone release build.
val gtnllibBoundaryDependencies = setOf(
    "GTNHLib", "ModularUI2", "unimixins", "NotEnoughItems", "CodeChickenCore", "Angelica",
    // Public MUI2 signatures still inherit MUI1 handlers and expose these optional integration APIs.
    "ModularUI", "neverenoughanimation", "lwjgl3ify",
    "GTNHExtLib", "fastutil", "gson", "guava", "annotations", "lombok", "authlib", "netty-all",
    "lwjgl", "lwjgl_util", "asm", "asm-tree", "asm-commons", "log4j-api", "log4j-core",
    "launchwrapper", "jabel-stubs", "jvmdowngrader-java-api", "joml", "commons-lang3"
)

val compileGTNLLibBoundary = tasks.register<JavaCompile>("compileGTNLLibBoundary") {
    group = "verification"
    description = "Compile GTNLLib against its declared library and optional integration dependencies only"
    source(fileTree("src/main/java") { include("com/gtnllib/**/*.java") })
    val allowedDependencies = gtnllibBoundaryDependencies.toSet()
    classpath = sourceSets.main.get().compileClasspath.filter { dependency ->
        dependency.extension == "jar" && allowedDependencies.any { dependency.name.startsWith("$it-") }
    } + sourceSets["patchedMc"].output
    destinationDirectory.set(layout.buildDirectory.dir("classes/gtnllib-boundary"))
    javaCompiler.set(javaToolchains.compilerFor(java.toolchain))
    // The real build validates mixins and writes its refmap. This compile must not overwrite those outputs.
    options.compilerArgs.addAll(listOf("-processor", "lombok.launch.AnnotationProcessorHider\$AnnotationProcessor"))
}

tasks.named("check") {
    dependsOn(compileGTNLLibBoundary)
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
