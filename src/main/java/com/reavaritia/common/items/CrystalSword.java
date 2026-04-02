package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.reavaritia.utils.item.ToolHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalSword extends ItemSword implements SubtitleDisplay {

    public CrystalSword() {
        super(ToolHelper.CRYSTAL);
        this.setUnlocalizedName("CrystalSword");
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "CrystalSword");
        this.setMaxDamage(8888);
        ReAvaItemList.CrystalSword.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_CrystalSword_00"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            toggleSwordMode(stack, world);
            return stack;
        } else {
            if (isSwordAuraActive(stack)) {
                handleSwordAttack(stack, world, player);
            }
        }
        return super.onItemRightClick(stack, world, player);
    }

    private void toggleSwordMode(ItemStack stack, World world) {
        NBTTagCompound nbt = getOrCreateNBT(stack);
        boolean currentMode = nbt.getBoolean("SwordAuraMode");
        nbt.setBoolean("SwordAuraMode", !currentMode);

        String messageKey = !currentMode ? "Tooltip_CrystalSword_Aura_On" : "Tooltip_CrystalSword_Aura_Off";

        if (world.isRemote) {
            showSubtitle(messageKey, 0);
        }
    }

    private void handleSwordAttack(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbt = stack.getTagCompound();
        long lastUsed = nbt.getLong("LastUsed");
        long cooldown = 5000;

        if (System.currentTimeMillis() - lastUsed < cooldown) {
            if (world.isRemote) {
                long remainingTime = (cooldown - (System.currentTimeMillis() - lastUsed)) / 1000;
                showSubtitle("Tooltip_CrystalSword_Aura_00", remainingTime);
            }
            return;
        }

        if (!world.isRemote) {
            executeSwordAttack(world, player);
        } else {
            spawnClientParticles(world, player);
        }

        nbt.setLong("LastUsed", System.currentTimeMillis());
    }

    private void executeSwordAttack(World world, EntityPlayer player) {
        Vec3 lookVec = player.getLookVec();
        double range = 100.0;
        double width = 2.0;

        Vec3 startPos = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);

        Vec3 endPos = Vec3.createVectorHelper(
            startPos.xCoord + lookVec.xCoord * range,
            startPos.yCoord + lookVec.yCoord * range,
            startPos.zCoord + lookVec.zCoord * range);

        AxisAlignedBB attackArea = calculateAttackArea(startPos, endPos, width);

        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, attackArea);
        for (EntityLivingBase target : targets) {
            if (target != player) {
                target.attackEntityFrom(DamageSource.outOfWorld, 52.0F);
            }
        }

        if (world.isRemote) {
            spawnClientParticles(world, player);
        }
    }

    private AxisAlignedBB calculateAttackArea(Vec3 start, Vec3 end, double width) {
        return AxisAlignedBB.getBoundingBox(
            Math.min(start.xCoord, end.xCoord) - width,
            Math.min(start.yCoord, end.yCoord) - width,
            Math.min(start.zCoord, end.zCoord) - width,
            Math.max(start.xCoord, end.xCoord) + width,
            Math.max(start.yCoord, end.yCoord) + width,
            Math.max(start.zCoord, end.zCoord) + width);
    }

    // 这里的粒子生成无法超过长度22格
    @SideOnly(Side.CLIENT)
    private void spawnClientParticles(World world, EntityPlayer player) {
        Vec3 lookVec = player.getLookVec();
        double range = 10000;
        double step = 0.5;

        Vec3 startPos = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);

        for (double d = 0; d < range; d += step) {
            double x = startPos.xCoord + lookVec.xCoord * d;
            double y = startPos.yCoord + lookVec.yCoord * d;
            double z = startPos.zCoord + lookVec.zCoord * d;

            for (int i = 0; i < 2; i++) {
                double offsetX = (world.rand.nextDouble() - 1) * 0.5;
                double offsetY = (world.rand.nextDouble() - 1) * 0.5;
                double offsetZ = (world.rand.nextDouble() - 1) * 0.5;

                String particleType = "flame";

                world.spawnParticle(
                    particleType,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    lookVec.xCoord * 0.5,
                    lookVec.yCoord * 0.5,
                    lookVec.zCoord * 0.5);
            }
        }
    }

    private NBTTagCompound getOrCreateNBT(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }

    private boolean isSwordAuraActive(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound()
            .getBoolean("SwordAuraMode");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle(String messageKey, long cooldown) {
        IChatComponent component = new ChatComponentTranslation(messageKey);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText() + cooldown, true);
    }
}
