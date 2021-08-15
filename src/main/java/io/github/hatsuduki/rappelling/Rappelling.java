package io.github.hatsuduki.rappelling;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Rappelling extends JavaPlugin implements Listener {
    int count = 0;

    @Override
    public void onEnable() {
        getLogger().info("§6Rappellingプラグインが開始しました");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("§6Rappellingプラグインが停止しました");
    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){//タッチしたとき
        if(!(event.getBlockFace() == BlockFace.UP)){//ブロック上面以外
            ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();//手に持っているアイテム
            if(itemStack.getType() == Material.LADDER || itemStack.isSimilar(new ItemStack(Material.CHAIN))/*itemStack.equals(new ItemStack(Material.CHAIN))*/){//はしごか鎖

                Location location = event.getClickedBlock().getLocation();
                Location location1 = event.getClickedBlock().getLocation();

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(!(location1.getBlock().getRelative(event.getBlockFace()).getType()==Material.BEDROCK)){//岩盤でない限り
                            if(location.getBlock().getType() != event.getMaterial()){//つなげる位置
                                location1.getBlock().getRelative(event.getBlockFace()).setType(event.getMaterial());//手に持っているアイテムを下につなげて置いていく
                            }else{
                                location1.getBlock().getRelative(BlockFace.DOWN).setType(event.getMaterial());
                            }
                            location1.setY(location1.getY() - 1);
                            count++;
                        }else{
                            count=0;
                            //event.getPlayer().sendMessage("Bedrock cancel");
                            cancel();
                        }

                        if(count == 10){//伸ばす長さ
                            count=0;
                            //event.getPlayer().sendMessage("count 10");
                            cancel();//ブロックに着いたらキャンセル
                        }
                    }
                }.runTaskTimer(this, 1, 3);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){//鎖を降りていく
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if(location.getBlock().getType() == Material.CHAIN || location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.CHAIN){
            new BukkitRunnable(){
                @Override
                public void run(){
                    if(location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.CHAIN){
                        if(!(player.isSneaking() || player.isFlying())) {
                            player.setGravity(true);
                            location.setY(location.getY() - 0.25);
                            player.teleport(location);
                        }else{
                            player.setGravity(false);
                        }
                    }else{
                        player.setGravity(true);
                        cancel();
                        //player.sendMessage("cancel");
                    }
                }
            } .runTaskTimer(this,0,1);
        }
    }

}//埋まるのをどうにかする