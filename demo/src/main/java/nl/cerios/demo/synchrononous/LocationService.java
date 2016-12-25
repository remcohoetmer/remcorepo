package nl.cerios.demo.synchrononous;

import nl.cerios.demo.LocationConfig;

public class LocationService {
	public LocationConfig getLocationConfig( final Integer locationId)
	{
		return new LocationConfig(locationId);
		
	}
}
