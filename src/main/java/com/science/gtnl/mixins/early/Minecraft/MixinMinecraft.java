package com.science.gtnl.mixins.early.Minecraft;

import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Timer;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.reavaritia.common.render.CustomEntityRenderer;
import com.science.gtnl.common.item.TimeStopManager;
import com.science.gtnl.utils.ClientUtils;

import cpw.mods.fml.common.FMLCommonHandler;

@Mixin(value = Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    public boolean isGamePaused;
    @Final
    @Shadow
    public Profiler mcProfiler;
    @Shadow
    public int rightClickDelayTimer;
    @Shadow
    public GuiIngame ingameGUI;
    @Shadow
    public EntityRenderer entityRenderer;
    @Shadow
    public WorldClient theWorld;
    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    public TextureManager renderEngine;
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    public EntityClientPlayerMP thePlayer;
    @Shadow
    public int leftClickCounter;
    @Shadow
    public long systemTime;
    @Shadow
    public GameSettings gameSettings;
    @Shadow
    public boolean inGameHasFocus;
    @Shadow
    public long field_83002_am;
    @Shadow
    public int joinPlayerCounter;
    @Shadow
    public RenderGlobal renderGlobal;
    @Shadow
    public MusicTicker mcMusicTicker;
    @Shadow
    public SoundHandler mcSoundHandler;
    @Shadow
    public EffectRenderer effectRenderer;
    @Shadow
    public NetworkManager myNetworkManager;
    @Shadow
    public Timer timer;
    @Shadow
    public boolean refreshTexturePacksScheduled;

    @Redirect(
        method = "startGame",
        at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/EntityRenderer;", ordinal = 0))
    private EntityRenderer redirectEntityRenderer(Minecraft mc, IResourceManager resourceManager) {
        return new CustomEntityRenderer(mc, resourceManager);
    }

    @Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
    private void mixin$runTick(CallbackInfo ci) {
        boolean isStop = TimeStopManager.isTimeStopped();
        if (!isStop) {
            return;
        } else {
            ci.cancel();
        }
        this.mcProfiler.startSection("scheduledExecutables");

        this.mcProfiler.endSection();

        if (this.rightClickDelayTimer > 0) {
            --this.rightClickDelayTimer;
        }

        FMLCommonHandler.instance()
            .onPreClientTick();

        this.mcProfiler.startSection("gui");

        if (!this.isGamePaused) {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endStartSection("pick");
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.endStartSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null) {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused) {
            if (!isStop) {
                this.renderEngine.tick();
            } else {
                // 获取自身物品栏
                ItemStack[] itemStacks = ((Minecraft) ((Object) this)).thePlayer.inventory.mainInventory;
                for (int i = 0; i < itemStacks.length; i++) {
                    ItemStack stack = itemStacks[i];
                    if (stack == null) continue;
                    stack.updateAnimation(theWorld, thePlayer, i, thePlayer.inventory.currentItem == i);
                }
            }
        }

        if (this.currentScreen == null && this.thePlayer != null) {
            if (this.thePlayer.getHealth() <= 0.0F) {
                ((Minecraft) ((Object) this)).displayGuiScreen(null);
            } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
                ((Minecraft) ((Object) this)).displayGuiScreen(new GuiSleepMP());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP
            && !this.thePlayer.isPlayerSleeping()) {
                ((Minecraft) ((Object) this)).displayGuiScreen(null);
            }

        if (this.currentScreen != null) {
            this.leftClickCounter = 10000;
        }

        CrashReport crashreport;
        CrashReportCategory crashreportcategory;

        if (this.currentScreen != null) {
            try {
                this.currentScreen.handleInput();
            } catch (Throwable throwable1) {
                crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addCrashSectionCallable("Screen name", new Callable() {

                    public String call() {
                        return ((Minecraft) ((Object) this)).currentScreen.getClass()
                            .getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }

            if (this.currentScreen != null) {
                try {
                    this.currentScreen.updateScreen();
                } catch (Throwable throwable) {
                    crashreport = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    crashreportcategory = crashreport.makeCategory("Affected screen");
                    crashreportcategory.addCrashSectionCallable("Screen name", new Callable() {

                        public String call() {
                            return ((Minecraft) ((Object) this)).currentScreen.getClass()
                                .getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput) {
            this.mcProfiler.endStartSection("mouse");
            int j;

            while (Mouse.next()) {
                if (ForgeHooksClient.postMouseEvent()) continue;

                j = Mouse.getEventButton();
                KeyBinding.setKeyBindState(j - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState()) {
                    KeyBinding.onTick(j - 100);
                }

                long k = Minecraft.getSystemTime() - this.systemTime;

                if (k <= 200L) {
                    int i = Mouse.getEventDWheel();

                    if (i != 0) {
                        this.thePlayer.inventory.changeCurrentItem(i);

                        if (this.gameSettings.noclip) {
                            if (i > 0) {
                                i = 1;
                            }

                            if (i < 0) {
                                i = -1;
                            }

                            this.gameSettings.noclipRate += (float) i * 0.25F;
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                            ((Minecraft) ((Object) this)).setIngameFocus();
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouseInput();
                    }
                }
                FMLCommonHandler.instance()
                    .fireMouseInput();
            }

            if (this.leftClickCounter > 0) {
                --this.leftClickCounter;
            }

            this.mcProfiler.endStartSection("keyboard");
            boolean flag;

            while (Keyboard.next()) {
                KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState()) {
                    KeyBinding.onTick(Keyboard.getEventKey());
                }

                if (this.field_83002_am > 0L) {
                    if (Minecraft.getSystemTime() - this.field_83002_am >= 6000L) {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                        this.field_83002_am = -1L;
                    }
                } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                    this.field_83002_am = Minecraft.getSystemTime();
                }

                ((Minecraft) ((Object) this)).func_152348_aa();

                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == 62 && this.entityRenderer != null) {
                        this.entityRenderer.deactivateShader();
                    }

                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                    } else {
                        if (Keyboard.getEventKey() == 1) {
                            ((Minecraft) ((Object) this)).displayInGameMenu();
                        }

                        if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
                            ((Minecraft) ((Object) this)).refreshResources();
                        }

                        if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
                            ((Minecraft) ((Object) this)).refreshResources();
                        }

                        if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
                            flag = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, flag ? -1 : 1);
                        }

                        if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
                            ((Minecraft) ((Object) this)).renderGlobal.loadRenderers();
                        }

                        if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                            this.gameSettings.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
                            RenderManager.debugBoundingBox = !RenderManager.debugBoundingBox;
                        }

                        if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                            this.gameSettings.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 59) {
                            this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                        }

                        if (Keyboard.getEventKey() == 61) {
                            this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                        }

                        if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                            ++this.gameSettings.thirdPersonView;

                            if (this.gameSettings.thirdPersonView > 2) {
                                this.gameSettings.thirdPersonView = 0;
                            }
                        }

                        if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                        }
                    }

                    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                        if (Keyboard.getEventKey() == 11) {
                            ((Minecraft) ((Object) this)).updateDebugProfilerName(0);
                        }

                        for (j = 0; j < 9; ++j) {
                            if (Keyboard.getEventKey() == 2 + j) {
                                ((Minecraft) ((Object) this)).updateDebugProfilerName(j + 1);
                            }
                        }
                    }
                }
                FMLCommonHandler.instance()
                    .fireKeyInput();
            }

            for (j = 0; j < 9; ++j) {
                if (this.gameSettings.keyBindsHotbar[j].isPressed()) {
                    this.thePlayer.inventory.currentItem = j;
                }
            }

            flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.gameSettings.keyBindInventory.isPressed()) {
                if (this.playerController.func_110738_j()) {
                    this.thePlayer.func_110322_i();
                } else {
                    ((Minecraft) ((Object) this)).getNetHandler()
                        .addToSendQueue(
                            new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    ((Minecraft) ((Object) this)).displayGuiScreen(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed()) {
                this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }

            while (this.gameSettings.keyBindChat.isPressed() && flag) {
                ((Minecraft) ((Object) this)).displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag) {
                ((Minecraft) ((Object) this)).displayGuiScreen(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem()) {
                if (!this.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                label391:

                while (true) {
                    if (!this.gameSettings.keyBindAttack.isPressed()) {
                        while (this.gameSettings.keyBindUseItem.isPressed()) {
                            ;
                        }

                        while (true) {
                            if (this.gameSettings.keyBindPickBlock.isPressed()) {
                                continue;
                            }

                            break label391;
                        }
                    }
                }
            } else {
                while (this.gameSettings.keyBindAttack.isPressed()) {
                    ((Minecraft) ((Object) this)).func_147116_af();
                }

                while (this.gameSettings.keyBindUseItem.isPressed()) {
                    ((Minecraft) ((Object) this)).func_147121_ag();
                }

                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                    ((Minecraft) ((Object) this)).func_147112_ai();
                }
            }

            if (this.gameSettings.keyBindUseItem.getIsKeyPressed() && this.rightClickDelayTimer == 0
                && !this.thePlayer.isUsingItem()) {
                ((Minecraft) ((Object) this)).func_147121_ag();
            }

            ((Minecraft) ((Object) this)).func_147115_a(
                this.currentScreen == null && this.gameSettings.keyBindAttack.getIsKeyPressed() && this.inGameHasFocus);
        }

        if (this.theWorld != null) {
            if (this.thePlayer != null) {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused) {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused) {
                if (!isStop) this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused) {
                if (this.theWorld.lastLightningBolt > 0) {
                    if (!isStop) --this.theWorld.lastLightningBolt;
                }

                this.theWorld.updateEntities();
            }
        }

        if (!this.isGamePaused) {
            if (!isStop) this.mcMusicTicker.update();
            if (!isStop) this.mcSoundHandler.update();
        }

        if (this.theWorld != null) {
            if (!this.isGamePaused) {
                if (!isStop) this.theWorld
                    .setAllowedSpawnTypes(this.theWorld.difficultySetting != EnumDifficulty.PEACEFUL, true);

                try {
                    if (!isStop) this.theWorld.tick();
                } catch (Throwable throwable2) {
                    crashreport = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                    if (this.theWorld == null) {
                        crashreportcategory = crashreport.makeCategory("Affected level");
                        crashreportcategory.addCrashSection("Problem", "Level is null!");
                    } else {
                        this.theWorld.addWorldInfoToCrashReport(crashreport);
                    }

                    throw new ReportedException(crashreport);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.theWorld != null) {
                if (!isStop) this.theWorld.doVoidFogParticles(
                    MathHelper.floor_double(this.thePlayer.posX),
                    MathHelper.floor_double(this.thePlayer.posY),
                    MathHelper.floor_double(this.thePlayer.posZ));
            }

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused) {
                if (!isStop) this.effectRenderer.updateEffects();
            }
        } else if (this.myNetworkManager != null) {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        FMLCommonHandler.instance()
            .onPostClientTick();

        this.mcProfiler.endSection();
        this.systemTime = Minecraft.getSystemTime();
    }

    @Inject(method = "runGameLoop", at = @At("HEAD"), cancellable = true)
    private void mixin$runGameLoop(CallbackInfo ci) {
        if (TimeStopManager.isTimeStopped()) {
            ci.cancel();
        } else return;

        this.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested()) {
            ((Minecraft) ((Object) this)).shutdown();
        }

        if (this.isGamePaused && this.theWorld != null) {
            float f = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = f;
        } else {
            this.timer.updateTimer();
        }

        if ((this.theWorld == null || this.currentScreen == null) && this.refreshTexturePacksScheduled) {
            this.refreshTexturePacksScheduled = false;
            ((Minecraft) ((Object) this)).refreshResources();
        }

        long j = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int i = 0; i < this.timer.elapsedTicks; ++i) {
            ((Minecraft) ((Object) this)).runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long k = System.nanoTime() - j;
        ((Minecraft) ((Object) this)).checkGLError("Pre render");
        RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GL11.glPushMatrix();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        ((Minecraft) ((Object) this)).framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();

        if (!((Minecraft) ((Object) this)).skipRenderWorld) {
            FMLCommonHandler.instance()
                .onRenderTickStart(this.timer.renderPartialTicks);
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
            this.mcProfiler.endSection();
            FMLCommonHandler.instance()
                .onRenderTickEnd(this.timer.renderPartialTicks);
        }

        GL11.glFlush();
        this.mcProfiler.endSection();

        if (!Display.isActive() && ((Minecraft) ((Object) this)).fullscreen) {
            ((Minecraft) ((Object) this)).toggleFullscreen();
        }

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
            if (!this.mcProfiler.profilingEnabled) {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            ((Minecraft) ((Object) this)).displayDebugInfo(k);
        } else {
            this.mcProfiler.profilingEnabled = false;
            ((Minecraft) ((Object) this)).prevFrameTime = System.nanoTime();
        }

        ((Minecraft) ((Object) this)).guiAchievement.func_146254_a();
        ((Minecraft) ((Object) this)).framebufferMc.unbindFramebuffer();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        ((Minecraft) ((Object) this)).framebufferMc
            .framebufferRender(((Minecraft) ((Object) this)).displayWidth, ((Minecraft) ((Object) this)).displayHeight);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.entityRenderer.func_152430_c(this.timer.renderPartialTicks);
        GL11.glPopMatrix();
        this.mcProfiler.startSection("root");
        ((Minecraft) ((Object) this)).func_147120_f();
        Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        ((Minecraft) ((Object) this)).field_152353_at.func_152935_j();
        this.mcProfiler.endStartSection("submit");
        ((Minecraft) ((Object) this)).field_152353_at.func_152922_k();
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        ((Minecraft) ((Object) this)).checkGLError("Post render");
        ++((Minecraft) ((Object) this)).fpsCounter;
        this.isGamePaused = ((Minecraft) ((Object) this)).isSingleplayer() && this.currentScreen != null
            && this.currentScreen.doesGuiPauseGame()
            && !((Minecraft) ((Object) this)).theIntegratedServer.getPublic();

        while (Minecraft.getSystemTime() >= ((Minecraft) ((Object) this)).debugUpdateTime + 1000L) {
            Minecraft.debugFPS = ((Minecraft) ((Object) this)).fpsCounter;
            ((Minecraft) ((Object) this)).debug = Minecraft.debugFPS + " fps, "
                + WorldRenderer.chunksUpdated
                + " chunk updates";
            WorldRenderer.chunksUpdated = 0;
            ((Minecraft) ((Object) this)).debugUpdateTime += 1000L;
            ((Minecraft) ((Object) this)).fpsCounter = 0;
            ((Minecraft) ((Object) this)).usageSnooper.addMemoryStatsToSnooper();

            if (!((Minecraft) ((Object) this)).usageSnooper.isSnooperRunning()) {
                ((Minecraft) ((Object) this)).usageSnooper.startSnooper();
            }
        }

        this.mcProfiler.endSection();

        if (((Minecraft) ((Object) this)).isFramerateLimitBelowMax()) {
            Display.sync(((Minecraft) ((Object) this)).getLimitFramerate());
        }
    }

    @Inject(
        method = "func_147112_ai",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/ForgeHooks;onPickBlock(Lnet/minecraft/util/MovingObjectPosition;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;)Z"),
        cancellable = true,
        remap = false)
    private void onBeforePickBlock(CallbackInfo ci) {
        if (ClientUtils.onBeforePickBlock(this.thePlayer, this.theWorld, false)) {
            ci.cancel();
        }
    }
}
