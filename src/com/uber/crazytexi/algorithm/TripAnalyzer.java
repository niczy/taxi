package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Trip;

public interface TripAnalyzer {

  /**
   * Feed one trip to the analyzer.
   * @param trip
   */
  public void addTrip(Trip trip);

  /**
   * Return the string report.
   */
  public String stats();

  public void waitUntilDone();
}

