package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.adaptivemarket.GoodsType;
import org.wolflink.minecraft.plugin.siriuxa.adaptivemarket.SellLog;
import org.wolflink.minecraft.plugin.siriuxa.api.DateAPI;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.io.File;
import java.util.*;

/**
 * 自适应市场的文件数据库
 *-adaptive_market
 *   -crop
 *     -20230721
 *     -20230720
 *     -...
 *   -mineral
 *   -monster
 *   -item
 */
@Singleton
public class AdaptiveMarketDB extends FileDB{
    @Inject
    private DateAPI dateAPI;
    public AdaptiveMarketDB() {
        super("adaptive_market");
    }

    /**
     * 保存记录(不直接保存到文件)
     */
    public void save(SellLog log) {
        FileConfiguration fileConfiguration = getDateFileConfiguration(log.getGoodsType(),log.getDate());
        fileConfiguration.set(String.valueOf(log.getDate().getTimeInMillis()),log);
    }

    @NonNull
    private File getDateFile(GoodsType goodsType,Calendar calendar) {
        String typeName = goodsType.name().toLowerCase();
        File typeFolder = new File(folder,typeName);
        if(!typeFolder.exists()) typeFolder.mkdirs();
        return new File(typeFolder,dateAPI.getDate(calendar));
    }
    /**
     * 尝试读取指定类型的商品于指定日期的数据文件
     * 如果不存在则会创建
     */
    private FileConfiguration getDateFileConfiguration(GoodsType goodsType,Calendar calendar) {
        File dateFile = getDateFile(goodsType,calendar);
        FileConfiguration fileConfiguration = getFileConfiguration(dateFile);
        if(fileConfiguration == null) fileConfiguration = createAndLoad(dateFile);
        return fileConfiguration;
    }

    /**
     * 读取指定日期的记录
     */
    @NonNull
    public List<SellLog> load(GoodsType goodsType, Calendar calendar) {
        FileConfiguration fileConfiguration = getDateFileConfiguration(goodsType,calendar);
        Set<String> paths = fileConfiguration.getKeys(false);
        List<SellLog> result = new ArrayList<>();
        for (String path : paths) {
            result.add((SellLog) fileConfiguration.get(path));
        }
        if(result.size() == 0) Notifier.debug("未能获取到"+goodsType.name().toLowerCase()+"于"+dateAPI.getDate(calendar)+"的数据");
        return result;
    }
}
