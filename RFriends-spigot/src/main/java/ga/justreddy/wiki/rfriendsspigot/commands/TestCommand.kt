package ga.justreddy.wiki.rfriendsspigot.commands

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import ga.justreddy.wiki.rfriendsspigot.helpers.command.BaseCommand
import ga.justreddy.wiki.rfriendsspigot.plugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand : BaseCommand("test", "test", "test", "", true) {

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        TODO("Not yet implemented")
    }

    override fun onCommand(player: Player, args: Array<String>) {
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
        out.writeUTF("Message")
        out.writeUTF("ReddyTest")
        out.writeUTF("Hello")
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord")
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())

    }

}