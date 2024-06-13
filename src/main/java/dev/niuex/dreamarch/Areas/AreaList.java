package dev.niuex.dreamarch.Areas;

import dev.niuex.dreamarch.DreamArch;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AreaList {

    private static final DreamArch plugin = DreamArch.instance;
    private static int count;
    private static int index;
    private static List<Area> areaList;

    public static void init() {
        File areaFolder = new File(plugin.getDataFolder(), "Area");
        if (!areaFolder.exists() || !areaFolder.isDirectory()) {
            areaFolder.mkdir();
        }

        areaList = new ArrayList<>();
        try {
            Files.list(Paths.get(areaFolder.getAbsolutePath()))
                    .filter(path -> path.toString().endsWith(".yml"))
                    .forEach(path -> {
                        YamlConfiguration areaConfig = YamlConfiguration.loadConfiguration(path.toFile());
                        areaList.add(new Area(
                                areaConfig.getInt("id"),
                                areaConfig.getString("name"),
                                areaConfig.getString("description"),
                                areaConfig.getBoolean("init"),
                                areaConfig.getString("owner"),
                                areaConfig.getString("owner_uuid") == null ? null : UUID.fromString(areaConfig.getString("owner_uuid")),
                                areaConfig.getIntegerList("pos").stream().mapToInt(Integer::intValue).toArray(), // 转换int列表为数组
                                areaConfig.getInt("time"),
                                areaConfig.getString("weather") == null ? null : WeatherType.valueOf(areaConfig.getString("weather")),
                                areaConfig.getString("biome") == null ? null : Biome.valueOf(areaConfig.getString("biome")),
                                areaConfig.getIntegerList("spawnPos").stream().mapToInt(Integer::intValue).toArray() // 转换int列表为数组
                        ));
                    });

            YamlConfiguration indexConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "index.yml"));
            index = indexConfig.getInt("index");
        } catch (Throwable e) {
            plugin.logger.severe("无法读取Area文件夹：" + e.getMessage() + "。\n" + Arrays.toString(e.getStackTrace()));
        }
        count = areaList.size();

        plugin.logger.info("已加载" + count + "个建筑区域。");
        areaList.forEach(area -> plugin.logger.info("[" + area.id + "] " + area.getName()));

        saveAllDelay();
    }


    // 从ConfigurationSection构建AreaData的静态方法

    public static List<Area> getAreaList() {
        return areaList;
    }

    public static Area getArea(int id) {
        for (Area area : areaList) {
            if (area.id == id) {
                return area;
            }
        }
        return null;
    }

    public static void addArea(Area area) {
        areaList.add(area);
        count = areaList.size();
        save(area.id);
        saveIndex();
    }

    public static int getCount() { return count; }

    public static int getIndex() {
        index++;
        return index;
    }

    public static void save(int id) {
        Area area = getArea(id);
        File areaFile = new File(plugin.getDataFolder(), "Area/" + area.id + ".yml");
        YamlConfiguration areaConfig = YamlConfiguration.loadConfiguration(areaFile);

        areaConfig.set("id", area.id);
        areaConfig.set("name", area.getName());
        areaConfig.set("description", area.getDescription());
        areaConfig.set("init", area.isInit());
        areaConfig.set("owner", area.getOwner());
        areaConfig.set("owner_uuid", area.getOwnerUuid() == null ? null : area.getOwnerUuid().toString());
        areaConfig.set("pos", area.getPos());
        areaConfig.set("time", area.getTime());
        areaConfig.set("weather", area.getWeather() == null ? null : area.getWeather().toString());
        areaConfig.set("biome", area.getBiome() == null ? null : area.getBiome().toString());
        areaConfig.set("spawnPos", area.getSpawnPosValue());

        try {
            areaConfig.save(areaFile);
            plugin.logger.info("[" + area.id + "] " + area.getName() + " 已保存。");
        }
        catch (IOException e) {
            plugin.logger.severe("无法保存区域文件：" + e.getMessage() + "。");
        }
    }

    public static void saveIndex() {
        File indexFile = new File(plugin.getDataFolder(), "index.yml");
        YamlConfiguration indexConfig = YamlConfiguration.loadConfiguration(indexFile);
        indexConfig.set("index", index);
        try {
            indexConfig.save(indexFile);
        } catch (IOException e) {
            plugin.logger.severe("无法保存索引文件：" + e.getMessage() + "。");
        }
    }

    public static void saveAll() {
        for (Area area : areaList) {
            save(area.id);
        }
        saveIndex();
    }

    public static void saveAllDelay() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300000);
                    plugin.logger.info("保存所有区域中。");
                    saveAll();
                    plugin.logger.info("保存完毕。");
                } catch (InterruptedException e) {
//                    plugin.logger.
                    e.printStackTrace();
                }
            }
        }).start();
    }
}