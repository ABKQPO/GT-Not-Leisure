package com.science.gtnl.utils.enums;

import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockSponge;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.Mods;

public enum CommonElements {

    BlockBeacon(new IStructureElement<>() {

        private final BlocksToPlace cached = BlocksToPlace.create(Blocks.beacon, 0);

        @Override
        public boolean check(Object o, World world, int x, int y, int z) {
            // in fact, we only need it is a beacon
            // so it also can be chisel's beacon...?
            return world.getBlock(x, y, z) instanceof BlockBeacon
                && world.getTileEntity(x, y, z) instanceof TileEntityBeacon;
        }

        @Override
        public boolean couldBeValid(Object o, World world, int x, int y, int z, ItemStack trigger) {
            // no side effect
            return check(o, world, x, y, z);
        }

        @Override
        public boolean spawnHint(Object o, World world, int x, int y, int z, ItemStack trigger) {
            StructureLibAPI.hintParticle(world, x, y, z, Blocks.beacon, 0);
            return true;
        }

        @Override
        public boolean placeBlock(Object o, World world, int x, int y, int z, ItemStack trigger) {
            world.setBlock(x, y, z, Blocks.beacon, 0, 2);
            if (check(o, world, x, y, z)) return true;
            else return world.setBlockToAir(x, y, z);
        }

        @Override
        public @Nullable BlocksToPlace getBlocksToPlace(Object o, World world, int x, int y, int z, ItemStack trigger,
            AutoPlaceEnvironment env) {
            return cached;
        }
    }),

    BlockSponge(new IStructureElement<>() {

        private final BlocksToPlace cached = BlocksToPlace.create(Blocks.sponge, 0);

        @Override
        public boolean check(Object o, World world, int x, int y, int z) {
            boolean isSponge = world.getBlock(x, y, z) instanceof BlockSponge;

            if (!isSponge && Mods.EtFuturumRequiem.isModLoaded()) {
                isSponge = world.getBlock(x, y, z)
                    == ItemUtils.getBlockFromItemStack(ModsItemlist.EtFuturumRequiemSponge.get(1));
            }

            return isSponge;
        }

        @Override
        public boolean couldBeValid(Object o, World world, int x, int y, int z, ItemStack trigger) {
            // no side effect
            return check(o, world, x, y, z);
        }

        @Override
        public boolean spawnHint(Object o, World world, int x, int y, int z, ItemStack trigger) {
            StructureLibAPI.hintParticle(world, x, y, z, Blocks.sponge, 0);
            return true;
        }

        @Override
        public boolean placeBlock(Object o, World world, int x, int y, int z, ItemStack trigger) {
            world.setBlock(x, y, z, Blocks.sponge, 0, 2);
            if (check(o, world, x, y, z)) return true;
            else return world.setBlockToAir(x, y, z);
        }

        @Override
        public @Nullable BlocksToPlace getBlocksToPlace(Object o, World world, int x, int y, int z, ItemStack trigger,
            AutoPlaceEnvironment env) {
            return cached;
        }
    });

    private final IStructureElement<?> element;

    @SuppressWarnings("unchecked")
    public <T> IStructureElement<T> get() {
        return (IStructureElement<T>) this.element;
    }

    CommonElements(IStructureElement<?> element) {
        this.element = element;
    }

}
