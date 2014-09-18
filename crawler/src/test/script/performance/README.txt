Testing the YAMS BaseX connector performance

There are two scripts:
 * testyams-hint.sh for testing the 'hint' service (autocomplete)
 * testyams-search.sh for testing the search service (search results)

Both take three parameters:

 1. the base URL of the service
 2. the search term (URL encoded)
 3. the number of times to retrieve the result

Both will output the total response times for each request as well as a sum
and an average.

Useful values:

 1. Base URL
	- http://lux16.mpi.nl/ds/yams-basex-connector/rest
	- https://lux17.mpi.nl/cmdi/lat/yams-basex-connector/rest
 2. Search term
	- co (a short term that yields results in the hints service)
	- comic (a longer hints term as well as a search term that yields
	results)
 3. Number of times to query
	- anything between 2 and 10 (in some cases might take a long time to
	complete)
