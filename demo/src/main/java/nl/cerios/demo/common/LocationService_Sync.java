package nl.cerios.demo.common;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import nl.cerios.demo.LaunderThrowable;


public class LocationService_Sync {

	private ConcurrentHashMap<Integer, Future<LocationConfig>> cache=
			new ConcurrentHashMap<>();

	public LocationConfig getLocationConfig( final Integer locationId) throws InterruptedException
	{
		while (true) {
			Future<LocationConfig> f = cache.get(locationId);
			if (f == null) {
				FutureTask<LocationConfig> futuretask = new FutureTask<LocationConfig>(
						()-> retrieveLocationConfig( locationId));
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
            } catch (CancellationException e) {
                cache.remove(locationId, f);
			} catch (ExecutionException e) {
				throw LaunderThrowable.launderThrowable(e.getCause());
			}
		}
	}

	private LocationConfig retrieveLocationConfig( final Integer locationId)
	{
		// JDBC access
		return new LocationConfig(locationId);
	}
}
