package com.science.gtnl.mixin.MultiBlockStructure.VacuumFreezer;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.metatileentity.implementations.MTECubicMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.common.tileentities.machines.multi.MTEVacuumFreezer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;

@Mixin(value = MTECubicMultiBlockBase.class, remap = false)
public abstract class VacuumFreezerMixin<T extends MTECubicMultiBlockBase<T>> extends MTEEnhancedMultiBlockBase<T>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";

    protected VacuumFreezerMixin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Shadow
    protected abstract List<IHatchElement<? super MTECubicMultiBlockBase<?>>> getAllowedHatches();

    @Inject(method = "getStructureDefinition", at = @At("HEAD"), cancellable = true)
    private void injectGetStructureDefinition(CallbackInfoReturnable<IStructureDefinition<T>> cir) {
        if ((Object) this instanceof MTEVacuumFreezer) {
            String[][] customStructure = StructureUtils.readStructureFromFile("sciencenotleisure:multiblock/vacuum_freezer");

            if (customStructure == null || customStructure.length == 0) {
                throw new IllegalStateException("无法加载自定义结构文件: sciencenotleisure:multiblock/vacuum_freezer");
            }

            @SuppressWarnings("unchecked")
            List<IHatchElement<? super T>> allowedHatches = (List<IHatchElement<? super T>>) (List<?>) getAllowedHatches();

            IStructureDefinition<T> customDefinition = StructureDefinition.<T>builder()
                .addShape(STRUCTURE_PIECE_MAIN, customStructure)
                .addElement(
                    'x',
                    ofChain(
                        lazy(
                            t -> convertToStructureElement(callGetCasingElement(t))
                        )
                    )
                )
                .build();

            cir.setReturnValue(customDefinition);
        }
    }

    private <T> IStructureElement<T> callGetCasingElement(MTECubicMultiBlockBase<?> instance) {
        try {
            Method method = MTECubicMultiBlockBase.class.getDeclaredMethod("getCasingElement");
            method.setAccessible(true);
            return (IStructureElement<T>) method.invoke(instance); // 在方法内部完成类型转换
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("无法调用 getCasingElement 方法", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> IStructureElement<T> convertToStructureElement(IStructureElement<?> element) {
        return (IStructureElement<T>) element;
    }
}
