/**
 * @author rudolfortner
 * @version 1.0
 */
public class PositionUTM {
	
	public static final String ZONE_LETTERS = "CDEFGHJKLMNPQRSTUVWXX";
	
	// Flattening
	public static final double f = 1.0 / 298.257_223_563;
	
	// Equatorial Radius
	public static final double a = 6378.137;	// km
	public static final double N0 = 10_000.0;	// km
	public static final double E0 = 500.0;	// km
	
	public static final double k0 = 0.9996;
	
	public static final double n = f / (2.0 - f);
	public static final double A = (1 + n*n/4.0 + n*n*n*n/64.0) * a * (1 + n);
	
	public static final double a1 = 0.5 * n - 2.0*n*n/3.0 + 5.0*n*n*n/16.0;
	public static final double a2 = 13.0 *n*n / 48.0 - 3.0 * n*n*n / 5.0;
	public static final double a3 = 61.0 * n*n*n / 240.0;
	
	public static final double b1 = 0.5 * n - 2.0*n*n/3.0 + 37.0*n*n*n/96.0;
	public static final double b2 = n*n / 48.0 + n*n*n / 15.0;
	public static final double b3 = 17.0 * n*n*n / 480.0;
	
	public static final double g1 = 2.0*n - 2.0*n*n/3.0 - 2.0*n*n*n;
	public static final double g2 = 7.0*n*n/3.0 - 8.0 * n*n*n / 5.0;
	public static final double g3 = 56.0 * n*n*n / 15.0;
	
	
	// ------------ CLASS --------------

	protected double easting, northing;
	protected char zoneLetter;
	protected int zoneNumber;
	
	/**
	 * @param easting Easting in kilometers
	 * @param northing Northing in kilometers
	 * @param zoneNumber UTM zone number
	 * @param zoneLetter UTM zone letter
	 */
	public PositionUTM(double easting, double northing, int zoneNumber, char zoneLetter) {
		this.setEasting(easting);
		this.setNorthing(northing);
		this.setZoneNumber(zoneNumber);
		this.setZoneLetter(zoneLetter);
	}
	
	public Position toLatLon() {
		return toLatLon(this);
	}
	
	
	public double getEasting() {
		return easting;
	}

	public void setEasting(double easting) {
		this.easting = easting;
	}

	public double getNorthing() {
		return northing;
	}

	public void setNorthing(double northing) {
		this.northing = northing;
	}

	public char getZoneLetter() {
		return zoneLetter;
	}

	public void setZoneLetter(char zoneLetter) {
		if(ZONE_LETTERS.indexOf(zoneLetter) == -1)
			throw new IllegalArgumentException("Zone not supported !");
		
		this.zoneLetter = zoneLetter;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		if(zoneNumber < 1 || zoneNumber > 60)
			throw new IllegalArgumentException("Zone Number must be in range 01-60 !");
		
		this.zoneNumber = zoneNumber;
	}
	
	
	
	

	public static double atanh(double x) {
		return (Math.log(1 + x) - Math.log(1 - x)) / 2.0;
	}
	
	/**
	 * Converts a given latitude in degrees into the appropriate UTM zone letter
	 * @param latitude Latitude in degrees
	 * @return UTM zone letter
	 */
	public static char toZoneLetter(double latitude) {
		if(latitude < -80.0) throw new IllegalArgumentException("South-Pole not supported!");
		if(latitude > 84.0) throw new IllegalArgumentException("North-Pole not supported!");
		
		int index = ((int) (latitude + 80.0)) >> 3;
		return ZONE_LETTERS.charAt(index);
	}
	
	/**
	 * Converts a given longitude in degrees into the appropriate UTM zone number
	 * @param longitude Longitude in degrees
	 * @return UTM zone number
	 */
	public static int toZoneNumber(double longitude) {
		return ((int) (longitude + 180.0) / 6) + 1;
	}
	
	/**
	 * Calculates the appropriate longitude of the center for a given zone
	 * @param zoneNumber UTM zone number
	 * @return Longitude value in degrees
	 */
	public static double toCentralLongitude(int zoneNumber) {
		return (zoneNumber - 1.0) * 6.0 - 180.0 + 3.0;
	}
	
	public static PositionUTM fromLatLon(Position position) {
		return fromLatLon(position.getLatitude(), position.getLongitude());
	}
	
	/**
	 * Converts a given lat/lon location into UTM coordinates
	 * @param latitude Latitude in degrees
	 * @param longitude Longitude in degrees
	 * @return A PositionUTM object
	 */
	public static PositionUTM fromLatLon(double latitude, double longitude) {
		int ZN = toZoneNumber(longitude);
		char ZL = toZoneLetter(latitude);
		
		latitude = Math.toRadians(latitude);
		longitude = Math.toRadians(longitude);
		
		double lon0 = Math.toRadians(toCentralLongitude(ZN));
		
		double t = Math.sinh(atanh(Math.sin(latitude)) - (2.0 * Math.sqrt(n) / (1+n)) * atanh(2.0 * Math.sqrt(n) * Math.sin(latitude) / (1 + n)));
		double e = Math.atan2(t, Math.cos(longitude - lon0));
		double ny = atanh(Math.sin(longitude - lon0) / Math.sqrt(1 + t*t));
		
		double sumE = 0.0;
		sumE += a1 * Math.cos(2.0 * 1.0 * e) * Math.sinh(2.0 * 1.0 * ny);
		sumE += a2 * Math.cos(2.0 * 2.0 * e) * Math.sinh(2.0 * 2.0 * ny);
		sumE += a3 * Math.cos(2.0 * 3.0 * e) * Math.sinh(2.0 * 3.0 * ny);
		double E = E0 + k0 * A * (ny + sumE);
		
		double sumN = 0.0;
		sumN += a1 * Math.sin(2.0 * 1.0 * e) * Math.cosh(2.0 * 1.0 * ny);
		sumN += a2 * Math.sin(2.0 * 2.0 * e) * Math.cosh(2.0 * 2.0 * ny);
		sumN += a3 * Math.sin(2.0 * 3.0 * e) * Math.cosh(2.0 * 3.0 * ny);
		double N = k0 * A * (e + sumN);
		if(latitude < 0.0) N += N0;
		
		return new PositionUTM(E, N, ZN, ZL);
	}
	
	public static Position toLatLon(PositionUTM utm) {
		return toLatLon(utm.easting, utm.northing, utm.zoneNumber, utm.zoneLetter);
	}
	
	/**
	 * Converts a given UTM position into lat/lon format
	 * @param easting Easting in kilometers
	 * @param northing Northing in kilometers
	 * @param zoneNumber UTM zone number
	 * @param zoneLetter UTM zone letter
	 * @return Position object
	 */
	public static Position toLatLon(double easting, double northing, int zoneNumber, char zoneLetter) {
		
		double equatorialOffset = zoneLetter <= 'M' ? N0 : 0.0;
		double e = (northing - equatorialOffset) / (k0 * A);
		double ny  = (easting - E0) / (k0 * A);
		
		double sume = 0.0;
		sume += b1 * Math.sin(2.0 * 1.0 * e) * Math.cosh(2.0 * 1.0 * ny);
		sume += b2 * Math.sin(2.0 * 2.0 * e) * Math.cosh(2.0 * 2.0 * ny);
		sume += b3 * Math.sin(2.0 * 3.0 * e) * Math.cosh(2.0 * 3.0 * ny);
		double ee = e - sume;
		
		double sumn = 0.0;
		sumn += b1 * Math.cos(2.0 * 1.0 * e) * Math.sinh(2.0 * 1.0 * ny);
		sumn += b2 * Math.cos(2.0 * 2.0 * e) * Math.sinh(2.0 * 2.0 * ny);
		sumn += b3 * Math.cos(2.0 * 3.0 * e) * Math.sinh(2.0 * 3.0 * ny);
		double nn = ny - sumn;
		
		double kappa = Math.asin(Math.sin(ee) / Math.cosh(nn));
		
		
		double sumLat = 0.0;
		sumLat += g1 * Math.sin(2.0 * 1.0 * kappa);
		sumLat += g2 * Math.sin(2.0 * 2.0 * kappa);
		sumLat += g3 * Math.sin(2.0 * 3.0 * kappa);
		double latitude = kappa + sumLat;
		
		double lon0 = zoneNumber * 6.0 - 183.0;
		double longitude = Math.toRadians(lon0) + Math.atan2(Math.sinh(nn), Math.cos(ee));
		
		latitude = Math.toDegrees(latitude);
		longitude = Math.toDegrees(longitude);
		
		return new Position(latitude, longitude);
	}
}
