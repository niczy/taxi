package com.uber.crazytexi.io;

import com.uber.crazytexi.data.Trip;

import java.io.IOException;
import java.util.Optional;

public interface TripLoader {

  public Optional<Trip> getNext() throws IOException;

  public void close();
}

