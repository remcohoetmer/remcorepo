package nl.cerios.demo;

import static nl.cerios.demo.LocationService.locationService;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class LocationService_2 {
	private Map<Integer, Observable<LocationConfig>> map=
			new HashMap<>();


	public Observable<LocationConfig> getLocationConfig( final Integer locationId)
	{
		Observable<LocationConfig> obs= locationService.retrieveLocationConfigObs( locationId);
		map.putIfAbsent( locationId, obs);
		return map.get( locationId);
	}
}
