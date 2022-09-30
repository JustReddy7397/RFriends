package ga.justreddy.wiki.rfriends.command.commands;

import ga.justreddy.wiki.rfriends.command.ICommand;
import ga.justreddy.wiki.rfriends.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AddCommand implements ICommand {

  @Override
  public String name() {
    return "add";
  }

  @Override
  public String description() {
    return "Sends a friend request to the specified person.";
  }

  @Override
  public String syntax() {
    return "/f add <name>";
  }

  @Override
  public String permission() {
    return "rfriends.command.add";
  }

  @Override
  public String[] aliases() {
    return new String[]{};
  }

  @Override
  public void onCommand(ProxiedPlayer player, String[] args) {

    try {
      ProxiedPlayer recipient = getPlugin().getProxy().getPlayer(args[1]);
      getDatabase().sendRequest(player, recipient);
    }catch (IndexOutOfBoundsException ex) {
      player.sendMessage(Utils.format(getPlugin().getMessagesConfig().getConfig().getString("error.invalid-arguments").replaceAll("<syntax>", syntax())));
    }

  }
}
