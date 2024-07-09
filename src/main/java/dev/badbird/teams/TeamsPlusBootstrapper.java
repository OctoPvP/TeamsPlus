package dev.badbird.teams;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class TeamsPlusBootstrapper implements PluginBootstrap {
    // private PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager;

    @Override
    public void bootstrap(@NotNull BootstrapContext ctx) {
        System.out.println("Done bootstrapping");
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new TeamsPlus();
    }
}
