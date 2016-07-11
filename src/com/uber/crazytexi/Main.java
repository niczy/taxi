package com.uber.crazytexi;

import com.uber.crazytexi.algorithm.Chaining;
import com.uber.crazytexi.algorithm.DriverStats;
import com.uber.crazytexi.algorithm.PickupMatching;
import com.uber.crazytexi.algorithm.TripAnalyzer;
import com.uber.crazytexi.algorithm.TripLength;
import com.uber.crazytexi.data.Trip;
import com.uber.crazytexi.io.AllTripLoader;
import com.uber.crazytexi.io.Consts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Main {

  private static final String REPORT = "report.txt";
  private static long totalRecords = 0;

  public static void main(String[] args) throws IOException {
    File report = new File(REPORT);
    if (report.exists()) {
      report.delete();
    }
    report.createNewFile();

    Writer writer = new FileWriter(report);

    List<TripAnalyzer> tripAnalyzers = new ArrayList<TripAnalyzer>(); 
    tripAnalyzers.add(new TripLength());
    tripAnalyzers.add(new DriverStats());
    tripAnalyzers.add(new PickupMatching(0.2 /* maxWalkingMiles */, Duration.ofMinutes(5)));
    tripAnalyzers.add(new PickupMatching(0.2 /* maxWalkingMiles */, Duration.ofMinutes(10)));
    tripAnalyzers.add(new PickupMatching(0.1 /* maxWalkingMiles */, Duration.ofMinutes(5)));
    tripAnalyzers.add(new PickupMatching(0.05 /* maxWalkingMiles */, Duration.ofMinutes(5)));
    tripAnalyzers.add(new Chaining());

    File shartedFileDir = new File(Consts.SHARTED_DATA_PATH);
    List<File> files = Arrays.asList(shartedFileDir.listFiles());
    Collections.sort(files, new Comparator<File>(){

      @Override
      public int compare(File o1, File o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    AllTripLoader allTripLoader =
        new AllTripLoader(files, false /* skipFirstRow */, true /* sorted */);
    Optional<Trip> trip = null;
    while ((trip = allTripLoader.getNext()).isPresent()) {
      //System.out.print("new trip " + trip.get().startTime());
      for (TripAnalyzer analyzer : tripAnalyzers) {
        //System.out.println(analyzer.getClass().getName());
        analyzer.addTrip(trip.get());
        //System.out.println("done " + analyzer.getClass().getName());
      }
      totalRecords++;
      if (totalRecords % 10000 == 0) {
        System.out.println(totalRecords + " processed");
      }
    }
    for (TripAnalyzer analyzer : tripAnalyzers) {
      analyzer.waitUntilDone();
      writer.append(analyzer.stats());
    }
    writer.flush();
    writer.close();
  }
}
