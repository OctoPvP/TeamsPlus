package dev.badbird.teams;

import dev.badbird.teams.commands.annotation.AnnotationMappers;
import dev.badbird.teams.commands.provider.CommandSenderInjector;
import dev.badbird.teams.commands.provider.PlayerDataInjector;
import dev.badbird.teams.commands.provider.TeamParser;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.processors.cooldown.annotation.CooldownBuilderModifier;
import org.incendo.cloud.setting.ManagerSetting;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
public class TeamsPlusBootstrapper implements PluginBootstrap {
    private PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager;
    @Override
    public void bootstrap(@NotNull BootstrapContext ctx) {
        try {
            final PaperCommandManager.Bootstrapped<CommandSourceStack> mgr = PaperCommandManager.builder()
                    .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
                    .buildBootstrapped(ctx);

            this.commandManager = mgr;
            mgr.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
            mgr.parserRegistry()
                            .registerParser(LocationParser.locationParser());
            /*mgr.parameterInjectorRegistry()
                    .registerInjector(Team.class, new TeamParser())
                    .registerInjector(CommandSender.class, new CommandSenderInjector())
                    .registerInjector(PlayerData.class, new PlayerDataInjector());*/

            MinecraftExceptionHandler.create(CommandSourceStack::getSender)
                    .defaultInvalidSyntaxHandler().defaultInvalidSenderHandler().defaultNoPermissionHandler().defaultArgumentParsingHandler()
                    .defaultCommandExecutionHandler().decorator(component -> text().append(text("[", NamedTextColor.DARK_GRAY))
                            .append(text("TeamsPlus", NamedTextColor.GOLD)).append(text("] ", NamedTextColor.DARK_GRAY))
                            .append(component).build()
                    ).registerTo(commandManager);

            AnnotationParser<CommandSourceStack> annotationParser = new AnnotationParser<>(commandManager, CommandSourceStack.class);
            // AnnotationMappers.register(annotationParser, mgr);
            // CooldownBuilderModifier.install(annotationParser);

            /*MinecraftHelp<CommandSourceStack> help = MinecraftHelp.<CommandSourceStack>builder()
                    .commandManager(commandManager)
                    .audienceProvider(CommandSourceStack::getSender)
                    .commandPrefix("/teamsplus")
                    .colors(MinecraftHelp.helpColors(NamedTextColor.AQUA, NamedTextColor.GOLD, NamedTextColor.GREEN, NamedTextColor.WHITE, NamedTextColor.GRAY))
                    .build();
            commandManager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());

            System.out.println("Registering commands");
            Class<?>[] classes = {
                    // TestCmd.class,
                    // LCTest.class,
                    // LCUtils.class,
                    // ClaimCommand.class,
                    // ChunkBorderCommand.class,
                    // ChatCommands.class,
                    // TeamsStaffCommand.class,
                    // TeamsCommand.class,
                    // TeamMemberManagement.class,
                    // TeamRelationsCommand.class,
                    // WaypointCommands.class,
                    TeamParser.class,
            };
            for (Class<?> clazz : classes) {//new Reflections("dev.badbird.teams.commands.impl").getSubTypesOf(Object.class)) {
                System.out.println("Registering: " + clazz.getName());
                Object obj = null;
                try {
                    obj = clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    e.printStackTrace();
                    continue;
                }
                annotationParser.parse(obj);
            }
             */
            annotationParser.parseContainers();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("Done bootstrapping");
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new TeamsPlus(this.commandManager);
    }
}
