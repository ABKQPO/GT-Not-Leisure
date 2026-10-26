package com.science.gtnl.common.gui.container;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.gtnllib.utils.GameUtils;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.item.items.DireCraftPattern;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.container.AEBaseContainer;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.container.slot.SlotPatternOutputs;
import appeng.container.slot.SlotRestrictedInput;
import appeng.util.Platform;
import cpw.mods.fml.common.Optional;
import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import gregtech.api.enums.Mods;

public class ContainerDirePatternEncoder extends AEBaseContainer implements IOptionalSlotHost {

    public static final NBTTagCompound empty = new NBTTagCompound();

    private final TileEntityDirePatternEncoder te;

    private final SlotFakeCraftingMatrix[] craftingSlots = new SlotFakeCraftingMatrix[81];
    private final OptionalSlotFake outputSlot;
    private final SlotRestrictedInput patternSlotIN;
    private final SlotRestrictedInput patternSlotOUT;

    public ContainerDirePatternEncoder(InventoryPlayer player, TileEntityDirePatternEncoder anchor) {
        super(player, anchor);
        te = anchor;

        var recipe = te.getRecipe();
        var pattern = te.getPattern();

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(
                    this.craftingSlots[x
                        + y * 9] = new SlotFakeCraftingMatrix(recipe, x + y * 9, 12 + x * 18, 8 + y * 18) {

                            @Override
                            public void putStack(ItemStack is) {
                                if (is == null) {
                                    super.putStack(is);
                                    return;
                                }
                                var i = is.copy();
                                i.stackSize = 1;
                                super.putStack(i);
                            }
                        });
            }
        }

        this.addSlotToContainer(outputSlot = new SlotPatternOutputs(recipe, this, 81, 210, 80, 0, 0, 1));
        outputSlot.setRenderDisabled(false);

        this.addSlotToContainer(
            this.patternSlotIN = new SlotRestrictedInput(
                SlotRestrictedInput.PlacableItemType.BLANK_PATTERN,
                pattern,
                0,
                210,
                100 + 10,
                this.getInventoryPlayer()));
        this.addSlotToContainer(
            this.patternSlotOUT = new SlotRestrictedInput(
                SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN,
                pattern,
                1,
                210,
                100 + 50,
                this.getInventoryPlayer()) {

                @Override
                public boolean isItemValid(final ItemStack i) {
                    if (i != null) {
                        return i.getItem() instanceof DireCraftPattern;
                    }
                    return false;
                }
            });

        this.bindPlayerInventory(player, 31, 174);
    }

    @Override
    public void putStackInSlot(final int par1, final ItemStack par2ItemStack) {
        super.putStackInSlot(par1, par2ItemStack);
        this.getAndUpdateOutput();
    }

    @Override
    public void putStacksInSlots(final ItemStack[] par1ArrayOfItemStack) {
        super.putStacksInSlots(par1ArrayOfItemStack);
        this.getAndUpdateOutput();
    }

    @Override
    public void onSlotChange(final Slot s) {
        if (!Platform.isServer()) return;
        if (s == this.patternSlotOUT) {
            for (final ICrafting crafter : this.crafters) {

                for (final Slot g : this.inventorySlots) {
                    if (g instanceof OptionalSlotFake || g instanceof SlotFakeCraftingMatrix) {
                        crafter.sendSlotContents(this, g.slotNumber, g.getStack());
                    }
                }
                ((EntityPlayerMP) crafter).isChangingQuantityOnly = false;
            }
            this.detectAndSendChanges();
        }
    }

    private ItemStack getAndUpdateOutput() {
        final InventoryCrafting ic = new InventoryCrafting(this, 9, 9);

        for (int x = 0; x < 81; x++) {
            ic.setInventorySlotContents(
                x,
                te.getRecipe()
                    .getStackInSlot(x));
        }

        final ItemStack is = Mods.Avaritia.isModLoaded() ? getRecipeOutput(ic) : null;
        this.outputSlot.putStack(is);
        return is;
    }

    @Optional.Method(modid = "Avaritia")
    public ItemStack getRecipeOutput(InventoryCrafting ic) {
        return ExtremeCraftingManager.getInstance()
            .findMatchingRecipe(ic, te.getWorldObj());
    }

    public void encode(boolean isShift) {
        ItemStack output = this.patternSlotOUT.getStack();

        var out = this.getAndUpdateOutput();
        if (out == null) return;

        if (output == null) {
            output = this.patternSlotIN.getStack();

            if (output == null) return;

            --output.stackSize;
            if (output.stackSize == 0) {
                this.patternSlotIN.putStack(null);
            }

            output = GTNLItemList.DireCraftPattern.get(1);
            if (!isShift) this.patternSlotOUT.putStack(output);
        } else if (!this.isPattern(output)) return;

        final NBTTagCompound encodedValue = new NBTTagCompound();

        final NBTTagList tagIn = new NBTTagList();
        final NBTTagCompound tagOut = Platform.writeItemStackToNBT(out, new NBTTagCompound());

        for (final SlotFakeCraftingMatrix i : craftingSlots) {
            tagIn.appendTag(
                i.getStack() != null ? Platform.writeItemStackToNBT(i.getStack(), new NBTTagCompound()) : empty);
        }

        encodedValue.setTag("in", tagIn);
        encodedValue.setTag("out", tagOut);
        encodedValue.setBoolean("crafting", true);
        encodedValue.setString("author", this.getPlayerInv().player.getCommandSenderName());

        output.setTagCompound(encodedValue);

        if (isShift) GameUtils.placeItemBackInInventory(this.getPlayerInv().player, output);
    }

    public void clear() {
        for (var i = 0; i < te.getRecipe()
            .getSizeInventory(); ++i) {
            te.getRecipe()
                .setInventorySlotContents(i, null);
        }
    }

    public void writeNEINBT(NBTTagCompound tag) {
        clear();
        for (var s : tag.func_150296_c()) {
            te.getRecipe()
                .setInventorySlotContents(Integer.parseInt(s), ItemStack.loadItemStackFromNBT(tag.getCompoundTag(s)));
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isPattern(final ItemStack output) {
        if (output == null) {
            return false;
        }

        return output.getItem() instanceof DireCraftPattern;
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return true;
    }
}
