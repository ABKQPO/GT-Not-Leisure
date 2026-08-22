package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes.ARCANE_RESEARCH;
import static com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes.ARCANE_VIS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
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
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import goodgenerator.loader.Loaders;
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
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.FakeThaumcraftPlayer;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileVisRelay;

@IMetaTileEntity.SkipGenerateDescription
public class SmallArcaneAssembler extends MultiMachineBase<SmallArcaneAssembler> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/small_infusion_matrix";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE_PATH);

    private static final int HORIZONTAL_OFFSET = 1;
    private static final int VERTICAL_OFFSET = 1;
    private static final int DEPTH_OFFSET = 0;
    private static final int CASING_TEXTURE_ID = 1536;
    private static final int RESEARCH_REFRESH_INTERVAL = 100;
    private static final long WAND_CHARGE_EUT = 32;
    private static final int WAND_CHARGE_INTERVAL = 128;
    private static final int WAND_CHARGE_ASPECT_COUNT = 2;
    private static final int WAND_CHARGE_MIN_VIS = 1;
    private static final int WAND_CHARGE_MAX_VIS = 4;
    private static final int CENTIVIS_PER_VIS = 100;
    private static final int CV_BOOST_REFERENCE = 30;
    private static final double MAX_CV_CHARGE_BONUS = 2.00;
    private static final int OVERFLOW_TRANSFER_DENOMINATOR = 60;
    private static final int BASE_OVERFLOW_TRANSFER_NUMERATOR = 15;
    private static final int VIS_EFFECT_RANGE = 8;
    private static final List<Aspect> PRIMAL_ASPECTS = Aspect.getPrimalAspects();

    private ArrayList<String> cachedResearch = new ArrayList<>();
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
                            HatchElement.Energy,
                            HatchElement.Maintenance)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility.onElementPass(
                        machine -> ++machine.mCountCasing,
                        StructureUtility.ofBlock(Loaders.magicCasing, 0))))
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
        NBTTagList list = new NBTTagList();
        for (String research : cachedResearch) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ResearchName", research);
            list.appendTag(tag);
        }
        nbt.setTag("Research", list);
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
        cachedResearch.clear();
        NBTTagList list = nbt.getTagList("Research", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            if (tag.hasKey("ResearchName")) {
                cachedResearch.add(tag.getString("ResearchName"));
            }
        }
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
    public void onPreTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        super.onPreTick(baseMetaTileEntity, tick);

        if (baseMetaTileEntity.isServerSide() && tick % RESEARCH_REFRESH_INTERVAL == 0) {
            refreshResearchCache();
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
        ArrayList<TileVisNode> nodes = new ArrayList<>();
        for (int x = -VIS_EFFECT_RANGE; x <= VIS_EFFECT_RANGE; x++) {
            for (int y = -VIS_EFFECT_RANGE; y <= VIS_EFFECT_RANGE; y++) {
                for (int z = -VIS_EFFECT_RANGE; z <= VIS_EFFECT_RANGE; z++) {
                    TileEntity tileEntity = baseMetaTileEntity.getWorld()
                        .getTileEntity(
                            baseMetaTileEntity.getXCoord() + x,
                            baseMetaTileEntity.getYCoord() + y,
                            baseMetaTileEntity.getZCoord() + z);
                    if (!isValidVisEffectNode(tileEntity)) continue;

                    TileVisNode node = (TileVisNode) tileEntity;
                    if (node.getDistanceFrom(
                        baseMetaTileEntity.getXCoord(),
                        baseMetaTileEntity.getYCoord(),
                        baseMetaTileEntity.getZCoord()) <= VIS_EFFECT_RANGE * VIS_EFFECT_RANGE) {
                        nodes.add(node);
                    }
                }
            }
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

    private void refreshResearchCache() {
        String ownerName = getBaseMetaTileEntity().getOwnerName();
        if (ownerName == null || ownerName.isEmpty()) return;

        ArrayList<String> list = ResearchManager.getResearchForPlayer(ownerName);
        if ((cachedResearch == null && list != null)
            || (list != null && !list.isEmpty() && cachedResearch.size() != list.size())) {
            cachedResearch = list;
        }
    }

    private boolean isResearchComplete(String research) {
        if (research.isEmpty()) return true;
        if (!research.startsWith("@") && ResearchCategories.getResearch(research) == null) return false;
        return cachedResearch.contains(research);
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
                if (!consumeVis(requiredVis, false)) {
                    return SimpleCheckRecipeResult.ofFailure("insufficient_vis");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                AspectList requiredVis = recipe.getMetadata(ARCANE_VIS);
                if (requiredVis == null) return CheckRecipeResultRegistry.NO_RECIPE;
                if (getArcaneWand() == null) {
                    return SimpleCheckRecipeResult.ofFailure("missing_arcane_wand");
                }
                if (!consumeVis(requiredVis, true)) {
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

    private boolean consumeVis(AspectList requiredVis, boolean consume) {
        ItemStack wand = getArcaneWand();
        if (wand == null) return false;

        String ownerName = getBaseMetaTileEntity().getOwnerName();
        if (ownerName == null || ownerName.isEmpty()) return false;

        UUID ownerUuid = getBaseMetaTileEntity().getOwnerUuid();
        FakeThaumcraftPlayer player = new FakeThaumcraftPlayer(
            getBaseMetaTileEntity().getWorld(),
            new GameProfile(ownerUuid, ownerName));
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tooltip = new MultiblockTooltipBuilder();
        tooltip.addMachineType(StatCollector.translateToLocal("SmallArcaneAssemblerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_04"))
            .beginStructureBlock(3, 3, 3, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_SmallArcaneAssembler_Casing"), 1)
            .toolTipFinisher();
        return tooltip;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SmallArcaneAssembler(mName);
    }
}
