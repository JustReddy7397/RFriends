package ga.justreddy.wiki.rfriendsspigot.menus

import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import wiki.justreddy.ga.reddyutils.menu.ChestMenu

class PlayerMenu(player: String) : ChestMenu(ChatColor.translateAlternateColorCodes('&', "&aEdit friend $player"), 27) {

    private val player: String

    init {
        this.player = player
    }

    override fun handleMenu(e: InventoryClickEvent) {
    }

    override fun setMenuItems(p: Player) {
    }
}