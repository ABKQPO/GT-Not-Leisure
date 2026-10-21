package com.science.gtnl.common.part.universal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.utils.enums.GuiType;

import appeng.container.PrimaryGui;

public class UniversalBusPrimaryGui extends PrimaryGui {

    private final GuiType returnGui;

    public UniversalBusPrimaryGui(GuiType returnGui, ItemStack icon, TileEntity tile, ForgeDirection side) {
        super(null, icon, tile, side);
        this.returnGui = returnGui;
    }

    @Override
    public void open(EntityPlayer player) {
        if (this.te != null && !this.te.isInvalid()) {
            CommonProxy.openGui(player, this.returnGui, this.side, this.te);
        }
    }
}
