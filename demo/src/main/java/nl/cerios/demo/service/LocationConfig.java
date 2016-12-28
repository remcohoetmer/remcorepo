package nl.cerios.demo.common;

public class LocationConfig {
	public static final Integer DEFAULT= 1000;
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
