package ga.justreddy.wiki.rfriendsbungee.helpers

import ga.justreddy.wiki.rfriendsbungee.plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


class ConfigHelper() {

    private lateinit var configuration: Configuration
    private lateinit var templateconfig: Configuration
    private var configkeys: MutableList<String> = ArrayList()
    private var templatekeys: MutableList<String> = ArrayList()

    fun getConfig(fileName: String) : Configuration {
        if(!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()
        val file = File(plugin.dataFolder, fileName)
        if (!file.exists()) {
            try {
                plugin.getResourceAsStream(fileName).use { `in` -> Files.copy(`in`, file.toPath()) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        try {
            configuration =
                ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(File(plugin.dataFolder, fileName))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (key: String in configuration.keys) {
            configkeys.add(key)
            if (configuration.get(key) is Configuration) {
                iterateKey(key, configkeys, configuration);
            }
        }

        for (key in configkeys) {
            if (!templateconfig.contains(key)) {
                configuration.set(key, null)
            } else if (templateconfig.get(key).javaClass != configuration.get(key).javaClass) {
                configuration.set(key, templateconfig.get(key))
            }
            templatekeys = ArrayList()

            // Get template config file
            templateconfig = ConfigurationProvider.getProvider(YamlConfiguration::class.java)
                .load(plugin.getResourceAsStream(fileName))
            for (tkey in templateconfig.getKeys()) {
                templatekeys.add(tkey!!)
                if (templateconfig.get(tkey) is Configuration) {
                    iterateKey(tkey, templatekeys, templateconfig)
                }
            }
            templatekeys.reverse()
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration::class.java)
                .save(configuration, File(plugin.dataFolder, fileName))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return configuration

    }

    private fun iterateKey(gkey: String, keylist: MutableList<String>, config: Configuration) {
        for (key in config.getSection(gkey).keys) {
            keylist.add("$gkey.$key")
            if (config["$gkey.$key"] is Configuration) {
                iterateKey("$gkey.$key", keylist, config)
            }
        }
    }

}