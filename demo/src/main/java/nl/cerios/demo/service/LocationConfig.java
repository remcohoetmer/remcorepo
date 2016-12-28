package nl.cerios.demo.service;

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
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
}
