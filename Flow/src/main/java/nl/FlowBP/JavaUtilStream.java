package nl.FlowBP;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JavaUtilStream {
	void parallelism()
	{
		AtomicInteger atomicInt = new AtomicInteger(60);

		Stream<? extends Integer> stream= IntStream.range(0, 1000)
				.parallel()
				.mapToObj(r-> atomicInt)
				.map(a->((AtomicInteger)a).incrementAndGet())
				.map(r-> JavaUtilConcurrent.printThread(r));

		Iterator<? extends Integer> iterator= stream.iterator();

		for (int i=0;i<1000;i++) {
			System.err.println( " result"+ iterator.next());
		}
		stream.close();
		// Conclusie: parallelism gaat enkel werken indien je batch operaties uitvoert.
	}
	static String pass( final String phase, final String input)
	{
		System.out.println("pass "+ phase + " " + input);
		return input;
	}

	void lazy()
	{

		List<String> myList= Arrays.asList("a1", "a2", "b1", "a3", "c1", "c2", "a4");

		Stream<? extends String> stream= myList.stream()
				.map(s -> JavaUtilStream.pass( "1", s))
				.filter(s -> s.startsWith("a"))
				.map(s -> JavaUtilStream.pass( "2", s));
		// .sorted();

		Iterator<? extends String> iterator= stream.iterator();

		for (int i=0;i<3;i++) {
			System.out.println( "result:");
			System.out.println( "is: "+ iterator.next());
			System.out.println( "------");
		}
		stream.close();
		// Conclusie: de map evalueert lazy todat er operaties komen die de hele set nodig hebben, zoals sort
	}

	public static void main(String[] args) throws InterruptedException {
		new JavaUtilStream().parallelism();
		new JavaUtilStream().lazy();

	}
}
