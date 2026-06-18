package com.science.gtnl.common.machine.multiMachineBase;

/**
 * Layout template for machine base classes in this package.
 *
 * <p>
 * English:
 * Use this class as a structural reference only. It is not a business base class and must not carry
 * executable machine logic. Real machine base classes in this package should keep members and methods
 * in the following order:
 *
 * <ol>
 * <li>Static constants and static utility methods</li>
 * <li>Persisted fields</li>
 * <li>Runtime fields</li>
 * <li>Constructors</li>
 * <li>Lifecycle methods</li>
 * <li>Structure check and parameter setup methods</li>
 * <li>Recipe and processing flow methods</li>
 * <li>Input, output, hatch, and texture helpers</li>
 * <li>Info display methods</li>
 * <li>UI methods</li>
 * <li>NBT and client sync methods</li>
 * <li>Capability switches and strategy getters</li>
 * <li>Low-level helpers and nested types</li>
 * </ol>
 *
 * <p>
 * Keep methods with the same responsibility contiguous. Do not interleave UI, Waila, NBT, and
 * recipe logic. Place overridden methods by responsibility, not by inherited declaration order.
 *
 * <p>
 * 中文：
 * 这个类仅作为本包机器基类的排布示范，不是业务基类，也不应该承载可执行机器逻辑。
 * 本包内真实的机器基类应当按以下顺序组织成员和方法：
 *
 * <ol>
 * <li>静态常量与静态工具方法</li>
 * <li>持久化字段</li>
 * <li>运行时字段</li>
 * <li>构造器</li>
 * <li>生命周期方法</li>
 * <li>结构检查与参数准备方法</li>
 * <li>配方与处理主流程方法</li>
 * <li>输入、输出、仓室与贴图辅助方法</li>
 * <li>信息展示方法</li>
 * <li>UI 方法</li>
 * <li>NBT 与客户端同步方法</li>
 * <li>能力开关与策略 Getter</li>
 * <li>底层辅助方法与内部类型</li>
 * </ol>
 *
 * <p>
 * 同一职责的方法必须连续摆放，不能把 UI、Waila、NBT、配方逻辑交错混放。覆写方法按职责归位，
 * 不按父类声明顺序归位。
 */
public abstract class MachineBaseLayoutTemplate {
}
