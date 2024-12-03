package com.science.gtnl.mixin;

import bartworks.API.BorosilicateGlass;
import bartworks.common.tileentities.multis.mega.MTEMegaBlastFurnace;
import bartworks.common.tileentities.multis.mega.MegaMultiBlockBase;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.mixin.Accessor.MTEMegaBlastFurnaceAccessor;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.gtnewhorizon.structurelib.alignment.constructable.ChannelDataAccessor.withChannel;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;

@Mixin(value = MTEMegaBlastFurnace.class,remap = false)
public abstract class MTETieredMachineBlockMixin extends MegaMultiBlockBase<MTEMegaBlastFurnace> implements ISurvivalConstructable {

    protected MTETieredMachineBlockMixin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    /**
     * @author GT-NOT-Leisure
     * @reason 修改巨高炉结构
     */
    @Overwrite
    private static String[][] createShape() {
        String resourcePath = "assets/sciencenotleisure/multiblock/mega_blast_furnace.mb";
        List<String[]> structure = new ArrayList<>();

        try (InputStream is = MTEMegaBlastFurnace.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                structure.add(line.split(""));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load multiblock structure: " + resourcePath, e);
        }

        return structure.toArray(new String[0][0]);
    }

    @Override
    public IStructureDefinition<MTEMegaBlastFurnace> getStructureDefinition() {
        return StructureDefinition
            .<MTEMegaBlastFurnace>builder()
            .addShape("main", createShape())
            .addElement('=', StructureElementAirNoHint.getInstance())
            .addElement(
                't',
                buildHatchAdder(MTEMegaBlastFurnace.class)
                    .atLeast(
                        OutputHatch.withAdder(MTEMegaBlastFurnace::addOutputHatchToTopList)
                            .withCount(t -> {
                                if (t instanceof MTEMegaBlastFurnaceAccessor accessor) {
                                    return accessor.getPollutionOutputHatches().size();
                                }
                                return 0;
                            }))
                    .casingIndex(MTEMegaBlastFurnaceAccessor.getCasingIndex())
                    .dot(1)
                    .buildAndChain(GregTechAPI.sBlockCasings1, MTEMegaBlastFurnaceAccessor.getCasingIndex()))
            .addElement('m', Muffler.newAny(MTEMegaBlastFurnaceAccessor.getCasingIndex(), 2))
            .addElement(
                'C',
                withChannel("coil", ofCoil(MTEMegaBlastFurnace::setCoilLevel, MTEMegaBlastFurnace::getCoilLevel)))
            .addElement(
                'g',
                withChannel(
                    "glass",
                    BorosilicateGlass
                        .ofBoroGlass(
                            (byte) 0,
                            (byte) 1,
                            Byte.MAX_VALUE,
                            (te, t) -> {
                                if (te instanceof MTEMegaBlastFurnaceAccessor accessor) {
                                    accessor.setGlassTier(t);
                                }
                            },
                            te -> {
                                if (te instanceof MTEMegaBlastFurnaceAccessor accessor) {
                                    return accessor.getGlassTier();
                                }
                                return 0;
                            })))
            .addElement(
                'b',
                buildHatchAdder(MTEMegaBlastFurnace.class)
                    .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                    .casingIndex(MTEMegaBlastFurnaceAccessor.getCasingIndex())
                    .dot(1)
                    .buildAndChain(GregTechAPI.sBlockCasings1, MTEMegaBlastFurnaceAccessor.getCasingIndex()))
            .build();
    }

}
