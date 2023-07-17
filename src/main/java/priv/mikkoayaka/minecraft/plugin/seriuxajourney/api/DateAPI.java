package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import org.bukkit.Material;
import org.wolflink.common.ioc.Singleton;

import java.util.Calendar;

@Singleton
public class DateAPI {
    /**
     * 格式如：20230401
     */
    public String getDate(Calendar calendar) {
        return String.format("%4d%2d%2d",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).replace(" ","0");
    }
}
