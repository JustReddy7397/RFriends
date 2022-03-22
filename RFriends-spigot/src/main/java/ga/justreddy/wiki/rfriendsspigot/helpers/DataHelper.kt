package ga.justreddy.wiki.rfriendsspigot.helpers

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import ga.justreddy.wiki.rfriendsspigot.databaseManager
import ga.justreddy.wiki.rfriendsspigot.enums.Messages
import ga.justreddy.wiki.rfriendsspigot.mongoHelper
import ga.justreddy.wiki.rfriendsspigot.plugin
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.sql.ResultSet
import java.sql.SQLException

lateinit var dataHelper: DataHelper

class DataHelper {

    // TODO SQL

    init {
        dataHelper = this
    }

    /**
     * Gets all the friends of a player
     * @param uuid The UUID of the friend
     */

    fun getFriends(uuid: String): List<String> {
        val friends = mutableListOf<String>()
        if (plugin.isMongoConnected) {
            val document: Document? = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
            if (document != null) {
                friends.addAll(document.getList("friends", String::class.java))
            }
        } else {
            val friendsRaw: String = getFriendListRaw(uuid)
            if (friendsRaw.isNotEmpty()) {
                val friendies: List<String> = friendsRaw.split(";")
                friends.addAll(friendies)
            }
        }

        return friends
    }

    // Stuff for one friend

    /**
     * Gets the name of the friend
     * @param uuid The UUID of the friend
     */
    fun getName(uuid: String): String {
        if (plugin.isMongoConnected) {
            val document: Document? = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
            if (document != null) {
                return document.getString("name")
            }
        } else {
            return get("*", "friends", "UUID", uuid, "NAME") as String
        }
        return ""
    }

    /**
     * Checks if the friend is online or not
     * @param uuid The UUID of the friend
     */

    fun isOnline(uuid: String): Boolean {

        if (plugin.isMongoConnected) {
            val document: Document = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
                ?: return false
            return document.getBoolean("online")
        } else {
            return get("*", "friends", "UUID", uuid, "ONLINE")?.equals("true") ?: false
        }
    }

    /**
     * Gets the friendcount of a friend
     * @param uuid The UUID of the friend
     */

    fun getFriendCount(uuid: String): Int {

        if (plugin.isMongoConnected) {
            val document: Document = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
                ?: return 0
            return document.getInteger("count")
        } else {
            return get("*", "friends", "UUID", uuid, "COUNT") as Int
        }

    }

    /**
     * Toggle the status from online to offline and offline to online
     * @param uuid The UUID of the friend
     */

    fun toggleStatus(uuid: String) {

        if (plugin.isMongoConnected) {
            if (isOnline(uuid)) {
                mongoHelper.getDatabase("friends").updateOne(Filters.eq("uuid", uuid), Updates.set("online", false));
            } else {
                mongoHelper.getDatabase("friends").updateOne(Filters.eq("uuid", uuid), Updates.set("online", true));
            }
        } else {
            if (isOnline(uuid)) {
                update("friends", "ONLINE", "false", "UUID", uuid)
            } else {
                update("friends", "ONLINE", "true", "UUID", uuid)
            }
        }
    }

    fun toggleFriendRequests(uuid: String) {
        if (plugin.isMongoConnected) {
            val document: Document = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first() ?: return
            if (document.getBoolean("allowfriendrequests")) {
                mongoHelper.getDatabase("friends")
                    .updateOne(Filters.eq("uuid", uuid), Updates.set("allowfriendrequests", false))
                Bukkit.getPlayer(uuid)!!.sendMessage(Messages.GENERAL_FRIEND_REQUEST_OFF.toString())
            } else {
                mongoHelper.getDatabase("friends")
                    .updateOne(Filters.eq("uuid", uuid), Updates.set("allowfriendrequests", true))
                Bukkit.getPlayer(uuid)!!.sendMessage(Messages.GENERAL_FRIEND_REQUEST_ON.toString())
            }
        } else {
            val rs: ResultSet = databaseManager
                .getResult("SELECT * FROM friends WHERE UUID='$uuid'")
            var allowFriendRequests = false
            try {
                allowFriendRequests = rs.getString("ALLOWFRIENDREQUESTS").equals("true", ignoreCase = true)
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }

            if (allowFriendRequests) {
                update("friends", "ALLOWFRIENDREQUESTS", "false", "UUID", uuid)
                Bukkit.getPlayer(uuid)!!.sendMessage(Messages.GENERAL_FRIEND_REQUEST_OFF.toString())
            } else {
                update("friends", "ALLOWFRIENDREQUESTS", "true", "UUID", uuid)
                Bukkit.getPlayer(uuid)!!.sendMessage(Messages.GENERAL_FRIEND_REQUEST_ON.toString())
            }
        }
    }


    // For loading stuff

    fun loadPlayerData(player: Player) {

        if (plugin.isMongoConnected) {
            val document: Document? =
                mongoHelper.getDatabase("friends").find(Document("uuid", player.uniqueId.toString())).first()
            if (document != null) mongoHelper.getDatabase("friends")
                .updateOne(Filters.eq("uuid", player.uniqueId.toString()), Updates.set("name", player.name))
            else mongoHelper.getDatabase("friends").insertOne(
                Document("uuid", player.uniqueId.toString())
                    .append("name", player.name)
                    .append("friends", ArrayList<String>())
                    .append("count", 0)
                    .append("online", true)
                    .append("allowfriendrequests", true)
            )
        } else {
            if (!exists(player.uniqueId.toString())) databaseManager.update("UPDATE friends SET NAME='${player.name}' WHERE UUID='${player.uniqueId}'")
            else databaseManager.update(
                "INSERT INTO friends (UUID, NAME, FRIENDS, COUNT, ONLINE, ALLOWFRIENDREQUESTS)" +
                        " VALUES " +
                        "('${player.uniqueId}', '${player.name}', '" + "" + "', '${0}', '${true}', '${true}')"
            )
        }

    }

    // Sql Stuff

    fun getFriendListRaw(uuid: String): String {
        return get("*", "friends", "UUID", uuid, "FRIENDS") as String
    }

    fun exists(uuid: String): Boolean {

        val rs: ResultSet = databaseManager.getResult("SELECT * FROM friends WHERE UUID='${uuid}'")

        try {
            return rs.next()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
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