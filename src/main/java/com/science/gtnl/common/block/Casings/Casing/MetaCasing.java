package com.science.gtnl.common.block.Casings.Casing;

public class MetaCasing extends MetaBlockCasingBase {

    public byte texturePageIndex;
    public byte textureIdOffsite;

    public MetaCasing(String unlocalizedName, byte textureIdOffsite) {
        this(unlocalizedName, textureIdOffsite, TEXTURE_PAGE_INDEX);
    }

    public MetaCasing(String unlocalizedName, byte textureIdOffsite, byte texturePageIndex) {
        super(unlocalizedName);
        if (textureIdOffsite > 112) throw new IllegalArgumentException(
            "Texture ID will overflow. Create a new GT Texture Page and manually solve this problem.");
        this.texturePageIndex = texturePageIndex;
        this.textureIdOffsite = textureIdOffsite;
    }

    @Override
    public int getTextureIndex(int aMeta) {
        return super.getTextureIndex(aMeta);
    }

    @Override
    public byte getTexturePageIndex() {
        return texturePageIndex;
    }

    @Override
    public byte getTextureIndexInPage(int meta) {
        return (byte) (textureIdOffsite + meta);
    }

}
