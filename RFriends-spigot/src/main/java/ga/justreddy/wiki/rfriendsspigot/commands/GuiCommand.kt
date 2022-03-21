package ga.justreddy.wiki.rfriendsspigot.commands

import ga.justreddy.wiki.rfriendsspigot.helpers.command.BaseCommand
import ga.justreddy.wiki.rfriendsspigot.menus.MainMenu
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil

class GuiCommand : ChatUtil, BaseCommand(
    "gui",
    "Opens the friend GUI",
    "/f gui",
    "rfriends.command.gui",
    true,
) {

    override fun onCommand(player: Player, args: Array<String>) {

        if(args.isNotEmpty()) {
            MainMenu().open(player)
        }

    }

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        // Player only cmd, no need for this function
    }
}