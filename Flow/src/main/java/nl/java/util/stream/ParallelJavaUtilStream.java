package nl.java.util.stream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParallelJavaUtilStream {
	Integer printThread(Integer i)
	{
		System.out.println( i+ " "+ Thread.currentThread().getName()+ " "+ System.currentTimeMillis());
		return i;
	}

	private IntFunction<? extends Integer> sleepAWhile(AtomicInteger atomicInt) {
		return r-> 
		{
			Integer i=atomicInt.incrementAndGet();
			printThread(i);
			if (i==1) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return i;
		};
	}
	void parallelNext() throws InterruptedException
	{
		// test parallelism
		AtomicInteger atomicInt = new AtomicInteger(0);

		//		List<? extends Integer> list= 
		IntStream.range(0, 3)
		.parallel()
		.mapToObj(sleepAWhile(atomicInt))
		.collect(Collectors.toList());
		Thread.sleep(1200);
	}

	private AtomicInteger sleepAWhile1(final AtomicInteger atomicInt) {
		Integer i=atomicInt.incrementAndGet();
		printThread(i);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return atomicInt;
	}
	void parallelismInterator()
	{
		// test parallelism
		AtomicInteger atomicInt = new AtomicInteger(60);

		Stream<? extends AtomicInteger> stream= IntStream.range(0, 100)
				.parallel()
				.mapToObj(r-> atomicInt)	
				.map( this::sleepAWhile1);
		Iterator<? extends AtomicInteger> iterator= stream.iterator();

		for (int i=0;i<4;i++) {
			System.err.println( " result"+ iterator.next());
		}
		stream.close();
		// Conclusie: parallelism gaat enkel werken indien je batch operaties uitvoert. Niet als je itereert
	}

	String pass( final String phase, final String input)
	{
		System.out.println("pass "+ phase + " " + input);
		return input;
	}

	void lazy()
	{
		// TEST whether the stream operates really lazy
		// indeed, if no more elements are requested, these are not computed (only synchronous mode).
		List<String> myList= Arrays.asList("a1", "a2", "b1", "a3", "c1", "c2", "a4");

		Stream<? extends String> stream= myList.stream()
				.map(s -> this.pass( "1", s))
				.filter(s -> s.startsWith("a"))
				.map(s -> this.pass( "2", s));
		// .sorted(); 

		Iterator<? extends String> iterator= stream.iterator();

		for (int i=0;i<3;i++) {
			System.out.println( "result:");
			System.out.println( "is: "+ iterator.next());
			System.out.println( "------");
		}
		stream.close();
		// Conclusie: de map evalueert lazy. Tenzij er operaties komen die de hele set nodig hebben, zoals sort
	}

	public static void main(String[] args) throws InterruptedException {
		ParallelJavaUtilStream obj= new ParallelJavaUtilStream();
		obj.parallelismInterator();
		obj.lazy();
		obj.parallelNext();

	}
}
