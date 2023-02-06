#include "utm.h"

#include <cmath>
#include <stdexcept>
#include <stdint.h>

namespace utm
{

static char ZONE_LETTERS[] = "CDEFGHJKLMNPQRSTUVWXX";

// Flattening
static double f = 1.0 / 298.257223563;

// Equatorial Radius
static double a = 6378.137;		// km
static double N0 = 10000.0;	// km
static double E0 = 500.0;		// km

static double k0 = 0.9996;

static double n = f / (2.0 - f);
static double A = (1 + n*n/4.0 + n*n*n*n/64.0) * a * (1 + n);

static double a1 = 0.5 * n - 2.0*n*n/3.0 + 5.0*n*n*n/16.0;
static double a2 = 13.0 *n*n / 48.0 - 3.0 * n*n*n / 5.0;
static double a3 = 61.0 * n*n*n / 240.0;

static double b1 = 0.5 * n - 2.0*n*n/3.0 + 37.0*n*n*n/96.0;
static double b2 = n*n / 48.0 + n*n*n / 15.0;
static double b3 = 17.0 * n*n*n / 480.0;

static double g1 = 2.0*n - 2.0*n*n/3.0 - 2.0*n*n*n;
static double g2 = 7.0*n*n/3.0 - 8.0 * n*n*n / 5.0;
static double g3 = 56.0 * n*n*n / 15.0;


static double toRadians(const double& degrees)
{
	return degrees * M_PI / 180.0;
}

static double toDegrees(const double& radians)
{
	return 180.0 * radians / M_PI;
}

static uint8_t toZoneNumber(const double& longitude)
{
	return uint8_t((longitude + 180.0) / 6.0) + 1;
}

static char toZoneLetter(const double& latitude)
{
	if(latitude < -80.0) throw std::runtime_error("South-Pole not supported!");
	if(latitude > 84.0) throw new std::runtime_error("North-Pole not supported!");

	uint8_t index = uint8_t(latitude + 80.0) >> 3;
	return ZONE_LETTERS[index];
}

static uint8_t toCentralLongitude(uint8_t zoneNumber)
{
	return (zoneNumber - 1.0) * 6.0 - 180.0 + 3.0;
}

void convertToUTM(const utm::Position& position, utm::PositionUTM& utm)
{
	convertToUTM(position.latitude, position.longitude,
				 utm.easting, utm.northing, utm.zoneNumber, utm.zoneLetter);
}

void convertToUTM(const double& latitudeDegrees, const double& longitudeDegrees, double& easting, double& northing, uint8_t& zoneNumber, char& zoneLetter)
{
	zoneNumber = toZoneNumber(longitudeDegrees);
	zoneLetter = toZoneLetter(latitudeDegrees);

	double latitude = toRadians(latitudeDegrees);
	double longitude = toRadians(longitudeDegrees);

	double lon0 = toRadians(toCentralLongitude(zoneNumber));

	double t = sinh(atanh(sin(latitude)) - (2.0 * sqrt(n) / (1+n)) * atanh(2.0 * sqrt(n) * sin(latitude) / (1 + n)));
	double e = atan2(t, cos(longitude - lon0));
	double ny = atanh(sin(longitude - lon0) / sqrt(1 + t*t));

	double sumE = 0.0;
	sumE += a1 * cos(2.0 * 1.0 * e) * sinh(2.0 * 1.0 * ny);
	sumE += a2 * cos(2.0 * 2.0 * e) * sinh(2.0 * 2.0 * ny);
	sumE += a3 * cos(2.0 * 3.0 * e) * sinh(2.0 * 3.0 * ny);
	easting = E0 + k0 * A * (ny + sumE);

	double sumN = 0.0;
	sumN += a1 * sin(2.0 * 1.0 * e) * cosh(2.0 * 1.0 * ny);
	sumN += a2 * sin(2.0 * 2.0 * e) * cosh(2.0 * 2.0 * ny);
	sumN += a3 * sin(2.0 * 3.0 * e) * cosh(2.0 * 3.0 * ny);
	northing = k0 * A * (e + sumN);
	if(latitude < 0.0) northing += N0;
}

void convertToLatLon(const utm::PositionUTM& utm, utm::Position& position)
{
	convertToLatLon(utm.easting, utm.northing, utm.zoneNumber, utm.zoneLetter,
					position.latitude, position.longitude);
}

void convertToLatLon(const double& easting, const double& northing, const uint8_t& zoneNumber, const char& zoneLetter, double& latitude, double& longitude)
{
	double equatorialOffset = zoneLetter <= 'M' ? N0 : 0.0;
	double e = (northing - equatorialOffset) / (k0 * A);
	double ny  = (easting - E0) / (k0 * A);

	double sume = 0.0;
	sume += b1 * sin(2.0 * 1.0 * e) * cosh(2.0 * 1.0 * ny);
	sume += b2 * sin(2.0 * 2.0 * e) * cosh(2.0 * 2.0 * ny);
	sume += b3 * sin(2.0 * 3.0 * e) * cosh(2.0 * 3.0 * ny);
	double ee = e - sume;

	double sumn = 0.0;
	sumn += b1 * cos(2.0 * 1.0 * e) * sinh(2.0 * 1.0 * ny);
	sumn += b2 * cos(2.0 * 2.0 * e) * sinh(2.0 * 2.0 * ny);
	sumn += b3 * cos(2.0 * 3.0 * e) * sinh(2.0 * 3.0 * ny);
	double nn = ny - sumn;

	double kappa = asin(sin(ee) / cosh(nn));


	double sumLat = 0.0;
	sumLat += g1 * sin(2.0 * 1.0 * kappa);
	sumLat += g2 * sin(2.0 * 2.0 * kappa);
	sumLat += g3 * sin(2.0 * 3.0 * kappa);
	latitude = kappa + sumLat;

	double lon0 = zoneNumber * 6.0 - 183.0;
	longitude = toRadians(lon0) + atan2(sinh(nn), cos(ee));

	latitude = toDegrees(latitude);
	longitude = toDegrees(longitude);
}

}	// end namespace utm
