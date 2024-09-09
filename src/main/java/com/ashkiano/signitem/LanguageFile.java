package com.ashkiano.signitem;

import java.io.File;
import java.io.IOException;
import com.ashkiano.signitem.exceptions.LanguageFileNotFoundException;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.net.URL;

public class LanguageFile {
    public static void init() throws IOException {
        File pluginFolder = SignItem.instance.getDataFolder();
        if (pluginFolder.toPath().resolve("en.yml").toFile().exists()) return;
        pluginFolder.mkdirs();

        URL enLangFile = TranslatableProvider.class.getResource("/en.yml");
        assert enLangFile != null;
        FileUtils.copyURLToFile(enLangFile, pluginFolder.toPath().resolve("en.yml").toFile());
    }

    public static YamlConfiguration getLanguageFile(String lang) throws LanguageFileNotFoundException {
        File pluginFolder = SignItem.instance.getDataFolder();
        File langFile = pluginFolder.toPath().resolve(lang + ".yml").toFile();
        if (!langFile.exists()) {
            langFile = pluginFolder.toPath().resolve("en.yml").toFile();
        }
        return YamlConfiguration.loadConfiguration(langFile);
    }
}
