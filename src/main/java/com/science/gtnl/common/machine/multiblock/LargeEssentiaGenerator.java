package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ExoticDynamo;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.machine.LargeEssentiaEnergyData;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.fluids.GTPPFluids;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;

public class LargeEssentiaGenerator extends MultiMachineBase<LargeEssentiaGenerator> implements ISurvivalConstructable {

    public int mStableValue = 0;
    public int mTierLimit = -1;
    public int tierMachine = -1;
    public long mLeftEnergy;
    public int mUpgrade = 1;
    public final XSTR random = new XSTR();
    public List<TileEntityEssentiaHatch> mEssentiaHatch = new ArrayList<>();

    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 0;
    private static final int DEPTH_OFF_SET = 4;

    public static final Fluid XPJUICE = FluidRegistry.getFluid("xpjuice");
    public static final Fluid LIFEESSENCE = FluidRegistry.getFluid("lifeessence");
    public static final Fluid PURE = FluidRegistry.getFluid("fluidpure");
    public static final Fluid DEATH = FluidRegistry.getFluid("fluiddeath");
    public static final Fluid SPIRIT = FluidRegistry.getFluid("witchery:fluidspirit");
    public static final Fluid HOLLOW_TEARS = FluidRegistry.getFluid("witchery:hollowtears");

    public static final Object2IntMap<GTUtility.ItemId> ESSENTIA_UPGRADE = new Object2IntOpenHashMap<>();

    static {
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeEmpty.get(1)), 0);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeAir.get(1)), 1);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeThermal.get(1)), 2);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeUnstable.get(1)), 3);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeVictus.get(1)), 4);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeTainted.get(1)), 5);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeMechanics.get(1)), 6);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeSpirit.get(1)), 7);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeRadiation.get(1)), 8);
        ESSENTIA_UPGRADE.put(GTUtility.ItemId.createWithoutNBT(GTNLItemList.EssentiaUpgradeElectric.get(1)), 9);
    }

    public LargeEssentiaGenerator(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public LargeEssentiaGenerator(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeEssentiaGenerator(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return 1536;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mEssentiaHatch.clear();
        tierMachine = -1;
        mStableValue = 0;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(mName, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch();
    }

    @Override
    public void setupParameters() {
        for (TileEntityEssentiaHatch hatch : mEssentiaHatch) {
            hatch.mState = mUpgrade;
        }
        switch (tierMachine) {
            case 0 -> {
                ++mStableValue;
                mTierLimit = Math.max(mTierLimit, 4);
            }
            case 1 -> {
                mStableValue += 2;
                mTierLimit = Math.max(mTierLimit, 6);
            }
            case 2 -> {
                mStableValue += 5;
                mTierLimit = Math.max(mTierLimit, 8);
            }
            case 3 -> {
                mStableValue += 10;
                mTierLimit = Math.max(mTierLimit, 10);
            }
        }
    }

    @Override
    public boolean checkHatch() {
        setupParameters();
        if (mDynamoHatches.size() + mExoticDynamoHatches.size() != 1) return false;
        for (MTEHatchInput tHatch : mInputHatches) {
            if (tHatch.mTier > mTierLimit) return false;
        }
        for (MTEHatchDynamo tHatch : mDynamoHatches) {
            if (tHatch.mTier > mTierLimit) return false;
        }
        for (MTEHatch tHatch : mExoticDynamoHatches) {
            if (tHatch.mTier > mTierLimit) return false;
            int maxAmp = 64 << (Integer.bitCount(mUpgrade) + Math.max(0, GTUtility.getTier(tHatch.maxEUOutput()) - 5));
            if (tHatch.maxAmperesOut() > maxAmp) return false;
        }
        return super.checkHatch();
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.mStableValue = aNBT.getInteger("mStableValue");
        this.mLeftEnergy = aNBT.getLong("mLeftEnergy");
        this.mUpgrade = aNBT.getInteger("mUpgrade");
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mStableValue", this.mStableValue);
        aNBT.setLong("mLeftEnergy", this.mLeftEnergy);
        aNBT.setInteger("mUpgrade", this.mUpgrade);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!getBaseMetaTileEntity().isServerSide()) return super.onRightclick(aBaseMetaTileEntity, aPlayer);
        var itemstack = aPlayer.inventory.getCurrentItem();
        if (itemstack == null) return super.onRightclick(aBaseMetaTileEntity, aPlayer);
        var tCurrentItem = GTUtility.ItemId.createWithoutNBT(itemstack);
        int upgrade = ESSENTIA_UPGRADE.getOrDefault(tCurrentItem, -1);
        if (upgrade != -1) {
            if ((mUpgrade & (1 << upgrade)) == 0 && upgrade != 0) {
                itemstack.stackSize--;
                mUpgrade = mUpgrade | (1 << upgrade);
                GTUtility.sendChatToPlayer(
                    aPlayer,
                    itemstack.getDisplayName() + StatCollector.translateToLocal("Info_LargeEssentiaGenerator_00"));
            }
            setupParameters();
            return true;
        }
        return super.onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    @Override
    public IStructureDefinition<LargeEssentiaGenerator> getStructureDefinition() {
        return StructureDefinition.<LargeEssentiaGenerator>builder()
            .addShape(
                mName,
                StructureUtility.transpose(
                    new String[][] {
                        { "A       A", "         ", "         ", "         ", "    ~    ", "         ", "         ",
                            "         ", "A       A" },
                        { "T   C   T", "   CEC   ", "  CEEEC  ", " CEEEEEC ", "CEEEEEEEC", " CEEEEEC ", "  CEEEC  ",
                            "   CEC   ", "T   C   T" },
                        { "T  TXT  T", "  TCXCT  ", " TCCXCCT ", "TCCCXCCCT", "XXXXXXXXX", "TCCCXCCCT", " TCCXCCT ",
                            "  TCXCT  ", "T  TXT  T" } }))
            .addElement('A', StructureUtility.ofBlock(ConfigBlocks.blockCosmeticOpaque, 1))
            .addElement('T', StructureUtility.ofBlock(ConfigBlocks.blockCosmeticSolid, 7))
            .addElement('C', StructureUtility.ofBlock(Loaders.magicCasing, 0))
            .addElement(
                'E',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        LargeEssentiaGenerator::getTierCasing,
                        ImmutableList.of(
                            Pair.of(Loaders.essentiaCell, 0),
                            Pair.of(Loaders.essentiaCell, 1),
                            Pair.of(Loaders.essentiaCell, 2),
                            Pair.of(Loaders.essentiaCell, 3)),
                        -1,
                        (t, m) -> t.tierMachine = m,
                        t -> t.tierMachine)))
            .addElement(
                'X',
                StructureUtility.ofChain(
                    buildHatchAdder(LargeEssentiaGenerator.class)
                        .atLeast(
                            HatchElement.Dynamo.or(ExoticDynamo),
                            HatchElement.Maintenance,
                            HatchElement.InputHatch)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    StructureUtility.ofBlock(Loaders.magicCasing, 0),
                    StructureUtility.ofSpecificTileAdder(
                        LargeEssentiaGenerator::addEssentiaHatch,
                        TileEntityEssentiaHatch.class,
                        Loaders.magicCasing,
                        0)))
            .build();
    }

    @Nullable
    public static Integer getTierCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == Loaders.essentiaCell) return meta;
        return null;
    }

    public boolean addEssentiaHatch(TileEntityEssentiaHatch aTileEntity) {
        return this.mEssentiaHatch.add(aTileEntity);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        this.mEfficiency = 10000;
        this.mMaxProgresstime = 20;
        setEssentiaToEUVoltageAndAmp(getVoltageLimit(), getAmpLimit());
        return CheckRecipeResultRegistry.GENERATING;
    }

    public long getVoltageLimit() {
        long voltage = 0;
        for (MTEHatch tHatch : this.mExoticDynamoHatches) {
            voltage += tHatch.maxEUOutput();
        }
        for (MTEHatchDynamo tHatch : this.mDynamoHatches) {
            voltage += tHatch.maxEUOutput();
        }
        return voltage;
    }

    public long getAmpLimit() {
        long amp = 0;
        for (MTEHatch tHatch : this.mExoticDynamoHatches) {
            amp += tHatch.maxAmperesOut();
        }
        for (MTEHatchDynamo tHatch : this.mDynamoHatches) {
            amp += tHatch.maxAmperesOut();
        }
        return amp;
    }

    public long getPerAspectEnergy(Aspect aspect, int amount) {
        int type = LargeEssentiaEnergyData.getAspectTypeIndex(aspect);
        if (!isValidEssentia(aspect)) return 0;
        return switch (type) {
            case 0 -> normalEssentia(aspect, amount);
            case 1 -> airEssentia(aspect, amount);
            case 2 -> thermalEssentia(aspect, amount);
            case 3 -> unstableEssentia(aspect, amount);
            case 4 -> victusEssentia(aspect, amount);
            case 5 -> taintedEssentia(aspect, amount);
            case 6 -> mechanicEssentia(aspect, amount);
            case 7 -> spiritEssentia(aspect, amount);
            case 8 -> radiationEssentia(aspect, amount);
            case 9 -> electricEssentia(aspect, amount);
            default -> 0;
        };
    }

    public long normalEssentia(Aspect aspect, int amount) {
        return LargeEssentiaEnergyData.getAspectFuelValue(aspect);
    }

    public long airEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 0;
        long ceoInput = (long) (LargeEssentiaEnergyData.getAspectCeo(aspect) * 8L);
        if (depleteInput(Materials.LiquidAir.getFluid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 1.5D;
        } else if (depleteInput(Materials.Air.getGas(GTUtility.safeInt(ceoInput, 0)))) {
            ceoOutput = 1.0D;
        }
        return (long) (baseValue * ceoOutput);
    }

    public long thermalEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 0;
        long ceoInput = (long) LargeEssentiaEnergyData.getAspectCeo(aspect) * 2;
        if (depleteInput(Materials.SuperCoolant.getFluid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 9.0D;
        } else if (depleteInput(new FluidStack(GTPPFluids.Cryotheum, GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 5.0D;
        } else if (depleteInput(GTModHandler.getIC2Coolant(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 1.5D;
        } else if (depleteInput(Materials.Ice.getSolid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 1.2D;
        } else if (depleteInput(GTModHandler.getDistilledWater(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 1.0D;
        } else if (depleteInput(Materials.Water.getFluid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 0.5D;
        }

        return (long) (baseValue * ceoOutput);
    }

    public long unstableEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 0;
        long ceoInput = (long) LargeEssentiaEnergyData.getAspectCeo(aspect) * 4;
        if (depleteInput(WerkstoffLoader.Xenon.getFluidOrGas(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 4.0D;
        } else if (depleteInput(WerkstoffLoader.Krypton.getFluidOrGas(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 3.0D;
        } else if (depleteInput(Materials.Argon.getFluid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 2.5D;
        } else if (depleteInput(WerkstoffLoader.Neon.getFluidOrGas(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 2.2D;
        } else if (depleteInput(Materials.Helium.getFluid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 2.0D;
        } else if (depleteInput(Materials.Nitrogen.getFluid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 1.0D;
        }
        return (long) (baseValue * ceoOutput);
    }

    public long victusEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 1.0D;
        long ceoInput = (long) LargeEssentiaEnergyData.getAspectCeo(aspect) * 18;
        if (depleteInput(new FluidStack(XPJUICE, GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 2.0D;
        } else if (depleteInput(new FluidStack(LIFEESSENCE, GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 6.0D;
        }
        return (long) (baseValue * ceoOutput);
    }

    public long taintedEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 1.0D;
        long ceoInput = (long) LargeEssentiaEnergyData.getAspectCeo(aspect) * 3;
        int chance = 2000;
        if (depleteInput(new FluidStack(PURE, GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 60.0D;
            chance = 0;
        } else if (depleteInput(new FluidStack(DEATH, GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = Math.pow(25000D / baseValue, 4);
            chance = 4000;
        }

        if (chance > 0) {
            double expected = (double) amount * chance / 10000.0;
            int guaranteed = (int) expected;
            double remainder = expected - guaranteed;

            int totalTriggers = guaranteed;

            if (random.nextDouble() < remainder) {
                totalTriggers++;
            }

            var te = getBaseMetaTileEntity();
            var world = te.getWorld();
            int x = te.getXCoord();
            int y = te.getYCoord();
            int z = te.getZCoord();

            for (int i = 0; i < totalTriggers; i++) {
                int tX = x + random.nextInt(17) - 8;
                int tY = y + random.nextInt(17) - 8;
                int tZ = z + random.nextInt(17) - 8;

                if (world.isAirBlock(tX, tY, tZ)) {
                    world.setBlock(tX, tY, tZ, ConfigBlocks.blockFluxGas, random.nextInt(8), 3);
                }
            }
        }

        return (long) (baseValue * ceoOutput);
    }

    public long mechanicEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 0;
        long ceoInput = (long) LargeEssentiaEnergyData.getAspectCeo(aspect) * 20;
        if (depleteInput(Materials.Lubricant.getFluid(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 1.0D;
        }
        return (long) (baseValue * ceoOutput);
    }

    public long spiritEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 1.0D;
        long ceoInput = (long) LargeEssentiaEnergyData.getAspectCeo(aspect) * 2;
        if (depleteInput(new FluidStack(SPIRIT, GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 10D * (1 + mStableValue / 100D);
        } else if (depleteInput(new FluidStack(HOLLOW_TEARS, GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 15D * (1 + 100D / mStableValue);
        }
        return (long) (baseValue * ceoOutput);
    }

    public long radiationEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = 1.0D;
        long ceoInput = (long) LargeEssentiaEnergyData.getAspectCeo(aspect) * 6;
        if (depleteInput(Materials.Caesium.getMolten(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 2.0D;
        } else if (depleteInput(Materials.Uranium235.getMolten(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 3.0D;
        } else if (depleteInput(Materials.Naquadah.getMolten(GTUtility.safeInt(ceoInput * amount, 0)))) {
            ceoOutput = 4.0D;
        } else
            if (depleteInput(GGMaterial.atomicSeparationCatalyst.getMolten(GTUtility.safeInt(ceoInput * amount, 0)))) {
                ceoOutput = 16.0D;
            }
        return (long) (baseValue * ceoOutput);
    }

    public long electricEssentia(Aspect aspect, int amount) {
        long baseValue = LargeEssentiaEnergyData.getAspectFuelValue(aspect);
        double ceoOutput = Math.pow(3.0, GTUtility.getTier(getVoltageLimit()));
        return (long) (baseValue * ceoOutput);
    }

    public void setEssentiaToEUVoltageAndAmp(long voltageLimit, long ampLimit) {
        long eut = mLeftEnergy;
        long euVoltage = voltageLimit;
        long euAmp = 1;

        long maxEU;
        if (voltageLimit > 0 && ampLimit > Long.MAX_VALUE / voltageLimit) {
            maxEU = Long.MAX_VALUE;
        } else {
            maxEU = voltageLimit * ampLimit;
        }

        if (eut <= 0) {
            for (TileEntityEssentiaHatch hatch : this.mEssentiaHatch) {
                AspectList aspects = hatch.getAspects();
                Iterator<Aspect> iterator = aspects.aspects.keySet()
                    .iterator();

                while (iterator.hasNext()) {
                    Aspect aspect = iterator.next();
                    if (!isValidEssentia(aspect)) continue;

                    int amount = aspects.getAmount(aspect);
                    if (amount <= 0) {
                        iterator.remove();
                        continue;
                    }

                    long perEU = getPerAspectEnergy(aspect, amount) * mStableValue / 25;
                    if (perEU <= 0) continue;

                    long needEU = maxEU - eut;
                    if (needEU <= 0) break;

                    long canConsume = needEU / perEU;

                    if (canConsume <= 0) {
                        if (eut == 0) {
                            canConsume = 1;
                        } else continue;
                    }

                    if (canConsume > amount) canConsume = amount;
                    long add = canConsume * perEU;

                    if (Long.MAX_VALUE - eut < add) {
                        eut = Long.MAX_VALUE;
                    } else {
                        eut += add;
                    }

                    aspects.reduce(aspect, (int) canConsume);

                    if (aspects.getAmount(aspect) <= 0) {
                        iterator.remove();
                    }
                }
            }
        }

        if (eut <= voltageLimit * 20) {
            euVoltage = eut / 20;
            mLeftEnergy = 0;

        } else {
            euAmp = Math.min(ampLimit, eut / euVoltage / 20);
            if (euAmp < 1) euAmp = 1;
            mLeftEnergy = eut - (euVoltage * euAmp * 20);
        }

        this.lEUt = euVoltage * euAmp;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeEssentiaGeneratorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_06"))
            .beginStructureBlock(9, 3, 9, true)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_Casing"))
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_Casing"))
            .addDynamoHatch(StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_Casing"))
            .addOtherStructurePart(
                StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_EssentiaInputHatch"),
                StatCollector.translateToLocal("Tooltip_LargeEssentiaGenerator_Casing"),
                1)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.of(Textures.BlockIcons.MACHINE_CASING_DRAGONEGG), TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_DRAGONEGG_GLOW)
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.of(Textures.BlockIcons.MACHINE_CASING_DRAGONEGG) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    public boolean isValidEssentia(Aspect aspect) {
        int type = LargeEssentiaEnergyData.getAspectTypeIndex(aspect);
        return type != -1 && (mUpgrade & (1 << type)) != 0;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(mName, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            mName,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }
}
