package ga.justreddy.wiki.rfriendsspigot.helpers

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import ga.justreddy.wiki.rfriendsspigot.configManager
import ga.justreddy.wiki.rfriendsspigot.databaseManager
import ga.justreddy.wiki.rfriendsspigot.enums.Messages
import ga.justreddy.wiki.rfriendsspigot.events.custom.FriendAddEvent
import ga.justreddy.wiki.rfriendsspigot.events.custom.FriendMessageEvent
import ga.justreddy.wiki.rfriendsspigot.events.custom.FriendRemoveEvent
import ga.justreddy.wiki.rfriendsspigot.mongoHelper
import ga.justreddy.wiki.rfriendsspigot.plugin
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*


lateinit var friendHelper: FriendHelper

class FriendHelper : ChatUtil {

    init {
        friendHelper = this
    }

    /**
     * @param player The player who sends the request
     * @param friend The player who will receive the request
     */

    fun sendFriendRequest(player: Player, friend: OfflinePlayer) {
        if (player.uniqueId == friend.uniqueId) {
            player.sendMessage(Messages.GENERAL_NOT_YOURSELF.toString())
            return
        }

        if (plugin.isMongoConnected) {
            val friendDocument: Document? =
                mongoHelper.getDatabase("friends").find(Document("uuid", friend.uniqueId.toString())).first()
            if (friendDocument == null) {
                player.sendMessage(Messages.GENERAL_PLAYER_NOT_FOUND.toString(friend))
                return
            }

            val document: Document =
                mongoHelper.getDatabase("friends").find(Document("uuid", player.uniqueId.toString())).first()!!
            if (document.getList("friends", String::class.java).contains(friend.uniqueId.toString())) {
                player.sendMessage(Messages.GENERAL_ALREADY_FRIEND.toString(friend))
                return
            }

            val doc: Document? =
                mongoHelper.getDatabase("requests").find(Document("Player", player.uniqueId.toString())).first()
            if (doc != null) {
                if (doc.getString("Friend").equals(friend.uniqueId.toString(), ignoreCase = true)) {
                    player.sendMessage(Messages.GENERAL_FRIEND_ALREADY_REQUESTED.toString(friend));
                    return;
                }
            }

            val section: ConfigurationSection? =
                configManager.getFile("settings").config.getConfigurationSection("max-friends")
            if (section != null) {
                for (key: String in section.getKeys(false)) {
                    val permission: String = key
                    val max: Int = configManager.getFile("settings").config.getInt("max-friends.$key.max")
                    if (!player.hasPermission(permission) && document.getInteger("count") >= max) {
                        player.sendMessage(Messages.GENERAL_PLAYER_MAX_FRIENDS.toString())
                        return
                    }
                }
            }

            mongoHelper.getDatabase("requests").insertOne(
                Document("Player", player.uniqueId.toString())
                    .append("Friend", friend.uniqueId.toString())
                    .append("Time", (Date().time + 300000))
            )

            for (i in 0 until configManager.getFile("messages").config.getStringList("General.friend-request").size) {
                player.sendMessage(
                    c(friend.name?.let {
                        configManager.getFile("messages").config.getStringList("General.friend-request")[i].replace(
                            "%player%",
                            it
                        )
                    })
                )
            }


            if (friend.isOnline) {
                val f = TextComponent(c("&eFriend request from ${friend.name}\n"))
                val accept = TextComponent(c("&a[ACCEPT]"))
                accept.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    ComponentBuilder(c("&aClick to accept the friend request")).create()
                )
                accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + player.name)
                val line = TextComponent(c(" &9- "))
                val deny = TextComponent(c("&c[DENY]"))
                deny.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    ComponentBuilder(c("&cClick to deny the friend request")).create()
                )
                deny.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + player.name)
                friend.player!!.spigot().sendMessage(f, accept, line, deny)
            }


        } else {
            if(!dataHelper.exists(friend.uniqueId.toString())){
                player.sendMessage(Messages.GENERAL_PLAYER_NOT_FOUND.toString(friend))
                return
            }

            val friendListRaw: String = dataHelper.getFriendListRaw(player.uniqueId.toString())
            if (friendListRaw.contains(friend.uniqueId.toString())) {
                player.sendMessage(Messages.GENERAL_ALREADY_FRIEND.toString(friend))
                return
            }

            var alreadySend = false

            try {
                val rs: ResultSet = databaseManager.getResult(
                    "SELECT * FROM requests WHERE PLAYER='" + player.uniqueId.toString() + "'"
                )
                if (rs.next()) {
                    alreadySend = rs.getString("FRIEND").equals(friend.uniqueId.toString(), ignoreCase = true)
                }
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }

            if (alreadySend) {
                player.sendMessage(Messages.GENERAL_FRIEND_ALREADY_REQUESTED.toString(friend))
                return
            }

            val section: ConfigurationSection? =
                configManager.getFile("settings").config.getConfigurationSection("max-friends")
            if (section != null) {
                for (key: String in section.getKeys(false)) {
                    val permission: String = key
                    val max: Int = configManager.getFile("settings").config.getInt("max-friends.$key.max")
                    if (!player.hasPermission(permission) && get("*", "friends", "UUID", player.uniqueId.toString(), "COUNT") as Int >= max) {
                        player.sendMessage(Messages.GENERAL_PLAYER_MAX_FRIENDS.toString())
                        return
                    }
                }
            }

            var hasFriendRequestsEnabled = false

            try {
                val rs: ResultSet = databaseManager
                    .getResult("SELECT * FROM friends WHERE UUID='" + friend.uniqueId.toString() + "'")
                if (rs.next()) {
                    hasFriendRequestsEnabled = rs.getString("ALLOWFRIENDREQUESTS").equals("true", ignoreCase = true)
                }
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }

            if (!hasFriendRequestsEnabled) {
                player.sendMessage(Messages.GENERAL_FRIEND_REQUEST_DISABLED.toString(friend))
                return
            }

            databaseManager.update(
                "INSERT INTO requests (PLAYER, FRIEND, TIME) VALUES ('" + player.uniqueId.toString() + "', '" + friend.uniqueId.toString() + "', '" + (Date().time + 300000).toString() + "')"
            )

            for (i in 0 until configManager.getFile("messages").config.getStringList("General.friend-request").size) {
                player.sendMessage(
                    c(friend.name?.let {
                        configManager.getFile("messages").config.getStringList("General.friend-request")[i].replace(
                            "%player%",
                            it
                        )
                    })
                )
            }


            if (friend.isOnline) {
                val f = TextComponent(c("&eFriend request from ${friend.name}\n"))
                val accept = TextComponent(c("&a[ACCEPT]"))
                accept.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    ComponentBuilder(c("&aClick to accept the friend request")).create()
                )
                accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + player.name)
                val line = TextComponent(c(" &9- "))
                val deny = TextComponent(c("&c[DENY]"))
                deny.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    ComponentBuilder(c("&cClick to deny the friend request")).create()
                )
                deny.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + player.name)
                friend.player!!.spigot().sendMessage(f, accept, line, deny)
            }

        }

    }

    /**
     * @param friend The player the request has been sent to
     * @param player The player who has sent the request
     */

    fun denyFriendRequest(friend: Player, player: OfflinePlayer) {
        if (friend.uniqueId == player.uniqueId) {
            friend.sendMessage(Messages.GENERAL_NOT_YOURSELF.toString())
            return
        }

        if (plugin.isMongoConnected) {
            val requestDocument: Document? = mongoHelper.getDatabase("requests")
                .find(Document("Player", player.uniqueId.toString()).append("Friend", friend.uniqueId.toString()))
                .first()
            if (requestDocument == null) {
                friend.sendMessage(Messages.GENERAL_FRIEND_NO_REQUEST.toString(player));
                return
            }

            mongoHelper.getDatabase("requests").deleteOne(
                Document("Player", player.uniqueId.toString())
                    .append("Friend", friend.uniqueId.toString())
            )

        } else {
            var alreadySend = false

            try {
                val rs: ResultSet = databaseManager.getResult(
                    "SELECT * FROM requests WHERE PLAYER='" + player.uniqueId.toString() + "'"
                )
                if (rs.next()) {
                    alreadySend = rs.getString("FRIEND").equals(friend.uniqueId.toString(), ignoreCase = true)
                }
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }

            if (!alreadySend) {
                friend.sendMessage(Messages.GENERAL_FRIEND_NO_REQUEST.toString(player))
                return
            }

            databaseManager.update("DELETE FROM requests WHERE PLAYER='${player.uniqueId}' AND FRIEND='${friend.uniqueId}'");

        }

    }

    /**
     * @param friend The player the requests has been sent to
     * @param player The player who has sent the request
     */

    fun addFriend(friend: Player, player: OfflinePlayer) {
        if (player.uniqueId == friend.uniqueId) {
            friend.sendMessage(Messages.GENERAL_NOT_YOURSELF.toString())
            return
        }

        if (plugin.isMongoConnected) {
            val document: Document? = mongoHelper.getDatabase("requests")
                .find(Document("Player", player.uniqueId.toString()).append("Friend", friend.uniqueId.toString()))
                .first()
            print(Bukkit.getOfflinePlayer(UUID.fromString(document?.getString("Player"))).uniqueId == player.uniqueId)
            print(Bukkit.getOfflinePlayer(UUID.fromString(document?.getString("Friend"))).uniqueId == friend.uniqueId)
            if (document == null) {
                friend.sendMessage(Messages.GENERAL_FRIEND_NO_REQUEST.toString(player))
                return
            }

            val section: ConfigurationSection? =
                configManager.getFile("settings").config.getConfigurationSection("max-friends")
            if (section != null) {
                for (key: String in section.getKeys(false)) {
                    val permission: String = key
                    val max: Int = configManager.getFile("settings").config.getInt("max-friends.$key.max")
                    if (!friend.hasPermission(permission) && mongoHelper.getDatabase("friends")
                            .find(Document("uuid", friend.uniqueId.toString())).first()!!.getInteger("count") >= max
                    ) {
                        friend.sendMessage(Messages.GENERAL_PLAYER_MAX_FRIENDS.toString())
                        return
                    }
                }
            }

            val event = FriendAddEvent(friend, player)
            if (event.isCancelled) return
            Bukkit.getPluginManager().callEvent(event)
            mongoHelper.getDatabase("friends")
                .updateOne(
                    Filters.eq("uuid", friend.uniqueId.toString()),
                    Updates.addToSet("friends", player.uniqueId.toString())
                )
            mongoHelper.getDatabase("friends")
                .updateOne(
                    Filters.eq("uuid", player.uniqueId.toString()),
                    Updates.addToSet("friends", friend.uniqueId.toString())
                )
            mongoHelper.getDatabase("friends")
                .updateOne(
                    Filters.eq("uuid", friend.uniqueId.toString()),
                    Updates.set("count", (dataHelper.getFriendCount(friend.uniqueId.toString()) + 1))
                )
            mongoHelper.getDatabase("friends")
                .updateOne(
                    Filters.eq("uuid", player.uniqueId.toString()),
                    Updates.set("count", (dataHelper.getFriendCount(player.uniqueId.toString()) + 1))
                )
            if (player.isOnline) {
                player.player?.sendMessage(Messages.GENERAL_FRIEND_REQUEST_ACCEPT.toString(friend));
            }

        } else {
            var alreadySend = false

            try {
                val rs: ResultSet = databaseManager.getResult(
                    "SELECT * FROM requests WHERE PLAYER='" + player.uniqueId.toString() + "'"
                )
                if (rs.next()) {
                    alreadySend = rs.getString("FRIEND").equals(friend.uniqueId.toString(), ignoreCase = true)
                }
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }

            if (!alreadySend) {
                friend.sendMessage(Messages.GENERAL_FRIEND_ALREADY_REQUESTED.toString(player))
                return
            }

            val section: ConfigurationSection? =
                configManager.getFile("settings").config.getConfigurationSection("max-friends")
            if (section != null) {
                for (key: String in section.getKeys(false)) {
                    val permission: String = key
                    val max: Int = configManager.getFile("settings").config.getInt("max-friends.$key.max")
                    if (!friend.hasPermission(permission) && get("*", "friends", "UUID", friend.uniqueId.toString(), "COUNT") as Int >= max) {
                        friend.sendMessage(Messages.GENERAL_PLAYER_MAX_FRIENDS.toString())
                        return
                    }
                }
            }

            databaseManager.update("DELETE FROM requests WHERE PLAYER='${player.uniqueId}' AND FRIEND='${friend.uniqueId}'");

            var playerListRaw = dataHelper.getFriendListRaw(player.uniqueId.toString())
            var friendListRaw = dataHelper.getFriendListRaw(friend.uniqueId.toString())

            playerListRaw += "${friend.uniqueId};"
            friendListRaw += "${player.uniqueId};"
            val event = FriendAddEvent(friend, player)
            if (event.isCancelled) return
            Bukkit.getPluginManager().callEvent(event)
            update("friends", "FRIENDS", playerListRaw, "UUID", player.uniqueId.toString())
            update("friends", "FRIENDS", friendListRaw, "UUID", friend.uniqueId.toString())
            update("friends", "COUNT", (dataHelper.getFriendCount(player.uniqueId.toString()) + 1) , "UUID", player.uniqueId.toString())
            update("friends", "COUNT", (dataHelper.getFriendCount(friend.uniqueId.toString()) + 1) , "UUID", friend.uniqueId.toString())
            if (player.isOnline) {
                player.player?.sendMessage(Messages.GENERAL_FRIEND_REQUEST_ACCEPT.toString(friend));
            }
        }

    }

    fun removeFriend(player: Player, friend: OfflinePlayer) {
        if (player.uniqueId == friend.uniqueId) {
            player.sendMessage(Messages.GENERAL_NOT_YOURSELF.toString())
            return
        }

        if (plugin.isMongoConnected) {
            val friendDocument: Document? =
                mongoHelper.getDatabase("friends").find(Document("uuid", friend.uniqueId.toString())).first()
            if (friendDocument == null) {
                player.sendMessage(Messages.DATABASE_NOT_EXISTS.toString(friend));
                return;
            }

            val document: Document =
                mongoHelper.getDatabase("friends").find(Document("uuid", player.uniqueId.toString())).first()!!

            if (document.getList("friends", String::class.java).contains(friend.uniqueId.toString())) {
                mongoHelper.getDatabase("friends").updateOne(
                    Filters.eq("uuid", player.uniqueId.toString()),
                    Updates.pull("friends", friend.uniqueId.toString())
                )
                mongoHelper.getDatabase("friends").updateOne(
                    Filters.eq("uuid", friend.uniqueId.toString()),
                    Updates.pull("friends", player.uniqueId.toString())
                )
                mongoHelper.getDatabase("friends").updateOne(
                    Filters.eq("uuid", friend.uniqueId.toString()),
                    Updates.set("count", (dataHelper.getFriendCount(friend.uniqueId.toString()) - 1))
                )
                mongoHelper.getDatabase("friends").updateOne(
                    Filters.eq("uuid", player.uniqueId.toString()),
                    Updates.set("count", (dataHelper.getFriendCount(player.uniqueId.toString()) - 1))
                )

                if (plugin.isBungecoordEnabled) {
                    try {
                        Messages.GENERAL_FRIEND_REMOVED_PLAYER2.toString(player)
                            ?.let { bungeeHelper.sendMessage(friend.name.toString(), it) }
                    } catch (ignored: NullPointerException) {
                    }
                } else {
                    if (friend.isOnline) {
                        friend.player!!.sendMessage(Messages.GENERAL_FRIEND_REMOVED_PLAYER2.toString(player))
                    }
                }
                player.sendMessage(Messages.GENERAL_FRIEND_REMOVED_PLAYER1.toString(friend))
            } else {
                player.sendMessage(Messages.GENERAL_NOT_FRIEND.toString(friend))
            }

        } else {

            var friendListRaw = dataHelper.getFriendListRaw(friend.uniqueId.toString())
            var playerListRaw = dataHelper.getFriendListRaw(player.uniqueId.toString())

            if(!friendListRaw.contains(player.uniqueId.toString())){
                player.sendMessage(Messages.GENERAL_NOT_FRIEND.toString(friend));
                return;
            }

            val event = FriendRemoveEvent(player, friend)
            if(event.isCancelled) return
            plugin.server.pluginManager.callEvent(event)
            friendListRaw = friendListRaw.replace("${player.uniqueId};", "")
            playerListRaw = playerListRaw.replace("${friend.uniqueId};", "")

            update("friends", "FRIENDS", friendListRaw, "UUID", friend.uniqueId.toString())
            update("friends", "FRIENDS", playerListRaw, "UUID", player.uniqueId.toString())
            player.sendMessage(Messages.GENERAL_FRIEND_REMOVED_PLAYER1.toString(friend));
            if (friend.isOnline) {
                friend.player?.sendMessage(Messages.GENERAL_FRIEND_REMOVED_PLAYER2.toString(player));
            }
        }
    }

    fun sendMessage(player: Player, friend: Player, message: String) {
        if(player.uniqueId == friend.uniqueId){
            player.sendMessage(Messages.GENERAL_NOT_YOURSELF.toString())
            return
        }

        if(plugin.isMongoConnected) {
            val document: Document = mongoHelper.getDatabase("friends").find(Document("uuid", player.uniqueId.toString())).first()!!
            if(document.getList("friends", String::class.java).contains(friend.uniqueId.toString())) {
                if (!dataHelper.isOnline(friend.uniqueId.toString())) {
                    player.sendMessage(Messages.GENERAL_FRIEND_OFFLINE.toString(friend));
                    return;
                }

                val event = FriendMessageEvent(player, friend, message)
                if(event.isCancelled) return

                player.sendMessage(Messages.MESSAGES_TO.toString(friend)!!.replace("%message%", message));
                friend.sendMessage(Messages.MESSAGES_FROM.toString(player)!!.replace("%message%", message));
                plugin.server.pluginManager.callEvent(event)
            }else {
                player.sendMessage(Messages.GENERAL_NOT_FRIEND.toString(friend));
                return;
            }
        }else{
            if(!dataHelper.getFriends(player.uniqueId.toString()).contains(friend.uniqueId.toString())) {
                player.sendMessage(Messages.GENERAL_NOT_FRIEND.toString(friend));
                return
            }

            if (!dataHelper.isOnline(friend.uniqueId.toString())) {
                player.sendMessage(Messages.GENERAL_FRIEND_OFFLINE.toString(friend));
                return;
            }

            val event = FriendMessageEvent(player, friend, message)
            if(event.isCancelled) return

            player.sendMessage(Messages.MESSAGES_TO.toString(friend)!!.replace("%message%", message));
            friend.sendMessage(Messages.MESSAGES_FROM.toString(player)!!.replace("%message%", message));
            plugin.server.pluginManager.callEvent(event)

        }

    }

    private fun get(select: String, database: String, where: String, result: String, type: String): Any? {
        val rs: ResultSet =
            databaseManager.getResult("SELECT $select FROM $database WHERE $where='$result'")
        try {
            if (rs.next()) {
                return rs.getObject(type)
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }
        return ""
    }

    private fun update(database: String, set: String, setTo: String, where: String, result: String) {
        databaseManager.update("UPDATE $database SET $set='$setTo' WHERE $where='$result'")
    }
    private fun update(database: String, set: String, setTo: Int, where: String, result: String) {
        databaseManager.update("UPDATE $database SET $set='$setTo' WHERE $where='$result'")
    }

}