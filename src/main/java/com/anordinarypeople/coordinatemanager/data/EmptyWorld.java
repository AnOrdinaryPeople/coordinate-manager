package com.anordinarypeople.coordinatemanager.data;

public class EmptyWorld {
  public int index;
  public String name = null;
  public ListSelectableCoor data;

  public EmptyWorld(int index, ListSelectableCoor data) {
    this.index = index;
    this.data = data;
  }
}
