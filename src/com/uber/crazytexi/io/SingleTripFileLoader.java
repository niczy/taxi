package com.uber.crazytexi.io;

import com.uber.crazytexi.data.Trip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public final class SingleTripFileLoader implements TripLoader {

  private final BufferedReader reader;

  public SingleTripFileLoader(String filePath, boolean skipFirstRow) throws IOException {
    File file = new File(filePath);
    this.reader = new BufferedReader(new FileReader(file));
    if (skipFirstRow) {
      reader.readLine();
    }
  }

  /**
   * Get next trip, {@code null} if not more trip.
   * @throws IOException 
   */
  @Override
  public Optional<Trip> getNext() throws IOException {
    String record = reader.readLine();
    if (record != null) {
      return Optional.of(new Trip(record));
    } else {
      return Optional.<Trip>empty();
    }
  }

  @Override
  public void close() {
    try {
      this.reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

