package com.science.gtnl.client.celestweave;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CwLook {

    public static final int HEAD = 0, CHEST = 1, LEGS = 2, BOOTS = 3;
    public static final int PACK_NONE = -2, PACK_RAINBOW = -1;
    private static final int[] ARC_PRIORITY = { CHEST, BOOTS, LEGS, HEAD };
    private static final Object[][] PACK_COLORS = { { GTNLItemList.PortableCopperChest, 0xC87533 },
        { GTNLItemList.PortableIronChest, 0xD8D8D8 }, { GTNLItemList.PortableSilverChest, 0xC8D2E1 },
        { GTNLItemList.PortableSteelChest, 0x82878F }, { GTNLItemList.PortableGoldenChest, 0xF2C94C },
        { GTNLItemList.PortableDiamondChest, 0x5EE0E0 }, { GTNLItemList.PortableCrystalChest, 0xBEF0FF },
        { GTNLItemList.PortableObsidianChest, 0x6E4CAA }, { GTNLItemList.PortableNetheriteChest, 0x786060 },
        { GTNLItemList.PortableDarkSteelChest, 0x464650 } };

    public final ItemStack[] worn = new ItemStack[4];
    public int tier;
    public float charge;
    public final float[] charges = new float[4];
    public CelestweaveCosmetic cosmetic = CelestweaveCosmetic.DEFAULT;
    public int theme = CelestweaveCosmetic.DEFAULT_COLOR;
    public boolean goggles, holo, pack, wings, flightInstalled, flightActive;
    public int packColor = PACK_NONE;

    private CwLook() {}

    public static boolean sameStacks(CwLook look, EntityLivingBase entity) {
        for (int type = 0; type < 4; type++)
            if (look.worn[type] != CelestweaveServerEvents.piece(entity, type)) return false;
        return true;
    }

    public static CwLook compute(EntityLivingBase entity) {
        CwLook look = new CwLook();
        for (int type = 0; type < 4; type++) {
            look.worn[type] = CelestweaveServerEvents.piece(entity, type);
            if (look.worn[type] != null) look.charges[type] = (float) CelestweaveEnergy.fraction(look.worn[type]);
        }
        ItemStack source = look.worn[CHEST];
        for (int type : new int[] { HEAD, LEGS, BOOTS }) if (source == null) source = look.worn[type];
        if (source != null) {
            look.tier = CelestweaveData.energyTier(source);
            look.charge = (float) CelestweaveEnergy.fraction(source);
        }
        if (look.worn[CHEST] != null) {
            look.cosmetic = CelestweaveData.cosmetic(look.worn[CHEST]);
            look.theme = look.cosmetic.glowColor();
        }
        ItemStack head = look.worn[HEAD];
        if (head != null) {
            Set<CelestweaveModule> installed = CelestweaveData.installed(head);
            Set<String> hidden = CelestweaveData.idList(head, CelestweaveData.HIDDEN);
            look.goggles = shownWithItem(head, installed, hidden, CelestweaveModule.REVEALING);
            look.holo = shownWithItem(head, installed, hidden, CelestweaveModule.HOLO);
        }
        ItemStack chest = look.worn[CHEST];
        if (chest != null) {
            Set<CelestweaveModule> installed = CelestweaveData.installed(chest);
            Set<String> hidden = CelestweaveData.idList(chest, CelestweaveData.HIDDEN);
            if (shownWithItem(chest, installed, hidden, CelestweaveModule.BACKPACK)) {
                look.pack = true;
                look.packColor = packColor(CelestweaveData.held(chest, CelestweaveModule.BACKPACK));
            }
        }
        ItemStack legs = look.worn[LEGS];
        if (legs != null) {
            Set<CelestweaveModule> installed = CelestweaveData.installed(legs);
            look.flightInstalled = installed.contains(CelestweaveModule.FLIGHT);
            look.flightActive = CelestweaveData.active(legs, CelestweaveModule.FLIGHT);
            look.wings = look.flightInstalled && !CelestweaveData.idList(legs, CelestweaveData.HIDDEN)
                .contains(CelestweaveModule.FLIGHT.id);
        }
        return look;
    }

    private static boolean shownWithItem(ItemStack stack, Set<CelestweaveModule> installed, Set<String> hidden,
        CelestweaveModule module) {
        return installed.contains(module) && !hidden.contains(module.id) && CelestweaveData.held(stack, module) != null;
    }

    private static int packColor(ItemStack held) {
        if (held == null) return 0xD8D8D8;
        if (GTNLItemList.PortableInfinityChest.hasBeenSet()
            && GTNLItemList.PortableInfinityChest.isStackEqual(held, false, true)) return PACK_RAINBOW;
        for (Object[] entry : PACK_COLORS) {
            GTNLItemList item = (GTNLItemList) entry[0];
            if (item.hasBeenSet() && item.isStackEqual(held, false, true)) return (Integer) entry[1];
        }
        return 0xD8D8D8;
    }

    public boolean wears(int type) {
        return worn[type] != null;
    }

    public boolean glowOn(int type) {
        return charges[type] > 0;
    }

    public int arcOwner() {
        for (int type : ARC_PRIORITY) if (worn[type] != null) return type;
        return -1;
    }

    public int idleWant(boolean storm) {
        int base = cosmetic.idleArcs() < 0 ? new int[] { 0, 1, 2, 4 }[Math.max(0, Math.min(3, tier))]
            : cosmetic.idleArcs();
        return base * (storm ? 2 : 1);
    }

    public static int mixWhite(int rgb, float k) {
        int r = rgb >> 16 & 255, g = rgb >> 8 & 255, b = rgb & 255;
        return Math.round(r + (255 - r) * k) << 16 | Math.round(g + (255 - g) * k) << 8 | Math.round(b + (255 - b) * k);
    }

    public static int scale(int rgb, float k) {
        int r = Math.min(255, Math.round((rgb >> 16 & 255) * k));
        int g = Math.min(255, Math.round((rgb >> 8 & 255) * k));
        int b = Math.min(255, Math.round((rgb & 255) * k));
        return r << 16 | g << 8 | b;
    }

    public int tierColor() {
        return switch (tier) {
            case 0 -> scale(theme, 0.45F);
            case 1 -> mixWhite(theme, 0.35F);
            case 2 -> theme;
            default -> mixWhite(theme, 0.85F);
        };
    }
}
