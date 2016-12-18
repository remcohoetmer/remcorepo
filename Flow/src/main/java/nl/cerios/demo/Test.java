package nl.cerios.demo;

public class Test {

	public static void main(String[] args) throws Exception {
		System.out.println( "LocationConfig: "+ new LocationService_2().getLocationConfig(10));
		System.out.println( "LocationConfig: "+ new LocationService_3().getLocationConfig(10));
		System.out.println( "LocationConfig: "+ new LocationService_3().getLocationConfig(10));
	}

}
