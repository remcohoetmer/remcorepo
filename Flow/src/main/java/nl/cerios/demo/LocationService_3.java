package nl.cerios.demo;

import static nl.cerios.demo.LocationService.locationService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import net.jcip.examples.LaunderThrowable;

public class LocationService_3 {
	private ConcurrentHashMap<Integer, Future<LocationConfig>> cache=
			new ConcurrentHashMap<>();
    
	public LocationConfig getLocationConfig( final Integer locationId) throws InterruptedException
	{
        Future<LocationConfig> f = cache.get(locationId);
        if (f == null) {
            FutureTask<LocationConfig> futuretask = new FutureTask<LocationConfig>(
            		()-> locationService.retrieveLocationConfig( locationId));
            f = cache.putIfAbsent(locationId, futuretask);
            if (f == null) { // the futuretask has been entered
                f = futuretask;
                futuretask.run();
            } else {
            	// the futuretask has not entered. f was the cached value
            }
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
    }
    
}
