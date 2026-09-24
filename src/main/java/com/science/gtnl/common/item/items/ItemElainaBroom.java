package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.science.gtnl.common.entity.EntityElainaBroom;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.loader.EffectLoader;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.effect.TextEffects;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElainaBroom extends ItemMajoBroom {

    public ItemElainaBroom() {
        super("elaina_broom", GTNLItemList.ElainaBroom);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        super.onUpdate(stack, world, entity, slot, isSelected);
        if (!world.isRemote && entity instanceof EntityPlayer player)
            EntityElainaBroom.refreshEffect(player, EffectLoader.miss_broom_blessing, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        for (int line = 0; line < 4; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.elaina_broom.lore." + line),
                    TextEffects.GENESIS_COMPONENT_RARITY_SHADER));
        }
        tooltip.add("");
        for (int line = 0; line < 4; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.elaina_broom.tooltip." + line),
                    TextEffects.EVERCOLD_CYAN));
        }
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
