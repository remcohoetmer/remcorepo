package nl.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Completable {
	class Message{}

	private String prepareSend(String i)
	{
		return i;
	}

	private String sendMsg(String i, Message msg )
	{
		return i;
	}

	private Message assembleMsg( )
	{
		return new Message();
	}

	private CompletableFuture<String> processResult(String i)
	{
		return CompletableFuture.supplyAsync( ()-> "zzzzzz...."+ i);
	}
	@SuppressWarnings("unused")

	private CompletableFuture<String> error(String i)
	{
		return CompletableFuture.supplyAsync( ()-> 
		{ if (9==9) {
			throw new RuntimeException("AppException");
		}
		return "";}
				);
	}
	private String handleError(Throwable e)
	{
		return e.getMessage();
	}

	private String raiseError()
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

	private String findReceiver()
	{
		return "Check";
	}		
	void fut() throws InterruptedException, ExecutionException
	{
		CompletableFuture<Void> future= CompletableFuture
				.supplyAsync(this::findReceiver,Executors.newScheduledThreadPool(4))
				.exceptionally(ex -> ex.getMessage())
				.thenAccept(System.out::println);
		future.get();
	}

	void test() throws Exception
	{
		CompletableFuture<String> task= CompletableFuture.supplyAsync(
				() -> "onThread: "+ Thread.currentThread().getName()
				);
		task.thenAccept( t-> System.out.println( t));
		Thread.sleep(1000);
		task.thenAccept( t-> System.err.println( t));

	}


	void compose() throws InterruptedException, ExecutionException
	{
		CompletableFuture<Message> msgAssember= 
				CompletableFuture
				.supplyAsync(this::assembleMsg);


		CompletableFuture
		.supplyAsync(this::findReceiver)
		.thenApplyAsync(this::prepareSend)
		.thenCombine( msgAssember, this::sendMsg)
		.thenCompose( this::processResult)
		.thenAccept(System.out::println)
		.join(); 

	}

	public static final void main(String[] args) throws Exception
	{
		//		new Completable().test();
		new Completable().compose( );
	}
}
