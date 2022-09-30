package ga.justreddy.wiki.rfriends.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@Getter
@Setter
@RequiredArgsConstructor
public class FriendMessageEvent extends Event implements Cancellable {

  private final ProxiedPlayer sender;
  private final ProxiedPlayer recipient;
  private final BaseComponent message;
  private boolean cancelled;

}
