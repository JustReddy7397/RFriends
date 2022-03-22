package ga.justreddy.wiki.rfriendsspigot.events.custom

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FriendMessageEvent(friend: Player, player: Player, message: String) : Event(), Cancellable {

    /**
     * @param player The player who has sent the message
     */
    private var player: Player

    /**
     * @param friend The friend the message has been sent to
     */
    private var friend: Player

    /**
     * @param message The message the player sent
     */

    private var message: String

    private var isCancelled: Boolean

    init {
        this.player = player
        this.friend = friend
        this.message = message
        this.isCancelled = false
    }

    fun getPlayer(): Player {
        return player
    }


    fun getFriend(): Player {
        return friend
    }

    fun getMessage() : String {
        return message
    }


    override fun isCancelled(): Boolean {
        return this.isCancelled
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        //I just added this.
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }
}