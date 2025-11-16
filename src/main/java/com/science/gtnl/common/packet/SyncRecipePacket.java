package com.science.gtnl.common.packet;

import com.github.dcysteine.neicustomdiagram.main.Logger;
import com.github.dcysteine.neicustomdiagram.main.NeiCustomDiagram;
import com.github.dcysteine.neicustomdiagram.main.Registry;
import com.github.dcysteine.neicustomdiagram.main.config.ConfigOptions;
import com.science.gtnl.loader.RecipeLoader;
import com.science.gtnl.mixins.late.NEICustomDiagram.AccessorNeiCustomDiagram;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gregtech.api.enums.Mods;
import io.netty.buffer.ByteBuf;

public class SyncRecipePacket implements IMessage {

    public SyncRecipePacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SyncRecipePacket, IMessage> {

        @Override
        public IMessage onMessage(SyncRecipePacket message, MessageContext ctx) {
            RecipeLoader.loadRecipesServerStart();
            if (Mods.NEICustomDiagrams.isModLoaded()) {
                loadNEICustomDiagram();
            }
            return null;
        }
    }

    @Optional.Method(modid = "neicustomdiagram")
    public static void loadNEICustomDiagram() {
        AccessorNeiCustomDiagram accessor = (AccessorNeiCustomDiagram) (Object) NeiCustomDiagram.instance;
        if (!ConfigOptions.GENERATE_DIAGRAMS_ON_CLIENT_CONNECT.get() || accessor.getHasGenerated()) {
            return;
        }
        Logger.MOD.info("Mod pre-connect starting...");

        Registry.INSTANCE.generateDiagramGroups();
        Registry.INSTANCE.cleanUp();
        accessor.setHasGenerated(true);

        Logger.MOD.info("Mod pre-connect complete!");
    }
}
