package ga.justreddy.wiki.rfriendsbungee;

import ga.justreddy.wiki.rfriendscommon.bungee.DLoaderBungee;
import ga.justreddy.wiki.rfriendscommon.bungee.base.Dependency;
import net.md_5.bungee.api.plugin.Plugin;

public class RFriends extends Plugin {

    private static RFriends plugin;

    @Override
    public void onLoad() {
        plugin = this;
        DLoaderBungee.getInstance().onLoad(plugin);
        DLoaderBungee.getInstance().load(new Dependency("kotlin-common", "1.6.20-RC", "org.jetbrains.kotlin", "kotlin-stdlib-common"));
        DLoaderBungee.getInstance().load(new Dependency("kotlin-jdk8", "1.6.20-RC", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8"));
        DLoaderBungee.getInstance().load(new Dependency("kotlin", "1.6.20-RC", "org.jetbrains.kotlin", "kotlin-stdlib"));
        DLoaderBungee.getInstance().load(new Dependency("mongo-driver", "3.12.10", "org.mongodb", "mongodb-driver"));
        DLoaderBungee.getInstance().load(new Dependency("mongo-driver-core", "3.12.10", "org.mongodb", "mongodb-driver-core"));
        DLoaderBungee.getInstance().load(new Dependency("bson", "4.4.0", "org.mongodb", "bson"));
        DLoaderBungee.getInstance().load(new Dependency("h2", "1.4.200", "com.h2database", "h2"));
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static RFriends getPlugin() {
        return plugin;
    }
}
