package com.science.gtnl.client.celestweave;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnllib.client.fx.FxColor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveLook {

    public static final int PACK_NONE = -2, PACK_RAINBOW = -1;
    private static final int[] ARC_PRIORITY = { CelestweaveArmor.CHEST, CelestweaveArmor.BOOTS, CelestweaveArmor.LEGS,
        CelestweaveArmor.HEAD };
    private static final Object[][] PACK_COLORS = { { GTNLItemList.PortableCopperChest, 0xC87533 },
        { GTNLItemList.PortableIronChest, 0xD8D8D8 }, { GTNLItemList.PortableSilverChest, 0xC8D2E1 },
        { GTNLItemList.PortableSteelChest, 0x82878F }, { GTNLItemList.PortableGoldenChest, 0xF2C94C },
        { GTNLItemList.PortableDiamondChest, 0x5EE0E0 }, { GTNLItemList.PortableCrystalChest, 0xBEF0FF },
        { GTNLItemList.PortableObsidianChest, 0x6E4CAA }, { GTNLItemList.PortableNetheriteChest, 0x786060 },
        { GTNLItemList.PortableDarkSteelChest, 0x464650 } };

    public final ItemStack[] worn = new ItemStack[CelestweaveArmor.PIECES];
    public int tier;
    public float fraction;
    public final float[] fractions = new float[CelestweaveArmor.PIECES];
    public CelestweaveCosmetic cosmetic = CelestweaveCosmetic.DEFAULT;
    public int glowColor = CelestweaveCosmetic.DEFAULT_COLOR;
    private final Set<CelestweaveModule> visible = EnumSet.noneOf(CelestweaveModule.class);
    public boolean flightInstalled, flightActive;
    public int packColor = PACK_NONE;

    private CelestweaveLook() {}

    public static boolean sameStacks(CelestweaveLook look, EntityLivingBase entity) {
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++)
            if (look.worn[piece] != CelestweaveData.piece(entity, piece)) return false;
        return true;
    }

    public static CelestweaveLook compute(EntityLivingBase entity) {
        CelestweaveLook look = new CelestweaveLook();
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            look.worn[piece] = CelestweaveData.piece(entity, piece);
            if (look.worn[piece] != null) look.fractions[piece] = (float) CelestweaveEnergy.fraction(look.worn[piece]);
        }
        ItemStack source = look.worn[CelestweaveArmor.CHEST];
        for (int piece : new int[] { CelestweaveArmor.HEAD, CelestweaveArmor.LEGS, CelestweaveArmor.BOOTS })
            if (source == null) source = look.worn[piece];
        if (source != null) {
            look.tier = CelestweaveData.energyTier(source);
            look.fraction = (float) CelestweaveEnergy.fraction(source);
        }
        if (look.worn[CelestweaveArmor.CHEST] != null) {
            look.cosmetic = CelestweaveData.cosmetic(look.worn[CelestweaveArmor.CHEST]);
            look.glowColor = look.cosmetic.getInt(CelestweaveCosmeticOption.GLOW_COLOR);
        }
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = look.worn[piece];
            if (stack == null) continue;
            for (CelestweaveModule module : CelestweaveData.installed(stack)) if (CelestweaveData.shown(stack, module)
                && (!module.holdsItem || CelestweaveData.held(stack, module) != null)) look.visible.add(module);
        }
        if (look.visible(CelestweaveModule.BACKPACK)) look.packColor = packColor(
            CelestweaveData.held(look.worn[CelestweaveArmor.CHEST], CelestweaveModule.BACKPACK));
        ItemStack legs = look.worn[CelestweaveArmor.LEGS];
        if (legs != null) {
            look.flightInstalled = CelestweaveData.installed(legs)
                .contains(CelestweaveModule.FLIGHT);
            look.flightActive = CelestweaveData.active(legs, CelestweaveModule.FLIGHT);
        }
        return look;
    }

    public boolean visible(CelestweaveModule module) {
        return visible.contains(module);
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

    public boolean wears(int piece) {
        return worn[piece] != null;
    }

    public boolean glowOn(int piece) {
        return fractions[piece] > 0;
    }

    public int arcOwner() {
        for (int piece : ARC_PRIORITY) if (worn[piece] != null) return piece;
        return -1;
    }

    public int idleWant(boolean storm) {
        int base = cosmetic.getInt(CelestweaveCosmeticOption.IDLE_ARCS) < 0
            ? new int[] { 0, 1, 2, 4 }[Math.max(0, Math.min(3, tier))]
            : cosmetic.getInt(CelestweaveCosmeticOption.IDLE_ARCS);
        return base * (storm ? 2 : 1);
    }

    public int tierColor() {
        return switch (tier) {
            case 0 -> FxColor.scale(glowColor, 0.45F);
            case 1 -> FxColor.mixWhite(glowColor, 0.35F);
            default -> glowColor;
        };
    }
}
