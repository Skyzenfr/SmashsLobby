package skyzen;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import skyzen.cmds.*;
import skyzen.listeners.*;
import skyzen.others.*;
import skyzen.menus.InventoryListener;
import skyzen.menus.jeux.Jeux;
import skyzen.menus.jeux.JeuxBowDragon;
import skyzen.menus.Teleportation;
import skyzen.menus.jeux.jeuxItemListener;
import skyzen.playercache.PlayerData;
import skyzen.playercache.PlayerDataManager;
import skyzen.rank.SqlConnection;

import java.util.HashMap;
import java.util.Map;

public class Lobby extends JavaPlugin implements Listener {

    public SqlConnection sql;
    public PlayerDataManager dataManager = new PlayerDataManager(this);
    public Map<Player, PlayerData> dataPlayers = new HashMap<>();
    ConsoleCommandSender consoleSender = getServer().getConsoleSender();

    public void onEnable() {
        sql = new SqlConnection(this, "jdbc:mysql://", "localhost", "smashs", "root", "");
        sql.connection();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ActionBarMessage(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(sql), this);
        getServer().getPluginManager().registerEvents(new MessageListener(sql), this);
        getServer().getPluginManager().registerEvents(new WorldListener(sql), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(sql), this);
        getServer().getPluginManager().registerEvents(new MoveSystem(sql), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(sql), this);
        getServer().getPluginManager().registerEvents(new CommandBlockerListener(), this);
        getServer().getPluginManager().registerEvents(new jeuxItemListener(), this);

        getCommand("coins").setExecutor(new CoinsCMD(sql));
        getCommand("grade").setExecutor(new GradeCMD(sql));
        getCommand("tp").setExecutor(new Teleportation(sql));
        getCommand("ban").setExecutor(new BanCMD(sql));
        getCommand("disponibilite").setExecutor(new DispoCMD(sql));
        getCommand("msg").setExecutor(new MsgCMD());
        getCommand("jeux").setExecutor(new Jeux());
        getCommand("list").setExecutor(new ListCMD());
        getCommand("bowdragon").setExecutor(new JeuxBowDragon());

        consoleSender.sendMessage(ChatColor.GREEN + "==================================================");
        consoleSender.sendMessage(ChatColor.GREEN + "----------------- PIXELS PALACE ------------------");
        consoleSender.sendMessage(ChatColor.GREEN + "==================================================" + ChatColor.RESET);
    }

    public void onDisable() {
        sql.disconnect();
    }

    @EventHandler
    public void onJoinAvant(PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        sql.createAccount(p);
        dataManager.loadPlayerData(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        dataManager.savePlayerData(p);
        for (Player p1 : Bukkit.getOnlinePlayers())
            p1.setLevel(Bukkit.getOnlinePlayers().size() - 1);
    }
}
