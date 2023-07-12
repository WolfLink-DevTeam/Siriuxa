package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;

public abstract class Menu {
    private Visible nowVisible;
    private final long refreshTicks;
    @Getter
    private Clickable[] content;
    /**
     * 刷新周期设置小于0则为静态菜单
     * @param refreshTicks  刷新周期(刻)
     */
    public Menu(long refreshTicks) {
        this.refreshTicks = refreshTicks;
        refresh();
    }
    private void refresh() {
        if(refreshTicks <= 0)return;
        SeriuxaJourney.getInstance().getSubScheduler().runTaskTimer(()->{
            if(this.content == null)this.content = initContent();
            nowVisible = getVisible();
            onVisibleUpdate();
        },refreshTicks,refreshTicks);
    }

    /**
     * 在视图刷新时触发该方法
     */
    public abstract void onVisibleUpdate();
    public abstract Visible getVisible();
    @NonNull
    protected abstract Clickable[] initContent();
    public void display(Player player) {
        if(this.content == null)this.content = initContent();
        if(this.nowVisible == null) {
            this.nowVisible = getVisible();
            onVisibleUpdate();
        }
        nowVisible.display(player);
    }
}
