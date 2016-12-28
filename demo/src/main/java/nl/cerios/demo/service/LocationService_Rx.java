package nl.cerios.demo.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.reactivex.Flowable;


public class LocationService_Rx {
	private static final Logger LOG = Logger.getLogger(LocationService_Rx.class.getName());
	private static ConcurrentHashMap<Integer, Flowable<LocationConfig>> cache=
			new ConcurrentHashMap<>();

	public Flowable<LocationConfig> getLocationConfig( final Integer locationId)
	{
		Flowable<LocationConfig> obs = cache.get(locationId);
		if (obs == null) {
			Flowable<LocationConfig> new_obs= retrieveLocationConfig( locationId).cacheWithInitialCapacity(1);
			obs = cache.putIfAbsent(locationId, new_obs);
			if (obs==null) {
				// there was no item in the cache
				return new_obs;
			} else {
				return obs;
			}
		}
		return obs;
		/*
		 * Pull model: Er wordt een Flowable teruggegeven die initieel nog niets doen.
		 * Pas als de waarde ergens nodig is, wordt de waarde opgehaald.
		 * Deze waarde wordt vervolgens hergebruikt indien
		 * De concurrent code is toegevoegd om te voorkomen dat meerdere clients dezelfde actie triggeren.
		 */
	}

	/*
	 * Alternative implementatie: er zit logica in de Flowable dat de cache na een seconde automatisch wordt geupdate
	 */
	public Flowable<LocationConfig> getLocationConfigAlternative( final Integer locationId)
	{
		Flowable<LocationConfig> obs = cache.get(locationId);
		if (obs == null) {
			Flowable<LocationConfig> new_obs= retrieveLocationConfig( locationId).replay(1, TimeUnit.SECONDS).autoConnect();
			obs = cache.putIfAbsent(locationId, new_obs);
		}
		return obs;
	}

	private Flowable<LocationConfig> retrieveLocationConfig( final Integer locationId)
	{
		LOG.info( "Obtain location "+ locationId);
		// create new Flowable that will trigger DB request
		// pull model: it will only start when subscribed
		return Flowable.defer( ()->Flowable.just( new LocationConfig(locationId)));	
	}
}
