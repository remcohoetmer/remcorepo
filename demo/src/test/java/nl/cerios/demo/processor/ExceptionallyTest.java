package nl.cerios.demo.processor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.junit.Test;

public class ExceptionallyTest {
	@Test
	public void exceptionally() throws InterruptedException
	{
		CompletableFuture<String> future= new CompletableFuture<>();


		Function<String, String> fn = v-> { System.out.println("step1"); throw new RuntimeException();};
		Function<String, String> feed = v-> v;

		future
		.thenApply( feed )
		.exceptionally(e -> {
			System.out.println("Catched "+ e);
			return null;
		})
		.thenAccept( System.out::println);
		
		future.complete("trigger");

		CompletableFuture.supplyAsync( () -> "Nieuw")
		.exceptionally(e -> {
			System.out.println("Catched "+ e);
			return null;
		}).thenAccept( System.out::println);
		
		CompletableFuture.supplyAsync( () -> { System.out.println("async"); throw new RuntimeException();})
		.handle( (f,e) -> {
			System.out.println("Catched "+ e);
			return "Remco";
		})
		.handle( (f,e) -> {
			System.out.println("Catched "+ e);
			return "Twee";
		})
		.thenAccept( System.out::println);
		
		Thread.sleep(100);

	}
}
