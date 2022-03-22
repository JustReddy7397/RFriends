package ga.justreddy.wiki.rfriendsspigot.helpers.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseCommand(name: String, description: String, syntax: String, permission: String, playersOnly: Boolean, vararg aliases: String?) {

    private val name: String
    private val description: String
    private val syntax: String
    private val permission: String
    private val playersOnly: Boolean
    private val aliases: MutableList<String> = ArrayList()

    init {
        this.name = name
        this.description = description
        this.syntax = syntax
        this.permission = permission
        this.playersOnly = playersOnly
        if(aliases != null) this.aliases.addAll(listOf((aliases).toString()))
    }

    fun getName() : String {
        return this.name
    }

    fun getDescription() : String {
        return this.description
    }

    fun getSyntax() : String {
        return this.syntax
    }

    fun getPermission() : String {
        return this.permission
    }

    fun isPermissionEmpty() : Boolean {
        return permission.isEmpty()
    }

    fun isPlayersOnly() : Boolean {
        return playersOnly
    }

    fun getAliases() : MutableList<String> {
        return this.aliases
    }

    abstract fun onCommand(player: Player, args: Array<String>)

    abstract fun onCommand(sender: CommandSender, args: Array<String>)


}