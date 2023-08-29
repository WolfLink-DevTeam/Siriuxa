package org.wolflink.minecraft.plugin.siriuxa.api;

import com.mongodb.lang.Nullable;
import org.wolflink.common.ioc.Singleton;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

@Singleton
public class RandomAPI {
    Random random = new Random();

    @Nullable
    public <T> T selectRandom(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) return null;
        int r = random.nextInt(collection.size());
        T value = null;
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i <= r; i++) {
            value = iterator.next();
        }
        return value;
    }
}
