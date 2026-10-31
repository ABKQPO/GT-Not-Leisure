package com.science.gtnl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;

import com.gtnllib.utils.StructureFileCodec;
import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.blocks.BlockCasingsAbstract;

public class StructureUtils {

    public static String BASE_PATH = "/assets/";
    public static ConcurrentHashMap<String, String[][]> MULTIBLOCK_CACHE = new ConcurrentHashMap<>();

    public static String[][] readStructureFromFile(String fileName) {
        return MULTIBLOCK_CACHE.computeIfAbsent(fileName, name -> {
            String filePath = BASE_PATH + name.replace(':', '/') + ".mbs";
            try (InputStream inputStream = StructureUtils.class.getResourceAsStream(filePath)) {
                if (inputStream == null) {
                    throw new IllegalArgumentException("Unable to read structure file: " + name);
                }
                return StructureFileCodec.readBinary(inputStream);
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error("Failed to load structure file: {}", name, e);
                throw new IllegalStateException("Failed to load multiblock structure: " + name, e);
            }
        });
    }

    public static String[][] transposeStructure(String[][] original) {
        if (original == null || original.length == 0) {
            throw new IllegalArgumentException("Matrix is empty and cannot be transposed.");
        }

        int rows = original.length;
        int cols = original[0].length;
        String[][] transposed = new String[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = original[i][j];
            }
        }

        return transposed;
    }

    public static void printStructure(String[][] structure) {
        for (String[] row : structure) {
            ScienceNotLeisure.LOG.debug("{}", String.join(",", row));
        }
    }

    public static void setStringBlockXZ(IGregTechTileEntity aBaseMetaTileEntity, int offSetX, int offSetY, int offSetZ,
        String[][] structureString, boolean isStructureFlipped, String targetString, Block targetBlock,
        int targetMeta) {
        // Tooltip prototypes have no world.
        if (aBaseMetaTileEntity.getWorld() == null) return;
        int mDirectionX = aBaseMetaTileEntity.getFrontFacing().offsetX;
        int mDirectionZ = aBaseMetaTileEntity.getFrontFacing().offsetZ;
        int xDir = 0;
        int zDir = 0;
        if (mDirectionX == 1) {
            xDir = 1;
            zDir = 1;
        } else if (mDirectionX == -1) {
            xDir = -1;
            zDir = -1;
        }
        if (mDirectionZ == 1) {
            xDir = -1;
            zDir = 1;
        } else if (mDirectionZ == -1) {
            xDir = 1;
            zDir = -1;
        }
        int lengthX = structureString[0][0].length();
        int lengthY = structureString.length;
        int lengthZ = structureString[0].length;
        for (int x = 0; x < lengthX; x++) {
            for (int z = 0; z < lengthZ; z++) {
                for (int y = 0; y < lengthY; y++) {
                    String listStr = String.valueOf(structureString[y][z].charAt(x));
                    if (!Objects.equals(listStr, targetString)) continue;

                    int aX = (offSetX - x) * xDir;
                    int aY = offSetY - y;
                    int aZ = (offSetZ - z) * zDir;
                    if (mDirectionX == 1 || mDirectionX == -1) {
                        int temp = aX;
                        aX = aZ;
                        aZ = temp;
                    }
                    if (isStructureFlipped) {
                        if (mDirectionX == 1 || mDirectionX == -1) {
                            aZ = -aZ;
                        } else {
                            aX = -aX;
                        }
                    }

                    aBaseMetaTileEntity.getWorld()
                        .setBlock(
                            aBaseMetaTileEntity.getXCoord() + aX,
                            aBaseMetaTileEntity.getYCoord() + aY,
                            aBaseMetaTileEntity.getZCoord() + aZ,
                            targetBlock,
                            targetMeta,
                            3);
                }
            }
        }
    }

    public static void setStringBlockXZ(IGregTechTileEntity aBaseMetaTileEntity, int offSetX, int offSetY, int offSetZ,
        String[][] structureString, boolean isStructureFlipped, String targetString, Block targetBlock) {
        setStringBlockXZ(
            aBaseMetaTileEntity,
            offSetX,
            offSetY,
            offSetZ,
            structureString,
            isStructureFlipped,
            targetString,
            targetBlock,
            0);
    }

    public static String[][] replaceLetters(String[][] array, String replacement) {
        String[][] outputArray = new String[array.length][];
        for (int i = 0; i < array.length; i++) {
            outputArray[i] = new String[array[i].length];
            for (int j = 0; j < array[i].length; j++) {
                outputArray[i][j] = array[i][j].replaceAll("[A-Z]", replacement);
            }
        }
        return outputArray;
    }

    public static int getTextureIndex(Block block, int meta) {
        return ((BlockCasingsAbstract) block).getTextureIndex(meta);
    }
}
