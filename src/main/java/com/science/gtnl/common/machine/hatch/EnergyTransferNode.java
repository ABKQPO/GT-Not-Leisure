package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.Dyes.MACHINE_METAL;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.utils.enums.BlockIcons;

import gregtech.api.enums.Dyes;
import gregtech.api.enums.Textures;
import gregtech.api.graphs.Node;
import gregtech.api.graphs.NodeList;
import gregtech.api.graphs.PowerNode;
import gregtech.api.graphs.PowerNodes;
import gregtech.api.graphs.consumers.ConsumerNode;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTECable;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTETieredMachineBlock;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.mechanics.pipe.IConnectsToEnergyTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaser;
import tectech.thing.metaTileEntity.pipe.MTEPipeLaserMirror;
import tectech.thing.metaTileEntity.single.MTEDebugPowerGenerator;

public class EnergyTransferNode extends MTETieredMachineBlock implements IConnectsToEnergyTunnel {

    public long mVoltage;
    public long mAmperes;

    public EnergyTransferNode(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 7, 0, "");
    }

    public EnergyTransferNode(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new EnergyTransferNode(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setLong("mVoltage", mVoltage);
        aNBT.setLong("mAmperes", mAmperes);
        aNBT.setInteger(
            "mFrontFacing",
            getBaseMetaTileEntity().getFrontFacing()
                .ordinal());
        aNBT.setBoolean("mode", getBaseMetaTileEntity().isAllowedToWork());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        mVoltage = aNBT.getLong("mVoltage");
        mAmperes = aNBT.getLong("mAmperes");
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_EnergyTransferNode_00"),
            StatCollector.translateToLocal("Tooltip_EnergyTransferNode_01"),
            StatCollector.translateToLocal("Tooltip_EnergyTransferNode_02"),
            StatCollector.translateToLocal("Tooltip_EnergyTransferNode_03"),
            StatCollector.translateToLocal("Tooltip_EnergyTransferNode_04"),
            StatCollector.translateToLocal("Tooltip_EnergyTransferNode_05"),
            StatCollector.translateToLocal("Tooltip_EnergyTransferNode_06"),
            StatCollector.translateToLocal("Tooltip_EnergyTransferNode_07") };
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            return new ITexture[] { TextureFactory.of(BlockIcons.OVERLAY_ENERGY_TRANSFER_NODE),
                TextureFactory.of(
                    getBaseMetaTileEntity().isAllowedToWork() ? Textures.BlockIcons.OVERLAY_ENERGY_OUT_MULTI_LASER
                        : Textures.BlockIcons.OVERLAY_ENERGY_IN_MULTI_LASER),
                TextureFactory.of(
                    BlockIcons.OVERLAY_ENERGY_TRANSFER_NODE_ACTIVE,
                    Dyes.getModulation(colorIndex, MACHINE_METAL.getRGBA())) };
        } else {
            return new ITexture[] { TextureFactory.of(BlockIcons.OVERLAY_ENERGY_TRANSFER_NODE),
                TextureFactory.of(
                    getBaseMetaTileEntity().isAllowedToWork() ? Textures.BlockIcons.OVERLAY_ENERGY_OUT_MULTI_LASER
                        : Textures.BlockIcons.OVERLAY_ENERGY_IN_MULTI_LASER,
                    Dyes.getModulation(colorIndex, MACHINE_METAL.getRGBA())) };
        }
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        return new ITexture[0][0][0];
    }

    @Override
    public boolean shouldJoinIc2Enet() {
        return true;
    }

    @Override
    public long maxEUInput() {
        return mVoltage;
    }

    @Override
    public long maxEUOutput() {
        return mVoltage;
    }

    @Override
    public long maxEUStore() {
        return mVoltage * mAmperes * mAmperes * 16;
    }

    @Override
    public long maxAmperesOut() {
        return mAmperes;
    }

    @Override
    public long maxAmperesIn() {
        return mAmperes;
    }

    @Override
    public long getMinimumStoredEU() {
        return mVoltage;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isEnetInput() {
        return true;
    }

    @Override
    public boolean isEnetOutput() {
        return true;
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        boolean front = side == getBaseMetaTileEntity().getFrontFacing();
        return getBaseMetaTileEntity().isAllowedToWork() ? front : !front;
    }

    @Override
    public boolean isOutputFacing(ForgeDirection side) {
        return !isInputFacing(side);
    }

    @Override
    public void doExplosion(long aExplosionPower) {}

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && aTick % 20 == 0) {
            byte color = getBaseMetaTileEntity().getColorization();
            if (color < 0) {
                return;
            }

            if (aBaseMetaTileEntity.isAllowedToWork()) {
                for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                    if (side == aBaseMetaTileEntity.getFrontFacing()) continue;

                    IMetaTileEntity provider = findMTE(aBaseMetaTileEntity, color, side, true);
                    if (provider instanceof MetaTileEntity metaTileEntity) {
                        mVoltage = Math.max(mVoltage, metaTileEntity.maxEUOutput());
                        mAmperes = Math.max(mAmperes, metaTileEntity.maxAmperesOut());
                        moveEnergy(metaTileEntity, this);
                    }
                }
                IMetaTileEntity receiver = findMTE(
                    aBaseMetaTileEntity,
                    color,
                    aBaseMetaTileEntity.getFrontFacing(),
                    false);
                if (receiver instanceof MetaTileEntity metaTileEntity) {
                    moveEnergy(this, metaTileEntity);
                }
            } else {
                for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                    if (side == aBaseMetaTileEntity.getFrontFacing()) continue;

                    IMetaTileEntity receiver = findMTE(aBaseMetaTileEntity, color, side, false);
                    if (receiver instanceof MetaTileEntity metaTileEntity) {
                        moveEnergy(this, metaTileEntity);
                    }
                }
                IMetaTileEntity provider = findMTE(
                    aBaseMetaTileEntity,
                    color,
                    aBaseMetaTileEntity.getFrontFacing(),
                    true);
                if (provider instanceof MetaTileEntity metaTileEntity) {
                    mVoltage = Math.max(mVoltage, metaTileEntity.maxEUOutput());
                    mAmperes = Math.max(mAmperes, metaTileEntity.maxAmperesOut());
                    moveEnergy(metaTileEntity, this);
                }
            }
        }
    }

    public void moveEnergy(MetaTileEntity dynamo, MetaTileEntity energy) {
        if (GTUtility.getTier(dynamo.maxEUOutput()) > GTUtility.getTier(energy.maxEUInput())) {
            energy.doExplosion(dynamo.maxEUOutput());
            dynamo.setEUVar(
                dynamo.getBaseMetaTileEntity()
                    .getStoredEU() - dynamo.maxEUOutput());
        } else {
            long diff = Math.min(
                dynamo.maxAmperesOut() * 20L * dynamo.maxEUOutput(),
                Math.min(
                    energy.maxEUStore() - energy.getBaseMetaTileEntity()
                        .getStoredEU(),
                    dynamo.getBaseMetaTileEntity()
                        .getStoredEU()));
            dynamo.setEUVar(
                dynamo.getBaseMetaTileEntity()
                    .getStoredEU() - diff);
            energy.setEUVar(
                energy.getBaseMetaTileEntity()
                    .getStoredEU() + diff);
        }

    }

    public IMetaTileEntity findMTE(IGregTechTileEntity base, byte color, ForgeDirection travelDirection,
        boolean findProvider) {
        if (travelDirection == null) {
            return null;
        }

        ForgeDirection facingSide = travelDirection.getOpposite();

        for (short dist = 1; dist < 1000; dist++) {
            IGregTechTileEntity tGTTileEntity = base.getIGregTechTileEntityAtSideAndDistance(travelDirection, dist);
            if (tGTTileEntity == null) {
                break;
            }

            IMetaTileEntity aMetaTileEntity = tGTTileEntity.getMetaTileEntity();

            if (aMetaTileEntity instanceof MTEPipeLaserMirror tMirror && tGTTileEntity.getColorization() == color) {
                ForgeDirection mirrorFacing = tMirror.getBendDirection(facingSide);
                return findMTE(tMirror.getBaseMetaTileEntity(), color, mirrorFacing, findProvider);
            }

            if (findProvider && aMetaTileEntity instanceof MTEHatchDynamo
                && facingSide == tGTTileEntity.getFrontFacing()) {
                return aMetaTileEntity;
            }

            if (findProvider && aMetaTileEntity instanceof MTEHatchDynamoMulti
                && facingSide == tGTTileEntity.getFrontFacing()) {
                if (aMetaTileEntity instanceof MTEHatchDynamoTunnel && tGTTileEntity.getColorization() != color) break;
                return aMetaTileEntity;
            }

            if (!findProvider && aMetaTileEntity instanceof MTEHatchEnergy
                && facingSide == tGTTileEntity.getFrontFacing()) {
                return aMetaTileEntity;
            }

            if (!findProvider && aMetaTileEntity instanceof MTEHatchEnergyMulti
                && facingSide == tGTTileEntity.getFrontFacing()) {
                if (aMetaTileEntity instanceof MTEHatchEnergyTunnel && tGTTileEntity.getColorization() != color) break;
                return aMetaTileEntity;
            }

            if (findProvider && aMetaTileEntity instanceof MTEDebugPowerGenerator) {
                return aMetaTileEntity;
            }

            if (findProvider && aMetaTileEntity instanceof MetaTileEntity metaTileEntity
                && metaTileEntity.maxEUOutput() > 0
                && metaTileEntity.isOutputFacing(facingSide)) {
                return aMetaTileEntity;
            }

            if (!findProvider && aMetaTileEntity instanceof MetaTileEntity metaTileEntity
                && metaTileEntity.maxEUInput() > 0
                && metaTileEntity.isInputFacing(facingSide)) {
                return aMetaTileEntity;
            }

            if (aMetaTileEntity instanceof EnergyTransferNode && tGTTileEntity.getColorization() == color) {
                if ((findProvider && facingSide != tGTTileEntity.getFrontFacing())
                    || (!findProvider && facingSide == tGTTileEntity.getFrontFacing())) {
                    return aMetaTileEntity;
                }
            }

            if (aMetaTileEntity instanceof MTEPipeLaser pipe && tGTTileEntity.getColorization() == color) {
                if (pipe.connectionCount < 2) break;
                pipe.markUsed();
                continue;
            }

            if (aMetaTileEntity instanceof MTECable cable) {
                if (!cable.isConnectedAtSide(facingSide)) break;
                if (findProvider) continue;
                var node = ((BaseMetaPipeEntity) cable.getBaseMetaTileEntity()).getNode();
                if (!(node instanceof PowerNode tNode)) break;
                if (tNode.mConsumers == null) break;

                tNode.mHadVoltage = true;

                for (ConsumerNode consumer : tNode.mConsumers) {
                    if (consumer == null) continue;
                    if (!(consumer.mTileEntity instanceof BaseMetaTileEntity baseMetaTileEntity)) continue;
                    var meta = baseMetaTileEntity.getMetaTileEntity();
                    var gtTE = meta.getBaseMetaTileEntity();
                    if (meta == this) continue;

                    long maxAvailableVoltage = gtTE.getInputVoltage();

                    if (maxAvailableVoltage <= 0) continue;

                    long currentStoredEU = getBaseMetaTileEntity().getStoredEU();

                    int maxAvailableAmps = Math.toIntExact(currentStoredEU / maxAvailableVoltage);

                    if (maxAvailableAmps <= 0) maxAvailableAmps = 1;

                    int ampsToAttempt = Math.toIntExact(Math.min(mAmperes, maxAvailableAmps));

                    var singleConsumerList = new NodeList(new Node[] { consumer });

                    long usedAmperes = PowerNodes
                        .powerNode(tNode, null, singleConsumerList, maxAvailableVoltage, ampsToAttempt);

                    if (usedAmperes > 0) {
                        long euToDeduct = maxAvailableVoltage * usedAmperes;
                        setEUVar(getBaseMetaTileEntity().getStoredEU() - euToDeduct);
                    }
                }
            }

            break;
        }
        return null;
    }

    @Override
    public boolean canConnect(ForgeDirection side) {
        return true;
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
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            EnumChatFormatting.BLUE
                + StatCollector.translateToLocalFormatted("Info_EnergyTransferNode_00", tag.getLong("mVoltage")));
        currentTip.add(
            EnumChatFormatting.YELLOW
                + StatCollector.translateToLocalFormatted("Info_EnergyTransferNode_01", tag.getLong("mAmperes")));

        if (tag.hasKey("mFrontFacing")) {
            byte facing = tag.getByte("mFrontFacing");
            if (facing >= 0 && facing < ForgeDirection.VALID_DIRECTIONS.length) {
                currentTip.add(
                    EnumChatFormatting.GREEN + StatCollector.translateToLocalFormatted(
                        "Info_EnergyTransferNode_02",
                        ForgeDirection.getOrientation(facing)
                            .name()));
            }
        }
        if (tag.hasKey("mode")) {
            boolean mode = tag.getBoolean("mode");
            currentTip.add(
                EnumChatFormatting.LIGHT_PURPLE
                    + StatCollector.translateToLocal("Info_EnergyTransferNode_03_" + (mode ? "Out" : "In")));
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setLong("mVoltage", mVoltage);
            tag.setLong("mAmperes", mAmperes);
            tag.setInteger(
                "mFrontFacing",
                getBaseMetaTileEntity().getFrontFacing()
                    .ordinal());
            tag.setBoolean("mode", getBaseMetaTileEntity().isAllowedToWork());
        }
    }
}
