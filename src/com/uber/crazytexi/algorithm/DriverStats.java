package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Driver;
import com.uber.crazytexi.data.Trip;

import java.util.HashMap;
import java.util.Map;

public class DriverStats implements TripAnalyzer {
  private static final long TOTAL_HOURS = 365 * 24;
  private static final long TOTAL_SECS = TOTAL_HOURS * 60 * 60;

  private Map<String, Driver> drivers = new HashMap<String, Driver>();
  private int totalTrips = 0;
  private long totalTripSec = 0;

  @Override
  public void addTrip(Trip trip) {
    Driver driver = drivers.get(trip.getHackLicense());
    if (driver == null) {
      driver = new Driver(trip.getHackLicense());
      drivers.put(trip.getHackLicense(), driver);
    }
    if (!trip.isBadData()) {
      driver.addTrip(trip);
      totalTrips++;
      totalTripSec += trip.getTripTimeSec();
    }
  }

  @Override
  public String stats() {
    return String.format(
        "A driver has %f trips per hour on average\n" +
        "Average driver utilization %f%%\n",
        totalTrips * 1.0 / TOTAL_HOURS / drivers.size(),
        totalTripSec * 100.0 / TOTAL_SECS / drivers.size());
  }

  @Override
  public void waitUntilDone() {
    return;
  }
}

