package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBasePrepareService {

  DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

  public void clearDataBaseEntries() {

    try (Connection con = dataBaseTestConfig.getConnection()) {
      String updateParkingSpot = "UPDATE parking SET available = true;";
      try (PreparedStatement ps = con.prepareStatement(updateParkingSpot)) {
        ps.execute();
      }
      String clearTicketEntries = "TRUNCATE table ticket;";
      try (PreparedStatement ps = con.prepareStatement(clearTicketEntries)) {
        ps.execute();
      }
    } catch (SQLException | ClassNotFoundException ex) {
      System.out.println("error while clearing data base for IT");
    }
  }
}
