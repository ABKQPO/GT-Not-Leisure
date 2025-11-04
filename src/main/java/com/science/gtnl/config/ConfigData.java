package com.science.gtnl.config;

import java.util.Objects;

import io.netty.buffer.ByteBuf;

public class ConfigData {

    public boolean enableDeleteRecipe;
    public boolean enableAprilFoolRecipe;
    public boolean enableShowDelRecipeTitle;
    public boolean enableSomethingRecipe;

    public boolean showTickrateMessages;

    public boolean enableShowJoinMessage;
    public boolean enableShowAddMods;

    public boolean enableDebugMode;

    public void write(ByteBuf buf) {
        buf.writeBoolean(enableDeleteRecipe);
        buf.writeBoolean(enableAprilFoolRecipe);
        buf.writeBoolean(enableShowDelRecipeTitle);
        buf.writeBoolean(enableSomethingRecipe);
        buf.writeBoolean(showTickrateMessages);
        buf.writeBoolean(enableShowJoinMessage);
        buf.writeBoolean(enableShowAddMods);
        buf.writeBoolean(enableDebugMode);
    }

    public void read(ByteBuf buf) {
        enableDeleteRecipe = buf.readBoolean();
        enableAprilFoolRecipe = buf.readBoolean();
        enableShowDelRecipeTitle = buf.readBoolean();
        enableSomethingRecipe = buf.readBoolean();
        showTickrateMessages = buf.readBoolean();
        enableShowJoinMessage = buf.readBoolean();
        enableShowAddMods = buf.readBoolean();
        enableDebugMode = buf.readBoolean();
    }

    public void readFromConfig() {
        enableDeleteRecipe = MainConfig.enableDeleteRecipe;
        enableAprilFoolRecipe = MainConfig.enableAprilFool;
        enableShowDelRecipeTitle = MainConfig.enableShowDelRecipeTitle;
        enableSomethingRecipe = MainConfig.enableSomethingRecipe;
        enableShowJoinMessage = MainConfig.enableShowJoinMessage;
        enableShowAddMods = MainConfig.enableShowAddMods;
        enableDebugMode = MainConfig.enableDebugMode;
    }

    public void writeToConfig() {
        MainConfig.enableDeleteRecipe = enableDeleteRecipe;
        MainConfig.enableAprilFool = enableAprilFoolRecipe;
        MainConfig.enableShowDelRecipeTitle = enableShowDelRecipeTitle;
        MainConfig.enableSomethingRecipe = enableSomethingRecipe;
        MainConfig.enableShowJoinMessage = enableShowJoinMessage;
        MainConfig.enableShowAddMods = enableShowAddMods;
        MainConfig.enableDebugMode = enableDebugMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigData that)) return false;
        return enableDeleteRecipe == that.enableDeleteRecipe && enableAprilFoolRecipe == that.enableAprilFoolRecipe
            && enableShowDelRecipeTitle == that.enableShowDelRecipeTitle
            && enableSomethingRecipe == that.enableSomethingRecipe
            && showTickrateMessages == that.showTickrateMessages
            && enableShowJoinMessage == that.enableShowJoinMessage
            && enableShowAddMods == that.enableShowAddMods
            && enableDebugMode == that.enableDebugMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            enableDeleteRecipe,
            enableAprilFoolRecipe,
            enableShowDelRecipeTitle,
            enableSomethingRecipe,
            showTickrateMessages,
            enableShowJoinMessage,
            enableShowAddMods,
            enableDebugMode);
    }
}
