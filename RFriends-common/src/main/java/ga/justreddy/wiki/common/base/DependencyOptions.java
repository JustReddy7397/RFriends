package ga.justreddy.wiki.common.base;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import ga.justreddy.wiki.common.DependencyLoader;
import ga.justreddy.wiki.common.util.Urls;

public class DependencyOptions {

  private String  customRepository;
  private boolean alwaysUpdate;


  public DependencyOptions(String customRepository, boolean alwaysUpdate) {
    this.customRepository = customRepository;
    this.alwaysUpdate = alwaysUpdate;
  }


  public String getCustomRepository() {
    return customRepository;
  }

  public void setCustomRepository(String customRepository) {
    this.customRepository = Urls.fixUrl(customRepository);
  }

  public boolean isAlwaysUpdate() {
    return alwaysUpdate;
  }

  public void setAlwaysUpdate(boolean alwaysUpdate) {
    this.alwaysUpdate = alwaysUpdate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DependencyOptions)) return false;
    DependencyOptions dOptions = (DependencyOptions) o;
    return isAlwaysUpdate() == dOptions.isAlwaysUpdate() &&
        Objects.equal(getCustomRepository(), dOptions.getCustomRepository());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getCustomRepository(), isAlwaysUpdate());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("customRepository", getCustomRepository())
        .add("alwaysUpdate", alwaysUpdate)
        .toString();
  }


}
