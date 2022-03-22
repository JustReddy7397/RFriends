package ga.justreddy.wiki.rfriendsspigot.helpers

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PlaceholderHelper : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "RFriends"
    }

    override fun getAuthor(): String {
        return "JustReddy"
    }

    override fun getVersion(): String {
        return ga.justreddy.wiki.rfriendsspigot.plugin.description.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {

        if(player == null) return ""

        val uuid = player.uniqueId.toString()

        when(params){
            "count" -> dataHelper.getFriends(uuid)
            "online" -> dataHelper.isOnline(uuid)

        }

        return null
    }

}