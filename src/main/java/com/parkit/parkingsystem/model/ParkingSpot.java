package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

public class ParkingSpot {
  private final int number;
  private final ParkingType parkingType;
  private boolean isAvailable;

  /**
   * Constructor for ParkingSpot class.
   *
   * @param number the number of the parking spot.
   * @param parkingType the type of the parking, enum in ParkingType constants
   * @param isAvailable availability of the parking spot
   */
  public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
    this.number = number;
    this.parkingType = parkingType;
    this.isAvailable = isAvailable;
  }

  public int getId() {
    return number;
  }

  public ParkingType getParkingType() {
    return parkingType;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParkingSpot that = (ParkingSpot) o;
    return number == that.number;
  }

  @Override
  public int hashCode() {
    return number;
  }
}
