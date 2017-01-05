package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class LocationService_CF {
	private static final Logger LOG = Logger.getLogger(LocationService_CF.class.getName());
	private ConcurrentHashMap<Integer, CompletableFuture<LocationConfig>> cache=
			new ConcurrentHashMap<>();
    
	public CompletableFuture<LocationConfig> getLocationConfig( final Integer locationId)
	{
		CompletableFuture<LocationConfig> f = cache.get(locationId);
        if (f == null) {
			// problem: if we create a CompletableFuture, the thread starts automatically
			// solution: we create a trigger task on which the data retrieval is dependent
			CompletableFuture<Void> trigger = new CompletableFuture<>();
			CompletableFuture<LocationConfig> futuretask= trigger.thenCompose(dummy->retrieveLocationConfig(locationId));
        	CompletableFuture<LocationConfig> futuretask2= cache.putIfAbsent(locationId, futuretask);
        	if (futuretask2==null) {
        		// the new task is put into the cache
        		// start futuretask
            	trigger.complete( null);//
        		return futuretask;
        	} else {
        		// there was already a task in the cash, the newly created tasks must be cancelled
        		trigger.cancel(true);
        		futuretask.cancel(true);
        		return futuretask2;
        	}
        }
        return f;
    }

	private CompletableFuture<LocationConfig> retrieveLocationConfig( final Integer locationId)
	{
		// create new observable that will trigger DB request
		// pull model: it will only start when subscribed
		LOG.info( "Obtain location "+ locationId);
		return CompletableFuture.supplyAsync( ()->new LocationConfig(locationId));
	}
}
