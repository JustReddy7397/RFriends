package ga.justreddy.wiki.rfriendsspigot.commands

import ga.justreddy.wiki.rfriendsspigot.enums.Messages
import ga.justreddy.wiki.rfriendsspigot.helpers.command.BaseCommand
import ga.justreddy.wiki.rfriendsspigot.helpers.friendHelper
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil

class MessageCommand : ChatUtil, BaseCommand(
    "message",
    "Message a friend",
    "/f message <player> <message>",
    "rfriends.command.message",
    true,
    "msg"
) {

    override fun onCommand(player: Player, args: Array<String>) {
        try{
            val friend = Bukkit.getPlayer(args[1])!!
            val builder: StringBuilder = StringBuilder()
            for (i in 2 until args.size) {
                builder.append(args[i]).append(" ")
            }

            val msg = builder.toString()
            friendHelper.sendMessage(player, friend, msg)


        }catch (e: IndexOutOfBoundsException) {
            player.sendMessage(Messages.INVALID_ARGUMENTS.toString().replace("%syntax%", getSyntax()))
        }

    }

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        // Player only cmd, no need for this function
    }
}