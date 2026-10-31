package com.science.gtnl.common.wireless;

import static com.gtnltest.TestSupport.check;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.science.gtnl.client.meminiterminal.MiniTerminalClient;
import com.science.gtnl.client.meminiterminal.MiniTerminalModel;

import sun.misc.Unsafe;

@Tag("wireless")
public final class MiniTerminalCacheTest {

    @Test
    void invalidatesCachedRowsWhenNetworkStateChanges() throws Exception {
        var selected = MiniTerminalClient.class.getDeclaredField("selected");
        var views = MiniTerminalClient.class.getDeclaredField("VIEWS");
        var cacheView = MiniTerminalClient.class.getDeclaredMethod("cacheView", UUID.class, MiniTerminalModel.class);
        var unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        selected.setAccessible(true);
        views.setAccessible(true);
        cacheView.setAccessible(true);
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Map<?, ?> cache = (Map<?, ?>) views.get(null);
        Object previousSelection = selected.get(null);
        cache.clear();
        try {
            selected.set(null, null);
            // Cache identity/order do not need AE's Forge-initialized item repository or a graphics context.
            MiniTerminalModel anonymous = (MiniTerminalModel) unsafe.allocateInstance(MiniTerminalModel.class);
            cacheView.invoke(null, null, anonymous);
            MiniTerminalModel[] models = new MiniTerminalModel[7];
            for (int i = 0; i < models.length; i++) {
                selected.set(null, new UUID(0, i));
                models[i] = (MiniTerminalModel) unsafe.allocateInstance(MiniTerminalModel.class);
                cacheView.invoke(null, new UUID(0, i), models[i]);
            }
            selected.set(null, null);
            check(MiniTerminalClient.model() == anonymous, "the temporary terminal's null key is cached and refreshed");
            selected.set(null, new UUID(0, 7));
            cacheView.invoke(null, new UUID(0, 7), unsafe.allocateInstance(MiniTerminalModel.class));
            check(
                cache.size() == 8 && !cache.containsKey(new UUID(0, 0)),
                "terminal views evict the oldest untouched entry");
            selected.set(null, new UUID(0, 2));
            check(MiniTerminalClient.model() == models[2], "switching back reuses the terminal model");
            selected.set(null, new UUID(0, 0));
            cacheView.invoke(null, new UUID(0, 0), unsafe.allocateInstance(MiniTerminalModel.class));
            check(MiniTerminalClient.model() != models[0], "an evicted terminal gets a fresh model");
        } finally {
            cache.clear();
            selected.set(null, previousSelection);
        }
    }
}
