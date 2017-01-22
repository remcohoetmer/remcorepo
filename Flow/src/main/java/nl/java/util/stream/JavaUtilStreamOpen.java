package nl.java.util.stream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JavaUtilStreamOpen {

	String pass( final String phase, final String input)
	{
		System.out.println("pass "+ phase + " " + input);
		return input;
	}

	class OpenIter implements Iterable<String>
	{
		@Override
		public Iterator<String> iterator() {
			return new Iterator<String>() {
				int i=0;
				final List<String> myList= Arrays.asList("a1", "a2", "b1", "a3", "c1", "c2", "a4");
				
				@Override
				public String next() {
					if (hasNext()) {
					return myList.get(i++);
					}
					return null;
				}
				@Override
				public boolean hasNext() {
					return i< myList.size();
				}
			};
		}
		
	}
	void open()
	{
		
		
		// TEST whether the stream operates really lazy
		// indeed, if no more elements are requested, these are not computed (only synchronous mode).

		Stream<? extends String> stream= StreamSupport.stream(new OpenIter().spliterator(), false);
		stream= stream
				.map(s -> this.pass( "1", s))
				.filter(s -> s.startsWith("a"))
				.map(s -> this.pass( "2", s));
 

		Iterator<? extends String> iterator= stream.iterator();

		for (int i=0;i<5;i++) {
			System.out.println( "result:");
			System.out.println( "is: "+ iterator.next());
			System.out.println( "------");
		}
		stream.close();
		// Conclusie: de map evalueert lazy. Tenzij er operaties komen die de hele set nodig hebben, zoals sort
	}

	public static void main(String[] args) throws InterruptedException {
		JavaUtilStreamOpen obj= new JavaUtilStreamOpen();
		//obj.parallelismInterator();
		obj.open();
		//obj.parallelNext();

	}
}
