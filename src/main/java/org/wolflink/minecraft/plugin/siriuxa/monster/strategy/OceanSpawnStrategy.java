package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.monster.SpawnerAttribute;


public class OceanSpawnStrategy extends SpawnStrategy{

    public OceanSpawnStrategy(SpawnerAttribute spawnerAttribute) {
        super(spawnerAttribute);
    }

    @Override
    public boolean isApplicable(Player player) {
        return false;
    }

    @Override
    public void spawn(Player player) {

    }

}
