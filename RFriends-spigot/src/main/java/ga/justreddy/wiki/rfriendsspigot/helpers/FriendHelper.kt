package ga.justreddy.wiki.rfriendsspigot.helpers

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import ga.justreddy.wiki.rfriendsspigot.configManager
import ga.justreddy.wiki.rfriendsspigot.databaseManager
import ga.justreddy.wiki.rfriendsspigot.enums.Messages
import ga.justreddy.wiki.rfriendsspigot.events.custom.FriendAddEvent
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
import java.io.ByteArrayInputStream
import java.io.DataInputStream
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
            TODO("SQL")
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

        } else {
            TODO("SQL")
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
            TODO("SQL")
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
}