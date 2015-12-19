package net.aidantaylor.bukkit.sleepytime;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SleepyTime extends JavaPlugin implements Listener {
	private boolean debug = true;
	private float playerAmount;
	private ArrayList<Player> sleeping = new ArrayList<Player>();
	private List<String> worlds;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		saveDefaultConfig();
		
		load();
		log(getName() + " has been enabled!", true);
	}

	@Override
	public void onDisable() {
		log(getName() + " has been disabled!", true);
	}
	
	public void load() {
		getConfig().options().copyDefaults(true);
		FileConfiguration configFile = getConfig();
		
		playerAmount = configFile.getInt("playerAmount");
		worlds = configFile.getStringList("worlds");
		debug = configFile.getBoolean("debug");
		
		playerAmount = playerAmount / 100;
		
		log("Player decimal " + playerAmount);
	}
	
	@EventHandler
	public void PlayerBedEnter(PlayerBedEnterEvent event){
		Player player = event.getPlayer();
		
		if (!player.hasPermission("sleepytime.allow")) {
			return;
		}
		
		checkSleep(player);
	}
	
	@EventHandler
	public void PlayerBedLeave(PlayerBedLeaveEvent event){
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (sleeping.indexOf(player) > 0) {
			sleeping.remove(player);
		}
		
		log(player + " stopped sleeping");
	}
	
	public void checkSleep(Player player) {
		World world = player.getWorld();
		
		if (sleeping.indexOf(player) < 0) {
			sleeping.add(player);
		}
		
		log(player + " is sleeping");
        
        for (String world2 : worlds){
    		
        	if (world.getName().indexOf(world2) >= 0 && world.getTime() >= 13000) {
        		float onlinePlayers = Bukkit.getOnlinePlayers().size();
        		float delta = Math.round(onlinePlayers * playerAmount);
        		
        		log("Sleep delta " + delta + " sleep decimal " + playerAmount);
        		log("players sleeping " + sleeping.size() + " online players " + onlinePlayers + "");
        		
        		sendMessage(ChatColor.YELLOW + "" + sleeping.size() + "/" + ((int) delta) + " are currently sleeping...");
        		
        		if (sleeping.size() >= delta) {
        			log("setting time to day");
        			
        			if(world.hasStorm()) {
        				world.setStorm(false);
        			}
        			
        			if(world.isThundering()) {
        				world.setThundering(false);
        			}

        			world.setTime(23450);
        			sleeping.clear();
        		}
        		
        		break;
        	}
        }
	}
	
	public void sendMessage(String ...strings) {
		String output = "";
		
		for(String s : strings) {
			output += s + " ";
		}
		
		for(Player player : Bukkit.getOnlinePlayers()) {
	        for (String world2 : worlds){
	        	if (player.getWorld().getName().indexOf(world2) >= 0) {
	        		player.sendMessage(output);
	        	}
	        }
		}
	}
	
	public void log(String string) {
		log(string, false);
	}
	
	public void log(String string, boolean bypassdebug) {
		if (bypassdebug == true || debug == true) {
			getLogger().info(string);
		}
	}
}
