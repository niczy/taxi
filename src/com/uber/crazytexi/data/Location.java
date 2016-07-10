package com.uber.crazytexi.data;

public class Location {
  
  private final double lat;
  private final double lon;

  public Location(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public double distanceMiles(Location other) {
    return distance(this.lat, this.lon, other.lat, other.lon);
  }

  private double distance(double lat1, double lon1, double lat2, double lon2) {
    double theta = lon1 - lon2;
    double dist =
        Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
            + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    dist = dist * 0.8684;
    return (dist);
  }

  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }
}

