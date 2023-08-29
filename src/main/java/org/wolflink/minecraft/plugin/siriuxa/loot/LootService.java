package org.wolflink.minecraft.plugin.siriuxa.loot;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.file.database.LootDB;

@Singleton
public class LootService extends LootDB {
    public Result addLoot(Player player, String entityTypeName, String dropChanceStr) {
        double dropChance;
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(entityTypeName.toUpperCase()); // 实体类型
            dropChance = Double.parseDouble(dropChanceStr);
        } catch (Exception ignore) {
            return new Result(true, "输入的指令格式有误");
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand(); // 玩家手持的物品

        if (itemStack.getType() == Material.AIR) return new Result(true, "必须手持物品");

        setLoot(entityType, itemStack);
        setDropChance(itemStack, dropChance);

        return new Result(true, "添加" + itemStack.getType().name() + "为" + entityType.name() + "的战利品，掉落概率为" + dropChance + "%");
    }

    public Result reloadLoot() {
        reload();
        return new Result(true, "配置文件重载成功");
    }
}
