package org.wolflink.minecraft.plugin.siriuxa.sculkinfection;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.ISwitchable;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class SculkInfection implements ISwitchable {
    /**
     * 感染值
     */
    private final Map<UUID,Integer> infectionMap = new ConcurrentHashMap<>();

    private final SubScheduler subScheduler = new SubScheduler();

    @Inject
    private BlockAPI blockAPI;
    @Inject
    private Config config;

    /**
     * 增加感染值
     */
    private void addInfectionValue(Player player,int value) {
        UUID pUuid = player.getUniqueId();
        int oldValue = getInfectionValue(player.getUniqueId());
        int newValue = oldValue + value;
        if(newValue < 0) newValue = 0;
        infectionMap.put(pUuid,newValue);
    }
    public int getInfectionValue(UUID uuid) {
        return infectionMap.getOrDefault(uuid,0);
    }

    /**
     * 刷新玩家的感染值
     * 玩家站在潜声方块上，每秒获得 40 点感染值
     * 每秒获得 附近8格内潜声方块数量 x 4 点感染值
     * 如果不处在附近，则每秒 -40 点感染值
     * 牛奶可以减少 100 点感染值
     *
     * 轻度感染 达到 100 点 间歇性虚弱+间歇性挖掘疲劳+走过的方块有概率变成潜声方块
     * 中度感染 达到 200 点 虚弱+挖掘疲劳+缓慢+走过的方块有概率变成潜声方块
     * 重度感染 达到 300 点 虚弱+挖掘疲劳+走过的方块有概率变成潜声方块+凋零+缓慢+失明
     */
    private void updateInfectionValue(Player player) {
        // 不在任务世界
        if(!(player.getWorld().getName().equals(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME)))) return;
        // 不是生存模式
        if(player.getGameMode() != GameMode.SURVIVAL) return;
        UUID pUuid = player.getUniqueId();
        List<Location> nearbySculks = blockAPI.searchBlock(Material.SCULK,player.getLocation(),8);
        int sculkAmount = nearbySculks.size();
        addInfectionValue(player,sculkAmount * 4 - 25);
        int value = getInfectionValue(pUuid);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double randDouble = random.nextDouble();
        subScheduler.runTaskLater(()->{
            if(value >= 400) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE,1f,1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§c§l你被幽匿方块严重感染了！"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,1,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,40,1,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,40,0,false,false,false));
                if(randDouble <= 0.4) {
                    Material material;
                    if(random.nextDouble() <= 0.2) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    player.getLocation().clone().add(0,-1,0).getBlock().setType(material);
                }
            }
            else if (value >= 250) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE,1f,1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§5§l你变得寸步难行..."));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,40,0,false,false,false));
                if(randDouble <= 0.2) {
                    Material material;
                    if(random.nextDouble() <= 0.2) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    Location location = player.getLocation().clone().add(0,-1,0);
                    if(location.getBlock().getType().isSolid()) location.getBlock().setType(material);
                }
            }
            else if (value >= 100) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE,1f,1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§e§l你感到有些不适..."));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,10,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,10,0,false,false,false));
                if(randDouble <= 0.1) {
                    Material material;
                    if(random.nextDouble() <= 0.2) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    player.getLocation().clone().add(0,-1,0).getBlock().setType(material);
                }
            }
        },1);
    }
    public void breakSculk(Player player) {
        if(!(player.getWorld().getName().equals(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME)))) return;
        // 不是生存模式
        if(player.getGameMode() != GameMode.SURVIVAL) return;
        addInfectionValue(player,10);
    }
    private final Set<UUID> milkCDSet = new HashSet<>();
    public void drinkMilk(Player player) {
        if(!(player.getWorld().getName().equals(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME)))) return;
        // 不是生存模式
        if(player.getGameMode() != GameMode.SURVIVAL) return;
        if(milkCDSet.contains(player.getUniqueId())) return;
        if(getInfectionValue(player.getUniqueId()) > 100) {
            milkCDSet.add(player.getUniqueId());
            subScheduler.runTaskLater(()->milkCDSet.remove(player.getUniqueId()),20 * 180);
            addInfectionValue(player,-150);
            Notifier.chat("喝了牛奶之后你感觉好多了。",player);
        }
    }
    private final SculkInfectionListener sculkInfectionListener = new SculkInfectionListener(this);
    @Override
    public void enable() {
        sculkInfectionListener.setEnabled(true);
        subScheduler.runTaskTimerAsync(()->{
            Bukkit.getOnlinePlayers().forEach(this::updateInfectionValue);
        },20,20);
    }

    @Override
    public void disable() {
        milkCDSet.clear();
        sculkInfectionListener.setEnabled(false);
        subScheduler.cancelAllTasks();
    }
}
@AllArgsConstructor
class SculkInfectionListener extends WolfirdListener{
    private final SculkInfection sculkInfection;
    private static final Set<Material> sculkTypes = new HashSet<>(){{
        add(Material.SCULK);
        add(Material.SCULK_CATALYST);
    }};
    @EventHandler
    void on(BlockBreakEvent event) {
        // 不是潜声方块
        if(!sculkTypes.contains(event.getBlock().getType())) return;
        sculkInfection.breakSculk(event.getPlayer());
    }
    @EventHandler
    void on(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.MILK_BUCKET) {
            // 玩家喝了牛奶
            sculkInfection.drinkMilk(event.getPlayer());
        }
    }
}