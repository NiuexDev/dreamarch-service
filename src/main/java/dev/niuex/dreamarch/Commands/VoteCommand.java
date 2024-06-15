package dev.niuex.dreamarch.Commands;

import dev.niuex.dreamarch.DreamArch;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class VoteCommand {

    private static final DreamArch plugin = DreamArch.instance;
    public static void init () {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(command, "创建和管理建筑", List.of("architecture", "建筑", "jz"));
        });
    }
}
