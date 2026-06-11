package com.science.gtnl.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

// Fuck you final
public class GregTechFinalFieldTransformer implements IClassTransformer {

    private static final String COMMON_META_TILE_ENTITY = "gregtech.api.metatileentity.CommonMetaTileEntity";
    private static final String META_TILE_ENTITY = "gregtech.api.metatileentity.MetaTileEntity";
    private static final String ITEM_STACK_ARRAY_DESCRIPTOR = "[Lnet/minecraft/item/ItemStack;";
    private static final String ITEM_STACK_HANDLER_DESCRIPTOR = "Lcom/cleanroommc/modularui/utils/item/ItemStackHandler;";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !isTargetClass(transformedName)) {
            return basicClass;
        }

        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(reader, 0);
        reader.accept(new GregTechInventoryFieldVisitor(writer, transformedName), 0);
        return writer.toByteArray();
    }

    private boolean isTargetClass(String transformedName) {
        return COMMON_META_TILE_ENTITY.equals(transformedName) || META_TILE_ENTITY.equals(transformedName);
    }

    private static class GregTechInventoryFieldVisitor extends ClassVisitor {

        private final String className;

        private GregTechInventoryFieldVisitor(ClassVisitor classVisitor, String className) {
            super(Opcodes.ASM5, classVisitor);
            this.className = className;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(removeBlockedFinalModifier(access, name, desc), name, desc, signature, value);
        }

        private int removeBlockedFinalModifier(int access, String name, String desc) {
            if (isMutableInventoryField(name, desc)) {
                return access & ~Opcodes.ACC_FINAL;
            }
            return access;
        }

        private boolean isMutableInventoryField(String name, String desc) {
            if (COMMON_META_TILE_ENTITY.equals(className)) {
                return "mInventory".equals(name) && ITEM_STACK_ARRAY_DESCRIPTOR.equals(desc);
            }
            if (META_TILE_ENTITY.equals(className)) {
                return "inventoryHandler".equals(name) && ITEM_STACK_HANDLER_DESCRIPTOR.equals(desc);
            }
            return false;
        }
    }
}
