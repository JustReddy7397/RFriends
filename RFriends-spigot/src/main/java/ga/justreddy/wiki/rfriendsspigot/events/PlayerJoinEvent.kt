package ga.justreddy.wiki.rfriendsbungee.events
import ga.justreddy.wiki.rfriendsbungee.events.custom.FriendJoinEvent
import ga.justreddy.wiki.rfriendsbungee.helpers.bungeeHelper
import ga.justreddy.wiki.rfriendsbungee.helpers.dataHelper
import ga.justreddy.wiki.rfriendsspigot.plugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil

class PlayerJoinEvent : Listener, ChatUtil {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        dataHelper.loadPlayerData(e.player)
/*        if (plugin.isMongoConnected) {
            for (uuid in dataHelper.getFriends(e.player.uniqueId.toString())) {
                if (!dataHelper.isOnline(uuid)) continue
               // if(event.isCancelled) return
            }
        }*/
        val event = FriendJoinEvent(e.player, e.player)

        Bukkit.getPluginManager().callEvent(event)

    }

    @EventHandler
    fun onFriendJoin(e: FriendJoinEvent) {
        if (plugin.isBungecoordEnabled) {
            val name: String = Bukkit.getOfflinePlayer(e.getFriend().uniqueId).name ?: return
            bungeeHelper.sendMessage(e.getPlayer().name, "hi")
        } else {
            Bukkit.getPlayer(e.getFriend().uniqueId)?.sendMessage(c("hi"))
        }
    }

}