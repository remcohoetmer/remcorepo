package nl.cerios.demo.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import io.reactivex.Observable;

public class LocationService {
	
	private ConcurrentHashMap<Integer, Future<LocationConfig>> cache=
			new ConcurrentHashMap<>();
	
	
	public LocationConfig getLocationConfig( final Integer locationId)
	{
		return new LocationConfig(locationId);
		
	}
	
	public CompletionStage<LocationConfig> getLocationConfig_CF( final Integer locationId)
	{
		return CompletableFuture
		.supplyAsync(()-> new LocationConfig(locationId));
		
	}
	
	public Observable<LocationConfig> getLocationConfig_Rx( final Integer locationId)
	{
		return Observable.just( new LocationConfig(locationId));
		
	}
}
