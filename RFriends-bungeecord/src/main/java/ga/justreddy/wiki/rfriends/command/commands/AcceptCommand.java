package ga.justreddy.wiki.rfriends.command.commands;

import ga.justreddy.wiki.rfriends.command.ICommand;
import ga.justreddy.wiki.rfriends.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AcceptCommand implements ICommand {

  @Override
  public String name() {
    return "accept";
  }

  @Override
  public String description() {
    return "Accepts a friend request if gotten any";
  }

  @Override
  public String syntax() {
    return "/f accept <recipient>";
  }

  @Override
  public String permission() {
    return "rfriends.command.accept";
  }

  @Override
  public String[] aliases() {
    return new String[]{};
  }

  @Override
  public void onCommand(ProxiedPlayer player, String[] args) {

    try {
      ProxiedPlayer recipient = getPlugin().getProxy().getPlayer(args[1]);
      getDatabase().acceptRequest(player, recipient);
    }catch (IndexOutOfBoundsException ex) {
      player.sendMessage(Utils.format(getPlugin().getMessagesConfig().getConfig().getString("error.invalid-arguments").replaceAll("<syntax>", syntax())));
    }

  }
}
