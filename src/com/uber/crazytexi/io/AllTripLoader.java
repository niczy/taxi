package com.uber.crazytexi.io;

import com.uber.crazytexi.data.Trip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AllTripLoader implements TripLoader {

  private final List<TripLoader> tripLoaders = new ArrayList<TripLoader>();
  private int idx = 0;

  public AllTripLoader() throws IOException {
    for (int i = 1; i <= Consts.DATA_COUNT; i++) {
      tripLoaders.add(
          new SingleTripFileLoader(
              Consts.DATA_PATH + "trip_data_" + i + ".csv", true /* skipFirstRow */));
    }
  }

  @Override
  public Optional<Trip> getNext() throws IOException {
    Optional<Trip> trip = tripLoaders.get(idx).getNext();
    if (trip.isPresent()) {
      return trip;
    } else {
      idx++;
      if (idx >= tripLoaders.size()) {
        return Optional.empty();
      }
      return tripLoaders.get(idx).getNext();
    }
  }

  @Override
  public void close() {
    for (TripLoader tripLoader : tripLoaders) {
      tripLoader.close();
    }
  }
}

