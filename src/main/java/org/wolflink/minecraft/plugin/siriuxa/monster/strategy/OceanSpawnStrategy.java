package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;


@Singleton
public class OceanSpawnStrategy extends SpawnStrategy{

    @Override
    public boolean isApplicable(Player player) {
        return false;
    }

    @Override
    public void spawn(Player player) {

    }

}
