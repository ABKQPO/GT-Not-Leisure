package com.science.gtnl.common.machine.eyeofharmony;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;

import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.RecipeCheckReason;
import tectech.thing.metaTileEntity.multi.MTEEyeOfHarmony;

// Link from a TecTech Eye of Harmony to the GTNL injector that feeds it, set with a data stick and kept by position so
// it survives reloads.
public class EyeOfHarmonyLink {

    public int controllerX, controllerY, controllerZ;
    public boolean controllerSet;
    private EyeOfHarmonyInjector controller;

    public EyeOfHarmonyInjector controller() {
        if (controller == null) return null;
        // Controller disappeared
        if (controller.getBaseMetaTileEntity() == null) return null;
        return controller;
    }

    public void unlink() {
        this.controllerSet = false;
        this.controller = null;
        this.controllerX = 0;
        this.controllerY = 0;
        this.controllerZ = 0;
    }

    // Tries to re-link to the controller periodically, for example on game load.
    public void onPostTick(MTEEyeOfHarmony eye, long aTimer) {
        if (aTimer % 100 == 5 && controllerSet && controller() == null) {
            tryLink(eye, controllerX, controllerY, controllerZ);
        }
    }

    public void onRecipeDone() {
        EyeOfHarmonyInjector injector = controller();
        if (injector != null) injector.scheduleRecipeCheck(RecipeCheckReason.IMMEDIATE);
    }

    public void unregisterFrom(MTEEyeOfHarmony eye) {
        EyeOfHarmonyInjector injector = controller();
        if (injector != null) injector.unregisterLinkedUnit(eye);
    }

    public void load(NBTTagCompound aNBT) {
        if (!aNBT.hasKey("controller")) return;
        NBTTagCompound controllerNBT = aNBT.getCompoundTag("controller");
        controllerX = controllerNBT.getInteger("x");
        controllerY = controllerNBT.getInteger("y");
        controllerZ = controllerNBT.getInteger("z");
        controllerSet = true;
    }

    public void save(NBTTagCompound aNBT) {
        if (!controllerSet) return;
        NBTTagCompound controllerNBT = new NBTTagCompound();
        controllerNBT.setInteger("x", controllerX);
        controllerNBT.setInteger("y", controllerY);
        controllerNBT.setInteger("z", controllerZ);
        aNBT.setTag("controller", controllerNBT);
    }

    public boolean tryLink(MTEEyeOfHarmony eye, int x, int y, int z) {
        var tileEntity = eye.getBaseMetaTileEntity()
            .getWorld()
            .getTileEntity(x, y, z);
        if (tileEntity == null) return false;
        if (!(tileEntity instanceof IGregTechTileEntity gtTileEntity)) return false;
        var metaTileEntity = gtTileEntity.getMetaTileEntity();
        if (!(metaTileEntity instanceof EyeOfHarmonyInjector injector)) return false;

        // Unlink from the current controller first, so the unit is never linked to two of them.
        EyeOfHarmonyInjector oldController = controller();
        if (oldController != null) {
            oldController.unregisterLinkedUnit(eye);
            unlink();
        }

        controllerX = x;
        controllerY = y;
        controllerZ = z;
        controllerSet = true;
        controller = injector;
        controller.registerLinkedUnit(eye);
        return true;
    }

    // Returns whether the player held an injector link data stick, linked or not.
    public boolean tryLinkDataStick(MTEEyeOfHarmony eye, EntityPlayer aPlayer) {
        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) return false;
        if (!dataStick.hasTagCompound() || !dataStick.stackTagCompound.getString("type")
            .equals("EyeOfHarmonyInjector")) return false;

        NBTTagCompound nbt = dataStick.stackTagCompound;
        if (tryLink(eye, nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"))) {
            aPlayer.addChatMessage(new ChatComponentText("Link successful"));
        } else {
            aPlayer.addChatMessage(new ChatComponentText("Link failed: No Injector found at link location"));
        }
        return true;
    }

    public void writeWailaData(NBTTagCompound tag) {
        tag.setBoolean("linked", controller() != null);
        if (controller() != null) {
            tag.setInteger("controllerX", controllerX);
            tag.setInteger("controllerY", controllerY);
            tag.setInteger("controllerZ", controllerZ);
        }
    }

    public static void addWailaBody(NBTTagCompound tag, List<String> currenttip) {
        if (tag.getBoolean("linked")) {
            currenttip.add(
                EnumChatFormatting.AQUA + "Linked to Injector at "
                    + EnumChatFormatting.WHITE
                    + tag.getInteger("controllerX")
                    + ", "
                    + tag.getInteger("controllerY")
                    + ", "
                    + tag.getInteger("controllerZ")
                    + EnumChatFormatting.RESET);
        } else {
            currenttip.add(EnumChatFormatting.AQUA + "Unlinked");
        }
    }
}
