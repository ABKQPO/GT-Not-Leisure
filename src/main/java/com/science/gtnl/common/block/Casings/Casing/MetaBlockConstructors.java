package com.science.gtnl.common.block.Casings.Casing;

import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.MetaItemStackUtils;

import gregtech.api.enums.Textures;
import gregtech.api.render.TextureFactory;

public final class MetaBlockConstructors {

    public static ItemStack initMetaBlock(String originEnglishName, byte meta, MetaBlockBase basicBlock) {
        return MetaItemStackUtils.initMetaItemStack(originEnglishName, meta, basicBlock, basicBlock.getUsedMetaSet());
    }

    public static ItemStack initMetaBlock(String originEnglishName, byte meta, MetaBlockBase basicBlock,
        String[] tooltips) {
        basicBlock.getTooltipsMap()
            .put((int) meta, tooltips);
        return MetaItemStackUtils.initMetaItemStack(originEnglishName, meta, basicBlock, basicBlock.getUsedMetaSet());
    }

    public static void setCasingTextureForMetaBlock(byte meta, MetaBlockCasingBase basicBlock) {
        Textures.BlockIcons
            .setCasingTextureForId(basicBlock.getTextureIndex(meta), TextureFactory.of(basicBlock, meta));
    }

    public static ItemStack initMetaBlockCasing(String originEnglishName, byte meta, MetaBlockCasingBase basicBlock) {
        setCasingTextureForMetaBlock(meta, basicBlock);
        return initMetaBlock(originEnglishName, meta, basicBlock);
    }

    public static ItemStack initMetaBlockCasing(String originEnglishName, byte meta, MetaBlockCasingBase basicBlock,
        String[] tooltips) {
        setCasingTextureForMetaBlock(meta, basicBlock);
        return initMetaBlock(originEnglishName, meta, basicBlock, tooltips);
    }

}
