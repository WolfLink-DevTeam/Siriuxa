package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;

import java.util.stream.Stream;

public abstract class Menu {
    private final Inventory inventory;
    private final long refreshTicks;
    private ItemIcon[] icons;
    @Getter
    private final String title;
    @Getter
    private final int size;
    /**
     * 刷新周期设置小于0则为静态菜单
     * @param refreshTicks  刷新周期(刻)
     */
    public Menu(long refreshTicks,String title,int size) {
        this.refreshTicks = refreshTicks;
        this.title = title;
        this.size = size;
        inventory = Bukkit.createInventory(null,size,title);
        initIcons();
        overrideIcons();
        for (int i = 0; i < size; i++) {
            ItemIcon itemIcon = getIcon(i);
            inventory.setItem(i,itemIcon.getIcon());
        }
        refresh();
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
        Border border = new Border();
        if(size == 27) {
            Stream.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26)
                    .forEach(index -> setIcon(index,border));
        }
        if(size == 54) {
            Stream.of(0,1,2,3,4,5,6,7,8,53,52,51,50,49,48,47,46,45)
                    .forEach(index -> setIcon(index, border));
        }
    }
    private void refresh() {
        if(refreshTicks <= 0)return;
        SeriuxaJourney.getInstance().getSubScheduler()
                .runTaskTimer(()->{
                    for (int i = 0; i < size; i++) {
                        ItemIcon itemIcon = getIcon(i);
                        if(itemIcon.isNeedRefresh()) inventory.setItem(i,itemIcon.getIcon());
                    }
                },refreshTicks,refreshTicks);
    }

    /**
     * 子类实现 ItemIcon
     */
    protected abstract void overrideIcons();

    public void setIcon(int index,ItemIcon icon) {
        icons[index] = icon;
    }
    public ItemIcon getIcon(int index) {
        return icons[index];
    }
    /**
     * 将菜单展示给玩家
     */
    public void display(Player player) {
        player.closeInventory();
        player.openInventory(inventory);
    }
}
