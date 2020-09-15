package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Contain method to display application menu. */
public class InteractiveShell {

  private static final Logger logger = LogManager.getLogger("InteractiveShell");

  /**
   * Method that loads the menu and use a switch to enter / exit a vehicle or quit the application.
   *
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public static void loadInterface() throws SQLException, ClassNotFoundException {
    logger.info("App initialized!!!");
    System.out.println("Welcome to Parking System!");

    boolean continueApp = true;
    InputReaderUtil inputReaderUtil = new InputReaderUtil();
    ParkingSpotDao parkingSpotDao = new ParkingSpotDao();
    TicketDao ticketDao = new TicketDao();
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDao, ticketDao);

    while (continueApp) {
      loadMenu();
      int option = inputReaderUtil.readSelection();
      switch (option) {
        case 1:
          {
            parkingService.processIncomingVehicle();
            break;
          }
        case 2:
          {
            parkingService.processExitingVehicle();
            break;
          }
        case 3:
          {
            System.out.println("Exiting from the system!");
            continueApp = false;
            break;
          }
        default:
          System.out.println(
              "Unsupported option. Please enter a number corresponding to the provided menu");
      }
    }
  }

  /** Method that contain the menu text. */
  private static void loadMenu() {
    System.out.println("Please select an option. Simply enter the number to choose an action");
    System.out.println("1 New Vehicle Entering - Allocate Parking Space");
    System.out.println("2 Vehicle Exiting - Generate Ticket Price");
    System.out.println("3 Shutdown System");
  }
}
