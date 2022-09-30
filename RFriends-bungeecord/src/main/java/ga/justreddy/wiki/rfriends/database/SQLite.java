package ga.justreddy.wiki.rfriends.database;


import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import ga.justreddy.wiki.rfriends.api.FriendMessageEvent;
import ga.justreddy.wiki.rfriends.api.FriendRequestEvent;
import ga.justreddy.wiki.rfriends.utils.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class SQLite implements Database {

  private final Connection connection;

  @SneakyThrows
  public SQLite() {
    connection = DriverManager.getConnection(
        "jdbc:sqlite:" + RFriendsBungeeCord.getInstance().getDataFolder().getAbsolutePath()
            + "/data/database.db");
    connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS rfriends_friends "
        + "(uuid VARCHAR(100), "
        + "name VARCHAR(100),"
        + "friends LONGTEXT,"
        + "online BOOLEAN,"
        + "count INT(100),"
        + "requests BOOLEAN"
        + ")");
    connection.createStatement().executeUpdate(
        "CREATE TABLE IF NOT EXISTS rfriends_requests (sender VARCHAR(100), recipient VARCHAR(100) time LONG(100000000))");
  }

  @SneakyThrows
  @Override
  public void createPlayerData(ProxiedPlayer player) {
    if(hasPlayerData(player)) return;

    connection.createStatement().executeUpdate("INSERT INTO rfriends_friends (uuid, name, friends, online, count, requests) VALUES ("
        + "'"+player.getUniqueId().toString()+"',"
        + "'"+player.getName()+"',"
        + "'"+""+"',"
        + "'"+true+"',"
        + "'"+0+"',"
        + "'"+true+"')");
  }

  @SneakyThrows
  @Override
  public boolean hasPlayerData(ProxiedPlayer player) {
    ResultSet rs = connection.createStatement().executeQuery(
        "SELECT * FROM rfriends_friends WHERE uuid='" + player.getUniqueId().toString() + "'");
    return rs.next();
  }

  @SneakyThrows
  @Override
  public void sendRequest(ProxiedPlayer sender, ProxiedPlayer recipient) {
    if (sender == recipient) {
      sender.sendMessage(Utils.format(
          getPlugin().getMessagesConfig().getConfig().getString("error.not-yourself")));
      return;
    }
    if (recipient == null) {
      sender.sendMessage(
          Utils.format(getPlugin().getMessagesConfig().getConfig().getString("error.offline")));
      return;
    }

    if (isAlreadyFriend(sender, recipient)) {
      sender.sendMessage(Utils.format(
          getPlugin().getMessagesConfig().getConfig().getString("error.already-friends")));
      return;
    }

    if (getFriendCount(sender) >= 100) {
      sender.sendMessage(Utils.format(
          getPlugin().getMessagesConfig().getConfig().getString("error.max-friends-sender")));
      return;
    }

    if (getFriendCount(recipient) >= 100) {
      sender.sendMessage(Utils.format(
          getPlugin().getMessagesConfig().getConfig().getString("error.max-friends-recipient")));
      return;
    }

    if (!hasFriendRequestsEnabled(recipient)) {
      sender.sendMessage(Utils.format(
          getPlugin().getMessagesConfig().getConfig().getString("error.friend-request-disabled")));
      return;
    }

    FriendRequestEvent event = new FriendRequestEvent(sender, recipient);
    getPlugin().getProxy().getPluginManager().callEvent(event);
    if (event.isCancelled()) return;

    List<TextComponent> componentList = new ArrayList<>();
    for (String line : getPlugin().getMessagesConfig().getConfig().getStringList("friend-request.received.text")) {
      if (line.contains("<buttons>")) {
        TextComponent textComponent = new TextComponent("");
        for (String key : getPlugin().getMessagesConfig().getConfig()
            .getSection("friend-request.received.buttons").getKeys()) {
          Configuration section = getPlugin().getMessagesConfig().getConfig()
              .getSection("friend-request.received.buttons." + key);
          TextComponent button = new TextComponent(
              ChatColor.translateAlternateColorCodes('&', section.getString("text")));

          try{
            if (section.getSection("click") != null) {
              Action.valueOf(section.getString("click.action"));
              button.setClickEvent(new ClickEvent(Action.valueOf(section.getString("click.action")),
                  section.getString("click.value")));
            }
          }catch (IllegalArgumentException ignored) {
          }


          if (section.getSection("hover") != null) {
            if (section.getString("hover.type").equals("SHOW_TEXT")) {
              button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                  ChatColor.translateAlternateColorCodes('&', section.getString("hover.value"))
              )));
            }
          }
          textComponent.addExtra(button);
          textComponent.addExtra(" ");
        }
        componentList.add(textComponent);
      }
      if (!line.contains("<buttons>")) {
        componentList.add((TextComponent) Utils.format(line.replaceAll("<sender>", Utils.getPlayerName(sender))));
      }
    }
    for (TextComponent textComponent : componentList) {
      recipient.sendMessage(textComponent);
    }
    for (String message : getPlugin().getMessagesConfig().getConfig().getStringList("friend-request.sent")) {
      sender.sendMessage(Utils.format(message.replaceAll("<recipient>", Utils.getPlayerName(recipient))));
    }

    connection.createStatement().executeUpdate("INSERT INTO rfriends_requests (sender, recipient, time) VALUES ('"+sender.getUniqueId().toString()+"',"
        + " '"+recipient.getUniqueId().toString()+"', "
        + "'"+Utils.getDurationMS(getPlugin().getSettingsConfig().getConfig().getString("request-time"))+"')");

  }

  @SneakyThrows
  @Override
  public void acceptRequest(ProxiedPlayer acceptor, ProxiedPlayer sender) {
    if (acceptor == sender) {
      sender.sendMessage(Utils.format(
          getPlugin().getMessagesConfig().getConfig().getString("error.not-yourself")));
      return;
    }



  }

  @SneakyThrows
  @Override
  public void denyRequest(ProxiedPlayer denier, ProxiedPlayer sender) {

  }

  @SneakyThrows
  @Override
  public boolean hasSendRequest(ProxiedPlayer sender, ProxiedPlayer recipient) {
    ResultSet rs = connection.createStatement().executeQuery(
        "SELECT * FROM rfriends_requests WHERE sender='" + sender.getUniqueId().toString()
            + "' AND recipient='" + recipient.getUniqueId().toString() + "'");
    return rs.next();
  }

  @SneakyThrows
  @Override
  public boolean isAlreadyFriend(ProxiedPlayer sender, ProxiedPlayer recipient) {
    PreparedStatement ps = connection.prepareStatement("SELECT * FROM rfriends_friends WHERE uuid='" + sender.getUniqueId().toString() + "'");
    ResultSet rs = ps.executeQuery();
    return rs.getString("friends").contains(recipient.getUniqueId().toString());
  }

  @SneakyThrows
  @Override
  public void removeFriend(ProxiedPlayer remover, ProxiedPlayer recipient) {

  }

  @SneakyThrows
  @Override
  public void sendMessage(ProxiedPlayer sender, ProxiedPlayer recipient, String message) {

    if (sender == recipient) {
      sender.sendMessage(Utils.format(
          getPlugin().getMessagesConfig().getConfig().getString("error.not-yourself")));
      return;
    }

    if (recipient == null) {
      sender.sendMessage(
          Utils.format(getPlugin().getMessagesConfig().getConfig().getString("error.offline")));
      return;
    }

    FriendMessageEvent event = new FriendMessageEvent(sender, recipient, Utils.format(message));
    getPlugin().getProxy().getPluginManager().callEvent(event);
    if (event.isCancelled()) return;

    sender.sendMessage(Utils.format(getPlugin().getMessagesConfig().getConfig().getString("friend-message.to").replaceAll("<recipient>", Utils.getPlayerName(recipient)).replaceAll("<message>", message)));
    recipient.sendMessage(Utils.format(getPlugin().getMessagesConfig().getConfig().getString("friend-message.from").replaceAll("<sender>", Utils.getPlayerName(sender)).replaceAll("<message>", message)));
  }

  @SneakyThrows
  @Override
  public void toggleStatus(ProxiedPlayer player) {
    ResultSet rs = connection.createStatement().executeQuery(
        "SELECT * FROM rfriends_friends WHERE uuid='" + player.getUniqueId().toString() + "'");
    if (!rs.next()) return;
    connection.createStatement().executeUpdate("UPDATE requests SET online='"+!isOnline(player)+"' WHERE uuid='"+player.getUniqueId().toString()+"'");

  }

  @SneakyThrows
  @Override
  public void toggleFriendRequests(ProxiedPlayer player) {
    ResultSet rs = connection.createStatement().executeQuery(
        "SELECT * FROM rfriends_friends WHERE uuid='" + player.getUniqueId().toString() + "'");
    if (!rs.next()) return;
    connection.createStatement().executeUpdate("UPDATE requests SET requests='"+!hasFriendRequestsEnabled(player)+"' WHERE uuid='"+player.getUniqueId().toString()+"'");
  }

  @SneakyThrows
  @Override
  public int getFriendCount(ProxiedPlayer player) {
    ResultSet rs = connection.createStatement().executeQuery(
        "SELECT * FROM rfriends_friends WHERE uuid='" + player.getUniqueId().toString() + "'");
    if (!rs.next()) return 0;
    return rs.getInt("count");
  }

  @SneakyThrows
  @Override
  public boolean hasFriendRequestsEnabled(ProxiedPlayer player) {
    ResultSet rs = connection.createStatement().executeQuery(
        "SELECT * FROM rfriends_friends WHERE uuid='" + player.getUniqueId().toString() + "'");
    if (!rs.next()) return true;
    return rs.getBoolean("requests");
  }

  @SneakyThrows
  @Override
  public boolean isOnline(ProxiedPlayer player) {
    ResultSet rs = connection.createStatement().executeQuery(
        "SELECT * FROM rfriends_friends WHERE uuid='" + player.getUniqueId().toString() + "'");
    if (!rs.next()) return false;
    return rs.getBoolean("online");  }

  @SneakyThrows
  @Override
  public List<Requests> getCurrentRequests() {
    List<Requests> list = new ArrayList<>();
    ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM rfriends_requests");
    while (resultSet.next()) {
      Requests requests = new Requests(
          UUID.fromString(resultSet.getString("sender")),
          UUID.fromString(resultSet.getString("recipient")),
          resultSet.getLong("time")
      );
      list.add(requests);
    }
    return list;
  }

  @SneakyThrows
  @Override
  public void removeRequest(String sender, String recipient) {
    connection.createStatement().executeUpdate("DELETE FROM rfriends_requests WHERE sender='"+sender+"' AND recipient='"+recipient+"'");
  }

  @SneakyThrows
  @Override
  public void close() {
    connection.close();
  }
}
