package ga.justreddy.wiki.rfriends.utils;

import com.asosyalbebe.moment4j.Moment;
import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Utils {

  public static BaseComponent format(String message) {
    return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
  }

  public static void sendTextComponents(ProxiedPlayer player, TextComponent... components) {
    player.sendMessage(components);
  }

  @SneakyThrows
  public static String getPlayerName(ProxiedPlayer player) {
    if (RFriendsBungeeCord.getInstance().getLuckPerms() == null) return player.getName();
    CompletableFuture<User> userFuture = RFriendsBungeeCord.getInstance().getLuckPerms().getUserManager().loadUser(player.getUniqueId());
    User user = userFuture.get();
    if (user == null) return player.getName();
    String groupName = user.getPrimaryGroup();
    Group group = RFriendsBungeeCord.getInstance().getLuckPerms().getGroupManager().getGroup(groupName);
    if (group == null) return player.getName();
    String prefix = group.getCachedData().getMetaData().getPrefix();
    if (prefix == null) return player.getName();
    return ChatColor.translateAlternateColorCodes('&', doesPrefixHaveOnlyColorCode(prefix) ? prefix + player.getName() : prefix + " " + player.getName());
  }

  private static boolean doesPrefixHaveOnlyColorCode(String prefix) {
    return (prefix.startsWith("&0") && prefix.endsWith("&0")) ||
        (prefix.startsWith("&1") && prefix.endsWith("&1")) ||
        (prefix.startsWith("&2") && prefix.endsWith("&2")) ||
        (prefix.startsWith("&3") && prefix.endsWith("&3")) ||
        (prefix.startsWith("&4") && prefix.endsWith("&4")) ||
        (prefix.startsWith("&5") && prefix.endsWith("&5")) ||
        (prefix.startsWith("&6") && prefix.endsWith("&6")) ||
        (prefix.startsWith("&7") && prefix.endsWith("&7")) ||
        (prefix.startsWith("&8") && prefix.endsWith("&8")) ||
        (prefix.startsWith("&9") && prefix.endsWith("&9")) ||
        (prefix.startsWith("&a") && prefix.endsWith("&a")) ||
        (prefix.startsWith("&c") && prefix.endsWith("&c")) ||
        (prefix.startsWith("&d") && prefix.endsWith("&d")) ||
        (prefix.startsWith("&e") && prefix.endsWith("&e")) ||
        (prefix.startsWith("&f") && prefix.endsWith("&f")) ||
        (prefix.startsWith("&r") && prefix.endsWith("&r")) ||
        (prefix.startsWith("&m") && prefix.endsWith("&m")) ||
        (prefix.startsWith("&o") && prefix.endsWith("&o")) ||
        (prefix.startsWith("&l") && prefix.endsWith("&l")) ||
        (prefix.startsWith("&k") && prefix.endsWith("&k")) ||
        (prefix.startsWith("&n") && prefix.endsWith("&n"));
  }

  public static long getDurationMS(String time) {
    long ms = 0;
    if (time.toLowerCase().contains("s"))
      ms = (Long.parseLong(time.replace("s", "")) * 1000) + System.currentTimeMillis();
    if (time.toLowerCase().contains("m") && !time.toLowerCase().contains("o"))
      ms = ((Long.parseLong(time.replace("m", "")) * 1000) * 60) + System.currentTimeMillis();
    if (time.toLowerCase().contains("h"))
      ms = (((Long.parseLong(time.replace("h", "")) * 1000) * 60) * 60) + System.currentTimeMillis();
    if (time.toLowerCase().contains("d"))
      ms = ((((Long.parseLong(time.replace("d", "")) * 1000) * 60) * 60) * 24) + System.currentTimeMillis();
    if (time.toLowerCase().contains("w"))
      ms = (((((Long.parseLong(time.replace("w", "")) * 1000) * 60) * 60) * 24) * 7) + System.currentTimeMillis();
    if (time.toLowerCase().contains("m") && time.toLowerCase().contains("o"))
      ms = (((((Long.parseLong(time.replace("mo", "")) * 1000) * 60) * 60) * 24) * 30) + System.currentTimeMillis();
    if (time.toLowerCase().contains("y"))
      ms = ((((((Long.parseLong(time.replace("y", "")) * 1000) * 60) * 60) * 24) * 7) * 52) + System.currentTimeMillis();

    return ms;
  }


}
