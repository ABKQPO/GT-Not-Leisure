package com.science.gtnl.common.metoolbox;

import java.util.function.Predicate;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.glodblock.github.loader.recipe.WirelessTerminalEnergyRecipe;
import com.glodblock.github.loader.recipe.WirelessTerminalQuantumBridgeRecipe;
import com.glodblock.github.loader.recipe.WirelessTerminalRecipe;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ItemMEToolbox;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.config.Settings;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.util.AEColor;
import appeng.container.ContainerNull;
import cpw.mods.fml.common.registry.GameRegistry;

public class ToolboxUpgradeRecipe extends ShapedOreRecipe {

    private static final String[] TERMINAL_TAGS = { "encryptionKey", "encryptionKeys", "name", "pins", "viewCell",
        "terminalSettings", "searchString", "display" };

    private ToolboxUpgradeRecipe() {
        super(
            GTNLItemList.METoolbox.get(1),
            "PEP",
            "CWC",
            "PEP",
            'P',
            "plateAluminium",
            'E',
            AEApi.instance()
                .definitions()
                .materials()
                .engProcessor()
                .maybeStack(1)
                .get(),
            'C',
            AEApi.instance()
                .definitions()
                .parts()
                .cableGlass()
                .stack(AEColor.Transparent, 1),
            'W',
            AEApi.instance()
                .definitions()
                .items()
                .wirelessTerminal()
                .maybeStack(1)
                .get());
    }

    public static void register() {
        RecipeSorter.register(
            ScienceNotLeisure.MODID + ":me_toolbox_upgrade",
            ToolboxUpgradeRecipe.class,
            RecipeSorter.Category.SHAPED,
            "after:forge:shapedore");
        GameRegistry.addRecipe(new ToolboxUpgradeRecipe());

        RecipeSorter.register(
            ScienceNotLeisure.MODID + ":me_toolbox_card",
            CardRecipe.class,
            RecipeSorter.Category.SHAPELESS,
            "after:forge:shapelessore");
        ItemStack toolbox = GTNLItemList.METoolbox.get(1);
        ItemMEToolbox item = (ItemMEToolbox) toolbox.getItem();
        GameRegistry.addRecipe(new CardRecipe(new WirelessTerminalEnergyRecipe(toolbox), item::hasInfinityPower));
        GameRegistry
            .addRecipe(new CardRecipe(new WirelessTerminalQuantumBridgeRecipe(toolbox), item::hasInfinityRange));
        if (WirelessTerminalRecipe.getInfinityBoosterCard() != null) {
            GameRegistry.addRecipe(new CardRecipe(new WirelessTerminalRecipe(toolbox), item::hasInfinityRange));
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack source = inventory.getStackInSlot(slot);
            if (AEApi.instance()
                .definitions()
                .items()
                .wirelessTerminal()
                .isSameAs(source)) {
                return upgrade(source, super.getCraftingResult(inventory));
            }
        }
        return null;
    }

    public static ItemStack upgrade(ItemStack source, ItemStack result) {
        if (!(result.getItem() instanceof ItemMEToolbox toolbox)) {
            throw new IllegalArgumentException("The upgrade output must be an ME Toolbox");
        }
        result.setTagCompound(copyTerminalData(source.getTagCompound()));
        if (source.getItem() instanceof IAEItemPowerStorage power) {
            double stored = power.getAECurrentPower(source);
            if (Double.isFinite(stored) && stored > 0) {
                toolbox.injectAEPower(result, stored);
            }
        }

        return result;
    }

    static NBTTagCompound copyTerminalData(NBTTagCompound original) {
        NBTTagCompound tag = new NBTTagCompound();
        if (original != null) {
            for (String key : TERMINAL_TAGS) {
                if (original.hasKey(key)) tag.setTag(
                    key,
                    original.getTag(key)
                        .copy());
            }

            for (Settings setting : new Settings[] { Settings.SORT_BY, Settings.VIEW_MODE, Settings.SORT_DIRECTION }) {
                String key = setting.name();
                if (original.hasKey(key)) tag.setTag(
                    key,
                    original.getTag(key)
                        .copy());
            }
        }
        return tag;
    }

    private static final class CardRecipe extends ShapelessOreRecipe {

        private final ShapelessRecipes nativeRecipe;
        private final Predicate<ItemStack> installed;

        private CardRecipe(ShapelessRecipes nativeRecipe, Predicate<ItemStack> installed) {
            super(nativeRecipe.getRecipeOutput(), nativeRecipe.recipeItems.toArray());
            this.nativeRecipe = nativeRecipe;
            this.installed = installed;
        }

        @Override
        public boolean matches(InventoryCrafting inventory, World world) {
            if (!super.matches(inventory, world)) return false;
            ItemStack toolbox = findToolbox(inventory);
            return toolbox != null && !installed.test(toolbox);
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inventory) {
            if (!matches(inventory, null)) return null;

            InventoryCrafting nativeInput = new InventoryCrafting(new ContainerNull(), 2, 1);
            nativeInput.setInventorySlotContents(0, findToolbox(inventory));
            return nativeRecipe.getCraftingResult(nativeInput);
        }

        private static ItemStack findToolbox(InventoryCrafting inventory) {
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (ItemMEToolbox.isToolbox(stack)) return stack;
            }
            return null;
        }
    }
}
