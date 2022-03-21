package ga.justreddy.wiki.rfriendsspigot

import ga.justreddy.wiki.rfriendsbungee.events.PlayerJoinEvent
import ga.justreddy.wiki.rfriendsbungee.helpers.MongoHelper
import ga.justreddy.wiki.rfriendsbungee.exception.InvalidDatabaseException
import ga.justreddy.wiki.rfriendsbungee.helpers.BungeeHelper
import ga.justreddy.wiki.rfriendsbungee.helpers.DataHelper
import ga.justreddy.wiki.rfriendsbungee.helpers.FriendHelper
import ga.justreddy.wiki.rfriendsbungee.helpers.command.CommandHelper
import ga.justreddy.wiki.rfriendsspigot.tasks.RequestTask
import org.bukkit.scheduler.BukkitTask
import pluginlib.DependentJavaPlugin
import wiki.justreddy.ga.reddyutils.config.ConfigManager
import wiki.justreddy.ga.reddyutils.manager.DatabaseManager
import wiki.justreddy.ga.reddyutils.menu.events.MenuEvent
import java.util.*
lateinit var plugin: RFriends
lateinit var configManager: ConfigManager
lateinit var databaseManager: DatabaseManager
lateinit var commandHelper: CommandHelper
lateinit var mongoHelper: MongoHelper

class RFriends : DependentJavaPlugin() {

    var isMongoConnected: Boolean = false
    var isBungecoordEnabled: Boolean = false

    private val databaseTypes = arrayOf("mongodb", "sql", "mysql")

    override fun onEnable() {
        // Plugin startup logic
        plugin = this;
        loadHelpers()
        configManager = ConfigManager()
        configManager.createFolder(this)
        configManager.registerFile(this, "database", "database")
        configManager.registerFile(this, "messages", "messages")
        configManager.registerFile(this, "settings", "settings")
 /*       val match = Arrays.stream(databaseTypes).anyMatch(configManager.getFile("database").config.getString("storage")?.lowercase())
        if(!match){
            throw InvalidDatabaseException()
        }


*/
        val type: String = configManager.getFile("database").config.getString("storage")?.lowercase()!!
        val match = Arrays.stream(databaseTypes).anyMatch(type::contains)
        if(!match) throw InvalidDatabaseException()
        if(configManager.getFile("database").config.getString("storage").equals("mongodb", ignoreCase = true)) {
            mongoHelper = configManager.getFile("database").config.getString("mongodb.uri")?.let { MongoHelper(it) }!!
            mongoHelper.connect()
            isMongoConnected = true
        }else if (configManager.getFile("database").config.getString("storage").equals("sql", ignoreCase = true)){
            databaseManager = DatabaseManager()
            databaseManager.connectH2(this, "data/friends.db")
        }else if(configManager.getFile("database").config.getString("storage").equals("mysql", ignoreCase = true)){
            databaseManager = DatabaseManager()
            databaseManager.connectMysQL(
                configManager.getFile("database").config.getString("mysql.database"),
                configManager.getFile("database").config.getString("mysql.username"),
                configManager.getFile("database").config.getString("mysql.password"),
                configManager.getFile("database").config.getString("mysql.host"),
                configManager.getFile("database").config.getInt("mysql.port")
            )
        }

        commandHelper = CommandHelper()
        getCommand("friends")?.setExecutor(commandHelper)
        registerEvents()
        val bukkitTask: BukkitTask = RequestTask().runTaskTimer(this, 0, 100L)
    }

    private fun loadHelpers() {
        BungeeHelper()
        DataHelper()
        FriendHelper()
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerJoinEvent(), this)
        server.pluginManager.registerEvents(MenuEvent(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic

    }

}