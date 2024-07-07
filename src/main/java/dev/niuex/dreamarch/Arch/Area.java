package dev.niuex.dreamarch.Arch;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.niuex.dreamarch.DreamArch;
import dev.niuex.dreamarch.Util.Callback;
import org.bukkit.*;
import org.bukkit.block.Biome;

import java.util.UUID;

public class Area {

    private static final DreamArch plugin = DreamArch.instance;

    public final static int size = 25;
    public final static int innerSize = size-1;
    public static final int total = innerSize * innerSize;
//    区域大小，包含一个间隔区块，即建筑区域32区块则填32+1=33

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
    private double[] spawnPos;

    private boolean generate = false;

    private LayerData layer;

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
            Biome biome,
            double[] spawnPos
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
        this.spawnPos = spawnPos;
    }

    public Area() {
        this.id = AreaList.getIndex();
        this.pos = new int[]{this.id * size, 0};
        AreaList.addArea(this);
    }

    public void init(
            Callback<Void, Void> start,
            Callback<Void, Integer> every,
            Callback<Void, Void> finish
    ) throws CommandSyntaxException {
        if (this.init) {
            throw new CommandSyntaxException(null, () -> "建筑区域已初始化。");
        }
        if (this.generate) {
            throw new CommandSyntaxException(null, () -> "建筑区域正在初始化。");
        }
        if (this.biome == null) {
            throw new CommandSyntaxException(null, () -> "建筑区域群系未设置。");
        }
        if (this.layer == null) {
            throw new CommandSyntaxException(null, () -> "建筑区域layer未设置。");
        }

        this.generate = true;
        AreaGenerator.add(
                this,
                start,
                every,
                (unused)->{
                    finish.call(null);
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        plugin.logger.info("建筑区域[" + this.id + "]初始化完成。");
                        this.spawnPos = new double[] {
                                this.pos[0] * 16 + innerSize * 8,
                                plugin.getServer().getWorld("world")
                                        .getHighestBlockYAt(
                                                this.pos[0] * 16 + innerSize * 16 / 2,
                                                this.pos[1] * 16 + innerSize * 16 / 2
                                        ) + 1,
                                this.pos[1] * 16 + innerSize * 8
                        };
                        this.init = true;
                    });
                    return null;
                }
        );
//        AtomicInteger runCount = new AtomicInteger();
//        new FlatGenerator(
//                size - 1,
//                baseHeight,
//                layerData,
//                (unused) -> {
//                    runCount.getAndIncrement();
//                    if (runCount.get() == (size - 1) * (size - 1)) {
//                        finish.call(null);
//                        plugin.logger.info("建筑区域[" + this.id + "]初始化完成。");
//                        this.spawnPos = new double[] {
//                                this.pos[0] * 16 + (size - 1) * 8,
//                                plugin.getServer().getWorld("world")
//                                        .getHighestBlockYAt(
//                                            this.pos[0] * 16 + (size - 1) * 16 / 2,
//                                            this.pos[1] * 16 + (size - 1) * 16 / 2
//                                        ) + 1,
//                                this.pos[1] * 16 + (size - 1) * 8
//                        };
//                        this.init = true;
//                    }
//                    every.call(runCount.get());
//                    return null;
//                }
//        );
//        start.call(null);
//        plugin.logger.info("开始初始化建筑区域[" + this.id + "]。");
    }

    public boolean isInit() {
        return init;
    }

    public String getName() {
        return name == null ? "未命名" : name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description == null ? "无简介" : description;
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
    public Location getSpawnLocation() {
        return new Location(
                plugin.getServer().getWorld("world"),
                this.spawnPos[0],
                this.spawnPos[1],
                this.spawnPos[2]
        );
    }
    public double[] getSpawnPos() {
        return this.spawnPos;
    }
    public void setSpawnPos(Location location) {
        this.spawnPos = new double[] {
                location.getX(),
                location.getY(),
                location.getZ()
        };
    }
    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public WeatherType getWeather() {
        return this.weather;
    }
    public void setWeather(WeatherType weather) {
        this.weather = weather;
    }
    public Biome getBiome() { return biome; }
    public void setBiome(Biome biome) {
        this.biome = biome;
    }
    public LayerData getLayer() {
        return layer;
    }
    public void setLayer(String layer) throws CommandSyntaxException {
        try {
            switch(layer) {
                case "classics":
                    this.layer = new LayerData("64;1*BEDROCK,2*DIRT,1*GRASS_BLOCK");
                    break;
                case "air":
                case "void":
                    this.layer = new LayerData("0;0*AIR");
                    break;
                case "":
                case "normal":
                    this.layer = new LayerData("0;67*DIRT,1*GRASS_BLOCK");
                    break;
                case "water":
                    this.layer = new LayerData("0;68*WATER");
                    break;
                default:
                    this.layer = new LayerData(layer);
            }
        } catch (Throwable e) {
            throw new CommandSyntaxException( null, () -> "layer格式错误。");
        }
    }


//    private class FlatGenerator {
//        final int baseHeight;
//        final Layer[] layerData;
//        int x = pos[0];
//        int z = pos[1];
//
//        public FlatGenerator(
//                int size,
//                int baseHeight,
//                Layer[] layer,
//                Callback<Void, Void> callback
//        ) {
//            this.baseHeight = baseHeight - 64;
//            this.layerData = layer;
//
//            plugin.logger.info("开始生成 [ " + x + ", " + z +" ] 至 [ " + (x+size) + ", " + (z+size) +" ] 的建筑区域...");
//
//            long delay = 0L;
//            BukkitScheduler scheduler = plugin.getServer().getScheduler();
//            for (int currentX = x; currentX < x + size; currentX++) {
//                for (int currentZ = z; currentZ < z + size; currentZ++) {
//                    Chunk chunk = plugin.getServer().getWorld("world").getChunkAt(currentX, currentZ);
//                    scheduler.runTaskLater(plugin, () -> {
//                        generateChunk(chunk);
//                        callback.call(null);
//                    },  delay);
//                    delay+=3;
//                }
//            }
//        }
//        private void generateChunk(Chunk chunk) {
//            int baseHeight = this.baseHeight;
//
//            for (Layer layer : layerData) {
//                for (int y = 0; y < layer.height; y++) {
//                    for (int x = 0; x < 16; x++) {
//                        for (int z = 0; z < 16; z++) {
//                            chunk.getBlock(x, baseHeight + y, z).setType(layer.id);
//                        }
//                    }
//                }
//                baseHeight +=  layer.height;
//
//            }
//            for (int y = -64; y < 319; y++) {
//                for (int x = 0; x < 16; x++) {
//                    for (int z = 0; z < 16; z++) {
//                        chunk.getBlock(x, y, z).setBiome(biome);
//                    }
//                }
//            }
//        }
//    }
//
//    private static class Layer {
//        public Material id;
//        public int height;
//
//        public Layer(String layer) {
//            String[] parts = layer.split("\\*");
//            height = Integer.parseInt(parts[0]);
//            id = Material.valueOf(parts[1].toUpperCase());
//        }
//    }
}