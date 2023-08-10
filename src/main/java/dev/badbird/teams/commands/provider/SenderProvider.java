package dev.badbird.teams.commands.provider;

import net.badbird5907.blib.command.Sender;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Deque;
import java.util.List;

public class SenderProvider implements Provider<Sender> {

    @Override
    public Sender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return new Sender(((BukkitCommandSender) context.getCommandSender()).getSender());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return null;
    }
}
