package ga.justreddy.wiki.rfriends;

import ga.justreddy.wiki.common.DependencyLoader;
import ga.justreddy.wiki.common.base.Dependency;
import ga.justreddy.wiki.rfriends.command.BaseCommand;
import ga.justreddy.wiki.rfriends.config.YamlConfig;
import ga.justreddy.wiki.rfriends.database.Database;
import ga.justreddy.wiki.rfriends.database.SQLite;
import ga.justreddy.wiki.rfriends.listeners.EventManager;
import ga.justreddy.wiki.rfriends.tasks.RequestTask;
import ga.justreddy.wiki.rfriends.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

public final class RFriendsBungeeCord extends Plugin {

  private static RFriendsBungeeCord instance;

  @Getter
  private YamlConfig databaseConfig;
  @Getter
  private YamlConfig messagesConfig;

  @Getter
  private YamlConfig settingsConfig;

  @Getter
  private Database database;

  private final CommandSender console = getProxy().getConsole();

  @Getter
  private LuckPerms luckPerms = null;


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
    instance = this;
    File databaseFolder = new File(getDataFolder().getAbsolutePath() + "/data");
    if (!databaseFolder.exists()) databaseFolder.mkdirs();
    console.sendMessage(Utils.format("&7[&dRFriends&7] &aLoading config files..."));
    if (!loadConfigs()) return;
    console.sendMessage(Utils.format("&7[&dRFriends&7] &aConnecting to the database..."));
    String databaseType;
    switch (getDatabaseConfig().getConfig().getString("storage").toLowerCase()) {
      case "mysql":
        // TODO
        databaseType = "MySQL";
        break;
      case "mongodb":
        // TODO
        databaseType = "MongoDB";
        break;
      case "yaml":
        // TODO
        databaseType = "YAML";
        break;
      default:
        database = new SQLite();
        databaseType = "SQLite";
        break;
    }
    console.sendMessage(Utils.format("&7[&dRFriends&7] &aSuccessfully loaded database: " + databaseType + "..."));
    console.sendMessage(Utils.format("&7[&dRFriends&7] &aStarting tasks..."));
    getProxy().getScheduler().schedule(this, new RequestTask(), 0, 1, TimeUnit.SECONDS);
    console.sendMessage(Utils.format("&7[&dRFriends&7] &aCompletely loaded the plugin."));
    getProxy().getPluginManager().registerCommand(this, new BaseCommand("friends"));
    getProxy().getPluginManager().registerListener(this, new EventManager());
    if (getSettingsConfig().getConfig().getBoolean("use-luckperms-prefix")) {
      luckPerms = LuckPermsProvider.get();
    }
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    database.close();
  }

  private boolean loadConfigs() {
    String configFile = "Config File";

    try {

      configFile = "database.yml";
      databaseConfig = new YamlConfig(configFile);
      configFile = "messages.yml";
      messagesConfig = new YamlConfig(configFile);
      configFile = "settings.yml";
      settingsConfig = new YamlConfig(configFile);

    }catch (IOException ex) {
      getLogger().log(Level.SEVERE, "Failed to load config file " + configFile);
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  public static RFriendsBungeeCord getInstance() {
    return instance;
  }

}
