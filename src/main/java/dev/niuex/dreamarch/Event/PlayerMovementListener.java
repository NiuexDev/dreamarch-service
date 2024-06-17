package dev.niuex.dreamarch.Event;

import dev.niuex.dreamarch.Arch.PlayerArea;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static dev.niuex.dreamarch.Arch.PlayerArea.getTempId;

public class PlayerMovementListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        int beforeId = getTempId(event.getFrom().getChunk());
        int afterId = getTempId(event.getTo().getChunk());
        if (beforeId == afterId) return;
        PlayerArea.Check(event.getPlayer(), afterId);
    }

}
