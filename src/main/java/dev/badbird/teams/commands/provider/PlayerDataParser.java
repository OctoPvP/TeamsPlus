package dev.badbird.teams.commands.provider;

import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.object.PlayerData;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;

@RequiredArgsConstructor
public class PlayerDataParser implements ArgumentParser<CommandSender, PlayerData>, BlockingSuggestionProvider<CommandSender> {
    private final boolean allowOffline;

    @Override
    public @NonNull ArgumentParseResult<PlayerData> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.hasRemainingInput()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(commandInput.readString());
            if (player.isOnline()) {
                return ArgumentParseResult.success(PlayerManager.getData(player.getUniqueId()));
            }
            if (allowOffline) {
                return ArgumentParseResult.success(PlayerManager.getDataLoadIfNeedTo(player.getUniqueId()));
            }
            return ArgumentParseResult.failure(new RuntimeException("Player not found"));
        }
        return ArgumentParseResult.failure(new RuntimeException("No player name provided"));
    }

    @Override
    public @NonNull Iterable<? extends @NonNull Suggestion> suggestions(@NonNull CommandContext<CommandSender> context, @NonNull CommandInput input) {
        return new PlayerParser<CommandSender>().suggestions(context, input);
    }
}
