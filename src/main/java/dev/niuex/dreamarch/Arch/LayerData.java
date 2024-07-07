package dev.niuex.dreamarch.Arch;

import org.bukkit.Material;

import java.util.Arrays;

public class LayerData {
    public final int baseHeight;
    public final Layer[] layerData;

    public LayerData(String layerMetaData) {
        baseHeight = Integer.parseInt(layerMetaData.split(";")[0]);
        layerData = Arrays.stream(layerMetaData.split(";")[1].split(",")).map(Layer::new).toArray(Layer[]::new);
    }

    public static class Layer {
        public Material id;
        public int height;

        public Layer(String layer) {
            String[] parts = layer.split("\\*");
            height = Integer.parseInt(parts[0]);
            id = Material.valueOf(parts[1].toUpperCase());
        }
    }
}
