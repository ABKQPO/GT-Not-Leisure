package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.entity.EntitySaddleSlime;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class SlimeSaddle extends Item {

    public SlimeSaddle() {
        this.setUnlocalizedName("SlimeSaddle");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setMaxStackSize(1);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "SlimeSaddle");
        GameRegistry.registerItem(this, getUnlocalizedName());
        GTNLItemList.SlimeSaddle.set(new ItemStack(this, 1));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) return stack;
        if (player.ridingEntity instanceof EntitySaddleSlime slime && slime.getSaddle()) {
            player.dismountEntity(slime);
            world.removeEntity(slime);
            world.playSoundAtEntity(player, "mob.slime.big", 1.0f, 0.5f);
            return stack;
        }

        EntitySaddleSlime slime = new EntitySaddleSlime(world);
        slime.publicSetSlimeSize(4);
        slime.setPosition(player.posX, player.posY + 1, player.posZ);
        slime.setSaddle(true);
        slime.getEntityData()
            .setBoolean("PersistenceRequired", true);
        world.spawnEntityInWorld(slime);
        player.mountEntity(slime);
        world.playSoundAtEntity(player, "mob.slime.big", 1.0f, 1.0f);
        return stack;
    }

}
