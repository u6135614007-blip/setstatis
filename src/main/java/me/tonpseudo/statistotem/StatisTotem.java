package me.tonpseudo.statistotem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;
public class StatisTotem extends JavaPlugin implements Listener, CommandExecutor {
    private final HashMap<UUID, Integer> counts = new HashMap<>();
    private final HashMap<UUID, Location> statis = new HashMap<>();
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("setstatis").setExecutor(this);
        getCommand("removestatis").setExecutor(this);
        getCommand("totemcount").setExecutor(this);
        getCommand("resettotem").setExecutor(this);
    }
    @EventHandler
    public void onTotemPop(EntityResurrectEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();
        int count = counts.getOrDefault(uuid, 0) + 1;
        counts.put(uuid, count);
        player.sendMessage("Totems pop: " + count + "/3");
        if (count >= 3) {
            Location loc = statis.get(uuid);
            if (loc != null) {
                Block block = loc.getBlock();
                Material old = block.getType();
                block.setType(Material.REDSTONE_BLOCK);
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    block.setType(old);
                }, 20L);
            }
            counts.put(uuid, 0);
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "setstatis":
                if (args.length != 4) return false;
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) return true;
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                Location loc = new Location(target.getWorld(), x, y, z);
                statis.put(target.getUniqueId(), loc);
                sender.sendMessage("Statis set pour " + target.getName());
                return true;
            case "removestatis":
                if (args.length != 1) return false;
                Player p1 = Bukkit.getPlayer(args[0]);
                if (p1 != null) {
                    statis.remove(p1.getUniqueId());
                    sender.sendMessage("Statis supprimée.");
                }
                return true;
            case "totemcount":
                if (args.length != 1) return false;
                Player p2 = Bukkit.getPlayer(args[0]);
                if (p2 != null) {
                    sender.sendMessage("Totems: " +
                            counts.getOrDefault(p2.getUniqueId(), 0));
                }
                return true;
            case "resettotem":
                if (args.length != 1) return false;
                Player p3 = Bukkit.getPlayer(args[0]);
                if (p3 != null) {
                    counts.put(p3.getUniqueId(), 0);
                    sender.sendMessage("Compteur reset.");
                }
                return true;
        }
        return false;
    }
}
