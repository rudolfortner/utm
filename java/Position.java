/**
 * @author rudolfortner
 * @version 1.0
 */
public class Position {

	protected double latitude;
	protected double longitude;
	
	/**
	 * @param latitude Latitude in degrees
	 * @param longitude Longitude in degrees
	 */
	public Position(double latitude, double longitude) {
		super();
		
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	public PositionUTM toUTM() {
		return toUTM(this);
	}
	
	public static PositionUTM toUTM(Position position) {
		return PositionUTM.fromLatLon(position);
	}
	
	public static PositionUTM toUTM(double latitude, double longitude) {
		return PositionUTM.fromLatLon(latitude, longitude);
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		if(latitude < -90.0 || latitude > 90.0)
			throw new IllegalArgumentException("Wrong value for latitude !");
	
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		if(longitude < -180.0 || longitude > 180.0)
			throw new IllegalArgumentException("Wrong value for longitude !" + " " + longitude);
		
		this.longitude = longitude;
	}
}
