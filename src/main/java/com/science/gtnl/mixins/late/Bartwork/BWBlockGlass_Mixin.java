package com.science.gtnl.mixins.late.Bartwork;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.Utils.BWBlockGlassHelper;

import bartworks.common.blocks.BWBlocks;
import bartworks.common.blocks.BWBlocksGlass;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SuppressWarnings("UnusedMixin")
@Mixin(value = BWBlocksGlass.class, remap = false)
public abstract class BWBlockGlass_Mixin extends BWBlocks {

    @Shadow
    private IIcon[] connectedTexture;

    @Mutable
    @Final
    @Shadow
    private final boolean connectedTex;

    public BWBlockGlass_Mixin(String name, String[] texture, boolean connectedTex) {
        super(name, texture);
        this.connectedTex = connectedTex;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        String[] textureNames = ((BWBlocksAccessor) this).getTextureNames();
        if (!this.connectedTex) {
            this.texture = new IIcon[textureNames.length];
            for (int i = 0; i < textureNames.length; i++) {
                this.texture[i] = par1IconRegister.registerIcon(textureNames[i]);
            }
            return;
        }

        this.texture = new IIcon[textureNames.length];
        this.connectedTexture = new IIcon[16];
        for (int i = 0; i < textureNames.length; i++) {
            this.texture[i] = par1IconRegister.registerIcon(textureNames[i]);
            String[] splitname = textureNames[0].split(":");
            for (int j = 0; j < 16; j++) {
                this.connectedTexture[j] = par1IconRegister
                    .registerIcon(splitname[0] + ":connectedTex/" + splitname[1] + '/' + splitname[1] + '_' + j);
            }
        }
        BWBlockGlassHelper.setTextureNames(textureNames);
    }
}
