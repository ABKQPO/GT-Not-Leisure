package com.science.gtnl.mixins.late.Gregtech;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ChannelDataAccessor;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.INEIPreviewModifier;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.util.GTUtility;
import gregtech.common.blocks.BlockCasings8;
import gregtech.common.tileentities.machines.multi.MTEPCBFactory;

@SuppressWarnings("UnusedMixin")
@Mixin(value = MTEPCBFactory.class, remap = false)
public abstract class MTEPCBFactory_Mixin extends MTEExtendedPowerMultiBlockBase<MTEPCBFactory>
    implements INEIPreviewModifier {

    @Final
    @Shadow
    private static final String tier1 = "tier1";

    @Final
    @Shadow
    private static final String tier2 = "tier2";

    @Final
    @Shadow
    private static final String tier3 = "tier3";

    @Final
    @Shadow
    private static final String bioUpgrade = "bioUpgrade";

    @Final
    @Shadow
    private static final String ocTier1Upgrade = "ocTier1Upgrade";

    @Final
    @Shadow
    private static final String ocTier2Upgrade = "ocTier2Upgrade";

    @Shadow
    private int mSetTier;

    protected MTEPCBFactory_Mixin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IStructureDefinition<MTEPCBFactory> getStructureDefinition() {
        return StructureDefinition.<MTEPCBFactory>builder()
            .addShape(
                tier1,
                transpose(
                    new String[][] {
                        // spotless:off
                        {"       ","E     E","E     E","EEEEEEE","E     E","E     E","       "},
                        {"EEEEEEE","CAAAAAC","CAAAAAC","CCCCCCC","CCCCCCC","CCCCCCC","E     E"},
                        {"EAAAAAE","C-----C","C-----C","C-----C","C-----C","C-----C","ECCCCCE"},
                        {"EAAAAAE","C-----C","B-----B","B-----B","B-----B","C-----C","ECCCCCE"},
                        {"EAAAAAE","C-----C","B-FFF-B","B-FFF-B","B-FFF-B","C-----C","EPPPPPE"},
                        {"ECC~CCE","CDDDDDC","CDDDDDC","CDDDDDC","CDDDDDC","CDDDDDC","EPPPPPE"}
                        //spotless:on
                    }))
            .addShape(
                tier2,
                transpose(
                    new String[][] {
                        // spotless:off
                        {"    ","    ","    ","HGGH","HGGH","HGGH","HGGH","HGGH","    ","    ","    "},
                        {"    ","    ","HGGH","GGGG","GGGG","GGGG","GGGG","GGGG","HGGH","    ","    "},
                        {"    ","HGGH","GGGG","G  G","G  G","G  G","G  G","G  G","GGGG","HGGH","    "},
                        {"    ","HGGH","G  G","G  G","G  G","G  G","G  G","G  G","G  G","HGGH","    "},
                        {"HGGH","G  G","G  G","G  G","G  G","G  G","G  G","G  G","G  G","G  G","HGGH"},
                        {"HGGH","G  G","G  G","G  G","G  G","G  G","G  G","G  G","G  G","G  G","HGGH"},
                        {"HGGH","GGGG","GGGG","GGGG","GGGG","GGGG","GGGG","GGGG","GGGG","GGGG","HGGH"}
                        //spotless:on
                    }))
            .addShape(
                tier3,
                transpose(
                    new String[][] {
                        // spotless:off
                        {"       ","       ","       ","       ","   I   ","   I   ","       ","       ","       ","       "},
                        {"       ","       ","       ","   I   ","   I   ","   I   ","   I   ","       ","       ","       "},
                        {"       ","       ","  KKK  ","  KIK  ","  K K  ","  K K  ","   I   ","       ","       ","       "},
                        {"       ","       ","  KKK  ","  K K  ","  K K  ","  K K  ","   I   ","       ","       ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I ","  K K  ","   I   ","       ","       ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I ","  K K  ","   I   ","       ","       ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I ","  K K  ","  KIK  ","       ","       ","       "},
                        {"       ","  I I  "," I K I "," I   I "," I   I ","  K K  ","  KIK  ","       ","       ","       "},
                        {"       ","  I I  "," I K I "," I   I "," I   I ","  K K  ","  K K  ","  KKK  ","       ","       "},
                        {"       ","  I I  "," I K I "," I   I "," I   I ","  K K  ","  K K  ","  KKK  ","       ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","  III  "," I   I ","  K K  ","  K K  ","  K K  ","  K K  "," I   I ","  III  ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","       ","  KKK  ","  K K  ","  K K  "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","       ","  KKK  ","  K K  ","  K K  "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","       ","  KKK  ","  K K  ","  K K  "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I "," I   I "," I   I "," I   I ","  III  ","       "},
                        {"       ","  III  "," I   I "," I   I "," I   I "," I   I "," I   I "," I   I ","  III  ","       "},
                        {" II~II ","IIJJJII","IJJJJJI","IJJJJJI","IJJJJJI","IJJJJJI","IJJJJJI","IJJJJJI","IIJJJII"," IIIII "}
                        //spotless:on
                    }))
            .addShape(
                bioUpgrade,
                transpose(
                    new String[][] {
                        // spotless:off
                        {"            ","            ","   LLLLLL   ","            ","            "},
                        {"            ","            ","  L      L  ","            ","            "},
                        {"E   E  E   E"," LLL    LLL "," LLL    LLL "," LLL    LLL ","E   E  E   E"},
                        {"EAAAE  EAAAE","A   A  A   A","A   A  A   A","A   A  A   A","EAAAE  EAAAE"},
                        {"EAAAE  EAAAE","A   A  A   A","A   A  A   A","A   A  A   A","EAAAE  EAAAE"},
                        {"EAAAE  EAAAE","A   A  A   A","A   A  A   A","A   A  A   A","EAAAE  EAAAE"},
                        {"ELLLE  ELLLE","LLLLL  LLLLL","LLLLL  LLLLL","LLLLL  LLLLL","ELLLE  ELLLE"}
                        //spotless:on
                    }))
            .addShape(
                ocTier1Upgrade,
                transpose(
                    new String[][] {
                        // spotless:off
                        {"EKKKE","K   K","K   K","K   K","EKKKE"},
                        {"E   E"," KKK "," K K "," KKK ","E   E"},
                        {"E   E"," NNN "," N N "," NNN ","E   E"},
                        {"E   E"," KKK "," K K "," KKK ","E   E"},
                        {"E   E"," KKK "," K K "," KKK ","E   E"},
                        {"EOOOE","OKKKO","OK KO","OKKKO","EOOOE"},
                        {"E   E"," KKK "," K K "," KKK ","E   E"},
                        {"E   E"," KKK "," K K "," KKK ","E   E"},
                        {"ENNNE","NKKKN","NK KN","NKKKN","ENNNE"},
                        {"EGGGE","GGGGG","GGMGG","GGGGG","EGGGE"}
                        //spotless:on
                    }))
            .addShape(
                ocTier2Upgrade,
                transpose(
                    new String[][] {
                        // spotless:off
                        {"RGGGR","G   G","G   G","G   G","RGGGR"},
                        {"R   R"," GGG "," GTG "," GGG ","R   R"},
                        {"R   R"," NNN "," NTN "," NNN ","R   R"},
                        {"R   R"," QQQ "," QTQ "," QQQ ","R   R"},
                        {"R   R"," QQQ "," QTQ "," QQQ ","R   R"},
                        {"R   R"," QQQ "," QTQ "," QQQ ","R   R"},
                        {"R   R"," QQQ "," QTQ "," QQQ ","R   R"},
                        {"R   R"," QQQ "," QTQ "," QQQ ","R   R"},
                        {"RNNNR","NQQQN","NQTQN","NQQQN","RNNNR"},
                        {"RGGGR","GGGGG","GGSGG","GGGGG","RGGGR"}
                        //spotless:on
                    }))
            .addElement('A', chainAllGlasses())
            .addElement('B', ofBlock(GregTechAPI.sBlockCasings3, 10))
            .addElement(
                'C',
                withChannel(
                    "enableBioUpgrade",
                    ofBlocksTiered(
                        (block, meta) -> block == GregTechAPI.sBlockCasings8 && meta == 11 ? 1 : null,
                        ImmutableList.of(Pair.of(GregTechAPI.sBlockCasings8, 11)),
                        -1,
                        (t, m) -> {},
                        t -> -1)))
            .addElement(
                'D',
                withChannel(
                    "enableOCTier1",
                    ofBlocksTiered(
                        (block, meta) -> block == GregTechAPI.sBlockReinforced && meta == 2 ? 1 : null,
                        ImmutableList.of(Pair.of(GregTechAPI.sBlockReinforced, 2)),
                        -1,
                        (t, m) -> {},
                        t -> -1)))
            // .addElement('D', ofBlock(GregTechAPI.sBlockReinforced, 2))
            .addElement('E', ofFrame(Materials.DamascusSteel))
            .addElement('F', ofFrame(Materials.VibrantAlloy))
            .addElement(
                'G',
                withChannel(
                    "enableOCTier2",
                    ofBlocksTiered(
                        (block, meta) -> block == GregTechAPI.sBlockCasings8 && meta == 12 ? 1 : null,
                        ImmutableList.of(Pair.of(GregTechAPI.sBlockCasings8, 12)),
                        -1,
                        (t, m) -> {},
                        t -> -1)))
            // .addElement('G', ofBlock(GregTechAPI.sBlockCasings8, 12))
            .addElement('H', ofFrame(Materials.Duranium))
            .addElement('I', ofBlock(GregTechAPI.sBlockCasings8, 13))
            .addElement(
                'J',
                buildHatchAdder(MTEPCBFactory.class)
                    .atLeast(
                        InputHatch,
                        OutputBus,
                        InputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        GTNL$getSpecialHatchElement())
                    .dot(1)
                    .casingIndex(((BlockCasings8) GregTechAPI.sBlockCasings8).getTextureIndex(13))
                    .buildAndChain(GregTechAPI.sBlockCasings8, 13))
            .addElement('K', ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('L', ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement(
                'M',
                buildHatchAdder(MTEPCBFactory.class).hatchClass(MTEHatchInput.class)
                    .adder(MTEPCBFactory::addCoolantInputToMachineList)
                    .casingIndex(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings8, 12))
                    .dot(2)
                    .buildAndChain(GregTechAPI.sBlockCasings8, 12))
            .addElement('N', ofBlock(GregTechAPI.sBlockCasings2, 15))
            .addElement('O', ofBlock(GregTechAPI.sBlockCasings8, 4))
            .addElement(
                'P',
                buildHatchAdder(MTEPCBFactory.class)
                    .atLeast(
                        InputHatch,
                        OutputBus,
                        InputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        GTNL$getSpecialHatchElement())
                    .dot(1)
                    .casingIndex(((BlockCasings8) GregTechAPI.sBlockCasings8).getTextureIndex(11))
                    .buildAndChain(GregTechAPI.sBlockCasings8, 11))
            .addElement('Q', ofBlock(GregTechAPI.sBlockCasings8, 14))
            .addElement('R', ofFrame(Materials.Americium))
            .addElement(
                'S',
                buildHatchAdder(MTEPCBFactory.class).hatchClass(MTEHatchInput.class)
                    .adder(MTEPCBFactory::addCoolantInputToMachineList)
                    .casingIndex(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings8, 12))
                    .dot(2)
                    .buildAndChain(GregTechAPI.sBlockCasings8, 12))
            .addElement('T', ofBlock(GregTechAPI.sBlockCasings1, 15))
            .build();
    }

    @Override
    public void onPreviewConstruct(@NotNull ItemStack trigger) {
        mSetTier = trigger.stackSize;
        if (ChannelDataAccessor.hasSubChannel(trigger, "enableBioUpgrade")
            && ChannelDataAccessor.getChannelData(trigger, "enableBioUpgrade") > 0) {
            buildPiece(bioUpgrade, trigger, false, -5, 6, 0);
        }
        if (ChannelDataAccessor.hasSubChannel(trigger, "enableOCTier2")
            && ChannelDataAccessor.getChannelData(trigger, "enableOCTier2") > 0) {
            buildPiece(ocTier2Upgrade, trigger, false, 2, 9, -11);
        } else if (ChannelDataAccessor.hasSubChannel(trigger, "enableOCTier1")
            && ChannelDataAccessor.getChannelData(trigger, "enableOCTier1") > 0) {
                buildPiece(ocTier1Upgrade, trigger, false, 2, 9, -11);
            }
    }

    @SuppressWarnings("unchecked")
    @Unique
    private static IHatchElement<MTEPCBFactory> GTNL$getSpecialHatchElement() {
        try {
            Class<?> outer = Class.forName("gregtech.common.tileentities.machines.multi.MTEPCBFactory");
            for (Class<?> inner : outer.getDeclaredClasses()) {
                if (inner.getSimpleName()
                    .equals("SpecialHatchElement")) {
                    Class<Enum> enumClass = (Class<Enum>) inner;
                    return (IHatchElement<MTEPCBFactory>) Enum.valueOf(enumClass, "NaniteBus");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to reflect enum MTEPCBFactory.SpecialHatchElement." + "NaniteBus", e);
        }
        throw new IllegalStateException("SpecialHatchElement enum not found");
    }

}
