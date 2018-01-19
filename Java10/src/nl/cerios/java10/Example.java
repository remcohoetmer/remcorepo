package nl.cerios.java10;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class Example {
  static void showList() {

    List<String> cities = new ArrayList<String>();  // infers ArrayList<String>
    cities.add("Edinborough");
    cities.add("Madrid");
    cities.add("London");
    cities.add("Köln");

    System.out.println("Cities:");
    Stream<String> stream = cities.stream();          // infers Stream<String>
    stream.map(city -> "- " + city).forEach(System.out::println);

    Date var = new Date(); // "var" is not a new keyword; instead it is a reserved type name
    System.out.println("Departure: " + var);
  }

  public static void main(String[] args) {
    showList();
  }

}
