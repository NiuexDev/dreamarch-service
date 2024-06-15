package dev.niuex.dreamarch;

import dev.niuex.dreamarch.Areas.AreaList;
import dev.niuex.dreamarch.Commands.ArchCommand;
import dev.niuex.dreamarch.Events.PlayerJoinListener;
import dev.niuex.dreamarch.Commands.VoteCommand;
import dev.niuex.dreamarch.Events.PlayerMovementListener;
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
        this.getServer().getPluginManager().registerEvents(new PlayerMovementListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

    }

    @Override
    public void onDisable() {
//         Plugin shutdown logic
        AreaList.saveAll();
    }
}
