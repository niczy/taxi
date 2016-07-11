package com.uber.crazytexi.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class Trip {

  private static final String HACK_LICENSE = "hack_license";
  private static final String PICK_TIME = "pickup_datetime";
  private static final String TRIP_TIME_SEC = "trip_time_in_secs";
  private static final String PICK_UP_LAT = "pickup_latitude";
  private static final String PICK_UP_LON = "pickup_longitude";
  private static final String DROP_OFF_LAT = "dropoff_latitude";
  private static final String DROP_OFF_LON = "dropoff_longitude";
  private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private static final String[] PROPS = new String[]{
      "medallion",
      HACK_LICENSE,
      "vendor_id",
      "rate_code",
      "store_and_fwd_flag",
      PICK_TIME,
      "dropoff_datetime",
      "passenger_count",
      TRIP_TIME_SEC,
      "trip_distance",
      PICK_UP_LON,
      PICK_UP_LAT,
      DROP_OFF_LON,
      DROP_OFF_LAT
  };

  private Date startTime;
  private Location pickUpLocation;
  private Location dropOffLocation;
  private final String rawRecod;

  private final Map<String, String> props = new HashMap<>();
  private boolean badData = false;

  public Trip(String record) {
    this.rawRecod = record;
    String[] segs = record.split(",");
    assert segs.length == PROPS.length;
    if (segs.length != PROPS.length) {
      //System.out.println("Bad data? " + Arrays.toString(segs));
      this.badData = true;
    }
    for (int i = 0; i < PROPS.length && i < segs.length; i++) {
      props.put(PROPS[i], segs[i]);
    }
    try {
      this.startTime = DATA_FORMAT.parse(props.get(PICK_TIME));
      this.pickUpLocation =
          new Location(
              Double.parseDouble(props.get(PICK_UP_LAT)),
              Double.parseDouble(props.get(PICK_UP_LON)));
      this.dropOffLocation =
          new Location(
              Double.parseDouble(props.get(DROP_OFF_LAT)),
              Double.parseDouble(props.get(DROP_OFF_LON)));
    } catch (Exception e) {
      this.badData = true;
      this.startTime = null;
      //e.printStackTrace();
    }
  }

  public Date startTime() {
    return startTime;
  }

  public Location getPickUpLocation() {
    return this.pickUpLocation;
  }

  public Location getDropOffLocation() {
    return this.dropOffLocation;
  }

  public long getTripTimeSec() {
    return Long.parseLong(props.get(TRIP_TIME_SEC));
  }

  public String getHackLicense() {
    return props.get(HACK_LICENSE);
  }

  public boolean isBadData() {
    return this.badData;
  }

  @Override
  public String toString() {
    return this.rawRecod;
  }
}

