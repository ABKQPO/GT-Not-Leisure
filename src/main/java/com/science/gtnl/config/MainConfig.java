package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.science.gtnl.ScienceNotLeisure;

@Config(modid = ScienceNotLeisure.MODID, configSubDirectory = "GTNotLeisure", filename = "GTNotLeisure")
@Config.LangKeyPattern(pattern = "gtnl.gui.config.%cat.%field", fullyQualified = true)
@Comment("General section")
public class MainConfig {

    public static final DebugConfig debug = new DebugConfig();

    public static final ClientConfig client = new ClientConfig();

    public static final EffectConfig effect = new EffectConfig();

    public static final WirelessConfig wireless = new WirelessConfig();

    public static final ItemConfig item = new ItemConfig();

    public static final MachineConfig machine = new MachineConfig();

    public static final MessageConfig message = new MessageConfig();

    public static final OtherConfig other = new OtherConfig();

    public static final ReAvaritiaConfig re_avaritia = new ReAvaritiaConfig();

    public static final RecipeConfig recipe = new RecipeConfig();

    public static final SuperCreeperConfig super_creeper = new SuperCreeperConfig();

    public static final TickrateConfig tickrate = new TickrateConfig();

    public static final MinecraftConfig minecraft = new MinecraftConfig();

}
