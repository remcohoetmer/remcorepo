package reflection;

import nl.classloading.SpecialIF;

public class Special implements SpecialIF {
  public String doThat(String task) {
    return task + " done";
  }
}
