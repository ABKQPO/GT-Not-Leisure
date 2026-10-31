package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultFloat;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.RangeFloat;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "sound",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.CLIENT_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Comment("Client sounds of Celestweave equipment; only changes what this client hears")
public class EquipmentSoundConfig {

    @LangKey("gtnl.gui.config.sound.celestweave")
    public static final Celestweave celestweave = new Celestweave();

    @LangKey("gtnl.gui.config.sound.railgun")
    public static final Railgun railgun = new Railgun();

    public static void save() {
        ConfigRegistry.save(EquipmentSoundConfig.class);
    }

    @Comment("Celestweave armor")
    public static class Celestweave {

        @LangKey("gtnl.gui.config.sound.all")
        public final Channel all = new Channel();

        @LangKey("gtnl.gui.config.sound.celestweave.shield_hit")
        public final Channel shieldHit = new Channel();

        @LangKey("gtnl.gui.config.sound.celestweave.shield_save")
        public final Channel shieldSave = new Channel();

        @LangKey("gtnl.gui.config.sound.celestweave.reflect")
        public final Channel reflect = new Channel();

        @LangKey("gtnl.gui.config.sound.celestweave.lightning")
        public final Channel lightning = new Channel();

        @LangKey("gtnl.gui.config.sound.celestweave.dash")
        public final Channel dash = new Channel();

        @LangKey("gtnl.gui.config.sound.celestweave.land")
        public final Channel land = new Channel();

        @LangKey("gtnl.gui.config.sound.celestweave.wings")
        public final Channel wings = new Channel();
    }

    @Comment("Celestweave Lance")
    public static class Railgun {

        @LangKey("gtnl.gui.config.sound.all")
        public final Channel all = new Channel();

        @LangKey("gtnl.gui.config.sound.railgun.charge")
        public final Channel charge = new Channel();

        @LangKey("gtnl.gui.config.sound.railgun.fire")
        public final Channel fire = new Channel();

        @LangKey("gtnl.gui.config.sound.railgun.impact")
        public final Channel impact = new Channel();

        @LangKey("gtnl.gui.config.sound.railgun.beam")
        public final Channel beam = new Channel();

        @LangKey("gtnl.gui.config.sound.railgun.chain")
        public final Channel chain = new Channel();
    }

    @Comment("One sound: whether it plays and its volume")
    public static class Channel {

        @Comment("Play this sound")
        @LangKey("gtnl.gui.config.sound.enabled")
        @DefaultBoolean(true)
        public boolean enabled = true;

        @Comment("Volume of this sound, 0 to 1")
        @LangKey("gtnl.gui.config.sound.volume")
        @DefaultFloat(1F)
        @RangeFloat(min = 0F, max = 1F)
        public float volume = 1F;
    }
}
