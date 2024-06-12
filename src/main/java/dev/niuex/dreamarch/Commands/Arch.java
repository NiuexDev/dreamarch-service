package dev.niuex.dreamarch.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.niuex.dreamarch.Areas.AreaList;
import dev.niuex.dreamarch.DreamArch;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import com.mojang.brigadier.context.CommandContext;
import dev.niuex.dreamarch.Areas.Area;

import java.util.List;
import java.util.UUID;

/**
 *
 * arch
 *  list
 *  create
 *  tp
 *  set
 *    name
 *      attribute
 *          value
 *
 * */

public class Arch {

    private static final DreamArch plugin = DreamArch.instance;

    public static void init() {
        LiteralCommandNode<CommandSourceStack> createCommand = Commands.literal("create")
                .executes(ctx -> {
                    isPlayer(ctx);
                    Area area = new Area();
                    area.setOwner(ctx.getSource().getExecutor().getName());
                    area.setOwnerUuid(ctx.getSource().getExecutor().getUniqueId());
                    ctx.getSource().getSender().sendPlainMessage("已创建区域 [" + area.id + "] " + area.getName());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("description", StringArgumentType.string())
                                .executes(ctx -> {
                                    isPlayer(ctx);
                                    String name = ctx.getArgument("name", String.class);
                                    String description = ctx.getArgument("description", String.class);
                                    String owner = ctx.getSource().getExecutor().getName();
                                    UUID ownerUuid = ctx.getSource().getExecutor().getUniqueId();
                                    Area area = new Area();
                                    area.setName(name);
                                    area.setDescription(description);
                                    area.setOwner(owner);
                                    area.setOwnerUuid(ownerUuid);
                                    ctx.getSource().getSender().sendPlainMessage("已创建区域 [" + area.id + "] " + area.getName());
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(Commands.argument("layer", StringArgumentType.string())
                                        .then(Commands.argument("biome", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    isPlayer(ctx);
                                                    String name = ctx.getArgument("name", String.class);
                                                    String description = ctx.getArgument("description", String.class);
                                                    String owner = ctx.getSource().getExecutor().getName();
                                                    UUID ownerUuid = ctx.getSource().getExecutor().getUniqueId();
                                                    Area area = new Area();
                                                    area.setName(name);
                                                    area.setDescription(description);
                                                    area.setOwner(owner);
                                                    area.setOwnerUuid(ownerUuid);
                                                    area.setLayer(ctx.getArgument("layer", String.class));
                                                    try {
                                                        area.setBiome(Biome.valueOf(ctx.getArgument("biome", String.class).toUpperCase()));
                                                    } catch (Exception e) {
                                                        throw new CommandSyntaxException(null, () -> "群系错误。");
                                                    }
                                                    ctx.getSource().getSender().sendPlainMessage("已创建区域 [" + area.id + "] " + area.getName());
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                                .then(Commands.argument("time", ArgumentTypes.time())
                                                        .then(Commands.argument("weather", StringArgumentType.string())
                                                                .executes(ctx -> {
                                                                    isPlayer(ctx);
                                                                    String name = ctx.getArgument("name", String.class);
                                                                    String description = ctx.getArgument("description", String.class);
                                                                    String owner = ctx.getSource().getExecutor().getName();
                                                                    UUID ownerUuid = ctx.getSource().getExecutor().getUniqueId();
                                                                    Area area = new Area();
                                                                    area.setName(name);
                                                                    area.setDescription(description);
                                                                    area.setOwner(owner);
                                                                    area.setOwnerUuid(ownerUuid);
                                                                    area.setLayer(ctx.getArgument("layer", String.class));
                                                                    try {
                                                                        area.setBiome(Biome.valueOf(ctx.getArgument("biome", String.class).toUpperCase()));
                                                                    } catch (Exception e) {
                                                                        throw new CommandSyntaxException(null, () -> "群系错误。");
                                                                    }
                                                                    area.setTime(ctx.getArgument("time", Integer.class));
                                                                    try {
                                                                        area.setWeather(WeatherType.valueOf(ctx.getArgument("weather", String.class).toUpperCase()));
                                                                    } catch (Exception e) {
                                                                        throw new CommandSyntaxException(null, () -> "天气错误。");
                                                                    }
                                                                    ctx.getSource().getSender().sendPlainMessage("已创建区域 [" + area.id + "] " + area.getName());
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                                .build()
                                                        )
                                                        .build()
                                                )
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

        LiteralCommandNode<CommandSourceStack> listCommand = Commands.literal("list")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendPlainMessage("已加载" + AreaList.getCount() + "个区域。");
                    AreaList.getAreaList().forEach(area -> ctx.getSource().getSender().sendPlainMessage("[" + area.id + "] " + area.getName()));
                    return Command.SINGLE_SUCCESS;
                })
                .build();

        LiteralCommandNode<CommandSourceStack> initCOmmand = Commands.literal("init")
                .then(Commands.argument("id", IntegerArgumentType.integer())
                        .executes(ctx -> {
                            Area area = AreaList.getArea(ctx.getArgument("id", Integer.class));
                            if (area == null) {
                                throw new CommandSyntaxException(null, () -> "区域不存在。");
                            }
                            area.init(
                                    (unused) -> {
                                        ctx.getSource().getSender().sendPlainMessage("开始初始化区域 [" + area.id + "] " + area.getName() + " 。");
                                        return null;
                                    },
                                    (n) -> {
                                        double rate = (double) n / ((Area.size - 1) * (Area.size - 1));
                                        int progressChars = (int) Math.round(rate * 17);
                                        StringBuilder progress = new StringBuilder();
                                        for (int i = 0; i < 17; i++) {
                                            progress.append(i < progressChars ? "=" : "-");
                                        }
                                        Audience.audience(ctx.getSource().getSender()).sendActionBar(Component.text("正在初始化 [" + progress + "]  " + (int) (rate*100) + "%"));
                                        return null;
                                    },
                                    (unused) -> {
                                        ctx.getSource().getSender().sendPlainMessage("[" + area.id + "] " + area.getName() + "已初始化。");
                                        return null;
                                    }
                            );
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();

        LiteralCommandNode<CommandSourceStack> tpCommand = Commands.literal("tp")
                .then(Commands.argument("id", IntegerArgumentType.integer())
                        .executes(ctx -> {
                            isPlayer(ctx);
                            Area area = AreaList.getArea(ctx.getArgument("id", Integer.class));
                            if (area == null) {
                                throw new CommandSyntaxException(null, () -> "区域不存在。");
                            }
                            if (!area.isInit()) {
                                throw new CommandSyntaxException(null, () -> "区域未初始化。");
                            }
                            int[] pos = area.getCenterPos();
                            ctx.getSource().getExecutor().teleportAsync(new Location(
                                    ctx.getSource().getExecutor().getWorld(),
                                    pos[0],
                                    ctx.getSource().getExecutor().getWorld().getHighestBlockYAt(pos[0], pos[1])+1,
                                    pos[1]
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();

        LiteralCommandNode<CommandSourceStack> command = Commands.literal("arch")
                .then(createCommand)
                .then(listCommand)
                .then(initCOmmand)
                .then(tpCommand)
                .build();

        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(command, "创建和管理建筑", List.of("architecture", "建筑", "jz"));
        });
    }

    private static void isPlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        if (ctx.getSource().getExecutor() == null || ctx.getSource().getExecutor().getType() != EntityType.PLAYER) {
            throw new CommandSyntaxException(null, () -> "命令应该由玩家执行");
        }
    }
}