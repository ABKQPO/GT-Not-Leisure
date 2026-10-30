package com.gtnllib;

import static com.gtnltest.ContractAssertions.check;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.gtnllib.registry.Registrar;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RegistryContracts {

    public static class RecordingSink implements Registrar.Sink {

        public final List<String> names = new ObjectArrayList<>();

        @Override
        public void item(Item item, String name) {
            names.add(name);
        }

        @Override
        public void block(Block block, Class<? extends ItemBlock> itemBlock, String name) {
            names.add(name);
        }

        @Override
        public void tile(Class<? extends TileEntity> tile, String id) {
            names.add(id);
        }
    }

    static void run() {
        RecordingSink sink = new RecordingSink();
        Registrar registrar = new Registrar("test.", "testdomain", null, sink);
        List<ItemStack> bound = new ObjectArrayList<>();
        Item plain = registrar.item("plain_item", new Item())
            .bind(bound::add)
            .register();
        check(
            "item.test.plain_item".equals(plain.getUnlocalizedName()),
            "the lang key defaults to the prefix plus the registry name, got " + plain.getUnlocalizedName());
        check(sink.names.equals(List.of("plain_item")), "the registry name reaches the registry once: " + sink.names);
        check(
            bound.size() == 1 && bound.get(0)
                .getItem() == plain && bound.get(0).stackSize == 1,
            "bind receives a one-item stack of the registered item");

        Item custom = registrar.item("custom_item", new Item())
            .lang("test.other_key")
            .noTexture()
            .register();
        check("item.test.other_key".equals(custom.getUnlocalizedName()), "lang(...) replaces the default key");

        check(
            fails(
                () -> registrar.item("plain_item", new Item())
                    .register()),
            "registering the same name twice in one table fails at startup");

        Item preset = new Item().setUnlocalizedName("test.preset_item");
        check(
            !fails(
                () -> registrar.item("preset_item", preset)
                    .register()),
            "a constructor key that matches the default is accepted");
        Item conflicting = new Item().setUnlocalizedName("test.something_else");
        check(
            fails(
                () -> registrar.item("conflicting_item", conflicting)
                    .register()),
            "a constructor key that differs from the registrar's key fails at startup instead of losing the name");
        Item ownKey = new Item() {

            @Override
            public String getUnlocalizedName() {
                return "test.block.own_key";
            }
        };
        check(
            !fails(
                () -> registrar.item("own_key_item", ownKey)
                    .register()),
            "a class that builds its own key in an overridden getter is left to it");
    }

    private static boolean fails(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }
}
