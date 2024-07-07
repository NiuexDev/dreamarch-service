package dev.niuex.dreamarch.Arch;

import dev.niuex.dreamarch.DreamArch;
import dev.niuex.dreamarch.Util.Callback;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class AreaGenerator {

    private static final DreamArch plugin = DreamArch.instance;

    private static final ArrayList<AreaTask> taskQueue = new ArrayList<>();
    private static BukkitTask runTaskTimer;
    private static int taskIndex = 0;

    public static void start() {
        if (runTaskTimer != null) return;
        runTaskTimer = plugin.getServer().getScheduler().runTaskTimer(plugin, AreaGenerator::generator, 0, 2);
    }

    private static class AreaTask {
        ArrayList<int[]> chunkPos;
        LayerData layerData;
        Biome biome;
        Callback<Void, Integer> every;

        public AreaTask(
                ArrayList<int[]> chunkPos,
                LayerData layerData,
                Biome biome,
                Callback<Void, Integer> every
        ) {
            this.chunkPos = chunkPos;
            this.layerData = layerData;
            this.biome = biome;
            this.every = every;
        }
    }

    public static void add(
            Area area,
            Callback<Void, Void> start,
            Callback<Void, Integer> every,
            Callback<Void, Void> finish
    ) {
        ArrayList<int[]> chunkPos = new ArrayList<>();
        int[] pos = area.getPos();
        for (int x = 0; x < Area.innerSize; x++) {
            for (int z = 0; z < Area.innerSize; z++) {
                chunkPos.add(new int[]{pos[0]+x, pos[1]+z});
            }
        }
//        HashMap<String, Object> areaData = new HashMap<>();
        AreaTask areaTask = new AreaTask(chunkPos, area.getLayer(), area.getBiome(), (unfinishedCount)-> {
            every.call(Area.total - unfinishedCount);
            if (unfinishedCount == 0) finish.call(null);
            return null;
        });
        taskQueue.add(areaTask);
        start.call(null);
        start();
    }

    private static void generator() {
        if (taskQueue.isEmpty()) {
            runTaskTimer.cancel();
            runTaskTimer = null;
            return;
        }
        if (taskIndex >= taskQueue.size()) {
            taskIndex = 0;
        }
        AreaTask areaTask = taskQueue.get(taskIndex);
        if (areaTask.chunkPos.isEmpty()) {
            taskQueue.remove(taskIndex);
            generator();
            return;
        }
        int[] chunkPos = areaTask.chunkPos.getLast();
        areaTask.chunkPos.removeLast();
        generateChunk(chunkPos, areaTask.layerData, areaTask.biome, (unused)->{
            areaTask.every.call(areaTask.chunkPos.size());
            return null;
        });
        taskIndex++;
    }

    private static void generateChunk(
            int[] chunkPos,
            LayerData layerData,
            Biome biome,
            Callback<Void, Void> finish
    ) {
        Chunk chunk = plugin.getServer().getWorld("world").getChunkAt(chunkPos[0], chunkPos[1]);
        int baseHeight = layerData.baseHeight - 64;

        for (LayerData.Layer layer : layerData.layerData) {
            for (int y = 0; y < layer.height; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        chunk.getBlock(x, baseHeight + y, z).setType(layer.id);
                    }
                }
            }
            baseHeight += layer.height;

        }
        for (int y = -64; y < 319; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.getBlock(x, y, z).setBiome(biome);
                }
            }
        }
        chunk = null;
        finish.call(null);
    }
}
