package net.velinquish.compressor.commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import net.velinquish.compressor.Compressor;
import net.velinquish.compressor.menu.CompressorMenu;
import net.velinquish.utils.Common;

public class CompressorCommand extends Command {

	private Compressor plugin;

	public CompressorCommand(Compressor plugin) {
		super(plugin.getConfig().getString("main-command"));
		setAliases(plugin.getConfig().getStringList("plugin-aliases"));

		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getLangManager().getNode("not-a-player").execute(sender);
			return false;
		}

		Player player = (Player) sender;

		String perm = plugin.getPermission().replaceAll("%action%", "open");
		if (!checkPermission(perm, player))
			return false;

		if (args.length == 0)
			new CompressorMenu(player.hasPermission("worlds.access.space")).displayTo(player);
		else if ("set".equalsIgnoreCase(args[0])) {
			perm = plugin.getPermission().replaceAll("%action%", "set");
			if (!checkPermission(perm, player))
				return false;

			if (args.length < 2) {
				plugin.getLangManager().getNode("command-set-usage").execute(player);
				return false;
			}

			plugin.saveOre(args[1], player.getInventory().getItemInMainHand());

			plugin.getLangManager().getNode("ore-set").replace(Common.map("%ore%", args[1])).execute(player);
		} else if ("reload".equals(args[0])) {
			perm = plugin.getPermission().replaceAll("%action%", "reload");
			if (!checkPermission(perm, player))
				return false;

			try {
				plugin.loadFiles();
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}

			plugin.getLangManager().getNode("plugin-reloaded").execute(player);
		} else
			plugin.getLangManager().getNode("command-compressor-usage").execute(player);
		return false;
	}

	private boolean checkPermission(String perm, Player player) {
		if ((player).hasPermission(perm))
			return true;
		else {
			plugin.getLangManager().getNode("no-permission").replace(Common.map("%permission%", perm)).execute(player);
			return false;
		}
	}

}
