package com.uber.crazytexi.io;

import com.uber.crazytexi.data.Trip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Tool to shard the trip records by hour based on pickup time.
 */
public final class ShardRecordTool {
  private static final long DIV = 60 * 60 * 1000;
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
      List<File> files = new ArrayList<File>();
      for (String fileName : Consts.RAW_TRIP_DATA) {
        files.add(new File(fileName));
      }
      TripLoader tripLoader = new AllTripLoader(files, true /* skipFirstRow */);
      Optional<Trip> trip = null;
      BufferedWriter writer = null;
      while ((trip = tripLoader.getNext()).isPresent()) {
        if (trip.get().startTime() == null) {
          badRecords++;
          continue;
        }
        String fileName = TRIP_PREFIX + trip.get().startTime().getTime() / DIV;
        if (writters.containsKey(fileName)) {
          writer = writters.get(fileName);
        } else {
          File file = new File(Consts.SHARTED_DATA_PATH, fileName);
          file.createNewFile();
          writer = new BufferedWriter(new FileWriter(file));
          writters.put(fileName, writer);
        }
        writer.append(trip.get().toString() + "\n");
        totalRecords += 1;
        if (totalRecords % 100000 == 0) {
          System.out.println(totalRecords + " processed.");
        }
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
