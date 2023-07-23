package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;

import java.util.UUID;

@Singleton
public class PlayerFocusSpawnStrategy extends SpawnStrategy{

    @Override
    public boolean isApplicable(Player player) {
        return false;
    }

    @Override
    public void spawn(Player player) {

    }

}
