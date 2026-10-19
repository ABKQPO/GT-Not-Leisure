package com.science.gtnl.common.item.items;

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
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.client.celestweave.CelestweaveArmorModel;
import com.science.gtnl.common.celestweave.CelestweaveCompat;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;
import com.science.gtnl.common.gui.CelestweaveContainer;
import com.science.gtnl.common.gui.CelestweaveGui;
import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.IArmorApiaristMulti;
import forestry.api.core.IArmorNaturalist;
import gregtech.api.hazards.Hazard;
import gregtech.api.hazards.IHazardProtector;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.modularui2.GTModularScreen;
import ic2.api.item.IElectricItem;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.IRevealer;
import vazkii.botania.api.mana.IManaDiscountArmor;

@Optional.InterfaceList({
    @Optional.Interface(iface = "forestry.api.apiculture.IArmorApiaristMulti", modid = "Forestry"),
    @Optional.Interface(iface = "forestry.api.core.IArmorNaturalist", modid = "Forestry"),
    @Optional.Interface(iface = "net.dries007.holoInventory.api.IHoloGlasses", modid = "holoinventory") })
public class CelestweaveArmor extends ItemArmor
    implements IElectricItem, ISpecialArmor, IHazardProtector, IGuiHolder<PlayerInventoryGuiData>, IGoggles, IRevealer,
    IVisDiscountGear, IManaDiscountArmor, IArmorApiaristMulti, IArmorNaturalist, IHoloGlasses {

    public static final ArmorMaterial MATERIAL = EnumHelper
        .addArmorMaterial("GTNL_CELESTWEAVE", 0, new int[] { 3, 8, 6, 3 }, 0);
    public static final String[] NAMES = { "celestweave_oculus", "celestweave_core", "celestweave_conduit",
        "celestweave_stride" };
    private static final String BLANK_TEXTURE = RESOURCE_ROOT_ID + ":textures/models/armor/celestweave_blank.png";
    private static final double[] SHARE = { 0.15, 0.40, 0.30, 0.15 };
    private static final int[] DISPLAY = { 3, 8, 6, 3 };

    public CelestweaveArmor(int armorType, GTNLItemList entry) {
        super(MATERIAL, 0, armorType);
        setMaxStackSize(1);
        setMaxDamage(0);
        setUnlocalizedName("gtnl." + NAMES[armorType]);
        setTextureName(RESOURCE_ROOT_ID + ":" + NAMES[armorType]);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, NAMES[armorType]);
        entry.set(new ItemStack(this));
        CelestweaveServerEvents.register();
    }

    public ItemStack preset() {
        ItemStack stack = new ItemStack(this);
        CelestweaveData.writeModuleStacks(
            stack,
            CelestweaveData.stacksOf(CelestweaveData.presetModules(armorType, ItemLoader.celestweaveModule)));
        CelestweaveEnergy.fill(stack);
        return stack;
    }

    private boolean on(ItemStack stack, CelestweaveModule module) {
        return CelestweaveData.active(stack, module) && CelestweaveEnergy.read(stack) > 0;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return BLANK_TEXTURE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
        CelestweaveArmorModel model = CelestweaveArmorModel.forType(armorType);
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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isSneaking()) return super.onItemRightClick(stack, world, player);
        if (!world.isRemote) GuiFactories.playerInventory()
            .openFromMainHand(player);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "item.gtnl.celestweave.tooltip.energy",
                String.format("%,.0f", CelestweaveEnergy.read(stack)),
                String.format("%,.0f", CelestweaveEnergy.capacity(stack))));
        for (CelestweaveModule module : CelestweaveData.installed(stack)) {
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
        items.add(preset());
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
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack stack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack stack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return CelestweaveEnergy.capacity(stack);
    }

    @Override
    public int getTier(ItemStack stack) {
        return CelestweaveEnergy.voltageTier(stack);
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return CelestweaveEnergy.transferLimit(stack);
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase entity, ItemStack stack, DamageSource source, double damage,
        int slot) {
        if (source == DamageSource.outOfWorld) return new ArmorProperties(0, 0, 0);
        int worn = 0;
        for (int type = 0; type < 4; type++) if (CelestweaveServerEvents.piece(entity, type) != null) worn++;
        double ratio = source.isUnblockable() ? 0.3
            : source.isMagicDamage() ? 0.4 : source.isFireDamage() || source.isExplosion() ? 0.7 : 0.6;
        if (CelestweaveEnergy.read(stack) <= 0) ratio *= 0.25;
        int priority = source.isUnblockable() ? 2
            : source.isMagicDamage() || source.isFireDamage() || source.isExplosion() ? 1 : 0;
        return new ArmorProperties(priority, ratio * SHARE[armorType] * (0.875 + 0.125 * worn), Integer.MAX_VALUE);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack stack, int slot) {
        return DISPLAY[armorType];
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}

    @Override
    public boolean protectsAgainst(ItemStack stack, Hazard hazard) {
        return false;
    }

    @Override
    public boolean protectsAgainstFully(EntityLivingBase entity, ItemStack stack, Hazard hazard) {
        ItemStack chest = entity != null ? CelestweaveServerEvents.piece(entity, 1) : armorType == 1 ? stack : null;
        return chest != null && on(chest, CelestweaveModule.HAZARD);
    }

    private ItemStack revealing(ItemStack stack) {
        return armorType == 0 && on(stack, CelestweaveModule.REVEALING)
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
        return armorType == 1 && on(stack, CelestweaveModule.TERRASTEEL) ? 0.4F : 0F;
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
        return armorType == 0 && on(stack, CelestweaveModule.APIARIST);
    }

    @Override
    @Optional.Method(modid = "holoinventory")
    public boolean shouldRender(ItemStack stack) {
        if (armorType != 0 || !on(stack, CelestweaveModule.HOLO)) return false;
        return CelestweaveCompat.holoShouldRender(CelestweaveData.held(stack, CelestweaveModule.HOLO));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel panel) {
        return new GTModularScreen(panel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager sync, UISettings settings) {
        CelestweaveGui gui = new CelestweaveGui(data, sync);
        settings.customContainer(() -> new CelestweaveContainer(data.getSlotIndex(), gui));
        settings.canInteractWith(player -> {
            ItemStack used = data.getUsedItemStack();
            return !player.isDead && used != null && used.getItem() == this;
        });
        return gui.build();
    }
}
