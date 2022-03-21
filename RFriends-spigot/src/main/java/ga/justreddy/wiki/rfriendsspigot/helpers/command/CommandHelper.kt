package ga.justreddy.wiki.rfriendsspigot.helpers.command

import ga.justreddy.wiki.rfriendsspigot.commands.AcceptCommand
import ga.justreddy.wiki.rfriendsspigot.commands.AddCommand
import ga.justreddy.wiki.rfriendsspigot.commands.GuiCommand
import ga.justreddy.wiki.rfriendsspigot.commands.RemoveCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.reflections.Reflections
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil

class CommandHelper : CommandExecutor, ChatUtil {

    private val commands: MutableList<BaseCommand> = ArrayList()
    private var noPermissionMessage: String = ""


    init {
        commands.add(AcceptCommand())
        commands.add(AddCommand())
        commands.add(GuiCommand())
        commands.add(RemoveCommand())
        noPermissionMessage = c("&cYou need the %permission% permission to run this command");
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size > 0) {
            for (i in 0 until getCommands().size) {
                if (getCommands()[i].isPlayersOnly()) {
                    if (sender is Player) {
                        if ((args[0].equals(
                                getCommands()[i].getName(),
                                ignoreCase = true
                            )) || (getCommands()[i].getAliases().contains(args[0]))
                        ) {
                            if (!getCommands()[i].isPermissionEmpty()) {
                                if (!sender.hasPermission(getCommands()[i].getPermission())) {
                                    sender.sendMessage(
                                        c(
                                            noPermissionMessage.replace(
                                                "%permission%".toRegex(),
                                                getCommands().get(i).getPermission()
                                            )
                                        )
                                    )
                                    return true
                                } else {
                                    getCommands()[i].onCommand(sender, args)
                                }
                            } else {
                                getCommands()[i].onCommand(sender, args)
                            }
                        }
                    }
                } else {
                    if (args[0].equals(getCommands()[i].getName(), ignoreCase = true) || getCommands()[i].getAliases()
                            .contains(
                                args[0]
                            )
                    ) {
                        getCommands()[i].onCommand(sender, args)
                    }
                }
            }
        } else {
            return true
        }
        return true
    }

    private fun getCommands(): MutableList<BaseCommand> {
        return commands
    }

}