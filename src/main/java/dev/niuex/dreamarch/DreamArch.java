package dev.niuex.dreamarch;

import dev.niuex.dreamarch.Areas.AreaList;
import dev.niuex.dreamarch.Commands.Arch;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class DreamArch extends JavaPlugin {

    public static DreamArch instance;
    public Logger logger = getLogger();
    @Override
    public void onEnable() {
        instance = this; // 初始化静态实例
        logger.info("* DreamArch Service 已启用");
        Arch.init();
        AreaList.init();
    }

    @Override
    public void onDisable() {
//         Plugin shutdown logic
        AreaList.saveAll();
    }
}
