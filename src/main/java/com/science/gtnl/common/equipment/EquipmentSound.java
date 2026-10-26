package com.science.gtnl.common.equipment;

import java.util.List;

import com.gtnllib.utils.MathUtils;
import com.science.gtnl.config.EquipmentSoundConfig;
import com.science.gtnl.config.EquipmentSoundConfig.Channel;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public enum EquipmentSound {

    SHIELD_HIT(Group.CELESTWEAVE, "shield_hit", EquipmentSoundConfig.celestweave.shieldHit),
    SHIELD_SAVE(Group.CELESTWEAVE, "shield_save", EquipmentSoundConfig.celestweave.shieldSave),
    REFLECT(Group.CELESTWEAVE, "reflect", EquipmentSoundConfig.celestweave.reflect),
    LIGHTNING(Group.CELESTWEAVE, "lightning", EquipmentSoundConfig.celestweave.lightning),
    DASH(Group.CELESTWEAVE, "dash", EquipmentSoundConfig.celestweave.dash),
    LAND(Group.CELESTWEAVE, "land", EquipmentSoundConfig.celestweave.land),
    WINGS(Group.CELESTWEAVE, "wings", EquipmentSoundConfig.celestweave.wings),
    CHARGE(Group.RAILGUN, "charge", EquipmentSoundConfig.railgun.charge),
    FIRE(Group.RAILGUN, "fire", EquipmentSoundConfig.railgun.fire),
    IMPACT(Group.RAILGUN, "impact", EquipmentSoundConfig.railgun.impact),
    BEAM(Group.RAILGUN, "beam", EquipmentSoundConfig.railgun.beam),
    CHAIN(Group.RAILGUN, "chain", EquipmentSoundConfig.railgun.chain);

    public enum Group {

        CELESTWEAVE("celestweave", EquipmentSoundConfig.celestweave.all),
        RAILGUN("railgun", EquipmentSoundConfig.railgun.all);

        public final String id;
        public final Channel all;

        Group(String id, Channel all) {
            this.id = id;
            this.all = all;
        }

        public String lang() {
            return "gtnl.gui.config.sound." + id;
        }

        public List<EquipmentSound> sounds() {
            List<EquipmentSound> sounds = new ObjectArrayList<>();
            for (EquipmentSound sound : EquipmentSound.values()) if (sound.group == this) sounds.add(sound);
            return sounds;
        }
    }

    public final Group group;
    public final String id;
    public final Channel channel;

    EquipmentSound(Group group, String id, Channel channel) {
        this.group = group;
        this.id = id;
        this.channel = channel;
    }

    public String lang() {
        return group.lang() + "." + id;
    }

    public boolean on() {
        return group.all.enabled && channel.enabled;
    }

    public float volume() {
        return on() ? MathUtils.clampUnit(group.all.volume, 1F) * MathUtils.clampUnit(channel.volume, 1F) : 0F;
    }
}
