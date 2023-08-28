package org.wolflink.minecraft.plugin.siriuxa.task.ornaments.smartai.customgoals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;

import java.util.stream.Stream;

public class ZombieBlockGoal extends Goal {
    private final Entity zombieEntity;
    private final Zombie zombie;
    public ZombieBlockGoal(Zombie zombie) {
        this.zombie = zombie;
        this.zombieEntity = zombie.getBukkitEntity();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if(zombie.getTarget() != null && zombie.getTarget().position().y - zombie.position().y >= 2) {
            return true;
        }
        return false;
    }

    private int stayTicks = 8;
    @Override
    public boolean canContinueToUse() {
        boolean result = stayTicks-- > 0;
        if(!result) stayTicks = 8;
        return result;
    }

    private static final double PULL_DISTANCE = 0.05;
    @Override
    public void start() {
        Siriuxa.getInstance().getSubScheduler().runTaskAsync(()->{
            breakNearestBlocks();
            Block locBlock = zombieEntity.getLocation().getBlock();
            Block headBlock = locBlock.getRelative(0,2,0);
            breakBlock(headBlock);
            jumpAndPlaceBlock();
            dragToTarget();
        });
    }
    /**
     * 强行牵引怪物到目标
     */
    private void dragToTarget() {
        LivingEntity livingEntity = zombie.getTarget();
        if(livingEntity != null) {
            Vec3 vector = livingEntity.position().add(zombie.position().reverse());
            double x;
            if(vector.x == 0) x = 0;
            else x = vector.x > 0 ? PULL_DISTANCE : -PULL_DISTANCE;
            double z;
            if(vector.z == 0) z = 0;
            else z = vector.z > 0 ? PULL_DISTANCE : -PULL_DISTANCE;
            Siriuxa.getInstance().getSubScheduler().runTask(()->zombieEntity.setVelocity(new Vector(x,0,z)));
        }
    }

    /**
     * 破坏紧邻的方块
     */
    private void breakNearestBlocks() {
        Block locBlock = zombieEntity.getLocation().getBlock();
        Stream.of(
                locBlock.getRelative(-1,0,0),
                locBlock.getRelative(1,0,0),
                locBlock.getRelative(0,0,-1),
                locBlock.getRelative(0,0,1),
                locBlock.getRelative(-1,1,0),
                locBlock.getRelative(1,1,0),
                locBlock.getRelative(0,1,-1),
                locBlock.getRelative(0,1,1)
                ).filter(block -> {
                    double deltaX = block.getLocation().getBlockX() + 0.5 - zombieEntity.getLocation().getX();
                    double deltaZ = block.getLocation().getBlockZ() + 0.5 - zombieEntity.getLocation().getZ();
                    double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                    return distance <= 1.0;
                })
                .forEach(this::breakBlock);
    }
    private void jumpAndPlaceBlock() {
        Block footBlock = zombieEntity.getLocation().getBlock();
        zombie.getJumpControl().jump();
        Siriuxa.getInstance().getSubScheduler().runTaskLater(()->{
            if(zombieEntity.isDead()) return;
            if(footBlock.getRelative(0,-1,0).isSolid()) {
                footBlock.setType(Material.COBBLESTONE);
                footBlock.getWorld().playSound(footBlock.getLocation(), Sound.BLOCK_STONE_PLACE,1f,1f);
            }
        },5);
    }
    private void breakBlock(Block block) {
        Siriuxa.getInstance().getSubScheduler().runTask(()->{
            if(block.isSolid()) {
                block.breakNaturally();
                block.getWorld().playSound(block.getLocation(),block.getBlockSoundGroup().getBreakSound(),1f,1f);
            }
        });
    }
}
