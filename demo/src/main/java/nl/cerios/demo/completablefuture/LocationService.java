package nl.cerios.demo.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import nl.cerios.demo.LocationConfig;

public class LocationService {
	public CompletionStage<LocationConfig> getLocationConfig( final Integer locationId)
	{
		return CompletableFuture
		.supplyAsync(()-> new LocationConfig(locationId));
		
	}
}
