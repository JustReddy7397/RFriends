package ga.justreddy.wiki.rfriends.command;

import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import ga.justreddy.wiki.rfriends.command.commands.AddCommand;
import ga.justreddy.wiki.rfriends.command.commands.MessageCommand;
import ga.justreddy.wiki.rfriends.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BaseCommand extends Command {

  private static final List<ICommand> commands = new ArrayList<>();

  public BaseCommand(String name) {
    super(name, null, "f", "friend");
    registerCommands(
        new AddCommand(),
        new MessageCommand()
    );
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!(sender instanceof ProxiedPlayer)) {
      return;
    }

    ProxiedPlayer player = (ProxiedPlayer) sender;

    if (args.length > 0) {
      ICommand command = getCommand(args[0]);
      if (command == null) {
        ProxiedPlayer proxiedPlayer = RFriendsBungeeCord.getInstance().getProxy().getPlayer(args[0]);
        RFriendsBungeeCord.getInstance().getDatabase().sendRequest(player, proxiedPlayer);
        return;
      }

        // RUN COMMAND
        if (command.permission() != null && !player.hasPermission(command.permission())) {
          player.sendMessage(Utils.format(
              RFriendsBungeeCord.getInstance().getMessagesConfig().getConfig()
                  .getString("error.no-permission")));
          return;
        }
      command.onCommand(player, args);
    }
  }

  private void registerCommands(ICommand... iCommands) {
    commands.addAll(Arrays.asList(iCommands));
  }

  public ICommand getCommand(String name) {
    ICommand iCommand = null;
    for (ICommand command : commands) {
      if ((command.name().equals(name)) || (Arrays.asList(command.aliases()).contains(name))) iCommand = command;
    }
    return iCommand;
  }

}
