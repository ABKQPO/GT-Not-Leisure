package com.science.gtnl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.client.handler.ParticleHandler;
import com.gtnllib.client.GTNLLibClient;
import com.gtnllib.client.fx.world.FxEffects;
import com.science.gtnl.client.gui.GTNLClientGuiHandler;
import com.science.gtnl.common.render.entity.EntityParticleBeam;
import com.science.gtnl.loader.ContentLoaders;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        FxEffects.register();
        GTNLLibClient.preInit();
        ContentLoaders.clientPreInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ContentLoaders.clientInit();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return GTNLClientGuiHandler.clientElement(ID, player, world, x, y, z);
    }

    @Override
    public EntityPlayer getEntityPlayerFromContext(MessageContext ctx) {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public EntityParticleBeam particleBeam(double x, double y, double z, double masterX, double masterY, double masterZ,
        double radius, EntityParticleBeam oldBeam, boolean render) {
        EntityParticleBeam beam = oldBeam;
        boolean inRange = ParticleHandler.isInRange(masterX, masterY, masterZ, 256);
        if (beam == null || beam.isDead) {
            if (inRange) {
                beam = new EntityParticleBeam(x, y, z, masterX, masterY, masterZ, radius);
                FMLClientHandler.instance()
                    .getClient().effectRenderer.addEffect(beam);
            }
        } else if (!inRange) {
            beam.setDead();
            return null;
        } else {
            beam.update();
        }
        return beam;
    }
}
