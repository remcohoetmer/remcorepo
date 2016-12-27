package nl.cerios.demo.common;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import nl.cerios.demo.LaunderThrowable;
import nl.cerios.demo.http.impl.PurchaseHttpHandlerImpl;


public class LocationService_Sync {
	private static final Logger LOG = Logger.getLogger(LocationService_Sync.class.getName());
	
	private ConcurrentHashMap<Integer, Future<LocationConfig>> cache=
			new ConcurrentHashMap<>();

	public LocationConfig getLocationConfig( final Integer locationId)
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
				try {
					return f.get();
				} catch (InterruptedException e) {
					throw LaunderThrowable.launderThrowable( e);
				}
            } catch (CancellationException e) {
                cache.remove(locationId, f);
			} catch (ExecutionException e) {
				throw LaunderThrowable.launderThrowable(e.getCause());
			}
		}
	}

	private LocationConfig retrieveLocationConfig( final Integer locationId)
	{
		LOG.info( "Obtain location "+ locationId);
		// JDBC access
		return new LocationConfig(locationId);
	}
}
