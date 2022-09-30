package ga.justreddy.wiki.rfriends.command.commands;

import ga.justreddy.wiki.rfriends.command.ICommand;
import ga.justreddy.wiki.rfriends.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MessageCommand implements ICommand {

  @Override
  public String name() {
    return "message";
  }

  @Override
  public String description() {
    return "Send a message to a friend";
  }

  @Override
  public String syntax() {
    return "/f msg <friend>";
  }

  @Override
  public String permission() {
    return "rfriends.command.message";
  }

  @Override
  public String[] aliases() {
    return new String[]{"msg"};
  }

  @Override
  public void onCommand(ProxiedPlayer player, String[] args) {

    try {
      ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(args[1]);
/*      if (!getDatabase().isAlreadyFriend(player, recipient)) {
        player.sendMessage(Utils.format(getPlugin().getMessagesConfig().getConfig().getString("error.not-friends")));
        return;
      }*/
      StringBuilder message = new StringBuilder();
      for (int i = 2; i < args.length; i++) {
        message.append(args[i]).append(" ");
      }
      getDatabase().sendMessage(player, recipient, message.toString());
    }catch (IndexOutOfBoundsException ex) {
      player.sendMessage(Utils.format(getPlugin().getMessagesConfig().getConfig().getString("error.invalid-arguments").replaceAll("<syntax>", syntax())));
    }
  }
}
