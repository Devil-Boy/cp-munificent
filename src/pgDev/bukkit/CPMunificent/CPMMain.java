package pgDev.bukkit.CPMunificent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import pgDev.bukkit.CommandPoints.*;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class CPMMain extends JavaPlugin {
	// Permissions Integration
    private static PermissionHandler Permissions;
    
    // CommandPoints API
    CommandPointsAPI cpAPI;
    
	// File Locations
    String pluginMainDir = "./plugins/CommandPointsMunificent";
    String pluginConfigLocation = pluginMainDir + "/CPM.cfg";
    
    // Settings
    CPMConfig pluginSettings;
    
    // Point give timer
    Timer pointBucket;
    
    // Partial point tracker
    ConcurrentHashMap<String, Float> partialPoints = new ConcurrentHashMap<String, Float>();
	
	public void onEnable() {
		// Check for the plugin directory (create if it does not exist)
    	File pluginDir = new File(pluginMainDir);
		if(!pluginDir.exists()) {
			boolean dirCreation = pluginDir.mkdirs();
			if (dirCreation) {
				System.out.println("New CommandPointsMunificent directory created!");
			}
		}
		
		// Load the Configuration
    	try {
        	Properties preSettings = new Properties();
        	if ((new File(pluginConfigLocation)).exists()) {
        		preSettings.load(new FileInputStream(new File(pluginConfigLocation)));
        		pluginSettings = new CPMConfig(preSettings, this);
        		if (!pluginSettings.upToDate) {
        			pluginSettings.createConfig();
        			System.out.println("CommandPointsMobDisguiseBridge Configuration updated!");
        		}
        	} else {
        		pluginSettings = new CPMConfig(preSettings, this);
        		pluginSettings.createConfig();
        		System.out.println("CommandPointsMunificent Configuration created!");
        	}
        } catch (Exception e) {
        	System.out.println("Could not load CommandPointsMunificent configuration! " + e);
        }
        
        // Integrations
        setupPermissions();
        setupCommandPoints();
        
        // Start the timer
        (pointBucket = new Timer(pluginSettings.grantInterval * 1000, pointGiver)).start();
		
		PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public void onDisable() {
		// Stop le timer!
		pointBucket.stop();
		
		System.out.println("CommandPointsMunificent disabled!");
	}
	
	// Permissions Methods
    private void setupPermissions() {
        Plugin permissions = this.getServer().getPluginManager().getPlugin("Permissions");

        if (Permissions == null) {
            if (permissions != null) {
                Permissions = ((Permissions)permissions).getHandler();
            } else {
            }
        }
    }
    
    protected boolean hasPermissions(Player player, String node) {
        if (Permissions != null) {
        	return Permissions.has(player, node);
        } else {
            return player.hasPermission(node);
        }
    }
    
    // CP Integrate
    private void setupCommandPoints() {
    	Plugin commandPoints = this.getServer().getPluginManager().getPlugin("CommandPoints");
    	if (commandPoints != null) {
    		cpAPI = ((CommandPoints)commandPoints).getAPI();
        }
    }
	
	// Repeating point giver
	ActionListener pointGiver = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			for (Player beneficiary : getServer().getOnlinePlayers()) {
				if (hasPermissions(beneficiary, "cpmunificent.beneficiary")) {
					grantPoints(beneficiary);
				}
			}
		}
	};
	
	// Point method
	public void grantPoints(Player player) {
		String name = player.getName();
		if (cpAPI.hasAccount(name, this)) {
			Float newTotal;
			if (partialPoints.contains(name)) {
				newTotal = partialPoints.get(name) + pluginSettings.grantAmount;
			} else {
				newTotal = pluginSettings.grantAmount;
			}
			 
			int pointsToGive = (int) Math.floor(newTotal);
			if (pointsToGive == 0) {
				partialPoints.put(name, newTotal);
			} else {
				partialPoints.put(name, newTotal - pointsToGive);
				cpAPI.addPoints(name, pointsToGive, "Played for " + pluginSettings.grantInterval + "seconds.", this);
			}
		} else {
			player.sendMessage(ChatColor.RED + "YOu could not be granted commandpoints because your account was not created. Rejoin the server to create one.");
		}
	}
}
