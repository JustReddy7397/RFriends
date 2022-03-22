package ga.justreddy.wiki.rfriendsspigot.enums

import ga.justreddy.wiki.rfriendsspigot.configManager
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil

enum class Messages(private val path: String) : ChatUtil {

    // General
    GENERAL_FRIEND_REQUEST_TIMEOUT("General.friend-request-timeout"),
    GENERAL_FRIEND_REQUEST_ACCEPT("General.friend-request-accept"),
    GENERAL_FRIEND_REMOVED_PLAYER1("General.removed.player1"),
    GENERAL_FRIEND_REMOVED_PLAYER2("General.removed.player2"),
    GENERAL_NOT_FRIEND("General.not-friend"),
    GENERAL_ALREADY_FRIEND("General.already-friend"),
    GENERAL_FRIEND_NO_REQUEST("General.friend-no-request"),
    GENERAL_FRIEND_ALREADY_REQUESTED("General.friend-already-requested"),
    GENERAL_FRIEND_JOIN("General.friend-join"),
    GENERAL_FRIEND_LEAVE("General.friend-leave"),
    GENERAL_PLAYER_NOT_FOUND("General.player-not-found"),
    GENERAL_PLAYER_MAX_FRIENDS("General.max-friends"),
    GENERAL_FRIEND_REQUEST_DISABLED("General.friend-request-disabled"),
    GENERAL_FRIEND_REQUEST_ON("General.friend-request-on"),
    GENERAL_FRIEND_REQUEST_OFF("General.friend-request-off"),
    GENERAL_FRIEND_OFFLINE("General.friend-offline"),
    GENERAL_NOT_YOURSELF("General.friend-request-yourself"),
    MESSAGES_TO("Messages.to"),
    MESSAGES_FROM("Messages.from"),
    DATABASE_NOT_EXISTS("Database.not-in-database"),
    INVALID_ARGUMENTS("General.invalid-arguments")
    ;


    init {

    }

    private val config: FileConfiguration = configManager.getFile("messages").config
    private val prefix: String = c(config.getString("prefix"));

    fun toString(player: Player): String? {

        val message: String? = config.getString(path)

        if (message == null || message.isEmpty()) return null

        return c(
            message.replace("%prefix%", prefix()).replace("%player%", player.name)
                .replace("%displayname%", player.displayName)
        )

    }

    fun toString(player: OfflinePlayer?): String? {

        val message: String? = config.getString(path)

        if (message == null || message.isEmpty()) return ""
        val name: String? = player!!.name
        print(message)
        print(name.toString() + " hey")
        print(prefix())
        //print(player.player!!.displayName + " hi")
        return c(message.replace("%prefix%", prefix()).replace("%player%", name.toString()))


    }

    override fun toString(): String {
        val message: String? = config.getString(path)

        if (message == null || message.isEmpty()) return ""
        print(message)
        return c(message.replace("%prefix%", prefix()));

    }

    private fun prefix(): String {
        if (prefix.isEmpty()) return ""
        return prefix
    }

}