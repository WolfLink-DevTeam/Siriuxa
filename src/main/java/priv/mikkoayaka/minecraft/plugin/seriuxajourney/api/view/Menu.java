package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class Menu {
    private final Inventory inventory;
    private final long refreshTicks;
    private ItemIcon[] icons;
    @Getter
    private final String title;
    @Getter
    private final int size;
    @Getter
    private final UUID ownerUuid;

    @Nullable
    public Player getOwner() {
        Player player = Bukkit.getPlayer(ownerUuid);
        if(player == null || !player.isOnline()) return null;
        return player;
    }

    /**
     * 刷新周期设置小于0则为静态菜单
     * 静态菜单只会在打开时刷新一次
     * @param refreshTicks  刷新周期(刻)
     */
    public Menu(UUID ownerUuid, long refreshTicks, String title, int size) {
        this.ownerUuid = ownerUuid;
        this.refreshTicks = refreshTicks;
        this.title = title;
        this.size = size;
        inventory = Bukkit.createInventory(null,size,title);
        Bukkit.getScheduler().runTaskLater(SeriuxaJourney.getInstance(),()->{
            refresh();
            if(refreshTicks <= 0)return;
            SeriuxaJourney.getInstance().getSubScheduler().runTaskTimer(this::refresh,refreshTicks,refreshTicks);
        },1);
    }

    /**
     * 格式化背包菜单，填充边界，空气等
     */
    private void initIcons() {
        icons = new ItemIcon[size];
        EmptyItemIcon emptyItemIcon = IOC.getBean(EmptyItemIcon.class);
        for (int i = 0; i < size; i++) {
            setIcon(i,emptyItemIcon);
        }
        Border border = IOC.getBean(Border.class);
        if(size == 27) {
            Stream.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26)
                    .forEach(index -> setIcon(index,border));
        }
        if(size == 54) {
            Stream.of(0,1,2,3,4,5,6,7,8,53,52,51,50,49,48,47,46,45)
                    .forEach(index -> setIcon(index, border));
        }
    }

    /**
     * 刷新整个菜单
     */
    private void refresh() {
        initIcons();
        overrideIcons();
        for (int i = 0; i < size; i++) {
            ItemIcon itemIcon = getIcon(i);
            inventory.setItem(i,itemIcon.getIcon());
        }
    }

    /**
     * 子类实现 ItemIcon(该方法中不能调用子类自身的成员变量)
     */
    protected abstract void overrideIcons();

    public void setIcon(int index,ItemIcon icon) {
        if(index >= icons.length || index < 0)return;
        icons[index] = icon;
    }
    public ItemIcon getIcon(int index) {
        if(index >= icons.length || index < 0) return null;
        return icons[index];
    }
    /**
     * 将菜单展示给玩家
     */
    public void display(Player player) {
        refresh();
        player.closeInventory();
        player.openInventory(inventory);
    }
}
