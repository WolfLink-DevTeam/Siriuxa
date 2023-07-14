package priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDifficulty{
    /**
     * 难度图标
     */
    protected Material icon;
    /**
     * 难度颜色
     */
    protected String color;
    /**
     * 难度等级
     */
    protected int level;
    /**
     * 难度名称
     */
    protected String name;
}