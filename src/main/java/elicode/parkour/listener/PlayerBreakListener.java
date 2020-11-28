package elicode.parkour.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerBreakListener implements Listener {

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){

        HumanEntity human = event.getPlayer();

        //クリックしたのがプレイヤーでなければ戻る
        if(!(human instanceof Player)) return;

        Player player = (Player) human;

        //クリエイティブモードでなければ全ての操作をキャンセルする
        if(player.getGameMode() != GameMode.CREATIVE) event.setCancelled(true);

    }

    @EventHandler
    public void soilChangePlayer(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SKULL)
            event.setCancelled(true);
    }

    @EventHandler
    public void soilChangeEntity(EntityInteractEvent event) {
        if (event.getEntityType() != EntityType.PLAYER && event.getBlock().getType() == Material.SKULL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        switch(event.getNewState().getData().getItemType()) {
            case CACTUS:
            case VINE:
            case CHORUS_PLANT:
            case SUGAR_CANE:
                event.setCancelled(true);
            default:
                return;
        }
    }

    /*@EventHandler
    public void onPhysicsChange(BlockPhysicsEvent event) {
        switch (event.getBlock().getType()) {
            case VINE:
            case CACTUS:
            case CHORUS_PLANT:
            case SUGAR_CANE:
                event.setCancelled(true);
            default:
                return;
        }
    }
    @EventHandler
    public void onGrowth(BlockSpreadEvent event) {
        switch (event.getBlock().getType()) {
            case VINE:
            case CACTUS:
            case CHORUS_PLANT:
            case SUGAR_CANE:
                event.setCancelled(true);
            default:
                return;
        }
    }*/


}
