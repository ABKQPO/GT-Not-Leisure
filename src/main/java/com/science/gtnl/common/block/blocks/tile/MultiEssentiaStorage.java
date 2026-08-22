package com.science.gtnl.common.block.blocks.tile;

import java.util.Comparator;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

// 多源质共享存储组件：围绕一个 AspectList + 容量，封装排序缓存与全部容器操作。
// 供复合源质罐 / 复合源质输入仓 / 复合源质管道复用，消除三份重复实现。
// 组件不拥有 AspectList 的所有权，直接操作 TE 传入的列表（罐/仓用各自
// 的 storedAspects，管道复用父类 aspects 字段），因此序列化与渲染不受影响。
public class MultiEssentiaStorage {

    private final AspectList storedAspects;
    private final int capacity;
    private Aspect[] cachedSortedAspects;
    private boolean sortedCacheDirty = true;

    public MultiEssentiaStorage(AspectList storedAspects, int capacity) {
        this.storedAspects = storedAspects;
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTotalAmount() {
        int total = 0;
        for (Aspect storedAspect : getStoredAspectsSorted()) {
            total += storedAspects.getAmount(storedAspect);
        }
        return total;
    }

    public int getStoredTypeCount() {
        return getStoredAspectsSorted().length;
    }

    public boolean isEmpty() {
        return getTotalAmount() <= 0;
    }

    public boolean isFull() {
        return getTotalAmount() >= capacity;
    }

    public int getAmount(Aspect aspect) {
        return aspect == null ? 0 : storedAspects.getAmount(aspect);
    }

    public AspectList copyAspects() {
        AspectList copy = new AspectList();
        for (Aspect storedAspect : getStoredAspectsSorted()) {
            copy.add(storedAspect, storedAspects.getAmount(storedAspect));
        }
        return copy;
    }

    public void setAspects(AspectList aspects) {
        storedAspects.aspects.clear();
        if (aspects != null) {
            int remaining = capacity;
            for (Aspect storedAspect : getSortedAspects(aspects)) {
                int accepted = Math.min(aspects.getAmount(storedAspect), remaining);
                if (accepted <= 0) continue;

                storedAspects.add(storedAspect, accepted);
                remaining -= accepted;
                if (remaining == 0) break;
            }
        }
        sortedCacheDirty = true;
    }

    public boolean doesContainerAccept(Aspect aspect) {
        return aspect != null && getTotalAmount() < capacity;
    }

    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) return amount;

        int accepted = Math.min(amount, capacity - getTotalAmount());
        if (accepted <= 0) return amount;

        storedAspects.add(aspect, accepted);
        sortedCacheDirty = true;
        return amount - accepted;
    }

    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!doesContainerContainAmount(aspect, amount)) return false;

        storedAspects.remove(aspect, amount);
        sortedCacheDirty = true;
        return true;
    }

    public boolean takeFromContainer(AspectList requestedAspects) {
        if (!doesContainerContain(requestedAspects)) return false;

        for (Aspect requestedAspect : getSortedAspects(requestedAspects)) {
            storedAspects.remove(requestedAspect, requestedAspects.getAmount(requestedAspect));
        }
        sortedCacheDirty = true;
        return true;
    }

    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return aspect != null && amount >= 0 && storedAspects.getAmount(aspect) >= amount;
    }

    public boolean doesContainerContain(AspectList requestedAspects) {
        if (requestedAspects == null) return false;

        for (Aspect requestedAspect : getSortedAspects(requestedAspects)) {
            if (!doesContainerContainAmount(requestedAspect, requestedAspects.getAmount(requestedAspect))) {
                return false;
            }
        }
        return true;
    }

    public int clearAll() {
        int clearedAmount = getTotalAmount();
        if (clearedAmount <= 0) return 0;

        storedAspects.aspects.clear();
        sortedCacheDirty = true;
        return clearedAmount;
    }

    // 外部直接读入 storedAspects 后调用：重置缓存、清理无效项、裁剪到容量。
    public void reload() {
        sortedCacheDirty = true;
        removeInvalidAspects();
        trimToCapacity();
    }

    // 使排序缓存失效（供 TE 在 markEssentiaChanged 里调用）。
    public void markDirty() {
        sortedCacheDirty = true;
    }

    public Aspect[] getStoredAspectsSorted() {
        if (sortedCacheDirty) {
            cachedSortedAspects = getSortedAspects(storedAspects);
            sortedCacheDirty = false;
        }
        return cachedSortedAspects;
    }

    private void removeInvalidAspects() {
        storedAspects.aspects.entrySet()
            .removeIf(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0);
    }

    private void trimToCapacity() {
        int remaining = capacity;
        AspectList trimmed = new AspectList();

        for (Aspect storedAspect : getStoredAspectsSorted()) {
            int accepted = Math.min(storedAspects.getAmount(storedAspect), remaining);

            if (accepted > 0) {
                trimmed.add(storedAspect, accepted);
                remaining -= accepted;
            }

            if (remaining == 0) break;
        }

        storedAspects.aspects.clear();
        for (Aspect storedAspect : getSortedAspects(trimmed)) {
            storedAspects.add(storedAspect, trimmed.getAmount(storedAspect));
        }
    }

    public static Aspect[] getSortedAspects(AspectList aspectList) {
        return aspectList.aspects.entrySet()
            .stream()
            .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
            .map(entry -> entry.getKey())
            .sorted(Comparator.comparing(Aspect::getTag))
            .toArray(Aspect[]::new);
    }
}
