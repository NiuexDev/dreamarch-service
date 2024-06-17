package dev.niuex.dreamarch;

import dev.niuex.dreamarch.Arch.AreaList;
import dev.niuex.dreamarch.Arch.Vote;
import dev.niuex.dreamarch.Command.ArchCommand;
import dev.niuex.dreamarch.Event.PlayerJoinListener;
import dev.niuex.dreamarch.Command.VoteCommand;
import dev.niuex.dreamarch.Event.PlayerMovementListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class DreamArch extends JavaPlugin {

    public static DreamArch instance;
    public Logger logger = getLogger();
    @Override
    public void onEnable() {
        instance = this; // 初始化静态实例
        logger.info("* DreamArch Service 已启用");
        ArchCommand.init();
        VoteCommand.init();
        AreaList.init();
        Vote.init();
        this.getServer().getPluginManager().registerEvents(new PlayerMovementListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

    }

    @Override
    public void onDisable() {
//         Plugin shutdown logic
        AreaList.saveAll();
    }
}