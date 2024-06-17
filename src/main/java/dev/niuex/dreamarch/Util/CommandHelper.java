package dev.niuex.dreamarch.Util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.niuex.dreamarch.Arch.Area;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.EntityType;

public class CommandHelper {
    public static void isPlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getExecutor() == null || ctx.getSource().getExecutor().getType() != EntityType.PLAYER) {
            throw new CommandSyntaxException(null, () -> "命令应该由玩家执行");
        }
    }

    public static void existArea(Area area) throws CommandSyntaxException {
        existArea(area, "建筑区域不存在。");
    }

    public static void existArea(Area area, String message) throws CommandSyntaxException {
        if (area == null) {
            throw new CommandSyntaxException(null, () -> message);
        }
    }
}
