package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Trip;

import java.util.LinkedList;
import java.util.List;

/**
 * Analyze how many trips can be chained.
 * The idea is to maintain a list of {@code ChainedTrip} sorted by it's earliest ended trip.
 * For every new trip, go through all previous ChainedTips to create a new ChainedTrip.
 */
public final class Chaining implements TripAnalyzer {

  private long totalTrips = 0;
  // trips is a trip list sorted by endtime.
  private List<ChainedTrip> trips = new LinkedList<ChainedTrip>();
  private int maxChainLength = 0;
  private long chainedTripCount = 0;
  

  @Override
  public void addTrip(Trip trip) {
    //System.out.println(trips.size());
    //System.out.println("current start time " + trip.startTime());
    if (trips.size() > 0) {
      //System.out.println("first end time " + trips.get(0).trip1.endTime());
    }
    while (trips.size() > 0 && trips.get(0).getEndTime() < trip.startTime().getTime()) {
      //System.out.println("remove");
      trips.remove(0);
    }
    boolean chained = false;
    List<ChainedTrip> tripsToAdd = new LinkedList<ChainedTrip>();
    // Start a new chained trip.
    tripsToAdd.add(new ChainedTrip(trip, null, 1));

    // Check if trip can be chained to previous trips.
    for (ChainedTrip previousTrip : trips) {
      if (previousTrip.trip1.endTime().getTime() > trip.startTime().getTime()) {
        break;
      }
      if (previousTrip.trip1.endTime().getTime() == trip.startTime().getTime()
          && previousTrip.trip1.getDropOffLocation().equals(trip.getPickUpLocation())) {
        ChainedTrip chainedTrip =
            new ChainedTrip(trip, previousTrip.trip2, previousTrip.chainLength + 1);
        if (chainedTrip.chainLength > maxChainLength) {
          maxChainLength = chainedTrip.chainLength;
        }
        tripsToAdd.add(chainedTrip);
        chained = true;
      }
    }
    for (ChainedTrip tripToAdd : tripsToAdd) {
      trips.add(tripToAdd);
      int i = trips.size()-1;
      while (i > 0
          && trips.get(i).trip1.endTime().getTime() < trips.get(i - 1).trip1.endTime().getTime()) {
        ChainedTrip tmp = trips.get(i);
        trips.set(i, trips.get(i - 1));
        trips.set(i - 1, tmp);
        i--;
      }
    }

    if (chained) {
      chainedTripCount++;
    }
    totalTrips++;
    if (totalTrips % 1000 == 0) {
      System.out.println(stats());
    }
  }

  @Override
  public String stats() {
    return String.format(
        "%.3f %% trips can be chained, longest potential chain length is %d.",
        chainedTripCount * 100.0 / totalTrips, maxChainLength);
  }

  /*
   * A chained trip may contains two trips.
   */
  private static class ChainedTrip {

    // trip1's end time < trip2's end time
    final Trip trip1;
    final Trip trip2;
    final int chainLength;
    public ChainedTrip(Trip trip1, Trip trip2, int chainLength) {
      if (trip2 != null && trip2.endTime().getTime() < trip1.endTime().getTime()) {
        this.trip1 = trip2;
        this.trip2 = trip1;
      } else {
        this.trip1 = trip1;
        this.trip2 = trip2;
      }
      this.chainLength = chainLength;
    }

    public long getEndTime() {
      if (trip2 == null) {
        return trip1.endTime().getTime();
      } else {
        return trip2.endTime().getTime();
      }
    }
  }
}

