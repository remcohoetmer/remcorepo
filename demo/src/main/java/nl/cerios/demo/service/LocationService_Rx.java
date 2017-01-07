package nl.cerios.demo.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import io.reactivex.Single;


public class LocationService_Rx {
	private static final Logger LOG = Logger.getLogger(LocationService_Rx.class.getName());
	private static ConcurrentHashMap<Integer, Single<LocationConfig>> cache=
			new ConcurrentHashMap<>();

	public Single<LocationConfig> getLocationConfig( final Integer locationId) throws ValidationException
	{
		Single<LocationConfig> obs = cache.get(locationId);
		if (obs == null) {
			Single<LocationConfig> new_obs= retrieveLocationConfig( locationId).cache();
			obs = cache.putIfAbsent(locationId, new_obs);
			if (obs==null) {
				return new_obs; // there was no item in the cache
			} else {
				return obs;// de cache had toch een waarde
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

	private Single<LocationConfig> retrieveLocationConfig( final Integer locationId)
	{
		LOG.info( "Obtain location "+ locationId);
		return Single.defer( ()->Single.just( new LocationConfig(locationId)));	
	}
}
