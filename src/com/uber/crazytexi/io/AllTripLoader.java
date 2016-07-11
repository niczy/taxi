package com.uber.crazytexi.io;

import com.uber.crazytexi.data.Trip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AllTripLoader implements TripLoader {

  private final List<File> files = new ArrayList<File>();
  private int idx = 0;
  private final boolean  skipFirstRow;
  private final boolean sorted;
  private TripLoader currentTripLoader;

  public AllTripLoader(List<File> files, boolean skipFirstRow) {
    this(files, skipFirstRow, false);
  }

  public AllTripLoader(List<File> filesToLoad, boolean skipFirstRow, boolean sorted) {
    files.addAll(filesToLoad);
    this.skipFirstRow = skipFirstRow;
    this.sorted = sorted;
  }

  @Override
  public Optional<Trip> getNext() throws IOException {
    if (currentTripLoader == null) {
      newTripLoader();
    }
    Optional<Trip> trip = currentTripLoader.getNext();
    if (trip.isPresent()) {
      return trip;
    } else {
      currentTripLoader.close();
      idx++;
      if (idx >= files.size()) {
        return Optional.empty();
      }
      newTripLoader();
      return currentTripLoader.getNext();
    }
  }

  private void newTripLoader() throws IOException {
    if (sorted) {
      currentTripLoader = new SingleFileSortedTripLoader(files.get(idx), skipFirstRow);
    } else {
      currentTripLoader = new SingleFileTripLoader(files.get(idx), skipFirstRow);
    }
  }

  @Override
  public void close() {
    currentTripLoader.close();
  }
}

