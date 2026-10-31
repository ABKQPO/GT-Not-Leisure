package com.science.gtnl.client.bauble;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionHitEffectState;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionState;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public class DraconicArmorProjectionClientEvents {

    private static final ResourceLocation PROJECTED_SHIELD_MODEL = ResourceHandler
        .getResource("models/shieldSphere.obj");
    private static final ResourceLocation PROJECTED_SHIELD_TEXTURE = ResourceHandler
        .getResource("textures/models/ShieldSphere.png");
    private static IModelCustom projectedShieldModel;
    private static ItemStack wyvernHelmetProjection;
    private static ItemStack wyvernChestProjection;
    private static ItemStack wyvernLegsProjection;
    private static ItemStack wyvernBootsProjection;
    private static ItemStack draconicHelmetProjection;
    private static ItemStack draconicChestProjection;
    private static ItemStack draconicLegsProjection;
    private static ItemStack draconicBootsProjection;

    @SubscribeEvent
    public static void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer != null) {
            DraconicArmorProjectionState.clear(minecraft.thePlayer.getUniqueID());
            DraconicArmorProjectionHitEffectState.clear(minecraft.thePlayer.getUniqueID());
        }
        DraconicArmorProjectionHitEffectState.clearAll();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        suppressProjectedArmorHurtVisual(mc.thePlayer);
        for (Object playerObject : mc.theWorld.playerEntities) {
            if (playerObject instanceof EntityPlayer player && player != mc.thePlayer) {
                suppressProjectedArmorHurtVisual(player);
            }
        }
        DraconicArmorProjectionHitEffectState.tick();
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        DraconicArmorProjectionHitEffectState.clearAll();
    }

    @SubscribeEvent
    public static void onRenderProjectedArmor(RenderPlayerEvent.SetArmorModel event) {
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
        model = ForgeHooksClient.getArmorModel(event.entityPlayer, projectedArmor, event.slot, model);
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
    public static void onRenderProjectedShield(RenderPlayerEvent.Post event) {
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
    public static void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
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

    private static void suppressProjectedArmorHurtVisual(EntityPlayer player) {
        if (!DraconicArmorProjectionHitEffectState.isActive(player)) {
            return;
        }
        player.hurtTime = 0;
        player.maxHurtTime = 0;
    }

    private static IModelCustom getProjectedShieldModel() {
        if (projectedShieldModel == null) {
            projectedShieldModel = AdvancedModelLoader.loadModel(PROJECTED_SHIELD_MODEL);
        }
        return projectedShieldModel;
    }

    private static ItemStack getProjectedArmorStack(DraconicArmorProjectionType projectionType, int slot) {
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

    // The projection stacks are render-only sentinels shared by every player. Do not mutate them.
    private static ItemStack getWyvernHelmetProjection() {
        if (wyvernHelmetProjection == null) wyvernHelmetProjection = new ItemStack(ModItems.wyvernHelm);
        return wyvernHelmetProjection;
    }

    private static ItemStack getWyvernChestProjection() {
        if (wyvernChestProjection == null) wyvernChestProjection = new ItemStack(ModItems.wyvernChest);
        return wyvernChestProjection;
    }

    private static ItemStack getWyvernLegsProjection() {
        if (wyvernLegsProjection == null) wyvernLegsProjection = new ItemStack(ModItems.wyvernLeggs);
        return wyvernLegsProjection;
    }

    private static ItemStack getWyvernBootsProjection() {
        if (wyvernBootsProjection == null) wyvernBootsProjection = new ItemStack(ModItems.wyvernBoots);
        return wyvernBootsProjection;
    }

    private static ItemStack getDraconicHelmetProjection() {
        if (draconicHelmetProjection == null) draconicHelmetProjection = new ItemStack(ModItems.draconicHelm);
        return draconicHelmetProjection;
    }

    private static ItemStack getDraconicChestProjection() {
        if (draconicChestProjection == null) draconicChestProjection = new ItemStack(ModItems.draconicChest);
        return draconicChestProjection;
    }

    private static ItemStack getDraconicLegsProjection() {
        if (draconicLegsProjection == null) draconicLegsProjection = new ItemStack(ModItems.draconicLeggs);
        return draconicLegsProjection;
    }

    private static ItemStack getDraconicBootsProjection() {
        if (draconicBootsProjection == null) draconicBootsProjection = new ItemStack(ModItems.draconicBoots);
        return draconicBootsProjection;
    }
}
