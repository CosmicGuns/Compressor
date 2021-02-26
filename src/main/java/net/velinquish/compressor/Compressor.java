package net.velinquish.compressor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.designer.Designer;

import lombok.Getter;
import net.velinquish.compressor.commands.CompressorCommand;
import net.velinquish.utils.Common;
import net.velinquish.utils.VelinquishPlugin;
import net.velinquish.utils.lang.LangManager;

public class Compressor extends JavaPlugin implements VelinquishPlugin {

	@Getter
	private static Compressor instance;
	@Getter
	private LangManager langManager;

	@Getter
	private String prefix;
	@Getter
	private String permission;

	private static boolean debug;

	@Getter
	private YamlConfiguration config;
	private File configFile;

	@Getter
	private YamlConfiguration lang;
	private File langFile;

	private YamlConfiguration resources;
	private File resourcesFile;

	private Map<String, ItemStack> items;

	@Override
	public void onEnable() {
		instance = this;
		Common.setInstance(this);
		Designer.setPlugin(this);

		langManager = new LangManager();

		items = new HashMap<>();

		try {
			loadFiles();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		Common.registerCommand(new CompressorCommand(this));
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public void loadFiles() throws IOException, InvalidConfigurationException {
		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			saveResource("config.yml", false);
		}
		config = new YamlConfiguration();
		config.load(configFile);

		prefix = getConfig().getString("plugin-prefix");
		debug = getConfig().getBoolean("debug");
		permission = getConfig().getString("permission");

		langFile = new File(getDataFolder(), "lang.yml");
		if (!langFile.exists()) {
			langFile.getParentFile().mkdirs();
			saveResource("lang.yml", false);
		}
		lang = new YamlConfiguration();
		lang.load(langFile);

		langManager.clear();
		langManager.setPrefix(prefix);
		langManager.loadLang(lang);

		resourcesFile = new File(getDataFolder(), "resources.yml");
		if (!resourcesFile.exists()) {
			resourcesFile.getParentFile().mkdirs();
			saveResource("resources.yml", false);
		}
		resources = new YamlConfiguration();
		resources.load(resourcesFile);

		for (String key : resources.getKeys(false))
			items.put(key, resources.getItemStack(key));
	}

	public void saveOre(String key, ItemStack item) {
		resources.set(key, item);
		try {
			resources.save(resourcesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		items.put(key, item);
	}

	public ItemStack getItem(String key) {
		return items.get(key);
	}

	public static void debug(String message) {
		if (debug == true)
			Common.log(message);
	}
}
