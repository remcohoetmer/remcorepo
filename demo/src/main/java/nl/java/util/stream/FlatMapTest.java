package nl.java.util.stream;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

class Developer {

    private String name;
    private Set<String> languages;

    public Developer(String name) {
        this.languages = new HashSet<>();
        this.name = name;
    }

    public void add(String language) {
        this.languages.add(language);
    }

    public Set<String> getLanguages() {
        return languages;
    }
    Developer printThread()
	{
		System.out.println( "[Developer " + name + "] "+ Thread.currentThread().getName()+ " "+ System.currentTimeMillis());
		return this;
	}
	
}
public class FlatMapTest {
	static String printThread(String i)
	{
		System.out.println( i + " "+ Thread.currentThread().getName()+ " "+ System.currentTimeMillis());
		return i;
	}
	
	@Test
    public void flatMap() {
        List<Developer> team = new ArrayList<>();
        Developer polyglot = new Developer("esoteric");
        polyglot.add("clojure");
        polyglot.add("scala");
        polyglot.add("groovy");
        polyglot.add("go");

        Developer busy = new Developer("pragmatic");
        busy.add("java");
        busy.add("javascript");

        team.add(polyglot);
        team.add(busy);

        List<String> teamLanguages = team.stream().parallel().
        		map( t-> t.printThread()).
        		map(d -> d.getLanguages()).
                flatMap(l -> l.stream().parallel()).// parallel doet hier niets meer!
                map( FlatMapTest::printThread).
                collect(Collectors.toList());
        assertTrue(teamLanguages.containsAll(polyglot.getLanguages()));
        assertTrue(teamLanguages.containsAll(busy.getLanguages()));
    }
}