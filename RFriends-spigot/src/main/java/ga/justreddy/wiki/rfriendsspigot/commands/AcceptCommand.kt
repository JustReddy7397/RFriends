package ga.justreddy.wiki.rfriendsspigot.commands

import ga.justreddy.wiki.rfriendsspigot.helpers.command.BaseCommand
import ga.justreddy.wiki.rfriendsspigot.helpers.friendHelper
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil
import java.lang.IndexOutOfBoundsException

class AcceptCommand : ChatUtil, BaseCommand(
    "accept",
    "Accept a friend request",
    "/f accept <name>",
    "rfriends.command.accept",
    true
) {

    override fun onCommand(player: Player, args: Array<String>) {
        try{

            val p: OfflinePlayer = Bukkit.getOfflinePlayer(args[1])

            friendHelper.addFriend(player, p)

        }catch (e: IndexOutOfBoundsException) {
            player.sendMessage(c(getSyntax()))
        }

    }

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        // Player only cmd, no need for this function
    }

}