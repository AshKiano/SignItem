package com.ashkiano.signitem;

import com.ashkiano.signitem.exceptions.LanguageFileNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SignItem extends JavaPlugin {

    private String signText;
    public static SignItem instance;
    public TranslatableProvider translatableProvider;

    @Override
    public void onEnable() {
        instance = this;
        try {
            LanguageFile.init();
            String lang = getConfig().getString("language", "en");
            translatableProvider = new TranslatableProvider(LanguageFile.getLanguageFile(lang));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LanguageFileNotFoundException e) {
            throw new RuntimeException(e);
        }

        saveDefaultConfig();
        signText = getConfig().getString("signText", "&eSigned by %player%");
        getCommand("signitem").setExecutor(new SignItemCommand());
        getCommand("unsignitem").setExecutor(new UnsignItemCommand());
        Metrics metrics = new Metrics(this, 22259);
        this.getLogger().info(translatableProvider.pluginBootThanks);
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
                sender.sendMessage(ChatColor.RED + translatableProvider.onlyPlayerCanUseCommand);
                return true;
            }

            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item == null || item.getType().isAir()) {
                player.sendMessage(ChatColor.RED + translatableProvider.signItemCanNotBeAir);
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
                player.sendMessage(ChatColor.RED + translatableProvider.itemAlreadySigned);
                return true;
            }

            String playerName = player.getName();
            String finalSignText = ChatColor.translateAlternateColorCodes('&', signText.replace("%player%", playerName));

            lore.add(finalSignText);
            meta.setLore(lore);
            item.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + translatableProvider.itemSignedSuccessfully);
            return true;
        }
    }

    public class UnsignItemCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + translatableProvider.onlyPlayerCanUseCommand);
                return true;
            }

            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item == null || item.getType().isAir()) {
                player.sendMessage(ChatColor.RED + translatableProvider.unsignItemCanNotBeAir);
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasLore()) {
                player.sendMessage(ChatColor.RED + translatableProvider.itemAlreadyUnsigned);
                return true;
            }

            List<String> lore = meta.getLore();
            if (lore == null || !isItemSigned(lore, player.getName())) {
                player.sendMessage(ChatColor.RED + translatableProvider.itemUnsignByWrongPlayer);
                return true;
            }

            String playerName = player.getName();
            lore.removeIf(line -> isPlayerSignedItem(line, playerName));
            meta.setLore(lore);
            item.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + translatableProvider.itemUnsignedSuccessfully);
            return true;
        }
    }
}