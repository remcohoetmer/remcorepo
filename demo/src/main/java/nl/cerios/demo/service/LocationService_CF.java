package nl.cerios.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;


public class LocationService_CF {
	private static final Logger LOG = Logger.getLogger(LocationService_CF.class.getName());
	private ConcurrentHashMap<Integer, CompletableFuture<LocationConfig>> cache=
			new ConcurrentHashMap<>();
    
	public CompletionStage<LocationConfig> getLocationConfig( final Integer locationId)
	{
		//CompletableFuture<LocationConfig> f = cache.get(locationId);
        //if (f == null) {
        	// problem: the thread is already started!!
        	CompletableFuture<LocationConfig> futuretask = retrieveLocationConfig( locationId);
        	CompletableFuture<LocationConfig> futuretask2= cache.putIfAbsent(locationId, futuretask);
        	if (futuretask2==null) {
        		// the new task is put into the cache
        		// start futuretask
        		return futuretask;
        	} else {
        		futuretask.cancel(true);
        		// there
        		return futuretask2;
        	}
        //}
        //return f;
    }

	private CompletableFuture<LocationConfig> retrieveLocationConfig( final Integer locationId)
	{
		// create new observable that will trigger DB request
		// pull model: it will only start when subscribed
		LOG.info( "Obtain location "+ locationId);
		return CompletableFuture.supplyAsync( ()->new LocationConfig(locationId), new ScheduledThreadPoolExecutor(2));
	}
}
