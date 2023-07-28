package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class AttributeChecker extends WolfirdListener {

    private static final AttributeModifier healthModifier = new AttributeModifier("40-health-modifier",20, AttributeModifier.Operation.ADD_NUMBER);

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!player.isOnline()) return;
        AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attributeInstance == null) return;
        if(attributeInstance.getValue() == 20.0) {
            attributeInstance.addModifier(healthModifier);
        }

    }
}
