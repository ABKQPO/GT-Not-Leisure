package com.science.gtnl.common.machine.hatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.drawable.AdaptableUITexture;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;

import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class NanitesInputBus extends MTEHatchInputBus {

    public NanitesInputBus(int id, String name, String nameRegional) {
        super(
            id,
            name,
            nameRegional,
            8,
            4,
            new String[] { StatCollector.translateToLocal("Tooltip_NanitesInputBus_00"),
                StatCollector.translateToLocal("Tooltip_NanitesInputBus_01") });
    }

    public NanitesInputBus(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NanitesInputBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        disableLimited = false;
        disableSort = true;
        disableFilter = false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        if (aIndex == getCircuitSlot()) return false;
        return side == getBaseMetaTileEntity().getFrontFacing() && !limitedAllowPutStack(aIndex, aStack);
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == getBaseMetaTileEntity().getFrontFacing() && aIndex != getCircuitSlot()
            && limitedAllowPutStack(aIndex, aStack);
    }

    @Override
    public boolean limitedAllowPutStack(int aIndex, ItemStack aStack) {
        boolean isNanite = false;
        int[] oreIds = OreDictionary.getOreIDs(aStack);
        for (int id : oreIds) {
            String name = OreDictionary.getOreName(id);
            if (OrePrefixes.isInstanceOf(name, OrePrefixes.nanite)) {
                isNanite = true;
                break;
            }
        }
        if (!isNanite) return false;
        for (int i = 0; i < getSizeInventory(); i++)
            if (GTUtility.areStacksEqual(GTOreDictUnificator.get_nocopy(aStack), mInventory[i])) return i == aIndex;
        return mInventory[aIndex] == null;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        IItemHandlerModifiable inventoryHandler = getInventoryHandler();
        if (inventoryHandler == null) return;
        builder
            .widget(
                new SlotGroup.BuilderWithPattern()
                    .where(
                        's',
                        index -> new SlotWidget(inventoryHandler, index)
                            .setFilter(stack -> limitedAllowPutStack(index, stack))
                            .setBackground(UITexture.fullImage("gregtech", "gui/slot/item_nanochip")))
                    .row("ssss")
                    .setCellSize(new Size(38, 18))
                    .build()
                    .setPos(21, 35))
            .setBackground(AdaptableUITexture.of("gregtech", "gui/background/nanochip_default", 198, 203, 0));
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {}
}
