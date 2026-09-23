package com.science.gtnl.common.machine.hatch;

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
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.util.IInterfaceViewable;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.render.TextureFactory;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IHatchWatcher;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputSlave;
import gregtech.common.tileentities.machines.RecipeCheckReason;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@IMetaTileEntity.SkipGenerateDescription
public class SuperCraftingInputProxy extends MTEHatchInputBus implements IDualInputHatch, IDataCopyable, IHatchWatcher {

    private SuperCraftingInputHatchME masterSuper;
    private int masterSuperX, masterSuperY, masterSuperZ;
    private boolean masterSuperSet = false;

    private MTEHatchCraftingInputME craftingMaster;
    private int craftingMasterX, craftingMasterY, craftingMasterZ;
    private boolean craftingMasterSet = false;
    private boolean registeredWithMaster = false;

    public SuperCraftingInputProxy(int aID, String aName, String aNameRegional) {
        super(
            aID,
            aName,
            aNameRegional,
            6,
            0,
            new String[] { StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.tooltip.0"),
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.tooltip.1"),
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.tooltip.2"),
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.tooltip.3") });
        disableSort = true;
    }

    public SuperCraftingInputProxy(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
        disableSort = true;
    }

    @Override
    public String[] getDescription() {
        return mDescriptionArray;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperCraftingInputProxy(mName, mTier, mDescriptionArray, mTextures);
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

        if (aNBT.hasKey("masterSuper")) {
            loadSuperMasterData(aNBT.getCompoundTag("masterSuper"));
            aNBT.removeTag("craftingMaster");
        } else if (aNBT.hasKey("craftingMaster")) {
            loadCraftingMasterData(aNBT.getCompoundTag("craftingMaster"));
            aNBT.removeTag("masterSuper");
        }
    }

    private void loadSuperMasterData(NBTTagCompound masterNBT) {
        masterSuperX = masterNBT.getInteger("xSuper");
        masterSuperY = masterNBT.getInteger("ySuper");
        masterSuperZ = masterNBT.getInteger("zSuper");
        masterSuperSet = true;
        clearCraftingMaster();
    }

    private void loadCraftingMasterData(NBTTagCompound masterNBT) {
        craftingMasterX = masterNBT.getInteger("x");
        craftingMasterY = masterNBT.getInteger("y");
        craftingMasterZ = masterNBT.getInteger("z");
        craftingMasterSet = true;
        clearSuperMaster();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

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
        aNBT.removeTag("craftingMaster");
    }

    private void saveCraftingMasterData(NBTTagCompound aNBT) {
        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("x", craftingMasterX);
        masterNBT.setInteger("y", craftingMasterY);
        masterNBT.setInteger("z", craftingMasterZ);
        aNBT.setTag("craftingMaster", masterNBT);
        aNBT.removeTag("masterSuper");
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
                IGregTechDeviceInformation.encode(
                    "gtnl.hatch.super_crafting_input_proxy.master_super",
                    masterSuperX,
                    masterSuperY,
                    masterSuperZ));
            ret.addAll(Arrays.asList(getMasterSuper().getInfoData()));
        } else if (getCraftingMaster() != null) {
            ret.add(
                IGregTechDeviceInformation.encode(
                    "gtnl.hatch.super_crafting_input_proxy.crafting_master",
                    craftingMasterX,
                    craftingMasterY,
                    craftingMasterZ));
            ret.addAll(Arrays.asList(getCraftingMaster().getInfoData()));
        } else {
            ret.add("gtnl.hatch.super_crafting_input_proxy.unlinked");
        }
        return ret.toArray(new String[0]);
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
    public void addWatcher(IHatchWatcher watcher) {
        watchers.add(watcher);
        registerWithMaster();
    }

    @Override
    public void removeWatcher(IHatchWatcher watcher) {
        watchers.remove(watcher);
        if (watchers.isEmpty()) {
            unregisterFromMaster();
        }
    }

    @Override
    public void scheduleRecipeCheck(RecipeCheckReason reason) {
        for (IHatchWatcher watcher : watchers) {
            watcher.scheduleRecipeCheck(reason);
        }
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
        return new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_SLAVE) };
    }

    public SuperCraftingInputHatchME trySetSuperMasterFromCoord(int x, int y, int z) {
        unregisterFromMaster();
        clearCraftingMaster();
        TileEntity te = getBaseMetaTileEntity().getWorld()
            .getTileEntity(x, y, z);
        if (te instanceof IGregTechTileEntity gtTe
            && gtTe.getMetaTileEntity() instanceof SuperCraftingInputHatchME mte) {
            masterSuperX = x;
            masterSuperY = y;
            masterSuperZ = z;
            masterSuperSet = true;
            masterSuper = mte;
            registerWithMaster();
            return mte;
        }
        registerWithMaster();
        return null;
    }

    public MTEHatchCraftingInputME trySetCraftingMasterFromCoord(int x, int y, int z) {
        unregisterFromMaster();
        clearSuperMaster();
        TileEntity te = getBaseMetaTileEntity().getWorld()
            .getTileEntity(x, y, z);
        if (te instanceof IGregTechTileEntity gtTe && gtTe.getMetaTileEntity() instanceof MTEHatchCraftingInputME mte) {
            craftingMasterX = x;
            craftingMasterY = y;
            craftingMasterZ = z;
            craftingMasterSet = true;
            craftingMaster = mte;
            registerWithMaster();
            return mte;
        }
        registerWithMaster();
        return null;
    }

    private boolean tryLinkDataStick(EntityPlayer aPlayer) {
        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) return false;

        if (dataStick.stackTagCompound != null) {
            if ("SuperCraftingInputBuffer".equals(dataStick.stackTagCompound.getString("typeSuper"))) {
                NBTTagCompound nbt = dataStick.stackTagCompound;
                boolean success = trySetSuperMasterFromCoord(
                    nbt.getInteger("xSuper"),
                    nbt.getInteger("ySuper"),
                    nbt.getInteger("zSuper")) != null;
                aPlayer.addChatMessage(
                    new ChatComponentTranslation(
                        success ? "gtnl.hatch.super_crafting_input_proxy.bind_success"
                            : "gtnl.hatch.super_crafting_input_proxy.bind_failed"));
                return true;
            }
            if ("CraftingInputBuffer".equals(dataStick.stackTagCompound.getString("type"))) {
                NBTTagCompound nbt = dataStick.stackTagCompound;
                boolean success = trySetCraftingMasterFromCoord(
                    nbt.getInteger("x"),
                    nbt.getInteger("y"),
                    nbt.getInteger("z")) != null;
                aPlayer.addChatMessage(
                    new ChatComponentTranslation(
                        success ? "gtnl.hatch.super_crafting_input_proxy.bind_success"
                            : "gtnl.hatch.super_crafting_input_proxy.bind_failed"));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!(aPlayer instanceof EntityPlayerMP)) return false;
        if (tryLinkDataStick(aPlayer)) return true;

        if (getMasterSuper() != null) {
            return getMasterSuper().onRightclick(getMasterSuper().getBaseMetaTileEntity(), aPlayer);
        }
        if (getCraftingMaster() != null) {
            return getCraftingMaster().onRightclick(getCraftingMaster().getBaseMetaTileEntity(), aPlayer);
        }
        return false;
    }

    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return MTEHatchCraftingInputSlave.COPIED_DATA_IDENTIFIER;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound nbt) {
        if (nbt == null || !nbt.hasKey("dataType") || !nbt.hasKey("dataPayload")) {
            return false;
        }

        String type = nbt.getString("dataType");
        NBTTagCompound data = nbt.getCompoundTag("dataPayload");

        return switch (type) {
            case "SUPER" -> trySetSuperMasterFromCoord(data.getInteger("x"), data.getInteger("y"), data.getInteger("z"))
                != null;
            case "CRAFTING" -> trySetCraftingMasterFromCoord(
                data.getInteger("x"),
                data.getInteger("y"),
                data.getInteger("z")) != null;
            default -> false;
        };
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound tag = new NBTTagCompound();

        if (masterSuperSet) {
            tag.setString("dataType", "SUPER");
            NBTTagCompound data = new NBTTagCompound();
            data.setInteger("x", masterSuperX);
            data.setInteger("y", masterSuperY);
            data.setInteger("z", masterSuperZ);
            tag.setTag("dataPayload", data);
        } else if (craftingMasterSet) {
            tag.setString("dataType", "CRAFTING");
            NBTTagCompound data = new NBTTagCompound();
            data.setInteger("x", craftingMasterX);
            data.setInteger("y", craftingMasterY);
            data.setInteger("z", craftingMasterZ);
            tag.setTag("dataPayload", data);
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
            (superLinked || craftingLinked)
                ? StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.waila.connected")
                : StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.waila.unlinked"));

        if (superLinked) {
            currenttip.add(
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.waila.connected_to") + " "
                    + tag.getInteger("superMasterX")
                    + ", "
                    + tag.getInteger("superMasterY")
                    + ", "
                    + tag.getInteger("superMasterZ"));
        }
        if (craftingLinked) {
            currenttip.add(
                StatCollector.translateToLocal("gtnl.hatch.super_crafting_input_proxy.waila.connected_to") + " "
                    + tag.getInteger("craftingMasterX")
                    + ", "
                    + tag.getInteger("craftingMasterY")
                    + ", "
                    + tag.getInteger("craftingMasterZ"));
        }

        gtnl$addMasterName(currenttip, tag, "superMaster");
        gtnl$addMasterName(currenttip, tag, "craftingMaster");

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

        if (getMasterSuper() != null) gtnl$writeMasterName(tag, "superMaster", getMasterSuper());
        if (getCraftingMaster() != null) gtnl$writeMasterName(tag, "craftingMaster", getCraftingMaster());

        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    private static void gtnl$writeMasterName(NBTTagCompound tag, String key, IInterfaceViewable master) {
        tag.setString(key + "RawName", master.getRawName());
        IChatComponent suffix = master.getNameSuffix();
        if (suffix != null) tag.setString(key + "Suffix", IChatComponent.Serializer.func_150696_a(suffix));
        ItemStack display = master.getDisplayRep();
        if (display != null) tag.setTag(key + "Display", display.writeToNBT(new NBTTagCompound()));
    }

    private static void gtnl$addMasterName(List<String> currenttip, NBTTagCompound tag, String key) {
        if (!tag.hasKey(key + "RawName")) return;

        ItemStack display = tag.hasKey(key + "Display")
            ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag(key + "Display"))
            : null;
        String name = gtnl$localize(tag.getString(key + "RawName"), display);
        String suffix = tag.getString(key + "Suffix");
        if (!suffix.isEmpty()) name += gtnl$resolveSuffix(suffix);
        currenttip.add(EnumChatFormatting.GOLD + name + EnumChatFormatting.RESET);
    }

    private static String gtnl$localize(String rawName, ItemStack display) {
        if (StatCollector.canTranslate(rawName)) return StatCollector.translateToLocal(rawName);
        String fallback = rawName + ".name";
        if (StatCollector.canTranslate(fallback)) return StatCollector.translateToLocal(fallback);
        return display == null ? rawName : display.getDisplayName();
    }

    private static String gtnl$resolveSuffix(String suffix) {
        try {
            IChatComponent component = IChatComponent.Serializer.func_150699_a(suffix);
            return component == null ? suffix : component.getUnformattedText();
        } catch (Exception ignored) {
            return suffix;
        }
    }

    @Override
    public List<ItemStack> getItemsForHoloGlasses() {
        if (getMasterSuper() != null) return getMasterSuper().getItemsForHoloGlasses();
        if (getCraftingMaster() != null) return getCraftingMaster().getItemsForHoloGlasses();
        return null;
    }

    public SuperCraftingInputHatchME getMasterSuper() {
        if (masterSuper == null) return null;
        if (masterSuper.getBaseMetaTileEntity() == null) {
            unregisterFromMaster();
            masterSuper = null;
        }
        return masterSuper;
    }

    public MTEHatchCraftingInputME getCraftingMaster() {
        if (craftingMaster == null) return null;
        if (craftingMaster.getBaseMetaTileEntity() == null) {
            unregisterFromMaster();
            craftingMaster = null;
        }
        return craftingMaster;
    }

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

    private void registerWithMaster() {
        if (watchers.isEmpty() || registeredWithMaster) return;
        if (masterSuper != null) {
            masterSuper.addWatcher(this);
            registeredWithMaster = true;
        } else if (craftingMaster != null) {
            craftingMaster.addWatcher(this);
            registeredWithMaster = true;
        }
    }

    private void unregisterFromMaster() {
        if (!registeredWithMaster) return;
        if (masterSuper != null) {
            masterSuper.removeWatcher(this);
        } else if (craftingMaster != null) {
            craftingMaster.removeWatcher(this);
        }
        registeredWithMaster = false;
    }
}
