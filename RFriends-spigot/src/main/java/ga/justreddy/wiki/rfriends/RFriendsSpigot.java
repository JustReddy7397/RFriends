package ga.justreddy.wiki.rfriends;

import ga.justreddy.wiki.common.DependencyLoader;
import ga.justreddy.wiki.common.base.Dependency;
import org.bukkit.entity.EnderPearl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class RFriendsSpigot extends JavaPlugin {

  @Override
  public void onLoad() {
    try{
      DependencyLoader.getInstance().onLoad();
      DependencyLoader.getInstance().load(new Dependency("SQLite", "3.34.0", "org.xerial", "sqlite-jdbc"));
      DependencyLoader.getInstance().load(new Dependency("MongoDB-Driver", "3.12.11", "org.mongodb", "mongodb-driver"));
      DependencyLoader.getInstance().load(new Dependency("MongoDB-Core", "3.12.11", "org.mongodb", "mongodb-driver-core"));
      DependencyLoader.getInstance().load(new Dependency("MongoDB-Bson", "3.12.11", "org.mongodb", "bson"));
    }catch (ClassCastException | ExceptionInInitializerError ignored) {

    }
  }

  @Override
  public void onEnable() {
    // Plugin startup logic

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }


}
