package com.nxtinc.thetestgame.textalert;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TextAlert extends JavaPlugin
{

	   private static final Logger log = Logger.getLogger("Minecraft");
	   
	   public static Permission permission = null;
	   
	   public PHPManager php;
	   
	   public void info(String text)
	   {
		   log.info("[TextAlert] " + text);
	   }
	    
	   public void severe(String text)
	   {
		   log.severe("[TextAlert] " + text);    
	   }
	   
	   public void warning(String text)
	   {
		   log.warning("[TextAlert] " + text);    
	   }

	   @Override
	   public void onEnable()
	   {
		   info("- Activating");
		   
		   this.saveDefaultConfig();
		   
		   this.php = new PHPManager(this);
		   
		   PluginManager pm = this.getServer().getPluginManager();
		   //* Setup Vault
		   if (pm.getPlugin("Vault") != null) 
		   {
			   setupPermissions();
		   }
		   else
		   {
			   severe("- Vault dependency not found! Disabling");
			   pm.disablePlugin(this);
		   }
		   
		   CommandExtender commandextender = new CommandExtender(this);
		   ///* Admin Commands
		   this.getCommand("textalert").setExecutor(commandextender);
		   ///* General Commands
		   this.getCommand("modrequest").setExecutor(commandextender);
		   //this.getCommand("adminrequest").setExecutor(commandextender);
		   //this.getCommand("oprequest").setExecutor(commandextender);
		   
		   PluginDescriptionFile pdfFile = this.getDescription();
		   info("Version " + pdfFile.getVersion() + " enabled");
	   }
	   
	   @Override
	   public void onDisable()
	   {
		   info("Disabled");  
	   }
	   
	   public void Reload()
	   {
		   this.reloadConfig();
	   }
	   
	   private boolean setupPermissions()
	   {
		   RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		   if (permissionProvider != null) {
	            permission = permissionProvider.getProvider();
		   }
		   return (permission != null);
	   }
	   
	   public boolean hasPermission(Player player, String perm, boolean op)
	   {
		   if (op)
		   {
			   if (player.hasPermission(perm) || permission.has(player, perm) || player.isOp())
			   {
				   return true;
			   }
		   }
		   else
		   {
			   if (player.hasPermission(perm) || permission.has(player, perm))
			   {
				   return true;
			   }
		   }
		   
		   return false;
	   }
}
