package dev.badbird.teams;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.badbird.teams.commands.CommandManager;
import dev.badbird.teams.listeners.CombatListener;
import dev.badbird.teams.listeners.MessageListener;
import dev.badbird.teams.listeners.SessionListener;
import dev.badbird.teams.manager.HookManager;
import dev.badbird.teams.manager.StorageManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.manager.WaypointManager;
import dev.badbird.teams.runnable.DataUpdateRunnable;
import dev.badbird.teams.storage.impl.FlatFileStorageHandler;
import dev.badbird.teams.util.Metrics;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.util.Logger;
import dev.badbird.teams.api.TeamsPlusAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public final class TeamsPlus extends JavaPlugin {
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private static TeamsPlus instance;
    @Getter
    private static TeamsPlusAPI api;
    @Getter
    private static YamlConfiguration langFile;
    /**
     * because {@link FlatFileStorageHandler}
     */
    @Getter
    private static boolean disabling = false;
    @Getter
    private final ConversationFactory conversationFactory = new ConversationFactory(this);
    @Getter
    private StorageManager storageManager;
    @Getter
    private TeamsManager teamsManager;
    @Getter
    private WaypointManager waypointManager;
    @Getter
    private MiniMessage miniMessage;

    public static void reloadLang() {
        langFile = new YamlConfiguration();
        try {
            langFile.load(new File(instance.getDataFolder() + "/messages.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        bLib.create(this);
        Logger.info("Starting TeamsPlus v." + getDescription().getVersion());
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        saveDefaultConfig();
        new Metrics(this, 12438);
        miniMessage = MiniMessage.builder().build();
        //bLib.getCommandFramework().registerCommandsInPackage("net.badbird5907.teams.commands");
        CommandManager.init();
        Listener[] listeners = {new CombatListener(), new MessageListener(), new SessionListener()};
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
        api = new TeamsPlusAPI();
        File messages = new File(getDataFolder() + "/messages.yml");
        if (!messages.getParentFile().exists())
            messages.getParentFile().mkdirs();
        if (!messages.exists()) {
            InputStream stream = getResource("messages.yml");
            Files.copy(Objects.requireNonNull(stream), messages.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        langFile = new YamlConfiguration();
        langFile.load(messages);

        Logger.info("Hooking into plugins...");
        HookManager.init();

        waypointManager = new WaypointManager();
        storageManager = new StorageManager();
        teamsManager = new TeamsManager();

        waypointManager.init(this);

        new DataUpdateRunnable().runTaskTimerAsynchronously(this, 20, 20);
        Logger.info("Successfully started TeamsPlus in (%1 ms.)", (System.currentTimeMillis() - start));

        /*
        CommandManager.getCommander().getCommandMap().forEach((k,v)-> {
            BukkitCommandWrapper wrapper = (BukkitCommandWrapper) v.getPlatformCommandObject();
            System.out.println(wrapper.getName() + ":");
            for (String alias : wrapper.getAliases()) {
                System.out.println(" - " + alias);
            }
        });
         */
    }

    @Override
    public void onDisable() {
        disabling = true;
        teamsManager.saveTeams(StorageManager.getStorageHandler());
        StorageManager.getStorageHandler().disable();
        HookManager.disable();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        waypointManager.init(this);
    }
}
