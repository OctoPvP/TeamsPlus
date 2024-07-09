package dev.badbird.teams.commands.provider;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.util.annotation.AnnotationAccessor;

public class CommandSenderInjector implements ParameterInjector<CommandSourceStack, CommandSender> {
    @Override
    public @Nullable CommandSender create(@NonNull CommandContext<CommandSourceStack> context, @NonNull AnnotationAccessor annotationAccessor) {
        return context.sender().getSender();
    }
}
