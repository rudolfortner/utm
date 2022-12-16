#ifndef UTM_H
#define UTM_H

#include <stdint.h>

void convertToUTM(const double& latitudeDegrees, const double& longitudeDegrees, double& easting, double& northing, uint8_t& zoneNumber, char& zoneLetter);
void convertToLatLon(const double& easting, const double& northing, const uint8_t& zoneNumber, const char& zoneLetter, double& latitude, double& longitude);

#endif // UTM_H
