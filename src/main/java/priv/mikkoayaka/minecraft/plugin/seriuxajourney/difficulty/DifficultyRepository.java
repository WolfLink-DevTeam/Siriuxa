package priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty;

import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

/**
 * 任务难度等级 -> 任务难度记录类
 */
@Singleton
public class DifficultyRepository extends MapRepository<Integer,TaskDifficulty> {
    public DifficultyRepository() {
        insert(new TaskDifficulty(
                1,
                "轻松",
                50,
                1.0,
                1.0,
                6,
                0.1,
                0.5
        ));
        insert(new TaskDifficulty(
                2,
                "常规",
                80,
                1.5,
                1.25,
                9,
                0.16,
                0.65
        ));
        insert(new TaskDifficulty(
                3,
                "困难",
                120,
                1.8,
                1.5,
                12,
                0.24,
                0.8
        ));
        insert(new TaskDifficulty(
                4,
                "专家",
                160,
                2.0,
                1.75,
                15,
                0.35,
                1.0
        ));
    }
    @Override
    public Integer getPrimaryKey(TaskDifficulty taskDifficulty) {
        return taskDifficulty.level();
    }
}
