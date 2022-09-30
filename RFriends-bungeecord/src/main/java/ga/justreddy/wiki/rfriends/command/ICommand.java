package ga.justreddy.wiki.rfriends.command;

import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import ga.justreddy.wiki.rfriends.database.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ICommand {

  String name();

  String description();

  String syntax();

  String permission();

  String[] aliases();

  void onCommand(ProxiedPlayer player, String[] args);

  default RFriendsBungeeCord getPlugin() {
    return RFriendsBungeeCord.getInstance();
  }

  default Database getDatabase() {
    return RFriendsBungeeCord.getInstance().getDatabase();
  }

}
