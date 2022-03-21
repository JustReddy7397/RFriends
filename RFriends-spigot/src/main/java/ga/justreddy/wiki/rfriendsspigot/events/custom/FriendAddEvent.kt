package ga.justreddy.wiki.rfriendsspigot.events.custom

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class FriendAddEvent(friend: Player, player: OfflinePlayer) : Event(), Cancellable {

    /**
     * @param player The player who has sent the friend request
     */
    private var player: OfflinePlayer

    /**
     * @param friend The player who has accepted the friend request
     */
    private var friend: Player

    private var isCancelled: Boolean

    init {
        this.player = player
        this.friend = friend
        this.isCancelled = false
    }

    fun getPlayer() : OfflinePlayer {
        return player
    }



    fun getFriend() : Player {
        return friend
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