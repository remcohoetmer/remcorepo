package nl.cerios.demo.service;

// Here we have the problem that the task does not retuen a value
import java.util.concurrent.CompletableFuture;

import io.reactivex.Flowable;

public class MailboxHandler {

	public void sendMessage(String message) {
	}
	
	// the CF versions sends a dummy value
	public CompletableFuture<Object> sendMessage_CF(String message)
	{
		return CompletableFuture.supplyAsync( ()-> { sendMessage( message); return new Object();});
	}
	
	// Flowable never emits a value, only issues complete when completed
	public Flowable<Object> sendMessage_Rx(String message)
	{
		return Flowable.generate( (consumer)-> {
				sendMessage( message);
				consumer.onComplete(); });
	}
}
