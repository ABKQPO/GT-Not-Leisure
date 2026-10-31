package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.item.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartUniversalExportBus;
import com.science.gtnl.common.part.PartUniversalImportBus;
import com.science.gtnl.common.part.PartUniversalStorageBus;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartUniversalBus extends Item implements IPartItem {

    public enum BusType {

        STORAGE("part_universal_storage_bus", PartUniversalStorageBus::new, GTNLItemList.PartUniversalStorageBus),
        IMPORT("part_universal_import_bus", PartUniversalImportBus::new, GTNLItemList.PartUniversalImportBus),
        EXPORT("part_universal_export_bus", PartUniversalExportBus::new, GTNLItemList.PartUniversalExportBus);

        private final String name;
        private final Function<ItemStack, IPart> factory;
        private final GTNLItemList itemList;

        BusType(String name, Function<ItemStack, IPart> factory, GTNLItemList itemList) {
            this.name = name;
            this.factory = factory;
            this.itemList = itemList;
        }
    }

    private final BusType busType;

    public ItemPartUniversalBus(BusType busType) {
        this.busType = busType;
        this.setMaxStackSize(64);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        this.setUnlocalizedName("gtnl." + busType.name);
        this.setTextureName(RESOURCE_ROOT_ID + ":part/" + busType.name.substring("part_".length()));
        GameRegistry.registerItem(this, busType.name);
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
        busType.itemList.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl." + this.busType.name;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack is) {
        return this.busType.factory.apply(is);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float xOffset, float yOffset, float zOffset) {
        return AEApi.instance()
            .partHelper()
            .placeBus(player.getHeldItem(), x, y, z, side, player, world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }
}
