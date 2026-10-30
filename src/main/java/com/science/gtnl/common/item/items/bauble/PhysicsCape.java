package com.science.gtnl.common.item.items.bauble;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;

import com.science.gtnl.client.render.PhysicsCapeRenderer;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.utils.item.ItemUtils;

import baubles.api.BaubleType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import vazkii.botania.api.item.IBaubleRender;

public class PhysicsCape extends BaubleItem implements IBaubleRender {

    public PhysicsCape() {
        this.setMaxDamage(2000);
        this.setMaxStackSize(1);
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.physics_cape";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("item.gtnl.physics_cape.tooltip.0"));
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }

    private double serverLastX = Double.NaN;
    private double serverLastY = Double.NaN;
    private double serverLastZ = Double.NaN;
    private double clientLastX = Double.NaN;
    private double clientLastY = Double.NaN;
    private double clientLastZ = Double.NaN;

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        super.onWornTick(itemstack, player);
        if (!(player instanceof EntityPlayer)) return;
        World world = player.worldObj;
        if (!world.isRemote) {
            double serverCurrX = Math.floor(player.posX * 1e5) / 1e5;
            double serverCurrY = Math.floor(player.posY * 1e3) / 1e3;
            double serverCurrZ = Math.floor(player.posZ * 1e5) / 1e5;

            if (Double.compare(serverCurrX, serverLastX) == 0 && Double.compare(serverCurrY, serverLastY) == 0
                && Double.compare(serverCurrZ, serverLastZ) == 0) {
                return;
            }

            serverLastX = serverCurrX;
            serverLastY = serverCurrY;
            serverLastZ = serverCurrZ;

            int px = MathHelper.floor_double(serverCurrX);
            int py = MathHelper.floor_double(serverCurrY);
            int pz = MathHelper.floor_double(serverCurrZ);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 0; dy++) {
                        TileEntity te = world.getTileEntity(px + dx, py + dy, pz + dz);
                        if (te instanceof IGregTechTileEntity gtTE
                            && gtTE.getMetaTileEntity() instanceof MetaTileEntity mte) {
                            if (mte.getEUVar() <= mte.maxEUStore()) {
                                long storeEU = mte.getEUVar();
                                long addedEU = GTValues.V[4];
                                mte.setEUVar(Math.min(storeEU + addedEU, mte.maxEUStore()));
                                itemstack.damageItem(1, player);
                            }
                            if (itemstack.getItemDamage() == itemstack.getMaxDamage() - 1) {
                                ItemUtils.removeItemFromPlayer((EntityPlayer) player, itemstack);
                                break;
                            }
                        }
                    }
                }
            }

            world.playSoundEffect(
                clientLastX,
                clientLastY,
                clientLastZ,
                "random.explode",
                4.0F,
                (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        } else {

            double clientCurrX = Math.floor(player.posX * 1e5) / 1e5;
            double clientCurrY = Math.floor(player.posY * 1e3) / 1e3;
            double clientCurrZ = Math.floor(player.posZ * 1e5) / 1e5;

            if (Double.compare(clientCurrX, clientLastX) == 0 && Double.compare(clientCurrY, clientLastY) == 0
                && Double.compare(clientCurrZ, clientLastZ) == 0) {
                return;
            }

            clientLastX = clientCurrX;
            clientLastY = clientCurrY;
            clientLastZ = clientCurrZ;

            world.spawnParticle("largeexplode", clientLastX, clientLastY - 1, clientLastZ, 1.0D, 0.0D, 0.0D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, IBaubleRender.RenderType type) {
        PhysicsCapeRenderer.render(event, type);
    }
}
