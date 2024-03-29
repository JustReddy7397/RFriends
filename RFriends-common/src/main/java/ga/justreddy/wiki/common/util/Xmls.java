package ga.justreddy.wiki.common.util;

import ga.justreddy.wiki.common.DependencyLoader;
import ga.justreddy.wiki.common.base.Dependency;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Xmls {

  private static final String
      TAG_SCOPE      = "scope",
      TAG_GROUP      = "groupId",
      TAG_VERSION    = "version",
      TAG_OPTIONAL   = "optional",
      TAG_ARTIFACT   = "artifactId",
      TAG_DEPENDENCY = "dependency",

  SCOPE_ONE = "provided",
      SCOPE_TWO = "runtime";

  private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  /**
   * Prevent Instantiation
   */
  private Xmls() {}

  public static List<Dependency> readDependencies(File pomFile) {
    final List<Dependency> dependencies = new ArrayList<>();

    try {
      final Element document = readDocument(pomFile);

      NodeList pomDependencies = document.getElementsByTagName(TAG_DEPENDENCY);
      if (pomDependencies == null) return Collections.emptyList();

      DependencyLoader.debug(" ", "Found " + pomDependencies.getLength() + " Dependencies" + " ");

      for (int i = 0; i < pomDependencies.getLength(); i++) {
        Element dependency = ((Element) pomDependencies.item(i));

        final String groupId    = readTag(dependency, TAG_GROUP);
        final String artifactId = readTag(dependency, TAG_ARTIFACT);
        final String scope      = readTag(dependency, TAG_SCOPE);

        if (!scope.equals(SCOPE_ONE) && !scope.equals(SCOPE_TWO)) {
          DependencyLoader.debug("Skipping " + groupId + ":" + artifactId + ", its scope is '" + scope + "'");
          continue;
        }

        String version = readTag(dependency, TAG_VERSION);

        if (version.startsWith("${")) {
          String propertyName = version.substring(2, version.length() - 1);
          version = readTag(document, propertyName);
        }

        final String optional = readTag(dependency, TAG_OPTIONAL);
        if (!optional.isEmpty() && optional.equalsIgnoreCase("true")) continue;

        DependencyLoader.debug("Child >  GroupId " + groupId + ", ArtifactId " + artifactId + ", Version " + version + "  < Child");

        dependencies.add(new Dependency(groupId + ':' + artifactId + ':' + version, version, groupId, artifactId));
      }

    } catch (Exception e) {
      //   DLoader.log(Level.SEVERE, "Failed to load dependencies for pom " + pomFile.getName());
      DependencyLoader.log(Level.SEVERE,
              "[RFriends] Failed to load dependencies for pom " + pomFile.getName());
      e.printStackTrace();

      return Collections.emptyList();
    }

    return dependencies;
  }

  @SuppressWarnings("WeakerAccess")
  public static String readLatestSnapshot(Dependency dependency, File metaFile) {
    try {
      final Element document = readDocument(metaFile);

      Element snapshot = (Element) document.getElementsByTagName("snapshot").item(0);

      final String timestamp   = readTag(snapshot, "timestamp");
      final String buildNumber = readTag(snapshot, "buildNumber");

      final String latestSnapshot = dependency.getVersion().replace("SNAPSHOT", timestamp + "-" + buildNumber);
      DependencyLoader.debug("Latest Snapshot version of " + dependency.getName() + " is " + latestSnapshot);

      FileUtils.forceDelete(metaFile);

      return latestSnapshot;

    } catch (Exception e) {
      DependencyLoader.log(Level.SEVERE, "Failed to load meta for snapshot of  " + dependency);
      e.printStackTrace();
    }

    return "ERROR";
  }


  private static Element readDocument(File file) throws ParserConfigurationException, IOException, SAXException {
    final DocumentBuilder builder  = factory.newDocumentBuilder();
    final Document        document = builder.parse(file);

    document.normalize();

    return document.getDocumentElement();
  }

  private static String readTag(Element element, String tagName) {
    Node item = element.getElementsByTagName(tagName).item(0);
    if (item == null) return "";

    return item.getTextContent();
  }

}
