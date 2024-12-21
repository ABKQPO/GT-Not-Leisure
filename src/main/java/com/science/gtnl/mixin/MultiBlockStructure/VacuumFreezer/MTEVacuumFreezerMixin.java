package com.science.gtnl.mixin.MultiBlockStructure.VacuumFreezer;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.FilpStructure;
import com.science.gtnl.mixin.Accessor.MTECubicMultiBlockBaseAccessor;
import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTECubicMultiBlockBase;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEVacuumFreezer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;

@Mixin(value = MTEVacuumFreezer.class, remap = false)
public abstract class MTEVacuumFreezerMixin extends MTECubicMultiBlockBase<MTEVacuumFreezer> {

    private static final String DEBUG_PREFIX = "[DEBUG][VacuumFreezer] ";

    private static void debug(String message) {
        System.out.println(DEBUG_PREFIX + message);
    }

    private static IStructureDefinition<MTEVacuumFreezer> STRUCTURE_DEFINITION;

    private static IStructureElement<MTEVacuumFreezer> createHatchElement() {
        return ofChain(
            lazy(t -> {
                debug("构建 HatchAdder");
                MTECubicMultiBlockBaseAccessor<MTEVacuumFreezer> accessor = (MTECubicMultiBlockBaseAccessor<MTEVacuumFreezer>) t;
                var hatches = accessor.invokeGetAllowedHatches();  // 使用 invoke 方法
                var textureIndex = accessor.invokeGetHatchTextureIndex();  // 使用 invoke 方法
                debug("Hatches size: " + (hatches != null ? hatches.size() : "null"));
                debug("Texture Index: " + textureIndex);
                return GTStructureUtility.<MTEVacuumFreezer>buildHatchAdder()
                    .atLeastList((List<IHatchElement<? super MTEVacuumFreezer>>) hatches)
                    .casingIndex(textureIndex)
                    .dot(1)
                    .build();
            }),
            onElementPass(
                t -> ((MTECubicMultiBlockBaseAccessor<MTEVacuumFreezer>)t).invokeOnCorrectCasingAdded(),  // 使用 invoke 方法
                ofBlock(GregTechAPI.sBlockCasings2, 1))
        );
    }

    private static IStructureDefinition<MTEVacuumFreezer> createStructureDefinition() {
        debug("开始创建结构定义");
        try {
            String[][] structure = FilpStructure.loadAndTransposeStructure("sciencenotleisure:multiblock/vacuum_freezer");

            if (structure == null || structure.length == 0) {
                debug("加载结构失败，使用默认结构");
                return getDefaultStructureDefinition();
            }

            return StructureDefinition.<MTEVacuumFreezer>builder()
                .addShape(STRUCTURE_PIECE_MAIN, structure)
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 1))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings2, 1))
                .addElement('E', ofBlock(GregTechAPI.sBlockCasings2, 1))
                .addElement('F', ofBlock(GregTechAPI.sBlockCasings2, 1))
                .addElement('A', createHatchElement())
                .addElement('B', createHatchElement())
                .build();
        } catch (Exception e) {
            debug("创建结构定义时出错: " + e.getMessage());
            e.printStackTrace();
            return getDefaultStructureDefinition();
        }
    }

    private static IStructureDefinition<MTEVacuumFreezer> getDefaultStructureDefinition() {
        return StructureDefinition.<MTEVacuumFreezer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, new String[][] {
                {"~~~", "~~~", "~~~"},
                {"~~~", "~C~", "~~~"},
                {"~~~", "~~~", "~~~"}
            })
            .addElement('C', createHatchElement())
            .build();
    }

    protected MTEVacuumFreezerMixin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTEVacuumFreezerMixin(String aName) {
        super(aName);
    }

    @Override
    public IStructureDefinition<MTEVacuumFreezer> getStructureDefinition() {
        debug("调用 getStructureDefinition");
        if (STRUCTURE_DEFINITION == null) {
            debug("首次创建结构定义");
            STRUCTURE_DEFINITION = createStructureDefinition();
            debug("结构定义创建完成");
        } else {
            debug("使用缓存的结构定义");
        }
        return STRUCTURE_DEFINITION;
    }

    /**
     * @author GT-Not-Leisure
     * @reason 覆写真空冷冻机提示信息
     */
    @Overwrite
    protected MultiblockTooltipBuilder createTooltip() {
        System.out.println("[DEBUG][VacuumFreezer] 开始创建工具提示");
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();

        try {
            // 加载结构
            System.out.println("[DEBUG][VacuumFreezer] 尝试加载结构文件");
            String[][] structure = FilpStructure.loadAndTransposeStructure("sciencenotleisure:multiblock/vacuum_freezer");

            if (structure == null) {
                System.out.println("[DEBUG][VacuumFreezer] 错误：加载的结构为null");
                return getDefaultTooltip(tt);
            }

            if (structure.length == 0) {
                System.out.println("[DEBUG][VacuumFreezer] 错误：加载的结构是空数组");
                return getDefaultTooltip(tt);
            }

            System.out.println("[DEBUG][VacuumFreezer] 结构加载成功，开始分析结构数据");
            System.out.println("[DEBUG][VacuumFreezer] 结构层数: " + structure.length);

            // 打印每层的详细信息
            for (int y = 0; y < structure.length; y++) {
                if (structure[y] == null) {
                    System.out.println("[DEBUG][VacuumFreezer] 警告：第 " + (y + 1) + " 层为null");
                    continue;
                }
                System.out.println("[DEBUG][VacuumFreezer] 第 " + (y + 1) + " 层行数: " + structure[y].length);
                for (int x = 0; x < structure[y].length; x++) {
                    if (structure[y][x] == null) {
                        System.out.println("[DEBUG][VacuumFreezer] 警告：第 " + (y + 1) + " 层第 " + (x + 1) + " 行为null");
                    } else {
                        System.out.println("[DEBUG][VacuumFreezer] 第 " + (y + 1) + " 层第 " + (x + 1) + " 行内容: '" + structure[y][x] + "'");
                        System.out.println("[DEBUG][VacuumFreezer] 该行长度: " + structure[y][x].length());
                    }
                }
            }

            // 获取结构尺寸
            int height = structure.length;
            int width = structure[0].length;
            int length = structure[0][0].length();

            System.out.println("[DEBUG][VacuumFreezer] 结构最终尺寸 - 宽度: " + width + ", 高度: " + height + ", 长度: " + length);

            // 构建工具提示
            System.out.println("[DEBUG][VacuumFreezer] 开始构建工具提示");
            tt.addMachineType("Vacuum Freezer")
                .addInfo("Cools hot ingots and cells")
                .beginStructureBlock(width, height, length, true)
                .addController("Front center")
                .addSeparator()
                .addStructureInfo("Structure Guide:");

            // 添加结构层信息
            System.out.println("[DEBUG][VacuumFreezer] 添加结构层信息");
            for (int y = 0; y < height; y++) {
                StringBuilder layerInfo = new StringBuilder();
                layerInfo.append("Layer ").append(y + 1).append(": ");

                if (structure[y] != null) {
                    for (int x = 0; x < structure[y].length; x++) {
                        if (structure[y][x] != null) {
                            layerInfo.append(structure[y][x]).append(" ");
                            System.out.println("[DEBUG][VacuumFreezer] 第 " + (y + 1) + " 层添加行: " + structure[y][x]);
                        } else {
                            System.out.println("[DEBUG][VacuumFreezer] 警告：第 " + (y + 1) + " 层第 " + (x + 1) + " 行为null，跳过");
                        }
                    }
                } else {
                    System.out.println("[DEBUG][VacuumFreezer] 警告：第 " + (y + 1) + " 层为null，跳过");
                }

                String layerString = layerInfo.toString().trim();
                System.out.println("[DEBUG][VacuumFreezer] 添加层信息: " + layerString);
                tt.addStructureInfo(layerString);
            }

            // 添加组件信息
            System.out.println("[DEBUG][VacuumFreezer] 添加组件信息");
            tt.addSeparator()
                .addCasingInfoRange("Frost Proof Machine Casing", 16, 24, false)
                .addEnergyHatch("Any casing", 1)
                .addMaintenanceHatch("Any casing", 1)
                .addInputHatch("Any casing", 1)
                .addOutputHatch("Any casing", 1)
                .addInputBus("Any casing", 1)
                .addOutputBus("Any casing", 1)
                .toolTipFinisher();

            System.out.println("[DEBUG][VacuumFreezer] 工具提示创建完成");

        } catch (Exception e) {
            System.out.println("[DEBUG][VacuumFreezer] 发生错误:");
            System.out.println("[DEBUG][VacuumFreezer] 错误类型: " + e.getClass().getName());
            System.out.println("[DEBUG][VacuumFreezer] 错误信息: " + e.getMessage());
            System.out.println("[DEBUG][VacuumFreezer] 错误堆栈:");
            e.printStackTrace();
            return getDefaultTooltip(tt);
        }

        return tt;
    }

    private MultiblockTooltipBuilder getDefaultTooltip(MultiblockTooltipBuilder tt) {
        System.out.println("[DEBUG][VacuumFreezer] 使用默认工具提示");
        return tt.addMachineType("Vacuum Freezer")
            .addInfo("Cools hot ingots and cells")
            .beginStructureBlock(3, 3, 3, true)
            .addController("Front center")
            .addCasingInfoRange("Frost Proof Machine Casing", 16, 24, false)
            .addEnergyHatch("Any casing", 1)
            .addMaintenanceHatch("Any casing", 1)
            .addInputHatch("Any casing", 1)
            .addOutputHatch("Any casing", 1)
            .addInputBus("Any casing", 1)
            .addOutputBus("Any casing", 1)
            .toolTipFinisher();
    }

}
