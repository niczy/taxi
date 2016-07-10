package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Trip;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PickupMatching implements TripAnalyzer {

  private final List<Trip> trips = new ArrayList<Trip>();
  private double maxWalkingDistanceMiles = 0.2;
  private final Duration maxWaitTime;
  private long totalTrip;
  private long matchedTripCount = 0;

  public PickupMatching(Duration maxWaitTime) {
    this.maxWaitTime = maxWaitTime;
  }

  @Override
  public void addTrip(Trip trip) {
    if (trip.isBadData()) {
      return;
    }
    //this.trips.add(trip);
    totalTrip++;
  }

  public void endMonth() {
    // Clear previous months trips, assuming trip from different months won't match.
    trips.clear();
  }

  @Override
  public String stats() {
    System.out.println("Total trip size: " + trips.size());
    for (int i = 0; i < trips.size(); i++) {
      for (int j = i - 1; j >= 0; j--) {
        Trip trip1 = trips.get(i);
        Trip trip2 = trips.get(j);
        if (trip1.startTime().getTime() <= trip2.startTime().getTime() + maxWaitTime.toMillis()
            && trip1.getPickUpLocation().distanceMiles(trip2.getPickUpLocation())
                <= maxWalkingDistanceMiles) {
          matchedTripCount++;
          //System.out.println(trips.get(j).startTime() + "---" + trips.get(i).startTime());
          break;
        } else {
          break;
        }
      }
    }
    return String.format(
        "%.4f%% of trips can be matched with each other.", matchedTripCount * 100.0 / totalTrip);
  }
}

