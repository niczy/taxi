# Crazy Taxi
## Overview
### How to run the code
Go to data directory, execute:
```
./download.sh
./unzipdata.sh
```
Execute `src/com/uber/crazytexi/Main.java`

## Metrics
* Total trip 173176321, average trip length 812.016062 seconds.
* A driver has 0.457711 trips per hour on average
* Average driver utilization 10.324%

## Pickup matching
The algorithmm is implenmented in `PickupMatching.java`. The idea is to sort the trip by start time. Then go over all the trip, and look forward to find trips that the distance between of their pickup location is smaller then `maxWalkingDistanceMiles` and the difference between their pickup time is smaller than `maxWaitTime`.

Unforturtantly, I didn't finish it since one month's trip data can't be sorted in memory, I would do some preprocess first, break the 12 files into 8784 files, each file contains the trips for only one data. Then run the same algorithm. (updated: it's done)

## Trip matching
At a given time, if there're two trips with:
```
 pickLocation1, dropLocation2, pickLocation2, dropLocation2.
```
The driver has the options to go through following routes:

```
  driverLocation, pickLocation1,  pickLocation2, dropLocation1, dropLocation2
  driverLocation, pickLocation1,  pickLocation2, dropLocation2, dropLocation1
  driverLocation, pickLocation2,  pickLocation1, dropLocation1, dropLocation2
  driverLocation, pickLocation2,  pickLocation1, dropLocation2, dropLocation1
```

I would chose a route that minimaze the (driver's driving distance + user's waiting time + user's ontrip time). If the minmal values is larget than certain threshold, the those two trips can't be matched.

## Chaining
Similar to pick up matching, we need to first sort one day's trips.

## TODO
* Didn't look at the data size first, will try to break up the data by day, and solve remaining problems.



