package dev.niuex.dreamarch.Arch;

import dev.niuex.dreamarch.DreamArch;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.*;

public class Vote {

    private static final DreamArch plugin = DreamArch.instance;

    private static ArrayList<HashMap<String, Object>> voteList;

    public static void init() {
        voteList = new ArrayList<>();

        File areaFolder = new File(plugin.getDataFolder(), "Area");
        if (!areaFolder.exists() || !areaFolder.isDirectory()) {
            areaFolder.mkdir();
        }

        File latestFile = new File(areaFolder, "latest.yml");
        if (latestFile.exists()) {
            try {
                load("latest");
            } catch (VoteException e) {
                plugin.logger.info(e.getMessage());
            }
        }
    }

    public static void vote(int arch, UUID player, int score) {
        HashMap<String, Object> record = find(arch, player);

        // 如果记录存在，则更新分数；否则，添加新记录
        if (record != null) {
            record.replace("score", score);
        } else {
            record = new LinkedHashMap<>();
            record.put("arch", arch);
            record.put("uuid", player.toString());
            record.put("score", score);
            voteList.add(record);
        }
    }

    private static HashMap<String, Object> find(int arch, UUID uuid) {
        Optional<HashMap<String, Object>> result = voteList.stream()
//                .filter(it -> it.get("arch") != null)
//                .filter(it -> it.get("arch") instanceof Integer)
                .filter(record ->
                        (Integer) record.get("arch") == arch && record.get("uuid").equals(uuid.toString())
                ).findFirst();
        return result.orElse(null);
    }

//    public static AreaInfoTemplate<HashMap<String, Object>> getPlayerVoteRecords(UUID uuid) {
//        return voteList.stream()
//                .filter(record -> record.get("uuid").equals(uuid))
//                .toList();
//    }
//
//    public static AreaInfoTemplate<HashMap<String, Object>> getArchVoteRecords(int arch) {
//        return voteList.stream()
//                .filter(record -> record.get("arch").equals(arch))
//                .toList();
//    }

    public static void save() {
        try {
            save("latest");
        } catch (VoteException e) {
            plugin.logger.info(e.getMessage());
        }
    }

    public static void save(String saveFileName) throws VoteException {
        File voteFile = new File(plugin.getDataFolder(), "Vote/" + saveFileName + ".yml");
        YamlConfiguration voteConfig = YamlConfiguration.loadConfiguration(voteFile);
        voteConfig.set("vote", voteList);
        try {
            voteConfig.save(voteFile);
        } catch (Throwable e) {
            plugin.logger.severe("无法写入vote文件：" + e.getMessage() + "。\n" + Arrays.toString(e.getStackTrace()));
            throw new VoteException("无法写入vote文件：" + e.getMessage());
        }
    }


    public static void load(String loadFileName) throws VoteException {

        File voteFile = new File(plugin.getDataFolder(), "Vote/" + loadFileName + ".yml");
        if (!voteFile.exists()) {
            throw new VoteException("vote文件不存在");
        }
        try {
            YamlConfiguration voteConfig = YamlConfiguration.loadConfiguration(voteFile);
            if (voteConfig.get("vote") == null) {
                throw new VoteException("vote文件不存在");
            }
            voteList = (ArrayList<HashMap<String, Object>>) voteConfig.get("vote");
        } catch (Throwable e) {
            throw new VoteException("无法读取vote文件：" + e.getMessage() + "。\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static class VoteException extends Exception {
        public VoteException(String message) {
            super(message);
        }
    }

}
