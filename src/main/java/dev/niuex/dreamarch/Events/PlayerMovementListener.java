package dev.niuex.dreamarch.Events;

import dev.niuex.dreamarch.Areas.Area;
import dev.niuex.dreamarch.Areas.AreaList;
import dev.niuex.dreamarch.Areas.PlayerArea;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static dev.niuex.dreamarch.Areas.PlayerArea.getTempId;

public class PlayerMovementListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        int beforeId = getTempId(event.getFrom().getChunk());
        int afterId = getTempId(event.getTo().getChunk());
        if (beforeId == afterId) return;
        PlayerArea.Check(event.getPlayer(), afterId);
    }

}
