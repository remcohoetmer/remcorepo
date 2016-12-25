package nl.future;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class JavaUtilConcurrent {
	public static void stop(ExecutorService executor) {
		try {
			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			System.err.println("termination interrupted");
		}
		finally {
			if (!executor.isTerminated()) {
				System.err.println("killing non-finished tasks");
			}
			executor.shutdownNow();
		}
	}

	public static void sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	void concurrentHashMap()
	{
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		map.put("foo", "bar");
		map.put("han", "solo");
		map.put("r2", "d2");
		map.put("c3", "p0");
		String result = map.reduce(1,
				(key, value) -> {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Transform: " + Thread.currentThread().getName());
					return key + "=" + value;
				},
				(s1, s2) -> {
					System.out.println("Reduce: " + Thread.currentThread().getName());
					return s1 + ", " + s2;
				});

		System.out.println("Result: " + result);

	}

	static class TomicInteger {
		Integer in;
		public TomicInteger(int i) {
			in= i;
		}
		void inc() { in++;}
	}

	void atomic()
	{
		AtomicInteger atomicInt = new AtomicInteger(0);
		ExecutorService executor = Executors.newFixedThreadPool(3);

		IntStream.range(0, 1000)
		.forEach(i -> executor.submit(atomicInt::incrementAndGet ));
		stop(executor);

		System.out.println(atomicInt.get());    // => 1000
	}
	
	void tomic()
	{
		TomicInteger tomicInt = new TomicInteger(0);
		ExecutorService executor = Executors.newFixedThreadPool(3);

		IntStream.range(0, 1000)
		.forEach(i -> executor.submit(tomicInt::inc ));
		stop(executor);

		System.out.println(tomicInt.in);    // => 1000
	}
	static Integer printThread(Integer i)
	{
		System.out.println( "i "+ Thread.currentThread().getName());
		return i;
	}


	public static void main(String[] args) throws InterruptedException {
	//	new JavaUtilConcurrent().atomic();
		System.out.println(ForkJoinPool.getCommonPoolParallelism());
//		new JavaUtilConcurrent().tomic();
		new JavaUtilConcurrent().concurrentHashMap();
	}

}
