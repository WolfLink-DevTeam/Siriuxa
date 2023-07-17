package priv.mikkoayaka.minecraft.plugin.siriuxa.api;

import org.wolflink.common.ioc.Singleton;

import java.util.Calendar;

@Singleton
public class DateAPI {
    /**
     * 格式如：20230401
     */
    public String getDate(Calendar calendar) {
        return String.format("%4d%2d%2d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).replace(" ", "0");
    }

    /**
     * 格式如：20230401-115203250
     * 年月日-时分秒毫秒
     */
    public String getTime(Calendar calendar) {
        return getDate(calendar) + "-" + String.format("%2d%2d%2d%3d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
    }
}
