# UTM
The [Universal Transverse Mercator coordinate system](https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system) is often used to convert from latitude/longitude coordinates into a cartesian coordinate system. This is useful for path planning, performing measurements and other things that are not easy in a spherical coordinate system.

## Implementation
The files provided by this repository where implemented according to the formulas described on the [Wikipedia](https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system) page. This implementation is only ment for latitude values from -80° to 84°. The zones (A, B, Y, Z) for the poles are not included in this code. The [Universal polar stereographic coordinate system](https://en.wikipedia.org/wiki/Universal_polar_stereographic_coordinate_system) should be used for this regions according to the UTM specification.

## Installation
There is no installation needed. Just download the appropriate files and put them into your project directory. Adjustments have to be made for some languages for the compiler to include the files into compilation.

## Disclaimer
As the license already states, we give no warranty nor guarantee that the files are error free or performant. We tested the Java version in our own projects and found no errors while working in the area of middle europe. If you find any errors please let us know!
