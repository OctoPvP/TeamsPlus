package dev.badbird.teams.commands.provider;

import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.object.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.util.annotation.AnnotationAccessor;

public class PlayerDataInjector implements ParameterInjector<CommandSender, PlayerData> {


    @Override
    public @Nullable PlayerData create(@NonNull CommandContext<CommandSender> context, @NonNull AnnotationAccessor annotationAccessor) {
        CommandSender sender = context.sender();
        if (sender instanceof Player player) {
            if (annotationAccessor.annotation(Sender.class) != null) {
                System.out.println("Getting player data from player");
                return PlayerManager.getData(player.getUniqueId());
            }
            System.out.println("Returning null");
            return null;
        }
        throw new RuntimeException("Must be a player.");
    }
    /*
    @Override
    public PlayerData provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (context.getCommandInfo().getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            return PlayerManager.getData((UUID) context.getCommandSender().getIdentifier());
        } else {
            String arg = args.peek();
            if (arg == null) {
                if (parameterInfo.getParameter().isAnnotationPresent(DefaultSelf.class)) {
                    return PlayerManager.getData((UUID) context.getCommandSender().getIdentifier());
                }
                throw new CommandException(Lang.PLAYER_NOT_FOUND.toString());
            }
            Player player = Bukkit.getPlayer(args.pop());
            if (player == null) {
                if (parameterInfo.getParameter().isAnnotationPresent(AllowOffline.class)) {
                    return PlayerManager.getDataLoadIfNeedTo(arg);
                }
                throw new CommandException(Lang.PLAYER_NOT_FOUND.toString());
            }
            return PlayerManager.getData(player.getUniqueId());
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return Arrays.asList(); // FIXME
        // return CommandManager.getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }
     */
}
