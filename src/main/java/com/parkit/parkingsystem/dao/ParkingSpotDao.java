package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DbConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Contain DAO method for Parking Table. */
public class ParkingSpotDao {
  private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

  public DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * Method to Get the next available parking slot in DB for this parkingType.
   *
   * @param parkingType the parking type checked for availability
   * @return integer result (number of the 1st available slot in DB, -1 == error)
   * @throws SQLException from dataBaseConfig
   */
  public int getNextAvailableSlot(ParkingType parkingType)
      throws SQLException, ClassNotFoundException {

    int result = -1;
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DbConstants.GET_NEXT_PARKING_SPOT)) {
      ps.setString(1, parkingType.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          result = rs.getInt(1);
        }
      }
    }
    return result;
  }

  /**
   * Method to update the availability for that parking slot.
   *
   * @param parkingSpot the parking spot to update availability
   * @return true if 1 parking spot have been updated
   * @throws SQLException from dataBaseConfig
   * @throws ClassNotFoundException from dataBaseConfig
   */
  public boolean updateParking(ParkingSpot parkingSpot)
      throws SQLException, ClassNotFoundException {

    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DbConstants.UPDATE_PARKING_SPOT)) {
      ps.setBoolean(1, parkingSpot.isAvailable());
      ps.setInt(2, parkingSpot.getId());
      int updateRowCount = ps.executeUpdate();
      return (updateRowCount == 1);

    }
  }
}
