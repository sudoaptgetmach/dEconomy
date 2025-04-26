package org.destroyer.dEconomy.handler;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.util.BukkitUtils;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.node.ParameterNode;

import java.util.Objects;

import static org.destroyer.dEconomy.enums.Messages.INVALID_PLAYER;
import static org.destroyer.dEconomy.enums.Messages.NO_PERMISSION;
import static revxrsal.commands.bukkit.util.BukkitUtils.legacyColorize;

public class LampExceptionHandler extends BukkitExceptionHandler {
    @Override
    public void onInvalidPlayer(InvalidPlayerException e, BukkitCommandActor actor) {
        actor.error(legacyColorize(INVALID_PLAYER.get()));
    }

    @Override
    public void onNoPermission(@NotNull NoPermissionException e, @NotNull BukkitCommandActor actor) {
        actor.error(NO_PERMISSION.get());
    }

    @Override
    public void onMissingArgument(@NotNull MissingArgumentException e, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, ?> parameter) {
        if (!Objects.requireNonNull(actor.asPlayer()).hasPermission(parameter.command().permission().toString())) {
            actor.error(NO_PERMISSION.get());
        } else {
            actor.error(BukkitUtils.legacyColorize("&cUsage: /" + parameter.command().usage() + "&c."));
        }
    }
}