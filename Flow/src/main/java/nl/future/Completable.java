package nl.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Completable {


	private String sendMsg(String i)
	{
		return i;
	}

	private String findReceiver()
	{
//		throw new RuntimeException( )
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
		throw new RuntimeException("Error");
		//return "Check";
	}
	
	void fut() throws InterruptedException, ExecutionException
	{
		CompletableFuture<Void> future= CompletableFuture
				.supplyAsync(this::findReceiver,Executors.newScheduledThreadPool(4))
				.exceptionally(ex -> ex.getMessage())
				.thenApply(this::sendMsg)
				.thenAccept(System.out::println);
		future.get();
	}

	void test() throws Exception
	{
		CompletableFuture<String> task= CompletableFuture.supplyAsync(
			() -> "Remco "+ Thread.currentThread().getName()
		);
		task.thenAccept( t-> System.out.println( t));
		Thread.sleep(1000);
		task.thenAccept( t-> System.err.println( t));
		
	}
	public static final void main(String[] args) throws Exception
	{
		new Completable().test();
	}
}
