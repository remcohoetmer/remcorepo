package nl.cerios.demo.service;

// Here we have the problem that the task does not return a value
import java.util.concurrent.CompletableFuture;

import io.reactivex.Completable;

public class MailboxHandler {

	public void sendMessage_Sync(String message) {
	}
	
	public CompletableFuture<Void> sendMessage_CF(String message)
	{
		return CompletableFuture.runAsync(()-> sendMessage_Sync( message));
	}
	
	public Completable sendMessage_Rx(String message)
	{
		return Completable.fromAction( ()-> sendMessage_Sync( message));
	}
}
