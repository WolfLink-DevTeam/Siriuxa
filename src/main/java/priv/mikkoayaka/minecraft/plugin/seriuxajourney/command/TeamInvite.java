package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.Result;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class TeamInvite extends WolfirdCommand {

    @Inject
    TaskRepository taskRepository;
    @Inject
    ExplorationService explorationService;
    /**
     * 被邀请人 - 邀请人
     */
    private final Map<UUID,String> inviteMap = new HashMap<>();

    /**
     * 玩家是否被邀请了
     */
    public boolean beenInvited(UUID uuid) {
        return inviteMap.containsKey(uuid);
    }
    public TeamInvite() {
        super(true, false, true, "sj team invite {player}", "邀请玩家加入队伍");
    }
    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        Player invited = Bukkit.getPlayer(strings[0]);
        if(invited == null || !invited.isOnline()) {
            Notifier.chat("§e邀请失败，未找到玩家：§f"+strings[0],player);
        } else  {
            Task task = taskRepository.findByPlayer(player);
            if(task == null) {
                Notifier.chat("§e邀请失败，你没有处于队伍中。",player);
                return;
            }
            inviteMap.put(invited.getUniqueId(),player.getName());
            Bukkit.getScheduler().runTaskLater(SeriuxaJourney.getInstance(),()->{
                if(inviteMap.containsKey(invited.getUniqueId())) {
                    inviteMap.remove(invited.getUniqueId());
                    Notifier.chat("§e发给 §f"+invited.getName()+" §e的邀请已过期。",player);
                    Notifier.chat("§e来自 §f"+player.getName()+" §e的邀请已过期。",invited);
                }
            },20 * 30);
            Notifier.notify("§a"+player.getName()+" §f邀请你加入Ta的队伍，请在30秒内回应。\n§a同意 §f/sj team accept\n§c拒绝 §f/sj team deny",invited);
            Notifier.chat("邀请成功，等待对方回应。",player);
        }
    }
    public void accept(Player invited) {
        if(!beenInvited(invited.getUniqueId())) {
            Notifier.chat("§e你没有收到任何有效的队伍邀请。",invited);
        } else {
            String senderName = inviteMap.get(invited.getUniqueId());
            OfflinePlayer offlinePlayer = Bukkit.getPlayer(senderName);
            Task task;
            if(offlinePlayer == null) task = null;
            else task = taskRepository.findByUuid(offlinePlayer.getUniqueId());
            if(task == null) {
                Notifier.chat("§e该队伍已解散，无法加入。",invited);
            } else {
                Result result = explorationService.joinTask(invited, (ExplorationTask) task);
                result.show(invited);
                Player player = offlinePlayer.getPlayer();
                if(result.result()) {
                    Notifier.chat("§f"+invited.getName()+" §a加入了你的队伍。",player);
                } else {
                    Notifier.chat("§f"+invited.getName()+" §e尝试加入队伍但失败了。",player);
                }
            }
            inviteMap.remove(invited.getUniqueId());
        }
    }
    public void deny(Player invited) {
        if(!beenInvited(invited.getUniqueId())) {
            Notifier.chat("§e你没有收到任何有效的队伍邀请。",invited);
        } else {
            String senderName = inviteMap.get(invited.getUniqueId());
            Player player = Bukkit.getPlayer(senderName);
            if(player != null && player.isOnline()) {
                Notifier.chat("§f"+invited.getName()+" §c拒绝了你的队伍邀请。",player);
            }
            inviteMap.remove(invited.getUniqueId());
            Notifier.chat("§c你拒绝了 §f"+senderName+" §c的队伍邀请。",invited);
        }
    }
}
