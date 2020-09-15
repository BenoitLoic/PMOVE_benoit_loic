package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/** Contain a method to calculate the price of parking. */
public class FareCalculatorService {

  /**
   * Method to calculate the price of parking.
   *
   * @param ticket
   * @throws NullPointerException if out time is null
   * @throws IllegalArgumentException if out time is before in time
   */
  public void calculateFare(Ticket ticket) throws IllegalArgumentException, NullPointerException {
    final float millisecondsToHoursConversion = 1 / 3600000.f;
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      if (ticket.getOutTime() != null) {
        throw new IllegalArgumentException(
            "Out time provided is incorrect:" + ticket.getOutTime().toString());
      } else {
        throw new NullPointerException("Out time provided is null");
      }
    }

    long inHour = ticket.getInTime().getTime();
    long outHour = ticket.getOutTime().getTime();

    float duration = (outHour - inHour) * millisecondsToHoursConversion;

    if (duration <= Fare.FREE_TIME_IN_HOUR) {
      duration = 0;
    }

    switch (ticket.getParkingSpot().getParkingType()) {
      case CAR:
        ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
        break;
      case BIKE:
        ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
        break;
      default:
        throw new IllegalArgumentException("Unknown Parking Type");
    }
  }
}
