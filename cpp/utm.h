#ifndef UTM_H
#define UTM_H

#include <stdint.h>

namespace utm
{
	struct Position {
		double latitude;
		double longitude;
	};

	struct PositionUTM {
		double easting;
		double northing;
		uint8_t zoneNumber;
		char zoneLetter;
	};

	void convertToUTM(const Position& position, PositionUTM& utm);
	void convertToUTM(const double& latitudeDegrees, const double& longitudeDegrees, double& easting, double& northing, uint8_t& zoneNumber, char& zoneLetter);

	void convertToLatLon(const PositionUTM& utm, Position& position);
	void convertToLatLon(const double& easting, const double& northing, const uint8_t& zoneNumber, const char& zoneLetter, double& latitude, double& longitude);

}	// end namespace utm

#endif // UTM_H
