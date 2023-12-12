package dev.badbird.teams.commands.impl;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.limb.ArmorPiece;
import com.lunarclient.apollo.module.limb.BodyPart;
import com.lunarclient.apollo.module.limb.LimbModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import dev.badbird.teams.util.Permissions;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Completer;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Command(name = "lcutils")
@Permission(Permissions.ADMIN)
public class LCUtils {
    @Command(name = "hidebodypart", description = "Hide specific body parts")
    public void reload(@Sender Player sender, Player target, String bodyPart) {
        BodyPart bodyPart1 = BodyPart.valueOf(bodyPart.toUpperCase());
        sender.sendMessage("Hiding " + bodyPart + " for " + target.getName());
        LimbModule limbModule = Apollo.getModuleManager().getModule(LimbModule.class);
        if (limbModule == null) {
            sender.sendMessage("LimbModule is null");
            return;
        }
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());

            apolloPlayerOpt.ifPresent(apolloPlayer -> {
                limbModule.hideBodyParts(apolloPlayer,
                        target.getUniqueId(),
                        EnumSet.of(bodyPart1)
                );
            });
        }
    }

    @Command(name = "resetbodyparts", description = "Reset body parts")
    public void reset(@Sender Player sender, Player target) {
        sender.sendMessage("Resetting body parts for " + target.getName());
        LimbModule limbModule = Apollo.getModuleManager().getModule(LimbModule.class);
        if (limbModule == null) {
            sender.sendMessage("LimbModule is null");
            return;
        }
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());

            apolloPlayerOpt.ifPresent(apolloPlayer -> limbModule.resetBodyParts(apolloPlayer,
                    target.getUniqueId(),
                    EnumSet.allOf(BodyPart.class)
            ));
        }
    }
    @Command(name = "hidearmor", description = "Hide armor pieces")
    public void hideArmor(@Sender Player sender, Player target, String armorPiece) {
        sender.sendMessage("Hiding " + armorPiece + " for " + target.getName());
        LimbModule limbModule = Apollo.getModuleManager().getModule(LimbModule.class);
        if (limbModule == null) {
            sender.sendMessage("LimbModule is null");
            return;
        }
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());

            apolloPlayerOpt.ifPresent(apolloPlayer -> {
                limbModule.hideArmorPieces(apolloPlayer,
                        target.getUniqueId(),
                        EnumSet.of(ArmorPiece.valueOf(armorPiece.toUpperCase()))
                );
            });
        }
    }

    @Command(name = "resetarmor", description = "Reset armor pieces")
    public void resetArmor(@Sender Player sender, Player target) {
        sender.sendMessage("Resetting armor pieces for " + target.getName());
        LimbModule limbModule = Apollo.getModuleManager().getModule(LimbModule.class);
        if (limbModule == null) {
            sender.sendMessage("LimbModule is null");
            return;
        }
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());

            apolloPlayerOpt.ifPresent(apolloPlayer -> limbModule.resetArmorPieces(apolloPlayer,
                    target.getUniqueId(),
                    EnumSet.allOf(ArmorPiece.class)
            ));
        }
    }
}
