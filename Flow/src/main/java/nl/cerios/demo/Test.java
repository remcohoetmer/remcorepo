package nl.cerios.demo;

public class Test {

	public static void main(String[] args) throws Exception {

		System.out.println( "LocationConfig: "+ new LocationServiceCache().getLocationConfig(10));
		System.out.println( "LocationConfig: "+ new LocationServiceCache().getLocationConfig(10));
	}

}
