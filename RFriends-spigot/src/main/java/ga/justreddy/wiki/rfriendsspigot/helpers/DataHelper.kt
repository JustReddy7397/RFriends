package ga.justreddy.wiki.rfriendsspigot.helpers

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import ga.justreddy.wiki.rfriendsspigot.databaseManager
import ga.justreddy.wiki.rfriendsspigot.mongoHelper
import ga.justreddy.wiki.rfriendsspigot.plugin
import org.bson.Document
import org.bukkit.entity.Player
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList

lateinit var dataHelper: DataHelper

class DataHelper {

    init {
        dataHelper = this
    }

    /**
     * Gets all the friends of a player
     * @param friend the friend
     */

    fun getFriends(uuid: String) : List<String> {
        val friends = mutableListOf<String>()
        if(plugin.isMongoConnected){
            val document: Document? = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
            if (document != null) {
                friends.addAll(document.getList("friends", String::class.java))
            }
        }else{

            val friendsRaw: String = getFriendListRaw(uuid)
            if(friendsRaw.isNotEmpty()) {
                val friendies: List<String> = friendsRaw.split(";")
                friends.addAll(friendies)
            }
        }

        return friends
    }

    // Stuff for one friend

    /**
     * @param friend the friend
     */
    fun getName(uuid: String) : String {
        if(plugin.isMongoConnected) {
            val document: Document? = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
            if(document != null){
                return document.getString("name")
            }
        }else{
            val rs: ResultSet = databaseManager.getResult("SELECT * FROM friends WHERE UUID='${uuid}'")

        }
        return ""
    }

    fun isOnline(uuid: String) : Boolean {

        if (plugin.isMongoConnected) {
            val document: Document = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
                ?: return false
            return document.getBoolean("online")
        }

        return false
    }

    fun getFriendCount(uuid: String) : Int {

        if(plugin.isMongoConnected) {
            val document: Document = mongoHelper.getDatabase("friends").find(Document("uuid", uuid)).first()
                ?: return 0
            return document.getInteger("count")
        }else{

        }

        return 0
    }




    // For loading stuff

    fun loadPlayerData(player: Player) {

        if(plugin.isMongoConnected) {
            val document: Document? = mongoHelper.getDatabase("friends").find(Document("uuid", player.uniqueId.toString())).first()
            if(document != null) mongoHelper.getDatabase("friends").updateOne(Filters.eq("uuid", player.uniqueId.toString()), Updates.set("name", player.name))

            else mongoHelper.getDatabase("friends").insertOne(
                Document("uuid", player.uniqueId.toString())
                    .append("name", player.name)
                    .append("friends", ArrayList<String>())
                    .append("count", 0)
                    .append("online", true)
                    .append("allowfriendrequests", true)
            )
        }else{
            if(!exists(player)) databaseManager.update("UPDATE friends SET NAME='${player.name}' WHERE UUID='${player.uniqueId}'")
            else databaseManager.update("INSERT INTO friends (UUID, NAME, FRIENDS, COUNT, ONLINE, ALLOWFRIENDREQUESTS)" +
                    " VALUES " +
                    "('${player.uniqueId}', '${player.name}', '"+""+"', '${0}', '${true}', '${true}')")
        }

    }

    // Sql Stuff

    private fun getFriendListRaw(uuid: String) : String {

        val rs: ResultSet = databaseManager.getResult("SELECT * FROM friends WHERE UUID='${uuid}'")

        try{
            if (rs.next()) {
                return rs.getString("FRIENDS")
            }
        }catch (e: SQLException) {
            e.printStackTrace()
        }

        return ""
    }

    private fun exists(player: Player) : Boolean {

        val rs: ResultSet = databaseManager.getResult("SELECT * FROM friends WHERE UUID='${player.uniqueId}'")

        try {
            return !rs.next()
        }catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
    }

}