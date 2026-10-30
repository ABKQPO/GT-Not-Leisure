package com.gtnllib.registry;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.registry.GameRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class Registrar {

    public interface Sink {

        /**
         * Registers an item under its registry name.
         */
        void item(Item item, String name);

        /**
         * Registers a block and its item form under the registry name.
         */
        void block(Block block, Class<? extends ItemBlock> itemBlock, String name);

        /**
         * Registers a tile entity class under the id stored in saves.
         */
        void tile(Class<? extends TileEntity> tile, String id);
    }

    public static final Sink GAME_REGISTRY = new Sink() {

        @Override
        public void item(Item item, String name) {
            requirePreInit(name);
            GameRegistry.registerItem(item, name);
        }

        @Override
        public void block(Block block, Class<? extends ItemBlock> itemBlock, String name) {
            requirePreInit(name);
            GameRegistry.registerBlock(block, itemBlock, name);
        }

        @Override
        public void tile(Class<? extends TileEntity> tile, String id) {
            GameRegistry.registerTileEntity(tile, id);
        }
    };

    public final String langPrefix;
    public final String textureDomain;
    public final CreativeTabs tab;
    public final Sink sink;
    private final Set<String> names = new ObjectOpenHashSet<>();

    public Registrar(String langPrefix, String textureDomain, CreativeTabs tab) {
        this(langPrefix, textureDomain, tab, GAME_REGISTRY);
    }

    public Registrar(String langPrefix, String textureDomain, CreativeTabs tab, Sink sink) {
        this.langPrefix = langPrefix;
        this.textureDomain = textureDomain;
        this.tab = tab;
        this.sink = sink;
    }

    public <T extends Item> ItemEntry<T> item(String name, T item) {
        return new ItemEntry<>(name, item);
    }

    public <T extends Block> BlockEntry<T> block(String name, T block) {
        return new BlockEntry<>(name, block);
    }

    public static void requirePreInit(String name) {
        if (!Loader.instance()
            .isInState(LoaderState.PREINITIALIZATION)) {
            throw new IllegalStateException(name + " is registered outside pre-init");
        }
    }

    private void claim(String name) {
        if (!names.add(name)) throw new IllegalStateException("registry name " + name + " is registered twice");
    }

    private static void checkLang(String name, String current, String unset, String wanted) {
        // A key without the vanilla prefix comes from an overridden getter, which decides the shown name itself.
        String prefix = unset.substring(0, unset.indexOf('.') + 1);
        if (current.equals(unset) || !current.startsWith(prefix)) return;
        if (!current.equals(wanted)) {
            throw new IllegalStateException(
                name + " sets lang key " + current + " in its constructor, the registrar sets " + wanted);
        }
    }

    public class Entry<T, E extends Entry<T, E>> {

        public final String name;
        public final T value;
        protected String lang;
        protected String texture;
        protected boolean textured = true;
        protected CreativeTabs entryTab = tab;
        protected final List<Consumer<ItemStack>> binds = new ObjectArrayList<>();
        protected final List<Consumer<T>> steps = new ObjectArrayList<>();

        protected Entry(String name, T value) {
            this.name = name;
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        protected E self() {
            return (E) this;
        }

        public E lang(String key) {
            lang = key;
            return self();
        }

        public E texture(String path) {
            texture = path;
            return self();
        }

        public E noTexture() {
            textured = false;
            return self();
        }

        public E tab(CreativeTabs creativeTab) {
            entryTab = creativeTab;
            return self();
        }

        public E bind(Consumer<ItemStack> target) {
            binds.add(target);
            return self();
        }

        public E then(Consumer<T> step) {
            steps.add(step);
            return self();
        }

        protected String langKey() {
            return lang != null ? lang : langPrefix + name;
        }

        protected String texturePath() {
            return texture != null ? texture : textureDomain + ":" + name;
        }

        protected void finish(ItemStack stack) {
            for (Consumer<ItemStack> bind : binds) bind.accept(stack);
            for (Consumer<T> step : steps) step.accept(value);
        }
    }

    public class ItemEntry<T extends Item> extends Entry<T, ItemEntry<T>> {

        protected ItemEntry(String name, T item) {
            super(name, item);
        }

        public T register() {
            claim(name);
            String key = langKey();
            checkLang(name, value.getUnlocalizedName(), "item.null", "item." + key);
            value.setUnlocalizedName(key);
            if (textured) value.setTextureName(texturePath());
            if (entryTab != null) value.setCreativeTab(entryTab);
            sink.item(value, name);
            finish(new ItemStack(value, 1));
            return value;
        }
    }

    public class BlockEntry<T extends Block> extends Entry<T, BlockEntry<T>> {

        protected Class<? extends ItemBlock> itemBlock = ItemBlock.class;
        protected final List<Class<? extends TileEntity>> tiles = new ObjectArrayList<>();
        protected final List<String> tileIds = new ObjectArrayList<>();

        protected BlockEntry(String name, T block) {
            super(name, block);
        }

        public BlockEntry<T> itemBlock(Class<? extends ItemBlock> itemForm) {
            itemBlock = itemForm;
            return this;
        }

        public BlockEntry<T> tile(Class<? extends TileEntity> tile, String id) {
            tiles.add(tile);
            tileIds.add(id);
            return this;
        }

        public T register() {
            claim(name);
            String key = langKey();
            checkLang(name, value.getUnlocalizedName(), "tile.null", "tile." + key);
            value.setBlockName(key);
            if (textured) value.setBlockTextureName(texturePath());
            if (entryTab != null) value.setCreativeTab(entryTab);
            sink.block(value, itemBlock, name);
            for (int i = 0; i < tiles.size(); i++) sink.tile(tiles.get(i), tileIds.get(i));
            finish(new ItemStack(value, 1));
            return value;
        }
    }
}
