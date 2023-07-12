package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.wolflink.common.ioc.IOC;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public abstract class InventoryMenu extends Menu {

    @Getter
    private final int size;
    @Getter
    private final Inventory inventory;
    /**
     * 刷新周期设置小于0则为静态菜单
     *
     * @param refreshTicks 刷新周期(刻)
     */
    public InventoryMenu(long refreshTicks,String title,int size) {
        super(refreshTicks);
        inventory = Bukkit.createInventory(null,size,title);
        this.size = size;
    }
    @Override
    public Visible getVisible() {
        return (player)->{
            player.openInventory(inventory);
        };
    }
    @Override
    public void onVisibleUpdate() {
        updateContent();
        updateInventory();
    }

    @Nullable
    public ItemIcon findClickedIcon(int index) {
        if(0 <= index && index < inventory.getSize()) {
            return (ItemIcon) getContent()[index];
        } else return null;
    }

    /**
     * 根据 content 更新背包
     */
    private void updateInventory() {
        for (int i = 0; i < size; i++) {
            ItemIcon itemIcon = (ItemIcon) getContent()[i];
            inventory.setItem(i,itemIcon.getIcon());
        }
    }
    /**
     * 如果是动态菜单则会反复调用该方法
     * 静态菜单则只会在玩家打开菜单时调用刷新
     */
    public abstract void updateContent();
    @Override
    protected ItemIcon[] initContent() {
        ItemIcon[] itemIcons = new ItemIcon[size];
        EmptyItemIcon emptyItemIcon = IOC.getBean(EmptyItemIcon.class);
        for (int i = 0; i < size; i++) {
            itemIcons[i] = emptyItemIcon;
        }
        Border border = new Border();
        if(size == 27) {
            Stream.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26)
                    .forEach(index -> itemIcons[index] = border);
        }
        if(size == 54) {
            Stream.of(0,1,2,3,4,5,6,7,8,53,52,51,50,49,48,47,46,45)
                    .forEach(index -> itemIcons[index] = border);
        }

        return itemIcons;
    }
}
