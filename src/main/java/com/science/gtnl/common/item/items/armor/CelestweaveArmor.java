package com.science.gtnl.common.item.items.armor;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.dries007.holoInventory.api.IHoloGlasses;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.client.celestweave.CelestweaveArmorModel;
import com.science.gtnl.common.celestweave.CelestweaveCompat;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.IEquipmentElectricItem;
import com.science.gtnl.common.equipment.IEquipmentItem;
import com.science.gtnl.common.gui.equipment.CelestweaveGui;
import com.science.gtnl.common.gui.equipment.EquipmentContainer;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.IArmorApiaristMulti;
import forestry.api.core.IArmorNaturalist;
import gregtech.api.hazards.Hazard;
import gregtech.api.hazards.IHazardProtector;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.IRevealer;
import vazkii.botania.api.mana.IManaDiscountArmor;

@Optional.InterfaceList({
    @Optional.Interface(iface = "forestry.api.apiculture.IArmorApiaristMulti", modid = "Forestry"),
    @Optional.Interface(iface = "forestry.api.core.IArmorNaturalist", modid = "Forestry"),
    @Optional.Interface(iface = "net.dries007.holoInventory.api.IHoloGlasses", modid = "holoinventory") })
public class CelestweaveArmor extends ItemArmor implements IEquipmentElectricItem, IEquipmentItem, ISpecialArmor,
    IHazardProtector, IGuiHolder<PlayerInventoryGuiData>, IGoggles, IRevealer, IVisDiscountGear, IManaDiscountArmor,
    IArmorApiaristMulti, IArmorNaturalist, IHoloGlasses {

    public static final int HEAD = 0, CHEST = 1, LEGS = 2, BOOTS = 3, PIECES = 4;
    public static final ArmorMaterial MATERIAL = EnumHelper
        .addArmorMaterial("GTNL_CELESTWEAVE", 0, new int[] { 3, 8, 6, 3 }, 0);
    public static final String[] NAMES = { "celestweave_oculus", "celestweave_core", "celestweave_conduit",
        "celestweave_stride" };
    private static final String BLANK_TEXTURE = RESOURCE_ROOT_ID + ":textures/models/armor/celestweave_blank.png";
    private static final double[] SHARE = { 0.15, 0.40, 0.30, 0.15 };
    private static final double UNBLOCKABLE_RATIO = 0.3, MAGIC_RATIO = 0.4, FIRE_OR_EXPLOSION_RATIO = 0.7,
        PHYSICAL_RATIO = 0.6;
    private static final double UNPOWERED_FACTOR = 0.25;
    private static final double SET_BASE = 0.875, SET_BONUS_PER_PIECE = 0.125;
    private static final int UNBLOCKABLE_PRIORITY = 2, SPECIAL_PRIORITY = 1, PHYSICAL_PRIORITY = 0;

    public CelestweaveArmor(int armorType, GTNLItemList entry) {
        super(MATERIAL, 0, armorType);
        setMaxStackSize(1);
        setMaxDamage(0);
        setUnlocalizedName("gtnl." + NAMES[armorType]);
        setTextureName(RESOURCE_ROOT_ID + ":" + NAMES[armorType]);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, NAMES[armorType]);
        entry.set(new ItemStack(this));
    }

    private boolean on(ItemStack stack, CelestweaveModule module) {
        return CelestweaveData.active(stack, module) && EquipmentEnergy.read(stack) > 0;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return BLANK_TEXTURE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
        CelestweaveArmorModel model = CelestweaveArmorModel.forPiece(armorType);
        model.isSneak = entity != null && entity.isSneaking();
        model.isRiding = entity != null && entity.isRiding();
        model.isChild = entity != null && entity.isChild();
        model.aimedBow = false;
        model.heldItemRight = 0;
        if (entity != null) {
            ItemStack held = entity.getHeldItem();
            model.heldItemRight = held != null ? 1 : 0;
            if (held != null && entity instanceof EntityPlayer player && player.getItemInUseCount() > 0) {
                EnumAction action = held.getItemUseAction();
                if (action == EnumAction.block) model.heldItemRight = 3;
                else if (action == EnumAction.bow) model.aimedBow = true;
            }
        }
        return model;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "item.gtnl.celestweave.tooltip.energy",
                EquipmentEnergy.format(EquipmentEnergy.read(stack)),
                EquipmentEnergy.format(CelestweaveEnergy.capacity(stack))));
        for (CelestweaveModule module : CelestweaveData.installed(stack)) {
            if (module.builtIn && CelestweaveData.held(stack, module) == null) continue;
            String line = (CelestweaveData.active(stack, module) ? "§a- " : "§7- ")
                + StatCollector.translateToLocal("item.gtnl.celestweave_module." + module.id + ".name");
            if (!CelestweaveData.active(stack, module))
                line += " " + StatCollector.translateToLocal("item.gtnl.celestweave.tooltip.disabled");
            if (!CelestweaveData.shown(stack, module))
                line += " " + StatCollector.translateToLocal("item.gtnl.celestweave.tooltip.hidden");
            tooltip.add(line);
        }
        tooltip.add(StatCollector.translateToLocal("item.gtnl.celestweave.tooltip.configure"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        items.add(new ItemStack(this));
        items.add(charged());
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - CelestweaveEnergy.fraction(stack);
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack material) {
        return false;
    }

    @Override
    public int energyTier(ItemStack stack) {
        return CelestweaveData.energyTier(stack);
    }

    @Override
    public double capacity(ItemStack stack) {
        return CelestweaveEnergy.capacity(stack);
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase entity, ItemStack stack, DamageSource source, double damage,
        int slot) {
        if (source == DamageSource.outOfWorld) return new ArmorProperties(0, 0, 0);
        int worn = 0;
        for (int piece = 0; piece < PIECES; piece++) if (CelestweaveData.piece(entity, piece) != null) worn++;
        double ratio = source.isUnblockable() ? UNBLOCKABLE_RATIO
            : source.isMagicDamage() ? MAGIC_RATIO
                : source.isFireDamage() || source.isExplosion() ? FIRE_OR_EXPLOSION_RATIO : PHYSICAL_RATIO;
        if (EquipmentEnergy.read(stack) <= 0) ratio *= UNPOWERED_FACTOR;
        int priority = source.isUnblockable() ? UNBLOCKABLE_PRIORITY
            : source.isMagicDamage() || source.isFireDamage() || source.isExplosion() ? SPECIAL_PRIORITY
                : PHYSICAL_PRIORITY;
        return new ArmorProperties(
            priority,
            ratio * SHARE[armorType] * (SET_BASE + SET_BONUS_PER_PIECE * worn),
            Integer.MAX_VALUE);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack stack, int slot) {
        return MATERIAL.getDamageReductionAmount(armorType);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}

    @Override
    public boolean protectsAgainst(ItemStack stack, Hazard hazard) {
        return false;
    }

    @Override
    public boolean protectsAgainstFully(EntityLivingBase entity, ItemStack stack, Hazard hazard) {
        ItemStack chest = entity != null ? CelestweaveData.piece(entity, CHEST) : armorType == CHEST ? stack : null;
        return chest != null && on(chest, CelestweaveModule.HAZARD);
    }

    private ItemStack revealing(ItemStack stack) {
        return armorType == HEAD && on(stack, CelestweaveModule.REVEALING)
            ? CelestweaveData.held(stack, CelestweaveModule.REVEALING)
            : null;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase entity) {
        ItemStack held = revealing(stack);
        return held != null && held.getItem() instanceof IGoggles goggles && goggles.showIngamePopups(held, entity);
    }

    @Override
    public boolean showNodes(ItemStack stack, EntityLivingBase entity) {
        ItemStack held = revealing(stack);
        return held != null && held.getItem() instanceof IRevealer revealer && revealer.showNodes(held, entity);
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        ItemStack held = revealing(stack);
        return held != null && held.getItem() instanceof IVisDiscountGear gear
            ? gear.getVisDiscount(held, player, aspect)
            : 0;
    }

    @Override
    public float getDiscount(ItemStack stack, int slot, EntityPlayer player) {
        return armorType == CHEST && on(stack, CelestweaveModule.TERRASTEEL) ? 0.4F : 0F;
    }

    @Override
    @Optional.Method(modid = "Forestry")
    public boolean protectEntity(EntityLivingBase entity, ItemStack stack, String cause, boolean doProtect) {
        return on(stack, CelestweaveModule.APIARIST);
    }

    @Override
    @Optional.Method(modid = "Forestry")
    public boolean protectPlayer(EntityPlayer player, ItemStack stack, String cause, boolean doProtect) {
        return on(stack, CelestweaveModule.APIARIST);
    }

    @Override
    @Optional.Method(modid = "Forestry")
    public int getProtectionCount(EntityLivingBase entity, ItemStack stack, String cause) {
        return on(stack, CelestweaveModule.APIARIST) ? 4 : 0;
    }

    @Override
    @Optional.Method(modid = "Forestry")
    public boolean canSeePollination(EntityPlayer player, ItemStack stack, boolean doSee) {
        return armorType == HEAD && on(stack, CelestweaveModule.APIARIST);
    }

    @Override
    @Optional.Method(modid = "holoinventory")
    public boolean shouldRender(ItemStack stack) {
        if (armorType != HEAD || !on(stack, CelestweaveModule.HOLO)) return false;
        return CelestweaveCompat.holoShouldRender(CelestweaveData.held(stack, CelestweaveModule.HOLO));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel panel) {
        return new ModularScreen(ScienceNotLeisure.MODID, panel).useTheme(EquipmentTheme.selected());
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager sync, UISettings settings) {
        CelestweaveGui gui = new CelestweaveGui(data, sync);
        settings.customContainer(() -> new EquipmentContainer(data.getSlotIndex(), gui, CelestweaveEnergy::clamp));
        settings.canInteractWith(player -> {
            ItemStack used = data.getUsedItemStack();
            return !player.isDead && used != null && used.getItem() == this;
        });
        return gui.build();
    }
}
