package dev.niuex.dreamarch.Areas;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.niuex.dreamarch.DreamArch;
import dev.niuex.dreamarch.Utils.Callback;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.scheduler.BukkitScheduler;

//import javax.security.auth.callback.Callback;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Area {

    private static final DreamArch plugin = DreamArch.instance;

    public final static int size = 25;

    public final int id;
    private String name;
    private String description;
    private boolean init = false;
    private String owner;
    private UUID ownerUuid;
    private final int[] pos;
    private int time;
    private WeatherType weather;
    private Biome biome;

    private boolean generate = false;

    private String layer;

    // 构造函数（如果有参数，请根据实际情况调整）
    public Area(
            int id,
            String name,
            String description,
            boolean init,
            String owner,
            UUID ownerUuid,
            int[] pos,
            int time,
            WeatherType weather,
            Biome biome
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.init = init;
        this.owner = owner;
        this.ownerUuid = ownerUuid;
        this.pos = pos;
        this.time = time;
        this.weather = weather;
        this.biome = biome;
    }

    public Area() {
        this.id = AreaList.getIndex();
        this.pos = new int[]{this.id * size, 0};
        AreaList.addArea(this);
    }

    public void init(Callback<Void, Void> start, Callback<Void, Integer> every, Callback<Void, Void> finish) throws CommandSyntaxException {
        if (this.init) {
            throw new CommandSyntaxException(null, () -> "区域已初始化。");
        }
        if (this.generate) {
            throw new CommandSyntaxException(null, () -> "区域正在初始化。");
        }
        if (this.biome == null) {
            throw new CommandSyntaxException(null, () -> "区域群系未设置。");
        }
        try {
            switch(this.layer) {
                case "classics":
                    this.layer = "64;1*BEDROCK,2*DIRT,1*GRASS_BLOCK";
                    break;
                case "":
                case "air":
                case "void":
                    this.layer = "0;0*AIR";
                    break;
                case "normal":
                    this.layer = "0;67*DIRT,1*GRASS_BLOCK";
                    break;
                case "water":
                    this.layer = "0;68*WATER";
            }

            int height = Integer.parseInt(this.layer.split(";")[0]);
            List<Map<String, Object>> layerData = new ArrayList<>();

            // 分割layer字符串去除高度部分，然后遍历剩余的block信息
            String[] blocksInfo = this.layer.split(";")[1].split(",");
            for (String blockInfo : blocksInfo) {
                // 分割每个block的信息为高度和BLOCK_ID
                String[] parts = blockInfo.split("\\*");
                Map<String, Object> blockData = new HashMap<>();
                blockData.put("height", Integer.parseInt(parts[0]));
                blockData.put("id", Material.valueOf(parts[1].toUpperCase()));
                // 将单个块的信息添加到layers列表中
                layerData.add(blockData);
            }
            this.generate = true;
            AtomicInteger runCount = new AtomicInteger();
            new FlatChunkGenerator(this.pos[0],
                    this.pos[1],
                    size-1,
                    height,
                    layerData,
                    this.biome,
                    (unused) -> {
                        runCount.getAndIncrement();
                        if (runCount.get() == (size-1)*(size-1)) {
                            finish.call(null);
                            plugin.logger.info("建筑区域[" + this.id + "]初始化完成。");
                            this.init = true;
                        }
                        every.call(runCount.get());
                        return null;
                    }
            );
            start.call(null);
            plugin.logger.info("开始初始化建筑区域[" + this.id + "]。");
        } catch (Throwable e) {
            throw new CommandSyntaxException( null, () -> "layer未设置或格式错误。");
        }
    }

    public boolean isInit() {
        return init;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }
    public int[] getPos() {
        return pos;
    }
    public int[] getCenterPos() {
        return new int[]{
                this.pos[0] * 16 + (size-1) * 16 / 2,
                this.pos[1] * 16 + (size-1) * 16 / 2
        };
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public WeatherType getWeather() {
        return weather;
    }
    public void setWeather(WeatherType weather) {
        this.weather = weather;
    }
    public Biome getBiome() { return biome; }
    public void setBiome(Biome biome) {
        this.biome = biome;
    }
    public String getLayer() { return layer; }
    public void setLayer(String layer) { this.layer = layer; }
}

class FlatChunkGenerator {


    private static final DreamArch plugin = DreamArch.instance;

    public FlatChunkGenerator(
            int x,
            int z,
            int size,
            int height,
            List<Map<String, Object>> layer,
            Biome biome,
            Callback<Void, Void> callback
    ) {
        plugin.logger.info("开始生成 [ " + x + ", " + z +" ] 至 [ " + (x+size) + ", " + (z+size) +" ] 的建筑区域...");

        long delay = 0L;
        for (int currentX = x; currentX < x + size; currentX++) {
            for (int currentZ = z; currentZ < z + size; currentZ++) {
                BukkitScheduler scheduler = plugin.getServer().getScheduler();
                Chunk chunk = plugin.getServer().getWorld("world").getChunkAt(currentX, currentZ);
                scheduler.runTaskLater(plugin, () -> {
                    generateChunk(chunk, height, layer, biome);
                    callback.call(null);
                },  delay);
                delay+=3;
            }
        }
    }
    private void generateChunk(Chunk chunk, int baseHeight, List<Map<String, Object>> layerData, Biome biome) {

        int baseY = -64 + baseHeight;
        for (Map<String, Object> layer : layerData) {
            int height = (int) layer.get("height");
            Material id = (Material) layer.get("id");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        chunk.getBlock(x, baseY + y, z).setType(id);
                    }
                }
            }
            baseY +=  height;

        }
        for (int y = -64; y < 319; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.getBlock(x, y, z).setBiome(biome);
                }
            }
        }
    }
}