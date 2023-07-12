package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum LangProjection {
    ;
    @Getter
    private final String path;
    @Getter
    private final Object defaultValue;
    LangProjection(String path,Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }
    public static Map<String,Object> asMap() {
        Map<String,Object> map = new HashMap<>();
        for (LangProjection langProjection : LangProjection.values()) {
            map.put(langProjection.path,langProjection.defaultValue);
        }
        return map;
    }
}
