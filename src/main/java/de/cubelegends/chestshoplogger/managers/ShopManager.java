package de.cubelegends.chestshoplogger.managers;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.MaterialUtil;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.models.PlayerModel;
import de.cubelegends.chestshoplogger.models.ShopModel;

public class ShopManager {

private ChestShopLogger plugin;
	
	public ShopManager(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	public void tp(Player player, String idStr) {
		
		int id = 0;
		
		if(!player.hasPermission("chestshoplogger.tp")) {
			player.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			player.sendMessage(ChestShopLogger.PREFIX + "You've entered an invalid id!");
			return;
		}
		
		ShopModel shop = new ShopModel(plugin, id);
		
		if(shop.exists()) {
			player.sendMessage(ChestShopLogger.PREFIX + "There is no shop with the id " + id + "!");
			return;
		}
		
		Block blockAbove = new Location(shop.getTP().getWorld(), shop.getTP().getX(), shop.getTP().getY() + 1, shop.getTP().getZ()).getBlock();
		if(shop.getTP().getBlock().getType().isSolid() || blockAbove.getType().isSolid()) {
			player.sendMessage(ChestShopLogger.PREFIX + "The destination is obstructed!");
			return;
		}

		player.teleport(shop.getTP());
		player.sendMessage(ChestShopLogger.PREFIX + "Welcome to shop " + id + "!");
		
	}
	
	public void coords(CommandSender sender, String idStr) {
		
		int id = 0;
		
		if(!sender.hasPermission("chestshoplogger.coords")) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You've entered an invalid id!");
			return;
		}
		
		ShopModel shop = new ShopModel(plugin, id);
		
		if(shop.exists()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no shop with the id " + id + "!");
			return;
		}

		Location loc = shop.getLoc();
		sender.sendMessage(ChestShopLogger.PREFIX + "Shop " + id + " is located in " + loc.getWorld().getName() + " at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
		
	}
	
	public void find(CommandSender sender, String action, String dirtyName) {
		
		if(!sender.hasPermission("chestshoplogger.find")) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		String itemName = getItemName(dirtyName);
		
		if(itemName.equals("Unknown")) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no item, called " + dirtyName + "!");
			return;
		}
		
		List<ShopModel> shops = null;
		
		switch(action) {
		case "sell":
			shops = ShopModel.findShops(plugin, ShopModel.SELLACTION, itemName);
			sender.sendMessage(ChatColor.DARK_GREEN + "========== Sell " + itemName + " ==========");
			break;
		case "buy":
			shops = ShopModel.findShops(plugin, ShopModel.BUYACTION, itemName);
			sender.sendMessage(ChatColor.DARK_GREEN + "========== Buy " + itemName + " ==========");
			break;
		}
		
		if(shops == null) {
			sender.sendMessage(ChestShopLogger.PREFIX + "\"" + action + "\" is not a valid search option!");
			return;
		}
		
		if(shops.size() == 0) {
			sender.sendMessage(ChatColor.GRAY + "There were no results, sorry! :(");
		}
		
		for(ShopModel shop : shops) {
			
			PlayerModel ownerModel = new PlayerModel(plugin, shop.getOwnerUUID());
			String msg = "";
			switch(action) {
			
			case "sell":
				msg = msg + ChatColor.GRAY + "Sell " + ChatColor.GREEN + shop.getMaxAmount() + "x ";
				msg = msg + ChatColor.GRAY + "for " + ChatColor.GREEN + shop.getSellPrice() + " ";
				msg = msg + ChatColor.GRAY + "to " + ChatColor.GREEN + ownerModel.getName() + " ";
				msg = msg + ChatColor.GRAY + "at " + ChatColor.YELLOW + "#" + shop.getID();
				break;
				
			case "buy":
				msg = msg + ChatColor.GRAY + "Buy " + ChatColor.GREEN + shop.getMaxAmount() + "x ";
				msg = msg + ChatColor.GRAY + "for " + ChatColor.GREEN + shop.getBuyPrice() + " ";
				msg = msg + ChatColor.GRAY + "from " + ChatColor.GREEN + ownerModel.getName() + " ";
				msg = msg + ChatColor.GRAY + "at " + ChatColor.YELLOW + "#" + shop.getID();
				break;
				
			}
			sender.sendMessage(msg);
			
		}
		
	}
	
	public static String getItemName(String dirtyName) {
		String itemName = "Unknown";
		ItemStack itemStack = MaterialUtil.getItem(dirtyName);		
		if(itemStack != null) {
			itemName = MaterialUtil.getName(itemStack, true);
		}		
		return itemName;
	}
	
}
