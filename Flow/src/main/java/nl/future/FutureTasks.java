package nl.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RecursiveTask;

public class FutureTasks {


	public static final void main(String[] args) throws InterruptedException, ExecutionException
	{
		FutureTask<String> task= new FutureTask<>( ()-> "Executor: "+ Thread.currentThread().getName());
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(task);
		
		RecursiveTask<String> rectask= new RecursiveTask<String>() {

			private static final long serialVersionUID = 1L;
			protected String compute() { return "ForkJoinPool " + Thread.currentThread().getName();}
		};

		ForkJoinTask<String> recval= ForkJoinPool.commonPool().submit( rectask);

		FutureTask<String> simpletask= new FutureTask<>( ()-> "No thread: "+ Thread.currentThread().getName());
		simpletask.run();

		//task.cancel(true);
		//recval.cancel(true);
		System.out.println( task.get());
		System.out.println( recval.get());
		System.out.println( simpletask.get());

		executor.shutdown();

		/* problem if you share a future among different clients:
    	if one cancels the task, then
    	*/
    }
}
