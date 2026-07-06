package com.science.gtnl.utils.event;

import static com.science.gtnl.ScienceNotLeisure.network;
import static com.science.gtnl.common.packet.client.SoundHandler.PLAYING_SOUNDS;
import static com.science.gtnl.common.render.tile.BallRenderer.visualStateMap;

import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import com.gtnewhorizon.gtnhlib.client.title.TitleAPI;
import com.reavaritia.client.render.CustomEntityRenderer;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.common.item.items.NullPointerException;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionHitEffectState;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionState;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionType;
import com.science.gtnl.common.packet.NBTUpdatePacket;
import com.science.gtnl.common.render.item.ItemNullPointerExceptionRender;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.mixins.early.minecraft.AccessorGuiChat;
import com.science.gtnl.utils.ClientUtils;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.client.ElectricJukeboxSound;

public class SubscribeEventClientUtils {

    private static final ResourceLocation PROJECTED_SHIELD_MODEL = ResourceHandler
        .getResource("models/shieldSphere.obj");
    private static final ResourceLocation PROJECTED_SHIELD_TEXTURE = ResourceHandler
        .getResource("textures/models/ShieldSphere.png");
    private static IModelCustom projectedShieldModel;
    public static final Random RANDOM = new Random();
    public static String HALO_NOISE_ICON_TEXTURE = ModList.ScienceNotLeisure.resourceDomain + ":halonoise";
    public static IIcon haloNoiseIcon;

    public static String CHEAT_WRENCH_ICON_TEXTURE = "nei:cheat_speical";
    public static IIcon cheatWrenchIcon;

    public static boolean HAS_HANDLED_DEATH_MESSAGE = false;
    public static float LAST_HEALTH = 20.0f;
    private static ItemStack wyvernHelmetProjection;
    private static ItemStack wyvernChestProjection;
    private static ItemStack wyvernLegsProjection;
    private static ItemStack wyvernBootsProjection;
    private static ItemStack draconicHelmetProjection;
    private static ItemStack draconicChestProjection;
    private static ItemStack draconicLegsProjection;
    private static ItemStack draconicBootsProjection;

    public static void registerAllIcons(net.minecraft.client.renderer.texture.IIconRegister ir) {
        haloNoiseIcon = ir.registerIcon(HALO_NOISE_ICON_TEXTURE);
        cheatWrenchIcon = ir.registerIcon(CHEAT_WRENCH_ICON_TEXTURE);
    }

    // Player
    @SubscribeEvent
    public void onClientConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent aEvent) {
        PLAYING_SOUNDS.clear();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer != null) {
            BaubleItem.removePlayer(minecraft.thePlayer.getUniqueID());
            DraconicArmorProjectionState.clear(minecraft.thePlayer.getUniqueID());
            DraconicArmorProjectionHitEffectState.clear(minecraft.thePlayer.getUniqueID());
        }
        DraconicArmorProjectionHitEffectState.clearAll();
        TickrateAPI.changeServerTickrate(MainConfig.tickrate.defaultTickrate);
        TickrateAPI.changeClientTickrate(null, MainConfig.tickrate.defaultTickrate);
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        ItemStack held = player.getCurrentEquippedItem();
        if (held == null) return;

        if (held.getItem() != ItemLoader.veinMiningPickaxe) return;

        NBTTagCompound nbt = held.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            held.setTagCompound(nbt);
        }

        boolean rightClickHeld = Mouse.isButtonDown(1);

        if (player.isSneaking() && !rightClickHeld) {
            if (event.dwheel == 0) return;
            int oldRange = nbt.hasKey("range") ? nbt.getInteger("range") : 0;
            int newRange = oldRange;

            if (event.dwheel > 0) {
                newRange++;
            } else {
                newRange--;
            }

            if (newRange < -1) newRange = -1;
            if (newRange > MainConfig.item.vein_miner_pickaxe.maxRange)
                newRange = MainConfig.item.vein_miner_pickaxe.maxRange;

            if (newRange != oldRange) {
                nbt.setInteger("range", newRange);
                ClientUtils.showSubtitle("Tooltip_VeinMiningPickaxe_00", newRange);
                network.sendToServer(new NBTUpdatePacket(player.inventory.currentItem, held));
                event.setCanceled(true);
            }
        }

        if (!player.isSneaking() && rightClickHeld) {
            if (event.dwheel == 0) return;
            int oldAmount = nbt.hasKey("amount") ? nbt.getInteger("amount") : 0;
            int newAmount = oldAmount;

            if (event.dwheel > 0) {
                newAmount += 10000;
            } else {
                newAmount -= 10000;
            }

            if (newAmount < 0) newAmount = 0;
            if (newAmount > MainConfig.item.vein_miner_pickaxe.maxAmount)
                newAmount = MainConfig.item.vein_miner_pickaxe.maxAmount;

            if (newAmount != oldAmount) {
                nbt.setInteger("amount", newAmount);
                ClientUtils.showSubtitle("Tooltip_VeinMiningPickaxe_01", newAmount);
                network.sendToServer(new NBTUpdatePacket(player.inventory.currentItem, held));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END && !MainConfig.other.enableDeathIncompleteMessage) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (event.phase == TickEvent.Phase.END) {
            suppressProjectedArmorHurtVisual(mc.thePlayer);
            for (Object playerObject : mc.theWorld.playerEntities) {
                if (playerObject instanceof EntityPlayer player && player != mc.thePlayer) {
                    suppressProjectedArmorHurtVisual(player);
                }
            }
            DraconicArmorProjectionHitEffectState.tick();
        }

        float currentHealth = mc.thePlayer.getHealth();

        if (LAST_HEALTH > 0 && currentHealth <= 0
            && !HAS_HANDLED_DEATH_MESSAGE
            && mc.currentScreen instanceof GuiChat guiChat) {
            String chat = ((AccessorGuiChat) guiChat).getInputField()
                .getText();
            mc.thePlayer.sendChatMessage(chat + (chat.startsWith("/") ? "" : "-"));
            mc.thePlayer.closeScreen();
            HAS_HANDLED_DEATH_MESSAGE = true;
        }

        if (currentHealth > 0) {
            HAS_HANDLED_DEATH_MESSAGE = false;
        }

        LAST_HEALTH = currentHealth;
    }

    @SubscribeEvent
    public void onIngameMenuGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player != null && !player.capabilities.isCreativeMode) {
            PotionEffect effect = player.getActivePotionEffect(EffectLoader.awe);

            if (effect != null && event.gui instanceof GuiIngameMenu) {
                TitleAPI.setTimes(10, 100, 20);
                TitleAPI.setTitle(new ChatComponentTranslation("Awe_Cancel_01"));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onInventoryGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (player != null && !player.capabilities.isCreativeMode) {
            PotionEffect effect = player.getActivePotionEffect(EffectLoader.awe);

            if (effect != null && event.gui instanceof GuiInventory) {
                String[] messages = { "Awe_Cancel_02_01", "Awe_Cancel_02_02" };
                String message = messages[RANDOM.nextInt(messages.length)];
                TitleAPI.setTimes(10, 100, 20);
                TitleAPI.setTitle(new ChatComponentTranslation(message));

                event.setCanceled(true);
            }
        }
    }

    // Render
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            registerAllIcons(event.map);
        }
    }

    @SubscribeEvent
    public void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.item;
        if (stack != null && stack.getItem() instanceof NullPointerException) {
            ItemNullPointerExceptionRender.renderItem(IItemRenderer.ItemRenderType.ENTITY, event.item, true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderLivingEvent.Pre event) {
        if (!(event.entity instanceof EntityPlayer player)) return;

        if (player.isPotionActive(EffectLoader.shimmering) || player.isPotionActive(EffectLoader.ghostly_shape)) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderLivingEvent.Post event) {
        if (!(event.entity instanceof EntityPlayer player)) return;

        if (player.isPotionActive(EffectLoader.shimmering) || player.isPotionActive(EffectLoader.ghostly_shape)) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
        }
    }

    @SubscribeEvent
    public void onRenderProjectedArmor(RenderPlayerEvent.SetArmorModel event) {
        DraconicArmorProjectionType projectionType = DraconicArmorProjectionState.get(event.entityPlayer);
        if (projectionType == null) {
            return;
        }

        ItemStack projectedArmor = getProjectedArmorStack(projectionType, event.slot);
        if (projectedArmor == null || !(projectedArmor.getItem() instanceof ItemArmor itemArmor)) {
            return;
        }

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(RenderBiped.getArmorResource(event.entityPlayer, projectedArmor, event.slot, null));
        ModelBiped model = event.slot == 2 ? event.renderer.modelArmor : event.renderer.modelArmorChestplate;
        model.bipedHead.showModel = event.slot == 0;
        model.bipedHeadwear.showModel = event.slot == 0;
        model.bipedBody.showModel = event.slot == 1 || event.slot == 2;
        model.bipedRightArm.showModel = event.slot == 1;
        model.bipedLeftArm.showModel = event.slot == 1;
        model.bipedRightLeg.showModel = event.slot == 2 || event.slot == 3;
        model.bipedLeftLeg.showModel = event.slot == 2 || event.slot == 3;
        model = net.minecraftforge.client.forgeHooksClient
            .getArmorModel(event.entityPlayer, projectedArmor, event.slot, model);
        event.renderer.setRenderPassModel(model);
        model.onGround = event.renderer.modelBipedMain.onGround;
        model.isRiding = event.renderer.modelBipedMain.isRiding;
        model.isChild = event.renderer.modelBipedMain.isChild;

        int color = itemArmor.getColor(projectedArmor);
        if (color != -1) {
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            GL11.glColor3f(red, green, blue);
            event.result = projectedArmor.isItemEnchanted() ? 31 : 16;
            return;
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        event.result = projectedArmor.isItemEnchanted() ? 15 : 1;
    }

    @SubscribeEvent
    public void onRenderProjectedShield(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (!DraconicArmorProjectionHitEffectState.isActive(player)) {
            return;
        }

        float shieldPower = DraconicArmorProjectionHitEffectState.getShieldPower(player);
        int remainingTicks = DraconicArmorProjectionHitEffectState.getRemainingTicks(player);
        float alpha = remainingTicks / 5.0F;
        float red = 1.0F - shieldPower;
        float blue = shieldPower;

        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(PROJECTED_SHIELD_TEXTURE);

        if (Minecraft.getMinecraft().thePlayer == player) {
            GL11.glTranslated(0.0D, -0.5D, 0.0D);
        } else {
            EntityPlayer viewingPlayer = Minecraft.getMinecraft().thePlayer;
            double translationXLT = player.prevPosX - viewingPlayer.prevPosX;
            double translationYLT = player.prevPosY - viewingPlayer.prevPosY;
            double translationZLT = player.prevPosZ - viewingPlayer.prevPosZ;
            double translationX = translationXLT
                + (((player.posX - viewingPlayer.posX) - translationXLT) * event.partialRenderTick);
            double translationY = translationYLT
                + (((player.posY - viewingPlayer.posY) - translationYLT) * event.partialRenderTick);
            double translationZ = translationZLT
                + (((player.posZ - viewingPlayer.posZ) - translationZLT) * event.partialRenderTick);
            GL11.glTranslated(translationX, translationY + 1.1D, translationZ);
        }

        GL11.glScalef(1.0F, 1.5F, 1.0F);
        GL11.glColor4f(red, 0.0F, blue, alpha);
        getProjectedShieldModel().renderAll();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) {
            return;
        }
        if (!DraconicArmorProjectionHitEffectState.isActive(player)) {
            return;
        }
        if (event.name != null && event.name.startsWith("damage.hit")) {
            event.setCanceled(true);
        }
    }

    // Sound
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTickEvent(TickEvent.ClientTickEvent aEvent) {
        if (aEvent.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.thePlayer;

            if (player != null && mc.theWorld != null && !PLAYING_SOUNDS.isEmpty()) {
                double playerX = player.posX;
                double playerY = player.posY;
                double playerZ = player.posZ;

                for (Map.Entry<String, ElectricJukeboxSound> entry : PLAYING_SOUNDS.entrySet()) {
                    ElectricJukeboxSound sound = entry.getValue();
                    sound.xPosition = (float) playerX;
                    sound.yPosition = (float) playerY;
                    sound.zPosition = (float) playerZ;
                }
            }
        }
    }

    // World
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        visualStateMap.clear();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
            if (renderer instanceof CustomEntityRenderer customEntityRenderer) {
                customEntityRenderer.resetShader();
            }
        }
        DraconicArmorProjectionHitEffectState.clearAll();
        TimeStopPocketWatch.setTimeStopped(false);
    }

    private void suppressProjectedArmorHurtVisual(EntityPlayer player) {
        if (!DraconicArmorProjectionHitEffectState.isActive(player)) {
            return;
        }
        player.hurtTime = 0;
        player.maxHurtTime = 0;
    }

    private IModelCustom getProjectedShieldModel() {
        if (projectedShieldModel == null) {
            projectedShieldModel = AdvancedModelLoader.loadModel(PROJECTED_SHIELD_MODEL);
        }
        return projectedShieldModel;
    }

    private ItemStack getProjectedArmorStack(DraconicArmorProjectionType projectionType, int slot) {
        return switch (projectionType) {
            case WYVERN -> switch (slot) {
                    case 0 -> getWyvernHelmetProjection();
                    case 1 -> getWyvernChestProjection();
                    case 2 -> getWyvernLegsProjection();
                    case 3 -> getWyvernBootsProjection();
                    default -> null;
                };
            case DRACONIC -> switch (slot) {
                    case 0 -> getDraconicHelmetProjection();
                    case 1 -> getDraconicChestProjection();
                    case 2 -> getDraconicLegsProjection();
                    case 3 -> getDraconicBootsProjection();
                    default -> null;
                };
        };
    }

    private ItemStack getWyvernHelmetProjection() {
        if (wyvernHelmetProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            wyvernHelmetProjection = new ItemStack(ModItems.wyvernHelm);
        }
        return wyvernHelmetProjection;
    }

    private ItemStack getWyvernChestProjection() {
        if (wyvernChestProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            wyvernChestProjection = new ItemStack(ModItems.wyvernChest);
        }
        return wyvernChestProjection;
    }

    private ItemStack getWyvernLegsProjection() {
        if (wyvernLegsProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            wyvernLegsProjection = new ItemStack(ModItems.wyvernLeggs);
        }
        return wyvernLegsProjection;
    }

    private ItemStack getWyvernBootsProjection() {
        if (wyvernBootsProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            wyvernBootsProjection = new ItemStack(ModItems.wyvernBoots);
        }
        return wyvernBootsProjection;
    }

    private ItemStack getDraconicHelmetProjection() {
        if (draconicHelmetProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            draconicHelmetProjection = new ItemStack(ModItems.draconicHelm);
        }
        return draconicHelmetProjection;
    }

    private ItemStack getDraconicChestProjection() {
        if (draconicChestProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            draconicChestProjection = new ItemStack(ModItems.draconicChest);
        }
        return draconicChestProjection;
    }

    private ItemStack getDraconicLegsProjection() {
        if (draconicLegsProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            draconicLegsProjection = new ItemStack(ModItems.draconicLeggs);
        }
        return draconicLegsProjection;
    }

    private ItemStack getDraconicBootsProjection() {
        if (draconicBootsProjection == null) {
            // Reused as a render-only sentinel stack. Do not mutate.
            draconicBootsProjection = new ItemStack(ModItems.draconicBoots);
        }
        return draconicBootsProjection;
    }
}
