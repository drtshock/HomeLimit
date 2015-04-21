package com.puzlinc.homelimit;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HomeLimit extends JavaPlugin {

    private static Permission perms;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
    }

    /*
        /homelimit <name> # - where # can be positive or negative to change the amount of sethomes.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length != 2) {
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null || !player.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.DARK_RED.toString() + "Cannot find player: " + args[0]);
            return true;
        }

        Integer toAdd = getNumber(args[1]);
        if(toAdd == null) {
            return false;
        }

        int currentLimit = getLimit(player);
        int newLimit = currentLimit + toAdd;
        perms.playerAdd(player, "essentials.sethome.multiple." + newLimit);
        perms.playerRemove(player, "essentials.sethome.multiple." + currentLimit);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(getConfig().getString("set", "&aYour new home limit is &6%d"), newLimit)));
        sender.sendMessage(String.format(ChatColor.GREEN.toString() + "Set new home limit for %s to %d", player.getName(), newLimit));
        return true;
    }

    private int getLimit(Player player) {
        for (int i = 1; i < getConfig().getInt("max", 20); i++) {
            if(player.hasPermission("essentials.sethome.multiple." + i)) {
                return i;
            }
        }
        return 1;
    }

    private Integer getNumber(String s) {
        try {
            Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
        return Integer.valueOf(s);
    }
}
