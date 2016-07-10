package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Trip;

public class TripLength implements TripAnalyzer {

  private long tripCount = 0;
  private long totalSec = 0;

  @Override
  public void addTrip(Trip trip) {
    if (trip.isBadData()) {
      return;
    }
    tripCount++;
    totalSec += trip.getTripTimeSec();
  }

  @Override
  public String stats() {
    return String.format(
        "Total trip %d, average trip length %f\n", tripCount, totalSec * 1.0 / tripCount);
  }
}

