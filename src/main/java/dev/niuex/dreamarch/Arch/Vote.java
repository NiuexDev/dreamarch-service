package dev.niuex.dreamarch.Arch;

import dev.niuex.dreamarch.DreamArch;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class Vote {

    private static final DreamArch plugin = DreamArch.instance;

    private static List<VoteRecord> voteList;
    private static String fileName;

    public static void init() {
        voteList = new ArrayList<>();
        fileName = "";

        File areaFolder = new File(plugin.getDataFolder(), "Area");
        if (!areaFolder.exists() || !areaFolder.isDirectory()) {
            areaFolder.mkdir();
        }
    }

    public static void vote(int arch, UUID player, int score) {
        VoteRecord record = find(arch, player);

        // 如果记录存在，则更新分数；否则，添加新记录
        if (record != null) {
            record.score = score;
        } else {
            voteList.add(new VoteRecord(arch, player, score));
        }
    }

    private static VoteRecord find(int arch, UUID player) {
        Optional<VoteRecord> result = voteList.stream()
                .filter(record -> record.arch == arch && record.player.equals(player)) // 使用 equals 比较 UUID
                .findFirst();
        return result.orElse(null);
    }

    /**
     * 帮我写出方法实现：
     * 查询同投票者的所有投票记录
     * 查询某建筑的所有所有投票记录
     * */

//    public static List<VoteRecord> getPlayerVoteRecords(UUID player) {
//        return voteList.stream()
//                .filter(record -> record.player.equals(player))
//                .toList();
//    }
//
//    public static List<VoteRecord> getArchVoteRecords(int arch) {
//        return voteList.stream()
//                .filter(record -> record.arch == arch)
//                .toList();
//    }


    /**
     * 帮我写出方法实现：
     * 保存voteList至/vote/下某指定yml文件
     * 从某yml加载voteList
     * */

    public static void save() throws VoteException {
        if (fileName.isEmpty()) {

            throw new VoteException("未设置储存文件名，无法保存。请先指定保存文件名。");
        }
        save(fileName);
    }

    public static void save(String saveFileName) throws VoteException {
        File voteFile = new File(plugin.getDataFolder(), "Vote/" + saveFileName + ".yml");
        YamlConfiguration voteConfig = YamlConfiguration.loadConfiguration(voteFile);
        voteConfig.set("vote", voteList);
        try {
            voteConfig.save(voteFile);
            fileName = saveFileName;
        } catch (Throwable e) {
            plugin.logger.severe("无法写入vote文件：" + e.getMessage() + "。\n" + Arrays.toString(e.getStackTrace()));
            throw new VoteException("无法写入vote文件：" + e.getMessage());
        }
    }

    public static void saveAndCLear(String fileName) throws VoteException {
        save(fileName);
        voteList.clear();
    }

    public static void load(String loadFileName) throws VoteException {
        if (!voteList.isEmpty() && fileName.isEmpty()) {
            throw new VoteException("投票列表已有数据，且未设置储存文件名，无法自动保存。请先保存数据，而后加载。无法加载！");
        } else if (!voteList.isEmpty()) {
            saveAndCLear(fileName);
        }

        File voteFile = new File(plugin.getDataFolder(), "Vote/" + loadFileName + ".yml");
        if (voteFile.exists()) {
            plugin.logger.severe("vote文件不存在");
            return;
        };
        try {
            YamlConfiguration voteConfig = YamlConfiguration.loadConfiguration(voteFile);
            if (voteConfig.get("vote") == null) {
                plugin.logger.severe("vote文件不存在");
                return;
            }
            voteList = (List<VoteRecord>) voteConfig.get("vote");
            fileName = loadFileName;
        } catch (Throwable e) {
            plugin.logger.severe("无法读取vote文件：" + e.getMessage() + "。\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static class VoteException extends Exception {
        public VoteException(String message) {
            super(message);
        }
    }


static class VoteRecord {
        public final int arch;
        public final UUID player;
        public int score;

        private VoteRecord(int arch, UUID player, int score) {
            this.arch = arch;
            this.player = player;
            this.score = score;
        }
    }
}
