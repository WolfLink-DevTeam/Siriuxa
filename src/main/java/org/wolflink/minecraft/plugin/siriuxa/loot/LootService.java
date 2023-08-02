package org.wolflink.minecraft.plugin.siriuxa.loot;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.file.database.LootDB;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class LootService extends LootDB {
    public Result addLoot(Player player, String[] strings) {
        if (strings.length != 1) return new Result(true, "输入的指令格式有误");

        EntityType entityType = EntityType.valueOf(strings[0].toUpperCase()); // 实体类型
        ItemStack itemStack = player.getInventory().getItemInMainHand(); // 玩家手持的物品

        if (itemStack.getType() == Material.AIR) return new Result(true, "必须手持物品");

        Map<String, ItemStack> loot = new HashMap<>(); // 怪物的死亡掉落战利品
        loot.put(entityType.name().toLowerCase(), itemStack);
        double dropChance = Math.random();// 怪物死亡时有dropChance(0-1的随机数)的概率掉落

        setDropChance(loot, dropChance);

        return new Result(true, "添加" + itemStack.getType().name() + "为" + entityType.name() + "的战利品，掉落概率为" + dropChance + "%.");
    }

    public Result reloadLoot() {
        reload();
        return new Result(true, "配置文件重载成功");
    }
}
