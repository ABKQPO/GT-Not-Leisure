package com.science.gtnl.common.block.blocks.tile;

import static tectech.thing.metaTileEntity.multi.godforge.color.ForgeOfGodsStarColor.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.api.math.Color;

import lombok.Getter;
import tectech.thing.metaTileEntity.multi.godforge.color.ForgeOfGodsStarColor;
import tectech.thing.metaTileEntity.multi.godforge.color.StarColorSetting;

public class TileEntityNanoPhagocytosisPlant extends TileEntity {

    public float radius = 1;
    public float rotationSpeed = 10;
    @Getter
    public float rotAngle = 0, rotAxisX = 1, rotAxisY = 0, rotAxisZ = 0;
    public AxisAlignedBB renderBoundingBox;

    public ForgeOfGodsStarColor starColor = ForgeOfGodsStarColor.DEFAULT;

    // current color data
    public int currentColor = Color.rgb(DEFAULT_RED, DEFAULT_GREEN, DEFAULT_BLUE);
    @Getter
    public float gamma = DEFAULT_GAMMA;

    // interpolation color data
    public int cycleStep;
    public int interpA;
    public int interpB;
    public float interpGammaA;
    public float interpGammaB;

    public static final String NBT_TAG = "NPPRender:";
    public static final String ROTATION_SPEED_NBT_TAG = NBT_TAG + "ROTATION";
    public static final String SIZE_NBT_TAG = NBT_TAG + "RADIUS";
    public static final String ROT_ANGLE_NBT_TAG = NBT_TAG + "ROT_ANGLE";
    public static final String ROT_AXIS_X_NBT_TAG = NBT_TAG + "ROT_AXIS_X";
    public static final String ROT_AXIS_Y_NBT_TAG = NBT_TAG + "ROT_AXIS_Y";
    public static final String ROT_AXIS_Z_NBT_TAG = NBT_TAG + "ROT_AXIS_Z";

    public static final double RING_RADIUS = 63;
    public static final double BEAM_LENGTH = 59;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (renderBoundingBox == null) {
            double x = this.xCoord;
            double y = this.yCoord;
            double z = this.zCoord;

            // This could possibly be made smaller by figuring out the beam direction,
            // but since this is not always known (set dynamically by the MTE), this
            // currently just bounds as if the beam is in all 4 directions.
            renderBoundingBox = AxisAlignedBB.getBoundingBox(
                x - RING_RADIUS - BEAM_LENGTH,
                y - RING_RADIUS - BEAM_LENGTH,
                z - RING_RADIUS - BEAM_LENGTH,
                x + RING_RADIUS + BEAM_LENGTH + 1,
                y + RING_RADIUS + BEAM_LENGTH + 1,
                z + RING_RADIUS + BEAM_LENGTH + 1);
        }
        return renderBoundingBox;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return Double.MAX_VALUE;
    }

    public float getColorR() {
        return Color.getRedF(currentColor);
    }

    public float getColorG() {
        return Color.getGreenF(currentColor);
    }

    public float getColorB() {
        return Color.getBlueF(currentColor);
    }

    public void setRenderRotation(ForgeDirection direction) {
        switch (direction) {
            case SOUTH -> rotAngle = 90;
            case NORTH -> rotAngle = 90;
            case WEST -> rotAngle = 0;
            case EAST -> rotAngle = 180;
            case UP -> rotAngle = -90;
            case DOWN -> rotAngle = -90;
        }
        rotAxisX = 0;
        rotAxisY = direction.offsetZ + direction.offsetX;
        rotAxisZ = direction.offsetY;

        updateToClient();
    }

    public static float interpolate(float x0, float x1, float y0, float y1, float x) {
        return y0 + ((x - x0) * (y1 - y0)) / (x1 - x0);
    }

    public void incrementColors() {
        if (starColor.numColors() > 1) {
            cycleStep += starColor.getCycleSpeed();

            if (cycleStep < 255) {
                // interpolate like normal between these two colors
                interpolateColors();
            } else if (cycleStep == 255) {
                // interpolate like normal, but then update interp values to the next set and reset cycleStep
                cycleStarColors();
                currentColor = interpA;
                gamma = interpGammaA;
                cycleStep = 0;
            } else {
                // update interp values to the next set, reset cycleStep then interpolate
                cycleStep -= 255;
                cycleStarColors();
                interpolateColors();
            }
        }
    }

    public void interpolateColors() {
        float position = cycleStep / 255.0f;
        currentColor = Color.interpolate(interpA, interpB, position);
        gamma = interpGammaA + (interpGammaB - interpGammaA) * position;
    }

    public void cycleStarColors() {
        interpA = interpB;
        interpGammaA = interpGammaB;

        StarColorSetting nextColor = starColor.getColor(0);

        interpB = Color.rgb(nextColor.getColorR(), nextColor.getColorG(), nextColor.getColorB());
        interpGammaB = nextColor.getGamma();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat(ROTATION_SPEED_NBT_TAG, rotationSpeed);
        compound.setFloat(SIZE_NBT_TAG, radius);
        compound.setFloat(ROT_ANGLE_NBT_TAG, rotAngle);
        compound.setFloat(ROT_AXIS_X_NBT_TAG, rotAxisX);
        compound.setFloat(ROT_AXIS_Y_NBT_TAG, rotAxisY);
        compound.setFloat(ROT_AXIS_Z_NBT_TAG, rotAxisZ);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        rotationSpeed = compound.getFloat(ROTATION_SPEED_NBT_TAG);
        radius = compound.getFloat(SIZE_NBT_TAG);

        rotAngle = compound.getFloat(ROT_ANGLE_NBT_TAG);
        rotAxisX = compound.getFloat(ROT_AXIS_X_NBT_TAG);
        rotAxisY = compound.getFloat(ROT_AXIS_Y_NBT_TAG);
        rotAxisZ = compound.getFloat(ROT_AXIS_Z_NBT_TAG);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    public void updateToClient() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
