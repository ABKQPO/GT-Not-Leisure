package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtnhlanth.common.register.LanthItemList.SHIELDED_ACCELERATOR_CASING;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.dreammaster.gthandler.CustomItemList;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ChannelDataAccessor;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class LibraryOfRuina extends GTMMultiMachineBase<LibraryOfRuina> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LOR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/library_of_ruina";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LOR_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 34;
    private static final int VERTICAL_OFF_SET = 34;
    private static final int DEPTH_OFF_SET = 20;

    public LibraryOfRuina(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LibraryOfRuina(String aName) {
        super(aName);
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LibraryOfRuina(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return 1662;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.TheTwilightForestRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LibraryOfRuinaRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(69, 51, 73, true)
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_09"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_10"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_11"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_12"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_13"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_14"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_15"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_16"))
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LibraryOfRuina_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LibraryOfRuina> getStructureDefinition() {
        return StructureDefinition.<LibraryOfRuina>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                withChannel(
                    "enableRender",
                    ofBlocksTiered(
                        (block, meta) -> block == Loaders.gravityStabilizationCasing ? 1 : null,
                        ImmutableList.of(Pair.of(Loaders.gravityStabilizationCasing, 0)),
                        -1,
                        (t, m) -> {},
                        t -> -1)))
            .addElement('B', ofBlock(BlockLoader.metaCasing, 13))
            .addElement('C', ofBlock(sBlockCasingsSE, 1))
            .addElement(
                'D',
                buildHatchAdder(LibraryOfRuina.class)
                    .atLeast(
                        InputHatch,
                        OutputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlockAnyMeta(SHIELDED_ACCELERATOR_CASING))))
            .addElement('E', ofBlock(sBlockCasings10, 4))
            .addElement('F', ofBlock(sBlockCasings10, 11))
            .addElement('G', ofBlock(sBlockCasings9, 11))
            .addElement('H', ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement(
                'I',
                ofChain(
                    ofBlock(Blocks.water, 0),
                    ofBlockAnyMeta(
                        TwilightForest.isModLoaded() ? GameRegistry.findBlock(TwilightForest.ID, "tile.TFPortal")
                            : Blocks.end_portal)))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if ((ChannelDataAccessor.hasSubChannel(stackSize, "enableRender")
            && ChannelDataAccessor.getChannelData(stackSize, "enableRender") > 0) || stackSize.stackSize > 1) {
            buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
        }
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        if ((ChannelDataAccessor.hasSubChannel(stackSize, "enableRender")
            && ChannelDataAccessor.getChannelData(stackSize, "enableRender") > 0) || stackSize.stackSize > 1) {
            int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);

            return this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN,
                stackSize,
                HORIZONTAL_OFF_SET,
                VERTICAL_OFF_SET,
                DEPTH_OFF_SET,
                realBudget,
                env,
                false,
                true);
        }
        return 0;
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            replacePortalWithWater();
            return false;
        }
        replaceWaterWithPortal();
        setupParameters();
        return mCountCasing >= 850;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch()
            && GTUtility.areStacksEqual(getControllerSlot(), CustomItemList.TwilightCrystal.get(1));
    }

    public void replaceWaterWithPortal() {
        IGregTechTileEntity aBaseMetaTileEntity = this.getBaseMetaTileEntity();
        World world = aBaseMetaTileEntity.getWorld();
        int baseX = aBaseMetaTileEntity.getXCoord();
        int baseY = aBaseMetaTileEntity.getYCoord();
        int baseZ = aBaseMetaTileEntity.getZCoord();

        ForgeDirection frontFacing = aBaseMetaTileEntity.getFrontFacing();
        ForgeDirection backFacing = frontFacing.getOpposite();

        ForgeDirection perpDir = backFacing.getRotation(ForgeDirection.DOWN);
        int perpX = perpDir.offsetX;
        int perpZ = perpDir.offsetZ;

        Block targetBlock = Blocks.end_portal;
        if (TwilightForest.isModLoaded()) {
            targetBlock = GameRegistry.findBlock(TwilightForest.ID, "tile.TFPortal");
            if (targetBlock == null) targetBlock = Blocks.end_portal;
        }

        for (int step = 10; step >= 8; step--) {
            int mainX = backFacing.offsetX * step;
            int mainZ = backFacing.offsetZ * step;

            for (int offset = -1; offset <= 1; offset++) {
                int x = baseX + mainX + perpX * offset;
                int z = baseZ + mainZ + perpZ * offset;
                int y = baseY - 1;

                Block block = world.getBlock(x, y, z);
                if (block == Blocks.water || block == Blocks.flowing_water) {
                    world.setBlock(x, y, z, targetBlock, 0, 3);
                }
            }
        }
    }

    public void replacePortalWithWater() {
        IGregTechTileEntity aBaseMetaTileEntity = this.getBaseMetaTileEntity();
        World world = aBaseMetaTileEntity.getWorld();
        int baseX = aBaseMetaTileEntity.getXCoord();
        int baseY = aBaseMetaTileEntity.getYCoord();
        int baseZ = aBaseMetaTileEntity.getZCoord();

        ForgeDirection frontFacing = aBaseMetaTileEntity.getFrontFacing();
        ForgeDirection backFacing = frontFacing.getOpposite();

        ForgeDirection perpDir = backFacing.getRotation(ForgeDirection.DOWN);
        int perpX = perpDir.offsetX;
        int perpZ = perpDir.offsetZ;

        for (int step = 10; step >= 8; step--) {
            int mainX = backFacing.offsetX * step;
            int mainZ = backFacing.offsetZ * step;

            for (int offset = -1; offset <= 1; offset++) {
                int x = baseX + mainX + perpX * offset;
                int z = baseZ + mainZ + perpZ * offset;
                int y = baseY - 1;

                Block block = world.getBlock(x, y, z);
                if (block == Blocks.end_portal || (TwilightForest.isModLoaded()
                    && block == GameRegistry.findBlock(TwilightForest.ID, "tile.TFPortal"))) {
                    world.setBlock(x, y, z, Blocks.water, 0, 3);
                }
            }
        }
    }
}
