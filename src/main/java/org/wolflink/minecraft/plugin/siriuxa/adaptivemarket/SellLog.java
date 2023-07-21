package org.wolflink.minecraft.plugin.siriuxa.adaptivemarket;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 向系统出售的日志(不要转为 Record 类型，Bukkit不一定支持)
 */
@Data
@AllArgsConstructor
public class SellLog implements ConfigurationSerializable {
    /**
     * 日期(作为主键)
     */
    private final Calendar date;
    /**
     * 出售者名称
     */
    private final String sellerName;
    /**
     * 商品类型
     */
    private final GoodsType goodsType;
    /**
     * 实际价格
     */
    private final double price;
    @Override
    @NonNull
    public Map<String,Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("sellerName",sellerName);
        map.put("goodsType",goodsType);
        map.put("date",Calendar.getInstance().getTimeInMillis());
        map.put("price",price);
        return map;
    }
    public static SellLog deserialize(Map<String,Object> map) {
        String sellerName = (String) map.get("sellerName");
        GoodsType goodsType = (GoodsType) map.get("goodsType");
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis((long) map.get("date"));
        double price = (double) map.get("price");
        return new SellLog(date,sellerName,goodsType,price);
    }
}
