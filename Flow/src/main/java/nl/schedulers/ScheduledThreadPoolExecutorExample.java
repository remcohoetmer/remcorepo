package nl.schedulers;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class BeeperControl {
	private final ScheduledExecutorService scheduler =
			Executors.newScheduledThreadPool(1);

	public void beepForAnHour() {
		final ScheduledFuture<?> beeperHandle =
				scheduler.scheduleAtFixedRate(()->System.out.println("beep"), 0, 10, SECONDS);
		scheduler.schedule( ()-> beeperHandle.cancel(true), 60 * 60, SECONDS);
	}
}

public class ScheduledThreadPoolExecutorExample {

	void start() throws InterruptedException, ExecutionException
	{
		Runnable runnabledelayedTask= 
				()->System.out.println(Thread.currentThread().getName()+" is Running Delayed Task");

				ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(4);
				scheduledPool.scheduleWithFixedDelay(runnabledelayedTask, 1, 1, TimeUnit.SECONDS);

				ScheduledFuture<String> sf = scheduledPool.schedule(() -> {
					scheduledPool.shutdown();
					return "";
				}, 4, TimeUnit.SECONDS);

				int N=40;
				ScheduledFuture<?>[] futures= new ScheduledFuture<?>[N];
				
				for (int i=0;i<N;i++){
					futures[i] = scheduledPool.schedule(()-> {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							System.err.println( "Finished"+ System.currentTimeMillis()/1000);
						}, 1, TimeUnit.MILLISECONDS);
				}
				String value= sf.get();
				System.out.println("Callable returned "+value);
				System.out.println("Is ScheduledThreadPool shutting down? "+scheduledPool.isShutdown());
	}


	public static void main(String[] args) throws Exception
	{
		new ScheduledThreadPoolExecutorExample().start();
		//new BeeperControl().beepForAnHour();

	}
}
