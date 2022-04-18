package ga.justreddy.wiki.rfriendsspigot.helpers

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import ga.justreddy.wiki.rfriendsspigot.plugin
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil


lateinit var bungeeHelper: BungeeHelper

class BungeeHelper : ChatUtil {

    init {
        bungeeHelper = this
    }

    fun sendMessage(player: Player, name: String, message: String) {

        if (name.isEmpty()) return
        if(message.isEmpty()) return

        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
        out.writeUTF("Message")
        out.writeUTF(name)
        out.writeUTF(c(message))

        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())

    }



}