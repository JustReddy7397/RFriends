package ga.justreddy.wiki.rfriendsspigot.exception

import ga.justreddy.wiki.rfriendsspigot.plugin
import org.bukkit.Bukkit

class InvalidDatabaseException : RuntimeException("Invalid Database Type! The plugin will now disable...") {

    init {
        Bukkit.getPluginManager().disablePlugin(plugin)
    }

}