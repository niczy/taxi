package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Trip;

import java.time.Duration;
import java.util.LinkedList;

public class PickupMatching implements TripAnalyzer {

  private final LinkedList<Trip> trips = new LinkedList<Trip>();
  private double maxWalkingDistanceMiles;
  private final Duration maxWaitTime;
  private long totalTrip;
  private long matchedTripCount = 0;
  private Trip lastTrip;

  public PickupMatching(double maxWalkingDistanceMiles, Duration maxWaitTime) {
    this.maxWalkingDistanceMiles = maxWalkingDistanceMiles;
    this.maxWaitTime = maxWaitTime;
  }

  @Override
  public void addTrip(Trip trip) {
    if (trip.isBadData()) {
      return;
    }
    
    if (lastTrip != null) {
      if (lastTrip.startTime().getTime() > trip.startTime().getTime()) {
        throw new RuntimeException("Trips are not sorted by pick up time.");
      }
    }
    while (trips.size() > 0
        && trips.get(0).startTime().getTime() + maxWaitTime.toMillis()
            < trip.startTime().getTime()) {
      trips.remove(0);
    }
      for (Trip earlierTrip : trips) {
        if (earlierTrip.getPickUpLocation().distanceMiles(trip.getPickUpLocation())
            <= maxWalkingDistanceMiles) {
          matchedTripCount++;
          // this new trip can be matched with a earlier trip.
          break;
        }
      }
    this.trips.add(trip);

    lastTrip = trip;
    totalTrip++;
    if (totalTrip % 100000 == 0) {
      System.out.println(stats());
    }
  }

  @Override
  public String stats() {
    return String.format(
        "%.4f%% of trips can be matched with each other when max wait time "
        + "is %d minutes and max walking distance is %.3f.",
        matchedTripCount * 100.0 / totalTrip, maxWaitTime.toMinutes(), maxWalkingDistanceMiles);
  }
}

