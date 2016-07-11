package com.uber.crazytexi.io;

import java.util.ArrayList;
import java.util.List;

public final class Consts {
  static final int DATA_COUNT = 12;
  static final String DATA_PATH = "/usr/local/google/home/nicholaszhao/workspace/tmp/taxi/";
  public static final String SHARTED_DATA_PATH = "/usr/local/google/home/nicholaszhao/tmp/taxi/sharted";
  static final List<String> RAW_TRIP_DATA = new ArrayList<String>();
  static {
    for (int i = 1; i <= Consts.DATA_COUNT; i++) {
      RAW_TRIP_DATA.add(Consts.DATA_PATH + "trip_data_" + i + ".csv");
    }
  }
}

