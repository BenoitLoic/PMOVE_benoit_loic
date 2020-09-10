package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  public void calculateFare(Ticket ticket) {
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

    float duration = (outHour - inHour) / 3600000.f;

    if (duration <= 0.5) {
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
