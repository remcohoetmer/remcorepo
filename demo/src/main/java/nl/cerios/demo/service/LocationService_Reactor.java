package nl.cerios.demo.service;

import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class LocationService_Reactor {
	private static final Logger LOG = Logger.getLogger(LocationService_Reactor.class.getName());
	private static ConcurrentHashMap<Integer, Mono<LocationConfig>> cache=
			new ConcurrentHashMap<>();

	public Mono<LocationConfig> getLocationConfig( final Integer locationId)
	{
		Mono<LocationConfig> obs = cache.get(locationId);
		if (obs == null) {
			Mono<LocationConfig> new_obs= retrieveLocationConfig( locationId).cache();
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

	private Mono<LocationConfig> retrieveLocationConfig( final Integer locationId)
	{
		LOG.info( "Obtain location "+ locationId);
		return Mono.defer( ()-> Mono.just( new LocationConfig(locationId)));
	}
}
