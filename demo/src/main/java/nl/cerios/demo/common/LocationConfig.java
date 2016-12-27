package nl.cerios.demo;

public class LocationConfig {
	private Integer locationId;
	public LocationConfig(Integer locationId)
	{
		this.locationId= locationId;
	}
	@Override
	public String toString()
	{
		return "config "+ locationId;
	}
}
