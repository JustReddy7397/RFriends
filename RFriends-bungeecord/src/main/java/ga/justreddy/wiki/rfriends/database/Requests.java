package ga.justreddy.wiki.rfriends.database;

import java.util.UUID;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Data
public class Requests {

  private final UUID sender;
  private final UUID receiver;
  private final long time;

}
