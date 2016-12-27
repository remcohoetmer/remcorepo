package nl.cerios.demo.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;


public class LocationService_Rx {
	private static ConcurrentHashMap<Integer, Observable<LocationConfig>> cache=
			new ConcurrentHashMap<>();

	public Observable<LocationConfig> getLocationConfig( final Integer locationId) throws InterruptedException
	{
		Observable<LocationConfig> obs = cache.get(locationId);
		if (obs == null) {
			Observable<LocationConfig> new_obs= retrieveLocationConfig( locationId).cacheWithInitialCapacity(1);
			obs = cache.putIfAbsent(locationId, new_obs);
		}
		return obs;
		/*
		 * Pull model: Er wordt een observable teruggegeven die initieel nog niets doen.
		 * Pas als de waarde ergens nodig is, wordt de waarde opgehaald.
		 * Deze waarde wordt vervolgens hergebruikt indien
		 * De concurrent code is toegevoegd om te voorkomen dat meerdere clients dezelfde actie triggeren.
		 */
	}

	/*
	 * Alternative implementatie: er zit logica in de observable dat de cache na een seconde automatisch wordt geupdate
	 */
	public Observable<LocationConfig> getLocationConfigAlternative( final Integer locationId)
	{
		Observable<LocationConfig> obs = cache.get(locationId);
		if (obs == null) {
			Observable<LocationConfig> new_obs= retrieveLocationConfig( locationId).replay(1, TimeUnit.SECONDS).autoConnect();
			obs = cache.putIfAbsent(locationId, new_obs);
		}
		return obs;
	}


	private Observable<LocationConfig> retrieveLocationConfig( final Integer locationId)
	{
		// create new observable that will trigger DB request
		// pull model: it will only start when subscribed
		return Observable.just( new LocationConfig(locationId));
	}
}
