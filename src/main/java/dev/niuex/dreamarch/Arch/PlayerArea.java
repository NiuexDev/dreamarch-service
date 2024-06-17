package dev.niuex.dreamarch.Arch;

import dev.niuex.dreamarch.DreamArch;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.time.Duration;

public class PlayerArea {

    private static final DreamArch plugin = DreamArch.instance;

    public static void Check(Player player) {
        Check(player, getTempId(player.getChunk()));
    }

    public static void Check(Player player, int id) {
        if (id == 0) {
            PlayerArea.Leave(player);
            return;
        }

        Area area = AreaList.getArea(id);
        if (area == null) {
            PlayerArea.Leave(player);
            return;
        }

        PlayerArea.Enter(player, area);
    }

    public static void Enter(Player player, Area area) {
        Audience.audience(player).showTitle(Title.title(
                Component
                        .text(area.getName() == null ? "建筑" + area.id : area.getName())
                        .color(TextColor.color(0xeeeeee))
                        .decoration(TextDecoration.BOLD, true),
                MiniMessage.miniMessage().deserialize("<gradient:#eeddee:#eeffee>" + "作者: " + area.getOwner() + "</gradient>"),
                Title.Times.times(
                        Duration.ofMillis(300),
                        Duration.ofMillis(2300),
                        Duration.ofMillis(700)
                )
        ));
        setPlayerTimeWeather(player, area);
    }

    public static void Leave(Player player) {
        resetPlayerTimeWeather(player);
    }

    public static void setPlayerTimeWeather(Player player, Area area) {
        player.setPlayerTime(area.getTime(), false);
        player.setPlayerWeather(area.getWeather());
    }

    public static void resetPlayerTimeWeather(Player player) {
        player.resetPlayerTime();
        player.resetPlayerWeather();
    }

    public static int getTempId(Chunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();
        if ( x<0 || z<0 || z>Area.size-2 || x % Area.size == Area.size-1) return 0;
        return x/Area.size;
    }


}
