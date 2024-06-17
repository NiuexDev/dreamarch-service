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
import dev.niuex.dreamarch.Util.CommandHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
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
                    .then(listCommand)
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
            .requires(cs -> cs.getSender().hasPermission("dreamarch.command.vote.admin"))
            .then(Commands.literal("save")
                    .executes(ctx -> {
                        try {
                            Vote.save();
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


    private static final ArgumentCommandNode<CommandSourceStack, Integer> voteCommand = Commands.argument("score", IntegerArgumentType.integer(1))
            .executes(ctx -> {
                CommandHelper.isPlayer(ctx);
                Area area = AreaList.getArea(PlayerArea.getTempId(ctx.getSource().getExecutor().getChunk()));
                CommandHelper.existArea(area, "您未处在建筑区域内，请前往一个建筑区域。");
                int score = ctx.getArgument("score", Integer.class);
                Vote.vote(area.id, ctx.getSource().getExecutor().getUniqueId(), score);
                ctx.getSource().getSender().sendPlainMessage("已评分。（" + score + "）");
                return Command.SINGLE_SUCCESS;
            })
//            .then(Commands.argument("score", IntegerArgumentType.integer(0,10))
//                    .executes(ctx -> {
//                        CommandHelper.isPlayer(ctx);
//                        int id = ctx.getArgument("id", Integer.class);
//                        Area area = AreaList.getArea(id);
//                        CommandHelper.existArea(area);
//                        int score = ctx.getArgument("score", Integer.class);
//                        Vote.vote(id, ctx.getSource().getExecutor().getUniqueId(), score);
//                        ctx.getSource().getExecutor().sendPlainMessage(id + ", " + score);
//                        return Command.SINGLE_SUCCESS;
//                    })
//                    .build()
//            )
            .build();

    private static int voteCommandWithNoArgs(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandHelper.isPlayer(ctx);
        Area area = AreaList.getArea(PlayerArea.getTempId(ctx.getSource().getExecutor().getChunk()));
        CommandHelper.existArea(area, "您未处在建筑区域内，请前往一个建筑区域。");
        ctx.getSource().getSender().sendPlainMessage("点这里打分");
        return Command.SINGLE_SUCCESS;
    }
}
