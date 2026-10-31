import org.gradle.jvm.tasks.Jar
import java.io.File
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
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val host: RegularFileProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val library: ConfigurableFileCollection

    override fun asArguments(): Iterable<String> =
        listOf(
            "-Dgtnl.test.hostJar=${host.get().asFile.absolutePath}",
            "-Dgtnl.test.libraryJars=${library.files.map { it.absolutePath }.sorted().joinToString(File.pathSeparator)}"
        )
}

val clientSmoke = providers.environmentVariable("GTNL_CLIENT_SMOKE").isPresent
val runtimeTestDirectory = providers.gradleProperty("gtnl.runtimeDir").orNull
minecraft {
    extraRunJvmArguments.addAll(
        if (clientSmoke || runtimeTestDirectory != null) "-Xmx4G" else "-Xmx8G",
        if (clientSmoke || runtimeTestDirectory != null) "-Xms1G" else "-Xms8G",
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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    // Minecraft, Forge and MUI keep global registries. Each test class owns a fresh JVM.
    forkEvery = 1
    maxParallelForks = 1
    maxHeapSize = "1G"
    workingDir = projectDir
    inputs.dir("src/main")
    inputs.dir("gtnllib/src/main")
    inputs.files("build.gradle.kts", "gtnllib/build.gradle", "gradle.properties")
    testLogging { events("failed", "skipped"); exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL }
}

tasks.named<Test>("test") {
    classpath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().compileClasspath
    dependsOn(":gtnllib:test")
}

listOf("architecture", "machine", "wireless", "equipment").forEach { category ->
    tasks.register<Test>("${category}Test") {
        group = "verification"
        description = "Run $category regression tests without launching Minecraft"
        testClassesDirs = sourceSets.test.get().output.classesDirs
        classpath = sourceSets.test.get().runtimeClasspath + sourceSets.main.get().runtimeClasspath +
            sourceSets.main.get().compileClasspath
        useJUnitPlatform { includeTags(category) }
    }
}

mapOf(
    "verifyCelestweave" to "equipmentTest",
    "verifyArchitecture" to "architectureTest",
    "verifyMachines" to "machineTest",
    "verifyWireless" to "wirelessTest",
    "verifyGrandAssemblyLine" to "grandAssemblyLineTest"
).forEach { (old, replacement) ->
    tasks.register(old) { group = "verification"; description = "Compatibility alias for $replacement"; dependsOn(replacement) }
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

tasks.register<Test>("grandAssemblyLineTest") {
    group = "verification"
    description = "Verify Grand Assembly Line overclocking and large energy settlement without starting Minecraft"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath + sourceSets.main.get().runtimeClasspath +
        sourceSets.main.get().compileClasspath
    filter { includeTestsMatching("*GrandAssemblyLineOverclockTest") }
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
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

val artifactTests = sourceSets.create("artifactTest") {
    compileClasspath += sourceSets.main.get().output + sourceSets.main.get().compileClasspath + sourceSets.test.get().compileClasspath
    runtimeClasspath += compileClasspath + sourceSets.test.get().runtimeClasspath
}

val artifactTest = tasks.register<Test>("artifactTest") {
    group = "verification"
    description = "Verify independent mod metadata, coremods, refmaps and multi-release class files"
    dependsOn(tasks.named("reobfJar"), libraryDistribution)
    testClassesDirs = artifactTests.output.classesDirs
    classpath = artifactTests.runtimeClasspath
    val archives = objects.newInstance<ModuleArchiveArguments>()
    archives.host.set(tasks.named<Jar>("reobfJar").flatMap { it.archiveFile })
    archives.library.from(libraryDistribution)
    jvmArgumentProviders.add(archives)
}

tasks.register("verifyModuleArtifacts") { group = "verification"; dependsOn(artifactTest) }

tasks.named("check") {
    dependsOn(artifactTest)
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

val runtimeTests = sourceSets.create("runtimeTest") {
    compileClasspath += sourceSets.main.get().output + sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}
val runtimeTestJar = tasks.register<Jar>("runtimeTestJar") {
    group = "verification"
    description = "Build the isolated Forge test mod; it is not a distribution artifact"
    from(runtimeTests.output)
    archiveFileName.set("gtnl-runtime-tests.jar")
    destinationDirectory.set(layout.buildDirectory.dir("test-mods"))
}
if (runtimeTestDirectory != null) {
    val testDirectory = file(runtimeTestDirectory).canonicalFile
    require(testDirectory.toPath().startsWith(layout.buildDirectory.dir("runtime-tests").get().asFile.canonicalFile.toPath())) {
        "Runtime tests must use a runner-owned directory under build/runtime-tests"
    }
    require(testDirectory.resolve(".gtnl-test-workspace").isFile) { "Use tools/testing/run-runtime-tests.cjs to prepare the test workspace" }
    val installRuntimeTests = tasks.register<Copy>("installRuntimeTests") {
        from(runtimeTestJar)
        into(testDirectory.resolve("mods"))
    }
    listOf("runServer25", "runClient25").forEach { taskName ->
        tasks.named<JavaExec>(taskName) {
            dependsOn(installRuntimeTests)
            workingDir = testDirectory
            if (taskName == "runClient25") args("--width", "1280", "--height", "720")
        }
    }
}

runConfigs.forEach { (taskName, path) ->
    tasks.named<JavaExec>(taskName) {
        workingDir = if (taskName in listOf("runServer25", "runClient25") && runtimeTestDirectory != null) file(runtimeTestDirectory) else file("${projectDir}/$path")
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
