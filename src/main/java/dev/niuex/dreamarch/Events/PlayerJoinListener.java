package dev.niuex.dreamarch.Events;

import dev.niuex.dreamarch.Areas.PlayerArea;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
//import static dev.niuex.dreamarch.Areas.PlayerArea.;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerArea.Check(player);
    }
}
