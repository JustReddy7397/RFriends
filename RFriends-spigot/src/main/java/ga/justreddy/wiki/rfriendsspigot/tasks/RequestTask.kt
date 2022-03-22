package ga.justreddy.wiki.rfriendsspigot.tasks

import ga.justreddy.wiki.rfriendsspigot.databaseManager
import ga.justreddy.wiki.rfriendsspigot.enums.Messages
import ga.justreddy.wiki.rfriendsspigot.helpers.bungeeHelper
import ga.justreddy.wiki.rfriendsspigot.mongoHelper
import ga.justreddy.wiki.rfriendsspigot.plugin
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.sql.ResultSet
import java.util.*

class RequestTask : BukkitRunnable() {

    override fun run() {

        val date = Date()

        if(plugin.isMongoConnected) {
            for (document in mongoHelper.getDatabase("requests").find()) {
                if(date.time < document.getLong("Time")) continue
                val player: String? = document.getString("Player")
                val friend: String? = document.getString("Friend")
                if(player == null) continue
                if(friend == null) continue
                sendMessage(Bukkit.getPlayer(player), friend)
                mongoHelper.getDatabase("requests").deleteOne(Document("Player", player).append("Friend", friend))
            }
        }else{
            val rs: ResultSet = databaseManager.getResult("SELECT * FROM requests")
            while (rs.next()) {
                if(date.time <= rs.getLong("TIME")) continue
                val player: String? = rs.getString("Player")
                val friend: String? = rs.getString("Friend")
                if(player == null) continue
                if(friend == null) continue
                sendMessage(Bukkit.getPlayer(player), friend)
                databaseManager.update("DELETE FROM requests WHERE PLAYER='$player' AND AND FRIEND='$friend'")
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