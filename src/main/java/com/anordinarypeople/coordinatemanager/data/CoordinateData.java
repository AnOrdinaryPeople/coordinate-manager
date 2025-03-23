package com.anordinarypeople.coordinatemanager.data;

import java.util.UUID;

public class CoordinateData {
  public String uuid;
  public String name;
  public String dimension;
  public String imagePath;
  public double x;
  public double y;
  public double z;
  public boolean isPinned = false;

  public CoordinateData(String name, String dimension, double x, double y, double z) {
    this.uuid = UUID.randomUUID().toString();
    this.name = name;
    this.dimension = dimension;
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
