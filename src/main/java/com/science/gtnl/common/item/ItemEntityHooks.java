package com.science.gtnl.common.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;

public class ItemEntityHooks {

    public static void moveFloatingOnLava(EntityItem item, double mx, double my, double mz) {
        double buoyancy = 0;
        if (item.isImmuneToFire() && item.worldObj
            .getBlock(
                MathHelper.floor_double(item.posX),
                MathHelper.floor_double(item.posY),
                MathHelper.floor_double(item.posZ))
            .getMaterial() == Material.lava) {
            item.motionY += 0.04D;
            my += 0.04D;
            buoyancy = 0.3D;
            mx *= 0.5D;
            mz *= 0.5D;
        }
        item.moveEntity(mx, my + buoyancy, mz);
    }
}
