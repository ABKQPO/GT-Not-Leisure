package com.science.gtnl.client;

import java.util.Map;
import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.glodblock.github.common.item.ItemFluidDrop;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.Stick;
import com.science.gtnl.common.packet.KeyBindingHandler;
import com.science.gtnl.utils.ClientUtils;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import codechicken.nei.BookmarkPanel;
import codechicken.nei.ItemPanel;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.Widget;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.common.items.ItemFluidDisplay;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@SideOnly(Side.CLIENT)
public class GTNLInputHandler implements IContainerInputHandler {

    public static final GTNLInputHandler INSTANCE = new GTNLInputHandler();
    public static final String AE_RETRIEVE_ITEM_KEY = "gui.ae_retrieve_item";
    public static final String AE_START_CRAFT_KEY = "gui.ae_start_craft";
    public static final int PICK_BLOCK_COOLDOWN_TICKS = 10;
    public static final int ANIMATION_FRAME_COUNT = 14;
    public static final Map<String, BooleanSupplier> KEY_BINDINGS = createKeyBindings();
    public static final Minecraft MC = Minecraft.getMinecraft();
    public static int INPUT_COOLDOWN_TICKS = 0;
    public static int ANIMATION_TICK = 0;
    public static int FRAME_COUNTER = 0;
    public static GuiScreen LAST_GUI_SCREEN = null;
    public static Runnable DELAY_METHOD = null;

    public GTNLInputHandler() {
        GuiContainerManager.addInputHandler(this);
    }

    public static Map<String, BooleanSupplier> createKeyBindings() {
        Map<String, BooleanSupplier> keyBindings = new Object2ObjectOpenHashMap<>(2);
        keyBindings.put(AE_RETRIEVE_ITEM_KEY, () -> GuiScreen.isCtrlKeyDown() && Mouse.isButtonDown(2));
        keyBindings.put(AE_START_CRAFT_KEY, () -> Interactable.hasAltDown() && Mouse.isButtonDown(2));
        return keyBindings;
    }

    public static boolean tryHandlePickBlockInput() {
        EntityClientPlayerMP player = MC.thePlayer;
        if (player == null || player.capabilities.isCreativeMode
            || INPUT_COOLDOWN_TICKS != 0
            || !MC.gameSettings.keyBindPickBlock.isPressed()) {
            return false;
        }
        World world = player.worldObj;
        if (world == null) {
            return false;
        }
        ClientUtils.onBeforePickBlock(player, world, true);
        INPUT_COOLDOWN_TICKS = PICK_BLOCK_COOLDOWN_TICKS;
        return true;
    }

    @Override
    public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode) {
        ItemStack stack = GuiContainerManager.getStackMouseOver(gui);
        if (stack == null) return false;
        Item item = stack.getItem();

        if (item instanceof ICraftingPatternItem pattern) {
            ICraftingPatternDetails details = pattern.getPatternForItem(stack, MC.theWorld);
            if (details == null) return false;

            stack = details.getCondensedOutputs()[0].getItemStack();
        }

        if (item instanceof Stick) {
            ItemStack fake = Stick.getDisguisedStack(stack);
            if (fake == null) return false;

            stack = fake;
        }

        ItemStack dataStickOutput = AssemblyLineUtils.getDataStickOutput(stack);
        if (dataStickOutput != null) stack = dataStickOutput;

        if (NEIClientConfig.isKeyHashDown("gui.recipe")) {
            return GuiCraftingRecipe.openRecipeGui("item", stack);
        }

        if (NEIClientConfig.isKeyHashDown("gui.usage")) {
            return GuiUsageRecipe.openRecipeGui("item", stack);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        ItemStack stack = GuiContainerManager.getStackMouseOver(gui);
        if (stack == null) return false;
        if (stack.getItem() instanceof ItemFluidDisplay) {
            FluidStack fluidStack = ItemUtils.getFluidFromItemFluidDisplay(stack);
            if (fluidStack == null) return false;
            if (fluidStack.amount == 0) fluidStack.amount = 1;
            stack = ItemFluidDrop.newStack(fluidStack);
        }
        return startAEWork(stack, mousex, mousey);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (INPUT_COOLDOWN_TICKS > 0) {
            INPUT_COOLDOWN_TICKS--;
        }
        ANIMATION_TICK = (ANIMATION_TICK + ((++FRAME_COUNTER & 1) == 0 ? 1 : 0)) % ANIMATION_FRAME_COUNT;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.KeyInputEvent event) {
        tryHandlePickBlockInput();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.MouseInputEvent event) {
        tryHandlePickBlockInput();
    }

    public boolean startAEWork(ItemStack item, int mouseX, int mouseY) {
        for (Map.Entry<String, BooleanSupplier> keyBinding : KEY_BINDINGS.entrySet()) {
            if (!keyBinding.getValue()
                .getAsBoolean()) continue;
            final Widget focused = LayoutManager.instance()
                .getWidgetUnderMouse(mouseX, mouseY);

            if (!(focused instanceof BookmarkPanel || focused instanceof ItemPanel)) return false;
            final GuiScreen currentGui = MC.currentScreen;
            ScienceNotLeisure.network
                .sendToServer(new KeyBindingHandler(keyBinding.getKey(), item, currentGui instanceof GuiMEMonitorable));
            if (AE_START_CRAFT_KEY.equals(keyBinding.getKey())) {
                var player = MC.thePlayer;
                if (player.openContainer instanceof ContainerCraftAmount
                    || player.openContainer instanceof ContainerCraftConfirm) return false;
                LAST_GUI_SCREEN = currentGui;
            }
            return true;
        }

        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {}

    @Override
    public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {}

    @Override
    public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {}

    @Override
    public boolean keyTyped(GuiContainer gui, char keyChar, int keyID) {
        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {}

    @Override
    public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {}

}
