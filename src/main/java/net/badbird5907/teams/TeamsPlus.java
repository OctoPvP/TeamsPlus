package net.badbird5907.teams;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.bstats.Metrics;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.teams.api.TeamsPlusAPI;
import net.badbird5907.teams.manager.HookManager;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.manager.TeamsManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class TeamsPlus extends JavaPlugin {
    @Getter
    private static TeamsPlus instance;
    @Getter
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private static TeamsPlusAPI api;
    @Getter
    private static YamlConfiguration langFile;
    @Getter
    private StorageManager storageManager;
    @Getter
    private TeamsManager teamsManager;
    @SneakyThrows
    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        new bLib(this,"[Teams+]");
        Logger.info("Starting TeamsPlus v." + getDescription().getVersion());
        new Metrics(this,12438);
        bLib.getCommandFramework().registerCommandsInPackage("net.badbird5907.teams.commands");
        bLib.getInstance().registerListenersInPackage("net.badbird5907.teams.listeners");
        api = new TeamsPlusAPI();
        File messages = new File(getDataFolder() + "/messages.yml");
        if (!messages.exists()){
            Files.copy(getResource("messages.yml"),messages.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        langFile = new YamlConfiguration();
        langFile.load(messages);

        Logger.info("Hooking into plugins...");
        HookManager.init();

        storageManager = new StorageManager();
        teamsManager = new TeamsManager();
        Logger.info("Successfully started TeamsPlus in (%1 ms.)",(System.currentTimeMillis() - start));
    }

    @Override
    public void onDisable() {
        PlayerManager.getPlayers().forEach((uuid,data)->{
            PlayerManager.getPlayers().remove(uuid);
            StorageManager.getStorageHandler().saveData(data);
        });
        teamsManager.saveTeams(StorageManager.getStorageHandler());
        StorageManager.getStorageHandler().disable();
        HookManager.disable();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
    }
}
