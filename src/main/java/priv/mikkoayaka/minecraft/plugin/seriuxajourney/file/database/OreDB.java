package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.database;

import org.wolflink.common.ioc.Singleton;

/**
 * 记录矿物数据
 */
@Singleton
public class OreDB extends FileDB{
    public OreDB() {
        super("ore");
    }
}
