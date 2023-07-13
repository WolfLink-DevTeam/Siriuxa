package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import lombok.NonNull;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;

@Singleton
public class Lang extends YamlConfig {
    public Lang() {
        super("SeriuxaJourneyLang", LangProjection.asMap());
    }
    @NonNull
    public <T> T get(LangProjection langProjection) {
        T result = super.get(langProjection.getPath());
        if(result == null)return (T) langProjection.getDefaultValue();
        else return result;
    }
}
