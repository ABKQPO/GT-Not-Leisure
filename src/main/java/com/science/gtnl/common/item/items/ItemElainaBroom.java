package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;

import com.science.gtnl.common.entity.EntityElainaBroom;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElainaBroom extends ItemMajoBroom {

    public ItemElainaBroom() {
        super("elaina_broom", GTNLItemList.ElainaBroom);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(RESOURCE_ROOT_ID + ":elaina_broom");
    }

    @Override
    protected EntityMajoBroom createBroom(World world) {
        return new EntityElainaBroom(world);
    }
}
