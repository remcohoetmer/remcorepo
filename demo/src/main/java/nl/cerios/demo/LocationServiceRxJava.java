package nl.cerios.demo;

import io.reactivex.Observable;

public class LocationServiceRxJava {
	public static LocationServiceRxJava locationService= new LocationServiceRxJava();
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
