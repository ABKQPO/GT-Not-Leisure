package com.science.gtnl.mixins.early.minecraft;

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

import com.reavaritia.client.render.CustomEntityRenderer;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.utils.ClientUtils;

import cpw.mods.fml.common.FMLCommonHandler;

/*
 * Early mixin — runs BEFORE SRG name mapping at runtime.
 * All field names below are obfuscated (SEARGE) names that exist
 * in the transformed Minecraft class at injection time.
 * Deobfuscated names are shown inline as comments.
 */
@Mixin(value = Minecraft.class, remap = true)
public abstract class MixinMinecraft {

    // --- @Shadow fields (obfuscated names required for early mixin configs) ---
    @Final
    @Shadow
    public Profiler field_71424_I; // mcProfiler
    @Shadow
    public boolean field_71445_n; // boolean guard
    @Shadow
    public int field_71467_ac; // rightClickDelayTimer
    @Shadow
    public GuiIngame field_71456_v; // ingameGUI
    @Shadow
    public EntityRenderer field_71460_t; // entityRenderer
    @Shadow
    public WorldClient field_71441_e; // theWorld
    @Shadow
    public PlayerControllerMP field_71442_b; // playerController
    @Shadow
    public TextureManager field_71446_o; // renderEngine
    @Shadow
    public GuiScreen field_71462_r; // currentScreen
    @Shadow
    public EntityClientPlayerMP field_71439_g; // thePlayer
    @Shadow
    public int field_71429_W; // leftClickCounter
    @Shadow
    public long field_71423_H; // systemTime
    @Shadow
    public GameSettings field_71474_y; // gameSettings
    @Shadow
    public boolean field_71415_G; // inGameHasFocus
    @Shadow
    public long field_83002_am; // debugKey (F3)
    @Shadow
    public int field_71457_ai; // joinPlayerCounter
    @Shadow
    public RenderGlobal field_71438_f; // renderGlobal
    @Shadow
    public MusicTicker field_147126_aw; // mcMusicTicker
    @Shadow
    public SoundHandler field_147127_av; // mcSoundHandler
    @Shadow
    public EffectRenderer field_71452_i; // effectRenderer
    @Shadow
    public NetworkManager field_71453_ak; // myNetworkManager
    @Shadow
    public Timer field_71428_T; // timer
    @Shadow
    public boolean field_71468_ad; // refreshTexturePacksScheduled

    // MC 1.7.10 does not have isGamePaused field - compute it manually
    private boolean isGamePaused() {
        return this.field_71441_e == null || (this.field_71462_r != null && this.field_71462_r.doesGuiPauseGame());
    }

    @Redirect(
        method = "startGame",
        at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/EntityRenderer;", ordinal = 0))
    private EntityRenderer redirectEntityRenderer(Minecraft mc, IResourceManager resourceManager) {
        return new CustomEntityRenderer(mc, resourceManager);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
    private void mixin$runTick(CallbackInfo ci) {
        boolean isStop = TimeStopPocketWatch.isTimeStopped();
        if (!isStop) {
            return;
        } else {
            ci.cancel();
        }
        this.field_71424_I.startSection("scheduledExecutables");
        this.field_71424_I.endSection();

        if (this.field_71467_ac > 0) {
            --this.field_71467_ac;
        }

        FMLCommonHandler.instance()
            .onPreClientTick();

        this.field_71424_I.startSection("gui");

        if (!this.isGamePaused()) {
            this.field_71456_v.updateTick();
        }

        this.field_71424_I.endStartSection("pick");
        this.field_71460_t.getMouseOver(1.0F);
        this.field_71424_I.endStartSection("gameMode");

        if (!this.isGamePaused() && this.field_71441_e != null) {
            this.field_71442_b.updateController();
        }

        this.field_71424_I.endStartSection("textures");

        if (!this.isGamePaused()) {
            if (!isStop) {
                this.field_71446_o.tick();
            } else {
                ItemStack[] itemStacks = this.field_71439_g.inventory.mainInventory;
                for (int i = 0; i < itemStacks.length; i++) {
                    ItemStack stack = itemStacks[i];
                    if (stack == null) continue;
                    stack.updateAnimation(
                        this.field_71441_e,
                        this.field_71439_g,
                        i,
                        this.field_71439_g.inventory.currentItem == i);
                }
            }
        }

        if (this.field_71462_r == null && this.field_71439_g != null) {
            if (this.field_71439_g.getHealth() <= 0.0F) {
                ((Minecraft) ((Object) this)).displayGuiScreen(null);
            } else if (this.field_71439_g.isPlayerSleeping() && this.field_71441_e != null) {
                ((Minecraft) ((Object) this)).displayGuiScreen(new GuiSleepMP());
            }
        } else if (this.field_71462_r != null && this.field_71462_r instanceof GuiSleepMP
            && !this.field_71439_g.isPlayerSleeping()) {
                ((Minecraft) ((Object) this)).displayGuiScreen(null);
            }

        if (this.field_71462_r != null) {
            this.field_71429_W = 10000;
        }

        CrashReport crashreport;
        CrashReportCategory crashreportcategory;

        if (this.field_71462_r != null) {
            try {
                this.field_71462_r.handleInput();
            } catch (Throwable throwable1) {
                crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addCrashSectionCallable("Screen name", new Callable<>() {

                    public String call() {
                        return MixinMinecraft.this.field_71462_r.getClass()
                            .getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }

            if (this.field_71462_r != null) {
                try {
                    this.field_71462_r.updateScreen();
                } catch (Throwable throwable) {
                    crashreport = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    crashreportcategory = crashreport.makeCategory("Affected screen");
                    crashreportcategory.addCrashSectionCallable("Screen name", new Callable<>() {

                        public String call() {
                            return MixinMinecraft.this.field_71462_r.getClass()
                                .getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }

        if (this.field_71462_r == null || this.field_71462_r.allowUserInput) {
            this.field_71424_I.endStartSection("mouse");
            int j;

            while (Mouse.next()) {
                if (ForgeHooksClient.postMouseEvent()) continue;

                j = Mouse.getEventButton();
                KeyBinding.setKeyBindState(j - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState()) {
                    KeyBinding.onTick(j - 100);
                }

                long k = Minecraft.getSystemTime() - this.field_71423_H;

                if (k <= 200L) {
                    int i = Mouse.getEventDWheel();

                    if (i != 0) {
                        this.field_71439_g.inventory.changeCurrentItem(i);

                        if (this.field_71474_y.noclip) {
                            if (i > 0) i = 1;
                            if (i < 0) i = -1;
                            this.field_71474_y.noclipRate += (float) i * 0.25F;
                        }
                    }

                    if (this.field_71462_r == null) {
                        if (!this.field_71415_G && Mouse.getEventButtonState()) {
                            ((Minecraft) ((Object) this)).setIngameFocus();
                        }
                    } else {
                        this.field_71462_r.handleMouseInput();
                    }
                }
                FMLCommonHandler.instance()
                    .fireMouseInput();
            }

            if (this.field_71429_W > 0) {
                --this.field_71429_W;
            }

            this.field_71424_I.endStartSection("keyboard");
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
                    if (Keyboard.getEventKey() == 62 && this.field_71460_t != null) {
                        this.field_71460_t.deactivateShader();
                    }

                    if (this.field_71462_r != null) {
                        this.field_71462_r.handleKeyboardInput();
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
                            this.field_71474_y.setOptionValue(GameSettings.Options.RENDER_DISTANCE, flag ? -1 : 1);
                        }

                        if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
                            this.field_71438_f.loadRenderers();
                        }

                        if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
                            this.field_71474_y.advancedItemTooltips = !this.field_71474_y.advancedItemTooltips;
                            this.field_71474_y.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
                            RenderManager.debugBoundingBox = !RenderManager.debugBoundingBox;
                        }

                        if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
                            this.field_71474_y.pauseOnLostFocus = !this.field_71474_y.pauseOnLostFocus;
                            this.field_71474_y.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 59) {
                            this.field_71474_y.hideGUI = !this.field_71474_y.hideGUI;
                        }

                        if (Keyboard.getEventKey() == 61) {
                            this.field_71474_y.showDebugInfo = !this.field_71474_y.showDebugInfo;
                            this.field_71474_y.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                        }

                        if (this.field_71474_y.keyBindTogglePerspective.isPressed()) {
                            ++this.field_71474_y.thirdPersonView;
                            if (this.field_71474_y.thirdPersonView > 2) {
                                this.field_71474_y.thirdPersonView = 0;
                            }
                        }

                        if (this.field_71474_y.keyBindSmoothCamera.isPressed()) {
                            this.field_71474_y.smoothCamera = !this.field_71474_y.smoothCamera;
                        }
                    }

                    if (this.field_71474_y.showDebugInfo && this.field_71474_y.showDebugProfilerChart) {
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
                if (this.field_71474_y.keyBindsHotbar[j].isPressed()) {
                    this.field_71439_g.inventory.currentItem = j;
                }
            }

            flag = this.field_71474_y.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.field_71474_y.keyBindInventory.isPressed()) {
                if (this.field_71442_b.func_110738_j()) {
                    this.field_71439_g.func_110322_i();
                } else {
                    ((Minecraft) ((Object) this)).getNetHandler()
                        .addToSendQueue(
                            new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    ((Minecraft) ((Object) this)).displayGuiScreen(new GuiInventory(this.field_71439_g));
                }
            }

            while (this.field_71474_y.keyBindDrop.isPressed()) {
                this.field_71439_g.dropOneItem(GuiScreen.isCtrlKeyDown());
            }

            while (this.field_71474_y.keyBindChat.isPressed() && flag) {
                ((Minecraft) ((Object) this)).displayGuiScreen(new GuiChat());
            }

            if (this.field_71462_r == null && this.field_71474_y.keyBindCommand.isPressed() && flag) {
                ((Minecraft) ((Object) this)).displayGuiScreen(new GuiChat("/"));
            }

            if (this.field_71439_g.isUsingItem()) {
                if (!this.field_71474_y.keyBindUseItem.getIsKeyPressed()) {
                    this.field_71442_b.onStoppedUsingItem(this.field_71439_g);
                }
                label391: while (true) {
                    if (!this.field_71474_y.keyBindAttack.isPressed()) {
                        while (this.field_71474_y.keyBindUseItem.isPressed()) {}
                        while (true) {
                            if (!this.field_71474_y.keyBindPickBlock.isPressed()) continue;
                            break label391;
                        }
                    }
                }
            } else {
                while (this.field_71474_y.keyBindAttack.isPressed()) {
                    ((Minecraft) ((Object) this)).func_147116_af();
                }
                while (this.field_71474_y.keyBindUseItem.isPressed()) {
                    ((Minecraft) ((Object) this)).func_147121_ag();
                }
                while (this.field_71474_y.keyBindPickBlock.isPressed()) {
                    ((Minecraft) ((Object) this)).func_147112_ai();
                }
            }

            if (this.field_71474_y.keyBindUseItem.getIsKeyPressed() && this.field_71467_ac == 0
                && !this.field_71439_g.isUsingItem()) {
                ((Minecraft) ((Object) this)).func_147121_ag();
            }

            ((Minecraft) ((Object) this)).func_147115_a(
                this.field_71462_r == null && this.field_71474_y.keyBindAttack.getIsKeyPressed() && this.field_71415_G);
        }

        if (this.field_71441_e != null) {
            if (this.field_71439_g != null) {
                ++this.field_71457_ai;
                if (this.field_71457_ai == 30) {
                    this.field_71457_ai = 0;
                    this.field_71441_e.joinEntityInSurroundings(this.field_71439_g);
                }
            }

            this.field_71424_I.endStartSection("gameRenderer");
            if (!this.isGamePaused()) {
                this.field_71460_t.updateRenderer();
            }

            this.field_71424_I.endStartSection("levelRenderer");
            if (!this.isGamePaused()) {
                if (!isStop) this.field_71438_f.updateClouds();
            }

            this.field_71424_I.endStartSection("level");
            if (!this.isGamePaused()) {
                if (this.field_71441_e.lastLightningBolt > 0) {
                    if (!isStop) --this.field_71441_e.lastLightningBolt;
                }
                this.field_71441_e.updateEntities();
            }
        }

        if (!this.isGamePaused()) {
            if (!isStop) this.field_147126_aw.update();
            if (!isStop) this.field_147127_av.update();
        }

        if (this.field_71441_e != null) {
            if (!this.isGamePaused()) {
                if (!isStop) this.field_71441_e
                    .setAllowedSpawnTypes(this.field_71441_e.difficultySetting != EnumDifficulty.PEACEFUL, true);

                try {
                    if (!isStop) this.field_71441_e.tick();
                } catch (Throwable throwable2) {
                    crashreport = CrashReport.makeCrashReport(throwable2, "Exception in world tick");
                    if (this.field_71441_e == null) {
                        crashreportcategory = crashreport.makeCategory("Affected level");
                        crashreportcategory.addCrashSection("Problem", "Level is null!");
                    } else {
                        this.field_71441_e.addWorldInfoToCrashReport(crashreport);
                    }
                    throw new ReportedException(crashreport);
                }
            }

            this.field_71424_I.endStartSection("animateTick");
            if (!this.isGamePaused() && this.field_71441_e != null) {
                if (!isStop) this.field_71441_e.doVoidFogParticles(
                    MathHelper.floor_double(this.field_71439_g.posX),
                    MathHelper.floor_double(this.field_71439_g.posY),
                    MathHelper.floor_double(this.field_71439_g.posZ));
            }

            this.field_71424_I.endStartSection("particles");
            if (!this.isGamePaused()) {
                if (!isStop) this.field_71452_i.updateEffects();
            }
        } else if (this.field_71453_ak != null) {
            this.field_71424_I.endStartSection("pendingConnection");
            this.field_71453_ak.processReceivedPackets();
        }

        FMLCommonHandler.instance()
            .onPostClientTick();
        this.field_71424_I.endSection();
        this.field_71423_H = Minecraft.getSystemTime();
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "runGameLoop", at = @At("HEAD"), cancellable = true)
    private void mixin$runGameLoop(CallbackInfo ci) {
        if (TimeStopPocketWatch.isTimeStopped()) {
            ci.cancel();
        } else return;

        this.field_71424_I.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested()) {
            ((Minecraft) ((Object) this)).shutdown();
        }

        if (this.isGamePaused() && this.field_71441_e != null) {
            float f = this.field_71428_T.renderPartialTicks;
            this.field_71428_T.updateTimer();
            this.field_71428_T.renderPartialTicks = f;
        } else {
            this.field_71428_T.updateTimer();
        }

        if ((this.field_71441_e == null || this.field_71462_r == null) && this.field_71468_ad) {
            this.field_71468_ad = false;
            ((Minecraft) ((Object) this)).refreshResources();
        }

        long j = System.nanoTime();
        this.field_71424_I.startSection("tick");

        for (int i = 0; i < this.field_71428_T.elapsedTicks; ++i) {
            ((Minecraft) ((Object) this)).runTick();
        }

        this.field_71424_I.endStartSection("preRenderErrors");
        long k = System.nanoTime() - j;
        ((Minecraft) ((Object) this)).checkGLError("Pre render");
        RenderBlocks.fancyGrass = this.field_71474_y.fancyGraphics;
        this.field_71424_I.endStartSection("sound");
        this.field_147127_av.setListener(this.field_71439_g, this.field_71428_T.renderPartialTicks);
        this.field_71424_I.endSection();
        this.field_71424_I.startSection("render");
        GL11.glPushMatrix();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        ((Minecraft) ((Object) this)).framebufferMc.bindFramebuffer(true);
        this.field_71424_I.startSection("display");
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (this.field_71439_g != null && this.field_71439_g.isEntityInsideOpaqueBlock()) {
            this.field_71474_y.thirdPersonView = 0;
        }

        this.field_71424_I.endSection();

        if (!((Minecraft) ((Object) this)).skipRenderWorld) {
            FMLCommonHandler.instance()
                .onRenderTickStart(this.field_71428_T.renderPartialTicks);
            this.field_71424_I.endStartSection("gameRenderer");
            this.field_71460_t.updateCameraAndRender(this.field_71428_T.renderPartialTicks);
            this.field_71424_I.endSection();
            FMLCommonHandler.instance()
                .onRenderTickEnd(this.field_71428_T.renderPartialTicks);
        }

        GL11.glFlush();
        this.field_71424_I.endSection();

        if (!Display.isActive() && ((Minecraft) ((Object) this)).fullscreen) {
            ((Minecraft) ((Object) this)).toggleFullscreen();
        }

        if (this.field_71474_y.showDebugInfo && this.field_71474_y.showDebugProfilerChart) {
            if (!this.field_71424_I.profilingEnabled) {
                this.field_71424_I.clearProfiling();
            }
            this.field_71424_I.profilingEnabled = true;
            ((Minecraft) ((Object) this)).displayDebugInfo(k);
        } else {
            this.field_71424_I.profilingEnabled = false;
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
        this.field_71460_t.func_152430_c(this.field_71428_T.renderPartialTicks);
        GL11.glPopMatrix();
        this.field_71424_I.startSection("root");
        ((Minecraft) ((Object) this)).func_147120_f();
        Thread.yield();
        this.field_71424_I.startSection("stream");
        this.field_71424_I.startSection("update");
        ((Minecraft) ((Object) this)).field_152353_at.func_152935_j();
        this.field_71424_I.endStartSection("submit");
        ((Minecraft) ((Object) this)).field_152353_at.func_152922_k();
        this.field_71424_I.endSection();
        this.field_71424_I.endSection();
        ((Minecraft) ((Object) this)).checkGLError("Post render");
        ++((Minecraft) ((Object) this)).fpsCounter;

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

        this.field_71424_I.endSection();

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
        if (ClientUtils.onBeforePickBlock(this.field_71439_g, this.field_71441_e, false)) {
            ci.cancel();
        }
    }
}
