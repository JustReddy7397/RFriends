package ga.justreddy.wiki.common;

import com.google.common.base.Strings;
import ga.justreddy.wiki.common.base.Dependency;
import ga.justreddy.wiki.common.util.Urls;
import ga.justreddy.wiki.common.util.Xmls;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class DependencyLoader {

  private static final Logger logger = Logger.getAnonymousLogger();

  private static DependencyLoader instance;

  public static DependencyLoader getInstance() {
    return instance == null ? instance = new DependencyLoader() : instance;
  }

  private static Method method;
  private static final URLClassLoader classLoader =
      ((URLClassLoader) ClassLoader.getSystemClassLoader());

  private static boolean working = true, showDebug = false, enforceFileCheck = true;

  static {
    try {
      method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      method.setAccessible(true);
    } catch (NoSuchMethodException e) {
      log(Level.SEVERE, "Failed to initialize URLClassLoader, Dependencies will not be loaded!");
      e.printStackTrace();
      working = false;
    }
  }

  private File dependencyFolder;
  private final Map<String, Dependency> dependencies = new HashMap<>();

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void onLoad() throws ExceptionInInitializerError {
    if (!working) return;
    log(Level.INFO, "[RFriends] Loading dependencies...");
    dependencyFolder = new File("plugins/RFriends/libs");
    if (!dependencyFolder.exists()) dependencyFolder.mkdirs();
  }

  public void onEnable() {}

  public static boolean isShowingDebug() {
    return showDebug;
  }

  public static boolean isEnforcingFileCheck() {
    return enforceFileCheck;
  }

  public void load(Dependency dependency) {
    load(
        dependency,
        () -> {
          log(Level.INFO,
              "[RFriends] Successfully downloaded the dependency "
                      + dependency.getName());
        });
  }

  private void load(Dependency dependency, Runnable whenDone) {
    debug(" ", " ", blockBar(60), " ", blockArrow(dependency, "v"));

    Urls.download(
        dependency,
        new File(dependencyFolder, dependency.getGroupId()),
        (jar, pom) -> {
          if (!jar.exists() || !pom.exists())
            debug(
                "POM File Downloaded -> " + pom.exists(), "Jar File Downloaded -> " + jar.exists());
          else {
            loadJar(dependency, jar);
            loadChildren(dependency, pom, whenDone);
          }
        });
  }

  public Optional<Dependency> get(String name) {
    return Optional.ofNullable(dependencies.get(name.toLowerCase()));
  }

  private void loadChildren(Dependency dependency, File pomFile, Runnable whenDone) {
    debug("Loading child dependencies of " + dependency.getName());
    List<Dependency> children = Xmls.readDependencies(pomFile);
    if (children.isEmpty()) {
      debug(
          "No children found in " + dependency.getName(),
          blockArrow(dependency, "^"),
          blockBar(60),
          " ",
          " ");
      whenDone.run();
      return;
    }

    final int[] loaded = {0};

    children.forEach(
        child -> {
          child.setParent(dependency);

          load(
              child,
              () -> {
                if (++loaded[0] == children.size()) {
                  debug(
                      "Finished loading children from " + dependency.getName(),
                      blockArrow(dependency, "^"),
                      " ",
                      blockBar(60),
                      " ",
                      " ");
                  whenDone.run();
                }
              });
        });
  }

  private void loadJar(Dependency dependency, File jarFile) {
    try {
      method.invoke(classLoader, jarFile.toURI().toURL());
      debug("Added " + jarFile.getName() + " to ClassLoader");
      dependencies.put(dependency.getName().toLowerCase(), dependency);
    } catch (Exception e) {
      log(Level.SEVERE, "Failed to load Jar File " + jarFile.getName());
      e.printStackTrace();
    }
  }

  private String blockArrow(Dependency dependency, String c) {
    final String generated = Strings.repeat(c, dependency.getParentDepth());
    return generated.isEmpty() ? c + c + c : generated;
  }

  private String blockBar(int length) {
    return Strings.repeat("=", length);
  }

  public static void debug(String... message) {
    if (isShowingDebug()) log(Level.WARNING, message);
  }

  public static void log(Level level, String... message) {
    for (String msgLine : message) logger.log(level, msgLine);
  }


}
