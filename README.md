# MeterDataManagementService

During the development of SUNSHINE’s platform the scope of Meter Data Management Service has been extended from the management of meter data to the management of data coming more generally from sensors. More specifically, the Meter Data Management service manages:
* Consumption data from energy meters in pilot buildings;
*	Indoor temperature readings from indoor sensors in pilot buildings;
*	Weather observation and forecasts data from a dedicated weather service from Meteogrid;
*	Status data (dimming level, hours of use, etc) from pilot lamps and light-lines.

All these data types are stored in the same Sensor DB and managed with common procedures. 
In brief, the purpose of the Meter Data Management Service is to:
* Gather all the data coming from meters/sensors; 
*	Elaborate them and generate derived data, if necessary (Section 0);
*	Store them in a normalized format into a repository (Section 0) from which they can be retrieved through a standard interface (Section 0).

For consumption data from meters two data flows are supported by the platform:
*	Simple FTP transfer of CSV file;
*	Feed from Green Button web service. 
Two service endpoints correspond to these types of flows: 
*	FTP Ingestion service;
*	Green Button Ingestion service.
*	
To maximize the reuse of the developed software, the Green Button Ingestion service doesn’t load directly the data in the DB, but produces a CSV file that is treated by a dedicated component, the CSV loader, that is the same used by the FTP Ingestion service. 
The flow of indoor temperature readings is channelled through the Green Button web service.
