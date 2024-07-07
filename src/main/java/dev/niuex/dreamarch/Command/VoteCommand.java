package dev.niuex.dreamarch.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.niuex.dreamarch.Arch.Area;
import dev.niuex.dreamarch.Arch.AreaList;
import dev.niuex.dreamarch.Arch.PlayerArea;
import dev.niuex.dreamarch.Arch.Vote;
import dev.niuex.dreamarch.DreamArch;
import dev.niuex.dreamarch.TextTemplate.VoteTemplate;
import dev.niuex.dreamarch.Util.CommandHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class VoteCommand {

    private static final DreamArch plugin = DreamArch.instance;
    public static void init () {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            LiteralCommandNode<CommandSourceStack> command = Commands.literal("vote")
                    .then(voteCommand)
                    .executes(VoteCommand::voteCommandWithNoArgs)
                    .then(adminCommand)
                    .build();

            commands.register(command, "给建筑打分", List.of("打分"));
        });
    }

    private static final LiteralCommandNode<CommandSourceStack> listCommand = Commands.literal("list")
            .executes(ctx -> {
                CommandHelper.isPlayer(ctx);
                ctx.getSource().getExecutor().sendPlainMessage(AreaList.getAreaList().toString());
                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("id", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        CommandHelper.isPlayer(ctx);
                        int id = ctx.getArgument("id", Integer.class);
                        ctx.getSource().getExecutor().sendPlainMessage(AreaList.getArea(id).toString());
                        return Command.SINGLE_SUCCESS;
                    })
                    .build()
            )
            .build();

    private static final LiteralCommandNode<CommandSourceStack> adminCommand = Commands.literal("admin")
            .requires(ctx -> ctx.getSender().hasPermission("dreamarch.command.vote.admin"))
            .then(Commands.literal("save")
                    .executes(ctx -> {
                        try {
                            Vote.save("latest");
                        } catch (Vote.VoteException e) {
                            throw new CommandSyntaxException(null, e::getMessage);
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("filename", StringArgumentType.string())
                            .executes(ctx -> {
                                try {
                                    Vote.save(ctx.getArgument("filename", String.class));
                                } catch (Vote.VoteException e) {
                                    throw new CommandSyntaxException(null, e::getMessage);
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                            .build()
                    )
                    .build()
            )
            .then(Commands.literal("load")
                    .then(Commands.argument("filename", StringArgumentType.string())
                            .executes(ctx -> {
                                try {
                                    Vote.load(ctx.getArgument("filename", String.class));
                                } catch (Vote.VoteException e) {
                                    throw new CommandSyntaxException(null, e::getMessage);
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                            .build()
                    )
                    .build()
            )
            .build();


    private static final ArgumentCommandNode<CommandSourceStack, Integer> voteCommand = Commands.argument("n1", IntegerArgumentType.integer(1))
            .executes(ctx -> {
                CommandHelper.isPlayer(ctx);
                 Area area = AreaList.getArea(PlayerArea.getTempId(ctx.getSource().getExecutor().getChunk()));
                CommandHelper.existArea(area, "您未处在建筑区域内，请前往一个建筑区域。");
                int score = ctx.getArgument("n1", Integer.class);
                if (score < 0 || score > 10) {
                    throw new CommandSyntaxException(null, () -> "评分须在0-10分之间。");
                }
                Vote.vote(area.id, ctx.getSource().getExecutor().getUniqueId(), score);
                ctx.getSource().getSender().sendPlainMessage("已为 ["+area.id+"]"+area.getName()+" 评"+score + "分。");
                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("n2", IntegerArgumentType.integer(0,10))
                    .executes(ctx -> {
                        CommandHelper.isPlayer(ctx);
                        Area area = AreaList.getArea(ctx.getArgument("n1", Integer.class));
                        CommandHelper.existArea(area, "建筑不存在。");
                        int score = ctx.getArgument("n2", Integer.class);
                        Vote.vote(area.id, ctx.getSource().getExecutor().getUniqueId(), score);
                        ctx.getSource().getSender().sendPlainMessage("已为 ["+area.id+"]"+area.getName()+" 评"+score + "分。");
                        return Command.SINGLE_SUCCESS;
                    })
                    .build()
            )
            .then(Commands.literal("gui")
                    .executes(ctx -> {
                        CommandHelper.isPlayer(ctx);
                        Area area = AreaList.getArea(ctx.getArgument("n1", Integer.class));
                        CommandHelper.existArea(area, "建筑不存在。");

                        Audience.audience(ctx.getSource().getSender()).sendMessage(VoteTemplate.render(area));
                        return Command.SINGLE_SUCCESS;
                    })
                    .build()
            )
            .build();

    private static int voteCommandWithNoArgs(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandHelper.isPlayer(ctx);
        Area area = AreaList.getArea(PlayerArea.getTempId(ctx.getSource().getExecutor().getChunk()));
        CommandHelper.existArea(area, "您未处在建筑区域内，请前往一个建筑区域。");

        Audience.audience(ctx.getSource().getSender()).sendMessage(VoteTemplate.render(area));
        return Command.SINGLE_SUCCESS;
    }
}
