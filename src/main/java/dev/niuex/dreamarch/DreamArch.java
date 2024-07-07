package dev.niuex.dreamarch;

import dev.niuex.dreamarch.Arch.AreaGenerator;
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
        instance = JavaPlugin.getPlugin(DreamArch.class);
        logger.info("* DreamArch Service 已启用");
        ArchCommand.init();
        VoteCommand.init();
        AreaList.init();
        AreaGenerator.start();
        Vote.init();
        this.getServer().getPluginManager().registerEvents(new PlayerMovementListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        saveAllDelay();
    }

    public void saveAllDelay() {
        int period = 20 * 300; // 每5分钟执行一次（300秒）
        this.getServer().getScheduler().runTaskTimer(this, () -> {
            this.getLogger().info("保存所有数据中。");
            AreaList.saveAll();
            Vote.save();
            this.getLogger().info("保存完毕。");
        }, period, period);
    }

    @Override
    public void onDisable() {
//         Plugin shutdown logic
        AreaList.saveAll();
        Vote.save();
    }
}