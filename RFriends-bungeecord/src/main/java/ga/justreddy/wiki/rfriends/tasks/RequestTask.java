package ga.justreddy.wiki.rfriends.tasks;

import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import ga.justreddy.wiki.rfriends.database.Requests;
import ga.justreddy.wiki.rfriends.utils.Utils;
import java.util.Date;

public class RequestTask implements Runnable {

  public RequestTask() {
    RFriendsBungeeCord.getInstance().getProxy().getConsole().sendMessage(Utils.format("&7[&dRFriends&7] &aStarted task: RequestTask"));
  }

  @Override
  public void run() {
    for (Requests request : RFriendsBungeeCord.getInstance().getDatabase().getCurrentRequests()) {
      if (new Date().getTime() <= request.getTime()) continue;
      RFriendsBungeeCord.getInstance().getDatabase().removeRequest(request.getSender().toString(), request.getReceiver().toString());
      // Due to limitations of the bungeecord api, i can't do more.

    }
  }
}
