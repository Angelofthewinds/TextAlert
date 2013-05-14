package com.nxtinc.thetestgame.textalert;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExtender  implements CommandExecutor {

	private TextAlert plugin;
	
	public CommandExtender(TextAlert plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("textalert"))
		{
			if (args.length > 0)
			{
				if (args[0].equals("reload"))
				{
					if (sender instanceof Player)
					{
						Player player = (Player) sender;
						if (plugin.hasPermission(player,"textalert.op.reload",true))
						{
							plugin.Reload();
							player.sendMessage(ChatColor.GREEN + "Config Reloaded!");		
							return true;
						}
					}
					else
					{
						sender.sendMessage("Reloading Config");
						plugin.Reload();
						sender.sendMessage("Config Reloaded!");
						return true;
					}
				}
				else if (args[0].equals("version"))
				{
					if (sender instanceof Player)
					{
						Player player = (Player) sender;
						if (plugin.hasPermission(player,"textalert.op.version",true))
						{
							player.sendMessage(ChatColor.GREEN + "Version : " + plugin.getDescription().getVersion());		
							return true;
						}
					}
					else
					{
						sender.sendMessage("Version : " + plugin.getDescription().getVersion());		
						return true;
					}					
				}
				else if (args[0].equals("notices"))
				{
					if (sender instanceof Player)
					{
						Player player = (Player) sender;
						if (args.length > 1)
						{
							String state = args[1];
							boolean bs = false;
							if (state.equalsIgnoreCase("true"))
							{
								bs = true;
								player.sendMessage(ChatColor.GREEN + " Notices enabled!");
							}
							else
							{
								bs = false;
								player.sendMessage(ChatColor.GREEN + " Notices disabled!");
							}
							plugin.getConfig().set("numbers." + player.getName() + ".enabled", bs);
							return true;
						}
					}
					else
					{
						sender.sendMessage("You need to be a player to use this command!");
						return true;
					}
				}
				else if (args[0].equals("pref") || args[0].equals("preference"))
				{
					if (sender instanceof Player)
					{
						Player player = (Player) sender;
						if (args.length > 1)
						{
							String name = player.getName();
							if (args[1].equals("txt"))
							{
								player.sendMessage(ChatColor.GREEN + "Notice type changed to text messages!");
								plugin.getConfig().set("numbers." + name + ".pref", "txt");
							}
							else if (args[1].equals("email"))
							{
								player.sendMessage(ChatColor.GREEN + "Notice type changed to emails!");
								plugin.getConfig().set("numbers." + name + ".pref", "email");
							}
							else
							{
								player.sendMessage(ChatColor.RED + args[1] + " is not a valid type!");
								player.sendMessage(ChatColor.RED + "txt, email");
							}
							return true;
						}
					}
					else
					{
						sender.sendMessage("You need to be a player to use this command!");
						return true;
					}
				}
				else if (args[0].equals("help"))
				{
					if (sender instanceof Player)
					{
						sender.sendMessage(ChatColor.GREEN + "**Help**");
						sender.sendMessage(ChatColor.GREEN + "/version: outputs the version");
						sender.sendMessage(ChatColor.GREEN + "/reload: reloads the config*");
						sender.sendMessage(ChatColor.GREEN + "/personal <who> <message>: sends a personal text message");
						sender.sendMessage(ChatColor.GREEN + "/help: displays this screen");
						return true;
					}
					else
					{
						sender.sendMessage("**Help**");
						sender.sendMessage("/version: outputs the version");
						sender.sendMessage("/reload: reloads the config*");
						sender.sendMessage("/personal <who> <message>: sends a personal text message");
						sender.sendMessage("/help: displays this screen");
						return true;
					}
				}
				else if (args[0].equals("register"))
				{
					if (args.length > 4)
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							if (plugin.hasPermission(player, "textalert.register", true))
							{
								String name = args[1];
								String carrier = args[2];
								String email = args[3];
								String number = args[4];
								if (name != null && carrier != null && email != null && number != null)
								{
									String savepoint = "numbers." + name;
									plugin.getConfig().set(savepoint + ".carrier", carrier);
									plugin.getConfig().set(savepoint + ".number", number);
									plugin.getConfig().set(savepoint + ".email", email);
									plugin.getConfig().set(savepoint + ".pref", "txt");
									plugin.getConfig().set(savepoint + ".enabled", true);
									if (plugin.getConfig().getString("carriers." + carrier) != null)
									{
										player.sendMessage(ChatColor.GREEN + name + " saved to file and sent confirming message!");
										plugin.php.Send("txt", "Confirming", "Please contact a admin or operator once you receive this message to confirm that your info was entered correctly!", plugin.getConfig().getString("carriers." + carrier), number);
									}
									else
									{
										player.sendMessage(ChatColor.YELLOW + name + "'s info was saved but the confirming txt was not sent because his carrier is not in the system!");
									}
								}
								else
								{
									sender.sendMessage(ChatColor.RED + "Invalid usage!");
									sender.sendMessage(ChatColor.RED + "/textalert register [name] [carrier] [email] [number]");
								}
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "You dont have permission to register someone to the contacts database!");
							}
						}
						else
						{
							sender.sendMessage("You must be a player to use this command!");
						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Invalid usage!");
						sender.sendMessage(ChatColor.RED + "/textalert register [name] [carrier] [email] [number]");
					}
					return true;
				}
				else if (args[0].equals("personal"))
				{
					if (sender instanceof Player)
					{
						Player player = (Player) sender;
						if (plugin.hasPermission(player, "textalert.personal", true))
						{
							if (args.length > 1)
							{
								String name = args[1];
								String msg = "";
								if (args.length > 2)
								{
									msg = args[2];
									for (int i=3;i < args.length;i++)
									{
										msg = msg + " " + args[i];
									}
								}
								if (msg != null)
								{
									if (plugin.getConfig().getString("numbers." + name + ".pref").equals("txt"))
									{
										String address = plugin.getConfig().getString("carriers." + plugin.getConfig().getString("numbers." + name + ".carrier"));
										String number = plugin.getConfig().getString("numbers." + name + ".number");
										if (address != null && number != null && msg != null)
										{
											plugin.php.Send("txt","Personal", msg , address, number);
											sender.sendMessage(ChatColor.GREEN + "Message sent!");
										}
										else
										{
											sender.sendMessage(ChatColor.RED + "No player by that name is on file!");
										}
									}
									else if (plugin.getConfig().getString("numbers." + name + ".pref").equals("email"))
									{
										String address = plugin.getConfig().getString("numbers." + name + ".email");
										String number = plugin.getConfig().getString("numbers." + name + ".number");
										if (address != null && number != null && msg != null)
										{
											plugin.php.Send("email","Personal", msg , address, number);
											sender.sendMessage(ChatColor.GREEN + "Message sent!");
										}
										else
										{
											sender.sendMessage(ChatColor.RED + "No player by that name is on file!");
										}
									}
								}
								return true;
							}
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You dont have permission to use that command!");
							return true;
						}
					}
					else
					{
						if (args.length > 1)
						{
							String name = args[1];
							String msg = "";
							if (args.length > 2)
							{
								msg = args[2];
								for (int i=3;i < args.length;i++)
								{
									msg = msg + " " + args[i];
								}
							}
							if (msg != null)
							{
								if (plugin.getConfig().getString("numbers." + name + ".pref").equals("txt"))
								{
									String address = plugin.getConfig().getString("carriers." + plugin.getConfig().getString("numbers." + name + ".carrier"));
									String number = plugin.getConfig().getString("numbers." + name + ".number");
									if (address != null && number != null && msg != null)
									{
										plugin.php.Send("txt","Personal", msg , address, number);
										sender.sendMessage(ChatColor.GREEN + "Message sent!");
									}
									else
									{
										sender.sendMessage(ChatColor.RED + "No player by that name is on file!");
									}
								}
								else if (plugin.getConfig().getString("numbers." + name + ".pref").equals("email"))
								{
									String address = plugin.getConfig().getString("numbers." + name + ".email");
									String number = plugin.getConfig().getString("numbers." + name + ".number");
									if (address != null && number != null && msg != null)
									{
										plugin.php.Send("email","Personal", msg , address, number);
										sender.sendMessage(ChatColor.GREEN + "Message sent!");
									}
									else
									{
										sender.sendMessage(ChatColor.RED + "No player by that name is on file!");
									}
								}
							}
							return true;
						}
					}
				}
				else
				{
					if (sender instanceof Player)
					{
						sender.sendMessage(ChatColor.RED + "**Invalid Command**");
						sender.sendMessage(ChatColor.GREEN + "/version: outputs the version");
						sender.sendMessage(ChatColor.GREEN + "/reload: reloads the config*");
						sender.sendMessage(ChatColor.GREEN + "/personal <who> <message>: sends a personal text message");
						sender.sendMessage(ChatColor.GREEN + "/help: displays this screen");
						return true;
					}
					else
					{
						sender.sendMessage("**Invalid Command**");
						sender.sendMessage("/version: outputs the version");
						sender.sendMessage("/reload: reloads the config*");
						sender.sendMessage("/personal <who> <message>: sends a personal text message");
						sender.sendMessage("/help: displays this screen");
						return true;
					}
				}
			}
			else
			{
				if (sender instanceof Player)
				{
					sender.sendMessage(ChatColor.RED + "Invalid usage!");
					sender.sendMessage(ChatColor.RED + "Valid sub commands: reload, version, help, personal");
					return true;
				}
				else
				{
					sender.sendMessage("Invalid usage!");
					sender.sendMessage("Valid sub commands: reload, version, help, personal");
					return true;
				}
			}
		}
		else if (cmd.getName().equalsIgnoreCase("modrequest"))
		{
			Player receiver = null;
			
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				
				if (plugin.getConfig().getBoolean("logtoconsole",false))
				{
					plugin.info("- " + player.getName() + " requested a mod!");
				}
				
				List<Player> mods = new ArrayList<Player>();
				for (int i=0;i<plugin.getServer().getOnlinePlayers().length;i++)
				{
					Player user = plugin.getServer().getOnlinePlayers()[i];
					if (user != null)
					{
						List<String> groups = plugin.getConfig().getStringList("permissions.modrequestgroups");
						if (groups.size() > 0)
						{
							for (int g=0;g<groups.size();g++)
							{
								String group = groups.get(g);
								if (plugin.permission.playerInGroup(user, group))
								{
									mods.add(user);
									plugin.info(user.getName() + " added!");
								}
							}
						}
					}
				}
				
				if (mods.size() == 0)
				{
					for (int i=0;i<plugin.getServer().getOfflinePlayers().length;i++)
					{
						OfflinePlayer offline = plugin.getServer().getOfflinePlayers()[i];
						if (offline != null)
						{
							plugin.info(offline.getName() + " was found offline!");
							Player online = offline.getPlayer();
							if (online != null)
							{
								List<String> groups = plugin.getConfig().getStringList("permissions.modrequestgroups");
								if (groups.size() > 0)
								{
									for (int g=0;g<groups.size();g++)
									{
										String group = groups.get(g);
										if (plugin.permission.playerInGroup(online, group))
										{
											mods.add(online);
											plugin.info(online.getName() + " added!");
										}
									}
								}
							}
						}
					}
				}
				
				if (mods.size() > 0)
				{
					if (mods.size() > 1)
					{
						receiver = mods.get((int) (1 + (Math.random() * (mods.size() - 1))));
					}
					else
					{
						receiver = mods.get(0);
					}
					
					if (plugin.getConfig().getBoolean("logtoconsole", false))
					{
						plugin.info("Contacting " + receiver.getName() + "...");
					}
					
					if (receiver != null)
					{
						if (receiver.isOnline())
						{
							receiver.sendMessage(ChatColor.BLUE + "[ModRequest] " + player.getName() + " has requested a moderator!");
							player.sendMessage(ChatColor.GREEN + receiver.getName() + "  has been contacted!");
						}
						else
						{
							String number = plugin.getConfig().getString("numbers." + receiver.getName() + ".number");
							String address = null;
							String pref = plugin.getConfig().getString("numbers." + receiver.getName() + ".pref");
							if (pref.equals("txt"))
							{
								address = plugin.getConfig().getString("carriers." + plugin.getConfig().getString("numbers." + receiver.getName() + ".carrier"));
							}
							else
							{
								address = plugin.getConfig().getString("numbers." + receiver.getName() + ".email");
							}
							if (address != null)
							{
								plugin.php.Send(pref, "Mod request", player.getName() + " has requested a moderator!", address, number);
								if (plugin.getConfig().getBoolean("logtoconsole",false))
								{
									plugin.info("Sent mod request notice to ''" + receiver.getName() + "''!");
								}
								player.sendMessage(ChatColor.GREEN + "No moderators are online");
								player.sendMessage(ChatColor.GREEN + receiver.getName() + "  has been contacted and should be here shortly!");
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Unable to contact a moderator!");
							}
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Unable to contact a moderator!");
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Unable to contact a moderator!");
				}
				
				return true;
			}
			else
			{
				sender.sendMessage("You cant use this command from console!");
				return true;
			}
		
		}
		return false;
	}

}
