package ga.justreddy.wiki.rfriendsspigot.menus

import com.cryptomorin.xseries.XMaterial
import ga.justreddy.wiki.rfriendsspigot.helpers.friendHelper
import ga.justreddy.wiki.rfriendsspigot.plugin
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.sql.SQLException


class AnvilMenu {

    fun openRequestMenu(p: Player?) {
        AnvilGUI.Builder()
            .onComplete { player, s ->
                try {
                    friendHelper.sendFriendRequest(player, Bukkit.getOfflinePlayer(s))
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                AnvilGUI.Response.close()
            }
            .itemLeft(XMaterial.PAPER.parseItem())
            .text(ChatColor.translateAlternateColorCodes('&', "Put in a name"))
            .title(ChatColor.translateAlternateColorCodes('&', "Put in a name"))
            .plugin(plugin)
            .open(p)
    }

    fun openRemoveMenu(p: Player?) {
        AnvilGUI.Builder()
            .onComplete { player, s ->
                try {
                    friendHelper.removeFriend(player, Bukkit.getOfflinePlayer(s))
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                AnvilGUI.Response.close()
            }
            .itemLeft(XMaterial.PAPER.parseItem())
            .text(ChatColor.translateAlternateColorCodes('&', "Put in a name"))
            .title(ChatColor.translateAlternateColorCodes('&', "Put in a name"))
            .plugin(plugin)
            .open(p)
    }

}