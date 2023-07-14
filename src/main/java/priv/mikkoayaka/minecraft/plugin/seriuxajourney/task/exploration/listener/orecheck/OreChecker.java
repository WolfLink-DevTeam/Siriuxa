package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.listener.orecheck;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;

import java.util.*;

@Singleton
public class OreChecker extends WolfirdListener {

    /**
     * 启用该监听器的任务类
     */
    private final Set<Class<? extends Task>> availableTaskClasses = new HashSet<>();
    public OreChecker() {
        availableTaskClasses.add(ExplorationTask.class);
    }
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private OreValues oreValues;
    @EventHandler
    public void oreBlockCheck(PlayerToggleSneakEvent e)
    {
        Player player = e.getPlayer();
        Location checkLoc = player.getLocation().add(0,-1,0);
        Task task = taskRepository.findByPlayer(player);
        if(task == null) return;
        if(!availableTaskClasses.contains(task.getClass())) return;
        Block block = checkLoc.getBlock();
        Material material = block.getType();
        if(!oreValues.getOreMaterials().contains(material)) return;
        oreValues.record(material);
        block.setType(Material.AIR);
        double wheatValue = oreValues.getOreValue(material);
        task.addWheat(wheatValue);
        for (Player teamPlayer : task.getPlayers()) {
            teamPlayer.playSound(teamPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1.5f);
            teamPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§f"+player.getName()+"§7刚刚出售了"+material.name()+"§7换取 §f"+String.format("%.1f",wheatValue)+" §6麦穗"));
        }
        renderBlockBorder(block.getLocation());
    }
    private void renderBlockBorder(Location center)
    {
        Bukkit.getScheduler().runTaskAsynchronously(SeriuxaJourney.getInstance(),()->{
            World world = center.getWorld();
            for (double x = 0;x <= 1;x+=0.1)
            {
                world.spawnParticle(Particle.END_ROD,center.clone().add(x,0,0),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(x,0,1),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(x,1,0),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(x,1,1),1,0, 0, 0, 0);
            }
            for (double y = 0;y <= 1;y+=0.1)
            {
                world.spawnParticle(Particle.END_ROD,center.clone().add(0,y,0),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(0,y,1),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(1,y,0),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(1,y,1),1,0, 0, 0, 0);
            }
            for (double z = 0;z <= 1;z+=0.1)
            {
                world.spawnParticle(Particle.END_ROD,center.clone().add(0,0,z),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(0,1,z),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(1,0,z),1,0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD,center.clone().add(1,1,z),1,0, 0, 0, 0);
            }
        });
    }
}
