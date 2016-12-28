package nl.cerios.demo.processor;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

public class ExceptionallyTest {
	@Test
	public void exceptionally() throws InterruptedException
	{
		CompletableFuture<String> future= new CompletableFuture<>();
		future.complete("trigger");

		CompletableFuture.supplyAsync( () -> "Oud")
		.exceptionally(e -> {
			System.out.println("Catched "+ e);
			return "Nieuw";
		}).thenAccept( System.out::println);
		
		CompletableFuture.supplyAsync( () -> { System.out.println("async"); throw new RuntimeException();})
		.handle( (f,e) -> {
			System.out.println("Catched "+ e);
			return "Nieuw";
		})
		.handle( (f,e) -> {
			System.out.println("Catched "+ e);
			return f;
		})
		.thenApply( v ->  "Plak aan "+ v)
		.thenAccept( System.out::println);
		
		Thread.sleep(100);

	}
}
