package dev.phoenixhaven.customac.base.config;

import dev.phoenixhaven.customac.CustomAC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigManager {
    private FileConfiguration config = null;
    private File configFile = null;

    public ConfigManager() {
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(CustomAC.getInstance().getDataFolder(), "config.yml");

            this.config = YamlConfiguration.loadConfiguration(this.configFile);

            InputStream defaultStream = CustomAC.getInstance().getResource("config.yml");

            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
                this.config.setDefaults(defaultConfig);
            }
        }
    }

    public FileConfiguration getConfig() {
        if (this.config == null) reloadConfig();
        return this.config;
    }

    public void saveConfig() {
        if (this.config == null || this.configFile == null) {
            return;
        }

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            CustomAC.getInstance().getLogger().severe("Couldn't save config file");
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null) {
            this.configFile = new File(CustomAC.getInstance().getDataFolder(), "config.yml");
        }

        if (!this.configFile.exists()) {
            CustomAC.getInstance().saveResource("config.yml", false);
        }
    }
}
