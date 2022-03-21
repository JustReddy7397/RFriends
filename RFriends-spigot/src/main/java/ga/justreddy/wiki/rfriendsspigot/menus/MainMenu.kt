package ga.justreddy.wiki.rfriendsspigot.menus

import com.cryptomorin.xseries.XMaterial
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta
import wiki.justreddy.ga.reddyutils.menu.ChestMenu
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil


class MainMenu : ChestMenu(
    ChatColor.translateAlternateColorCodes('&', "&aFriends Menu"), 27), ChatUtil {

    override fun handleMenu(e: InventoryClickEvent) {
        if(e.whoClicked is Player) {
            val player: Player = e.whoClicked as Player
            if(e.slot == 11) {
                AnvilMenu().openRequestMenu(player)
            }
            if(e.slot == 13) {
                FriendsMenu().open(player)
            }
            if(e.slot == 15) {
                AnvilMenu().openRemoveMenu(player)
            }
        }
    }

    override fun setMenuItems(p: Player) {
        val friends = XMaterial.PLAYER_HEAD.parseItem()
        val friend = friends!!.itemMeta as SkullMeta?
        friend!!.owner = p.name
        friend!!.setDisplayName(c("&aView all your friends"))
        friends.itemMeta = friend
        val request = XMaterial.PAPER.parseItem()
        val requestMeta = request!!.itemMeta
        requestMeta!!.setDisplayName(c("&aAdd a friend"))
        request.itemMeta = requestMeta
        val remove = XMaterial.BARRIER.parseItem()
        val removeMeta = remove!!.itemMeta
        removeMeta!!.setDisplayName(c("&cRemove a friend"))
        remove.itemMeta = removeMeta
        inventory.setItem(13, friends)
        inventory.setItem(11, request)
        inventory.setItem(15, remove)
        setFillerGlass()
    }

}