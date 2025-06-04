package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_SLAVE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.render.TextureFactory;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class SuperCraftingInputProxy extends MTEHatchInputBus implements IDualInputHatch, IDataCopyable {

    // 原有Super类型变量
    public static final String COPIED_DATA_IDENTIFIER_SUPER = "superCraftingInputProxy";
    private SuperCraftingInputHatchME masterSuper;
    private int masterSuperX, masterSuperY, masterSuperZ;
    private boolean masterSuperSet = false;

    // 新增Crafting类型变量
    public static final String COPIED_DATA_IDENTIFIER_CRAFTING = "craftingInputProxy";
    private MTEHatchCraftingInputME craftingMaster;
    private int craftingMasterX, craftingMasterY, craftingMasterZ;
    private boolean craftingMasterSet = false;

    private MTEHatchCraftingInputME master; // use getMaster() to access

    public SuperCraftingInputProxy(int aID, String aName, String aNameRegional) {
        super(
            aID,
            aName,
            aNameRegional,
            6,
            0,
            new String[] { StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_00"),
                StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_01"),
                StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_02"),
                StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_03") });
        disableSort = true;
    }

    public SuperCraftingInputProxy(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
        disableSort = true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperCraftingInputProxy(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public ItemStack[] getSharedItems() {
        if (getMasterSuper() != null) {
            return getMasterSuper().getSharedItems();
        } else if (getCraftingMaster() != null) {
            return getCraftingMaster().getSharedItems();
        } else {
            return new ItemStack[0];
        }
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return getTexturesInactive(aBaseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_ME_CRAFTING_INPUT_SLAVE) };
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        if (aTimer % 100 == 0) {
            if (masterSuperSet && getMasterSuper() == null) {
                trySetSuperMasterFromCoord(masterSuperX, masterSuperY, masterSuperZ);
            }
            if (craftingMasterSet && getCraftingMaster() == null) {
                trySetCraftingMasterFromCoord(craftingMasterX, craftingMasterY, craftingMasterZ);
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        // 优先加载Super类型数据
        if (aNBT.hasKey("masterSuper")) {
            loadSuperMasterData(aNBT.getCompoundTag("masterSuper"));
            aNBT.removeTag("craftingMaster"); // 清除可能存在的Crafting数据
        }
        // 其次加载Crafting类型数据
        else if (aNBT.hasKey("craftingMaster")) {
            loadCraftingMasterData(aNBT.getCompoundTag("craftingMaster"));
            aNBT.removeTag("masterSuper"); // 清除可能存在的Super数据
        }
    }

    private void loadSuperMasterData(NBTTagCompound masterNBT) {
        masterSuperX = masterNBT.getInteger("xSuper");
        masterSuperY = masterNBT.getInteger("ySuper");
        masterSuperZ = masterNBT.getInteger("zSuper");
        masterSuperSet = true;
        clearCraftingMaster(); // 清除Crafting数据
    }

    private void loadCraftingMasterData(NBTTagCompound masterNBT) {
        craftingMasterX = masterNBT.getInteger("x");
        craftingMasterY = masterNBT.getInteger("y");
        craftingMasterZ = masterNBT.getInteger("z");
        craftingMasterSet = true;
        clearSuperMaster(); // 清除Super数据
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        // 只保存当前有效的主仓室数据
        if (masterSuperSet) {
            saveSuperMasterData(aNBT);
        } else if (craftingMasterSet) {
            saveCraftingMasterData(aNBT);
        }
    }

    private void saveSuperMasterData(NBTTagCompound aNBT) {
        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("xSuper", masterSuperX);
        masterNBT.setInteger("ySuper", masterSuperY);
        masterNBT.setInteger("zSuper", masterSuperZ);
        aNBT.setTag("masterSuper", masterNBT);
        aNBT.removeTag("craftingMaster"); // 清除残留数据
    }

    private void saveCraftingMasterData(NBTTagCompound aNBT) {
        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("x", craftingMasterX);
        masterNBT.setInteger("y", craftingMasterY);
        masterNBT.setInteger("z", craftingMasterZ);
        aNBT.setTag("craftingMaster", masterNBT);
        aNBT.removeTag("masterSuper"); // 清除残留数据
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        var ret = new ArrayList<String>();
        if (getMasterSuper() != null) {
            ret.add(
                StatCollector.translateToLocal("Chat_SuperCraftingInputProxy_00_00") + masterSuperX
                    + ", "
                    + masterSuperY
                    + ", "
                    + masterSuperZ
                    + ".");
            ret.addAll(Arrays.asList(getMasterSuper().getInfoData()));
        } else if (getCraftingMaster() != null) {
            ret.add(
                StatCollector.translateToLocal("Chat_SuperCraftingInputProxy_00_01") + craftingMasterX
                    + ", "
                    + craftingMasterY
                    + ", "
                    + craftingMasterZ
                    + ".");
            ret.addAll(Arrays.asList(getCraftingMaster().getInfoData()));
        } else {
            ret.add(StatCollector.translateToLocal("Chat_SuperCraftingInputProxy_01"));
        }
        return ret.toArray(new String[0]);
    }

    // 清除主仓室方法
    private void clearSuperMaster() {
        masterSuper = null;
        masterSuperSet = false;
        masterSuperX = masterSuperY = masterSuperZ = 0;
    }

    private void clearCraftingMaster() {
        craftingMaster = null;
        craftingMasterSet = false;
        craftingMasterX = craftingMasterY = craftingMasterZ = 0;
    }

    // 原有Super类型方法
    public SuperCraftingInputHatchME getMasterSuper() {
        if (masterSuper == null) return null;
        if (masterSuper.getBaseMetaTileEntity() == null) {
            masterSuper = null;
        }
        return masterSuper;
    }

    // 新增Crafting类型方法
    public MTEHatchCraftingInputME getCraftingMaster() {
        if (craftingMaster == null) return null;
        if (craftingMaster.getBaseMetaTileEntity() == null) {
            craftingMaster = null;
        }
        return craftingMaster;
    }

    @Override
    public byte getTierForStructure() {
        if (getMasterSuper() != null) return getMasterSuper().getTierForStructure();
        if (getCraftingMaster() != null) return getCraftingMaster().getTierForStructure();
        return super.getTierForStructure();
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public Iterator<? extends IDualInputInventory> inventories() {
        if (getMasterSuper() != null) return getMasterSuper().inventories();
        if (getCraftingMaster() != null) return getCraftingMaster().inventories();
        return Collections.emptyIterator();
    }

    @Override
    public Optional<IDualInputInventory> getFirstNonEmptyInventory() {
        if (getMasterSuper() != null) return getMasterSuper().getFirstNonEmptyInventory();
        if (getCraftingMaster() != null) return getCraftingMaster().getFirstNonEmptyInventory();
        return Optional.empty();
    }

    @Override
    public boolean supportsFluids() {
        if (getMasterSuper() != null) return getMasterSuper().supportsFluids();
        if (getCraftingMaster() != null) return getCraftingMaster().supportsFluids();
        return false;
    }

    @Override
    public boolean justUpdated() {
        if (getMasterSuper() != null) return getMasterSuper().justUpdated();
        if (getCraftingMaster() != null) return getCraftingMaster().justUpdated();
        return false;
    }

    // 链接方法（新增互斥逻辑）
    public SuperCraftingInputHatchME trySetSuperMasterFromCoord(int x, int y, int z) {
        clearCraftingMaster(); // 清除现有Crafting链接
        TileEntity te = getBaseMetaTileEntity().getWorld()
            .getTileEntity(x, y, z);
        if (te instanceof IGregTechTileEntity gtTe
            && gtTe.getMetaTileEntity() instanceof SuperCraftingInputHatchME mte) {
            masterSuperX = x;
            masterSuperY = y;
            masterSuperZ = z;
            masterSuperSet = true;
            masterSuper = mte;
            return mte;
        }
        return null;
    }

    public MTEHatchCraftingInputME trySetCraftingMasterFromCoord(int x, int y, int z) {
        clearSuperMaster(); // 清除现有Super链接
        TileEntity te = getBaseMetaTileEntity().getWorld()
            .getTileEntity(x, y, z);
        if (te instanceof IGregTechTileEntity gtTe && gtTe.getMetaTileEntity() instanceof MTEHatchCraftingInputME mte) {
            craftingMasterX = x;
            craftingMasterY = y;
            craftingMasterZ = z;
            craftingMasterSet = true;
            craftingMaster = mte;
            return mte;
        }
        return null;
    }

    private boolean tryLinkDataStick(EntityPlayer aPlayer) {
        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) return false;

        if (dataStick.stackTagCompound != null) {
            // 处理Super类型链接
            if ("SuperCraftingInputBuffer".equals(dataStick.stackTagCompound.getString("typeSuper"))) {
                NBTTagCompound nbt = dataStick.stackTagCompound;
                boolean success = trySetSuperMasterFromCoord(
                    nbt.getInteger("xSuper"),
                    nbt.getInteger("ySuper"),
                    nbt.getInteger("zSuper")) != null;
                aPlayer.addChatMessage(
                    new ChatComponentTranslation(
                        success ? "Chat_SuperCraftingInputProxy_02" : "Chat_SuperCraftingInputProxy_03"));
                return true;
            }
            // 处理Crafting类型链接
            if ("CraftingInputBuffer".equals(dataStick.stackTagCompound.getString("type"))) {
                NBTTagCompound nbt = dataStick.stackTagCompound;
                boolean success = trySetCraftingMasterFromCoord(
                    nbt.getInteger("x"),
                    nbt.getInteger("y"),
                    nbt.getInteger("z")) != null;
                aPlayer.addChatMessage(
                    new ChatComponentTranslation(
                        success ? "Chat_SuperCraftingInputProxy_02" : "Chat_SuperCraftingInputProxy_03"));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!(aPlayer instanceof EntityPlayerMP)) return false;
        if (tryLinkDataStick(aPlayer)) return true;

        // 优先响应当前有效的主仓室
        if (getMasterSuper() != null) {
            return getMasterSuper().onRightclick(getMasterSuper().getBaseMetaTileEntity(), aPlayer);
        }
        if (getCraftingMaster() != null) {
            return getCraftingMaster().onRightclick(getCraftingMaster().getBaseMetaTileEntity(), aPlayer);
        }
        return false;
    }

    // 数据复制分开处理两种类型
    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return COPIED_DATA_IDENTIFIER_SUPER; // 保持原有标识符不变
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound nbt) {
        if (nbt == null) return false;

        // 处理原有Super类型
        if (COPIED_DATA_IDENTIFIER_SUPER.equals(nbt.getString("typeSuper")) && nbt.hasKey("masterSuper")) {
            NBTTagCompound masterNBT = nbt.getCompoundTag("masterSuper");
            return trySetSuperMasterFromCoord(
                masterNBT.getInteger("xSuper"),
                masterNBT.getInteger("ySuper"),
                masterNBT.getInteger("zSuper")) != null;
        }
        // 处理新增Crafting类型
        if (COPIED_DATA_IDENTIFIER_CRAFTING.equals(nbt.getString("type")) && nbt.hasKey("master")) {
            NBTTagCompound masterNBT = nbt.getCompoundTag("master");
            return trySetCraftingMasterFromCoord(
                masterNBT.getInteger("x"),
                masterNBT.getInteger("y"),
                masterNBT.getInteger("z")) != null;
        }
        return false;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound tag = new NBTTagCompound();
        // 优先复制Super类型数据
        if (masterSuperSet) {
            tag.setString("typeSuper", COPIED_DATA_IDENTIFIER_SUPER);
            NBTTagCompound masterNBT = new NBTTagCompound();
            masterNBT.setInteger("xSuper", masterSuperX);
            masterNBT.setInteger("ySuper", masterSuperY);
            masterNBT.setInteger("zSuper", masterSuperZ);
            tag.setTag("masterSuper", masterNBT);
        }
        // 其次复制Crafting类型数据
        else if (craftingMasterSet) {
            tag.setString("type", COPIED_DATA_IDENTIFIER_CRAFTING);
            NBTTagCompound masterNBT = new NBTTagCompound();
            masterNBT.setInteger("x", craftingMasterX);
            masterNBT.setInteger("y", craftingMasterY);
            masterNBT.setInteger("z", craftingMasterZ);
            tag.setTag("master", masterNBT);
        }
        return tag;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();

        boolean superLinked = tag.getBoolean("superLinked");
        boolean craftingLinked = tag.getBoolean("craftingLinked");
        currenttip.add(
            (superLinked || craftingLinked) ? StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_05_00")
                : StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_05_01"));

        if (superLinked) {
            currenttip.add(
                StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_04") + tag.getInteger(
                    "superMasterX") + ", " + tag.getInteger("superMasterY") + ", " + tag.getInteger("superMasterZ"));
        }
        if (craftingLinked) {
            currenttip.add(
                StatCollector.translateToLocal("Tooltip_SuperCraftingInputProxy_04") + tag.getInteger("craftingMasterX")
                    + ", "
                    + tag.getInteger("craftingMasterY")
                    + ", "
                    + tag.getInteger("craftingMasterZ"));
        }

        if (tag.hasKey("superMasterName")) {
            currenttip.add(EnumChatFormatting.GOLD + tag.getString("superMasterName") + EnumChatFormatting.RESET);
        }
        if (tag.hasKey("craftingMasterName")) {
            currenttip.add(EnumChatFormatting.GOLD + tag.getString("craftingMasterName") + EnumChatFormatting.RESET);
        }

        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        tag.setBoolean("superLinked", getMasterSuper() != null);
        tag.setBoolean("craftingLinked", getCraftingMaster() != null);

        if (masterSuperSet) {
            tag.setInteger("superMasterX", masterSuperX);
            tag.setInteger("superMasterY", masterSuperY);
            tag.setInteger("superMasterZ", masterSuperZ);
        }
        if (craftingMasterSet) {
            tag.setInteger("craftingMasterX", craftingMasterX);
            tag.setInteger("craftingMasterY", craftingMasterY);
            tag.setInteger("craftingMasterZ", craftingMasterZ);
        }

        if (getMasterSuper() != null) tag.setString("superMasterName", getMasterSuper().getName());
        if (getCraftingMaster() != null) tag.setString("craftingMasterName", getCraftingMaster().getName());

        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    @Override
    public List<ItemStack> getItemsForHoloGlasses() {
        if (getMasterSuper() != null) return getMasterSuper().getItemsForHoloGlasses();
        if (getCraftingMaster() != null) return getCraftingMaster().getItemsForHoloGlasses();
        return null;
    }
}
