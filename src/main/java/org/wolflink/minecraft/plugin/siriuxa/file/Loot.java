package org.wolflink.minecraft.plugin.siriuxa.file;

import lombok.NonNull;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;

import java.util.Map;

public class Loot extends YamlConfig {

    public Loot(String configName, Map<String, Object> defaultConfig) {
        super("SeriuxaJourneyLoot", LootProjection.asMap());
    }

    @NonNull
    public <T> T get(LootProjection lootProjection) {
        T result = super.get(lootProjection.getPath());
        if (result == null) return (T) lootProjection.getDefaultValue();
        else return result;
    }
}
