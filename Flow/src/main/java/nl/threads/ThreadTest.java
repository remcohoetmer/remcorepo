package nl.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import nl.schedulers.ScheduledThreadPoolExecutorExample;

public class ThreadTest {
	void start() throws InterruptedException, ExecutionException
	{
		Runnable runnabledelayedTask= ()->System.out.println(Thread.currentThread().getName()+" is Running Delayed Task");

		ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);
	//	scheduledPool.scheduleWithFixedDelay(runnabledelayedTask, 1, 1, TimeUnit.SECONDS);

		ScheduledFuture<String> sf = scheduledPool.schedule(() -> {
			scheduledPool.shutdown();
			return "shutdown";
		}, 20, TimeUnit.SECONDS);

		int N=3;
		ScheduledFuture<?>[] futures= new ScheduledFuture<?>[N];

		for (int i=0;i<N;i++){
			futures[i] = scheduledPool.schedule(()-> {
				System.out.println(Thread.currentThread().getName()+" is Running Delayed Task");

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.err.println( "Finished"+ Thread.currentThread());
			}, 1, TimeUnit.MILLISECONDS);
		}
		new Thread(
				() -> {
					String value;
					try {
						value = sf.get();
						System.out.println("Callable returned "+value);
					} catch (InterruptedException|ExecutionException e) {
						e.printStackTrace();
					}
					System.out.println("Is ScheduledThreadPool shutting down? "+scheduledPool.isShutdown());
				}).start();
	}

	void poolSize() throws InterruptedException, ExecutionException
	{
		ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);

		ScheduledFuture<String> sf = scheduledPool.schedule(() -> {
			scheduledPool.shutdown();
			return "shutdown";
		}, 20, TimeUnit.SECONDS);

		int N=3;
		ScheduledFuture<?>[] futures= new ScheduledFuture<?>[N];

		for (int i=0;i<N;i++){
			
			futures[i] = scheduledPool.schedule(()-> {
				System.out.println(Thread.currentThread()+" runningTask");

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.err.println( "Finished"+ Thread.currentThread());
			}, 1, TimeUnit.MILLISECONDS);
			
			System.out.println("Scheduled task "+ i);
		}
		new Thread(
				() -> {
					String value;
					try {
						value = sf.get();
						System.out.println("Callable returned "+value);
					} catch (InterruptedException|ExecutionException e) {
						e.printStackTrace();
					}
					System.out.println("Is ScheduledThreadPool shutting down? "+scheduledPool.isShutdown());
				}).start();
	}

	
	

	public static void main(String[] args) throws Exception
	{
//		new ThreadTest().start();
		new ThreadTest().poolSize();
	}

}
