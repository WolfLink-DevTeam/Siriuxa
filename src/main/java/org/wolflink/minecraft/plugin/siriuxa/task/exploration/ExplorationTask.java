package org.wolflink.minecraft.plugin.siriuxa.task.exploration;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.interfaces.HurtCheckAvailable;
import org.wolflink.minecraft.plugin.siriuxa.task.common.interfaces.OreCheckAvailable;
import org.wolflink.minecraft.plugin.siriuxa.task.common.stage.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.ReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 自由探索任务
 * 活动区域大
 */
public class ExplorationTask extends Task implements HurtCheckAvailable, OreCheckAvailable {
    @Getter
    private final ExplorationDifficulty difficulty;
    @Getter
    private final LinearStageHolder stageHolder = (LinearStageHolder) super.getStageHolder();

    private static final PlayerBackpack defaultKit = new PlayerBackpack();

    static {
        defaultKit.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        defaultKit.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        defaultKit.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        defaultKit.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.WOODEN_SWORD));
        items.add(new ItemStack(Material.WOODEN_PICKAXE));
        items.add(new ItemStack(Material.BREAD, 8));
        defaultKit.setItems(items);
    }

    public ExplorationTask(GlobalTeam globalTeam, ExplorationDifficulty difficulty) {
        super(globalTeam, difficulty, defaultKit);
        this.difficulty = difficulty;
    }

    @Override
    protected StageHolder initStageHolder() {
        TaskLinearStageHolder linearStageHolder = new TaskLinearStageHolder(this);
        linearStageHolder.bindStages(new Stage[]{new WaitStage(linearStageHolder), new ReadyStage(linearStageHolder), new GameStage(linearStageHolder), new EndStage(linearStageHolder)});
        // 进入等待阶段
        linearStageHolder.next();
        return linearStageHolder;
    }

    @Override
    public void finish() {
        // do nothing
    }

    @Override
    public void failed() {
        // do nothing
    }

    @Override
    public double getHurtWheatCost() {
        return difficulty.getHurtWheatCost();
    }

    @Override
    public String getName() {
        return "自由勘探";
    }

    @Override
    public String getColor() {
        return "§f";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExplorationTask other)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return Objects.equals(difficulty, other.difficulty) && Objects.equals(stageHolder, other.stageHolder) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(difficulty, stageHolder) + 31 * super.hashCode();
    }
}
