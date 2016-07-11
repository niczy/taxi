package com.uber.crazytexi.algorithm;

import com.uber.crazytexi.data.Trip;
import com.uber.crazytexi.io.Consts;
import com.uber.crazytexi.io.SingleFileSortedTripLoader;
import com.uber.crazytexi.io.TripLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Analyze how many trips can be chained.
 * The idea is to maintain a list of {@code ChainedTrip} sorted by it's earliest ended trip.
 * For every new trip, go through all previous ChainedTips to create a new ChainedTrip.
 */
public final class Chaining implements TripAnalyzer {

  private static final int MAX_BUFFER_SIZE = 100000;
  private long totalTrips = 0;
  // trips is a trip list sorted by endtime.
  private final BlockingQueue<Trip> tripBuffer = new ArrayBlockingQueue<>(MAX_BUFFER_SIZE);
  private List<ChainedTrip> trips = new LinkedList<ChainedTrip>();
  private int maxChainLength = 0;
  private long chainedTripCount = 0;

  public Chaining() {
    new Thread() {
      @Override
      public void run() {
        System.out.println("Starting Chaining thread.");
        while (true) {
          Trip trip;
          try {
            trip = tripBuffer.take();
            //System.out.println("Took one trip");
            innerRun(trip);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }

      private void innerRun(Trip trip) { //System.out.println(trips.size());
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
        // Check if the new ChainedTip should be kept.
        // For ChainedTips having same drop off time, only remember the one with longer chainLength.
        for (ChainedTrip tripToAdd : tripsToAdd) {
          boolean shouldIgnore = false;
          for (ChainedTrip previousTrip : trips) {
            if (previousTrip.trip1.endTime().getTime() > tripToAdd.trip1.endTime().getTime()) {
              break;
            }
            if (previousTrip.trip1.endTime().getTime() == tripToAdd.trip1.endTime().getTime()) {
              if (previousTrip.trip2 == null && tripToAdd.trip2 == null
                  || previousTrip.trip2 != null
                      && tripToAdd.trip2 != null
                      && previousTrip.trip2.endTime().getTime()
                          == tripToAdd.trip2.endTime().getTime()) {
                shouldIgnore = true;
                if (previousTrip.chainLength >= tripToAdd.chainLength) {
                  // do nothing
                } else {
                  previousTrip.trip1 = tripToAdd.trip1;
                  previousTrip.trip2 = tripToAdd.trip2;
                  previousTrip.chainLength = tripToAdd.chainLength;
                }
              }
            }
          }
          if (shouldIgnore) {
            continue;
          }
          trips.add(tripToAdd);
          int i = trips.size() - 1;
          while (i > 0
              && trips.get(i).trip1.endTime().getTime()
                  < trips.get(i - 1).trip1.endTime().getTime()) {
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
        if (totalTrips % 100000 == 0) {
          System.out.println(totalTrips + " processed by Chaining!");
          System.out.println("Buffer size is " + tripBuffer.size());
          System.out.println("Chained trip size " + trips.size());
          System.out.println(stats());
        }
      }
    }.start();
  }
  

  @Override
  public void addTrip(Trip trip) {
    while (tripBuffer.size() >= MAX_BUFFER_SIZE) {
      try {
        //System.out.print("sleeip waiting for trip buffer to be consumed.\n");
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    tripBuffer.offer(trip);
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
    Trip trip1;
    Trip trip2;
    int chainLength;
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

  public static void main(String[] args) throws IOException {
    List<Chaining> chanings = new ArrayList<Chaining>();
    File shartedFileDir = new File(Consts.SHARTED_DATA_PATH);
    List<File> files = Arrays.asList(shartedFileDir.listFiles());
    Collections.sort(files, new Comparator<File>(){
      @Override
      public int compare(File o1, File o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    for (File file : files) {
      TripLoader tripLoader = new SingleFileSortedTripLoader(file, false);
      Chaining chaining = new Chaining();
      Optional<Trip> trip = null;
      while ((trip = tripLoader.getNext()).isPresent()) {
        chaining.addTrip(trip.get());
      }
      System.out.println("chained trip length is " + chaining.trips.size());
      chanings.add(chaining);
    }
  }

  @Override
  public void waitUntilDone() {
    while (!tripBuffer.isEmpty()) {
      try {
        System.out.println("Sleep 100ms in Chaining, left buffer size " + tripBuffer.size());
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}

