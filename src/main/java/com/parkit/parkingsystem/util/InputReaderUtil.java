package com.parkit.parkingsystem.util;

import java.io.IOException;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputReaderUtil {

  private static final Scanner SCAN = new Scanner(System.in, "UTF-8");
  private static final Logger LOGGER = LogManager.getLogger("InputReaderUtil");

  public int readSelection() {
    try {
      return Integer.parseInt(SCAN.nextLine());
    } catch (Exception e) {
      LOGGER.error("Error while reading user input from Shell", e);
      System.out.println("Error reading input. Please enter valid number for proceeding further");
      return -1;
    }
  }

  public String readVehicleRegistrationNumber() {
    String vehicleRegNumber = SCAN.nextLine();
    if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
      throw new IllegalArgumentException("Invalid input provided");
    }
    return vehicleRegNumber;
  }
}
