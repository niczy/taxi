package com.uber.crazytexi;

import com.uber.crazytexi.algorithm.DriverStats;
import com.uber.crazytexi.algorithm.PickupMatching;
import com.uber.crazytexi.algorithm.TripLength;
import com.uber.crazytexi.data.Trip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.util.Scanner;

public class Main {

  private static final String DATA_PATH = "data";
  private static final String REPORT = "report.txt";
  private static final int RECORD_COUNT = -1;

  public static void main(String[] args) throws IOException {
    File file = new File(DATA_PATH);
    File report = new File(REPORT);
    if (report.exists()) {
      report.delete();
    }
    report.createNewFile();

    Writer writer = new FileWriter(report);

    TripLength tripLength = new TripLength();
    DriverStats driverStats = new DriverStats();
    PickupMatching pickupMatching = new PickupMatching(Duration.ofMinutes(5));

    int recordCount = 0;
    for (File dataFile : file.listFiles()) {
      if (dataFile.getName().contains("trip_data_") && !dataFile.getName().contains(".zip")) {
        recordCount = parseFile(tripLength, driverStats, pickupMatching, recordCount, dataFile);
        pickupMatching.endMonth();
      }
    }
    writer.write(tripLength.stats());
    writer.write(driverStats.stats());
    writer.write(pickupMatching.stats());
    writer.flush();
    writer.close();
  }

  private static int parseFile(
      TripLength tripLength,
      DriverStats driverStats,
      PickupMatching pickupMatching,
      int recordCount,
      File dataFile) {
    System.out.println("Parsing file: " + dataFile.getName());
    Scanner scanner = null;
    try {
      scanner = new Scanner(dataFile);
      String line = scanner.nextLine();
      //System.out.println(line);
      while (scanner.hasNext()) {
        line = scanner.nextLine();
        //System.out.println(line);
        Trip trip = new Trip(line);
        tripLength.addTrip(trip);
        driverStats.addTrip(trip);
        pickupMatching.addTrip(trip);
        recordCount++;
        if (RECORD_COUNT > 0 && recordCount > RECORD_COUNT) {
          break;
        }
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      scanner.close();
    }
    return recordCount;
  }
}
