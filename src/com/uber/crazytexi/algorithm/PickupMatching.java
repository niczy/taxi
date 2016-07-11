package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Trip;

import java.time.Duration;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PickupMatching implements TripAnalyzer {

  private static final int MAX_BUFFER_SIZE = 1000;
  private final LinkedList<Trip> trips = new LinkedList<Trip>();
  private final BlockingQueue<Trip> tripBuffer = new ArrayBlockingQueue<>(MAX_BUFFER_SIZE);
  private double maxWalkingDistanceMiles;
  private final Duration maxWaitTime;
  private long totalTrip;
  private long matchedTripCount = 0;
  private Trip lastTrip;

  public PickupMatching(final double maxWalkingDistanceMiles, final Duration maxWaitTime) {
    this.maxWalkingDistanceMiles = maxWalkingDistanceMiles;
    this.maxWaitTime = maxWaitTime;
    new Thread() {
      @Override
      public void run() {
        while (true) {
          Trip trip;
          try {
            trip = tripBuffer.take();
            innerRun(trip);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }

      private void innerRun(Trip trip) {
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
        trips.add(trip);

        lastTrip = trip;
        totalTrip++;
        if (totalTrip % 100000 == 0) {
          System.out.println(stats());
        }
      }
    }.start();
  }

  @Override
  public void addTrip(Trip trip) {
    while (tripBuffer.size() >= MAX_BUFFER_SIZE) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (trip.isBadData() || trip.startTime() == null) {
      return;
    }
    tripBuffer.offer(trip);
    if (tripBuffer.size() > 1000000) {
      System.out.println("Trip buffer size " + tripBuffer.size());
    }
  }

  @Override
  public String stats() {
    return String.format(
        "%.4f%% of trips can be matched with each other when max wait time "
        + "is %d minutes and max walking distance is %.3f.",
        matchedTripCount * 100.0 / totalTrip, maxWaitTime.toMinutes(), maxWalkingDistanceMiles);
  }

  @Override
  public void waitUntilDone() {
    while (!tripBuffer.isEmpty()) {
      try {
        System.out.println("Sleep 100ms in PickupMatching, left buffer size " + tripBuffer.size());
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}

