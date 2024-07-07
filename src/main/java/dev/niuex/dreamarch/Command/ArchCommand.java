package dev.niuex.dreamarch.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.niuex.dreamarch.Arch.AreaList;
import dev.niuex.dreamarch.Arch.PlayerArea;
import dev.niuex.dreamarch.DreamArch;
import dev.niuex.dreamarch.TextTemplate.AreaInfoTemplate;
import dev.niuex.dreamarch.TextTemplate.AreaListTemplate;
import dev.niuex.dreamarch.TextTemplate.PagingTemplate;
import dev.niuex.dreamarch.Util.CommandHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import com.mojang.brigadier.context.CommandContext;
import dev.niuex.dreamarch.Arch.Area;

import java.util.List;
import java.util.UUID;

import static dev.niuex.dreamarch.Arch.PlayerArea.getTempId;
import static dev.niuex.dreamarch.Arch.PlayerArea.setPlayerTimeWeather;

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

@SuppressWarnings("UnstableApiUsage")
public class ArchCommand {

    private static final DreamArch plugin = DreamArch.instance;

    public static void init() {

        LiteralCommandNode<CommandSourceStack> command = Commands.literal("arch")
                .then(createCommand)
                .then(listCommand)
                .then(initCommand)
                .then(tpCommand)
                .then(setCommand)
                .build();

        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(command, "创建和管理建筑", List.of("architecture", "建筑", "jz"));
        });
    }

    private static final LiteralCommandNode<CommandSourceStack> listCommand = Commands.literal("list")
            .then(Commands.argument("id", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        Area area = AreaList.getArea(ctx.getArgument("id", Integer.class));
                        CommandHelper.existArea(area, "该建筑区域不存在。");
                        Audience.audience(ctx.getSource().getSender()).sendMessage(AreaInfoTemplate.render(area));
                        return Command.SINGLE_SUCCESS;
                    })
            )
            .executes(ctx -> listCommandRunner(ctx, 1))
            .then(Commands.literal("page").then(Commands.argument("page", IntegerArgumentType.integer(1))
                    .executes(ctx -> listCommandRunner(ctx, ctx.getArgument("page", Integer.class)))
            ))
            .build();

    private static final LiteralCommandNode<CommandSourceStack> initCommand = Commands.literal("init")
            .then(Commands.argument("id", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        CommandHelper.isPlayer(ctx);
                        Area area = AreaList.getArea(ctx.getArgument("id", Integer.class));
                        CommandHelper.existArea(area);
                        hasPermission(ctx, area);
//                        Audience echo = Audience.audience(ctx.getSource().getSender());
                        UUID uuid = ctx.getSource().getExecutor().getUniqueId();
                        area.init(
                                (unused) -> {
                                    Audience.audience(plugin.getServer().getPlayer(uuid)).sendMessage(Component.text("开始初始化建筑区域 [" + area.id + "]" + area.getName() + " 。"));
                                    return null;
                                },
                                (n) -> {
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                        if (plugin.getServer().getPlayer(uuid) == null) return;
                                        double rate = (double) n / Area.total;
                                        int progressChars = (int) Math.round(rate * 17);
                                        StringBuilder progress = new StringBuilder();
                                        for (int i = 0; i < 17; i++) {
                                            progress.append(i < progressChars ? "=" : "-");
                                        }
                                        Audience.audience(plugin.getServer().getPlayer(uuid)).sendActionBar(Component.text("正在初始化 [" + progress + "]  " + (int) (rate * 100) + "%"));
                                    });
                                    return null;
                                },
                                (unused) -> {
                                    Audience.audience(plugin.getServer().getPlayer(uuid)).sendMessage(Component.text("[" + area.id + "]" + area.getName() + "已初始化。"));
                                    return null;
                                }
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                    .build()
            )
            .build();

    private static final LiteralCommandNode<CommandSourceStack> tpCommand = Commands.literal("tp")
            .then(Commands.argument("id", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        CommandHelper.isPlayer(ctx);
                        Area area = AreaList.getArea(ctx.getArgument("id", Integer.class));
                        CommandHelper.existArea(area);
                        if (!area.isInit()) {
                            throw new CommandSyntaxException(null, () -> "该建筑区域未初始化。");
                        }
                        Player player = plugin.getServer().getPlayer(ctx.getSource().getExecutor().getUniqueId());
                        Location location = area.getSpawnLocation();
                        location.setPitch(player.getPitch());
                        location.setYaw(player.getYaw());
                        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH);
                        PlayerArea.Enter(player, area);
                        return Command.SINGLE_SUCCESS;
                    })
                    .build()
            )
            .build();

    private static final LiteralCommandNode<CommandSourceStack> setCommand = Commands.literal("set")
            .then(Commands.argument("id", IntegerArgumentType.integer(1))
                    .then(Commands.literal("name")
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(ctx -> {
                                        Area area = getArea(ctx);
                                        hasPermission(ctx, area);
                                        area.setName(ctx.getArgument("value", String.class));
                                        ctx.getSource().getSender().sendPlainMessage("设置成功。");
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .build()
                            )
                            .build()
                    )
                    .then(Commands.literal("description")
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(ctx -> {
                                        Area area = getArea(ctx);
                                        hasPermission(ctx, area);
                                        area.setDescription(ctx.getArgument("value", String.class));
                                        ctx.getSource().getSender().sendPlainMessage("设置成功。");
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .build()
                            )
                            .build()
                    )
                    .then(Commands.literal("layer")
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(ctx -> {
                                        Area area = getArea(ctx);
                                        hasPermission(ctx, area);
                                        area.setLayer(ctx.getArgument("value", String.class));
                                        ctx.getSource().getSender().sendPlainMessage("设置成功。");
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .build()
                            )
                            .build()
                    )
                    .then(Commands.literal("time")
                            .then(Commands.argument("value", ArgumentTypes.time())
                                    .executes(ctx -> {
                                        CommandHelper.isPlayer(ctx);
                                        Area area = getArea(ctx);
                                        hasPermission(ctx, area);
                                        area.setTime(ctx.getArgument("value", Integer.class));
                                        plugin.getServer().getOnlinePlayers().forEach(player -> {
                                            if (getTempId(player.getChunk()) == area.id) {
                                                setPlayerTimeWeather(player, area);
                                            }
                                        });
                                        ctx.getSource().getSender().sendPlainMessage("设置成功。");
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .build()
                            )
                            .build()
                    )
                    .then(Commands.literal("weather")
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(ctx -> {
                                        CommandHelper.isPlayer(ctx);
                                        Area area = getArea(ctx);
                                        hasPermission(ctx, area);
                                        try {
                                            area.setWeather(WeatherType.valueOf(ctx.getArgument("value", String.class).toUpperCase()));
                                            plugin.getServer().getOnlinePlayers().forEach(player -> {
                                                if (getTempId(player.getChunk()) == area.id) {
                                                    setPlayerTimeWeather(player, area);
                                                }
                                            });
                                        } catch (Exception e) {
                                            throw new CommandSyntaxException(null, () -> "天气错误。");
                                        }
                                        ctx.getSource().getSender().sendPlainMessage("设置成功。");
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .build()
                            )
                            .build()
                    )
                    .then(Commands.literal("biome")
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(ctx -> {
                                        Area area = getArea(ctx);
                                        hasPermission(ctx, area);
                                        try {
                                            area.setBiome(Biome.valueOf(ctx.getArgument("value", String.class).toUpperCase()));
                                        } catch (Exception e) {
                                            throw new CommandSyntaxException(null, () -> "群系错误。");
                                        }
                                        ctx.getSource().getSender().sendPlainMessage("设置成功。");
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .build()
                            )
                            .build()
                    )
                    .then(Commands.literal("spawnpos")
                            .executes(ctx -> {
                                CommandHelper.isPlayer(ctx);
                                Location location = ctx.getSource().getExecutor().getLocation();
                                Area area = getArea(ctx);
                                hasPermission(ctx, area);
                                if (getTempId(location.getChunk()) != area.id) {
                                    throw new CommandSyntaxException(null, () -> "不应该设置在建筑区域外。");
                                }
                                area.setSpawnPos(location);
                                ctx.getSource().getSender().sendPlainMessage("设置成功。");
                                return Command.SINGLE_SUCCESS;
                            })
                            .build()
                    )
                    .build()
            )
            .build();

    private static void hasPermission(CommandContext<CommandSourceStack> ctx, Area area) throws CommandSyntaxException {
        if (
                !ctx.getSource().getSender().hasPermission("dreamarch.command.arch.admin") &&
                !area.getOwnerUuid().equals(ctx.getSource().getExecutor().getUniqueId())
        ) {
            throw new CommandSyntaxException(null, () -> "这不是您创建的建筑区域。");
        }
    }

    private static Area getArea(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Area area = AreaList.getArea(ctx.getArgument("id", Integer.class));
        CommandHelper.existArea(area);
        return area;
    }

    private static final LiteralCommandNode<CommandSourceStack> createCommand = Commands.literal("create")
            .executes(ctx -> createCommandRunner(0, ctx))

                    .then(Commands.argument("name", StringArgumentType.string())
                    .then(Commands.argument("description", StringArgumentType.string())
                    .executes(ctx -> createCommandRunner(1, ctx))

                            .then(Commands.argument("layer", StringArgumentType.string())
                            .then(Commands.argument("biome", StringArgumentType.string())
                            .executes(ctx -> createCommandRunner(2, ctx))

                                    .then(Commands.argument("time", ArgumentTypes.time())
                                    .then(Commands.argument("weather", StringArgumentType.string())
                                    .executes(ctx -> createCommandRunner(3, ctx))
                                    .build())
                                    .build())
                            .build())
                            .build())
                    .build())
                    .build())
            .build();

    private static int createCommandRunner(int step, CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandHelper.isPlayer(ctx);
        Area area = new Area();
        Entity executor = ctx.getSource().getExecutor();

        switch (step) {
            case 3:
                area.setTime(ctx.getArgument("time", Integer.class));
                try {
                    area.setWeather(WeatherType.valueOf(ctx.getArgument("weather", String.class).toUpperCase()));
                } catch (Exception e) {
                    ctx.getSource().getSender().sendPlainMessage("天气格式有误，天气未设置成功。");
                }
            case 2:
                area.setLayer(ctx.getArgument("layer", String.class));
                try {
                    area.setBiome(Biome.valueOf(ctx.getArgument("biome", String.class).toUpperCase()));
                } catch (Exception e) {
                    ctx.getSource().getSender().sendPlainMessage("群系格式有误，群系未设置成功。");
                }
            case 1:
                String name = ctx.getArgument("name", String.class);
                String description = ctx.getArgument("description", String.class);
                area.setName(name);
                area.setDescription(description);
            case 0:
                area.setOwner(executor.getName());
                area.setOwnerUuid(executor.getUniqueId());
        }

        ctx.getSource().getExecutor().sendPlainMessage("已创建建筑区域 [" + area.id + "]" + area.getName());
        return Command.SINGLE_SUCCESS;
    }

    private static int listCommandRunner(CommandContext<CommandSourceStack> ctx, int pageNumber) throws CommandSyntaxException {
        int areaListCount = AreaList.getCount();
        int maxPageNumber = (int) Math.ceil((double) areaListCount / 5);
        if (pageNumber > maxPageNumber) {
            throw new CommandSyntaxException(null, () -> "没有第"+pageNumber+"页。");
        }
        Component pageing = PagingTemplate.render(maxPageNumber, pageNumber);
        Component areaListTemplate = AreaListTemplate.render(
                AreaList.getAreaList()
                        .subList(pageNumber*5-5, Math.min(areaListCount, pageNumber*5))
                        .stream()
                        .map(AreaInfoTemplate::render)
                        .toArray(Component[]::new),
                pageing
        );
        Audience.audience(ctx.getSource().getSender()).sendMessage(areaListTemplate);
        return Command.SINGLE_SUCCESS;
    }

}