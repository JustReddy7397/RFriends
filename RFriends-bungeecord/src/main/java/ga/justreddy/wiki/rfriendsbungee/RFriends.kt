package ga.justreddy.wiki.rfriendsbungee

import ga.justreddy.wiki.rfriendsbungee.helpers.ConfigHelper
import net.md_5.bungee.api.plugin.Plugin

lateinit var plugin: RFriends
lateinit var configHelper: ConfigHelper

class RFriends : Plugin() {


    override fun onEnable() {
        TODO("BungeeCord Implementation")
    /*        plugin = this
        configHelper = ConfigHelper()
        configHelper.getConfig("database.yml")
        configHelper.getConfig("messages.yml")
        configHelper.getConfig("settings.yml")*/
    }

    override fun onDisable() {

    }

}