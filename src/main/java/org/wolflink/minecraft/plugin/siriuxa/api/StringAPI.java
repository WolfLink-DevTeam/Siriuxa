package org.wolflink.minecraft.plugin.siriuxa.api;

import com.mojang.datafixers.types.Func;
import org.wolflink.common.ioc.Singleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class StringAPI {
    public <T> String joining(Collection<T> collection, char separator) {
        return joining(collection, Object::toString, separator);
    }
    public <T> String joining(Collection<T> collection, Function<T,String> toStringFunc, char separator) {
        StringBuilder stringBuilder = new StringBuilder();
        collection.forEach(it -> {
            stringBuilder.append(toStringFunc.apply(it));
            stringBuilder.append(separator);
        });
        if(!collection.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        return stringBuilder.toString();
    }
    public <T> Stream<T> spliting(String string, Function<String,T> toTFunc, char separator) {
        return Arrays.stream(string.split(String.valueOf(separator))).map(toTFunc);
    }
}
