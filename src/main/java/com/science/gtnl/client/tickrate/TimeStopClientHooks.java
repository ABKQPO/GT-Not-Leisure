package com.science.gtnl.client.tickrate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.ReportedException;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;

public class TimeStopClientHooks {

    private TimeStopClientHooks() {}

    // Minecraft 1.7.10 has no isGamePaused field.
    public static boolean isGamePaused(Minecraft mc) {
        return mc.theWorld == null || (mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame());
    }

    public static void runTick(Minecraft mc) {
        mc.mcProfiler.startSection("scheduledExecutables");
        mc.mcProfiler.endSection();

        if (mc.rightClickDelayTimer > 0) {
            --mc.rightClickDelayTimer;
        }

        FMLCommonHandler.instance()
            .onPreClientTick();

        mc.mcProfiler.startSection("gui");

        if (!isGamePaused(mc)) {
            mc.ingameGUI.updateTick();
        }

        mc.mcProfiler.endStartSection("pick");
        mc.entityRenderer.getMouseOver(1.0F);
        mc.mcProfiler.endStartSection("gameMode");

        if (!isGamePaused(mc) && mc.theWorld != null) {
            mc.playerController.updateController();
        }

        mc.mcProfiler.endStartSection("textures");

        if (!isGamePaused(mc)) {
            ItemStack[] itemStacks = mc.thePlayer.inventory.mainInventory;
            for (int i = 0; i < itemStacks.length; i++) {
                ItemStack stack = itemStacks[i];
                if (stack == null) continue;
                stack.updateAnimation(mc.theWorld, mc.thePlayer, i, mc.thePlayer.inventory.currentItem == i);
            }
        }

        if (mc.currentScreen == null && mc.thePlayer != null) {
            if (mc.thePlayer.getHealth() <= 0.0F) {
                mc.displayGuiScreen(null);
            } else if (mc.thePlayer.isPlayerSleeping() && mc.theWorld != null) {
                mc.displayGuiScreen(new GuiSleepMP());
            }
        } else if (mc.currentScreen != null && mc.currentScreen instanceof GuiSleepMP
            && !mc.thePlayer.isPlayerSleeping()) {
                mc.displayGuiScreen(null);
            }

        if (mc.currentScreen != null) {
            mc.leftClickCounter = 10000;
        }

        CrashReport crashreport;
        CrashReportCategory crashreportcategory;

        if (mc.currentScreen != null) {
            try {
                mc.currentScreen.handleInput();
            } catch (Throwable throwable1) {
                crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addCrashSectionCallable(
                    "Screen name",
                    () -> mc.currentScreen.getClass()
                        .getCanonicalName());
                throw new ReportedException(crashreport);
            }

            if (mc.currentScreen != null) {
                try {
                    mc.currentScreen.updateScreen();
                } catch (Throwable throwable) {
                    crashreport = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    crashreportcategory = crashreport.makeCategory("Affected screen");
                    crashreportcategory.addCrashSectionCallable(
                        "Screen name",
                        () -> mc.currentScreen.getClass()
                            .getCanonicalName());
                    throw new ReportedException(crashreport);
                }
            }
        }

        if (mc.currentScreen == null || mc.currentScreen.allowUserInput) {
            mc.mcProfiler.endStartSection("mouse");
            int j;

            while (Mouse.next()) {
                if (ForgeHooksClient.postMouseEvent()) continue;

                j = Mouse.getEventButton();
                KeyBinding.setKeyBindState(j - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState()) {
                    KeyBinding.onTick(j - 100);
                }

                long k = Minecraft.getSystemTime() - mc.systemTime;

                if (k <= 200L) {
                    int i = Mouse.getEventDWheel();

                    if (i != 0) {
                        mc.thePlayer.inventory.changeCurrentItem(i);

                        if (mc.gameSettings.noclip) {
                            if (i > 0) i = 1;
                            if (i < 0) i = -1;
                            mc.gameSettings.noclipRate += (float) i * 0.25F;
                        }
                    }

                    if (mc.currentScreen == null) {
                        if (!mc.inGameHasFocus && Mouse.getEventButtonState()) {
                            mc.setIngameFocus();
                        }
                    } else {
                        mc.currentScreen.handleMouseInput();
                    }
                }
                FMLCommonHandler.instance()
                    .fireMouseInput();
            }

            if (mc.leftClickCounter > 0) {
                --mc.leftClickCounter;
            }

            mc.mcProfiler.endStartSection("keyboard");
            boolean flag;

            while (Keyboard.next()) {
                KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState()) {
                    KeyBinding.onTick(Keyboard.getEventKey());
                }

                if (mc.field_83002_am > 0L) {
                    if (Minecraft.getSystemTime() - mc.field_83002_am >= 6000L) {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                        mc.field_83002_am = -1L;
                    }
                } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                    mc.field_83002_am = Minecraft.getSystemTime();
                }

                mc.func_152348_aa();

                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == 62 && mc.entityRenderer != null) {
                        mc.entityRenderer.deactivateShader();
                    }

                    if (mc.currentScreen != null) {
                        mc.currentScreen.handleKeyboardInput();
                    } else {
                        if (Keyboard.getEventKey() == 1) {
                            mc.displayInGameMenu();
                        }

                        if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
                            mc.refreshResources();
                        }

                        if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
                            mc.refreshResources();
                        }

                        if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
                            flag = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                            mc.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, flag ? -1 : 1);
                        }

                        if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
                            mc.renderGlobal.loadRenderers();
                        }

                        if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
                            mc.gameSettings.advancedItemTooltips = !mc.gameSettings.advancedItemTooltips;
                            mc.gameSettings.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
                            RenderManager.debugBoundingBox = !RenderManager.debugBoundingBox;
                        }

                        if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
                            mc.gameSettings.pauseOnLostFocus = !mc.gameSettings.pauseOnLostFocus;
                            mc.gameSettings.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 59) {
                            mc.gameSettings.hideGUI = !mc.gameSettings.hideGUI;
                        }

                        if (Keyboard.getEventKey() == 61) {
                            mc.gameSettings.showDebugInfo = !mc.gameSettings.showDebugInfo;
                            mc.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                        }

                        if (mc.gameSettings.keyBindTogglePerspective.isPressed()) {
                            ++mc.gameSettings.thirdPersonView;
                            if (mc.gameSettings.thirdPersonView > 2) {
                                mc.gameSettings.thirdPersonView = 0;
                            }
                        }

                        if (mc.gameSettings.keyBindSmoothCamera.isPressed()) {
                            mc.gameSettings.smoothCamera = !mc.gameSettings.smoothCamera;
                        }
                    }

                    if (mc.gameSettings.showDebugInfo && mc.gameSettings.showDebugProfilerChart) {
                        if (Keyboard.getEventKey() == 11) {
                            mc.updateDebugProfilerName(0);
                        }
                        for (j = 0; j < 9; ++j) {
                            if (Keyboard.getEventKey() == 2 + j) {
                                mc.updateDebugProfilerName(j + 1);
                            }
                        }
                    }
                }
                FMLCommonHandler.instance()
                    .fireKeyInput();
            }

            for (j = 0; j < 9; ++j) {
                if (mc.gameSettings.keyBindsHotbar[j].isPressed()) {
                    mc.thePlayer.inventory.currentItem = j;
                }
            }

            flag = mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (mc.gameSettings.keyBindInventory.isPressed()) {
                if (mc.playerController.func_110738_j()) {
                    mc.thePlayer.func_110322_i();
                } else {
                    mc.getNetHandler()
                        .addToSendQueue(
                            new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                }
            }

            while (mc.gameSettings.keyBindDrop.isPressed()) {
                mc.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }

            while (mc.gameSettings.keyBindChat.isPressed() && flag) {
                mc.displayGuiScreen(new GuiChat());
            }

            if (mc.currentScreen == null && mc.gameSettings.keyBindCommand.isPressed() && flag) {
                mc.displayGuiScreen(new GuiChat("/"));
            }

            if (mc.thePlayer.isUsingItem()) {
                if (!mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                }
                label391: while (true) {
                    if (!mc.gameSettings.keyBindAttack.isPressed()) {
                        while (mc.gameSettings.keyBindUseItem.isPressed()) {}
                        while (true) {
                            if (!mc.gameSettings.keyBindPickBlock.isPressed()) continue;
                            break label391;
                        }
                    }
                }
            } else {
                while (mc.gameSettings.keyBindAttack.isPressed()) {
                    mc.func_147116_af();
                }
                while (mc.gameSettings.keyBindUseItem.isPressed()) {
                    mc.func_147121_ag();
                }
                while (mc.gameSettings.keyBindPickBlock.isPressed()) {
                    mc.func_147112_ai();
                }
            }

            if (mc.gameSettings.keyBindUseItem.getIsKeyPressed() && mc.rightClickDelayTimer == 0
                && !mc.thePlayer.isUsingItem()) {
                mc.func_147121_ag();
            }

            mc.func_147115_a(
                mc.currentScreen == null && mc.gameSettings.keyBindAttack.getIsKeyPressed() && mc.inGameHasFocus);
        }

        if (mc.theWorld != null) {
            if (mc.thePlayer != null) {
                ++mc.joinPlayerCounter;
                if (mc.joinPlayerCounter == 30) {
                    mc.joinPlayerCounter = 0;
                    mc.theWorld.joinEntityInSurroundings(mc.thePlayer);
                }
            }

            mc.mcProfiler.endStartSection("gameRenderer");
            if (!isGamePaused(mc)) {
                mc.entityRenderer.updateRenderer();
            }

            mc.mcProfiler.endStartSection("levelRenderer");
            mc.mcProfiler.endStartSection("level");
            if (!isGamePaused(mc)) {
                mc.theWorld.updateEntities();
            }
        }

        if (mc.theWorld != null) {
            mc.mcProfiler.endStartSection("animateTick");
            mc.mcProfiler.endStartSection("particles");
        } else if (mc.myNetworkManager != null) {
            mc.mcProfiler.endStartSection("pendingConnection");
            mc.myNetworkManager.processReceivedPackets();
        }

        FMLCommonHandler.instance()
            .onPostClientTick();
        mc.mcProfiler.endSection();
        mc.systemTime = Minecraft.getSystemTime();
    }

    public static void runGameLoop(Minecraft mc) {
        mc.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested()) {
            mc.shutdown();
        }

        if (isGamePaused(mc) && mc.theWorld != null) {
            float f = mc.timer.renderPartialTicks;
            mc.timer.updateTimer();
            mc.timer.renderPartialTicks = f;
        } else {
            mc.timer.updateTimer();
        }

        if ((mc.theWorld == null || mc.currentScreen == null) && mc.refreshTexturePacksScheduled) {
            mc.refreshTexturePacksScheduled = false;
            mc.refreshResources();
        }

        long j = System.nanoTime();
        mc.mcProfiler.startSection("tick");

        for (int i = 0; i < mc.timer.elapsedTicks; ++i) {
            mc.runTick();
        }

        mc.mcProfiler.endStartSection("preRenderErrors");
        long k = System.nanoTime() - j;
        mc.checkGLError("Pre render");
        RenderBlocks.fancyGrass = mc.gameSettings.fancyGraphics;
        mc.mcProfiler.endStartSection("sound");
        mc.mcSoundHandler.setListener(mc.thePlayer, mc.timer.renderPartialTicks);
        mc.mcProfiler.endSection();
        mc.mcProfiler.startSection("render");
        GL11.glPushMatrix();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        mc.framebufferMc.bindFramebuffer(true);
        mc.mcProfiler.startSection("display");
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (mc.thePlayer != null && mc.thePlayer.isEntityInsideOpaqueBlock()) {
            mc.gameSettings.thirdPersonView = 0;
        }

        mc.mcProfiler.endSection();

        if (!mc.skipRenderWorld) {
            FMLCommonHandler.instance()
                .onRenderTickStart(mc.timer.renderPartialTicks);
            mc.mcProfiler.endStartSection("gameRenderer");
            mc.entityRenderer.updateCameraAndRender(mc.timer.renderPartialTicks);
            mc.mcProfiler.endSection();
            FMLCommonHandler.instance()
                .onRenderTickEnd(mc.timer.renderPartialTicks);
        }

        GL11.glFlush();
        mc.mcProfiler.endSection();

        if (!Display.isActive() && mc.fullscreen) {
            mc.toggleFullscreen();
        }

        if (mc.gameSettings.showDebugInfo && mc.gameSettings.showDebugProfilerChart) {
            if (!mc.mcProfiler.profilingEnabled) {
                mc.mcProfiler.clearProfiling();
            }
            mc.mcProfiler.profilingEnabled = true;
            mc.displayDebugInfo(k);
        } else {
            mc.mcProfiler.profilingEnabled = false;
            mc.prevFrameTime = System.nanoTime();
        }

        mc.guiAchievement.func_146254_a();
        mc.framebufferMc.unbindFramebuffer();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        mc.framebufferMc.framebufferRender(mc.displayWidth, mc.displayHeight);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        mc.entityRenderer.func_152430_c(mc.timer.renderPartialTicks);
        GL11.glPopMatrix();
        mc.mcProfiler.startSection("root");
        mc.func_147120_f();
        Thread.yield();
        mc.mcProfiler.startSection("stream");
        mc.mcProfiler.startSection("update");
        mc.field_152353_at.func_152935_j();
        mc.mcProfiler.endStartSection("submit");
        mc.field_152353_at.func_152922_k();
        mc.mcProfiler.endSection();
        mc.mcProfiler.endSection();
        mc.checkGLError("Post render");
        ++mc.fpsCounter;

        while (Minecraft.getSystemTime() >= mc.debugUpdateTime + 1000L) {
            Minecraft.debugFPS = mc.fpsCounter;
            mc.debug = Minecraft.debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
            WorldRenderer.chunksUpdated = 0;
            mc.debugUpdateTime += 1000L;
            mc.fpsCounter = 0;
            mc.usageSnooper.addMemoryStatsToSnooper();

            if (!mc.usageSnooper.isSnooperRunning()) {
                mc.usageSnooper.startSnooper();
            }
        }

        mc.mcProfiler.endSection();

        if (mc.isFramerateLimitBelowMax()) {
            Display.sync(mc.getLimitFramerate());
        }
    }
}
