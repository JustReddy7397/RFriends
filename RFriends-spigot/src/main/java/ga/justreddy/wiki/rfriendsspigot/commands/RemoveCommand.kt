package ga.justreddy.wiki.rfriendsspigot.commands

import ga.justreddy.wiki.rfriendsspigot.enums.Messages
import ga.justreddy.wiki.rfriendsspigot.helpers.command.BaseCommand
import ga.justreddy.wiki.rfriendsspigot.helpers.friendHelper
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil

class RemoveCommand : ChatUtil, BaseCommand(
    "remove",
    "Remove a friend",
    "/f remove <name>",
    "rfriends.command.remove",
    true,
){

    override fun onCommand(player: Player, args: Array<String>) {
        try{

            val p: OfflinePlayer = Bukkit.getOfflinePlayer(args[1])

            friendHelper.removeFriend(player, p)

        }catch (e: IndexOutOfBoundsException) {
            player.sendMessage(Messages.INVALID_ARGUMENTS.toString().replace("%syntax%", getSyntax()))

        }
    }

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        // Player only cmd, no need for this function
    }

}