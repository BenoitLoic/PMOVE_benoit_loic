package com.parkit.parkingsystem.util;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Contain method to read user input. */
public class InputReaderUtil {

  private static final Scanner SCAN = new Scanner(System.in, "UTF-8");
  private static final Logger LOGGER = LogManager.getLogger("InputReaderUtil");

  /**
   * Method that read user input and parse it to integer, print an error if input is not valid.
   *
   * @return integer parsed from user input
   */
  public int readSelection() {
    try {
      return Integer.parseInt(SCAN.nextLine());
    } catch (Exception e) {
      LOGGER.error("Error while reading user input from Shell");
      System.out.println("Error reading input. Please enter valid number for proceeding further");
      return -1;
    }
  }

  /**
   * Method that read the vehicle registration number from user.
   *
   * @return String vehicleRegNumber
   * @throws IllegalArgumentException if vehicleRegNumber == null | vehicleRegNumber.trim.length ==0
   */
  public String readVehicleRegistrationNumber() throws IllegalArgumentException {
    String vehicleRegNumber = SCAN.nextLine();
    if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
      throw new IllegalArgumentException("Invalid input provided");
    }
    return vehicleRegNumber;
  }
}
