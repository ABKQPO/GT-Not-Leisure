package com.science.gtnl.wirelesstest;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Comparator;

/** Runs LaunchWrapper in an isolated URL loader, without starting Minecraft or opening a world. */
public final class WirelessMixinBootstrapTest {

    public static void main(String[] args) throws Exception {
        ArrayList<URL> urls = new ArrayList<>();
        for (String entry : System.getProperty("java.class.path")
            .split(File.pathSeparator)) {
            String path = entry.replace('\\', '/');
            if (!path.endsWith(".jar") || path.contains("/org.ow2.asm/") && !path.contains("/asm-debug-all/")
                || path.contains("/unimixins/")
                || path.contains("/mixinextras-")
                || path.contains("/launchwrapper/")
                || path.contains("/jopt-simple/")
                || path.contains("/log4j-")
                || path.contains("/gson/")
                || path.contains("/guava/")
                || path.contains("/fastutil/")
                || path.contains("/io.netty/")
                || path.contains("/authlib/")
                || path.contains("/commons-lang3/")
                || path.contains("/commons-io/")
                || path.contains("/jvmdowngrader-java-api/")
                || path.toLowerCase(java.util.Locale.ROOT)
                    .contains("/gtnhlib/")
                || path.contains("/Applied-Energistics-2-Unofficial/")) {
                urls.add(
                    new File(entry).toURI()
                        .toURL());
            }
        }
        // Mods bundle old ASM copies; use the resolved ASM dependency before any shaded copies.
        urls.sort(
            Comparator.comparingInt(
                url -> url.toString()
                    .contains("/org.ow2.asm/") ? 0 : 1));
        // Keep this loader alive until JVM exit: Log4j's shutdown hook still loads classes from it.
        URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader()) {

            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                // This test runs Mixin only, not FML coremods or the Minecraft launch process.
                if (name.equals("cpw.mods.fml.relauncher.CoreModManager")
                    || name.equals("net.minecraftforge.fml.relauncher.CoreModManager")) {
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name, resolve);
            }
        };
        Thread.currentThread()
            .setContextClassLoader(loader);
        Class<?> launch = Class.forName("net.minecraft.launchwrapper.Launch", true, loader);
        launch.getMethod("main", String[].class)
            .invoke(
                null,
                (Object) new String[] { "--tweakClass", "com.science.gtnl.wirelesstest.WirelessTestTweaker",
                    "--tweakClass", "org.spongepowered.asm.launch.MixinTweaker" });
    }
}
