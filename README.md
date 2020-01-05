GPX route converter
===================
The aim of this simple *Java* tool is to convert flight plans produced with the free software [**VfrFlight**](http://www.vfrflight.org) to **GPX** and viceversa.

VfrFlight
---------
*VfrFlight* is a free, cross platform tool for VFR flight planning. It can be used for real world flight planning and training in *FSX*, *FSX:SE*, *Prepar3D*, *X-Plane* 10/11 and *FlightGear*.  
Unfortunately *VfrFlight* (version 2.3.1) it is not supporting the *GPX* format and that's where **GPXrouteConverter** comes into play.
* To know know more about *VfrFlight* check its [documentation](http://serwer1998768.home.pl/vfr/en/documentation.html).
* To get *VfrFlight* check its [download page](http://serwer1998768.home.pl/vfr/en/downloads.php).

GPX
---
GPX is an open XML schema that allows to store in a file collections of: way points, routes and tracks. It is always more used by many GPS devices and applications, because this format is independent from any GPS device producer it is often used to exchange data between devices and softwares of different brands. For example: also with the popular GoogleEarth it is possible to open GPX files and see routes and tracks even in 3D view. More information about the GPX format can be found on its [official website](https://www.topografix.com/gpx.asp).  
A GPX flight plan can be then loaded (and flown) on other free and open source software such as [LK8000](https://www.lk8000.it/).

Download GPXrouteConverter
--------------------------
Here, an already compiled *Java* JAR executable: [**GPXrouteConverter.jar**](https://www.alus.it/airnavigator/GPXrouteConverter.jar).

How to use it
-------------
1. Launch the provided JAR executable
   - On *Windows* it should be enough to double click the JAR file
   - Anyway from command line it would be: `java -jar GPXrouteConverter.jar`
2. Browse to the directory where the file(s) to convert are located
3. Choose if you want to convert **from** *VfrFlight* files (.vfr) or **from** *GPX* files (.gpx)
4. Select the file(s) to be converted
5. Press the *Convert* button
6. The converted files will be created in the same folder
   - The selected *VfrFlight* file(s) will be converted to *GPX* with **.gpx** extension ...
   - ... or the selected *GPX* file(s) will be converted to *VfrFlight* with **.vfr** extension

