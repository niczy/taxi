package com.uber.crazytexi.io;

import com.uber.crazytexi.data.Trip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SingleFileSortedTripLoader implements TripLoader {

  private final List<Trip> list = new ArrayList<Trip>();
  private int idx = 0;
  public SingleFileSortedTripLoader(String filePath, boolean skipFirstRow) throws IOException {
    this(new File(filePath), skipFirstRow);
  }

  public SingleFileSortedTripLoader(File file, boolean skipFirstRow) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    try {
    if (skipFirstRow) {
      reader.readLine();
    }
    String line = null;
    while ((line = reader.readLine()) != null) {
      Trip trip = new Trip(line);
      if (trip.startTime() == null) {
        continue;
      }
      list.add(trip);
    }
    } finally {
      reader.close();
    }
    Collections.sort(list, new Comparator<Trip>(){

      @Override
      public int compare(Trip o1, Trip o2) {
        return o1.startTime().compareTo(o2.startTime());
      }
    });
  }

  @Override
  public Optional<Trip> getNext() throws IOException {
    if (idx >= list.size()) {
      return Optional.empty();
    } else {
      return Optional.of(list.get(idx++));
    }
  }

  @Override
  public void close() {
    // do nothing.
  }
}

