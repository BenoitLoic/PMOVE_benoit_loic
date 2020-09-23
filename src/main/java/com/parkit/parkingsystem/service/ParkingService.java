package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Contains method to process incoming/exiting vehicle. */
@SuppressFBWarnings("UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD")
public class ParkingService {

  private static final Logger LOGGER = LogManager.getLogger("ParkingService");

  private static final FareCalculatorService fareCalculatorService = new FareCalculatorService();

  private final InputReaderUtil inputReaderUtil;
  private final ParkingSpotDao parkingSpotDao;
  private final TicketDao ticketDao;

  /**
   * Constructor for ParkingService class.
   *
   * @param inputReaderUtil user input
   * @param parkingSpotDao DAO for parking table in db
   * @param ticketDao DAO for ticket table in db
   */
  public ParkingService(
      InputReaderUtil inputReaderUtil, ParkingSpotDao parkingSpotDao, TicketDao ticketDao) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDao = parkingSpotDao;
    this.ticketDao = ticketDao;
  }

  /**
   * Method that add a vehicle to the parking lot .
   *
   * @throws SQLException from dataBaseConfig and DAO
   * @throws ClassNotFoundException from dataBaseConfig
   */
  public void processIncomingVehicle() throws SQLException, ClassNotFoundException {
    ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
    if (parkingSpot.getId() > 0) {
      parkingSpot.setAvailable(false);
      // allot this parking space and mark it's availability as false
      parkingSpotDao.updateParking(parkingSpot);
      String vehicleRegNumber = getVehicleRegNumber();
      Date inTime = new Date();
      Ticket ticket = new Ticket();
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber(vehicleRegNumber);
      ticket.setPrice(0);
      ticket.setInTime(inTime);
      ticket.setOutTime(null);
      ticketDao.saveTicket(ticket);
      if (ticketDao.checkRecurrentUser(ticket)) {
        System.out.println(
            "Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
      }
      System.out.println("Generated Ticket and saved in DB");
      System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
      System.out.println(
          "Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
    }
  }

  /**
   * Method that return the vehicleRegNumber from user.
   *
   * @return String Vehicle Registration Number
   */
  private String getVehicleRegNumber() {
    System.out.println("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  private ParkingSpot getNextParkingNumberIfAvailable()
      throws SQLException, ClassNotFoundException {
    int parkingNumber;
    ParkingSpot parkingSpot;

      ParkingType parkingType = getVehicleType();
      parkingNumber = parkingSpotDao.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new SQLException("Error fetching parking number from DB. Parking slots might be full");
      }


    return parkingSpot;
  }

  /**
   * Method that get the vehicle type from user.
   *
   * @return Vehicle type (CAR/BIKE)
   */
  private ParkingType getVehicleType() throws IllegalArgumentException {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");
    int input = inputReaderUtil.readSelection();
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }
      case 2: {
        return ParkingType.BIKE;
      }
      default: {
        System.out.println("Incorrect input provided");
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }

  /**
   * Method that proceed to remove a vehicle from parking lot. It will add price and out time to
   * ticket and update DB.
   *
   * @throws SQLException from dataBaseConfig and DAO
   * @throws NullPointerException from fareCalculatorService and if ticket is null
   * @throws ClassNotFoundException from dataBaseConfig
   */
  public void processExitingVehicle()
      throws SQLException, NullPointerException, ClassNotFoundException {
    String vehicleRegNumber = getVehicleRegNumber();
    Ticket ticket = ticketDao.getTicket(vehicleRegNumber);
    Date outTime = new Date();
    ticket.setOutTime(outTime);
    fareCalculatorService.calculateFare(ticket);
    if (ticketDao.updateTicket(ticket)) {
      ParkingSpot parkingSpot = ticket.getParkingSpot();
      parkingSpot.setAvailable(true);
      parkingSpotDao.updateParking(parkingSpot);
      boolean recurrentUser = ticketDao.checkRecurrentUser(ticket);
      if (recurrentUser) {
        System.out.println("Please pay the parking fare :" + ticket.getPriceRecurrentUser());
      } else {
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
      }
      System.out.println(
          "Recorded out-time for vehicle number:"
              + ticket.getVehicleRegNumber()
              + " is:"
              + outTime);
    } else {
      System.out.println("Unable to update ticket information. Error occurred");
    }
  }
}
