package com.science.gtnl.common.machine.multiblock.AprilFool;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.SteamMultiMachineBase;
import com.science.gtnl.loader.RecipeRegister;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings1;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class SteamInfernalCokeOven extends SteamMultiMachineBase<SteamInfernalCokeOven>
    implements ISurvivalConstructable {

    public static final String TEXTURE_OVERLAY_STEAM_COKE_OVEN = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_STEAM_COKE_OVEN";
    public static final String TEXTURE_OVERLAY_STEAM_COKE_OVEN_ACTIVE = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_STEAM_COKE_OVEN_ACTIVE";
    public static final String TEXTURE_OVERLAY_STEAM_COKE_OVEN_ACTIVE_GLOW = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_STEAM_COKE_OVEN_ACTIVE_GLOW";
    public static Textures.BlockIcons.CustomIcon OVERLAY_STEAM_COKE_OVEN = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_STEAM_COKE_OVEN);
    public static Textures.BlockIcons.CustomIcon OVERLAY_STEAM_COKE_OVEN_ACTIVE = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_STEAM_COKE_OVEN_ACTIVE);
    public static Textures.BlockIcons.CustomIcon OVERLAY_STEAM_COKE_OVEN_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_STEAM_COKE_OVEN_ACTIVE_GLOW);
    private static IStructureDefinition<SteamInfernalCokeOven> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SICO = RESOURCE_ROOT_ID + ":" + "multiblock/steam_infernal_coke_oven";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SICO);
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private float speedup = 1;
    private int runningTickCounter = 0;

    public SteamInfernalCokeOven(String aName) {
        super(aName);
    }

    public SteamInfernalCokeOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new SteamInfernalCokeOven(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamInfernalCokeOvenRecipeType");
    }

    @Override
    public IStructureDefinition<SteamInfernalCokeOven> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<SteamInfernalCokeOven>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    ofChain(
                        buildHatchAdder(SteamInfernalCokeOven.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                InputBus,
                                SteamHatchElement.OutputBus_Steam,
                                OutputBus,
                                OutputHatch)
                            .casingIndex(((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(10))
                            .dot(1)
                            .buildAndChain(),
                        ofBlock(GregTechAPI.sBlockCasings1, 10)))
                .addElement('B', ofBlock(ModBlocks.blockCasingsMisc, 2))
                .addElement('C', ofBlock(Blocks.nether_brick, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public int getCasingTextureID() {
        return ((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(10);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        ITexture[] rTexture;
        if (side == aFacing) {
            if (aActive) {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_STEAM_COKE_OVEN_ACTIVE)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_STEAM_COKE_OVEN_ACTIVE_GLOW)
                        .extFacing()
                        .glow()
                        .build() };
            } else {
                rTexture = new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(OVERLAY_STEAM_COKE_OVEN)
                        .extFacing()
                        .build() };
            }
        } else {
            rTexture = new ITexture[] { Textures.BlockIcons
                .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)) };
        }
        return rTexture;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;
        return true;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        runningTickCounter++;
        if (runningTickCounter % 100 == 0 && speedup < 7) {
            runningTickCounter = 0;
            speedup += 0.1F;
        }
        return super.onRunningTick(aStack);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                setSpeedBonus(1F / speedup);
                return super.validateRecipe(recipe);
            }

            @Override
            @Nonnull
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setMaxOverclocks(Math.min(4, recipeOcCount))
                    .setEUtDiscount(1)
                    .setDurationModifier(1);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public int getMaxParallelRecipes() {
        // Max call to prevent seeing -16 parallels in waila for unformed multi
        return 16;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.InfernalCockRecipes;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamInfernalCokeOven_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamInfernalCokeOven_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamInfernalCokeOven_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 5, 5, true)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setFloat("steam", speedup);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        speedup = aNBT.getFloat("steam");
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_MACERATOR_OP;
    }

}
