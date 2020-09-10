package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DataBasePrepareService {

  DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

  public void clearDataBaseEntries() {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseTestConfig.getConnection();

      // set parking entries to available
      String updateParkingSpot = "UPDATE parking SET available = true;";
      ps = con.prepareStatement(updateParkingSpot);
      ps.execute();

      // clear ticket entries;
      String clearTicketEntries = "TRUNCATE table ticket;";
      ps = con.prepareStatement(clearTicketEntries);
      ps.execute();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closePreparedStatement(ps);
      dataBaseTestConfig.closeConnection(con);
    }
  }
}
