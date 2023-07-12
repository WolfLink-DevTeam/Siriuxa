package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;

@Singleton
public class Config extends YamlConfig {
    public Config() {
        super("SeriuxaJourney-Config", ConfigProjection.asMap());
    }

    @NonNull
    public <T> T get(ConfigProjection configProjection) {
        T result = super.get(configProjection.getPath());
        if(result == null)return (T) configProjection.getDefaultValue();
        else return result;
    }
}
