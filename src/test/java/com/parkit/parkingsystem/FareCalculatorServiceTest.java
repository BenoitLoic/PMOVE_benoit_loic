package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FareCalculatorServiceTest {

  private static FareCalculatorService fareCalculatorService;
  private Ticket ticket;
  private Date inTime;
  private Date outTime;

  @BeforeAll
  private static void setUp() {
    fareCalculatorService = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    ticket = new Ticket();
    inTime = new Date();
    outTime = new Date();
  }

  @Test
  public void calculateFareCar() {

    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice(), 0.01);
  }

  @Test
  public void calculateFareBike() {

    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice(), 0.01);
  }

  @Test
  public void calculateFareUnknownType() {

    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

    ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  public void calculateFareBikeWithFutureInTime() {

    inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  public void calculateFareBikeWithLessThanOneHourParkingTime() {

    inTime.setTime(
        System.currentTimeMillis()
            - (45 * 60 * 1000)); // 45 minutes parking time should give 3/4th parking fare

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice(), 0.001);
  }

  @Test
  public void calculateFareCarWithLessThanOneHourParkingTime() {

    inTime.setTime(
        System.currentTimeMillis()
            - (45 * 60 * 1000)); // 45 minutes parking time should give 3/4th parking fare

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice(), 0.0001);
  }

  @Test
  public void calculateFareCarWithMoreThanADayParkingTime() {

    inTime.setTime(
        System.currentTimeMillis()
            - (24 * 60 * 60
                * 1000)); // 24 hours parking time should give 24 * parking fare per hour

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  public void calculateFareAnyTypeWithLessThanThirtyMinutesParkingTime() {

    inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  public void calculateFareForRecurrentUserWithFivePercentReduction() {

    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(
        (Fare.CAR_RATE_PER_HOUR - Fare.CAR_RATE_PER_HOUR * 5 / 100),
        ticket.getPriceRecurrentUser(),
        0.0001);
  }
}
