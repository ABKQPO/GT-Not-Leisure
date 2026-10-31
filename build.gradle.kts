import org.gradle.jvm.tasks.Jar
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import com.gtnewhorizons.retrofuturagradle.util.IJarOutputTask

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

// RFG 2.0.6 shares mutable jar hashes between projects. Serialize only its jar chains,
// including onlyIf predicates; other compilation, tests and packaging remain parallel.
abstract class RfgJarChainLock : BuildService<BuildServiceParameters.None>
val rfgJarChainLock = gradle.sharedServices.registerIfAbsent("rfgJarChainLock", RfgJarChainLock::class) {
    maxParallelUsages.set(1)
}
allprojects {
    tasks.withType<JavaCompile>().configureEach {
        if (name == "compileJava") options.compilerArgs.addAll(listOf("-Xlint:deprecation,removal", "-Xmaxwarns", "10000"))
    }
    tasks.withType<IJarOutputTask>().configureEach {
        usesService(rfgJarChainLock)
    }
    // The modern run configuration inherits dependencies but not runtimeClasspath's attributes.
    // Request complete dev jars so project dependencies include both classes and mod resources.
    configurations.matching { it.name == "java17Dependencies" }.configureEach {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
        }
    }
}

abstract class ModuleArchiveArguments : CommandLineArgumentProvider {
    @get:InputFile
    abstract val host: RegularFileProperty

    @get:InputFiles
    abstract val library: ConfigurableFileCollection

    override fun asArguments(): Iterable<String> =
        listOf(host.get().asFile.absolutePath) + library.files.map { it.absolutePath }.sorted()
}

val clientSmoke = providers.environmentVariable("GTNL_CLIENT_SMOKE").isPresent
minecraft {
    extraRunJvmArguments.addAll(
        if (clientSmoke) "-Xmx4G" else "-Xmx8G",
        if (clientSmoke) "-Xms1G" else "-Xms8G",
        "-Dgtnhlib.dumpkeys=true"
    )
    if (clientSmoke) extraRunJvmArguments.add("-Dgtnhlib.debug.eventbus=true")
}

tasks.withType<JavaCompile>().configureEach {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}

sourceSets.test {
    compileClasspath += sourceSets.main.get().compileClasspath
}

val contractSuites = mapOf(
    "verifyCelestweave" to "com.science.gtnl.client.celestweave.CelestweaveContractTest",
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
    dependsOn("verifyGTNLLib")
}

// Keep the existing verification entry points while the library owns their implementation.
tasks.register("compileGTNLLibBoundary") {
    group = "verification"
    description = "Compile GTNLLib with its own dependencies, access transformer and mixin processor"
    dependsOn(":gtnllib:compileJava")
}

tasks.register("verifyGTNLLib") {
    group = "verification"
    dependsOn(":gtnllib:verifyGTNLLib")
}

tasks.named("check") {
    dependsOn(":gtnllib:check")
}

val libraryDistribution = configurations.create("libraryDistribution") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    api(project(":gtnllib"))
    testImplementation(testFixtures(project(":gtnllib")))
    libraryDistribution(project(path = ":gtnllib", configuration = "distribution"))
}

// Existing release workflows collect build/libs; include both mods without publishing test fixtures.
val collectLibraryArtifacts = tasks.register("collectLibraryArtifacts") {
    val archives = objects.fileCollection().from(libraryDistribution)
    val destination = layout.buildDirectory.dir("libs")
    inputs.files(archives)
    outputs.files(archives.elements.map { files -> files.map { destination.get().file(it.asFile.name) } })
    doLast {
        val directory = destination.get().asFile
        directory.mkdirs()
        archives.files.forEach { it.copyTo(directory.resolve(it.name), overwrite = true) }
    }
}

tasks.named("assemble") {
    dependsOn(collectLibraryArtifacts)
}

val verifyModuleArtifacts = tasks.register<JavaExec>("verifyModuleArtifacts") {
    group = "verification"
    description = "Verify independent mod metadata, coremods, refmaps and multi-release class files"
    dependsOn(tasks.testClasses, tasks.named("reobfJar"), libraryDistribution)
    classpath = sourceSets.test.get().runtimeClasspath + sourceSets.main.get().compileClasspath
    mainClass.set("com.science.gtnl.ModuleArtifactContracts")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    val archives = objects.newInstance<ModuleArchiveArguments>()
    archives.host.set(tasks.named<Jar>("reobfJar").flatMap { it.archiveFile })
    archives.library.from(libraryDistribution)
    argumentProviders.add(archives)
}

tasks.named("check") {
    dependsOn(verifyModuleArtifacts)
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

if (clientSmoke) {
    tasks.named<JavaExec>("runClient25") {
        args("--width", "960", "--height", "540")
    }
}

// Runtime-only smoke profiles leave the compilation API and ordinary dev launches unchanged.
val smokeScenario = providers.gradleProperty("gtnl.smokeScenario").orNull
if (smokeScenario != null) {
    require(smokeScenario in setOf("without-neenergistics", "quest-without-api")) { "Unknown smoke scenario: $smokeScenario" }
    val smokeQuest = configurations.create("smokeQuest") {
        isCanBeConsumed = false
        isTransitive = false
    }
    dependencies.add(smokeQuest.name, "com.github.GTNewHorizons:BetterQuesting:3.8.89-GTNH:dev")
    tasks.named<JavaExec>("runServer25") {
        val excludedArtifactPrefixes = if (smokeScenario == "without-neenergistics") listOf("NotEnoughEnergistics-")
            else listOf("BetterQuestingAPI-")
        classpath = classpath.filter { artifact ->
            excludedArtifactPrefixes.none { artifact.name.startsWith(it, ignoreCase = true) }
        }
        if (smokeScenario == "quest-without-api") classpath += smokeQuest
        workingDir = file("run/server-$smokeScenario")
        environment("GTNL_EXPECT_ABSENT_MODS", if (smokeScenario == "without-neenergistics") "neenergistics" else "bqapi")
        if (smokeScenario == "quest-without-api") environment("GTNL_EXPECT_PRESENT_MODS", "betterquesting")
    }
}
