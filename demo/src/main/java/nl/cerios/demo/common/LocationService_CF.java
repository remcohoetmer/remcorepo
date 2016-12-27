package nl.cerios.demo.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class LocationService_CF {
	private static final Logger LOG = Logger.getLogger(LocationService_CF.class.getName());
	private ConcurrentHashMap<Integer, CompletableFuture<LocationConfig>> cache=
			new ConcurrentHashMap<>();
    
	public CompletionStage<LocationConfig> getLocationConfig( final Integer locationId)
	{
		CompletableFuture<LocationConfig> f = cache.get(locationId);
        if (f == null) {
        	CompletableFuture<LocationConfig> futuretask = retrieveLocationConfig( locationId);
            return cache.putIfAbsent(locationId, futuretask);
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
