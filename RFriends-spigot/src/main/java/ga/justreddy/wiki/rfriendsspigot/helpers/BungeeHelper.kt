package ga.justreddy.wiki.rfriendsbungee.helpers

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import ga.justreddy.wiki.rfriendsspigot.plugin
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil


lateinit var bungeeHelper: BungeeHelper

class BungeeHelper : ChatUtil {

    init {
        bungeeHelper = this
    }

    fun sendMessage(name: String, message: String) {

        if (name.isEmpty()) return
        if(message.isEmpty()) return

        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
        out.writeUTF("Message")
        try{
            out.writeUTF(name)
        }catch (ignored: NullPointerException) {}
        out.writeUTF(c(message))

        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        Bukkit.getPlayer(name)?.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())

    }


    fun sendMessage(name: String, vararg textComponents: TextComponent) {
        if (name.isEmpty()) return
        if(textComponents.isEmpty()) return

        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
        out.writeUTF("Message")
        try{
            out.writeUTF(name)
        }catch (ignored: NullPointerException) {}
        out.writeUTF(c(textComponents.contentToString()))

        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        Bukkit.getPlayer(name)?.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
    }


}