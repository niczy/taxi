package com.uber.crazytexi.io;

import com.uber.crazytexi.data.Trip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Tool to shard the trip records based on time.
 */
public final class ShardRecordTool {
  private static final long MOD = 24 * 60 * 60 * 1000;
  private static final String TRIP_PREFIX = "trip_data.";

  private static long totalRecords = 0;
  private static long badRecords = 0;
  private static Map<String, BufferedWriter> writters;

  public static void main(String[] args) throws IOException {
    long startTime = System.currentTimeMillis();
    try {
      File shartedDataDir = new File(Consts.SHARTED_DATA_PATH);
      if (shartedDataDir.exists()) {
        shartedDataDir.delete();
      }
      shartedDataDir.mkdirs();

      writters = new HashMap<String, BufferedWriter>();
      TripLoader tripLoader = new AllTripLoader();
      Optional<Trip> trip = null;
      BufferedWriter writer = null;
      while ((trip = tripLoader.getNext()).isPresent()) {
        if (trip.get().startTime() == null) {
          badRecords++;
          continue;
        }
        String fileName = TRIP_PREFIX + trip.get().startTime().getTime() % MOD;
        if (writters.containsKey(fileName)) {
          writer = writters.get(fileName);
        } else {
          File file = new File(Consts.SHARTED_DATA_PATH, fileName);
          file.createNewFile();
          writer = new BufferedWriter(new FileWriter(file));
        }
        writer.append(trip.get().toString() + "\n");
        totalRecords += 1;
      }
      for (Entry<String, BufferedWriter> entry : writters.entrySet()) {
        entry.getValue().flush();
        entry.getValue().close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    long totalTime = System.currentTimeMillis() - startTime;
    System.out.println("Total records " + totalRecords);
    System.out.println("Total bad records " + badRecords);
    System.out.println("Took " + totalTime / 1000.0 / 60 + " minutes");
  }
}
