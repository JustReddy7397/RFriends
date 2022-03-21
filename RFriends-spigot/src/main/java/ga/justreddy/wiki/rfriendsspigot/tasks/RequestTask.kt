package ga.justreddy.wiki.rfriendsspigot.tasks

import ga.justreddy.wiki.rfriendsbungee.enums.Messages
import ga.justreddy.wiki.rfriendsbungee.helpers.bungeeHelper
import ga.justreddy.wiki.rfriendsspigot.mongoHelper
import ga.justreddy.wiki.rfriendsspigot.plugin
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class RequestTask : BukkitRunnable() {

    override fun run() {

        val date = Date()

        if(plugin.isMongoConnected) {
            for (document in mongoHelper.getDatabase("requests").find()) {
                if(date.time < document.getLong("Time")) continue
                val player: Player? = Bukkit.getPlayer(document.getString("Player"))
                val friend: String? = document.getString("Friend")
                sendMessage(player, friend)
                mongoHelper.getDatabase("requests").deleteOne(Document("Player", player).append("Friend", friend))
            }
        }

    }




    private fun sendMessage(player: Player?, friend: String?) {
        if(player == null) return
        if(plugin.isBungecoordEnabled) {
            Messages.GENERAL_FRIEND_REQUEST_TIMEOUT.toString(Bukkit.getOfflinePlayer(UUID.fromString(friend)))
                ?.let { bungeeHelper.sendMessage(player.name, it) }
        }else{
            if(player.isOnline){
                player.sendMessage(Messages.GENERAL_FRIEND_REQUEST_TIMEOUT.toString(Bukkit.getOfflinePlayer(UUID.fromString(friend))))
            }
        }
    }

}