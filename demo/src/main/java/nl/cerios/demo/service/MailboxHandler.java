package nl.cerios.demo.service;

// Here we have the problem that the task does not retuen a value
import java.util.concurrent.CompletableFuture;

import io.reactivex.Completable;

public class MailboxHandler {

	public void sendMessage(String message) {
	}
	
	public CompletableFuture<Void> sendMessage_CF(String message)
	{
		return CompletableFuture.runAsync(()-> sendMessage( message));
	}
	
	public Completable sendMessage_Rx(String message)
	{
		return Completable.fromAction( ()-> sendMessage( message));
	}
}
