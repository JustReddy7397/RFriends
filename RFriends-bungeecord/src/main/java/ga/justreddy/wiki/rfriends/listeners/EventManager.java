package ga.justreddy.wiki.rfriends.listeners;

import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventManager implements Listener {

  @EventHandler
  public void onProxyPlayerJoin(PostLoginEvent event) {
    RFriendsBungeeCord.getInstance().getDatabase().createPlayerData(event.getPlayer());
  }


}
