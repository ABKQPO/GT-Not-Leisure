package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes.ARCANE_RESEARCH;
import static com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes.ARCANE_VIS;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticOpaque;
import static thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.mojang.authlib.GameProfile;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.thaumcraft.OwnerResearchCache;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.FakeThaumcraftPlayer;
import thaumcraft.common.tiles.TileVisRelay;

@IMetaTileEntity.SkipGenerateDescription
public class SmallArcaneAssembler extends MultiMachineBase<SmallArcaneAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/small_arcane_assembler";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE_PATH);

    private static final int HORIZONTAL_OFFSET = 2;
    private static final int VERTICAL_OFFSET = 3;
    private static final int DEPTH_OFFSET = 0;
    private static final int CASING_TEXTURE_ID = 1536;
    private static final int VIS_NODE_SCAN_INTERVAL = 200;
    private static final long WAND_CHARGE_EUT = 32;
    private static final int WAND_CHARGE_INTERVAL = 128;
    private static final int WAND_CHARGE_ASPECT_COUNT = 2;
    private static final int WAND_CHARGE_MIN_VIS = 1;
    private static final int WAND_CHARGE_MAX_VIS = 4;
    private static final int CENTIVIS_PER_VIS = 100;
    private static final int MAX_SCALED_VIS = Integer.MAX_VALUE / CENTIVIS_PER_VIS;
    private static final int CV_BOOST_REFERENCE = 30;
    private static final double MAX_CV_CHARGE_BONUS = 2.00;
    private static final int OVERFLOW_TRANSFER_DENOMINATOR = 60;
    private static final int BASE_OVERFLOW_TRANSFER_NUMERATOR = 15;
    private static final int VIS_EFFECT_RANGE = 8;
    private static final List<Aspect> PRIMAL_ASPECTS = Aspect.getPrimalAspects();

    private final OwnerResearchCache researchCache = new OwnerResearchCache();
    private final ArrayList<ChunkCoordinates> visNodeCoordinates = new ArrayList<>();
    private long nextVisNodeScanTick;
    private int chargeCycleTicksRemaining;
    private final int[] cycleChargeCV = new int[6];
    private final int[] overflowTransferNumerators = new int[6];
    private final int[] overflowTransferRemainders = new int[6];
    private Aspect visEffectAspect;
    private boolean visConnectionActive;
    private final ArrayList<ChunkCoordinates> clientVisEffectNodes = new ArrayList<>();
    private final Map<ChunkCoordinates, Object> visConnectionBeams = new HashMap<>();
    private long nextVisEffectNodeSearchTick;

    public SmallArcaneAssembler(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public SmallArcaneAssembler(String name) {
        super(name);
    }

    @Override
    public IStructureDefinition<SmallArcaneAssembler> getStructureDefinition() {
        return StructureDefinition.<SmallArcaneAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(SHAPE))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(SmallArcaneAssembler.class)
                        .atLeast(
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.Energy.or(HatchElement.MultiAmpEnergy),
                            HatchElement.Maintenance)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility.onElementPass(machine -> ++machine.mCountCasing, Casings.MagicCasing.asElement())))
            .addElement('B', StructureUtility.ofBlock(blockCosmeticSolid, 7))
            .addElement('C', Casings.AlchemicalConstructTiered.asElement())
            .addElement('D', StructureUtility.ofBlock(blockCosmeticOpaque, 2))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;

        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 4);
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        researchCache.writeToNBT(nbt);
        nbt.setInteger("WandChargeTicks", chargeCycleTicksRemaining);
        nbt.setIntArray("WandCycleCharge", cycleChargeCV);
        nbt.setIntArray("WandOverflowNumerators", overflowTransferNumerators);
        nbt.setIntArray("WandOverflowRemainders", overflowTransferRemainders);
        nbt.setBoolean("VisConnectionActive", visConnectionActive);
        if (visEffectAspect != null) nbt.setString("VisConnectionAspect", visEffectAspect.getTag());
        super.saveNBTData(nbt);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        researchCache.readFromNBT(nbt);
        chargeCycleTicksRemaining = Math.min(WAND_CHARGE_INTERVAL, Math.max(0, nbt.getInteger("WandChargeTicks")));
        loadIntArray(nbt.getIntArray("WandCycleCharge"), cycleChargeCV);
        loadIntArray(nbt.getIntArray("WandOverflowNumerators"), overflowTransferNumerators);
        loadIntArray(nbt.getIntArray("WandOverflowRemainders"), overflowTransferRemainders);
        visConnectionActive = nbt.getBoolean("VisConnectionActive");
        visEffectAspect = nbt.hasKey("VisConnectionAspect") ? Aspect.getAspect(nbt.getString("VisConnectionAspect"))
            : null;
        if (visEffectAspect == null) visConnectionActive = false;
        super.loadNBTData(nbt);
    }

    private void loadIntArray(int[] source, int[] target) {
        for (int i = 0; i < target.length; i++) {
            target[i] = i < source.length ? source[i] : 0;
        }
    }

    @Override
    public void onFirstTick(IGregTechTileEntity baseMetaTileEntity) {
        super.onFirstTick(baseMetaTileEntity);
        if (baseMetaTileEntity.isServerSide() && visConnectionActive) {
            baseMetaTileEntity.sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        super.onPostTick(baseMetaTileEntity, tick);

        if (baseMetaTileEntity.isClientSide()) {
            updateClientVisConnectionBeam(baseMetaTileEntity, tick);
            return;
        }

        if (mMachine && baseMetaTileEntity.isAllowedToWork()) {
            chargeArcaneWand(baseMetaTileEntity);
        } else if (visConnectionActive) {
            stopVisConnectionEffect(baseMetaTileEntity);
        }
    }

    private void chargeArcaneWand(IGregTechTileEntity baseMetaTileEntity) {
        ItemStack wandStack = getArcaneWand();
        if (wandStack == null) {
            if (visConnectionActive) stopVisConnectionEffect(baseMetaTileEntity);
            return;
        }

        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        int maxVis = wand.getMaxVis(wandStack);
        boolean needsCharge = false;
        for (Aspect aspect : PRIMAL_ASPECTS) {
            if (wand.getVis(wandStack, aspect) < maxVis) {
                needsCharge = true;
                break;
            }
        }
        if (!needsCharge || !drainEnergyInput(WAND_CHARGE_EUT)) return;
        if (chargeCycleTicksRemaining <= 0) beginChargeCycle(baseMetaTileEntity);
        if (chargeCycleTicksRemaining <= 0) return;

        boolean charged = transferPendingCharge(baseMetaTileEntity, wand, wandStack, maxVis);
        chargeCycleTicksRemaining--;
        if (chargeCycleTicksRemaining <= 0) clearChargeCycle();
        if (charged) baseMetaTileEntity.markDirty();
    }

    private void beginChargeCycle(IGregTechTileEntity baseMetaTileEntity) {
        clearChargeCycle();
        ArrayList<Aspect> selectedAspects = new ArrayList<>(PRIMAL_ASPECTS);
        ArrayList<TileVisNode> visNodes = getNearbyVisNodes(baseMetaTileEntity);
        boolean connectionEffectStarted = false;
        int aspectCount = Math.min(WAND_CHARGE_ASPECT_COUNT, selectedAspects.size());
        for (int i = 0; i < aspectCount; i++) {
            Aspect aspect = selectedAspects.remove(baseMetaTileEntity.getWorld().rand.nextInt(selectedAspects.size()));
            int drainedCV = drainVisWithoutParticles(visNodes, aspect, CV_BOOST_REFERENCE);
            if (drainedCV > 0 && !connectionEffectStarted) {
                connectionEffectStarted = beginVisConnectionEffect(baseMetaTileEntity, aspect);
            }
            double cvPercentage = Math.min(1.0, (double) drainedCV / CV_BOOST_REFERENCE);
            int baseVis = WAND_CHARGE_MIN_VIS
                + baseMetaTileEntity.getWorld().rand.nextInt(WAND_CHARGE_MAX_VIS - WAND_CHARGE_MIN_VIS + 1);
            int aspectIndex = PRIMAL_ASPECTS.indexOf(aspect);
            cycleChargeCV[aspectIndex] = (int) Math
                .round(baseVis * CENTIVIS_PER_VIS * (1.0 + MAX_CV_CHARGE_BONUS * cvPercentage));
            overflowTransferNumerators[aspectIndex] = BASE_OVERFLOW_TRANSFER_NUMERATOR + drainedCV;
        }

        chargeCycleTicksRemaining = WAND_CHARGE_INTERVAL;
    }

    private ArrayList<TileVisNode> getNearbyVisNodes(IGregTechTileEntity baseMetaTileEntity) {
        long worldTime = baseMetaTileEntity.getWorld()
            .getTotalWorldTime();
        if (worldTime >= nextVisNodeScanTick) {
            scanVisNodeCoordinates(baseMetaTileEntity);
            nextVisNodeScanTick = worldTime + VIS_NODE_SCAN_INTERVAL;
        }

        ArrayList<TileVisNode> nodes = new ArrayList<>();
        for (ChunkCoordinates coordinates : visNodeCoordinates) {
            TileEntity tileEntity = baseMetaTileEntity.getWorld()
                .getTileEntity(coordinates.posX, coordinates.posY, coordinates.posZ);
            if (isValidVisEffectNode(tileEntity)) nodes.add((TileVisNode) tileEntity);
        }

        nodes.sort((first, second) -> {
            boolean firstIsRelay = first instanceof TileVisRelay;
            boolean secondIsRelay = second instanceof TileVisRelay;
            if (firstIsRelay != secondIsRelay) return firstIsRelay ? -1 : 1;
            return Double.compare(
                first.getDistanceFrom(
                    baseMetaTileEntity.getXCoord(),
                    baseMetaTileEntity.getYCoord(),
                    baseMetaTileEntity.getZCoord()),
                second.getDistanceFrom(
                    baseMetaTileEntity.getXCoord(),
                    baseMetaTileEntity.getYCoord(),
                    baseMetaTileEntity.getZCoord()));
        });
        return nodes;
    }

    private void scanVisNodeCoordinates(IGregTechTileEntity baseMetaTileEntity) {
        visNodeCoordinates.clear();
        int centerX = baseMetaTileEntity.getXCoord();
        int centerY = baseMetaTileEntity.getYCoord();
        int centerZ = baseMetaTileEntity.getZCoord();
        for (int x = -VIS_EFFECT_RANGE; x <= VIS_EFFECT_RANGE; x++) {
            for (int y = -VIS_EFFECT_RANGE; y <= VIS_EFFECT_RANGE; y++) {
                for (int z = -VIS_EFFECT_RANGE; z <= VIS_EFFECT_RANGE; z++) {
                    TileEntity tileEntity = baseMetaTileEntity.getWorld()
                        .getTileEntity(centerX + x, centerY + y, centerZ + z);
                    if (tileEntity instanceof TileVisNode node
                        && node.getDistanceFrom(centerX, centerY, centerZ) <= VIS_EFFECT_RANGE * VIS_EFFECT_RANGE) {
                        visNodeCoordinates.add(new ChunkCoordinates(node.xCoord, node.yCoord, node.zCoord));
                    }
                }
            }
        }
    }

    private int drainVisWithoutParticles(List<TileVisNode> nodes, Aspect aspect, int amount) {
        int drained = 0;
        for (TileVisNode node : nodes) {
            drained += node.consumeVis(aspect, amount - drained);
            if (drained >= amount) break;
        }
        return drained;
    }

    private boolean transferPendingCharge(IGregTechTileEntity baseMetaTileEntity, ItemWandCasting wand,
        ItemStack wandStack, int maxVis) {
        int overflowPool = 0;
        boolean charged = false;
        int elapsedTicks = WAND_CHARGE_INTERVAL - chargeCycleTicksRemaining + 1;
        for (int i = 0; i < cycleChargeCV.length; i++) {
            if (cycleChargeCV[i] <= 0) continue;

            int amount = (int) ((long) cycleChargeCV[i] * elapsedTicks / WAND_CHARGE_INTERVAL
                - (long) cycleChargeCV[i] * (elapsedTicks - 1) / WAND_CHARGE_INTERVAL);
            if (amount <= 0) continue;
            Aspect aspect = PRIMAL_ASPECTS.get(i);
            int acceptedCV = Math.min(amount, maxVis - wand.getVis(wandStack, aspect));
            if (acceptedCV > 0) {
                wand.addRealVis(wandStack, aspect, acceptedCV, true);
                charged = true;
            }

            int overflowCV = amount - acceptedCV;
            int scaledOverflow = overflowCV * overflowTransferNumerators[i] + overflowTransferRemainders[i];
            overflowPool += scaledOverflow / OVERFLOW_TRANSFER_DENOMINATOR;
            overflowTransferRemainders[i] = scaledOverflow % OVERFLOW_TRANSFER_DENOMINATOR;
        }

        return distributeOverflowVis(baseMetaTileEntity, wand, wandStack, maxVis, overflowPool) || charged;
    }

    private void clearChargeCycle() {
        chargeCycleTicksRemaining = 0;
        for (int i = 0; i < cycleChargeCV.length; i++) {
            cycleChargeCV[i] = 0;
            overflowTransferNumerators[i] = 0;
            overflowTransferRemainders[i] = 0;
        }
    }

    private boolean beginVisConnectionEffect(IGregTechTileEntity baseMetaTileEntity, Aspect aspect) {
        int color = PRIMAL_ASPECTS.indexOf(aspect);
        if (color < 0) return false;

        visEffectAspect = aspect;
        visConnectionActive = true;
        baseMetaTileEntity.sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
        return true;
    }

    private void stopVisConnectionEffect(IGregTechTileEntity baseMetaTileEntity) {
        visConnectionActive = false;
        visEffectAspect = null;
        baseMetaTileEntity.sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, (byte) 0);
    }

    @Override
    public void onValueUpdate(byte value) {
        int color = Byte.toUnsignedInt(value) - 1;
        if (color >= 0 && color < PRIMAL_ASPECTS.size()) {
            boolean wasActive = visConnectionActive;
            visEffectAspect = PRIMAL_ASPECTS.get(color);
            visConnectionActive = true;
            if (!wasActive) {
                clientVisEffectNodes.clear();
                visConnectionBeams.clear();
                nextVisEffectNodeSearchTick = 0;
                nextVisNodeScanTick = 0;
            }
        } else {
            visConnectionActive = false;
            visEffectAspect = null;
            clientVisEffectNodes.clear();
            visConnectionBeams.clear();
        }
    }

    @Override
    public byte getUpdateData() {
        if (!visConnectionActive || visEffectAspect == null) return 0;
        return (byte) (PRIMAL_ASPECTS.indexOf(visEffectAspect) + 1);
    }

    private void updateClientVisConnectionBeam(IGregTechTileEntity baseMetaTileEntity, long tick) {
        if (!visConnectionActive || visEffectAspect == null) {
            clientVisEffectNodes.clear();
            visConnectionBeams.clear();
            return;
        }

        if (tick >= nextVisEffectNodeSearchTick) {
            refreshClientVisEffectNodes(baseMetaTileEntity);
            nextVisEffectNodeSearchTick = tick + 20;
        }

        int color = visEffectAspect.getColor();
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        for (ChunkCoordinates coordinates : clientVisEffectNodes) {
            TileEntity tileEntity = baseMetaTileEntity.getWorld()
                .getTileEntity(coordinates.posX, coordinates.posY, coordinates.posZ);
            if (!(tileEntity instanceof TileVisNode node) || node.isInvalid()) continue;

            Object beam = Thaumcraft.proxy.beamPower(
                baseMetaTileEntity.getWorld(),
                node.xCoord + 0.5,
                node.yCoord + 0.5,
                node.zCoord + 0.5,
                baseMetaTileEntity.getXCoord() + 0.5,
                baseMetaTileEntity.getYCoord() + 0.5,
                baseMetaTileEntity.getZCoord() + 0.5,
                red,
                green,
                blue,
                true,
                visConnectionBeams.get(coordinates));
            visConnectionBeams.put(coordinates, beam);
        }
    }

    private void refreshClientVisEffectNodes(IGregTechTileEntity baseMetaTileEntity) {
        ArrayList<ChunkCoordinates> updatedNodes = new ArrayList<>();
        for (TileVisNode node : getNearbyVisNodes(baseMetaTileEntity)) {
            updatedNodes.add(new ChunkCoordinates(node.xCoord, node.yCoord, node.zCoord));
        }
        clientVisEffectNodes.clear();
        clientVisEffectNodes.addAll(updatedNodes);
        visConnectionBeams.keySet()
            .retainAll(updatedNodes);
    }

    private boolean isValidVisEffectNode(TileEntity tileEntity) {
        return tileEntity instanceof TileVisNode node
            && (node.isSource() || VisNetHandler.isNodeValid(node.getParent()));
    }

    private boolean distributeOverflowVis(IGregTechTileEntity baseMetaTileEntity, ItemWandCasting wand,
        ItemStack wandStack, int maxVis, int overflowPool) {
        if (overflowPool <= 0) return false;

        ArrayList<Aspect> targets = new ArrayList<>();
        ArrayList<Integer> rooms = new ArrayList<>();
        int totalRoom = 0;
        for (Aspect aspect : PRIMAL_ASPECTS) {
            int room = maxVis - wand.getVis(wandStack, aspect);
            if (room <= 0) continue;

            targets.add(aspect);
            rooms.add(room);
            totalRoom += room;
        }
        if (targets.isEmpty()) return false;

        int distributable = Math.min(overflowPool, totalRoom);
        int[] shares = new int[targets.size()];
        int distributed = 0;
        for (int i = 0; i < targets.size(); i++) {
            shares[i] = (int) ((long) distributable * rooms.get(i) / totalRoom);
            distributed += shares[i];
        }

        int remainder = distributable - distributed;
        int start = baseMetaTileEntity.getWorld().rand.nextInt(targets.size());
        for (int i = 0; i < remainder; i++) {
            shares[(start + i) % targets.size()]++;
        }

        for (int i = 0; i < targets.size(); i++) {
            if (shares[i] > 0) wand.addRealVis(wandStack, targets.get(i), shares[i], true);
        }
        return distributable > 0;
    }

    private boolean isResearchComplete(String research) {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        String ownerName = baseMetaTileEntity == null ? null : baseMetaTileEntity.getOwnerName();
        return researchCache.isComplete(ownerName, research);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment environment) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stack,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            environment,
            false,
            true);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.IndustrialShapedArcaneCraftingRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                CheckRecipeResult baseResult = super.validateRecipe(recipe);
                if (!baseResult.wasSuccessful()) return baseResult;

                String research = recipe.getMetadata(ARCANE_RESEARCH);
                AspectList requiredVis = recipe.getMetadata(ARCANE_VIS);
                if (research == null || requiredVis == null) return CheckRecipeResultRegistry.NO_RECIPE;

                if (!isResearchComplete(research)) {
                    return SimpleCheckRecipeResult.ofFailure("missing_arcane_research");
                }
                if (getArcaneWand() == null) {
                    return SimpleCheckRecipeResult.ofFailure("missing_arcane_wand");
                }
                if (getAffordableCrafts(requiredVis, 1) <= 0) {
                    return SimpleCheckRecipeResult.ofFailure("insufficient_vis");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                int limit = (int) Math.min(Integer.MAX_VALUE, (long) getTrueParallel() * getMaxBatchSize());
                int affordable = getAffordableCrafts(recipe.getMetadata(ARCANE_VIS), limit);
                return super.createParallelHelper(recipe).setMaxTotalParallel(affordable);
            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                AspectList requiredVis = recipe.getMetadata(ARCANE_VIS);
                if (requiredVis == null) return CheckRecipeResultRegistry.NO_RECIPE;
                if (getArcaneWand() == null) {
                    return SimpleCheckRecipeResult.ofFailure("missing_arcane_wand");
                }
                FakeThaumcraftPlayer player = createOwnerPlayer();
                if (player == null || !consumeVis(scaleAspects(requiredVis, calculatedParallels), player, true)) {
                    return SimpleCheckRecipeResult.ofFailure("insufficient_vis");
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setHeatOC(getHeatOC())
                    .setMachineHeat(getMachineHeat())
                    .setHeatDiscount(getHeatDiscount())
                    .setAmperageOC(getAmperageOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    private ItemStack getArcaneWand() {
        ItemStack wand = getControllerSlot();
        return wand != null && wand.getItem() instanceof ItemWandCasting ? wand : null;
    }

    private int getAffordableCrafts(AspectList requiredVis, int limit) {
        if (requiredVis == null || limit <= 0) return 0;

        FakeThaumcraftPlayer player = createOwnerPlayer();
        if (player == null || !consumeVis(requiredVis, player, false)) return 0;
        if (consumeVis(scaleAspects(requiredVis, limit), player, false)) return limit;

        int affordable = 1;
        int unaffordable = limit;
        while (unaffordable - affordable > 1) {
            int middle = affordable + (unaffordable - affordable) / 2;
            if (consumeVis(scaleAspects(requiredVis, middle), player, false)) {
                affordable = middle;
            } else {
                unaffordable = middle;
            }
        }
        return affordable;
    }

    private static AspectList scaleAspects(AspectList aspects, int multiplier) {
        AspectList scaled = new AspectList();
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null) continue;
            int amount = aspects.getAmount(aspect);
            if (amount > 0) scaled.add(aspect, (int) Math.min(MAX_SCALED_VIS, (long) amount * multiplier));
        }
        return scaled;
    }

    private FakeThaumcraftPlayer createOwnerPlayer() {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        String ownerName = baseMetaTileEntity.getOwnerName();
        if (ownerName == null || ownerName.isEmpty()) return null;

        UUID ownerUuid = baseMetaTileEntity.getOwnerUuid();
        return new FakeThaumcraftPlayer(baseMetaTileEntity.getWorld(), new GameProfile(ownerUuid, ownerName));
    }

    private boolean consumeVis(AspectList requiredVis, FakeThaumcraftPlayer player, boolean consume) {
        ItemStack wand = getArcaneWand();
        if (wand == null || requiredVis.size() == 0) return false;

        boolean successful = ((ItemWandCasting) wand.getItem())
            .consumeAllVisCrafting(wand, player, requiredVis, consume);
        if (successful && consume) getBaseMetaTileEntity().markDirty();
        return successful;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public int getCasingTextureID() {
        return CASING_TEXTURE_ID;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstoneLevel) {
        if (side == facing) {
            if (active) {
                return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            }

            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }

        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SmallArcaneAssembler(mName);
    }
}
