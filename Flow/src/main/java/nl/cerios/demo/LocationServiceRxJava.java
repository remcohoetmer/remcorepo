package nl.cerios.demo;

import io.reactivex.Observable;

public class LocationService {
	public static LocationService locationService= new LocationService();
	public LocationConfig retrieveLocationConfig( final Integer locationId) throws Exception
	{
		// long lasting task ...
		return new LocationConfig(locationId);
	}

	public Observable<LocationConfig> retrieveLocationConfigObs( final Integer locationId)
	{
		// long lasting task
		return Observable.just( new LocationConfig(locationId));
	}
}
