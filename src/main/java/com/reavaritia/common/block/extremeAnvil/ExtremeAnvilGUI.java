package com.reavaritia.common.block.extremeAnvil;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;
import static com.reavaritia.ReAvaritia.network;

import java.util.List;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Charsets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtremeAnvilGUI extends GuiContainer implements ICrafting {

    private static final ResourceLocation ANVIL_TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "textures/gui/extreme_anvil.png");

    private GuiTextField itemNameTextField;
    private final ContainerExtremeAnvil containerAnvil;
    private boolean isTextFieldManuallyEdited = false;
    private ItemStack lastInputStack = null;
    private ItemStack lastMaterialStack = null;

    private int clientCanTake = 0;

    public ExtremeAnvilGUI(InventoryPlayer playerInv, World world, int x, int y, int z, IInventory inputSlots,
        IInventory outputSlot, EntityPlayer player) {
        super(new ContainerExtremeAnvil(playerInv, world, x, y, z, inputSlots, outputSlot, player));
        this.containerAnvil = (ContainerExtremeAnvil) this.inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.itemNameTextField = new GuiTextField(this.fontRendererObj, i + 62, j + 27, 103, 12);
        this.itemNameTextField.setTextColor(-1);
        this.itemNameTextField.setDisabledTextColour(-1);
        this.itemNameTextField.setEnableBackgroundDrawing(false);
        this.itemNameTextField.setMaxStringLength(256);
        this.itemNameTextField.setFocused(true);
        this.inventorySlots.removeCraftingFromCrafters(this);
        this.inventorySlots.addCraftingToCrafters(this);

        ItemStack inputStack = containerAnvil.inputSlots.getStackInSlot(0);
        ItemStack materialStack = containerAnvil.inputSlots.getStackInSlot(1);
        this.lastInputStack = inputStack != null ? inputStack.copy() : null;
        this.lastMaterialStack = materialStack != null ? materialStack.copy() : null;

        if (inputStack != null) {
            this.itemNameTextField.setText(inputStack.getDisplayName());
            this.containerAnvil.updateItemName(
                this.itemNameTextField.getText()
                    .trim());
        } else {
            this.itemNameTextField.setText("");
            this.containerAnvil.updateItemName("");
        }
        this.isTextFieldManuallyEdited = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        ItemStack currentInputStack = containerAnvil.inputSlots.getStackInSlot(0);
        ItemStack currentMaterialStack = containerAnvil.inputSlots.getStackInSlot(1);

        if (!ItemStack.areItemStacksEqual(currentInputStack, this.lastInputStack)
            || !ItemStack.areItemStacksEqual(currentMaterialStack, this.lastMaterialStack)) {

            this.lastInputStack = currentInputStack != null ? currentInputStack.copy() : null;
            this.lastMaterialStack = currentMaterialStack != null ? currentMaterialStack.copy() : null;

            this.isTextFieldManuallyEdited = false;

            if (currentInputStack != null) {
                String displayName = currentInputStack.getDisplayName();
                this.itemNameTextField.setText(displayName);
                this.updateItemName();
            } else {
                this.itemNameTextField.setText("");
                this.updateItemName();
            }
        } else {
            if (!isTextFieldManuallyEdited) {
                String repairedName = containerAnvil.getItemName();
                if (!repairedName.equals(this.itemNameTextField.getText())) {
                    this.itemNameTextField.setText(repairedName);
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.itemNameTextField.textboxKeyTyped(typedChar, keyCode)) {
            this.updateItemName();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    private void updateItemName() {
        String itemName = this.itemNameTextField.getText();

        Slot firstSlot = this.containerAnvil.getSlot(0);

        if ((firstSlot == null || !firstSlot.getHasStack())) {
            itemName = "";
        }

        this.containerAnvil.updateItemName(itemName.trim());

        byte[] nameBytes = itemName.getBytes(Charsets.UTF_8);

        network.sendToServer(new ExtremeAnvilPacket("ExtremeAnvil|ItemName", nameBytes));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.itemNameTextField.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isPointInRegion(mouseX, mouseY)) {
            this.itemNameTextField.setFocused(false);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.itemNameTextField.setText("");
        this.containerAnvil.removeCraftingFromCrafters(this);
    }

    private boolean isPointInRegion(int mouseX, int mouseY) {
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        mouseX -= guiLeft;
        mouseY -= guiTop;
        return mouseX >= 62 - 1 && mouseX < 62 + 103 + 1 && mouseY >= 24 - 1 && mouseY < 24 + 12 + 1;
    }

    private String getLocalizedName() {
        return StatCollector.translateToLocal("container.ExtremeAnvil");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.itemNameTextField.drawTextBox();
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = this.getLocalizedName();
        this.fontRendererObj
            .drawString(title, (this.xSize - this.fontRendererObj.getStringWidth(title)) / 2, 6, 0x404040);
        this.fontRendererObj
            .drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 3, 0x404040);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(ANVIL_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        int textFieldState = this.itemNameTextField.isFocused() ? 0 : 1;
        this.drawTexturedModalRect(x + 59, y + 23, 0, this.ySize + textFieldState * 16, 110, 16);

        boolean showError = (this.clientCanTake == 0);

        if (showError) {
            this.drawTexturedModalRect(x + 99, y + 47, this.xSize, 0, 28, 21);
        }
    }

    @Override
    public void sendContainerAndContentsToPlayer(Container container, List<ItemStack> itemStacks) {
        this.sendSlotContents(
            container,
            0,
            container.getSlot(0)
                .getStack());
    }

    @Override
    public void sendSlotContents(Container container, int slotIndex, ItemStack itemStack) {
        if (slotIndex == 0) {
            this.itemNameTextField.setText(itemStack == null ? "" : itemStack.getDisplayName());
            this.itemNameTextField.setEnabled(itemStack != null);

            if (itemStack != null) {
                this.updateItemName();
            }
        }
    }

    @Override
    public void sendProgressBarUpdate(Container container, int id, int data) {
        if (id == 0) {
            this.clientCanTake = data;
        }
    }
}
