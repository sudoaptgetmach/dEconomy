package org.destroyer.dEconomy.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.destroyer.dEconomy.services.ShopService;

import java.sql.SQLException;
import java.util.List;

import static org.destroyer.dEconomy.factory.ItemStackFactory.createItem;

public class MenuListener implements Listener {

    private final ShopService shopService;

    public MenuListener(ShopService shopService) {
        this.shopService = shopService;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) throws SQLException {

        if (e.getView().getTitle().contains(ChatColor.BOLD + "Loja")) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();

            switch (e.getRawSlot()) {
                case 10 -> openEquipmentShop(player);
                case 12 -> openCosmeticsShop(player);
                case 14 -> openCommandsShop(player);
                case 16 -> openBoosterShop(player);
                default -> {
                }
            }

        } else if (e.getView().getTitle().contains(ChatColor.BOLD + "Aba de itens e equipamentos")) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();

            switch (e.getRawSlot()) {
                case 10 -> shopService.buyItem(player, 10, e.getInventory().getItem(10));
                case 11 -> shopService.buyItem(player, 11, e.getInventory().getItem(11));
                case 12 -> shopService.buyItem(player, 12, e.getInventory().getItem(12));
                case 13 -> shopService.buyItem(player, 13, e.getInventory().getItem(13));
                default -> {
                }
            }
        }
    }

    private void openEquipmentShop(Player player) {
        Inventory equipment = Bukkit.createInventory(player, 54, ChatColor.BOLD + "Aba de itens e equipamentos");

        ItemStack helmet = createItem(Material.DIAMOND_HELMET, ChatColor.LIGHT_PURPLE + "Capacete de Diamante", List.of("Preço: 1000"));
        ItemStack chestplate = createItem(Material.DIAMOND_CHESTPLATE, ChatColor.LIGHT_PURPLE + "Peitoral de Diamante", List.of("Preço: 4000"));
        ItemStack leggings = createItem(Material.DIAMOND_LEGGINGS, ChatColor.LIGHT_PURPLE + "Calça de Diamante", List.of("Preço: 4000"));
        ItemStack boots = createItem(Material.DIAMOND_BOOTS, ChatColor.LIGHT_PURPLE + "Botas de Diamante", List.of("Preço: 1000"));

        equipment.setItem(10, helmet);
        equipment.setItem(11, chestplate);
        equipment.setItem(12, leggings);
        equipment.setItem(13, boots);

        player.openInventory(equipment);
    }

    private void openCosmeticsShop(Player player) {
        // todo
    }

    private void openCommandsShop(Player player) {
        // todo
    }

    private void openBoosterShop(Player player) {
        // todo
    }
}
