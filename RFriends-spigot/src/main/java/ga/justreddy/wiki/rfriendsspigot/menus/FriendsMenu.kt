package ga.justreddy.wiki.rfriendsspigot.menus

import com.cryptomorin.xseries.XMaterial
import ga.justreddy.wiki.rfriendsspigot.helpers.dataHelper
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta
import wiki.justreddy.ga.reddyutils.menu.PaginatedChestMenu
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil


class FriendsMenu : PaginatedChestMenu(
    ChatColor.translateAlternateColorCodes('&', "Your friends"), 54
), ChatUtil {

    override fun handleMenu(e: InventoryClickEvent) {
        e.whoClicked.sendMessage(c("&cComing Soon!"))
    }

    override fun setMenuItems(p: Player?) {
        addMenuBorder()
        for (i in 0 until maxItemsPerPage) {
            index = maxItemsPerPage * page + i
            if (index >= dataHelper.getFriends(p?.uniqueId.toString()).size) break
            if (dataHelper.getFriends(p?.uniqueId.toString())[index].isEmpty()) continue
            val head = XMaterial.PLAYER_HEAD.parseItem()
            val meta = head!!.itemMeta as SkullMeta?
            meta!!.setDisplayName(c("&e" + dataHelper.getName(dataHelper.getFriends(p?.uniqueId.toString())[index])))
            meta.owner = dataHelper.getFriends(p?.uniqueId.toString())[index]
            val lore: MutableList<String> = ArrayList()
            if (dataHelper.isOnline(dataHelper.getFriends(p?.uniqueId.toString())[index])) {
                lore.add("&eStatus: &aOnline")
            } else {
                lore.add("&eStatus: &cOffline")
            }
            lore.add("")
            lore.add("&4Click to edit this friend")
            meta.lore = cList(lore)
            head.itemMeta = meta
            inventory.addItem(head)



        }
    }

}