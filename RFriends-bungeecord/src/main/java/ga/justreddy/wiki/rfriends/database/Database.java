package ga.justreddy.wiki.rfriends.database;

import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import java.util.List;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface Database {

  /**
   * Creates a player data for this player.
   * @param player The player of whom the data has to be created,
   */
  void createPlayerData(ProxiedPlayer player);

  /**
   * Checks if the specified player has data or not.
   * @param player The player of whom the data has to be checked.
   * @return True if the specified player has data, False otherwise
   */
  boolean hasPlayerData(ProxiedPlayer player);

  /**
   * Sends a friend request to the recipient.
   * @param sender The one who sends the friend request.
   * @param recipient The one who receives the friend request.
   */
  void sendRequest(ProxiedPlayer sender, ProxiedPlayer recipient);

  /**
   * Accepts the friend request from the sender.
   * @param acceptor The one who accepts the friend request.
   * @param sender The one who has sent the friend request.
   */
  void acceptRequest(ProxiedPlayer acceptor, ProxiedPlayer sender);

  /**
   * Denies the friend request from the sender.
   * @param denier The one who denied the friend request.
   * @param sender The one who has sent the friend request.
   */
  void denyRequest(ProxiedPlayer denier, ProxiedPlayer sender);

  /**
   * Checks if the sender has already sent the friend request to the recipient.
   * @param sender The one who has sent the friend request.
   * @param recipient The one who receives the friend request.
   * @return True if the sender has sent the friend request to the recipient, False otherwise.
   */
  boolean hasSendRequest(ProxiedPlayer sender, ProxiedPlayer recipient);

  /**
   * Checks if the sender is already a friend with the recipient.
   * @param sender The player who has sent the friend request.
   * @param recipient The player who receives the friend request.
   * @return True if the sender is already a friend with the recipient, False otherwise.
   */
  boolean isAlreadyFriend(ProxiedPlayer sender, ProxiedPlayer recipient);

  /**
   * Removes a friend from the senders friend list.
   * @param remover The one who removes the friend.
   * @param recipient The one who has gotten removed.
   */
  void removeFriend(ProxiedPlayer remover, ProxiedPlayer recipient);

  /**
   * Sends a private message to the recipient.
   * @param sender The one who sends the message.
   * @param recipient The one who receives the message.
   * @param message The message to send.
   */
  void sendMessage(ProxiedPlayer sender, ProxiedPlayer recipient, String message);

  /**
   * Toggles the status of a player.
   * @param player THe player of whom the status should be toggled.
   */
  void toggleStatus(ProxiedPlayer player);

  /**
   * Toggles weather or not the player want to accept friend requests.
   * @param player The player who to accept friend requests should be toggled.
   */
  void toggleFriendRequests(ProxiedPlayer player);

  /**
   * Gets the friend count of the player.
   * @param player The player of whom to get the friend count of.
   * @return The amount of friends the player has.
   */
  int getFriendCount(ProxiedPlayer player);

  /**
   * Checks if the recipient has friend requests enabled.
   * @param player The one who has to get checked if friend requests are enabled..
   * @return True if the player has friend requests enabled, False otherwise.
   */
  boolean hasFriendRequestsEnabled(ProxiedPlayer player);

  /**
   * Checks if the player is online.
   * @param player THe player of whom to check if they're online.
   * @return True if online, False otherwise.
   */
  boolean isOnline(ProxiedPlayer player);

  /**
   * Gets the current requests
   * @return A list of current ongoing requests.
   */
  List<Requests> getCurrentRequests();


  void removeRequest(String sender, String recipient);

  /**
   * Closes the database connection.
   */
  void close();

  default RFriendsBungeeCord getPlugin() {
    return RFriendsBungeeCord.getInstance();
  }

}
