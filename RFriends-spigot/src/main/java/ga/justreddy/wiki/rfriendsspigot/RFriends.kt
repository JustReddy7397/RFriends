package ga.justreddy.wiki.rfriendsspigot

import ga.justreddy.wiki.rfriendscommon.spigot.DLoaderSpigot
import ga.justreddy.wiki.rfriendscommon.spigot.base.Dependency
import ga.justreddy.wiki.rfriendsspigot.events.PlayerJoinEvent
import ga.justreddy.wiki.rfriendsspigot.exception.InvalidDatabaseException
import ga.justreddy.wiki.rfriendsspigot.helpers.*
import ga.justreddy.wiki.rfriendsspigot.helpers.command.CommandHelper
import ga.justreddy.wiki.rfriendsspigot.tasks.RequestTask
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import wiki.justreddy.ga.reddyutils.config.ConfigManager
import wiki.justreddy.ga.reddyutils.manager.DatabaseManager
import wiki.justreddy.ga.reddyutils.menu.events.MenuEvent
import java.util.*

lateinit var plugin: RFriends
lateinit var configManager: ConfigManager
lateinit var databaseManager: DatabaseManager
lateinit var commandHelper: CommandHelper
lateinit var mongoHelper: MongoHelper

class RFriends : JavaPlugin() {

    var isMongoConnected: Boolean = false
    var isBungecoordEnabled: Boolean = false

    private val databaseTypes = arrayOf("mongodb", "sql", "mysql")

    override fun onLoad() {
        DLoaderSpigot.getInstance().onLoad(this);
        DLoaderSpigot.getInstance().load(Dependency("kotlin-common", "1.6.20-RC", "org.jetbrains.kotlin", "kotlin-stdlib-common"))
        DLoaderSpigot.getInstance().load(Dependency("kotlin-jdk8", "1.6.20-RC", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8"))
        DLoaderSpigot.getInstance().load(Dependency("kotlin", "1.6.20-RC", "org.jetbrains.kotlin", "kotlin-stdlib"))
        DLoaderSpigot.getInstance().load(Dependency("mongo-driver", "3.12.10", "org.mongodb", "mongodb-driver"))
        DLoaderSpigot.getInstance().load(Dependency("mongo-driver-core", "3.12.10", "org.mongodb", "mongodb-driver-core"))
        DLoaderSpigot.getInstance().load(Dependency("bson", "4.4.0", "org.mongodb", "bson"))
        DLoaderSpigot.getInstance().load(Dependency("h2", "1.4.200", "com.h2database", "h2"))
        DLoaderSpigot.getInstance().load(Dependency("xseries", "8.6.1", "com.github.cryptomorin", "XSeries"))
    }

    override fun onEnable() {
        // Plugin startup logic
        plugin = this;
        loadHelpers()
        configManager = ConfigManager()
        configManager.createFolder(this)
        configManager.registerFile(this, "database", "database")
        configManager.registerFile(this, "messages", "messages")
        configManager.registerFile(this, "settings", "settings")
        val type: String = configManager.getFile("database").config.getString("storage")?.lowercase()!!
        val match = Arrays.stream(databaseTypes).anyMatch(type::contains)
        if (!match) throw InvalidDatabaseException()
        if (configManager.getFile("database").config.getString("storage").equals("mongodb", ignoreCase = true)) {
            mongoHelper = configManager.getFile("database").config.getString("mongodb.uri")?.let { MongoHelper(it) }!!
            mongoHelper.connect()
            isMongoConnected = true
        } else if (configManager.getFile("database").config.getString("storage").equals("sql", ignoreCase = true)) {
            databaseManager = DatabaseManager()
            databaseManager.connectH2(this, "data/friends.db")
            databaseManager.update(
                "CREATE TABLE IF NOT EXISTS friends (UUID VARCHAR(100), " +
                        "NAME VARCHAR(100), " +
                        "ONLINE VARCHAR(100)," +
                        " COUNT INT(100)," +
                        " FRIENDS TEXT(200000000)," +
                        " ALLOWFRIENDREQUESTS VARCHAR(100))"
            )
            databaseManager.update(
                "CREATE TABLE IF NOT EXISTS requests (PLAYER VARCHAR(100), " +
                        "FRIEND VARCHAR(100)," +
                        " TIME LONG(100))"
            )
        } else if (configManager.getFile("database").config.getString("storage").equals("mysql", ignoreCase = true)) {
            databaseManager = DatabaseManager()
            databaseManager.connectMysQL(
                configManager.getFile("database").config.getString("mysql.database"),
                configManager.getFile("database").config.getString("mysql.username"),
                configManager.getFile("database").config.getString("mysql.password"),
                configManager.getFile("database").config.getString("mysql.host"),
                configManager.getFile("database").config.getInt("mysql.port")
            )
            databaseManager.update(
                "CREATE TABLE IF NOT EXISTS friends (UUID VARCHAR(100), " +
                        "NAME VARCHAR(100), " +
                        "ONLINE VARCHAR(100)," +
                        " COUNT INT(100)," +
                        " FRIENDS TEXT(200000000)," +
                        " ALLOWFRIENDREQUESTS VARCHAR(100))"
            )
            databaseManager.update(
                "CREATE TABLE IF NOT EXISTS requests (PLAYER VARCHAR(100), " +
                        "FRIEND VARCHAR(100)," +
                        " TIME LONG(100))"
            )
        }

        if(server.pluginManager.getPlugin("PlaceholderAPI") != null) PlaceholderHelper().register()
        isBungecoordEnabled = configManager.getFile("settings").config.getBoolean("bungeecord")
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