package org.destroyer.dEconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

import static org.destroyer.dEconomy.enums.Messages.NO_PERMISSION_CONSOLE;
import static org.destroyer.dEconomy.factory.ItemStackFactory.createItem;

public class ShopCommand {

    @Command({"shop", "loja"})
    @CommandPermission("deconomy.store.command")
    public void shop(BukkitCommandActor actor) {
        if (actor.isConsole()) {
            actor.error(NO_PERMISSION_CONSOLE.get());
            return;
        }

        Player player = actor.asPlayer();
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BOLD + "Loja");

        ItemStack tools = createItem(Material.DIAMOND_SWORD, ChatColor.AQUA + "Itens e equipamentos",
                List.of(ChatColor.GRAY + "Aba de itens e equipamentos"));

        ItemStack cosmetics = createItem(Material.NETHER_STAR, ChatColor.AQUA + "Cosméticos",
                List.of(ChatColor.GRAY + "Aba de cosméticos"));

        ItemStack commands = createItem(Material.NAME_TAG, ChatColor.AQUA + "Comandos e permissões",
                List.of(ChatColor.GRAY + "Aba de comandos e permissões a venda"));

        ItemStack booster = createItem(Material.EXPERIENCE_BOTTLE, ChatColor.AQUA + "Boosters e consumíveis",
                List.of(ChatColor.GRAY + "Aba de boosters e consumíveis"));

        inv.setItem(10, tools);
        inv.setItem(12, cosmetics);
        inv.setItem(14, commands);
        inv.setItem(16, booster);

        player.openInventory(inv);
    }

}
