package com.uber.crazytexi.data;

public final class Driver {

  private final String hackLicense;
  private long totalDrivingSecs;

  public Driver(String hackLicense) {
    this.hackLicense = hackLicense;
  }

  public void addTrip(Trip trip) {
    totalDrivingSecs += trip.getTripTimeSec();
  }

  public long getOnTripSec() {
    return totalDrivingSecs;
  }
}

