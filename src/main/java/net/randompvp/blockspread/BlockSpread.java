package net.randompvp.blockspread;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class BlockSpread extends JavaPlugin implements Listener {

	FileConfiguration config = this.getConfig();

	int maxDistance = config.getInt("MaxDistance");
	long blockDelay = config.getLong("BlockDelay");
	boolean blockDrop = config.getBoolean("DoBlocksDrop");

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		breakBlock(e.getBlock(), e.getPlayer());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && !sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You have no permission.");
			return false;
		}
		this.reloadConfig();
		this.config = this.getConfig();
		this.maxDistance = this.config.getInt("MaxDistance");
		this.blockDelay = this.config.getLong("BlockDelay");
		this.blockDrop = this.config.getBoolean("DoBlocksDrop");
		sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the configuration.");
		return super.onCommand(sender, cmd, label, args);
	}

	void breakBlock(Block block, Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			if (!player.isOnline())
				return;
			final Block[] relativeBlocks = { block.getRelative(BlockFace.DOWN), block.getRelative(BlockFace.UP), block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.EAST), block.getRelative(BlockFace.WEST) };
			for (Block blockToBreak : relativeBlocks) {
				if (!blockToBreak.getType().isAir() && blockToBreak.getType().getBlastResistance() < 1201 && player.getWorld().getName().equals(block.getWorld().getName()) && player.getLocation().distance(block.getLocation()) < maxDistance) {
					if (blockDrop)
						blockToBreak.breakNaturally();
					else
						blockToBreak.setType(Material.AIR);
					breakBlock(blockToBreak, player);
				}
			}
		}, blockDelay);
	}

}
