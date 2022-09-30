package ga.justreddy.wiki.rfriends.config;

import ga.justreddy.wiki.rfriends.RFriendsBungeeCord;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class YamlConfig {

  private static final String VERSION_KEY = "config-version";

  @Getter
  private final File file;

  @Getter
  private Configuration config;


  public YamlConfig(String name) throws IOException {
    String finalName = name.endsWith(".yml") ? name : name + ".yml";
    File file = new File(RFriendsBungeeCord.getInstance().getDataFolder().getAbsolutePath(), finalName);

    if (!file.exists()) {
      RFriendsBungeeCord.getInstance().getDataFolder().mkdir();
      Files.copy(RFriendsBungeeCord.getInstance().getResourceAsStream(finalName), file.toPath());
    }

    this.file = file;
    this.config =  ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    reload();
  }

  public void reload() throws IOException {
    this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
  }

  public void save() throws IOException {
    ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
  }

  public boolean isOutdated(final int currentVersion) {
    return config.getInt(VERSION_KEY, -1) < currentVersion;
  }

}
