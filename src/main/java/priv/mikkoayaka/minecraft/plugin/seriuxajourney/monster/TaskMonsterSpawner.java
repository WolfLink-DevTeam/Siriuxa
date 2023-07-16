package priv.mikkoayaka.minecraft.plugin.seriuxajourney.monster;

import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;

public class TaskMonsterSpawner {

    private final Task task;
    public TaskMonsterSpawner(Task task) {
        this.task = task;
    }

    private double healthMultiple = 1.0;
    private double movementMultiple = 1.0;
    private double damageMultiple = 1.0;

}
