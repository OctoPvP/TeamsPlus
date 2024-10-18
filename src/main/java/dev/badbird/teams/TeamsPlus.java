package dev.badbird.teams;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.badbird.teams.api.TeamsPlusAPI;
import dev.badbird.teams.claims.ClaimHandler;
import dev.badbird.teams.commands.annotation.AnnotationMappers;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.commands.provider.CommandSenderInjector;
import dev.badbird.teams.commands.provider.PlayerDataInjector;
import dev.badbird.teams.commands.provider.TeamParser;
import dev.badbird.teams.listeners.CombatListener;
import dev.badbird.teams.listeners.MessageListener;
import dev.badbird.teams.listeners.SessionListener;
import dev.badbird.teams.manager.*;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import dev.badbird.teams.runnable.DataUpdateRunnable;
import dev.badbird.teams.storage.impl.FlatFileStorageHandler;
import dev.badbird.teams.util.Metrics;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.util.Logger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.exception.CommandExecutionException;
import org.incendo.cloud.exception.InjectionException;
import org.incendo.cloud.exception.handling.ExceptionHandler;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.minecraft.extras.AudienceProvider;
import org.incendo.cloud.minecraft.extras.ImmutableMinecraftHelp;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.processors.cooldown.annotation.CooldownBuilderModifier;
import org.incendo.cloud.services.type.ConsumerService;
import org.incendo.cloud.setting.ManagerSetting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Function;

import static dev.badbird.teams.util.ChatUtil.tr;
import static net.kyori.adventure.text.Component.text;

@Getter
public final class TeamsPlus extends JavaPlugin {
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private static final Gson cleanGson = new GsonBuilder().setPrettyPrinting().create();
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
    private StorageManager storageManager;
    private TeamsManager teamsManager;
    private WaypointManager waypointManager;
    private MiniMessage miniMessage;

    private LegacyPaperCommandManager<CommandSender> commandManager;
    private MinecraftHelp<CommandSender> teamsHelp, chatHelp, allHelp;

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
        try {
            final LegacyPaperCommandManager<CommandSender> mgr = new LegacyPaperCommandManager<>(this, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity());
            this.commandManager = mgr;

            if (mgr.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
                mgr.registerBrigadier();
            } else if (mgr.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                mgr.registerAsynchronousCompletions();
            }
            mgr.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
            mgr.parserRegistry()
                    .registerParser(LocationParser.locationParser());
            mgr.parameterInjectorRegistry()
                    .registerInjector(Team.class, new TeamParser())
                    .registerInjector(CommandSender.class, new CommandSenderInjector())
                    .registerInjector(PlayerData.class, new PlayerDataInjector());
            mgr.exceptionController()
                    .registerHandler(CommandExecutionException.class, ExceptionHandler.unwrappingHandler(InjectionException.class))
                    .registerHandler(InjectionException.class, ctx -> {
                        ctx.context().sender().sendRichMessage("<red>Injection failed: <gray>" + ctx.exception().getMessage());
                    });

            Component prefix = LegacyComponentSerializer.legacyAmpersand().deserialize(getConfig().getString("prefix", "&7[&bTeams+&7]&r"));
            MinecraftExceptionHandler.create(new AudienceProvider<CommandSender>() {
                        @Override
                        public @NonNull Audience apply(@NonNull CommandSender sender) {
                            return Audience.audience(sender);
                        }
                    })
                    .defaultInvalidSyntaxHandler().defaultInvalidSenderHandler().defaultNoPermissionHandler().defaultArgumentParsingHandler()
                    .defaultCommandExecutionHandler().decorator(component -> text().append(prefix).append(Component.space()).append(component).build()
                    ).registerTo(commandManager);

            AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(commandManager, CommandSender.class);
            AnnotationMappers.register(annotationParser, mgr);
            CooldownBuilderModifier.install(annotationParser);

            CloudKey<TeamRank> teamRankKey = CloudKey.of("team-permission", TeamRank.class);
            annotationParser.registerBuilderModifier(TeamPermission.class, (annotation, builder) -> {
                // System.out.println("Setting team rank key: " + annotation.value() + " | " + builder.commandDescription().description().textDescription());
                return builder.meta(teamRankKey, annotation.value());
            });
            commandManager.registerCommandPostProcessor(ctx -> {
                Map<CloudKey<?>, ?> all = ctx.command().commandMeta().all();
                // all.forEach((key, value) -> System.out.println(key + ": " + value));
                if (ctx.command().commandMeta().contains(teamRankKey)) {
                    TeamRank reqRank = ctx.command().commandMeta().get(teamRankKey);
                    CommandSender sender = ctx.commandContext().sender();
                    if (sender instanceof Player player) {
                        PlayerData playerData = PlayerManager.getData(player);
                        if (!playerData.isInTeam()) {
                            player.sendMessage(Lang.MUST_BE_IN_TEAM.getComponent());
                            ConsumerService.interrupt();
                            // throw new CommandExecutionException(new RuntimeException("You must be in a team to use this command."));
                        }
                        if (!playerData.getPlayerTeam().isAtLeast(playerData.getUuid(), reqRank)) {
                            player.sendMessage(Lang.NO_PERMISSION.getComponent(tr("name", player.getName()), tr("rank", reqRank.name())));
                            ConsumerService.interrupt();
                            // throw new CommandExecutionException(new RuntimeException("You must be at least " + reqRank.name() + " to use this command. Ask your team leader to promote you."));
                        }
                    }
                }
            });

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
            annotationParser.parseContainers();

            Function<String, MinecraftHelp<CommandSender>> helpGenerator = (pfx) -> ImmutableMinecraftHelp.<CommandSender>builder()
                    .commandManager(commandManager)
                    .audienceProvider(AudienceProvider.nativeAudience())
                    .commandPrefix("/" + (pfx.isEmpty() ? "teams" : pfx) + " help")
                    .commandFilter((command) -> pfx.isEmpty() || command.rootComponent().name().equals(pfx))
                    .build();
            teamsHelp = helpGenerator.apply("teams");
            chatHelp = helpGenerator.apply("chat");
            allHelp = helpGenerator.apply("");
            //help = MinecraftHelp.createNative("/teams help", this.commandManager);
            commandManager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        Listener[] listeners = {new CombatListener(), new MessageListener(), new SessionListener()};
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
        api = new TeamsPlusAPI();
        File messages = new File(getDataFolder() + "/messages.yml");
        if (!messages.getParentFile().exists())
            messages.getParentFile().mkdirs();
        langFile = new YamlConfiguration();
        if (!messages.exists()) {
            messages.createNewFile();
            String comment = "# This is the message override file for TeamsPlus. You can change any message in here to whatever you want.";
            Files.write(messages.toPath(), comment.getBytes());
            /*for (Lang value : Lang.values()) {
                langFile.set(value.getConfigPath(), value.getDefault());
            }
            langFile.save(messages);*/
        }
        langFile.load(messages);

        Logger.info("Hooking into plugins...");

        waypointManager = new WaypointManager();
        storageManager = new StorageManager();
        teamsManager = new TeamsManager();

        HookManager.init();
        waypointManager.init(this);

        ClaimHandler.getInstance().init();

        new DataUpdateRunnable().runTaskTimerAsynchronously(this, 20, 20);
        Logger.info("Successfully started TeamsPlus in (%1 ms.)", (System.currentTimeMillis() - start));
        Logger.info("Loaded " + teamsManager.getTeams().size() + " teams and " + ClaimHandler.getInstance().getClaimCount() + " claim chunks.");
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
        if (waypointManager != null)
            waypointManager.init(this);
    }
}
