package com.ashkiano.signitem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SignItem extends JavaPlugin {

    private String signText;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        signText = getConfig().getString("signText", "&eSigned by %player%");
        getCommand("signitem").setExecutor(new SignItemCommand());
        getCommand("unsignitem").setExecutor(new UnsignItemCommand());
        Metrics metrics = new Metrics(this, 22259);
    }

    @Override
    public void onDisable() {
    }

    private boolean isItemSigned(List<String> lore, String playerName) {
        String signedText = signText.replace("%player%", playerName);
        return lore.stream().anyMatch(line -> line.equals(signedText));
    }

    private boolean isPlayerSignedItem(String line, String playerName) {
        String signedText = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', signText.replace("%player%", playerName)));
        return ChatColor.stripColor(line).equals(signedText);
    }

    public class SignItemCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item == null || item.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "You must hold an item in your hand to sign it.");
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                meta = Bukkit.getItemFactory().getItemMeta(item.getType());
            }

            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }

            if (isItemSigned(lore, player.getName())) {
                player.sendMessage(ChatColor.RED + "This item is already signed.");
                return true;
            }

            String playerName = player.getName();
            String finalSignText = ChatColor.translateAlternateColorCodes('&', signText.replace("%player%", playerName));

            lore.add(finalSignText);
            meta.setLore(lore);
            item.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + "Item signed successfully.");
            return true;
        }
    }

    public class UnsignItemCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item == null || item.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "You must hold an item in your hand to unsign it.");
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasLore()) {
                player.sendMessage(ChatColor.RED + "This item is not signed.");
                return true;
            }

            List<String> lore = meta.getLore();
            if (lore == null || !isItemSigned(lore, player.getName())) {
                player.sendMessage(ChatColor.RED + "This item is not signed by you.");
                return true;
            }

            String playerName = player.getName();
            lore.removeIf(line -> isPlayerSignedItem(line, playerName));
            meta.setLore(lore);
            item.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + "Item unsigned successfully.");
            return true;
        }
    }
}