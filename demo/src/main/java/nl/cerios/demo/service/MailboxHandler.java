package nl.cerios.demo.service;

// Here we have the problem that the task does not return a value
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class MailboxHandler {

	public void sendMessage_Sync(String message) {
	}
	
	public CompletableFuture<Void> sendMessage_CF(String message)
	{
		return CompletableFuture.runAsync(()-> sendMessage_Sync( message));
	}
	
	public Mono sendMessage_Rx(String message)
	{
		return Mono.fromRunnable( ()-> sendMessage_Sync( message));
	}
}
