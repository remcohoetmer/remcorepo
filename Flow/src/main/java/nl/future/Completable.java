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

	void testThread() throws Exception
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
	void composeAsync() throws InterruptedException, ExecutionException
	{
		CompletableFuture<Void> future= CompletableFuture
				.supplyAsync(this::findReceiver,Executors.newScheduledThreadPool(4))
				.thenComposeAsync(s->  {System.out.println("onThread: "+ Thread.currentThread().getName());return CompletableFuture
						.supplyAsync(this::assembleMsg);})
				.thenCompose(s->  {System.out.println("onThread: "+ Thread.currentThread().getName());return CompletableFuture
						.supplyAsync(this::assembleMsg);})
				.thenAccept(System.out::println);
		future.get();
	}

	void anyOf()
	{
		CompletableFuture<String> orderdataCF_1=
				CompletableFuture.supplyAsync( () -> "Result1");
		CompletableFuture<String> orderdataCF_2=
				CompletableFuture.supplyAsync( () -> "Result2");
		CompletableFuture<Object> orderdataCF= CompletableFuture.anyOf( orderdataCF_1, orderdataCF_2);
		/* Hamvraag: wanneer levert een completable geen result?
		 * Want: Als een andere tak niet wordt uitgevoerd (en dat weet je bij imperatieve programma's),
		 * 	     dan moet je daar ook niet op gaan wachten.
		 */
	}
	void voidTest() throws InterruptedException, ExecutionException
	{
		CompletableFuture<Void> i= new CompletableFuture<>();
		i.complete(null);
		i.thenApplyAsync( voiddummy->{ System.out.println( "Hoi" + voiddummy);return new Integer(1);}).get();
	}

	void composeTest()
	{
		CompletableFuture<String> task1= CompletableFuture.supplyAsync(

				() -> {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return "Taks 1 onThread: "+ Thread.currentThread().getName();
				}
				);
		CompletableFuture<String> task2= CompletableFuture.supplyAsync(
				() -> "Taks 2 onThread: "+ Thread.currentThread().getName()
				);
		task1.thenAccept(System.out::println);
		task2.thenAccept(System.out::println);
		CompletableFuture.allOf( task1, task2).join();
	}

	void print( String name) {
		System.out.println(String.format("[%d] {%35s} %s",
				System.currentTimeMillis(), 
				Thread.currentThread().getName(),
				name));
	}

	CompletableFuture<String> mongoStart( Integer id)
	{
		CompletableFuture<String> result= new CompletableFuture<String>();
		Thread startThread= new Thread( () -> { 
			try {
				Thread.sleep(10);
				print( "mongo started");
				new Thread( () -> { 
					try {
						Thread.sleep(20);
						print( "trigger end1");
						result.complete( "Value");
						print( "trigger end2");
					} catch (InterruptedException e) {
						result.completeExceptionally(e);
					}
				} ).start();
			} catch (InterruptedException e) {
				result.completeExceptionally(e);
			}
		} );
		print( "mongoStart before");
		startThread.start();
		print( "mongoStart after");
		return result;
	}

	void mongoStartTest() throws InterruptedException
	{

		CompletableFuture<Integer> trigger = new CompletableFuture<>( );

		CompletableFuture<String> mongoresult= trigger.thenComposeAsync(this::mongoStart);

		mongoresult.thenAcceptAsync( result-> {
			print( result);
			});
		print( "start");
		Thread.sleep(10);
		trigger.complete( 99);

		Thread.sleep(1000);
		
	}

	void test() throws InterruptedException
	{
		mongoStartTest();
	}

	public static final void main(String[] args) throws Exception
	{
		new Completable().test();
	}
}
